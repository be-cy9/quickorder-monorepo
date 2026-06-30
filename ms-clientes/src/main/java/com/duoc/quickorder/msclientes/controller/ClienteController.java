package com.duoc.quickorder.msclientes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.msclientes.exception.ResourceNotFoundException;
import com.duoc.quickorder.msclientes.model.Cliente;
import com.duoc.quickorder.msclientes.repository.ClienteRepository;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    
    // GET - Listar todos los clientes
    @GetMapping
    public List<Cliente> getAll() {
        log.info("GET /api/clientes - Listando todos los clientes");
        List<Cliente> clientes = clienteRepository.findAll();
        log.info("Se encontraron {} clientes", clientes.size());
        return clientes;
    }
    
    // GET - Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        log.info("GET /api/clientes/{} - Buscando cliente por ID", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        log.info("Cliente encontrado: {}", cliente.getNombre());
        return ResponseEntity.ok(cliente);
    }
    
    // POST - Crear nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> create(@Valid @RequestBody Cliente cliente) {
        log.info("POST /api/clientes - Creando cliente: {}", cliente.getNombre());
        cliente.setFechaRegistro(LocalDateTime.now());
        Cliente nuevoCliente = clienteRepository.save(cliente);
        log.info("Cliente creado con ID: {}", nuevoCliente.getId());
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }
    
    // PUT - Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @Valid @RequestBody Cliente clienteActualizado) {
        log.info("PUT /api/clientes/{} - Actualizando cliente", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setEmail(clienteActualizado.getEmail());
        cliente.setTelefono(clienteActualizado.getTelefono());
        cliente.setPais(clienteActualizado.getPais());
        log.info("Cliente {} actualizado correctamente", id);
        return ResponseEntity.ok(clienteRepository.save(cliente));
    }
    
    // DELETE - Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Eliminando cliente", id);
        if (!clienteRepository.existsById(id)) {
            log.warn("Cliente con ID {} no encontrado para eliminar", id);
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
        log.info("Cliente {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }
}