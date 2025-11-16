package com.clinica.experiment;

import com.clinica.model.ClinicaModel;
import com.clinica.model.Config;
import com.clinica.scenario.Cenarios;
import com.clinica.util.ReportManager;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClinicaExperiment {

    public static void main(String[] args) {
        if (args.length == 0) {
            mostrarMenu();
        } else {
            executarComArgumentos(args);
        }
    }

    public static void mostrarMenu() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n" +
                "╔════════════════════════════════════════════════════════╗\n" +
                "║     SIMULACAO CLINICA VIDA SAUDAVEL                  ║\n" +
                "║            Escolha um cenario                        ║\n" +
                "╚════════════════════════════════════════════════════════╝\n");

        System.out.println("1 - Cenario Base (4 consultorios)");
        System.out.println("2 - Com 3 Consultorios");
        System.out.println("3 - Com 2 Consultorios");
        System.out.println("4 - COM Priorizacao");
        System.out.println("5 - COM Fila Unica");
        System.out.println("6 - Sistema Eletronico (15 min)");
        System.out.println("7 - RODAR TODOS OS CENARIOS (para analise)");
        System.out.println("8 - Simulação Customizada (entrada manual dos parâmetros)");
        System.out.println("0 - Sair");

        System.out.print("\nDigite sua escolha: ");
        int escolha = sc.nextInt();

        switch(escolha) {
            case 1: Cenarios.cenarioBase(); break;
            case 2: Cenarios.cenarioTresConsultorios(); break;
            case 3: Cenarios.cenarioDoisConsultorios(); break;
            case 4: Cenarios.cenariosComPriorizacao(); break;
            case 5: Cenarios.cenarioFilaUnica(); break;
            case 6: Cenarios.cenarioSistemaEletronico(); break;
            case 7: executarTodosCenarios(); break;
            case 8: Cenarios.cenarioCustomizadoPeloUsuario(); break;
            case 0:
                System.out.println("Ate logo!");
                break;
            default:
                System.out.println("Opcao invalida!");
        }

        sc.close();
    }

    public static void executarTodosCenarios() {
        System.out.println("\n Executando TODOS OS CENARIOS PARA ANALISE...\n");

        long tempoInicio = System.currentTimeMillis();

        // Muda o diretório de trabalho para a pasta report
        System.setProperty("user.dir", "report");

        Cenarios.cenarioBase();
        Cenarios.cenarioTresConsultorios();
        Cenarios.cenarioDoisConsultorios();
        Cenarios.cenariosComPriorizacao();
        Cenarios.cenarioFilaUnica();
        Cenarios.cenarioSistemaEletronico();

        // Volta ao diretório original
        System.setProperty("user.dir", ".");

        long tempoTotal = System.currentTimeMillis() - tempoInicio;
        System.out.println("\n TODOS OS CENARIOS CONCLUIDOS!");
        System.out.println("Tempo total: " + (tempoTotal / 1000) + " segundos");
        System.out.println("Verifique a pasta 'report/' para os resultados");
    }

    public static void executarComArgumentos(String[] args) {
        Config.parseArguments(args);
        Config.printConfig();
        ReportManager.setupReportDirectory(Config.NOME_EXPERIMENTO);

        ClinicaModel model = new ClinicaModel(null, "ClinicaVidaSaudavel", true, true);

        Experiment exp = new Experiment(Config.NOME_EXPERIMENTO);
        model.connectToExperiment(exp);

        exp.setShowProgressBar(true);
        exp.stop(new TimeInstant(Config.DURACAO_SIMULACAO, TimeUnit.MINUTES));

        exp.start();
        exp.report();
        exp.finish();

        System.out.println("\n Simulacao concluida!");
    }
}
