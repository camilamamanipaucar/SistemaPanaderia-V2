package com.espigapedidos.espigapedidos.controller;

import com.espigapedidos.espigapedidos.entity.PedidoEspecial;
import com.espigapedidos.espigapedidos.entity.Tienda;
import com.espigapedidos.espigapedidos.service.PedidoEspecialService;
import com.espigapedidos.espigapedidos.service.TiendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/pedidos-especiales")
public class PedidoEspecialController {

    private static final String REDIRECT_PEDIDOS_ESPECIALES = "redirect:/pedidos-especiales";
    private static final String UPLOADS_DIRECTORY = "uploads";
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5MB

    private final PedidoEspecialService pedidoEspecialService;
    private final TiendaService tiendaService;

    public PedidoEspecialController(PedidoEspecialService pedidoEspecialService, TiendaService tiendaService) {
        this.pedidoEspecialService = pedidoEspecialService;
        this.tiendaService = tiendaService;
    }

    @GetMapping
    public String listarPedidosEspeciales(Model model) {
        model.addAttribute("pedidosEspeciales", pedidoEspecialService.listarPedidosEspeciales());
        return "pedidosespeciales/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pedidoEspecial", new PedidoEspecial());
        model.addAttribute("tiendas", tiendaService.listarTiendas());
        return "pedidosespeciales/formulario";
    }

    @PostMapping("/guardar")
    public String guardarPedidoEspecial(@ModelAttribute PedidoEspecial pedidoEspecial,
                                        @RequestParam("tiendaId") Long tiendaId,
                                        @RequestParam("archivoImagen") MultipartFile archivoImagen) throws IOException {

        Tienda tienda = tiendaService.obtenerTiendaPorId(tiendaId);
        pedidoEspecial.setTienda(tienda);

        if (!archivoImagen.isEmpty()) {
            String nombreArchivo = guardarImagenSegura(archivoImagen);
            pedidoEspecial.setImagen(nombreArchivo);
        }

        pedidoEspecialService.guardarPedidoEspecial(pedidoEspecial);
        return REDIRECT_PEDIDOS_ESPECIALES;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPedidoEspecial(@PathVariable Long id) {
        pedidoEspecialService.eliminarPedidoEspecial(id);
        return REDIRECT_PEDIDOS_ESPECIALES;
    }

    private String guardarImagenSegura(MultipartFile archivoImagen) throws IOException {
        validarImagen(archivoImagen);

        String extension = obtenerExtensionSegura(archivoImagen.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID() + extension;

        Path carpetaUploads = Path.of(System.getProperty("user.dir"), UPLOADS_DIRECTORY)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(carpetaUploads);

        Path destino = carpetaUploads.resolve(nombreArchivo).normalize();

        if (!destino.startsWith(carpetaUploads)) {
            throw new IOException("Ruta de archivo no permitida");
        }

        Files.copy(archivoImagen.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nombreArchivo;
    }

    private void validarImagen(MultipartFile archivoImagen) throws IOException {
        if (archivoImagen.getSize() > MAX_FILE_SIZE) {
            throw new IOException("El archivo supera el tamaño permitido");
        }

        String contentType = archivoImagen.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Solo se permiten archivos de imagen");
        }
    }

    private String obtenerExtensionSegura(String nombreOriginal) throws IOException {
        String nombreLimpio = StringUtils.cleanPath(nombreOriginal == null ? "" : nombreOriginal);
        String nombreMinuscula = nombreLimpio.toLowerCase(Locale.ROOT);

        if (nombreMinuscula.endsWith(".jpg") || nombreMinuscula.endsWith(".jpeg")) {
            return ".jpg";
        }

        if (nombreMinuscula.endsWith(".png")) {
            return ".png";
        }

        if (nombreMinuscula.endsWith(".webp")) {
            return ".webp";
        }

        throw new IOException("Extensión de archivo no permitida");
    }
}