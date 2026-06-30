package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import com.duoc.quickorder.mspedidos.dto.RespuestaCombinadaDTO;
import com.duoc.quickorder.mspedidos.exception.BadRequestException;
import com.duoc.quickorder.mspedidos.exception.ResourceNotFoundException;
import com.duoc.quickorder.mspedidos.model.Pedido;
import com.duoc.quickorder.mspedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PedidoService.
 *
 * CONCEPTOS CLAVE:
 * - @ExtendWith(MockitoExtension.class): activa Mockito para esta clase de tests
 * - @Mock: crea un "doble de actuación" del objeto real — no toca la BD ni llama servicios reales
 * - @InjectMocks: crea la instancia real del servicio e inyecta los @Mock automáticamente
 * - @BeforeEach: se ejecuta antes de CADA test para reiniciar el estado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de PedidoService")
class PedidoServiceTest {

    // MOCKS: objetos simulados. No tocan la base de datos ni llaman servicios externos.
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteService clienteService;

    // OBJETO REAL bajo prueba — Mockito inyecta los @Mock de arriba en su constructor
    @InjectMocks
    private PedidoService pedidoService;

    // Datos de prueba reutilizables
    private Pedido pedidoEjemplo;
    private ClienteDTO clienteEjemplo;

    @BeforeEach
    void setUp() {
        // Construir un pedido de prueba
        pedidoEjemplo = new Pedido(1L, 10L, "Pedido de prueba", BigDecimal.valueOf(5000), "PENDIENTE");
        pedidoEjemplo.setId(1L);

        // Construir un cliente de prueba
        clienteEjemplo = new ClienteDTO();
        clienteEjemplo.setId(1L);
        clienteEjemplo.setNombre("Juan Pérez");
        clienteEjemplo.setEmail("juan@test.com");
    }

    // =========================================================================
    // TESTS PARA obtenerTodos()
    // =========================================================================

    @Test
    @DisplayName("obtenerTodos - Camino Feliz: devuelve lista de pedidos")
    void obtenerTodos_debeRetornarListaDePedidos() {
        // ARRANGE: le decimos al mock qué devolver cuando se llame findAll()
        List<Pedido> pedidosEsperados = Arrays.asList(pedidoEjemplo, new Pedido());
        when(pedidoRepository.findAll()).thenReturn(pedidosEsperados);

        // ACT: llamamos al método real del servicio
        List<Pedido> resultado = pedidoService.obtenerTodos();

        // ASSERT: verificamos que el resultado es correcto
        assertNotNull(resultado, "La lista no debe ser nula");
        assertEquals(2, resultado.size(), "Debe haber 2 pedidos");
        verify(pedidoRepository, times(1)).findAll(); // verificamos que se llamó exactamente 1 vez
    }

    @Test
    @DisplayName("obtenerTodos - Camino Alternativo: devuelve lista vacía")
    void obtenerTodos_debeRetornarListaVacia_cuandoNoHayPedidos() {
        // ARRANGE
        when(pedidoRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Pedido> resultado = pedidoService.obtenerTodos();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "La lista debe estar vacía");
    }

    // =========================================================================
    // TESTS PARA obtenerPorId()
    // =========================================================================

