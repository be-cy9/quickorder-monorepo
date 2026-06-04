package com.duoc.quickorder.mspedidos.dto;

import com.duoc.quickorder.mspedidos.model.Pedido;
import java.util.List;

public class RespuestaCombinadaDTO {
    private ClienteDTO cliente;
    private List<Pedido> pedidos;
    
    public RespuestaCombinadaDTO() {}

    public RespuestaCombinadaDTO(ClienteDTO cliente, List<Pedido> pedidos) {
        this.cliente = cliente;
        this.pedidos = pedidos;
    }
    
    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }
}