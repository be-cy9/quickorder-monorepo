package com.duoc.quickorder.msreportes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.msreportes.exception.ResourceNotFoundException;
import com.duoc.quickorder.msreportes.model.Reporte;
import com.duoc.quickorder.msreportes.repository.ReporteRepository;
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
@RequestMapping("/api/reportes")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    
    private final ReporteRepository reporteRepository;

    public ReporteController(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    
    @GetMapping
    public List<Reporte> getAll() {
        return reporteRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> getById(@PathVariable Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        return ResponseEntity.ok(reporte);
    }
    
    @PostMapping
    public ResponseEntity<Reporte> create(@Valid @RequestBody Reporte reporte) {
        reporte.setFechaGeneracion(LocalDateTime.now());
        Reporte nuevoReporte = reporteRepository.save(reporte);
        return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> update(@PathVariable Long id, @Valid @RequestBody Reporte reporteActualizado) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        reporte.setNombre(reporteActualizado.getNombre());
        reporte.setTipo(reporteActualizado.getTipo());
        reporte.setFiltros(reporteActualizado.getFiltros());
        return ResponseEntity.ok(reporteRepository.save(reporte));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reporte no encontrado con ID: " + id);
        }
        reporteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<Reporte> getByTipo(@PathVariable String tipo) {
        return reporteRepository.findByTipo(tipo);
    }
}