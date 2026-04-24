package com.espigapedidos.espigapedidos.config;

import com.espigapedidos.espigapedidos.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_ERROR = "/login?error";
    private static final String LOGOUT_SUCCESS = "/login?logout";

    private static final String[] PUBLIC_RESOURCES = {
            "/css/**", "/js/**", "/uploads/**"
    };

    private static final String[] ADMIN_ROUTES = {
            "/usuarios/**", "/productos/**", "/tiendas/**"
    };

    private static final String[] ADMIN_TIENDA_ROUTES = {
            "/pedidos/**", "/pedidos-especiales/**"
    };

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    .authenticationProvider(authenticationProvider())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(PUBLIC_RESOURCES).permitAll()
                            .requestMatchers(LOGIN_URL, "/setup-admin", "/setup-tienda").permitAll()
                            .requestMatchers(ADMIN_ROUTES).hasRole("ADMIN")
                            .requestMatchers(ADMIN_TIENDA_ROUTES).hasAnyRole("ADMIN", "TIENDA")
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage(LOGIN_URL)
                            .loginProcessingUrl(LOGIN_URL)
                            .defaultSuccessUrl("/", true)
                            .failureUrl(LOGIN_ERROR)
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutSuccessUrl(LOGOUT_SUCCESS)
                            .permitAll()
                    );

            return http.build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error configurando Spring Security", e);
        }
    }
}