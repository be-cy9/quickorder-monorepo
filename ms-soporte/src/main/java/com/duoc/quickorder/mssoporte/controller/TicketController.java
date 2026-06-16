package com.duoc.quickorder.mssoporte.controller;

import com.duoc.quickorder.mssoporte.model.Ticket;
import com.duoc.quickorder.mssoporte.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @GetMapping
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setAsunto(ticketActualizado.getAsunto());
                    ticket.setDescripcion(ticketActualizado.getDescripcion());
                    ticket.setEstado(ticketActualizado.getEstado());
                    return ResponseEntity.ok(ticketRepository.save(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<Ticket> getByCliente(@PathVariable Long clienteId) {
        return ticketRepository.findByClienteId(clienteId);
    }
}