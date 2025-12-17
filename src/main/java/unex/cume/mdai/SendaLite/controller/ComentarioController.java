package unex.cume.mdai.SendaLite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import unex.cume.mdai.SendaLite.model.Comentario;
import unex.cume.mdai.SendaLite.service.ComentarioService;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;
import unex.cume.mdai.SendaLite.model.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/rutas/{rutaId}/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(ComentarioController.class);

    @Autowired
    public ComentarioController(ComentarioService comentarioService, UsuarioRepository usuarioRepository) {
        this.comentarioService = comentarioService;
        this.usuarioRepository = usuarioRepository;
        logger.info("ComentarioController inicializado");
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long rutaId, @RequestBody Map<String, Object> body) {
        try {
            // Usar el usuario autenticado en el SecurityContext para evitar suplantación
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            String email = auth.getName();
            logger.info("Usuario autenticado al crear comentario: {}", email);
            Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado: " + email));

            String texto = body.get("texto") != null ? body.get("texto").toString() : null;
            Comentario created = comentarioService.create(rutaId, usuario.getIdUsuario(), texto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            logger.warn("Error creando comentario: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error inesperado creando comentario", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear comentario");
        }
    }

    @GetMapping
    public List<Comentario> list(@PathVariable Long rutaId) {
        return comentarioService.listByRuta(rutaId);
    }

    @PutMapping("/{comentarioId}")
    public ResponseEntity<Comentario> update(@PathVariable Long comentarioId, @RequestBody Map<String, Object> body) {
        // Comprobar autenticación y permisos: sólo autor o admin pueden editar
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Comentario existing = comentarioService.buscarPorId(comentarioId).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        // Permitir si es autor o admin
        boolean esAutor = existing.getUsuario() != null && existing.getUsuario().getIdUsuario() != null && existing.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esAutor && !usuario.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String texto = body.get("texto") != null ? body.get("texto").toString() : null;
        if (texto == null) return ResponseEntity.badRequest().build();

        Comentario updated = comentarioService.update(comentarioId, texto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{comentarioId}")
    public ResponseEntity<Void> delete(@PathVariable Long comentarioId) {
        // Permitir eliminar sólo al autor o admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Comentario existing = comentarioService.buscarPorId(comentarioId).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        boolean esAutor = existing.getUsuario() != null && existing.getUsuario().getIdUsuario() != null && existing.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esAutor && !usuario.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        comentarioService.delete(comentarioId);
        return ResponseEntity.noContent().build();
    }
}
