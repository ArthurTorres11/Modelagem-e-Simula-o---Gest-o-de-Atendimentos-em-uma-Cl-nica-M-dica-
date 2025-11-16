package com.clinica.scenario;

import java.io.File;
import java.nio.file.*;

import com.clinica.model.ClinicaModel;
import com.clinica.model.Config;
import com.clinica.util.ReportManager;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Cenarios {

    public static void cenarioBase() {
        ReportManager.setupReportDirectory("Cenario_1_Base");
        System.out.println("\n Executando: Cenario Base (4 consultorios)");
        Config.N_CONSULTORIOS = 4;
        Config.USAR_PRIORIZACAO = false;
        Config.USAR_FILA_UNICA = false;
        Config.DURACAO_SIMULACAO = 600;
        Config.TEMPO_ATEND_NORMAL_MEDIA = 20;
        executarSimulacao("Cenario_Base");
    }

    public static void cenarioTresConsultorios() {
        ReportManager.setupReportDirectory("Cenario_2_3Consultorios");
        System.out.println("\n Executando: 3 Consultorios");
        Config.N_CONSULTORIOS = 3;
        Config.USAR_PRIORIZACAO = false;
        Config.USAR_FILA_UNICA = false;
        Config.DURACAO_SIMULACAO = 600;
        Config.TEMPO_ATEND_NORMAL_MEDIA = 20;
        executarSimulacao("Cenario_3_Consultorios");
    }

    public static void cenarioDoisConsultorios() {
        ReportManager.setupReportDirectory("Cenario_3_2Consultorios");
        System.out.println("\n Executando: 2 Consultorios");
        Config.N_CONSULTORIOS = 2;
        Config.USAR_PRIORIZACAO = false;
        Config.USAR_FILA_UNICA = false;
        Config.DURACAO_SIMULACAO = 600;
        Config.TEMPO_ATEND_NORMAL_MEDIA = 20;
        executarSimulacao("Cenario_2_Consultorios");
    }

    public static void cenariosComPriorizacao() {
        ReportManager.setupReportDirectory("Cenario_4_ComPriorizacao");
        System.out.println("\n Executando: 4 Consultorios COM Priorizacao");
        Config.N_CONSULTORIOS = 4;
        Config.USAR_PRIORIZACAO = true;
        Config.USAR_FILA_UNICA = false;
        Config.DURACAO_SIMULACAO = 600;
        Config.TEMPO_ATEND_NORMAL_MEDIA = 20;
        executarSimulacao("Cenario_Com_Priorizacao");
    }

    public static void cenarioFilaUnica() {
        ReportManager.setupReportDirectory("Cenario_5_FilaUnica");
        System.out.println("\n Executando: Fila Unica");
        Config.N_CONSULTORIOS = 4;
        Config.USAR_PRIORIZACAO = false;
        Config.USAR_FILA_UNICA = true;
        Config.DURACAO_SIMULACAO = 600;
        Config.TEMPO_ATEND_NORMAL_MEDIA = 20;
        executarSimulacao("Cenario_Fila_Unica");
    }

    public static void cenarioSistemaEletronico() {
        ReportManager.setupReportDirectory("Cenario_6_SistemaEletronico");
        System.out.println("\n Executando: Sistema Eletronico (tempo reduzido para 15 min)");
        Config.N_CONSULTORIOS = 4;
        Config.TEMPO_ATEND_NORMAL_MEDIA = 15.0;
        Config.USAR_PRIORIZACAO = false;
        Config.USAR_FILA_UNICA = false;
        Config.DURACAO_SIMULACAO = 600;
        executarSimulacao("Cenario_Sistema_Eletronico");
    }

    private static void executarSimulacao(String nomeExperimento) {
        Config.printConfig();

        ClinicaModel model = new ClinicaModel(null, "ClinicaVidaSaudavel", true, true);

        Experiment exp = new Experiment(nomeExperimento);
        model.connectToExperiment(exp);

        exp.setShowProgressBar(true);
        exp.stop(new TimeInstant(Config.DURACAO_SIMULACAO, TimeUnit.MINUTES));

        exp.start();
        exp.report();
        exp.finish();

        System.out.println("Simulacao concluida: " + nomeExperimento);

        // === 1. Move o HTML principal para a pasta do cenário ===
        String destinoDir = com.clinica.util.ReportManager.getCurrentReportDir();
        File destinoPasta = new File(destinoDir);
        if (!destinoPasta.exists()) destinoPasta.mkdirs();

        String htmlName = nomeExperimento + "_report.html";
        File origem = new File(htmlName);
        if (origem.exists()) {
            try {
                Files.move(origem.toPath(),
                        Paths.get(destinoDir, htmlName),
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Relatorio movido para: " + destinoDir);
            } catch (Exception e) {
                System.out.println("Nao foi possivel mover o relatorio: " + e.getMessage());
            }
        }

        // === 2. Move os arquivos de log para a subpasta logs ===
        File logsSubdir = new File(destinoDir, "logs");
        if (!logsSubdir.exists()) logsSubdir.mkdirs();

        for (String sufixo : new String[] { "_error.html", "_debug.html", "_trace.html" }) {
            String nomeArquivo = nomeExperimento + sufixo;
            File origemLog = new File(nomeArquivo);
            if (origemLog.exists()) {
                try {
                    Files.move(origemLog.toPath(),
                            Paths.get(logsSubdir.getPath(), nomeArquivo),
                            StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Arquivo de log movido: " + nomeArquivo);
                } catch (Exception e) {
                    System.out.println("Nao foi possivel mover o log " + nomeArquivo + ": " + e.getMessage());
                }
            }
        }
    }


    public static void cenarioCustomizadoPeloUsuario() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n === SIMULACAO CUSTOMIZADA ===");
        System.out.print("Número de consultórios: ");
        int nConsult = sc.nextInt();

        System.out.print("Tempo atendimento normal (min): ");
        double tempoNormal = sc.nextDouble();

        System.out.print("Tempo atendimento urgente (min): ");
        double tempoUrgente = sc.nextDouble();

        System.out.print("Probabilidade de paciente urgente (0-1): ");
        double probUrgente = sc.nextDouble();

        System.out.print("Média entre chegadas (min): ");
        double mediaChegada = sc.nextDouble();

        System.out.print("Duração da simulação (min): ");
        double duracao = sc.nextDouble();

        System.out.print("Usar priorização? (true/false): ");
        boolean priorizar = sc.nextBoolean();

        System.out.print("Usar fila única? (true/false): ");
        boolean filaUnica = sc.nextBoolean();

        // Parâmetros customizados
        ReportManager.setupReportDirectory("Simulacao_Customizada");

        Config.N_CONSULTORIOS = nConsult;
        Config.TEMPO_ATEND_NORMAL_MEDIA = tempoNormal;
        Config.TEMPO_ATEND_URGENTE_MEDIA = tempoUrgente;
        Config.PROB_URGENTE = probUrgente;
        Config.MEDIA_ENTRE_CHEGADAS = mediaChegada;
        Config.DURACAO_SIMULACAO = duracao;
        Config.USAR_PRIORIZACAO = priorizar;
        Config.USAR_FILA_UNICA = filaUnica;

        System.out.println("\n Iniciando simulação customizada...");
        executarSimulacao("Simulacao_Customizada");
    }

}
