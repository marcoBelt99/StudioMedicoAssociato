package com.beltra.sma.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="utenti")
public class Utente {

    @Id
    @Column(name = "id_utente")
    private String idUtente;

    @Column(name="username", nullable = false, unique = true)
    private String username;
    private String password;
    private Boolean attivo;

    @OneToOne
    @JoinColumn(name="id_anagrafica",
            referencedColumnName = "id_anagrafica",
            nullable = false)
    private Anagrafica anagrafica;


    @OneToMany(
            mappedBy = "utente",
            //fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<Ruolo> ruoli = new HashSet<>();


    /** TODO: Metodo helper per gestione bidirezionale.
     *   si occupa di mantenere la relazione bidirezionale tra Utente e Ruolo.
     * */
    public void addRuolo(Ruolo ruolo) {
        this.ruoli.add(ruolo);
        ruolo.setUtente(this); // Associazione bidirezionale
    }


//    public void addAnagrafica(Anagrafica anagrafica) {
//        this.anagrafica = anagrafica;
//        anagrafica.setUtente(this);
//    }

    // Getters and Setters
}
