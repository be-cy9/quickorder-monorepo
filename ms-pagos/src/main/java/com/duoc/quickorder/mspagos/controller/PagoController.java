package com.duoc.quickorder.mspagos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.mspagos.exception.ResourceNotFoundException;
import com.duoc.quickorder.mspagos.model.Pago;
import com.duoc.quickorder.mspagos.repository.PagoRepository;
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
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    
    private final PagoRepository pagoRepository;

    public PagoController(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    
    @GetMapping
    public List<Pago> getAllPagos() {
        return pagoRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
        return ResponseEntity.ok(pago);
    }
    
    @PostMapping
    public ResponseEntity<Pago> createPago(@Valid @RequestBody Pago pago) {
        pago.setFechaPago(java.time.LocalDateTime.now());
        Pago nuevoPago = pagoRepository.save(pago);
        return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pago> updatePago(@PathVariable Long id, @Valid @RequestBody Pago pagoActualizado) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
        pago.setPedidoId(pagoActualizado.getPedidoId());
        pago.setMonto(pagoActualizado.getMonto());
        pago.setMetodoPago(pagoActualizado.getMetodoPago());
        pago.setEstado(pagoActualizado.getEstado());
        return ResponseEntity.ok(pagoRepository.save(pago));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado con ID: " + id);
        }
        pagoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/pedido/{pedidoId}")
    public List<Pago> getPagosByPedido(@PathVariable Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId);
    }
}
