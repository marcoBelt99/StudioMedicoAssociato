package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "medici", schema = "public")
public class Medico {
    @Id
    @Column(name = "id_anagrafica", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica anagrafica;

    @Column(name = "matricola", nullable = false, length = 20)
    private String matricola;

    @Column(name = "specializzazione", length = 100)
    private String specializzazione;

}