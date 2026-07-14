package com.duoc.quickorder.mspedidos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Gestión de Pedidos", description = "Operaciones para crear, consultar, actualizar y eliminar pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(
        summary = "Obtener todos los pedidos",
        description = "Retorna la lista completa de pedidos registrados en el sistema con enlaces HATEOAS de navegación."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
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

    @Operation(
        summary = "Buscar pedido por ID",
        description = "Retorna un pedido específico según su identificador único, incluyendo enlaces HATEOAS."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public EntityModel<Pedido> getPedidoById(
            @Parameter(description = "Identificador único del pedido", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/pedidos/{} - Buscando pedido por ID", id);
        Pedido pedido = pedidoService.obtenerPorId(id);
        return EntityModel.of(pedido,
            linkTo(methodOn(PedidoController.class).getPedidoById(id)).withSelfRel(),
            linkTo(methodOn(PedidoController.class).getAllPedidos()).withRel("all-pedidos"),
            Link.of("/api/pedidos/" + id, "actualizar"),
            Link.of("/api/pedidos/" + id, "eliminar"));
    }

    @Operation(
        summary = "Crear un nuevo pedido",
        description = "Registra un nuevo pedido en el sistema. Se valida que el cliente exista en ms-clientes antes de guardar."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
            content = @Content(schema = @Schema(implementation = Pedido.class))),
        @ApiResponse(responseCode = "400", description = "Datos del pedido inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Pedido> createPedido(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del pedido a crear",
                required = true)
            @Valid @RequestBody Pedido pedido) {
        log.info("POST /api/pedidos - Creando pedido para clienteId={}", pedido.getClienteId());
        return new ResponseEntity<>(pedidoService.guardarPedido(pedido), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Actualizar un pedido",
        description = "Modifica los datos de un pedido existente identificado por su ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(
            @Parameter(description = "Identificador único del pedido a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Pedido pedidoActualizado) {
        log.info("PUT /api/pedidos/{} - Actualizando pedido", id);
        return ResponseEntity.ok(pedidoService.actualizarPedido(id, pedidoActualizado));
    }

    @Operation(
        summary = "Eliminar un pedido",
        description = "Elimina permanentemente un pedido del sistema según su ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(
            @Parameter(description = "Identificador único del pedido a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/pedidos/{} - Eliminando pedido", id);
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obtener pedidos con datos del cliente",
        description = "Retorna los pedidos de un cliente específico junto con su información personal, " +
                      "combinando datos de este microservicio con los de ms-clientes."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos con datos del cliente obtenidos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<RespuestaCombinadaDTO> getPedidosConCliente(
            @Parameter(description = "Identificador único del cliente", required = true, example = "1")
            @PathVariable Long clienteId) {
        log.info("GET /api/pedidos/cliente/{} - Obteniendo pedidos con datos de cliente", clienteId);
        return ResponseEntity.ok(pedidoService.obtenerPedidosConCliente(clienteId));
    }
}