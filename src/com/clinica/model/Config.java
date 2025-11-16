package com.clinica.model;

public class Config {

    // ============ PARAMETROS DE RECURSOS ============
    public static int N_CONSULTORIOS = 4;
    public static boolean USAR_FILA_UNICA = false;

    // ============ PARAMETROS DE CHEGADA ============
    public static double MEDIA_ENTRE_CHEGADAS = 15.0;
    public static double PROB_URGENTE = 0.30;

    // ============ PARAMETROS DE ATENDIMENTO ============
    public static double TEMPO_ATEND_URGENTE_MEDIA = 10.0;
    public static double TEMPO_ATEND_URGENTE_DP = 3.0;

    public static double TEMPO_ATEND_NORMAL_MEDIA = 20.0;
    public static double TEMPO_ATEND_NORMAL_DP = 5.0;

    // ============ PARAMETROS DE PRIORIZACAO ============
    public static boolean USAR_PRIORIZACAO = false;

    // ============ PARAMETROS DE SIMULACAO ============
    public static double DURACAO_SIMULACAO = 600.0;
    public static String NOME_EXPERIMENTO = "Experimento_Clinica";

    // ============ METODOS PARA ALTERAR PARAMETROS ============
    public static void parseArguments(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--consultorios=")) {
                N_CONSULTORIOS = Integer.parseInt(arg.substring(14));
            } else if (arg.startsWith("--duracao=")) {
                DURACAO_SIMULACAO = Double.parseDouble(arg.substring(10));
            } else if (arg.startsWith("--priorizar=")) {
                USAR_PRIORIZACAO = Boolean.parseBoolean(arg.substring(12));
            } else if (arg.startsWith("--filaUnica=")) {
                USAR_FILA_UNICA = Boolean.parseBoolean(arg.substring(12));
            } else if (arg.startsWith("--tempoNormal=")) {
                TEMPO_ATEND_NORMAL_MEDIA = Double.parseDouble(arg.substring(14));
            } else if (arg.startsWith("--tempoUrgente=")) {
                TEMPO_ATEND_URGENTE_MEDIA = Double.parseDouble(arg.substring(15));
            } else if (arg.startsWith("--mediaChegadas=")) {
                MEDIA_ENTRE_CHEGADAS = Double.parseDouble(arg.substring(16));
            } else if (arg.startsWith("--probUrgente=")) {
                PROB_URGENTE = Double.parseDouble(arg.substring(14));
            } else if (arg.startsWith("--nome=")) {
                NOME_EXPERIMENTO = arg.substring(7);
            }
        }
    }

    public static void printConfig() {
        System.out.println("\n========== CONFIGURACAO DA SIMULACAO ==========");
        System.out.println("N. Consultorios: " + N_CONSULTORIOS);
        System.out.println("Usar Fila Unica: " + USAR_FILA_UNICA);
        System.out.println("Usar Priorizacao: " + USAR_PRIORIZACAO);
        System.out.println("Media entre chegadas: " + MEDIA_ENTRE_CHEGADAS + " min");
        System.out.println("Prob. Urgente: " + (PROB_URGENTE * 100) + "%");
        System.out.println("Tempo Atend. Normal: " + TEMPO_ATEND_NORMAL_MEDIA + " min");
        System.out.println("Tempo Atend. Urgente: " + TEMPO_ATEND_URGENTE_MEDIA + " min");
        System.out.println("Duracao Simulacao: " + DURACAO_SIMULACAO + " min");
        System.out.println("Nome Experimento: " + NOME_EXPERIMENTO);
        System.out.println("==============================================\n");
    }
}
