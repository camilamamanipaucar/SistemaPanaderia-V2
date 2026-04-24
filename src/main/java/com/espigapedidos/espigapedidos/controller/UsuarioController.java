package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.dto.UsuarioRequest;
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
        UsuarioRequest request = new UsuarioRequest();
        request.setActivo(true);
        model.addAttribute(ATTR_USUARIO, request);
        return VIEW_FORMULARIO_USUARIO;
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute UsuarioRequest request, Model model) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            model.addAttribute(ATTR_ERROR, "El nombre de usuario es obligatorio");
            model.addAttribute(ATTR_USUARIO, request);
            return VIEW_FORMULARIO_USUARIO;
        }

        usuarioService.guardarUsuario(construirUsuario(request));
        return REDIRECT_USUARIOS;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        if (usuario == null) {
            return REDIRECT_USUARIOS;
        }

        model.addAttribute(ATTR_USUARIO, convertirARequest(usuario));
        return VIEW_FORMULARIO_USUARIO;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return REDIRECT_USUARIOS;
    }

    private Usuario construirUsuario(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setId(request.getId());
        usuario.setNombre(request.getNombre());
        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        usuario.setRol(request.getRol());
        usuario.setActivo(Boolean.TRUE.equals(request.getActivo()));
        return usuario;
    }

    private UsuarioRequest convertirARequest(Usuario usuario) {
        UsuarioRequest request = new UsuarioRequest();
        request.setId(usuario.getId());
        request.setNombre(usuario.getNombre());
        request.setUsername(usuario.getUsername());
        request.setRol(usuario.getRol());
        request.setActivo(usuario.getActivo());
        request.setPassword("");
        return request;
    }
}