package unex.cume.mdai.SendaLite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor: acepta opcionalmente PasswordEncoder para permitir tests con @DataJpaTest
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, Optional<PasswordEncoder> passwordEncoderOptional) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoderOptional.orElse(null);
    }

    @Transactional
    public Usuario anadirUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuario nulo");
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) throw new IllegalArgumentException("Email requerido");
        Optional<Usuario> exists = usuarioRepository.findByEmail(usuario.getEmail());
        if (exists.isPresent()) throw new IllegalArgumentException("Email ya registrado: " + usuario.getEmail());
        if (usuario.getFechaRegistro() == null) usuario.setFechaRegistro(LocalDate.now());
        // encode password if encoder available
        if (usuario.getPassword() != null && passwordEncoder != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    // Métodos compatibles con controller
    @Transactional
    public Usuario create(Usuario usuario) {
        return anadirUsuario(usuario);
    }

    @Transactional
    public List<Usuario> listAll() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario update(Long id, Usuario usuario) {
        if (id == null || usuario == null) throw new IllegalArgumentException("Id o usuario nulo");
        usuario.setIdUsuario(id);
        return modificarUsuario(usuario);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUsuarioPorId(id);
    }

    @Transactional
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario modificarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) throw new IllegalArgumentException("Usuario o id nulo");
        // Validar unicidad de email: si existe otro usuario con el mismo email y distinto id -> error
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) throw new IllegalArgumentException("Email requerido");
        var maybe = usuarioRepository.findByEmail(usuario.getEmail());
        if (maybe.isPresent() && !maybe.get().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("Email ya registrado: " + usuario.getEmail());
        }

        // Cargar entidad existente y copiar solo campos editables
        Usuario existente = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        existente.setNombre(usuario.getNombre() != null ? usuario.getNombre() : existente.getNombre());
        existente.setEmail(usuario.getEmail());
        existente.setAdmin(usuario.isAdmin());
        // Si se proporciona contraseña en el payload, actualizarla (codificando si hay encoder)
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            if (passwordEncoder != null) existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
            else existente.setPassword(usuario.getPassword());
        }
        // conservar fechaRegistro, activo, avatar y relaciones tal como estaban
        return usuarioRepository.save(existente);
    }

    @Transactional
    public void eliminarUsuario(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public boolean eliminarUsuarioPorId(Long idUsuario) {
        if (idUsuario == null) return false;
        Optional<Usuario> u = usuarioRepository.findById(idUsuario);
        if (u.isEmpty()) return false;
        usuarioRepository.delete(u.get());
        return true;
    }
}
