package com.duoc.quickorder.mspagos.controller;

import com.duoc.quickorder.mspagos.exception.ResourceNotFoundException;
import com.duoc.quickorder.mspagos.model.Pago;
import com.duoc.quickorder.mspagos.repository.PagoRepository;
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
@DisplayName("Tests unitarios de PagoController")
class PagoControllerTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoController pagoController;

    private Pago pagoEjemplo;

    @BeforeEach
    void setUp() {
        pagoEjemplo = new Pago();
        pagoEjemplo.setId(1L);
        pagoEjemplo.setPedidoId(10L);
        pagoEjemplo.setMonto(BigDecimal.valueOf(15000));
        pagoEjemplo.setMetodoPago("DEBITO");
        pagoEjemplo.setEstado("APROBADO");
    }

    @Test
    @DisplayName("getAllPagos - Camino Feliz: retorna lista de pagos")
    void getAllPagos_debeRetornarLista() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pagoEjemplo, new Pago()));
        List<Pago> resultado = pagoController.getAllPagos();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("getAllPagos - Camino Alternativo: retorna lista vacía")
    void getAllPagos_debeRetornarListaVacia() {
        when(pagoRepository.findAll()).thenReturn(List.of());
        List<Pago> resultado = pagoController.getAllPagos();
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("getPagoById - Camino Feliz: retorna pago cuando existe")
    void getPagoById_debeRetornarPago_cuandoExiste() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoEjemplo));
        ResponseEntity<Pago> respuesta = pagoController.getPagoById(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("APROBADO", respuesta.getBody().getEstado());
    }

    @Test
    @DisplayName("getPagoById - Camino Error: lanza excepción cuando no existe")
    void getPagoById_debeLanzarExcepcion_cuandoNoExiste() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> pagoController.getPagoById(99L));
    }

    @Test
    @DisplayName("createPago - Camino Feliz: crea pago y retorna 201")
    void createPago_debeCrear_yRetornar201() {
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);
        ResponseEntity<Pago> respuesta = pagoController.createPago(pagoEjemplo);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
    }

    @Test
    @DisplayName("createPago - Camino Alternativo: monto se persiste correctamente")
    void createPago_debeGuardarMontoCorrecto() {
        pagoEjemplo.setMonto(BigDecimal.valueOf(25000));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);
        ResponseEntity<Pago> respuesta = pagoController.createPago(pagoEjemplo);
        assertEquals(BigDecimal.valueOf(25000), respuesta.getBody().getMonto());
    }

    @Test
    @DisplayName("updatePago - Camino Feliz: actualiza estado del pago")
    void updatePago_debeActualizar_cuandoExiste() {
        Pago actualizado = new Pago();
        actualizado.setEstado("RECHAZADO");
        actualizado.setMetodoPago("CREDITO");
        actualizado.setMonto(BigDecimal.valueOf(5000));
        actualizado.setPedidoId(10L);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoEjemplo));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        ResponseEntity<Pago> respuesta = pagoController.updatePago(1L, actualizado);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("RECHAZADO", respuesta.getBody().getEstado());
    }

    @Test
    @DisplayName("updatePago - Camino Error: lanza excepción si pago no existe")
    void updatePago_debeLanzarExcepcion_cuandoNoExiste() {
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
            () -> pagoController.updatePago(999L, pagoEjemplo));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deletePago - Camino Feliz: elimina pago y retorna 204")
    void deletePago_debeEliminar_yRetornar204() {
        when(pagoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pagoRepository).deleteById(1L);
        ResponseEntity<Void> respuesta = pagoController.deletePago(1L);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    @Test
    @DisplayName("deletePago - Camino Error: lanza excepción si pago no existe")
    void deletePago_debeLanzarExcepcion_cuandoNoExiste() {
        when(pagoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> pagoController.deletePago(99L));
        verify(pagoRepository, never()).deleteById(anyLong());
    }
}
