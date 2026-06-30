package com.duoc.quickorder.msinventario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.duoc.quickorder.msinventario.exception.ResourceNotFoundException;
import com.duoc.quickorder.msinventario.model.Producto;
import com.duoc.quickorder.msinventario.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    
    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    
    @GetMapping
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return ResponseEntity.ok(producto);
    }
    
    @PostMapping
    public ResponseEntity<Producto> createProducto(@Valid @RequestBody Producto producto) {
        producto.setFechaCreacion(java.time.LocalDateTime.now());
        Producto nuevoProducto = productoRepository.save(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @Valid @RequestBody Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setStock(productoActualizado.getStock());
        producto.setPlataforma(productoActualizado.getPlataforma());
        producto.setRegion(productoActualizado.getRegion());
        producto.setCodigoKey(productoActualizado.getCodigoKey());
        return ResponseEntity.ok(productoRepository.save(producto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/plataforma/{plataforma}")
    public List<Producto> getProductosByPlataforma(@PathVariable String plataforma) {
        return productoRepository.findByPlataforma(plataforma);
    }
    
    @GetMapping("/stock/bajo/{limite}")
    public List<Producto> getProductosStockBajo(@PathVariable Integer limite) {
        return productoRepository.findByStockLessThan(limite);
    }
}
