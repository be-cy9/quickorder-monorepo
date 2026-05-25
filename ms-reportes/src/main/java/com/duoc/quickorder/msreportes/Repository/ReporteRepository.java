package com.duoc.quickorder.msreportes.repository;

import com.duoc.quickorder.msreportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByTipo(String tipo);
}