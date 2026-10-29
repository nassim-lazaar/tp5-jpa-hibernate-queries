package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

    public Salle() {
    }

    public Salle(String designationSalle, int capaciteAccueil) {
        this.nom = designationSalle;
        this.capacite = capaciteAccueil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idSalle) {
        this.id = idSalle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String designationSalle) {
        this.nom = designationSalle;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capaciteAccueil) {
        this.capacite = capaciteAccueil;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> ensembleReservations) {
        this.reservations = ensembleReservations;
    }

    public void addReservation(Reservation nouvelleReservation) {
        if (nouvelleReservation != null) {
            this.reservations.add(nouvelleReservation);
            nouvelleReservation.setSalle(this);
        }
    }

    public void removeReservation(Reservation reservationSupprimee) {
        if (reservationSupprimee != null) {
            this.reservations.remove(reservationSupprimee);
            reservationSupprimee.setSalle(null);
        }
    }

    @Override
    public String toString() {
        return "Salle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}
