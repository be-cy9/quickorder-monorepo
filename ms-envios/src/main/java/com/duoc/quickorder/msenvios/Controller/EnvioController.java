package com.duoc.quickorder.msenvios.controller;
import com.duoc.quickorder.msenvios.model.Envio;
import com.duoc.quickorder.msenvios.repository.EnvioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {
    
    @Autowired
    private EnvioRepository envioRepository;
    
    @GetMapping
    public List<Envio> getAll() {
        return envioRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Envio> getById(@PathVariable Long id) {
        return envioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Envio> create(@Valid @RequestBody Envio envio) {
        envio.setFechaEnvio(LocalDateTime.now());
        return new ResponseEntity<>(envioRepository.save(envio), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Envio> update(@PathVariable Long id, @Valid @RequestBody Envio envioActualizado) {
        return envioRepository.findById(id)
                .map(envio -> {
                    envio.setPedidoId(envioActualizado.getPedidoId());
                    envio.setDireccion(envioActualizado.getDireccion());
                    envio.setEstado(envioActualizado.getEstado());
                    return ResponseEntity.ok(envioRepository.save(envio));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (envioRepository.existsById(id)) {
            envioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}