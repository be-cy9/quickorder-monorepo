package com.duoc.quickorder.msfacturacion.controller;

import com.duoc.quickorder.msfacturacion.model.Factura;
import com.duoc.quickorder.msfacturacion.repository.FacturaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    @GetMapping
    public List<Factura> getAll() {
        return facturaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Factura> getById(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Factura> create(@Valid @RequestBody Factura factura) {
        factura.setFechaEmision(LocalDateTime.now());
        return new ResponseEntity<>(facturaRepository.save(factura), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Factura> update(@PathVariable Long id, @Valid @RequestBody Factura facturaActualizada) {
        return facturaRepository.findById(id)
                .map(factura -> {
                    factura.setPedidoId(facturaActualizada.getPedidoId());
                    factura.setClienteId(facturaActualizada.getClienteId());
                    factura.setMonto(facturaActualizada.getMonto());
                    factura.setEstado(facturaActualizada.getEstado());
                    return ResponseEntity.ok(facturaRepository.save(factura));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (facturaRepository.existsById(id)) {
            facturaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}