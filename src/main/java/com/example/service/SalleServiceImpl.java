package com.example.service;

import com.example.model.Salle;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class SalleServiceImpl implements SalleService {

    private SalleRepository salleRepository;

    public SalleServiceImpl() {
    }

    public SalleServiceImpl(SalleRepository repository) {
        this.salleRepository = repository;
    }

    public void setEntityManager(EntityManager em) {
        this.salleRepository = new SalleRepositoryImpl(em);
    }

    @Override
    public List<Salle> getAvailableRooms(LocalDateTime debut, LocalDateTime fin) {
        if (debut != null && fin != null && debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin.");
        }
        return salleRepository.findAvailableRooms(debut, fin);
    }

    @Override
    public PaginationResult<Salle> searchRoomsPaginated(String nom, Integer capaciteMin, LocalDateTime debut, LocalDateTime fin, int page, int size) {
        List<Salle> items = salleRepository.searchRooms(nom, capaciteMin, debut, fin, page, size);
        long total = salleRepository.countRooms(nom, capaciteMin, debut, fin);
        return new PaginationResult<>(items, page < 1 ? 1 : page, size, total);
    }
}
