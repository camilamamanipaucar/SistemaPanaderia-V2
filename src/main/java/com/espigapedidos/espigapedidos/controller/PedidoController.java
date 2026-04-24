package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.dto.PedidoRequest;
import com.espigapedidos.espigapedidos.entity.Pedido;
import com.espigapedidos.espigapedidos.entity.Tienda;
import com.espigapedidos.espigapedidos.service.PedidoService;
import com.espigapedidos.espigapedidos.service.TiendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private static final String VIEW_LISTA_PEDIDOS = "pedidos/lista";
    private static final String VIEW_FORMULARIO_PEDIDO = "pedidos/formulario";
    private static final String REDIRECT_PEDIDOS = "redirect:/pedidos";

    private static final String ATTR_PEDIDOS = "pedidos";
    private static final String ATTR_PEDIDO = "pedido";
    private static final String ATTR_TIENDAS = "tiendas";
    private static final String ATTR_ERROR = "error";

    private final PedidoService pedidoService;
    private final TiendaService tiendaService;

    public PedidoController(PedidoService pedidoService, TiendaService tiendaService) {
        this.pedidoService = pedidoService;
        this.tiendaService = tiendaService;
    }

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute(ATTR_PEDIDOS, pedidoService.listarPedidos());
        return VIEW_LISTA_PEDIDOS;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        PedidoRequest pedido = new PedidoRequest();
        pedido.setFecha(LocalDate.now());

        model.addAttribute(ATTR_PEDIDO, pedido);
        model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());

        return VIEW_FORMULARIO_PEDIDO;
    }

    @PostMapping("/guardar")
    public String guardarPedido(@ModelAttribute PedidoRequest request, Model model) {
        Tienda tienda = tiendaService.obtenerTiendaPorId(request.getTiendaId());

        if (tienda == null) {
            model.addAttribute(ATTR_ERROR, "Tienda no válida");
            model.addAttribute(ATTR_PEDIDO, request);
            model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());
            return VIEW_FORMULARIO_PEDIDO;
        }

        Pedido pedido = construirPedido(request, tienda);
        pedidoService.guardarPedido(pedido);

        return REDIRECT_PEDIDOS;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);

        if (pedido == null) {
            return REDIRECT_PEDIDOS;
        }

        model.addAttribute(ATTR_PEDIDO, convertirARequest(pedido));
        model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());

        return VIEW_FORMULARIO_PEDIDO;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return REDIRECT_PEDIDOS;
    }

    private Pedido construirPedido(PedidoRequest request, Tienda tienda) {
        Pedido pedido = new Pedido();
        pedido.setId(request.getId());
        pedido.setFecha(request.getFecha());
        pedido.setEstado(request.getEstado());
        pedido.setTienda(tienda);
        return pedido;
    }

    private PedidoRequest convertirARequest(Pedido pedido) {
        PedidoRequest request = new PedidoRequest();
        request.setId(pedido.getId());
        request.setFecha(pedido.getFecha());
        request.setEstado(pedido.getEstado());

        if (pedido.getTienda() != null) {
            request.setTiendaId(pedido.getTienda().getId());
        }

        return request;
    }
}