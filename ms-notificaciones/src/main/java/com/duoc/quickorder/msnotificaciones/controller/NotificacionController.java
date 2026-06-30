package com.duoc.quickorder.msnotificaciones.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.msnotificaciones.exception.ResourceNotFoundException;
import com.duoc.quickorder.msnotificaciones.model.Notificacion;
import com.duoc.quickorder.msnotificaciones.repository.NotificacionRepository;
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
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);

    
    private final NotificacionRepository notificacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    
    @GetMapping
    public List<Notificacion> getAllNotificaciones() {
        return notificacionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getNotificacionById(@PathVariable Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrada con ID: " + id));
        return ResponseEntity.ok(notificacion);
    }
    
    @PostMapping
    public ResponseEntity<Notificacion> createNotificacion(@Valid @RequestBody Notificacion notificacion) {
        notificacion.setFechaEnvio(java.time.LocalDateTime.now());
        notificacion.setLeida(false);
        Notificacion nuevaNotificacion = notificacionRepository.save(notificacion);
        return new ResponseEntity<>(nuevaNotificacion, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrada con ID: " + id));
        notificacion.setLeida(true);
        return ResponseEntity.ok(notificacionRepository.save(notificacion));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notificacion no encontrada con ID: " + id);
        }
        notificacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public List<Notificacion> getNotificacionesByUsuario(@PathVariable Long usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId);
    }
    
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public List<Notificacion> getNotificacionesNoLeidasByUsuario(@PathVariable Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId);
    }
}