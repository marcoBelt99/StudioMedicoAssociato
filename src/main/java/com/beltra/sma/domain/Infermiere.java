package com.beltra.sma.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "infermieri", schema = "public")
public class Infermiere {
    @Id
    @Column(name = "id_anagrafica", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica anagrafica;

    @Column(name = "num_cartellino", nullable = false, length = 20)
    private String numCartellino;

    @Column(name = "tipologia", length = 50)
    private String tipologia;

}