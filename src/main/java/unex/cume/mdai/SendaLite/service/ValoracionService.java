package unex.cume.mdai.SendaLite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unex.cume.mdai.SendaLite.model.Valoracion;
import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.repository.ValoracionRepository;
import unex.cume.mdai.SendaLite.repository.RutaRepository;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final RutaRepository rutaRepository;
    private final UsuarioRepository usuarioRepository;

    public ValoracionService(ValoracionRepository valoracionRepository, RutaRepository rutaRepository, UsuarioRepository usuarioRepository) {
        this.valoracionRepository = valoracionRepository;
        this.rutaRepository = rutaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Valoracion anadirValoracion(Valoracion valoracion) {
        if (valoracion == null) throw new IllegalArgumentException("Valoracion nula");
        if (valoracion.getUsuario() == null || valoracion.getRuta() == null) throw new IllegalArgumentException("Usuario y ruta requeridos");

        // Asegurar que las referencias a Ruta y Usuario están resueltas (entidades gestionadas)
        Long usuarioId = valoracion.getUsuario().getIdUsuario();
        Long rutaId = valoracion.getRuta().getIdRuta();
        if (usuarioId == null || rutaId == null) {
            throw new IllegalArgumentException("Usuario.id y Ruta.id requeridos");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + rutaId));

        // Reemplazar las referencias para evitar problemas con instancias transient/detached
        valoracion.setUsuario(usuario);
        valoracion.setRuta(ruta);

        Optional<Valoracion> exists = valoracionRepository.findByUsuarioIdUsuarioAndRutaIdRuta(
                usuarioId, rutaId);
        if (exists.isPresent()) throw new IllegalArgumentException("Ya existe una valoración de este usuario para la ruta");
        if (valoracion.getFechaValoracion() == null) valoracion.setFechaValoracion(LocalDate.now());
        Valoracion saved = valoracionRepository.save(valoracion);

        // Mantener la relación bidireccional: añadir la valoración al conjunto de la ruta gestionada
        if (ruta.getValoraciones() != null) {
            ruta.getValoraciones().add(saved);
        }

        return saved;
    }

    @Transactional
    public Valoracion modificarValoracion(Valoracion valoracion) {
        if (valoracion == null || valoracion.getIdValoracion() == null) throw new IllegalArgumentException("Valoracion o id nulo");
        // asegurar persistencia inmediata en tests
        return valoracionRepository.saveAndFlush(valoracion);
    }

    @Transactional
    public void eliminarValoracion(Valoracion valoracion) {
        if (valoracion == null) return;
        Long id = valoracion.getIdValoracion();
        if (id == null) {
            // intentar eliminar por la instancia directamente
            valoracionRepository.delete(valoracion);
            return;
        }
        Optional<Valoracion> managed = valoracionRepository.findById(id);
        managed.ifPresent(v -> {
            Ruta r = v.getRuta();
            if (r != null && r.getValoraciones() != null) {
                r.getValoraciones().remove(v);
            }
            valoracionRepository.delete(v);
        });
    }

    @Transactional
    public List<Valoracion> listarPorRuta(Long idRuta) {
        return valoracionRepository.findByRutaIdRuta(idRuta);
    }

    @Transactional
    public Optional<Valoracion> buscarPorId(Long id) {
        return valoracionRepository.findById(id);
    }

    // Wrappers y utilidades esperadas por controller
    @Transactional
    public Valoracion upsert(Long rutaId, Long usuarioId, int puntuacion) {
        if (rutaId == null || usuarioId == null) throw new IllegalArgumentException("RutaId y UsuarioId requeridos");
        Ruta ruta = rutaRepository.findById(rutaId).orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada: " + rutaId));
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        Optional<Valoracion> existing = valoracionRepository.findByUsuarioIdUsuarioAndRutaIdRuta(usuarioId, rutaId);
        Valoracion v;
        if (existing.isPresent()) {
            v = existing.get();
            v.setPuntuacion(puntuacion);
            v.setFechaValoracion(LocalDate.now());
            v = valoracionRepository.save(v);
        } else {
            v = new Valoracion();
            v.setRuta(ruta);
            v.setUsuario(usuario);
            v.setPuntuacion(puntuacion);
            v.setFechaValoracion(LocalDate.now());
            v = valoracionRepository.save(v);
            if (ruta.getValoraciones() != null) ruta.getValoraciones().add(v);
        }
        return v;
    }

    @Transactional
    public List<Valoracion> listByRuta(Long rutaId) {
        return listarPorRuta(rutaId);
    }

    @Transactional
    public double averageForRuta(Long rutaId) {
        List<Valoracion> list = listarPorRuta(rutaId);
        if (list.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Valoracion v : list) sum += v.getPuntuacion();
        return sum / list.size();
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) return;
        Optional<Valoracion> opt = valoracionRepository.findById(id);
        opt.ifPresent(v -> {
            Ruta r = v.getRuta();
            if (r != null && r.getValoraciones() != null) r.getValoraciones().remove(v);
            valoracionRepository.delete(v);
        });
    }

}
