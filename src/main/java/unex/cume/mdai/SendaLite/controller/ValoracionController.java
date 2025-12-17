package unex.cume.mdai.SendaLite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import unex.cume.mdai.SendaLite.model.Valoracion;
import unex.cume.mdai.SendaLite.service.ValoracionService;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;
import unex.cume.mdai.SendaLite.model.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/rutas/{rutaId}/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(ValoracionController.class);

    @Autowired
    public ValoracionController(ValoracionService valoracionService, UsuarioRepository usuarioRepository) {
        this.valoracionService = valoracionService;
        this.usuarioRepository = usuarioRepository;
        logger.info("ValoracionController inicializado");
    }

    @PostMapping
    public ResponseEntity<?> upsert(@PathVariable Long rutaId, @RequestBody Map<String, Object> body) {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado: " + email));

            int puntuacion = Integer.parseInt(body.get("puntuacion").toString());
            Valoracion v = valoracionService.upsert(rutaId, usuario.getIdUsuario(), puntuacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(v);
        } catch (IllegalArgumentException ex) {
            logger.warn("Error upsert valoracion: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error inesperado upsert valoracion", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar valoración");
        }
    }

    @GetMapping
    public List<Valoracion> list(@PathVariable Long rutaId) {
        return valoracionService.listByRuta(rutaId);
    }

    @GetMapping("/avg")
    public ResponseEntity<Double> avg(@PathVariable Long rutaId) {
        double avg = valoracionService.averageForRuta(rutaId);
        return ResponseEntity.ok(avg);
    }

    @DeleteMapping("/{valoracionId}")
    public ResponseEntity<Void> delete(@PathVariable Long rutaId, @PathVariable Long valoracionId) {
        // Verificar autenticacion y permisos: autor de la valoración o admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Valoracion existing = valoracionService.buscarPorId(valoracionId).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        boolean esAutor = existing.getUsuario() != null && existing.getUsuario().getIdUsuario() != null && existing.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esAutor && !usuario.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        valoracionService.delete(valoracionId);
        return ResponseEntity.noContent().build();
    }
}
