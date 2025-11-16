package com.clinica.entity;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

public class Paciente extends Entity {

    private TipoPaciente tipo;
    private double tempoChegada;

    public Paciente(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }

    public TipoPaciente getTipo() {
        return tipo;
    }

    public void setTipo(TipoPaciente tipo) {
        this.tipo = tipo;
    }

    public double getTempoChegada() {
        return tempoChegada;
    }

    public void setTempoChegada(double tempoChegada) {
        this.tempoChegada = tempoChegada;
    }
}
