package com.beltra.sma.utils;


import com.beltra.sma.model.Medico;

import java.sql.Time;
import java.util.Date;

public class SlotDisponibile {
    private Date data;
    private Time orario;
    private Medico medico;

    public SlotDisponibile(Date data, Time orario, Medico medico) {
        this.data = data;
        this.orario = orario;
        this.medico = medico;
    }

    // Getters e Setters

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Time getOrario() {
        return orario;
    }

    public void setOrario(Time orario) {
        this.orario = orario;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }


    @Override
    public String toString() {
        return "SlotDisponibile{" +
                "data=" + data +
                ", orario=" + orario +
                ", medico=" + medico.getAnagrafica().getCognome() + " " + medico.getAnagrafica().getNome()+
                '}';
    }
}
