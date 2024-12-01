package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@Table(name = "anagrafiche")
@Entity
public class Anagrafica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anagrafica")
    private Long idAnagrafica;

    private String cognome;
    private String nome;

    @Column(name="data_nascita")
    private Date dataNascita;
    private String genere;

    //@OneToOne(mappedBy = "anagrafica")
    //private Utente utente;

    // Getters and Setters


    @Override
    public String toString() {
        return "Anagrafica{" +
                "idAnagrafica=" + idAnagrafica +
                ", cognome='" + cognome + '\'' +
                ", nome='" + nome + '\'' +
                ", dataNascita=" + dataNascita +
                ", genere='" + genere + '\'' +
                '}';
    }
}
