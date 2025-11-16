package com.clinica.experiment;

import com.clinica.model.ClinicaModel;
import com.clinica.model.Config;
import com.clinica.util.ReportManager;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Relatorio {

    public static void main(String[] args) throws Exception {
        // Parâmetros padrão replicações
        int replicacoes = 30;

        // Cenários a testar: nome, consultórios, priorização, fila única, tempo serviço normal, tempo serviço urgente
        Object[][] cenarios = {
                // nome,        consult, priorizacao, filaUnica, tempoNormal, tempoUrgente
                {"CenarioBase",    4,    false,    false,      20.0,       10.0},
                {"TresConsult",    3,    false,    false,      20.0,       10.0},
                {"DoisConsult",    2,    false,    false,      20.0,       10.0},
                {"Prioridade",     4,    true,     false,      20.0,       10.0},
                {"FilaUnica",      4,    false,    true,       20.0,       10.0},
                {"NovoSistema",    4,    false,    false,      15.0,       10.0},
        };

        // Para salvar os resumos:
        StringBuilder tabelaResumo = new StringBuilder();
        tabelaResumo.append("Cenario\tConsultorios\tPriorizacao\tFilaUnica\tTempoNormal\tTempoUrgente\tReplicas\tTempoEsperaMedia\tTempoEsperaStd\tFilaMax\tPatientsAtendidos\n");

        for (Object[] cenario : cenarios) {
            double mediaEspera = 0, stdEspera = 0, maxFila = 0, pacientesAtendidos = 0;
            for (int r = 1; r <= replicacoes; r++) {
                Config.N_CONSULTORIOS = (int) cenario[1];
                Config.USAR_PRIORIZACAO = (boolean) cenario[2];
                Config.USAR_FILA_UNICA = (boolean) cenario[3];
                Config.TEMPO_ATEND_NORMAL_MEDIA = (double) cenario[4];
                Config.TEMPO_ATEND_URGENTE_MEDIA = (double) cenario[5];
                Config.DURACAO_SIMULACAO = 600.0; // 10 horas
                Config.NOME_EXPERIMENTO = cenario[0] + "_Run" + r;

                ReportManager.setupReportDirectory(Config.NOME_EXPERIMENTO);

                ClinicaModel model = new ClinicaModel(null, "ClinicaVidaSaudavel", false, false);
                Experiment exp = new Experiment(Config.NOME_EXPERIMENTO);
                model.connectToExperiment(exp);

                exp.stop(new TimeInstant(Config.DURACAO_SIMULACAO, TimeUnit.MINUTES));
                exp.start();
                exp.report();
                exp.finish();

                // Analise relatório gerado automático (parse simples do HTML)
                String path = Config.NOME_EXPERIMENTO + "_report.html";
                double tempoEsperaObs = parseTallyFromHtml(path, "Tempo Espera - Geral", 3); // média
                double tempoEsperaStd = parseTallyFromHtml(path, "Tempo Espera - Geral", 4); // std
                int pacientes = (int) parseTallyFromHtml(path, "Tempo Espera - Geral", 2); // obs
                int filaMax = (int) parseQueueFromHtml(path, "Fila Consultorio 1", 5);    // Qmax

                mediaEspera += tempoEsperaObs;
                stdEspera += tempoEsperaStd;
                maxFila += filaMax;
                pacientesAtendidos += pacientes;
            }
            // Calcula médias das replicações
            mediaEspera /= replicacoes;
            stdEspera /= replicacoes;
            maxFila /= replicacoes;
            pacientesAtendidos /= replicacoes;

            tabelaResumo.append(String.format("%s\t%d\t%s\t%s\t%.1f\t%.1f\t%d\t%.2f\t%.2f\t%.1f\t%.1f\n",
                    cenario[0],
                    cenario[1],
                    cenario[2],
                    cenario[3],
                    cenario[4],
                    cenario[5],
                    replicacoes,
                    mediaEspera,
                    stdEspera,
                    maxFila,
                    pacientesAtendidos
            ));
        }
        // Salva tabela para depois copiar/colar no Word
        Files.write(Paths.get("tabela_cenarios.tsv"), tabelaResumo.toString().getBytes());
        System.out.println("Tabela salva em tabela_cenarios.tsv");
    }

    // Lê a linha da tabela do HTML a partir do título do tally
    private static double parseTallyFromHtml(String filePath, String tallyTitle, int col) throws Exception {
        for (String line : Files.readAllLines(Paths.get(filePath))) {
            if (line.contains(tallyTitle)) {
                String[] parts = line.split("</td><td>");
                return Double.parseDouble(parts[col].replaceAll("[^\\d.]", ""));
            }
        }
        return 0;
    }
    // Mesma lógica para fila (Queue)
    private static double parseQueueFromHtml(String filePath, String queueTitle, int col) throws Exception {
        boolean tabelaQueue = false;
        for (String line : Files.readAllLines(Paths.get(filePath))) {
            if (line.contains("Queues")) tabelaQueue = true;
            if (tabelaQueue && line.contains(queueTitle)) {
                String[] parts = line.split("</td><td>");
                return Double.parseDouble(parts[col].replaceAll("[^\\d.]", ""));
            }
        }
        return 0;
    }
}
