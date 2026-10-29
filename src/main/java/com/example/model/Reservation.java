package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @Column(nullable = false)
    private LocalDateTime debut;

    @Column(nullable = false)
    private LocalDateTime fin;

    public Reservation() {
    }

    public Reservation(Salle salleReservee, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.salle = salleReservee;
        this.debut = dateDebut;
        this.fin = dateFin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idReservation) {
        this.id = idReservation;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salleAssociee) {
        this.salle = salleAssociee;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debutCreneau) {
        this.debut = debutCreneau;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime finCreneau) {
        this.fin = finCreneau;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", salleId=" + (salle != null ? salle.getId() : "null") +
                ", debut=" + debut +
                ", fin=" + fin +
                '}';
    }
}
