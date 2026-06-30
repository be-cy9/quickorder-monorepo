package com.duoc.quickorder.mspedidos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.service.PedidoService;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);


    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Pedido>> getAllPedidos() {
        log.info("GET /api/pedidos - Listando todos los pedidos");
        List<EntityModel<Pedido>> pedidos = pedidoService.obtenerTodos().stream()
            .map(pedido -> EntityModel.of(pedido,
                linkTo(methodOn(PedidoController.class).getPedidoById(pedido.getId())).withSelfRel(),
                linkTo(methodOn(PedidoController.class).getAllPedidos()).withRel("all-pedidos")))
            .collect(Collectors.toList());
        return CollectionModel.of(pedidos,
            linkTo(methodOn(PedidoController.class).getAllPedidos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Pedido> getPedidoById(@PathVariable Long id) {
        log.info("GET /api/pedidos/{} - Buscando pedido por ID", id);
        Pedido pedido = pedidoService.obtenerPorId(id);
        return EntityModel.of(pedido,
            linkTo(methodOn(PedidoController.class).getPedidoById(id)).withSelfRel(),
            linkTo(methodOn(PedidoController.class).getAllPedidos()).withRel("all-pedidos"),
            Link.of("/api/pedidos/" + id, "update"),
            Link.of("/api/pedidos/" + id, "delete"));
    }

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@Valid @RequestBody Pedido pedido) {
        log.info("POST /api/pedidos - Creando pedido para clienteId={}", pedido.getClienteId());
        return new ResponseEntity<>(pedidoService.guardarPedido(pedido), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @Valid @RequestBody Pedido pedidoActualizado) {
        log.info("PUT /api/pedidos/{} - Actualizando pedido", id);
        return ResponseEntity.ok(pedidoService.actualizarPedido(id, pedidoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        log.info("DELETE /api/pedidos/{} - Eliminando pedido", id);
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<RespuestaCombinadaDTO> getPedidosConCliente(@PathVariable Long clienteId) {
        log.info("GET /api/pedidos/cliente/{} - Obteniendo pedidos con datos de cliente", clienteId);
        return ResponseEntity.ok(pedidoService.obtenerPedidosConCliente(clienteId));
    }
}