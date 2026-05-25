package com.duoc.quickorder.mspedidos.controller;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.repository.PedidoRepository;
import com.duoc.quickorder.mspedidos.service.ClienteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    
    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ClienteService clienteService;  // ← INYECTAR EL SERVICIO
    
    @GetMapping
    public List<Pedido> getAllPedidos() {
        log.info("GET /api/pedidos - Listando todos los pedidos");
        return pedidoRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        log.info("GET /api/pedidos/{} - Buscando pedido", id);
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createPedido(@Valid @RequestBody Pedido pedido) {
        log.info("POST /api/pedidos - Creando nuevo pedido");
        
        // 1. Obtener los datos del cliente usando WebClient
        ClienteDTO cliente = clienteService.obtenerClientePorId(pedido.getClienteId());
        
        // 2. Guardar el pedido
        pedido.setFechaCreacion(LocalDateTime.now());
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        
        // 3. Retornar el pedido junto con los datos del cliente
        log.info("Pedido creado con ID: {} para el cliente: {}", nuevoPedido.getId(), cliente.getNombre());
        
        // Retornar una respuesta enriquecida con datos del cliente
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @Valid @RequestBody Pedido pedidoActualizado) {
        log.info("PUT /api/pedidos/{} - Actualizando pedido", id);
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    pedido.setClienteId(pedidoActualizado.getClienteId());
                    pedido.setDescripcion(pedidoActualizado.getDescripcion());
                    pedido.setMonto(pedidoActualizado.getMonto());
                    pedido.setEstado(pedidoActualizado.getEstado());
                    Pedido updated = pedidoRepository.save(pedido);
                    log.info("Pedido {} actualizado correctamente", id);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        log.info("DELETE /api/pedidos/{} - Eliminando pedido", id);
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            log.info("Pedido {} eliminado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("No se encontró pedido con ID: {} para eliminar", id);
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getPedidosConCliente(@PathVariable Long clienteId) {
        log.info("GET /api/pedidos/cliente/{} - Buscando pedidos del cliente", clienteId);
        
        // Obtener datos del cliente
        ClienteDTO cliente = clienteService.obtenerClientePorId(clienteId);
        
        // Obtener pedidos del cliente
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        
        // Retornar una respuesta combinada
        return ResponseEntity.ok(new RespuestaCombinada(cliente, pedidos));
    }
}

// Clase auxiliar para combinar respuesta
class RespuestaCombinada {
    private ClienteDTO cliente;
    private List<Pedido> pedidos;
    
    public RespuestaCombinada(ClienteDTO cliente, List<Pedido> pedidos) {
        this.cliente = cliente;
        this.pedidos = pedidos;
    }
    
    public ClienteDTO getCliente() { return cliente; }
    public List<Pedido> getPedidos() { return pedidos; }
}