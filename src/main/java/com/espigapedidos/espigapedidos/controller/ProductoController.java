package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.dto.ProductoRequest;
import com.espigapedidos.espigapedidos.entity.Producto;
import com.espigapedidos.espigapedidos.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private static final String VIEW_LISTA_PRODUCTOS = "productos/lista";
    private static final String VIEW_FORMULARIO_PRODUCTO = "productos/formulario";
    private static final String REDIRECT_PRODUCTOS = "redirect:/productos";

    private static final String ATTR_PRODUCTOS = "productos";
    private static final String ATTR_PRODUCTO = "producto";
    private static final String ATTR_ERROR = "error";

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute(ATTR_PRODUCTOS, productoService.listarProductos());
        return VIEW_LISTA_PRODUCTOS;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute(ATTR_PRODUCTO, new ProductoRequest());
        return VIEW_FORMULARIO_PRODUCTO;
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute ProductoRequest request, Model model) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            model.addAttribute(ATTR_ERROR, "El nombre es obligatorio");
            model.addAttribute(ATTR_PRODUCTO, request);
            return VIEW_FORMULARIO_PRODUCTO;
        }

        productoService.guardarProducto(construirProducto(request));
        return REDIRECT_PRODUCTOS;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerProductoPorId(id);

        if (producto == null) {
            return REDIRECT_PRODUCTOS;
        }

        model.addAttribute(ATTR_PRODUCTO, convertirARequest(producto));
        return VIEW_FORMULARIO_PRODUCTO;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return REDIRECT_PRODUCTOS;
    }

    private Producto construirProducto(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setId(request.getId());
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        return producto;
    }

    private ProductoRequest convertirARequest(Producto producto) {
        ProductoRequest request = new ProductoRequest();
        request.setId(producto.getId());
        request.setNombre(producto.getNombre());
        request.setCategoria(producto.getCategoria());
        request.setPrecio(producto.getPrecio());
        request.setStock(producto.getStock());
        return request;
    }
}