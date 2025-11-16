package com.clinica.experiment;

import java.nio.file.Files;
import java.nio.file.Paths;

public class RelatorioComparativoNovoSistema {
    public static void main(String[] args) throws Exception {
        String[] cenarios = {"CenarioBase", "NovoSistema"};
        int replicacoes = 30;

        StringBuilder sb = new StringBuilder();
        sb.append("Cenario\tTempoEsperaMedia\tPacientesAtendidos\n");

        for (String cenario : cenarios) {
            double tempoSum = 0;
            double pacientesSum = 0;

            for (int i = 1; i <= replicacoes; i++) {
                String reportPath = cenario + "_Run" + i + "_report.html";
                double tempo = parseTallyFromHtml(reportPath, "Tempo Espera - Geral", 3); // média
                int pacientes = (int) parseTallyFromHtml(reportPath, "Tempo Espera - Geral", 2); // obs

                tempoSum += tempo;
                pacientesSum += pacientes;
            }
            sb.append(String.format("%s\t%.4f\t%.1f\n",
                    cenario,
                    tempoSum / replicacoes,
                    pacientesSum / replicacoes
            ));
        }
        Files.write(Paths.get("tabela_comparativo_sistema.tsv"), sb.toString().getBytes());
        System.out.println("Tabela salva em: tabela_comparativo_sistema.tsv");
    }

    private static double parseTallyFromHtml(String filePath, String tallyTitle, int col) throws Exception {
        for (String line : Files.readAllLines(Paths.get(filePath))) {
            if (line.contains(tallyTitle)) {
                String[] parts = line.split("</td><td>");
                return Double.parseDouble(parts[col].replaceAll("[^\\d.,-]", "").replace(",", "."));
            }
        }
        return 0;
    }
}
