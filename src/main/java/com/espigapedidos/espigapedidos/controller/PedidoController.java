package com.espigapedidos.espigapedidos.controller;

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
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        return VIEW_LISTA_PEDIDOS;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());

        model.addAttribute(ATTR_PEDIDO, pedido);
        model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());

        return VIEW_FORMULARIO_PEDIDO;
    }

    @PostMapping("/guardar")
    public String guardarPedido(@ModelAttribute Pedido pedido,
                                @RequestParam("tienda") Long tiendaId,
                                Model model) {
        Tienda tienda = tiendaService.obtenerTiendaPorId(tiendaId);

        if (tienda == null) {
            model.addAttribute(ATTR_ERROR, "Tienda no válida");
            model.addAttribute(ATTR_PEDIDO, pedido);
            model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());
            return VIEW_FORMULARIO_PEDIDO;
        }

        pedido.setTienda(tienda);
        pedidoService.guardarPedido(pedido);

        return REDIRECT_PEDIDOS;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);

        if (pedido == null) {
            return REDIRECT_PEDIDOS;
        }

        model.addAttribute(ATTR_PEDIDO, pedido);
        model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());

        return VIEW_FORMULARIO_PEDIDO;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return REDIRECT_PEDIDOS;
    }
}