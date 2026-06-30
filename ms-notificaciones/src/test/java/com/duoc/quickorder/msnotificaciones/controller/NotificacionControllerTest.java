package com.duoc.quickorder.msnotificaciones.controller;

import com.duoc.quickorder.msnotificaciones.exception.ResourceNotFoundException;
import com.duoc.quickorder.msnotificaciones.model.Notificacion;
import com.duoc.quickorder.msnotificaciones.repository.NotificacionRepository;
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
@DisplayName("Tests unitarios de NotificacionController")
class NotificacionControllerTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionController notificacionController;

    private Notificacion notificacionEjemplo;

    @BeforeEach
    void setUp() {
        notificacionEjemplo = new Notificacion(1L, "Tu pedido fue enviado", "PEDIDO");
        notificacionEjemplo.setId(1L);
        notificacionEjemplo.setLeida(false);
    }

    // =========================================================================
    // TESTS PARA getAllNotificaciones()
    // =========================================================================

    @Test
    @DisplayName("getAllNotificaciones - Camino Feliz: retorna lista de notificaciones")
    void getAllNotificaciones_debeRetornarLista() {
        // ARRANGE
        when(notificacionRepository.findAll()).thenReturn(Arrays.asList(notificacionEjemplo, new Notificacion()));

        // ACT
        List<Notificacion> resultado = notificacionController.getAllNotificaciones();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllNotificaciones - Camino Alternativo: retorna lista vacía")
    void getAllNotificaciones_debeRetornarListaVacia() {
        // ARRANGE
        when(notificacionRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Notificacion> resultado = notificacionController.getAllNotificaciones();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getNotificacionById()
    // =========================================================================

    @Test
    @DisplayName("getNotificacionById - Camino Feliz: retorna notificación si existe")
    void getNotificacionById_debeRetornarNotificacion_cuandoExiste() {
        // ARRANGE
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));

        // ACT
        ResponseEntity<Notificacion> respuesta = notificacionController.getNotificacionById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Tu pedido fue enviado", respuesta.getBody().getMensaje());
    }

    @Test
    @DisplayName("getNotificacionById - Camino Error: lanza excepción si no existe")
    void getNotificacionById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> notificacionController.getNotificacionById(99L)
        );
    }

    // =========================================================================
    // TESTS PARA createNotificacion()
    // =========================================================================

    @Test
    @DisplayName("createNotificacion - Camino Feliz: crea notificación y retorna 201")
    void createNotificacion_debeCrear_yRetornar201() {
        // ARRANGE
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionEjemplo);

        // ACT
        ResponseEntity<Notificacion> respuesta = notificacionController.createNotificacion(notificacionEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("createNotificacion - Camino Alternativo: notificación se crea con leida=false")
    void createNotificacion_debeCrearConLeidaFalse() {
        // ARRANGE
        Notificacion nueva = new Notificacion(2L, "Bienvenido al sistema", "SISTEMA");
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(nueva);

        // ACT
        ResponseEntity<Notificacion> respuesta = notificacionController.createNotificacion(nueva);

        // ASSERT
        assertFalse(respuesta.getBody().getLeida(), "La notificación nueva debe estar no leída");
    }

    // =========================================================================
    // TESTS PARA marcarComoLeida()
    // =========================================================================

    @Test
    @DisplayName("marcarComoLeida - Camino Feliz: marca notificación como leída")
    void marcarComoLeida_debeCambiarEstadoALeida() {
        // ARRANGE
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> {
            Notificacion n = inv.getArgument(0);
            n.setLeida(true);
            return n;
        });

        // ACT
        ResponseEntity<Notificacion> respuesta = notificacionController.marcarComoLeida(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().getLeida(), "La notificación debe estar marcada como leída");
    }

    @Test
    @DisplayName("marcarComoLeida - Camino Error: lanza excepción si notificación no existe")
    void marcarComoLeida_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> notificacionController.marcarComoLeida(99L)
        );
    }

    // =========================================================================
    // TESTS PARA deleteNotificacion()
    // =========================================================================

    @Test
    @DisplayName("deleteNotificacion - Camino Feliz: elimina notificación y retorna 204")
    void deleteNotificacion_debeEliminar_yRetornar204() {
        // ARRANGE
        when(notificacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificacionRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = notificacionController.deleteNotificacion(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(notificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteNotificacion - Camino Error: lanza excepción si no existe")
    void deleteNotificacion_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(notificacionRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> notificacionController.deleteNotificacion(99L)
        );
        verify(notificacionRepository, never()).deleteById(anyLong());
    }
}
