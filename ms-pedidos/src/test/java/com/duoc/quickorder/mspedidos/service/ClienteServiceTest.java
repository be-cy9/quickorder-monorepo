package com.duoc.quickorder.mspedidos.service;

import com.duoc.quickorder.mspedidos.dto.ClienteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteService.
 *
 * ESTRATEGIA DE TEST:
 * El problema de mockear WebClient directamente es que su API fluent
 * usa generics con wildcards (?) que Mockito no puede resolver.
 *
 * SOLUCIÓN: usamos @Spy sobre ClienteService para mockear únicamente
 * el método protegido llamarMsClientes(), que es donde ocurre la
 * llamada HTTP real. Así probamos la lógica de negocio (fallback,
 * logging, retorno) sin necesitar mockear toda la cadena de WebClient.
 *
 * Conceptos clave:
 * - @Spy: crea un objeto REAL pero permite interceptar métodos específicos.
 * - doReturn(): alternativa a when() que funciona con @Spy.
 * - doThrow(): simula que el método lanza una excepción.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de ClienteService")
class ClienteServiceTest {

    // WebClient se inyecta en el constructor pero no lo usamos directamente en tests
    @Mock
    private WebClient webClient;

    // @Spy + @InjectMocks: crea el objeto real con el mock inyectado,
    // y nos permite hacer doReturn sobre métodos específicos.
    @Spy
    @InjectMocks
    private ClienteService clienteService;

    private ClienteDTO clienteEjemplo;

    @BeforeEach
    void setUp() {
        clienteEjemplo = new ClienteDTO();
        clienteEjemplo.setId(1L);
        clienteEjemplo.setNombre("Juan Pérez");
        clienteEjemplo.setEmail("juan@test.com");
        clienteEjemplo.setTelefono("+56912345678");
        clienteEjemplo.setDireccion("Av. Principal 123");
    }

    // =========================================================================
    // TESTS PARA obtenerClientePorId() — Camino Feliz
    // =========================================================================

    @Test
    @DisplayName("obtenerClientePorId - Camino Feliz: retorna cliente cuando ms-clientes responde")
    void obtenerClientePorId_debeRetornarCliente_cuandoMsClientesResponde() {
        // ARRANGE: mockeamos el método protegido que hace la llamada HTTP real.
        // doReturn evita ejecutar el código real de llamarMsClientes().
        doReturn(clienteEjemplo).when(clienteService).llamarMsClientes(1L);

        // ACT
        ClienteDTO resultado = clienteService.obtenerClientePorId(1L);

        // ASSERT
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(1L, resultado.getId(), "El ID debe coincidir");
        assertEquals("Juan Pérez", resultado.getNombre(), "El nombre debe coincidir");
        assertEquals("juan@test.com", resultado.getEmail(), "El email debe coincidir");

        // Verificamos que se llamó exactamente una vez al método HTTP
        verify(clienteService, times(1)).llamarMsClientes(1L);
    }

    // =========================================================================
    // TESTS PARA obtenerClientePorId() — Camino de Error (Fallback Graceful)
    // =========================================================================

    @Test
    @DisplayName("obtenerClientePorId - Camino Error: retorna cliente por defecto cuando ms-clientes falla")
    void obtenerClientePorId_debeRetornarClientePorDefecto_cuandoMsClientesFalla() {
        // ARRANGE: simulamos que la llamada HTTP lanza una excepción (ms-clientes caído)
        doThrow(new RuntimeException("Connection refused: ms-clientes no disponible"))
                .when(clienteService).llamarMsClientes(99L);

        // ACT: el servicio NO debe propagar la excepción — debe aplicar el fallback
        ClienteDTO resultado = clienteService.obtenerClientePorId(99L);

        // ASSERT: verifica el comportamiento del patrón fallback graceful
        assertNotNull(resultado, "El cliente por defecto no debe ser nulo");
        assertEquals(99L, resultado.getId(),
                "El ID del fallback debe ser el clienteId que se buscó");
        assertEquals("Cliente no encontrado", resultado.getNombre(),
                "El nombre por defecto indica que el cliente no fue encontrado");
        assertEquals("no-disponible@mail.com", resultado.getEmail(),
                "El email por defecto debe ser el establecido en el fallback");
    }

    @Test
    @DisplayName("obtenerClientePorId - Camino Error: el fallback preserva el clienteId original")
    void obtenerClientePorId_fallback_debePreservarElClienteIdOriginal() {
        // ARRANGE: timeout simulado para cualquier clienteId
        doThrow(new RuntimeException("Timeout al conectar con ms-clientes"))
                .when(clienteService).llamarMsClientes(42L);

        // ACT
        ClienteDTO resultado = clienteService.obtenerClientePorId(42L);

        // ASSERT: el ID del cliente por defecto debe ser EXACTAMENTE el que se buscó
        assertNotNull(resultado);
        assertEquals(42L, resultado.getId(),
                "El fallback debe preservar el clienteId original para mantener coherencia");
    }
}
