package com.duoc.quickorder.msseguridad.controller;

import com.duoc.quickorder.msseguridad.exception.ResourceNotFoundException;
import com.duoc.quickorder.msseguridad.model.Usuario;
import com.duoc.quickorder.msseguridad.repository.UsuarioRepository;
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
@DisplayName("Tests unitarios de UsuarioController")
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setUsername("admin");
        usuarioEjemplo.setEmail("admin@quickorder.cl");
        usuarioEjemplo.setPassword("hashedPassword123");
        usuarioEjemplo.setRol("ADMIN");
    }

    // =========================================================================
    // TESTS PARA getAll()
    // =========================================================================

    @Test
    @DisplayName("getAll - Camino Feliz: retorna lista de usuarios")
    void getAll_debeRetornarListaDeUsuarios() {
        // ARRANGE
        Usuario otroUsuario = new Usuario();
        otroUsuario.setUsername("gamer123");
        otroUsuario.setRol("USER");
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioEjemplo, otroUsuario));

        // ACT
        List<Usuario> resultado = usuarioController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Camino Alternativo: retorna lista vacía si no hay usuarios")
    void getAll_debeRetornarListaVacia_cuandoNoHayUsuarios() {
        // ARRANGE
        when(usuarioRepository.findAll()).thenReturn(List.of());

        // ACT
        List<Usuario> resultado = usuarioController.getAll();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // =========================================================================
    // TESTS PARA getById()
    // =========================================================================

    @Test
    @DisplayName("getById - Camino Feliz: retorna el usuario si existe")
    void getById_debeRetornarUsuario_cuandoExiste() {
        // ARRANGE
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        // ACT
        ResponseEntity<Usuario> respuesta = usuarioController.getById(1L);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("admin", respuesta.getBody().getUsername());
        assertEquals("ADMIN", respuesta.getBody().getRol());
    }

    @Test
    @DisplayName("getById - Camino Error: lanza ResourceNotFoundException si no existe")
    void getById_debeLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> usuarioController.getById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // TESTS PARA create()
    // =========================================================================

    @Test
    @DisplayName("create - Camino Feliz: crea un usuario y retorna 201 CREATED")
    void create_debeCrearUsuario_yRetornar201() {
        // ARRANGE
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        // ACT
        ResponseEntity<Usuario> respuesta = usuarioController.create(usuarioEjemplo);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("admin", respuesta.getBody().getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("create - Camino Alternativo: rol del usuario se persiste correctamente")
    void create_debeGuardarRolCorrecto() {
        // ARRANGE
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("gamer_vip");
        nuevoUsuario.setEmail("gamer@vip.com");
        nuevoUsuario.setPassword("pass123");
        nuevoUsuario.setRol("USER");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevoUsuario);

        // ACT
        ResponseEntity<Usuario> respuesta = usuarioController.create(nuevoUsuario);

        // ASSERT
        assertEquals("USER", respuesta.getBody().getRol());
    }

    // =========================================================================
    // TESTS PARA update()
    // =========================================================================

    @Test
    @DisplayName("update - Camino Feliz: actualiza los datos del usuario")
    void update_debeActualizarDatos_cuandoUsuarioExiste() {
        // ARRANGE
        Usuario datosActualizados = new Usuario();
        datosActualizados.setUsername("admin_v2");
        datosActualizados.setEmail("admin_v2@quickorder.cl");
        datosActualizados.setPassword("newPass456");
        datosActualizados.setRol("SUPERADMIN");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // ACT
        ResponseEntity<Usuario> respuesta = usuarioController.update(1L, datosActualizados);

        // ASSERT
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("admin_v2", respuesta.getBody().getUsername());
        assertEquals("SUPERADMIN", respuesta.getBody().getRol());
    }

    @Test
    @DisplayName("update - Camino Error: lanza excepción si el usuario no existe")
    void update_debeLanzarExcepcion_cuandoUsuarioNoExiste() {
        // ARRANGE
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> usuarioController.update(999L, usuarioEjemplo)
        );
        verify(usuarioRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS PARA delete()
    // =========================================================================

    @Test
    @DisplayName("delete - Camino Feliz: elimina el usuario y retorna 204 NO CONTENT")
    void delete_debeEliminar_yRetornar204() {
        // ARRANGE
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // ACT
        ResponseEntity<Void> respuesta = usuarioController.delete(1L);

        // ASSERT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Camino Error: lanza excepción si el usuario no existe")
    void delete_debeLanzarExcepcion_cuandoUsuarioNoExiste() {
        // ARRANGE
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // ASSERT + ACT
        assertThrows(
            ResourceNotFoundException.class,
            () -> usuarioController.delete(99L)
        );
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}
