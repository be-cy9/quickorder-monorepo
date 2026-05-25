package com.duoc.quickorder.mssoporte.repository;

import com.duoc.quickorder.mssoporte.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClienteId(Long clienteId);
    List<Ticket> findByEstado(String estado);
}