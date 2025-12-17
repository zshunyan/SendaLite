package unex.cume.mdai.SendaLite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import unex.cume.mdai.SendaLite.service.UsuarioService;
import unex.cume.mdai.SendaLite.service.RutaService;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class AdminController {

    private final UsuarioService usuarioService;
    private final RutaService rutaService;

    @Autowired
    public AdminController(UsuarioService usuarioService, RutaService rutaService) {
        this.usuarioService = usuarioService;
        this.rutaService = rutaService;
    }

    // Redirige a la lista de usuarios si alguien accede a /admin
    @GetMapping("/admin")
    public String adminIndex(Model model) {
        model.addAttribute("usuariosCount", usuarioService.listAll().size());
        model.addAttribute("rutasCount", rutaService.listAll().size());
        return "admin/index";
    }

    @GetMapping("/admin/usuarios")
    public String adminUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listAll());
        return "admin/usuarios";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String adminEditarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.findById(id).orElse(null));
        return "admin/usuario_form";
    }

    @GetMapping("/admin/rutas")
    public String adminRutas(Model model) {
        model.addAttribute("rutas", rutaService.listAll());
        return "admin/rutas";
    }

    @GetMapping("/admin/rutas/{id}/editar")
    public String adminEditarRuta(@PathVariable Long id, Model model) {
        model.addAttribute("ruta", rutaService.findById(id).orElse(null));
        return "ruta_form";
    }
}
