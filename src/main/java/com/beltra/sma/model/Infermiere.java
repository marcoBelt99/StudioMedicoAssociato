package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Table(name="infermieri")
@Entity
public class Infermiere {


    @Id
    @Column(name="id_anagrafica")
    private Long idAnagrafica;


    @MapsId
    @OneToOne
    @JoinColumn(name="id_anagrafica",
            referencedColumnName = "id_anagrafica"
    )
    private Anagrafica anagrafica;

    @Column(name="num_cartellino")
    private String numCartellino;

    private String tipologia;

    // Getters and Setters
}
