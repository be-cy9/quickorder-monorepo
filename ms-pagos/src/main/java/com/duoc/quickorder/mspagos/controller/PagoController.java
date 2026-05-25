package com.duoc.quickorder.mspagos.controller;

import com.duoc.quickorder.mspagos.model.Pago;
import com.duoc.quickorder.mspagos.repository.PagoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @GetMapping
    public List<Pago> getAllPagos() {
        return pagoRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable Long id) {
        return pagoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Pago> createPago(@Valid @RequestBody Pago pago) {
        pago.setFechaPago(java.time.LocalDateTime.now());
        Pago nuevoPago = pagoRepository.save(pago);
        return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pago> updatePago(@PathVariable Long id, @Valid @RequestBody Pago pagoActualizado) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.setPedidoId(pagoActualizado.getPedidoId());
                    pago.setMonto(pagoActualizado.getMonto());
                    pago.setMetodoPago(pagoActualizado.getMetodoPago());
                    pago.setEstado(pagoActualizado.getEstado());
                    return ResponseEntity.ok(pagoRepository.save(pago));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable Long id) {
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/pedido/{pedidoId}")
    public List<Pago> getPagosByPedido(@PathVariable Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId);
    }
}
