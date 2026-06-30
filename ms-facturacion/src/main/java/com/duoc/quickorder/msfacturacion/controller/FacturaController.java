package com.duoc.quickorder.msfacturacion.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.msfacturacion.exception.ResourceNotFoundException;
import com.duoc.quickorder.msfacturacion.model.Factura;
import com.duoc.quickorder.msfacturacion.repository.FacturaRepository;
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
@RequestMapping("/api/facturas")
public class FacturaController {

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);

    
    private final FacturaRepository facturaRepository;

    public FacturaController(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    
    @GetMapping
    public List<Factura> getAll() {
        return facturaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Factura> getById(@PathVariable Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        return ResponseEntity.ok(factura);
    }
    
    @PostMapping
    public ResponseEntity<Factura> create(@Valid @RequestBody Factura factura) {
        factura.setFechaEmision(LocalDateTime.now());
        return new ResponseEntity<>(facturaRepository.save(factura), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Factura> update(@PathVariable Long id, @Valid @RequestBody Factura facturaActualizada) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + id));
        factura.setPedidoId(facturaActualizada.getPedidoId());
        factura.setClienteId(facturaActualizada.getClienteId());
        factura.setMonto(facturaActualizada.getMonto());
        factura.setEstado(facturaActualizada.getEstado());
        return ResponseEntity.ok(facturaRepository.save(factura));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!facturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Factura no encontrada con ID: " + id);
        }
        facturaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}