package unex.cume.mdai.SendaLite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.service.RutaService;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;
import unex.cume.mdai.SendaLite.model.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(RutaController.class);

    @Autowired
    public RutaController(RutaService rutaService, UsuarioRepository usuarioRepository) {
        this.rutaService = rutaService;
        this.usuarioRepository = usuarioRepository;
        logger.info("RutaController inicializado");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Ruta ruta) {
        // Asignar autor desde el usuario autenticado para evitar que el cliente fije otro autor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
        try {
            // Si el actor es admin y el payload incluye autor.idUsuario permitimos usarlo
            if (usuario.isAdmin() && ruta.getAutor() != null && ruta.getAutor().getIdUsuario() != null) {
                Long idAutor = ruta.getAutor().getIdUsuario();
                Usuario u = usuarioRepository.findById(idAutor).orElse(null);
                if (u == null) return ResponseEntity.badRequest().body("Autor indicado no existe: " + idAutor);
                ruta.setAutor(u);
            } else {
                // Para usuarios normales forzamos autor autenticado
                ruta.setAutor(usuario);
            }
            Ruta saved = rutaService.create(ruta);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException ex) {
            logger.warn("Error creando ruta: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error inesperado creando ruta", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @GetMapping
    public List<Ruta> list(@RequestParam(value = "q", required = false) String q) {
        if (q != null && !q.isBlank()) {
            return rutaService.searchByTitulo(q);
        }
        return rutaService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> get(@PathVariable Long id) {
        // Usar buscarConDetalles para traer autor, comentarios y valoraciones ya inicializados
        return rutaService.buscarConDetalles(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ruta ruta) {
        // Validar permisos: solo autor o admin pueden modificar; además no permitir cambiar autor desde payload
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Ruta existing = rutaService.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        boolean esAutor = existing.getAutor() != null && existing.getAutor().getIdUsuario() != null && existing.getAutor().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esAutor && !usuario.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado a modificar esta ruta");
        }

        try {
            // Copiar campos editables desde el body, pero preservar el autor
            existing.setTitulo(ruta.getTitulo());
            existing.setDescripcion(ruta.getDescripcion());
            existing.setDificultad(ruta.getDificultad());
            existing.setDistanciaKm(ruta.getDistanciaKm());
            existing.setTipoActividad(ruta.getTipoActividad());
            // Si el actor es admin y el payload contiene autor.idUsuario permitir cambiar autor
            if (usuario.isAdmin() && ruta.getAutor() != null && ruta.getAutor().getIdUsuario() != null) {
                Long idAutor = ruta.getAutor().getIdUsuario();
                Usuario u = usuarioRepository.findById(idAutor).orElse(null);
                if (u == null) return ResponseEntity.badRequest().body("Autor indicado no existe: " + idAutor);
                existing.setAutor(u);
            }
            Ruta updated = rutaService.modificarRuta(existing);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            logger.warn("Error modificando ruta: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error inesperado modificando ruta", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Verificar autenticación y permisos: solo autor de la ruta o admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Ruta ruta = rutaService.findById(id).orElse(null);
        if (ruta == null) return ResponseEntity.notFound().build();

        boolean esAutor = ruta.getAutor() != null && ruta.getAutor().getIdUsuario() != null && ruta.getAutor().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esAutor && !usuario.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        rutaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
