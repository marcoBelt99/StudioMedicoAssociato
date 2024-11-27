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
@Table(name = "ruoli", schema = "public")
public class Ruolo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    //@ColumnDefault("nextval('ruoli_id_ruolo_seq')")
    @Column(name = "id_ruolo", nullable = false)
    private Integer id;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente idUtente;

    @Override
    public String toString() {
        return "Ruolo{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", idUtente=" + idUtente +
                '}';
    }
}