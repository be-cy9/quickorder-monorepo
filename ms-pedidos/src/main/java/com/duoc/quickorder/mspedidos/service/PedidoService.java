package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

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
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public Pedido guardarPedido(Pedido pedido) {
        // Lógica de negocio: Validar cliente externo usando el ClienteService
        ClienteDTO cliente = clienteService.obtenerClientePorId(pedido.getClienteId());

        if (cliente == null || cliente.getId() == null) {
            log.warn("No se pudo validar el cliente con ID: {}", pedido.getClienteId());
            throw new RuntimeException("Cliente no válido con ID: " + pedido.getClienteId());
        }

        log.info("Cliente validado: {} - {}", cliente.getId(), cliente.getNombre());
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        log.info("Actualizando pedido con ID: {}", id);
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        pedidoExistente.setClienteId(pedidoActualizado.getClienteId());
        pedidoExistente.setDescripcion(pedidoActualizado.getDescripcion());
        pedidoExistente.setMonto(pedidoActualizado.getMonto());
        pedidoExistente.setEstado(pedidoActualizado.getEstado());
        return pedidoRepository.save(pedidoExistente);
    }

    public void eliminarPedido(Long id) {
        log.info("Eliminando pedido con ID: {}", id);
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public RespuestaCombinadaDTO obtenerPedidosConCliente(Long clienteId) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(clienteId);

        if (cliente == null) {
            log.warn("Cliente con ID {} no encontrado al buscar pedidos combinados", clienteId);
            throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
        }

        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return new RespuestaCombinadaDTO(cliente, pedidos);
    }
}