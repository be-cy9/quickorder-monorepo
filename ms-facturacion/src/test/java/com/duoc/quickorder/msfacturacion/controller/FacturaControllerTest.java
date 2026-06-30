package com.duoc.quickorder.msfacturacion.controller;

import com.duoc.quickorder.msfacturacion.exception.ResourceNotFoundException;
import com.duoc.quickorder.msfacturacion.model.Factura;
import com.duoc.quickorder.msfacturacion.repository.FacturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de FacturaController")
class FacturaControllerTest {

    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private FacturaController facturaController;

    private Factura facturaEjemplo;

    @BeforeEach
    void setUp() {
        facturaEjemplo = new Factura();
        facturaEjemplo.setId(1L);
        facturaEjemplo.setPedidoId(10L);
        facturaEjemplo.setClienteId(5L);
        facturaEjemplo.setMonto(BigDecimal.valueOf(45000));
        facturaEjemplo.setEstado("EMITIDA");
    }

    // =========================================================================
    // TESTS PARA getAll()
    // =========================================================================

    @Test
    @DisplayName("getAll - Camino Feliz: retorna lista de facturas")
    void getAll_debeRetornarListaDeFacturas() {
        // ARRANGE
        when(facturaRepository.findAll()).thenReturn(Arrays.asList(facturaEjemplo, new Factura()));

        // ACT
        List<Factura> resultado = facturaController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(facturaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Camino Alternativo: retorna lista vacía si no hay facturas")
    void getAll_debeRetornarListaVacia_cuandoNoHayFacturas() {
        // ARRANGE
        when(facturaRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Factura> resultado = facturaController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getById()
    // =========================================================================

    @Test
    @DisplayName("getById - Camino Feliz: retorna la factura si existe")
    void getById_debeRetornarFactura_cuandoExiste() {
        // ARRANGE
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));

        // ACT
        ResponseEntity<Factura> respuesta = facturaController.getById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("EMITIDA", respuesta.getBody().getEstado());
    }

    @Test
    @DisplayName("getById - Camino Error: lanza ResourceNotFoundException si no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> facturaController.getById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // TESTS PARA create()
    // =========================================================================

    @Test
    @DisplayName("create - Camino Feliz: crea una factura y retorna 201 CREATED")
    void create_debeCrearFactura_yRetornar201() {
        // ARRANGE
        when(facturaRepository.save(any(Factura.class))).thenReturn(facturaEjemplo);

        // ACT
        ResponseEntity<Factura> respuesta = facturaController.create(facturaEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(facturaRepository, times(1)).save(any(Factura.class));
    }

    @Test
    @DisplayName("create - Camino Alternativo: el monto se persiste correctamente")
    void create_debeGuardarMontoCorrecto() {
        // ARRANGE
        facturaEjemplo.setMonto(BigDecimal.valueOf(99000));
        when(facturaRepository.save(any(Factura.class))).thenReturn(facturaEjemplo);

        // ACT
        ResponseEntity<Factura> respuesta = facturaController.create(facturaEjemplo);

        // ASSERT
        assertEquals(BigDecimal.valueOf(99000), respuesta.getBody().getMonto());
    }

    // =========================================================================
    // TESTS PARA update()
    // =========================================================================

    @Test
    @DisplayName("update - Camino Feliz: actualiza los datos de la factura")
    void update_debeActualizarDatos_cuandoFacturaExiste() {
        // ARRANGE
        Factura datosActualizados = new Factura();
        datosActualizados.setPedidoId(20L);
        datosActualizados.setClienteId(8L);
        datosActualizados.setMonto(BigDecimal.valueOf(70000));
        datosActualizados.setEstado("PAGADA");

        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaEjemplo));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ResponseEntity<Factura> respuesta = facturaController.update(1L, datosActualizados);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("PAGADA", respuesta.getBody().getEstado());
        assertEquals(BigDecimal.valueOf(70000), respuesta.getBody().getMonto());
    }

    @Test
    @DisplayName("update - Camino Error: lanza excepción si la factura a actualizar no existe")
    void update_debeLanzarExcepcion_cuandoFacturaNoExiste() {
        // ARRANGE
        when(facturaRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> facturaController.update(999L, facturaEjemplo)
        );
        verify(facturaRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA delete()
    // =========================================================================

    @Test
    @DisplayName("delete - Camino Feliz: elimina la factura y retorna 204 NO CONTENT")
    void delete_debeEliminar_yRetornar204() {
        // ARRANGE
        when(facturaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(facturaRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = facturaController.delete(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(facturaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Camino Error: lanza excepción si la factura no existe")
    void delete_debeLanzarExcepcion_cuandoFacturaNoExiste() {
        // ARRANGE
        when(facturaRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> facturaController.delete(99L)
        );
        verify(facturaRepository, never()).deleteById(anyLong());
    }
}
