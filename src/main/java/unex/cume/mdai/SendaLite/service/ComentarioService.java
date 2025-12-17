package unex.cume.mdai.SendaLite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unex.cume.mdai.SendaLite.model.Comentario;
import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.repository.ComentarioRepository;
import unex.cume.mdai.SendaLite.repository.RutaRepository;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final RutaRepository rutaRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository, RutaRepository rutaRepository, UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.rutaRepository = rutaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Comentario anadirComentario(Comentario comentario) {
        if (comentario == null) throw new IllegalArgumentException("Comentario nulo");
        if (comentario.getFechaComentario() == null) comentario.setFechaComentario(LocalDate.now());

        // Si el comentario trae referencias a usuario/ruta, asegurarlas como entidades gestionadas
        if (comentario.getUsuario() != null && comentario.getUsuario().getIdUsuario() != null) {
            Long uid = comentario.getUsuario().getIdUsuario();
            Usuario u = usuarioRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + uid));
            comentario.setUsuario(u);
        }
        if (comentario.getRuta() != null && comentario.getRuta().getIdRuta() != null) {
            Long rid = comentario.getRuta().getIdRuta();
            Ruta r = rutaRepository.findById(rid).orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + rid));
            comentario.setRuta(r);
        }

        Comentario saved = comentarioRepository.save(comentario);
        // Mantener la relación bidireccional
        if (saved.getRuta() != null && saved.getRuta().getComentarios() != null) {
            saved.getRuta().getComentarios().add(saved);
        }
        return saved;
    }

    @Transactional
    public Comentario modificarComentario(Comentario comentario) {
        if (comentario == null || comentario.getIdComentario() == null) throw new IllegalArgumentException("Comentario o id nulo");
        comentario.setFechaEdicion(LocalDate.now());
        // usar saveAndFlush para garantizar que los cambios se escriben inmediatamente en la BD
        return comentarioRepository.saveAndFlush(comentario);
    }

    @Transactional
    public void eliminarComentario(Comentario comentario) {
        if (comentario == null) return;
        Long id = comentario.getIdComentario();
        if (id == null) {
            comentarioRepository.delete(comentario);
            comentarioRepository.flush();
            return;
        }
        comentarioRepository.findById(id).ifPresent(c -> {
            Ruta r = c.getRuta();
            if (r != null && r.getComentarios() != null) {
                r.getComentarios().remove(c);
            }
            comentarioRepository.deleteById(id);
            comentarioRepository.flush();
        });
    }

    @Transactional
    public List<Comentario> listarPorRutaId(Long idRuta) {
        return comentarioRepository.findByRutaIdRuta(idRuta);
    }

    @Transactional
    public List<Comentario> listarPorUsuarioId(Long idUsuario) {
        return comentarioRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional
    public Optional<Comentario> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    // Métodos usados por ComentarioController
    @Transactional
    public Comentario create(Long rutaId, Long usuarioId, String texto) {
        if (rutaId == null || usuarioId == null || texto == null) throw new IllegalArgumentException("Parámetros insuficientes");
        Ruta ruta = rutaRepository.findById(rutaId).orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + rutaId));
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        Comentario c = new Comentario();
        c.setRuta(ruta);
        c.setUsuario(usuario);
        c.setTexto(texto);
        c.setFechaComentario(LocalDate.now());
        Comentario saved = comentarioRepository.save(c);
        if (ruta.getComentarios() != null) ruta.getComentarios().add(saved);
        return saved;
    }

    @Transactional
    public List<Comentario> listByRuta(Long rutaId) {
        return listarPorRutaId(rutaId);
    }

    @Transactional
    public Comentario update(Long comentarioId, String texto) {
        Comentario c = comentarioRepository.findById(comentarioId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado: " + comentarioId));
        c.setTexto(texto);
        c.setFechaEdicion(LocalDate.now());
        return comentarioRepository.saveAndFlush(c);
    }

    @Transactional
    public void delete(Long comentarioId) {
        if (comentarioId == null) return;
        comentarioRepository.findById(comentarioId).ifPresent(c -> {
            Ruta r = c.getRuta();
            if (r != null && r.getComentarios() != null) r.getComentarios().remove(c);
            comentarioRepository.deleteById(comentarioId);
            comentarioRepository.flush();
        });
    }
}
