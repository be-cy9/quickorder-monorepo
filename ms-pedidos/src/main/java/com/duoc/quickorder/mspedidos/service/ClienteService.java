package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class ClienteService {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    
    @Autowired
    private WebClient webClient;
    
    // Este método llama a ms-clientes para obtener los datos de un cliente por su ID
    public ClienteDTO obtenerClientePorId(Long clienteId) {
        log.info("Llamando a ms-clientes para obtener el cliente con ID: {}", clienteId);
        
        try {
            // Realizar la llamada HTTP a ms-clientes
            ClienteDTO cliente = webClient.get()
                    .uri("http://localhost:8082/api/clientes/{id}", clienteId)
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block(); // .block() convierte la respuesta asíncrona en síncrona
            
            log.info("Cliente obtenido: {} - {}", cliente.getId(), cliente.getNombre());
            return cliente;
            
        } catch (Exception e) {
            log.error("Error al obtener el cliente con ID {}: {}", clienteId, e.getMessage());
            // Si hay error, devolver un cliente vacío con datos por defecto
            ClienteDTO clientePorDefecto = new ClienteDTO();
            clientePorDefecto.setId(clienteId);
            clientePorDefecto.setNombre("Cliente no encontrado");
            clientePorDefecto.setEmail("no-disponible@mail.com");
            return clientePorDefecto;
        }
    }
}