package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.entity.Usuario;
import com.espigapedidos.espigapedidos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetupController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String TIENDA_USERNAME = "tienda";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_TIENDA = "TIENDA";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String tiendaPassword;

    public SetupController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.setup.admin-password}") String adminPassword,
            @Value("${app.setup.tienda-password}") String tiendaPassword
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.tiendaPassword = tiendaPassword;
    }

    @GetMapping("/setup-admin")
    public String crearAdmin() {
        return crearUsuarioInicial(
                ADMIN_USERNAME,
                "Administrador",
                adminPassword,
                ROLE_ADMIN,
                "El usuario admin ya existe",
                "Usuario admin creado correctamente"
        );
    }

    @GetMapping("/setup-tienda")
    public String crearTienda() {
        return crearUsuarioInicial(
                TIENDA_USERNAME,
                "Usuario Tienda",
                tiendaPassword,
                ROLE_TIENDA,
                "Usuario tienda ya existe",
                "Usuario tienda creado correctamente"
        );
    }

    private String crearUsuarioInicial(
            String username,
            String nombre,
            String rawPassword,
            String rol,
            String mensajeExistente,
            String mensajeCreado
    ) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return mensajeExistente;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuario.setRol(rol);
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return mensajeCreado;
    }
}