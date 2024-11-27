package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "pazienti", schema = "public")
public class Paziente {
    @Id
    @Column(name = "id_anagrafica", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica anagrafica;

    @Column(name = "codice_fiscale", nullable = false, length = 16)
    private String codiceFiscale;

    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "residenza", nullable = false, length = 100)
    private String residenza;

    @Override
    public String toString() {
        return "Paziente{" +
                "id=" + id +
                ", anagrafica=" + anagrafica +
                ", codiceFiscale='" + codiceFiscale + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", residenza='" + residenza + '\'' +
                '}';
    }
}