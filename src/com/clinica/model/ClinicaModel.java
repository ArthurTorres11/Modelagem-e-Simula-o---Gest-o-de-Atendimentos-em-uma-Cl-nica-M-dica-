package com.clinica.model;

import com.clinica.entity.Paciente;
import com.clinica.entity.TipoPaciente;
import com.clinica.process.GeradorPacientes;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.advancedModellingFeatures.Res;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.BoolDistBernoulli;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Tally;
import desmoj.core.statistic.Count;

public class ClinicaModel extends Model {

    // ============ RECURSOS ============
    public Res consultorios;

    // ============ FILAS ============
    public Queue<Paciente>[] filasConsultorios;
    public Queue<Paciente> filaUnica;

    // ============ DISTRIBUICOES ============
    public ContDistExponential geradorIntervaloChegadas;
    public BoolDistBernoulli geradorTipoPaciente;
    public ContDistNormal geradorTempoAtendUrgente;
    public ContDistNormal geradorTempoAtendNormal;

    // ============ COLETORES DE ESTATISTICAS ============
    public Tally tempoEsperaPacientesUrgentes;
    public Tally tempoEsperaPacientesNormais;
    public Tally tempoEsperaGeral;

    // Coleta de dados por hora
    public Tally[] tempoEsperaPorHora;
    public Count[] pacientesPorHora;

    public ClinicaModel(Model owner, String name, boolean showInReport, boolean showInTrace) {
        super(owner, name, showInReport, showInTrace);
    }

    @Override
    public String description() {
        String tipoFila = Config.USAR_FILA_UNICA ? "FILA UNICA" : "FILAS MULTIPLAS";
        String priorizacao = Config.USAR_PRIORIZACAO ? "COM" : "SEM";

        return "Modelo de simulacao da Clinica Vida Saudavel - " +
                Config.N_CONSULTORIOS + " consultorios, " +
                priorizacao + " priorizacao, " +
                tipoFila;
    }

    @Override
    public void doInitialSchedules() {
        GeradorPacientes primeiraChegada = new GeradorPacientes(this, "PrimeiraChegada", true);
        primeiraChegada.schedule(new TimeSpan(0.0));
    }

    @Override
    public void init() {
        // ============ INICIALIZAR RECURSOS ============
        consultorios = new Res(
                this,
                "Consultorios",
                0,
                Config.N_CONSULTORIOS,
                Config.N_CONSULTORIOS,
                true,
                true
        );

        // ============ INICIALIZAR FILAS ============
        if (Config.USAR_FILA_UNICA) {
            filaUnica = new Queue<>(
                    this,
                    "Fila Unica Global",
                    true,
                    true
            );
        } else {
            filasConsultorios = new Queue[Config.N_CONSULTORIOS];
            for (int i = 0; i < Config.N_CONSULTORIOS; i++) {
                filasConsultorios[i] = new Queue<>(
                        this,
                        "Fila Consultorio " + (i + 1),
                        true,
                        true
                );
            }
        }

        // ============ INICIALIZAR DISTRIBUICOES ============
        geradorIntervaloChegadas = new ContDistExponential(
                this,
                "IntervaloChegadas",
                Config.MEDIA_ENTRE_CHEGADAS,
                true,
                true
        );

        geradorTipoPaciente = new BoolDistBernoulli(
                this,
                "TipoPaciente",
                Config.PROB_URGENTE,
                true,
                true
        );

        geradorTempoAtendUrgente = new ContDistNormal(
                this,
                "TempoAtendUrgente",
                Config.TEMPO_ATEND_URGENTE_MEDIA,
                Config.TEMPO_ATEND_URGENTE_DP,
                true,
                true
        );

        geradorTempoAtendNormal = new ContDistNormal(
                this,
                "TempoAtendNormal",
                Config.TEMPO_ATEND_NORMAL_MEDIA,
                Config.TEMPO_ATEND_NORMAL_DP,
                true,
                true
        );

        // ============ INICIALIZAR COLETORES DE ESTATISTICAS ============
        tempoEsperaPacientesUrgentes = new Tally(
                this,
                "Tempo Espera - Urgentes",
                true,
                true
        );

        tempoEsperaPacientesNormais = new Tally(
                this,
                "Tempo Espera - Menos Graves",
                true,
                true
        );

        tempoEsperaGeral = new Tally(
                this,
                "Tempo Espera - Geral",
                true,
                true
        );

        // ============ INICIALIZAR COLETA POR HORA ============
        int numHoras = (int) Math.ceil(Config.DURACAO_SIMULACAO / 60.0) + 1;
        tempoEsperaPorHora = new Tally[numHoras];
        pacientesPorHora = new Count[numHoras];

        for (int i = 0; i < numHoras; i++) {
            tempoEsperaPorHora[i] = new Tally(
                    this,
                    "Tempo Espera Hora " + i,
                    true,
                    true
            );
            pacientesPorHora[i] = new Count(
                    this,
                    "Pacientes Hora " + i,
                    true,
                    true
            );
        }
    }

    public int obterHora(double tempoSimulacao) {
        return (int) (tempoSimulacao / 60.0);
    }
}
