package com.example;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.service.SalleService;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class SalleManagementTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private SalleRepository repository;
    private SalleService service;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("SallePU");
        em = emf.createEntityManager();
        repository = new SalleRepositoryImpl(em);

        SalleServiceImpl serviceImpl = new SalleServiceImpl();
        serviceImpl.setEntityManager(em);
        service = serviceImpl;

        // Populate initial data
        em.getTransaction().begin();

        Salle s1 = new Salle("Salle 101", 20);
        Salle s2 = new Salle("Salle 202", 50);
        Salle s3 = new Salle("Auditorium", 150);

        em.persist(s1);
        em.persist(s2);
        em.persist(s3);

        LocalDateTime ref = LocalDateTime.of(2026, 6, 1, 10, 0);
        Reservation r1 = new Reservation(s1, ref, ref.plusHours(2)); // 10h to 12h
        em.persist(r1);

        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    public void testFindAvailableRooms() {
        LocalDateTime testStart = LocalDateTime.of(2026, 6, 1, 10, 30);
        LocalDateTime testEnd = LocalDateTime.of(2026, 6, 1, 11, 30);

        List<Salle> available = service.getAvailableRooms(testStart, testEnd);

        // Salle 101 is booked 10h-12h, so only Salle 202 and Auditorium should be available
        assertEquals(2, available.size());
        assertTrue(available.stream().noneMatch(s -> s.getNom().equals("Salle 101")));
        assertTrue(available.stream().anyMatch(s -> s.getNom().equals("Salle 202")));
        assertTrue(available.stream().anyMatch(s -> s.getNom().equals("Auditorium")));
    }

    @Test
    public void testSearchRoomsWithCapacityFilter() {
        PaginationResult<Salle> result = service.searchRoomsPaginated(null, 40, null, null, 1, 10);
        assertEquals(2, result.getTotalItems());
        assertTrue(result.getItems().stream().allMatch(s -> s.getCapacite() >= 40));
    }

    @Test
    public void testSearchRoomsWithNameFilter() {
        PaginationResult<Salle> result = service.searchRoomsPaginated("auditorium", null, null, null, 1, 10);
        assertEquals(1, result.getTotalItems());
        assertEquals("Auditorium", result.getItems().get(0).getNom());
    }

    @Test
    public void testPagination() {
        PaginationResult<Salle> page1 = service.searchRoomsPaginated(null, null, null, null, 1, 2);
        assertEquals(3, page1.getTotalItems());
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page1.getItems().size());
        assertEquals(1, page1.getCurrentPage());

        PaginationResult<Salle> page2 = service.searchRoomsPaginated(null, null, null, null, 2, 2);
        assertEquals(1, page2.getItems().size());
        assertEquals(2, page2.getCurrentPage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDatesThrowException() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 15, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 14, 0);
        service.getAvailableRooms(start, end);
    }
}
