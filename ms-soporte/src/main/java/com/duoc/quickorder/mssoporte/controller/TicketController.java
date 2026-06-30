package com.duoc.quickorder.mssoporte.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.mssoporte.exception.ResourceNotFoundException;
import com.duoc.quickorder.mssoporte.model.Ticket;
import com.duoc.quickorder.mssoporte.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private static final Logger log = LoggerFactory.getLogger(TicketController.class);

    
    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    
    @GetMapping
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + id));
        return ResponseEntity.ok(ticket);
    }
    
    @PostMapping
    public ResponseEntity<Ticket> create(@Valid @RequestBody Ticket ticket) {
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setEstado("ABIERTO");
        Ticket newTicket = ticketRepository.save(ticket);
        return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Long id, @Valid @RequestBody Ticket ticketActualizado) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + id));
        ticket.setAsunto(ticketActualizado.getAsunto());
        ticket.setDescripcion(ticketActualizado.getDescripcion());
        ticket.setEstado(ticketActualizado.getEstado());
        return ResponseEntity.ok(ticketRepository.save(ticket));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket no encontrado con ID: " + id);
        }
        ticketRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<Ticket> getByCliente(@PathVariable Long clienteId) {
        return ticketRepository.findByClienteId(clienteId);
    }
}