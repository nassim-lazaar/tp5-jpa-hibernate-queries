package com.example.repository;

import com.example.model.Reservation;
import com.example.model.Salle;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalleRepositoryImpl implements SalleRepository {

    private final EntityManager entityManager;

    public SalleRepositoryImpl(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public List<Salle> findAvailableRooms(LocalDateTime dateDebut, LocalDateTime dateFin) {
        String jpql = "SELECT s FROM Salle s WHERE s.id NOT IN (" +
                      "SELECT r.salle.id FROM Reservation r WHERE (:dateDebut < r.fin AND :dateFin > r.debut)) " +
                      "ORDER BY s.id";
        return entityManager.createQuery(jpql, Salle.class)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .getResultList();
    }

    @Override
    public List<Salle> searchRooms(String nomSalle, Integer capaciteMinimale, LocalDateTime dateDebut, LocalDateTime dateFin, int numeroPage, int taillePage) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Salle> cq = cb.createQuery(Salle.class);
        Root<Salle> rootSalle = cq.from(Salle.class);

        List<Predicate> filtres = buildPredicates(cb, cq, rootSalle, nomSalle, capaciteMinimale, dateDebut, dateFin);

        cq.select(rootSalle).distinct(true);
        if (!filtres.isEmpty()) {
            cq.where(cb.and(filtres.toArray(new Predicate[0])));
        }
        cq.orderBy(cb.asc(rootSalle.get("id")));

        int indexDepart = numeroPage < 1 ? 0 : (numeroPage - 1) * taillePage;
        TypedQuery<Salle> requete = entityManager.createQuery(cq);
        requete.setFirstResult(indexDepart);
        requete.setMaxResults(taillePage);

        return requete.getResultList();
    }

    @Override
    public long countRooms(String nomSalle, Integer capaciteMinimale, LocalDateTime dateDebut, LocalDateTime dateFin) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Salle> countRoot = countQuery.from(Salle.class);

        List<Predicate> filtres = buildPredicates(cb, countQuery, countRoot, nomSalle, capaciteMinimale, dateDebut, dateFin);

        countQuery.select(cb.count(countRoot));
        if (!filtres.isEmpty()) {
            countQuery.where(cb.and(filtres.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Salle> rootSalle,
                                           String filtreNom, Integer filtreCapaciteMin, LocalDateTime debutCreneau, LocalDateTime finCreneau) {
        List<Predicate> conditions = new ArrayList<>();

        if (filtreNom != null && !filtreNom.trim().isEmpty()) {
            conditions.add(cb.like(cb.lower(rootSalle.get("nom")), "%" + filtreNom.toLowerCase().trim() + "%"));
        }

        if (filtreCapaciteMin != null) {
            conditions.add(cb.ge(rootSalle.get("capacite"), filtreCapaciteMin));
        }

        if (debutCreneau != null && finCreneau != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Reservation> subRoot = subquery.from(Reservation.class);
            subquery.select(subRoot.get("salle").get("id"));

            Predicate conflitHoraire = cb.and(
                    cb.lessThan(subRoot.get("debut"), finCreneau),
                    cb.greaterThan(subRoot.get("fin"), debutCreneau)
            );
            subquery.where(conflitHoraire);
            conditions.add(cb.not(rootSalle.get("id").in(subquery)));
        }

        return conditions;
    }
}
