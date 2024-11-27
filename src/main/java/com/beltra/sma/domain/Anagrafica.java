package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "anagrafiche", schema = "public")
public class Anagrafica {
    @Id
    //@ColumnDefault("nextval('anagrafiche_id_anagrafica_seq')")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_anagrafica", nullable = false)
    private Integer id;

    @Column(name = "cognome", nullable = false, length = 50)
    private String cognome;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "data_nascita", nullable = false)
    private Date dataNascita;

    @Column(name = "genere", nullable = false, length = Integer.MAX_VALUE)
    private String genere;

    @Override
    public String toString() {
        return "Anagrafica{" +
                "id=" + id +
                ", cognome='" + cognome + '\'' +
                ", nome='" + nome + '\'' +
                ", dataNascita=" + dataNascita +
                ", genere='" + genere + '\'' +
                '}';
    }
}