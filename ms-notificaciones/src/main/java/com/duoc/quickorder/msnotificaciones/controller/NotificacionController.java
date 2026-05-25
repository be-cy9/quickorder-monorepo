package com.duoc.quickorder.msnotificaciones.controller;

import com.duoc.quickorder.msnotificaciones.model.Notificacion;
import com.duoc.quickorder.msnotificaciones.repository.NotificacionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @GetMapping
    public List<Notificacion> getAllNotificaciones() {
        return notificacionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getNotificacionById(@PathVariable Long id) {
        return notificacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.setLeida(true);
                    return ResponseEntity.ok(notificacionRepository.save(notificacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        if (notificacionRepository.existsById(id)) {
            notificacionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
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