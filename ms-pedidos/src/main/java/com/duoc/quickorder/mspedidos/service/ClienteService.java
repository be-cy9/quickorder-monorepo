package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class ClienteService {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    
    private final WebClient webClient;

    public ClienteService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Método principal: llama a ms-clientes y aplica fallback si falla.
     * La separación en dos métodos permite testear la lógica de fallback
     * sin necesidad de mockear la cadena fluent de WebClient.
     */
    public ClienteDTO obtenerClientePorId(Long clienteId) {
        log.info("Llamando a ms-clientes para obtener el cliente con ID: {}", clienteId);
        
        try {
            ClienteDTO cliente = llamarMsClientes(clienteId);
            log.info("Cliente obtenido: {} - {}", cliente.getId(), cliente.getNombre());
            return cliente;
            
        } catch (Exception e) {
            log.error("Error al obtener el cliente con ID {}: {}", clienteId, e.getMessage());
            // Patrón fallback graceful: si ms-clientes no está disponible,
            // devolvemos un cliente por defecto en lugar de propagar el error.
            ClienteDTO clientePorDefecto = new ClienteDTO();
            clientePorDefecto.setId(clienteId);
            clientePorDefecto.setNombre("Cliente no encontrado");
            clientePorDefecto.setEmail("no-disponible@mail.com");
            return clientePorDefecto;
        }
    }

    /**
     * Realiza la llamada HTTP real a ms-clientes.
     * Separado en método propio para facilitar las pruebas unitarias
     * (se mockea este método en lugar de toda la cadena WebClient).
     */
    protected ClienteDTO llamarMsClientes(Long clienteId) {
        return webClient.get()
                .uri("http://localhost:8082/api/clientes/{id}", clienteId)
                .retrieve()
                .bodyToMono(ClienteDTO.class)
                .block(); // .block() convierte la respuesta asíncrona en síncrona
    }
}