    @Test
    @DisplayName("obtenerPorId - Camino Feliz: devuelve el pedido cuando existe")
    void obtenerPorId_debeRetornarPedido_cuandoExiste() {
        // ARRANGE: el mock devuelve un pedido cuando se busca por ID 1
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));

        // ACT
        Pedido resultado = pedidoService.obtenerPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    @DisplayName("obtenerPorId - Camino Error: lanza ResourceNotFoundException cuando no existe")
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE: el mock devuelve vacío (el ID no existe)
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT: assertThrows verifica que el método lanza la excepción esperada
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> pedidoService.obtenerPorId(99L),
            "Debe lanzar ResourceNotFoundException"
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // TESTS PARA guardarPedido()
    // =========================================================================

    @Test
    @DisplayName("guardarPedido - Camino Feliz: guarda el pedido cuando el cliente es válido")
    void guardarPedido_debeGuardar_cuandoClienteEsValido() {
        // ARRANGE: el clienteService devuelve un cliente válido
        when(clienteService.obtenerClientePorId(1L)).thenReturn(clienteEjemplo);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);

        // ACT
        Pedido resultado = pedidoService.guardarPedido(pedidoEjemplo);

        // ASSERT
        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("guardarPedido - Camino Error: lanza BadRequestException cuando cliente es inválido")
    void guardarPedido_debeLanzarExcepcion_cuandoClienteEsInvalido() {
        // ARRANGE: clienteService devuelve un cliente vacío (sin ID)
        ClienteDTO clienteVacio = new ClienteDTO(); // id = null
        when(clienteService.obtenerClientePorId(anyLong())).thenReturn(clienteVacio);

        // ASSERT + ACT
        assertThrows(
            BadRequestException.class,
            () -> pedidoService.guardarPedido(pedidoEjemplo),
            "Debe lanzar BadRequestException por cliente inválido"
        );
        // Verificamos que NUNCA se llamó save() — el pedido no se guardó
        verify(pedidoRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA actualizarPedido()
    // =========================================================================

    @Test
    @DisplayName("actualizarPedido - Camino Feliz: actualiza los datos del pedido")
    void actualizarPedido_debeActualizarDatos_cuandoPedidoExiste() {
        // ARRANGE
        Pedido pedidoActualizado = new Pedido(1L, 20L, "Descripción nueva", BigDecimal.valueOf(9000), "PROCESADO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Pedido resultado = pedidoService.actualizarPedido(1L, pedidoActualizado);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("PROCESADO", resultado.getEstado());
        assertEquals("Descripción nueva", resultado.getDescripcion());
        assertEquals(BigDecimal.valueOf(9000), resultado.getMonto());
    }

    @Test
    @DisplayName("actualizarPedido - Camino Error: lanza ResourceNotFoundException si no existe")
    void actualizarPedido_debeLanzarExcepcion_cuandoPedidoNoExiste() {
        // ARRANGE
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> pedidoService.actualizarPedido(999L, pedidoEjemplo)
        );
        verify(pedidoRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA eliminarPedido()
    // =========================================================================

    @Test
    @DisplayName("eliminarPedido - Camino Feliz: elimina el pedido cuando existe")
    void eliminarPedido_debeEliminar_cuandoExiste() {
        // ARRANGE
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        // ACT
        pedidoService.eliminarPedido(1L);

        // ASSERT: verificamos que deleteById fue invocado exactamente una vez con el ID correcto
        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarPedido - Camino Error: lanza ResourceNotFoundException si no existe")
    void eliminarPedido_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> pedidoService.eliminarPedido(99L)
        );
        verify(pedidoRepository, never()).deleteById(anyLong());
    }

    // =========================================================================
    // TESTS PARA obtenerPedidosConCliente()
    // =========================================================================

    @Test
    @DisplayName("obtenerPedidosConCliente - Camino Feliz: devuelve DTO combinado")
    void obtenerPedidosConCliente_debeRetornarDTOCombinado_cuandoClienteExiste() {
        // ARRANGE
        when(clienteService.obtenerClientePorId(1L)).thenReturn(clienteEjemplo);
        when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedidoEjemplo));

        // ACT
        RespuestaCombinadaDTO resultado = pedidoService.obtenerPedidosConCliente(1L);

        // ASSERT
        assertNotNull(resultado);
        assertNotNull(resultado.getCliente());
        assertFalse(resultado.getPedidos().isEmpty());
        assertEquals("Juan Pérez", resultado.getCliente().getNombre());
    }

    @Test
    @DisplayName("obtenerPedidosConCliente - Camino Error: lanza excepción si cliente es null")
    void obtenerPedidosConCliente_debeLanzarExcepcion_cuandoClienteEsNull() {
        // ARRANGE: clienteService devuelve null (cliente no encontrado)
        when(clienteService.obtenerClientePorId(999L)).thenReturn(null);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> pedidoService.obtenerPedidosConCliente(999L)
        );
    }
}
