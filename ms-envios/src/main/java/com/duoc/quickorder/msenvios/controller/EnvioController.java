package com.duoc.quickorder.msenvios.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.msenvios.exception.ResourceNotFoundException;
import com.duoc.quickorder.msenvios.model.Envio;
import com.duoc.quickorder.msenvios.repository.EnvioRepository;
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
@RequestMapping("/api/envios")
public class EnvioController {

    private static final Logger log = LoggerFactory.getLogger(EnvioController.class);

    
    private final EnvioRepository envioRepository;

    public EnvioController(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    
    @GetMapping
    public List<Envio> getAll() {
        return envioRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Envio> getById(@PathVariable Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado con ID: " + id));
        return ResponseEntity.ok(envio);
    }
    
    @PostMapping
    public ResponseEntity<Envio> create(@Valid @RequestBody Envio envio) {
        envio.setFechaEnvio(LocalDateTime.now());
        return new ResponseEntity<>(envioRepository.save(envio), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Envio> update(@PathVariable Long id, @Valid @RequestBody Envio envioActualizado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado con ID: " + id));
        envio.setPedidoId(envioActualizado.getPedidoId());
        envio.setEmailDestino(envioActualizado.getEmailDestino());
        envio.setEstado(envioActualizado.getEstado());
        return ResponseEntity.ok(envioRepository.save(envio));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!envioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Envio no encontrado con ID: " + id);
        }
        envioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}