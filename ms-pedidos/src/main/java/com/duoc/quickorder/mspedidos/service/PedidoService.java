package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteService clienteService;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardarPedido(Pedido pedido) {
        // Lógica de negocio: Validar cliente externo usando el ClienteService
        ClienteDTO cliente = clienteService.obtenerClientePorId(pedido.getClienteId());
        
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> actualizarPedido(Long id, Pedido pedidoActualizado) {
        return pedidoRepository.findById(id).map(pedidoExistente -> {
            pedidoExistente.setClienteId(pedidoActualizado.getClienteId());
            pedidoExistente.setDescripcion(pedidoActualizado.getDescripcion());
            pedidoExistente.setMonto(pedidoActualizado.getMonto());
            pedidoExistente.setEstado(pedidoActualizado.getEstado());
            return pedidoRepository.save(pedidoExistente);
        });
    }

    public boolean eliminarPedido(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public RespuestaCombinadaDTO obtenerPedidosConCliente(Long clienteId) {
        ClienteDTO cliente = clienteService.obtenerClientePorId(clienteId);
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return new RespuestaCombinadaDTO(cliente, pedidos);
    }
}