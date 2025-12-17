package unex.cume.mdai.SendaLite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.service.UsuarioService;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        logger.info("UsuarioController inicializado");
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario u) {
        Usuario saved = usuarioService.create(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Usuario> list() {
        return usuarioService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> get(@PathVariable Long id) {
        return usuarioService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario u) {
        try {
            Usuario updated = usuarioService.update(id, u);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            logger.warn("Validación al actualizar usuario {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            logger.warn("Violation al actualizar usuario {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de integridad: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error inesperado actualizando usuario {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Permitir eliminar sólo si el usuario autenticado es el mismo o es admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        Usuario actor = usuarioRepository.findByEmail(email).orElse(null);
        if (actor == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!actor.isAdmin() && (actor.getIdUsuario() == null || !actor.getIdUsuario().equals(id))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
