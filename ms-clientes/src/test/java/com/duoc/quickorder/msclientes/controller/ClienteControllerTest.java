package com.duoc.quickorder.msclientes.controller;

import com.duoc.quickorder.msclientes.exception.ResourceNotFoundException;
import com.duoc.quickorder.msclientes.model.Cliente;
import com.duoc.quickorder.msclientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteController.
 *
 * NOTA: En ms-clientes el Controller inyecta directamente el Repository
 * (sin capa de servicio intermedia). Por eso mockeamos el Repository
 * y testeamos el Controller en vez del Service.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de ClienteController")
class ClienteControllerTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente clienteEjemplo;

    @BeforeEach
    void setUp() {
        clienteEjemplo = new Cliente("Juan Pérez", "juan@test.com", "912345678", "Chile");
        clienteEjemplo.setId(1L);
    }

    // =========================================================================
    // TESTS PARA getAll()
    // =========================================================================

    @Test
    @DisplayName("getAll - Camino Feliz: retorna lista de clientes")
    void getAll_debeRetornarListaDeClientes() {
        // ARRANGE
        List<Cliente> clientes = Arrays.asList(clienteEjemplo, new Cliente("Ana López", "ana@test.com", "987654321", "Perú"));
        when(clienteRepository.findAll()).thenReturn(clientes);

        // ACT
        List<Cliente> resultado = clienteController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Camino Alternativo: retorna lista vacía si no hay clientes")
    void getAll_debeRetornarListaVacia_cuandoNoHayClientes() {
        // ARRANGE
        when(clienteRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Cliente> resultado = clienteController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getById()
    // =========================================================================

    @Test
    @DisplayName("getById - Camino Feliz: retorna el cliente si existe")
    void getById_debeRetornarCliente_cuandoExiste() {
        // ARRANGE
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEjemplo));

        // ACT
        ResponseEntity<Cliente> respuesta = clienteController.getById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Juan Pérez", respuesta.getBody().getNombre());
        assertEquals("juan@test.com", respuesta.getBody().getEmail());
    }

    @Test
    @DisplayName("getById - Camino Error: lanza ResourceNotFoundException si no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE: findById devuelve vacío (ID no existe)
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> clienteController.getById(99L)
        );
        assertTrue(ex.getMessage().contains("99"), "El mensaje debe mencionar el ID buscado");
    }

    // =========================================================================
    // TESTS PARA create()
    // =========================================================================

    @Test
    @DisplayName("create - Camino Feliz: crea un cliente y retorna 201 CREATED")
    void create_debeCrearCliente_yRetornar201() {
        // ARRANGE: el repository guarda y devuelve el mismo cliente
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEjemplo);

        // ACT
        ResponseEntity<Cliente> respuesta = clienteController.create(clienteEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode(), "Debe retornar HTTP 201");
        assertNotNull(respuesta.getBody());
        assertEquals("Juan Pérez", respuesta.getBody().getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("create - Camino Alternativo: la fecha de registro se asigna automáticamente")
    void create_debeAsignarFechaRegistro_automaticamente() {
        // ARRANGE: simular que save() devuelve un cliente con fecha asignada
        Cliente clienteConFecha = new Cliente("Ana López", "ana@test.com", "987654321", "Perú");
        clienteConFecha.setFechaRegistro(LocalDateTime.now());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteConFecha);

        // ACT
        ResponseEntity<Cliente> respuesta = clienteController.create(clienteConFecha);

        // ASSERT
        assertNotNull(respuesta.getBody().getFechaRegistro(), "La fecha debe estar asignada");
    }

    // =========================================================================
    // TESTS PARA update()
    // =========================================================================

    @Test
    @DisplayName("update - Camino Feliz: actualiza los datos del cliente")
    void update_debeActualizarDatos_cuandoClienteExiste() {
        // ARRANGE
        Cliente datosActualizados = new Cliente("Juan Actualizado", "juanv2@test.com", "999999999", "Argentina");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEjemplo));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ResponseEntity<Cliente> respuesta = clienteController.update(1L, datosActualizados);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Juan Actualizado", respuesta.getBody().getNombre());
        assertEquals("juanv2@test.com", respuesta.getBody().getEmail());
        assertEquals("Argentina", respuesta.getBody().getPais());
    }

    @Test
    @DisplayName("update - Camino Error: lanza excepción si el cliente a actualizar no existe")
    void update_debeLanzarExcepcion_cuandoClienteNoExiste() {
        // ARRANGE
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> clienteController.update(999L, clienteEjemplo)
        );
        verify(clienteRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA delete()
    // =========================================================================

    @Test
    @DisplayName("delete - Camino Feliz: elimina el cliente y retorna 204 NO CONTENT")
    void delete_debeEliminar_yRetornar204() {
        // ARRANGE
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = clienteController.delete(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode(), "Debe retornar HTTP 204");
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Camino Error: lanza excepción si el cliente no existe")
    void delete_debeLanzarExcepcion_cuandoClienteNoExiste() {
        // ARRANGE
        when(clienteRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> clienteController.delete(99L)
        );
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
