package com.duoc.quickorder.mspedidos.controller;

import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    
    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
    
    @Autowired
    private PedidoService pedidoService;
    
    @GetMapping
    public List<Pedido> getAllPedidos() {
        log.info("GET /api/pedidos - Listando todos los pedidos");
        return pedidoService.obtenerTodos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        log.info("GET /api/pedidos/{} - Buscando pedido", id);
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Pedido> createPedido(@Valid @RequestBody Pedido pedido) {
        log.info("POST /api/pedidos - Creando nuevo pedido");
        Pedido nuevoPedido = pedidoService.guardarPedido(pedido);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @Valid @RequestBody Pedido pedidoActualizado) {
        log.info("PUT /api/pedidos/{} - Actualizando pedido", id);
        return pedidoService.actualizarPedido(id, pedidoActualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        log.info("DELETE /api/pedidos/{} - Eliminando pedido", id);
        if (pedidoService.eliminarPedido(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<RespuestaCombinadaDTO> getPedidosConCliente(@PathVariable Long clienteId) {
        log.info("GET /api/pedidos/cliente/{} - Buscando pedidos e información del cliente", clienteId);
        RespuestaCombinadaDTO respuesta = pedidoService.obtenerPedidosConCliente(clienteId);
        return ResponseEntity.ok(respuesta);
    }
}