package com.clinica.experiment;

import java.nio.file.Files;
import java.nio.file.Paths;

public class RelatorioTempoEspera {
    public static void main(String[] args) throws Exception {
        // Altere para os nomes corretos do seu projeto:
        String[] cenarios = {
                "CenarioBase",      // FIFO
                "Prioridade"        // COM priorização
        };
        int replicacoes = 30;

        StringBuilder sb = new StringBuilder();
        sb.append("Cenario\tUrgente_Med\tUrgente_STD\tNormal_Med\tNormal_STD\tGeral_Med\tGeral_STD\n");

        for (String cenario : cenarios) {
            double urgenteSum = 0, urgenteSum2 = 0;
            double normalSum = 0, normalSum2 = 0;
            double geralSum = 0, geralSum2 = 0;

            for (int i = 1; i <= replicacoes; i++) {
                String reportPath = cenario + "_Run" + i + "_report.html";
                double urgente = parseTallyFromHtml(reportPath, "Tempo Espera - Urgentes", 3); // média
                double urgenteStd = parseTallyFromHtml(reportPath, "Tempo Espera - Urgentes", 4); // std

                double normal = parseTallyFromHtml(reportPath, "Tempo Espera - Menos Graves", 3);
                double normalStd = parseTallyFromHtml(reportPath, "Tempo Espera - Menos Graves", 4);

                double geral = parseTallyFromHtml(reportPath, "Tempo Espera - Geral", 3);
                double geralStd = parseTallyFromHtml(reportPath, "Tempo Espera - Geral", 4);

                urgenteSum += urgente;
                urgenteSum2 += Math.pow(urgenteStd, 2);
                normalSum += normal;
                normalSum2 += Math.pow(normalStd, 2);
                geralSum += geral;
                geralSum2 += Math.pow(geralStd, 2);
            }
            sb.append(String.format("%s\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n",
                    cenario,
                    urgenteSum / replicacoes, Math.sqrt(urgenteSum2 / replicacoes),
                    normalSum / replicacoes, Math.sqrt(normalSum2 / replicacoes),
                    geralSum / replicacoes, Math.sqrt(geralSum2 / replicacoes)
            ));
        }
        Files.write(Paths.get("tabela_tempo_espera.tsv"), sb.toString().getBytes());
        System.out.println("Tabela de tempo de espera salva em tabela_tempo_espera.tsv");
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
