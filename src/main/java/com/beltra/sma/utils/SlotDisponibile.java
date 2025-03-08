package com.beltra.sma.utils;


import com.beltra.sma.model.Medico;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
public class SlotDisponibile {
    private Date data;
    private Time orario;
    private Medico medico;


    public SlotDisponibile() {
    }

    public SlotDisponibile(Date data, Medico medico) {
        this.data = data;
        this.medico = medico;
    }

    public SlotDisponibile(Date data, Time orario) {
        this.data = data;
        this.orario = orario;
    }

    public SlotDisponibile(Date data, Time orario, Medico medico) {
        this.data = data;
        this.orario = orario;
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
