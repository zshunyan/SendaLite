// src/main/java/unex/cume/mdai/SendaLite/service/RutaService.java
package unex.cume.mdai.SendaLite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.repository.RutaRepository;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;
import unex.cume.mdai.SendaLite.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final UsuarioRepository usuarioRepository;

    public RutaService(RutaRepository rutaRepository, UsuarioRepository usuarioRepository) {
        this.rutaRepository = rutaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Ruta anadirRuta(Ruta ruta) {
        if (ruta == null) throw new IllegalArgumentException("Ruta nula");
        // Resolver autor por id si se ha enviado solo la referencia con id
        if (ruta.getAutor() != null && ruta.getAutor().getIdUsuario() != null) {
            Long idAutor = ruta.getAutor().getIdUsuario();
            Usuario u = usuarioRepository.findById(idAutor).orElseThrow(() -> new IllegalArgumentException("Autor no encontrado: " + idAutor));
            ruta.setAutor(u);
        } else {
            throw new IllegalArgumentException("Autor requerido");
        }
        if (ruta.getFechaCreacion() == null) ruta.setFechaCreacion(LocalDate.now());
        return rutaRepository.save(ruta);
    }

    // Wrappers para el controller
    @Transactional
    public Ruta create(Ruta ruta) {
        return anadirRuta(ruta);
    }

    @Transactional
    public List<Ruta> listAll() {
        // Usar fetch join para traer autor y evitar problema N+1
        try {
            return rutaRepository.findAllWithAutor();
        } catch (Exception e) {
            // fallback en caso de que la consulta no exista o falle
            return rutaRepository.findAll();
        }
    }

    @Transactional
    public Optional<Ruta> findById(Long id) {
        return rutaRepository.findById(id);
    }

    /**
     * Devuelve una ruta con autor, comentarios y valoraciones ya inicializados.
     * Usa la consulta con JOIN FETCH definida en el repository para evitar LazyInitializationException
     */
    @Transactional
    public Optional<Ruta> buscarConDetalles(Long id) {
        return rutaRepository.findByIdWithDetalles(id);
    }

    @Transactional
    public List<Ruta> searchByTitulo(String q) {
        return rutaRepository.findByTituloContainingIgnoreCase(q);
    }

    @Transactional
    public void delete(Long id) {
        eliminarRutaPorId(id);
    }

    @Transactional
    public Ruta modificarRuta(Ruta ruta) {
        if (ruta == null || ruta.getIdRuta() == null) throw new IllegalArgumentException("Ruta o id nulo");
        ruta.setFechaActualizacion(LocalDate.now());
        // usar saveAndFlush para asegurar persistencia inmediata en tests
        return rutaRepository.saveAndFlush(ruta);
    }

    @Transactional
    public void eliminarRuta(Ruta ruta) {
        if (ruta == null || ruta.getIdRuta() == null) return;
        Optional<Ruta> managed = rutaRepository.findById(ruta.getIdRuta());
        managed.ifPresent(rutaRepository::delete);
    }

    @Transactional
    public boolean eliminarRutaPorId(Long idRuta) {
        if (idRuta == null) return false;
        Optional<Ruta> r = rutaRepository.findById(idRuta);
        if (r.isEmpty()) return false;
        // eliminar la entidad gestionada para que JPA maneje correctamente las cascadas
        rutaRepository.delete(r.get());
        return true;
    }

    @Transactional
    public List<Ruta> listarRutas() {
        // misma implementación que listAll(): intentar traer con autor
        try {
            return rutaRepository.findAllWithAutor();
        } catch (Exception e) {
            return rutaRepository.findAll();
        }
    }

    @Transactional
    public Optional<Ruta> buscarPorId(Long id) {
        return rutaRepository.findById(id);
    }
}