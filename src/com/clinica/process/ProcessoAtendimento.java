package com.clinica.process;

import com.clinica.entity.Paciente;
import com.clinica.entity.TipoPaciente;
import com.clinica.model.ClinicaModel;
import com.clinica.model.Config;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class ProcessoAtendimento extends SimProcess {

    private ClinicaModel model;
    private Paciente paciente;

    public ProcessoAtendimento(Model owner, String name, boolean showInTrace, Paciente paciente) {
        super(owner, name, showInTrace);
        this.model = (ClinicaModel) owner;
        this.paciente = paciente;
    }

    @Override
    public void lifeCycle() throws SuspendExecution {

        Queue<Paciente> filaEscolhida = null;

        // ============ ESCOLHER A FILA ============
        if (Config.USAR_FILA_UNICA) {
            filaEscolhida = model.filaUnica;
        } else {
            filaEscolhida = getFilaMaisCurta();
        }

        // ============ ENTRAR NA FILA ============
        filaEscolhida.insert(paciente);

        // ============ REQUISITAR O RECURSO ============
        if (!model.consultorios.provide(1)) {
            passivate();
        }

        // ============ SAIR DA FILA ============
        filaEscolhida.remove(paciente);

        // ============ CALCULAR TEMPO DE ESPERA ============
        double tempoChegada = paciente.getTempoChegada();
        double tempoInicioAtendimento = model.presentTime().getTimeAsDouble();
        double tempoEspera = tempoInicioAtendimento - tempoChegada;

        // Atualizar estatisticas gerais
        if (paciente.getTipo() == TipoPaciente.URGENTE) {
            model.tempoEsperaPacientesUrgentes.update(tempoEspera);
        } else {
            model.tempoEsperaPacientesNormais.update(tempoEspera);
        }
        model.tempoEsperaGeral.update(tempoEspera);

        // Atualizar estatisticas por hora
        int hora = model.obterHora(tempoInicioAtendimento);
        if (hora < model.tempoEsperaPorHora.length) {
            model.tempoEsperaPorHora[hora].update(tempoEspera);
            model.pacientesPorHora[hora].update();
        }

        // ============ SIMULAR O ATENDIMENTO ============
        double tempoAtendimento;
        if (paciente.getTipo() == TipoPaciente.URGENTE) {
            tempoAtendimento = model.geradorTempoAtendUrgente.sample();
        } else {
            tempoAtendimento = model.geradorTempoAtendNormal.sample();
        }

        if (tempoAtendimento < 0) {
            tempoAtendimento = 1.0;
        }

        hold(new TimeSpan(tempoAtendimento));

        // ============ LIBERAR O RECURSO ============
        model.consultorios.takeBack(1);

        // ============ ATIVAR PROXIMO PACIENTE ============
        if (!filaEscolhida.isEmpty()) {
            Paciente proximo = filaEscolhida.first();
            if (proximo != null && proximo.isScheduled()) {
                // O sistema ja ativa automaticamente
            }
        }
    }

    private Queue<Paciente> getFilaMaisCurta() {
        Queue<Paciente>[] filas = model.filasConsultorios;

        if (Config.USAR_PRIORIZACAO && paciente.getTipo() == TipoPaciente.URGENTE) {
            Queue<Paciente> filaMaisCurta = filas[0];
            int menorTamanho = filaMaisCurta.length();

            for (int i = 1; i < filas.length; i++) {
                int tamanho = filas[i].length();
                if (tamanho < menorTamanho) {
                    menorTamanho = tamanho;
                    filaMaisCurta = filas[i];
                }
            }
            return filaMaisCurta;
        } else {
            Queue<Paciente> filaMaisCurta = filas[0];
            int menorTamanho = filaMaisCurta.length();

            for (int i = 1; i < filas.length; i++) {
                int tamanho = filas[i].length();
                if (tamanho < menorTamanho) {
                    menorTamanho = tamanho;
                    filaMaisCurta = filas[i];
                }
            }
            return filaMaisCurta;
        }
    }
}
