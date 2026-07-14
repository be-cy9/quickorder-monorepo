package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.exception.BadRequestException;
import com.duoc.quickorder.mspedidos.exception.ResourceNotFoundException;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    // Estados válidos que puede tener un pedido en el sistema
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE", "PROCESADO", "ENVIADO", "ENTREGADO", "CANCELADO"
    );

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;

    // Inyección por constructor (buena práctica recomendada por Spring)
    public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
    }

    public List<Pedido> obtenerTodos() {
        log.info("Listando todos los pedidos");
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        log.info("Buscando pedido con ID: {}", id);
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    public Pedido guardarPedido(Pedido pedido) {
        // Regla de negocio 1: Validar que el estado sea uno de los permitidos
        if (!ESTADOS_VALIDOS.contains(pedido.getEstado().toUpperCase())) {
            log.warn("Intento de crear pedido con estado inválido: '{}'", pedido.getEstado());
            throw new BadRequestException(
                "Estado inválido: '" + pedido.getEstado() + "'. " +
                "Los estados permitidos son: " + ESTADOS_VALIDOS
            );
        }

        // Regla de negocio 2: Validar cliente externo usando el ClienteService
        ClienteDTO cliente = clienteService.obtenerClientePorId(pedido.getClienteId());

        if (cliente == null || cliente.getId() == null) {
            log.warn("No se pudo validar el cliente con ID: {}", pedido.getClienteId());
            throw new BadRequestException("Cliente no válido con ID: " + pedido.getClienteId());
        }

        log.info("Cliente validado: {} - {}", cliente.getId(), cliente.getNombre());
        pedido.setEstado(pedido.getEstado().toUpperCase()); // normalizar a mayúsculas
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        log.info("Actualizando pedido con ID: {}", id);

        // Validar estado antes de actualizar
        if (!ESTADOS_VALIDOS.contains(pedidoActualizado.getEstado().toUpperCase())) {
            log.warn("Intento de actualizar pedido {} con estado inválido: '{}'", id, pedidoActualizado.getEstado());
            throw new BadRequestException(
                "Estado inválido: '" + pedidoActualizado.getEstado() + "'. " +
                "Los estados permitidos son: " + ESTADOS_VALIDOS
            );
        }

        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        pedidoExistente.setClienteId(pedidoActualizado.getClienteId());
        pedidoExistente.setProductoId(pedidoActualizado.getProductoId());
        pedidoExistente.setDescripcion(pedidoActualizado.getDescripcion());
        pedidoExistente.setMonto(pedidoActualizado.getMonto());
        pedidoExistente.setEstado(pedidoActualizado.getEstado().toUpperCase());
        return pedidoRepository.save(pedidoExistente);
    }

    public void eliminarPedido(Long id) {
        log.info("Eliminando pedido con ID: {}", id);
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public RespuestaCombinadaDTO obtenerPedidosConCliente(Long clienteId) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(clienteId);

        if (cliente == null) {
            log.warn("Cliente con ID {} no encontrado al buscar pedidos combinados", clienteId);
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId);
        }

        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return new RespuestaCombinadaDTO(cliente, pedidos);
    }
}