package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.entity.Usuario;
import com.espigapedidos.espigapedidos.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final String VIEW_LISTA_USUARIOS = "usuarios/lista";
    private static final String VIEW_FORMULARIO_USUARIO = "usuarios/formulario";
    private static final String REDIRECT_USUARIOS = "redirect:/usuarios";

    private static final String ATTR_USUARIOS = "usuarios";
    private static final String ATTR_USUARIO = "usuario";
    private static final String ATTR_ERROR = "error";

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute(ATTR_USUARIOS, usuarioService.listarUsuarios());
        return VIEW_LISTA_USUARIOS;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute(ATTR_USUARIO, new Usuario());
        return VIEW_FORMULARIO_USUARIO;
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            model.addAttribute(ATTR_ERROR, "El nombre de usuario es obligatorio");
            model.addAttribute(ATTR_USUARIO, usuario);
            return VIEW_FORMULARIO_USUARIO;
        }

        usuarioService.guardarUsuario(usuario);
        return REDIRECT_USUARIOS;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        if (usuario == null) {
            return REDIRECT_USUARIOS;
        }

        model.addAttribute(ATTR_USUARIO, usuario);
        return VIEW_FORMULARIO_USUARIO;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return REDIRECT_USUARIOS;
    }
}