package com.duoc.quickorder.msreportes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del reporte es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;
    
    @Column(length = 1000)
    private String filtros;
    
    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;
    
    public Reporte() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getFiltros() { return filtros; }
    public void setFiltros(String filtros) { this.filtros = filtros; }
    
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
}