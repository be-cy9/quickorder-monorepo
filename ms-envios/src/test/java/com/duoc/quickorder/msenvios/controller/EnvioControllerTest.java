package com.duoc.quickorder.msenvios.controller;

import com.duoc.quickorder.msenvios.exception.ResourceNotFoundException;
import com.duoc.quickorder.msenvios.model.Envio;
import com.duoc.quickorder.msenvios.repository.EnvioRepository;
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
@DisplayName("Tests unitarios de EnvioController")
class EnvioControllerTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioController envioController;

    private Envio envioEjemplo;

    @BeforeEach
    void setUp() {
        envioEjemplo = new Envio();
        envioEjemplo.setId(1L);
        envioEjemplo.setPedidoId(10L);
        envioEjemplo.setEmailDestino("gamer@pro.com");
        envioEjemplo.setEstado("ENVIADO");
    }

    // =========================================================================
    // TESTS PARA getAll()
    // =========================================================================

    @Test
    @DisplayName("getAll - Camino Feliz: retorna lista de envíos")
    void getAll_debeRetornarListaDeEnvios() {
        // ARRANGE
        when(envioRepository.findAll()).thenReturn(Arrays.asList(envioEjemplo, new Envio()));

        // ACT
        List<Envio> resultado = envioController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(envioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Camino Alternativo: retorna lista vacía si no hay envíos")
    void getAll_debeRetornarListaVacia_cuandoNoHayEnvios() {
        // ARRANGE
        when(envioRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Envio> resultado = envioController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getById()
    // =========================================================================

    @Test
    @DisplayName("getById - Camino Feliz: retorna el envío si existe")
    void getById_debeRetornarEnvio_cuandoExiste() {
        // ARRANGE
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioEjemplo));

        // ACT
        ResponseEntity<Envio> respuesta = envioController.getById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("ENVIADO", respuesta.getBody().getEstado());
    }

    @Test
    @DisplayName("getById - Camino Error: lanza ResourceNotFoundException si no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> envioController.getById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // TESTS PARA create()
    // =========================================================================

    @Test
    @DisplayName("create - Camino Feliz: crea un envío y retorna 201 CREATED")
    void create_debeCrearEnvio_yRetornar201() {
        // ARRANGE
        when(envioRepository.save(any(Envio.class))).thenReturn(envioEjemplo);

        // ACT
        ResponseEntity<Envio> respuesta = envioController.create(envioEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("create - Camino Alternativo: el email de destino se persiste correctamente")
    void create_debeGuardarEmailDestino_correctamente() {
        // ARRANGE
        envioEjemplo.setEmailDestino("cliente@duoc.cl");
        when(envioRepository.save(any(Envio.class))).thenReturn(envioEjemplo);

        // ACT
        ResponseEntity<Envio> respuesta = envioController.create(envioEjemplo);

        // ASSERT
        assertEquals("cliente@duoc.cl", respuesta.getBody().getEmailDestino());
    }

    // =========================================================================
    // TESTS PARA update()
    // =========================================================================

    @Test
    @DisplayName("update - Camino Feliz: actualiza los datos del envío")
    void update_debeActualizarDatos_cuandoEnvioExiste() {
        // ARRANGE
        Envio datosActualizados = new Envio();
        datosActualizados.setPedidoId(20L);
        datosActualizados.setEmailDestino("nuevo@email.com");
        datosActualizados.setEstado("ENTREGADO");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioEjemplo));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ResponseEntity<Envio> respuesta = envioController.update(1L, datosActualizados);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("ENTREGADO", respuesta.getBody().getEstado());
        assertEquals("nuevo@email.com", respuesta.getBody().getEmailDestino());
    }

    @Test
    @DisplayName("update - Camino Error: lanza excepción si el envío a actualizar no existe")
    void update_debeLanzarExcepcion_cuandoEnvioNoExiste() {
        // ARRANGE
        when(envioRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> envioController.update(999L, envioEjemplo)
        );
        verify(envioRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA delete()
    // =========================================================================

    @Test
    @DisplayName("delete - Camino Feliz: elimina el envío y retorna 204 NO CONTENT")
    void delete_debeEliminar_yRetornar204() {
        // ARRANGE
        when(envioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = envioController.delete(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(envioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Camino Error: lanza excepción si el envío no existe")
    void delete_debeLanzarExcepcion_cuandoEnvioNoExiste() {
        // ARRANGE
        when(envioRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> envioController.delete(99L)
        );
        verify(envioRepository, never()).deleteById(anyLong());
    }
}
