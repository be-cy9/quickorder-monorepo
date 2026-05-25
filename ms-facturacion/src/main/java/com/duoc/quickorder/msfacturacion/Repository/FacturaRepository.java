package com.duoc.quickorder.msfacturacion.repository;

import com.duoc.quickorder.msfacturacion.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByClienteId(Long clienteId);
}