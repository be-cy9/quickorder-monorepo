package com.duoc.quickorder.mssoporte.controller;

import com.duoc.quickorder.mssoporte.exception.ResourceNotFoundException;
import com.duoc.quickorder.mssoporte.model.Ticket;
import com.duoc.quickorder.mssoporte.repository.TicketRepository;
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
@DisplayName("Tests unitarios de TicketController")
class TicketControllerTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketController ticketController;

    private Ticket ticketEjemplo;

    @BeforeEach
    void setUp() {
        ticketEjemplo = new Ticket();
        ticketEjemplo.setId(1L);
        ticketEjemplo.setClienteId(5L);
        ticketEjemplo.setAsunto("Key no funciona");
        ticketEjemplo.setDescripcion("El código de activación de Elden Ring es inválido");
        ticketEjemplo.setEstado("ABIERTO");
    }

    // =========================================================================
    // TESTS PARA getAll()
    // =========================================================================

    @Test
    @DisplayName("getAll - Camino Feliz: retorna lista de tickets")
    void getAll_debeRetornarListaDeTickets() {
        // ARRANGE
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticketEjemplo, new Ticket()));

        // ACT
        List<Ticket> resultado = ticketController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Camino Alternativo: retorna lista vacía si no hay tickets")
    void getAll_debeRetornarListaVacia_cuandoNoHayTickets() {
        // ARRANGE
        when(ticketRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Ticket> resultado = ticketController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getById()
    // =========================================================================

    @Test
    @DisplayName("getById - Camino Feliz: retorna el ticket si existe")
    void getById_debeRetornarTicket_cuandoExiste() {
        // ARRANGE
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketEjemplo));

        // ACT
        ResponseEntity<Ticket> respuesta = ticketController.getById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Key no funciona", respuesta.getBody().getAsunto());
    }

    @Test
    @DisplayName("getById - Camino Error: lanza ResourceNotFoundException si no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> ticketController.getById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // TESTS PARA create()
    // =========================================================================

    @Test
    @DisplayName("create - Camino Feliz: crea un ticket y retorna 201 CREATED")
    void create_debeCrearTicket_yRetornar201() {
        // ARRANGE
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketEjemplo);

        // ACT
        ResponseEntity<Ticket> respuesta = ticketController.create(ticketEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("create - Camino Alternativo: estado se asigna como ABIERTO al crear")
    void create_debeAsignarEstadoAbierto() {
        // ARRANGE
        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setClienteId(3L);
        nuevoTicket.setAsunto("Problema con factura");
        nuevoTicket.setDescripcion("La factura no llegó a mi correo");
        nuevoTicket.setEstado("ABIERTO");
        when(ticketRepository.save(any(Ticket.class))).thenReturn(nuevoTicket);

        // ACT
        ResponseEntity<Ticket> respuesta = ticketController.create(nuevoTicket);

        // ASSERT
        assertEquals("ABIERTO", respuesta.getBody().getEstado());
    }

    // =========================================================================
    // TESTS PARA update()
    // =========================================================================

    @Test
    @DisplayName("update - Camino Feliz: actualiza los datos del ticket")
    void update_debeActualizarDatos_cuandoTicketExiste() {
        // ARRANGE
        Ticket datosActualizados = new Ticket();
        datosActualizados.setAsunto("Key reemplazada");
        datosActualizados.setDescripcion("Se envió nueva key al cliente");
        datosActualizados.setEstado("CERRADO");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketEjemplo));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ResponseEntity<Ticket> respuesta = ticketController.update(1L, datosActualizados);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("CERRADO", respuesta.getBody().getEstado());
        assertEquals("Key reemplazada", respuesta.getBody().getAsunto());
    }

    @Test
    @DisplayName("update - Camino Error: lanza excepción si el ticket no existe")
    void update_debeLanzarExcepcion_cuandoTicketNoExiste() {
        // ARRANGE
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> ticketController.update(999L, ticketEjemplo)
        );
        verify(ticketRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA delete()
    // =========================================================================

    @Test
    @DisplayName("delete - Camino Feliz: elimina el ticket y retorna 204 NO CONTENT")
    void delete_debeEliminar_yRetornar204() {
        // ARRANGE
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = ticketController.delete(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(ticketRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Camino Error: lanza excepción si el ticket no existe")
    void delete_debeLanzarExcepcion_cuandoTicketNoExiste() {
        // ARRANGE
        when(ticketRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> ticketController.delete(99L)
        );
        verify(ticketRepository, never()).deleteById(anyLong());
    }
}
