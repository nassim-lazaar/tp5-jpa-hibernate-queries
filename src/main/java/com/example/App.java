package com.example;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.service.SalleService;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("   TP 5 : REQUETES AVANCEES JPA - CRITERIA API, DISPONIBILITE & PAGINATION");
        System.out.println("========================================================================");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Salle salle1 = new Salle("Salle A101 - Informatique", 30);
            Salle salle2 = new Salle("Salle B202 - Reunion", 50);
            Salle salle3 = new Salle("Salle C303 - Atelier", 15);
            Salle salle4 = new Salle("Amphitheatre Pasteur", 120);
            Salle salle5 = new Salle("Espace Innovation", 45);

            em.persist(salle1);
            em.persist(salle2);
            em.persist(salle3);
            em.persist(salle4);
            em.persist(salle5);

            // Creation d'une reservation sur Salle A101 de 10h00 a 12h00 le 01/06/2026
            LocalDateTime refDate = LocalDateTime.of(2026, 6, 1, 10, 0);
            Reservation resA101 = new Reservation(salle1, refDate, refDate.plusHours(2));

            // Creation d'une reservation sur Salle B202 de 14h00 a 16h00 le 01/06/2026
            Reservation resB202 = new Reservation(salle2, refDate.plusHours(4), refDate.plusHours(6));

            em.persist(resA101);
            em.persist(resB202);

            em.getTransaction().commit();

            SalleServiceImpl serviceImpl = new SalleServiceImpl();
            serviceImpl.setEntityManager(em);
            SalleService service = serviceImpl;

            // 1. Test JPQL - Salles disponibles sur le creneau 09h30 - 11h30 (chevauche la reservation de A101)
            System.out.println("\n[1] RECHERCHE DE SALLES DISPONIBLES (JPQL)");
            System.out.println("------------------------------------------------------------------------");
            LocalDateTime slotDebut = LocalDateTime.of(2026, 6, 1, 9, 30);
            LocalDateTime slotFin = LocalDateTime.of(2026, 6, 1, 11, 30);
            System.out.println("Creneau demande : " + slotDebut + " -> " + slotFin);

            List<Salle> disponibles = service.getAvailableRooms(slotDebut, slotFin);
            System.out.println("Nombre de salles disponibles trouvees : " + disponibles.size());
            for (Salle s : disponibles) {
                System.out.println("  -> [ID: " + s.getId() + "] " + s.getNom() + " (Capacite: " + s.getCapacite() + " places)");
            }

            // 2. Test Criteria API - Recherche multi-critères avec filtre capacite >= 40 et disponibilite
            System.out.println("\n[2] RECHERCHE MULTI-CRITERES DYNAMIQUE (CRITERIA API)");
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Filtres : capaciteMin >= 40, creneau horaire " + slotDebut + " -> " + slotFin);
            PaginationResult<Salle> resultCrit = service.searchRoomsPaginated(null, 40, slotDebut, slotFin, 1, 10);
            System.out.println("Resultats : " + resultCrit.getTotalItems() + " salle(s) concordante(s)");
            for (Salle s : resultCrit.getItems()) {
                System.out.println("  -> [ID: " + s.getId() + "] " + s.getNom() + " (Capacite: " + s.getCapacite() + " places)");
            }

            // 3. Test Pagination serveur (taille de page = 2)
            System.out.println("\n[3] TEST DE PAGINATION SERVEUR (PAGE SIZE = 2)");
            System.out.println("------------------------------------------------------------------------");
            PaginationResult<Salle> page1 = service.searchRoomsPaginated(null, null, null, null, 1, 2);
            System.out.println("Page 1 / " + page1.getTotalPages() + " (Total elements : " + page1.getTotalItems() + ") :");
            for (Salle s : page1.getItems()) {
                System.out.println("  [P1] " + s.getNom() + " (Capacite: " + s.getCapacite() + ")");
            }

            PaginationResult<Salle> page2 = service.searchRoomsPaginated(null, null, null, null, 2, 2);
            System.out.println("Page 2 / " + page2.getTotalPages() + " :");
            for (Salle s : page2.getItems()) {
                System.out.println("  [P2] " + s.getNom() + " (Capacite: " + s.getCapacite() + ")");
            }

            System.out.println("\n========================================================================");
            System.out.println("   VALIDATION TERMINEE AVEC SUCCES : JPQL & CRITERIA API FONCTIONNELS   ");
            System.out.println("========================================================================");

        } finally {
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
        }
    }
}
