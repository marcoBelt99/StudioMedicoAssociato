package com.beltra.sma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="pazienti")
@Entity
public class Paziente {

    @Id
    @Column(name="id_anagrafica")
    private Long idAnagrafica;

    @MapsId
    @OneToOne
    @JoinColumn(name="id_anagrafica",
                referencedColumnName = "id_anagrafica"
            //,
               // nullable = false
    )
    private Anagrafica anagrafica;

    @Column(name="codice_fiscale")
    private String codiceFiscale;
    private String telefono;
    private String email;
    private String residenza;

    // Getters and Setters
}
