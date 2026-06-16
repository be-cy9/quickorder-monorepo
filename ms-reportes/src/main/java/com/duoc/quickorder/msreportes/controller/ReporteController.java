package com.duoc.quickorder.msreportes.controller;

import com.duoc.quickorder.msreportes.model.Reporte;
import com.duoc.quickorder.msreportes.repository.ReporteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    
    @Autowired
    private ReporteRepository reporteRepository;
    
    @GetMapping
    public List<Reporte> getAll() {
        return reporteRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> getById(@PathVariable Long id) {
        return reporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Reporte> create(@Valid @RequestBody Reporte reporte) {
        reporte.setFechaGeneracion(LocalDateTime.now());
        Reporte nuevoReporte = reporteRepository.save(reporte);
        return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> update(@PathVariable Long id, @Valid @RequestBody Reporte reporteActualizado) {
        return reporteRepository.findById(id)
                .map(reporte -> {
                    reporte.setNombre(reporteActualizado.getNombre());
                    reporte.setTipo(reporteActualizado.getTipo());
                    reporte.setFiltros(reporteActualizado.getFiltros());
                    return ResponseEntity.ok(reporteRepository.save(reporte));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (reporteRepository.existsById(id)) {
            reporteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<Reporte> getByTipo(@PathVariable String tipo) {
        return reporteRepository.findByTipo(tipo);
    }
}