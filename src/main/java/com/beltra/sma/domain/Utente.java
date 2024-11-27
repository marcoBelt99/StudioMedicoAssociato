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
@Table(name = "utenti", schema = "public")
public class Utente {
    @Id
    @Column(name = "id_utente", nullable = false, length = 20)
    private String idUtente;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @ColumnDefault("true")
    @Column(name = "attivo")
    private Boolean attivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_anagrafica", nullable = false)
    private Anagrafica idAnagrafica;


    @Override
    public String toString() {
        return "Utente{" +
                "idUtente='" + idUtente + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", attivo=" + attivo +
                ", idAnagrafica=" + idAnagrafica +
                '}';
    }
}