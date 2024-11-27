package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "esiti", schema = "public")
public class Esito {
    @Id
    @Column(name = "id_visita", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_visita", nullable = false)
    private Visita visita;

    @Column(name = "descrizione_referto")
    private String descrizioneReferto;

    @ColumnDefault("false")
    @Column(name = "visible")
    private Boolean visible;

}