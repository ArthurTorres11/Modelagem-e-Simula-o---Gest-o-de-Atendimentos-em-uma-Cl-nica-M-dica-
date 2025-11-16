package com.clinica.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {

    private static final String REPORTS_DIR = "report";
    private static String currentReportDir;

    public static void setupReportDirectory(String scenarioName) {
        // Criar pasta raiz "report" se nao existir
        File reportsFolder = new File(REPORTS_DIR);
        if (!reportsFolder.exists()) {
            reportsFolder.mkdir();
        }

        // Criar pasta com timestamp para cada execucao
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String scenarioDir = scenarioName.replace(" ", "_");
        currentReportDir = REPORTS_DIR + File.separator + scenarioDir + "_" + timestamp;

        File scenarioFolder = new File(currentReportDir);
        if (!scenarioFolder.exists()) {
            scenarioFolder.mkdirs();
        }

        System.out.println("Relatorios serao salvos em: " + currentReportDir);
    }

    public static String getCurrentReportDir() {
        return currentReportDir;
    }

    public static void saveExecutionSummary(String content) {
        try {
            FileWriter writer = new FileWriter(currentReportDir + File.separator + "resumo.txt");
            writer.write(content);
            writer.close();
            System.out.println("Resumo salvo!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
