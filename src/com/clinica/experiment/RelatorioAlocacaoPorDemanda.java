package com.clinica.experiment;

import com.clinica.model.ClinicaModel;
import com.clinica.model.Config;
import com.clinica.util.ReportManager;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class RelatorioAlocacaoPorDemanda {
    public static void main(String[] args) throws Exception {
        // Cenários de demanda (média entre chegadas)
        double[] mediasChegada = {20.0, 15.0, 10.0, 7.5}; // calmo, normal, pico, extremo
        String[] niveis = {"Calmo", "Normal", "Pico", "Extremo"};

        int replicacoes = 10; // Use 10 para ser mais rápido, aumente se quiser mais precisão

        StringBuilder sb = new StringBuilder();
        sb.append("NivelDemanda\tMediaChegada\tPacHora\tConsultMin\tFilaMax\n");

        for (int idx = 0; idx < mediasChegada.length; idx++) {
            double mediaChegada = mediasChegada[idx];
            int pacHora = (int) (60.0 / mediaChegada);

            // Testa com 1, 2, 3, 4, 5, 6 consultórios até achar o mínimo que mantém fila ≤ 5
            int consultMin = -1;
            for (int nConsult = 1; nConsult <= 8; nConsult++) {
                double filaMaxMedia = 0;

                for (int r = 1; r <= replicacoes; r++) {
                    Config.N_CONSULTORIOS = nConsult;
                    Config.MEDIA_ENTRE_CHEGADAS = mediaChegada;
                    Config.DURACAO_SIMULACAO = 600.0;
                    Config.USAR_PRIORIZACAO = false;
                    Config.USAR_FILA_UNICA = false;
                    Config.TEMPO_ATEND_NORMAL_MEDIA = 20.0;
                    Config.TEMPO_ATEND_URGENTE_MEDIA = 10.0;

                    String nomeExp = niveis[idx] + "_N" + nConsult + "_R" + r;
                    ReportManager.setupReportDirectory(nomeExp);

                    ClinicaModel model = new ClinicaModel(null, "ClinicaVidaSaudavel", false, false);
                    Experiment exp = new Experiment(nomeExp);
                    model.connectToExperiment(exp);
                    exp.stop(new TimeInstant(Config.DURACAO_SIMULACAO, TimeUnit.MINUTES));
                    exp.start();
                    exp.report();
                    exp.finish();

                    // Parse fila máxima do HTML
                    String htmlPath = nomeExp + "_report.html";
                    double filaMax = parseQueueFromHtml(htmlPath, "Fila Consultorio 1", 5);
                    filaMaxMedia += filaMax;
                }
                filaMaxMedia /= replicacoes;

                if (filaMaxMedia <= 5.0) {
                    consultMin = nConsult;
                    sb.append(String.format("%s\t%.1f\t%d\t%d\t%.1f\n",
                            niveis[idx], mediaChegada, pacHora, consultMin, filaMaxMedia));
                    break;
                }
            }
            if (consultMin == -1) {
                sb.append(String.format("%s\t%.1f\t%d\t>8\t>5\n", niveis[idx], mediaChegada, pacHora));
            }
        }
        Files.write(Paths.get("tabela_alocacao_demanda.tsv"), sb.toString().getBytes());
        System.out.println("Tabela salva em: tabela_alocacao_demanda.tsv");
    }

    private static double parseQueueFromHtml(String filePath, String queueTitle, int col) throws Exception {
        boolean tabelaQueue = false;
        for (String line : Files.readAllLines(Paths.get(filePath))) {
            if (line.contains("Queues")) tabelaQueue = true;
            if (tabelaQueue && line.contains(queueTitle)) {
                String[] parts = line.split("</td><td>");
                return Double.parseDouble(parts[col].replaceAll("[^\\d.,-]", "").replace(",", "."));
            }
        }
        return 0;
    }
}
