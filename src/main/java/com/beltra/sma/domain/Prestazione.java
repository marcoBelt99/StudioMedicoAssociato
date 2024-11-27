package com.beltra.sma.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "prestazioni", schema = "public")
public class Prestazione {
    @Id
    @ColumnDefault("nextval('prestazioni_id_prestazione_seq')")
    @Column(name = "id_prestazione", nullable = false)
    private Integer id;

    @Column(name = "titolo", nullable = false, length = 100)
    private String titolo;

    @Column(name = "descrizione", nullable = false, length = Integer.MAX_VALUE)
    private String descrizione;

    @Column(name = "durata_media", nullable = false)
    private Integer durataMedia;

    @Column(name = "costo", nullable = false)
    private Float costo;

    @Column(name = "ticket", nullable = false)
    private Float ticket;

}