package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.entity.DetallePedido;
import com.espigapedidos.espigapedidos.entity.Pedido;
import com.espigapedidos.espigapedidos.entity.Producto;
import com.espigapedidos.espigapedidos.service.DetallePedidoService;
import com.espigapedidos.espigapedidos.service.PedidoService;
import com.espigapedidos.espigapedidos.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/detalle-pedido")
public class DetallePedidoController {

    private static final String VIEW_LISTA_DETALLE = "detallepedido/lista";
    private static final String VIEW_FORMULARIO_DETALLE = "detallepedido/formulario";
    private static final String REDIRECT_DETALLE = "redirect:/detalle-pedido/";
    private static final String REDIRECT_PEDIDOS = "redirect:/pedidos";

    private final DetallePedidoService detallePedidoService;
    private final PedidoService pedidoService;
    private final ProductoService productoService;

    public DetallePedidoController(DetallePedidoService detallePedidoService,
                                   PedidoService pedidoService,
                                   ProductoService productoService) {
        this.detallePedidoService = detallePedidoService;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }

    @GetMapping("/{pedidoId}")
    public String verDetallePedido(@PathVariable Long pedidoId, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId);

        if (pedido == null) {
            return REDIRECT_PEDIDOS;
        }

        model.addAttribute("detalles", detallePedidoService.listarPorPedido(pedidoId));
        model.addAttribute("pedido", pedido);

        return VIEW_LISTA_DETALLE;
    }

    @GetMapping("/nuevo/{pedidoId}")
    public String mostrarFormularioNuevo(@PathVariable Long pedidoId, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId);

        if (pedido == null) {
            return REDIRECT_PEDIDOS;
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("detallePedido", new DetallePedido());

        return VIEW_FORMULARIO_DETALLE;
    }

    @PostMapping("/guardar")
    public String guardarDetalle(@ModelAttribute DetallePedido detallePedido,
                                 @RequestParam("pedidoId") Long pedidoId,
                                 @RequestParam("productoId") Long productoId) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId);
        Producto producto = productoService.obtenerProductoPorId(productoId);

        if (pedido == null || producto == null) {
            return REDIRECT_PEDIDOS;
        }

        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedidoService.guardarDetalle(detallePedido);

        return REDIRECT_DETALLE + pedidoId;
    }

    @PostMapping("/eliminar/{id}/{pedidoId}")
    public String eliminarDetalle(@PathVariable Long id, @PathVariable Long pedidoId) {
        detallePedidoService.eliminarDetalle(id);
        return REDIRECT_DETALLE + pedidoId;
    }
}