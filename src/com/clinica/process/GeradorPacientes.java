package com.clinica.process;

import com.clinica.entity.Paciente;
import com.clinica.entity.TipoPaciente;
import com.clinica.model.ClinicaModel;

import co.paralleluniverse.fibers.SuspendExecution;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

public class GeradorPacientes extends ExternalEvent {

    private ClinicaModel model;
    private static int contadorPacientes = 0;

    public GeradorPacientes(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        this.model = (ClinicaModel) owner;
    }

    @Override
    public void eventRoutine() throws SuspendExecution {

        // 1. CRIAR NOVO PACIENTE
        contadorPacientes++;
        Paciente novoPaciente = new Paciente(
                model,
                "Paciente_" + contadorPacientes,
                true
        );

        // 2. DEFINIR TIPO
        boolean ehUrgente = model.geradorTipoPaciente.sample();
        if (ehUrgente) {
            novoPaciente.setTipo(TipoPaciente.URGENTE);
        } else {
            novoPaciente.setTipo(TipoPaciente.MENOS_GRAVE);
        }

        // 3. REGISTRAR TEMPO DE CHEGADA
        novoPaciente.setTempoChegada(model.presentTime().getTimeAsDouble());

        // 4. CRIAR E ATIVAR PROCESSO
        ProcessoAtendimento processo = new ProcessoAtendimento(
                model,
                "Processo_" + contadorPacientes,
                true,
                novoPaciente
        );
        processo.activate();

        // 5. AGENDAR PROXIMA CHEGADA
        double intervalo = model.geradorIntervaloChegadas.sample();

        GeradorPacientes proximaChegada = new GeradorPacientes(
                model,
                "ChegadaPaciente",
                true
        );
        proximaChegada.schedule(new TimeSpan(intervalo));
    }
}
