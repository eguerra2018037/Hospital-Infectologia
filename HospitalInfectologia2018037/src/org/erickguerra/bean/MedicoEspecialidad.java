package org.erickguerra.bean;

public class MedicoEspecialidad {
    int codigoMedicoEspecialidad;
    int codigoMedico;
    int codigoEspecialidad;
    int codigoHorario;

    public MedicoEspecialidad() {
    }

    public MedicoEspecialidad(int codigoMedicoEspecialidad, int codigoMedico, int codigoEspecialidad, int codigoHorario) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
        this.codigoMedico = codigoMedico;
        this.codigoEspecialidad = codigoEspecialidad;
        this.codigoHorario = codigoHorario;
    }

    public int getCodigoMedicoEspecialidad() {
        return codigoMedicoEspecialidad;
    }

    public void setCodigoMedicoEspecialidad(int codigoMedicoEspecialidad) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
    }

    public int getCodigoMedico() {
        return codigoMedico;
    }

    public void setCodigoMedico(int codigoMedico) {
        this.codigoMedico = codigoMedico;
    }

    public int getCodigoEspecialidad() {
        return codigoEspecialidad;
    }

    public void setCodigoEspecialidad(int codigoEspecialidad) {
        this.codigoEspecialidad = codigoEspecialidad;
    }

    public int getCodigoHorario() {
        return codigoHorario;
    }

    public void setCodigoHorario(int codigoHorario) {
        this.codigoHorario = codigoHorario;
    }
    
    
}
