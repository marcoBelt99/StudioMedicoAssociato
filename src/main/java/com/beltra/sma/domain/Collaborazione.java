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
@Table(name = "collaborazioni", schema = "public")
public class Collaborazione {
    @Id
    @ColumnDefault("nextval('collaborazioni_id_collaborazione_seq')")
    @Column(name = "id_collaborazione", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_visita", nullable = false)
    private Visita idVisita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Infermiere idAnagrafica;

}