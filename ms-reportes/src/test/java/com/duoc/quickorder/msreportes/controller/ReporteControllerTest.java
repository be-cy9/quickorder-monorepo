package com.duoc.quickorder.msreportes.controller;

import com.duoc.quickorder.msreportes.exception.ResourceNotFoundException;
import com.duoc.quickorder.msreportes.model.Reporte;
import com.duoc.quickorder.msreportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de ReporteController")
class ReporteControllerTest {

    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteController reporteController;

    private Reporte reporteEjemplo;

    @BeforeEach
    void setUp() {
        reporteEjemplo = new Reporte();
        reporteEjemplo.setId(1L);
        reporteEjemplo.setNombre("Ventas Mensuales");
        reporteEjemplo.setTipo("VENTAS");
        reporteEjemplo.setFiltros("{\"mes\":\"junio\",\"anio\":\"2026\"}");
    }

    // =========================================================================
    // TESTS PARA getAll()
    // =========================================================================

    @Test
    @DisplayName("getAll - Camino Feliz: retorna lista de reportes")
    void getAll_debeRetornarListaDeReportes() {
        // ARRANGE
        when(reporteRepository.findAll()).thenReturn(Arrays.asList(reporteEjemplo, new Reporte()));

        // ACT
        List<Reporte> resultado = reporteController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reporteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Camino Alternativo: retorna lista vacía si no hay reportes")
    void getAll_debeRetornarListaVacia_cuandoNoHayReportes() {
        // ARRANGE
        when(reporteRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Reporte> resultado = reporteController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getById()
    // =========================================================================

    @Test
    @DisplayName("getById - Camino Feliz: retorna el reporte si existe")
    void getById_debeRetornarReporte_cuandoExiste() {
        // ARRANGE
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));

        // ACT
        ResponseEntity<Reporte> respuesta = reporteController.getById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Ventas Mensuales", respuesta.getBody().getNombre());
    }

    @Test
    @DisplayName("getById - Camino Error: lanza ResourceNotFoundException si no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> reporteController.getById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // TESTS PARA create()
    // =========================================================================

    @Test
    @DisplayName("create - Camino Feliz: crea un reporte y retorna 201 CREATED")
    void create_debeCrearReporte_yRetornar201() {
        // ARRANGE
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteEjemplo);

        // ACT
        ResponseEntity<Reporte> respuesta = reporteController.create(reporteEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    @DisplayName("create - Camino Alternativo: tipo del reporte se persiste correctamente")
    void create_debeGuardarTipoCorrecto() {
        // ARRANGE
        reporteEjemplo.setTipo("INVENTARIO");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteEjemplo);

        // ACT
        ResponseEntity<Reporte> respuesta = reporteController.create(reporteEjemplo);

        // ASSERT
        assertEquals("INVENTARIO", respuesta.getBody().getTipo());
    }

    // =========================================================================
    // TESTS PARA update()
    // =========================================================================

    @Test
    @DisplayName("update - Camino Feliz: actualiza los datos del reporte")
    void update_debeActualizarDatos_cuandoReporteExiste() {
        // ARRANGE
        Reporte datosActualizados = new Reporte();
        datosActualizados.setNombre("Ventas Anuales");
        datosActualizados.setTipo("ANUAL");
        datosActualizados.setFiltros("{\"anio\":\"2026\"}");

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ResponseEntity<Reporte> respuesta = reporteController.update(1L, datosActualizados);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Ventas Anuales", respuesta.getBody().getNombre());
        assertEquals("ANUAL", respuesta.getBody().getTipo());
    }

    @Test
    @DisplayName("update - Camino Error: lanza excepción si el reporte no existe")
    void update_debeLanzarExcepcion_cuandoReporteNoExiste() {
        // ARRANGE
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> reporteController.update(999L, reporteEjemplo)
        );
        verify(reporteRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA delete()
    // =========================================================================

    @Test
    @DisplayName("delete - Camino Feliz: elimina el reporte y retorna 204 NO CONTENT")
    void delete_debeEliminar_yRetornar204() {
        // ARRANGE
        when(reporteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reporteRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = reporteController.delete(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(reporteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Camino Error: lanza excepción si el reporte no existe")
    void delete_debeLanzarExcepcion_cuandoReporteNoExiste() {
        // ARRANGE
        when(reporteRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> reporteController.delete(99L)
        );
        verify(reporteRepository, never()).deleteById(anyLong());
    }
}
