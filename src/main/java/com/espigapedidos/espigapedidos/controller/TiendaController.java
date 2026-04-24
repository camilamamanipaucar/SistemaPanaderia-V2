package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.entity.Tienda;
import com.espigapedidos.espigapedidos.service.TiendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tiendas")
public class TiendaController {

    private static final String VIEW_LISTA_TIENDAS = "tiendas/lista";
    private static final String VIEW_FORMULARIO_TIENDA = "tiendas/formulario";
    private static final String REDIRECT_TIENDAS = "redirect:/tiendas";

    private static final String ATTR_TIENDAS = "tiendas";
    private static final String ATTR_TIENDA = "tienda";
    private static final String ATTR_ERROR = "error";

    private final TiendaService tiendaService;

    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    @GetMapping
    public String listarTiendas(Model model) {
        model.addAttribute(ATTR_TIENDAS, tiendaService.listarTiendas());
        return VIEW_LISTA_TIENDAS;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute(ATTR_TIENDA, new Tienda());
        return VIEW_FORMULARIO_TIENDA;
    }

    @PostMapping("/guardar")
    public String guardarTienda(@ModelAttribute Tienda tienda, Model model) {
        if (tienda.getNombre() == null || tienda.getNombre().isBlank()) {
            model.addAttribute(ATTR_ERROR, "El nombre de la tienda es obligatorio");
            model.addAttribute(ATTR_TIENDA, tienda);
            return VIEW_FORMULARIO_TIENDA;
        }

        tiendaService.guardarTienda(tienda);
        return REDIRECT_TIENDAS;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Tienda tienda = tiendaService.obtenerTiendaPorId(id);

        if (tienda == null) {
            return REDIRECT_TIENDAS;
        }

        model.addAttribute(ATTR_TIENDA, tienda);
        return VIEW_FORMULARIO_TIENDA;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarTienda(@PathVariable Long id) {
        tiendaService.eliminarTienda(id);
        return REDIRECT_TIENDAS;
    }
}