package com.duoc.quickorder.msinventario.controller;

import com.duoc.quickorder.msinventario.exception.ResourceNotFoundException;
import com.duoc.quickorder.msinventario.model.Producto;
import com.duoc.quickorder.msinventario.repository.ProductoRepository;
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
@DisplayName("Tests unitarios de ProductoController")
class ProductoControllerTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoController productoController;

    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = new Producto();
        productoEjemplo.setId(1L);
        productoEjemplo.setNombre("God of War");
        productoEjemplo.setDescripcion("Juego de acción PS5");
        productoEjemplo.setPrecio(59.99);
        productoEjemplo.setStock(50);
        productoEjemplo.setPlataforma("PS5");
        productoEjemplo.setRegion("Chile");
    }

    @Test
    @DisplayName("getAllProductos - Camino Feliz: retorna lista de productos")
    void getAllProductos_debeRetornarLista() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoEjemplo, new Producto()));
        List<Producto> resultado = productoController.getAllProductos();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllProductos - Camino Alternativo: retorna lista vacía")
    void getAllProductos_debeRetornarListaVacia() {
        when(productoRepository.findAll()).thenReturn(List.of());
        List<Producto> resultado = productoController.getAllProductos();
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("getProductoById - Camino Feliz: retorna producto cuando existe")
    void getProductoById_debeRetornarProducto_cuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        ResponseEntity<Producto> respuesta = productoController.getProductoById(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("God of War", respuesta.getBody().getNombre());
    }

    @Test
    @DisplayName("getProductoById - Camino Error: lanza excepción cuando no existe")
    void getProductoById_debeLanzarExcepcion_cuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoController.getProductoById(99L));
    }

    @Test
    @DisplayName("createProducto - Camino Feliz: crea producto y retorna 201")
    void createProducto_debeCrear_yRetornar201() {
        when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);
        ResponseEntity<Producto> respuesta = productoController.createProducto(productoEjemplo);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(productoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createProducto - Camino Alternativo: stock se mantiene del objeto recibido")
    void createProducto_debeManatenerStock() {
        productoEjemplo.setStock(10);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);
        ResponseEntity<Producto> respuesta = productoController.createProducto(productoEjemplo);
        assertEquals(10, respuesta.getBody().getStock());
    }

    @Test
    @DisplayName("updateProducto - Camino Feliz: actualiza datos del producto")
    void updateProducto_debeActualizar_cuandoExiste() {
        Producto actualizado = new Producto();
        actualizado.setNombre("Horizon");
        actualizado.setDescripcion("RPG PS5");
        actualizado.setPrecio(49.99);
        actualizado.setStock(30);
        actualizado.setPlataforma("PS5");
        actualizado.setRegion("USA");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        ResponseEntity<Producto> respuesta = productoController.updateProducto(1L, actualizado);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Horizon", respuesta.getBody().getNombre());
    }

    @Test
    @DisplayName("updateProducto - Camino Error: lanza excepción si producto no existe")
    void updateProducto_debeLanzarExcepcion_cuandoNoExiste() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
            () -> productoController.updateProducto(999L, productoEjemplo));
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteProducto - Camino Feliz: elimina producto y retorna 204")
    void deleteProducto_debeEliminar_yRetornar204() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);
        ResponseEntity<Void> respuesta = productoController.deleteProducto(1L);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProducto - Camino Error: lanza excepción si producto no existe")
    void deleteProducto_debeLanzarExcepcion_cuandoNoExiste() {
        when(productoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
            () -> productoController.deleteProducto(99L));
        verify(productoRepository, never()).deleteById(anyLong());
    }
}
