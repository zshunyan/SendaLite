package unex.cume.mdai.SendaLite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.model.Comentario;
import unex.cume.mdai.SendaLite.model.Dificultad;
import unex.cume.mdai.SendaLite.model.TipoActividad;
import unex.cume.mdai.SendaLite.service.UsuarioService;
import unex.cume.mdai.SendaLite.service.RutaService;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;
import unex.cume.mdai.SendaLite.repository.RutaRepository;
import unex.cume.mdai.SendaLite.repository.ComentarioRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // usar BD embebida (H2) para tests aislados
@Import({UsuarioService.class, RutaService.class, TestConfig.class})
public class UsuarioTest {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RutaService rutaService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RutaRepository rutaRepository;

	@Autowired
	private ComentarioRepository comentarioRepository;

	@Test
	void testPersistenciaEnCascadaDeRutaAComentario() {
		// Crear y persistir usuario (autor)
		Usuario user = new Usuario();
		// ...ajusta setters según tu entidad Usuario...
		user.setEmail("autor@example.com");
		user.setPassword("pwd");
		user.setNombre("Autor Prueba");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		// Asegurar explícitamente el flag admin en los tests (no admin por defecto)
		user.setAdmin(false);
		usuarioService.anadirUsuario(user);

		// Crear ruta y asignar autor
		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta de prueba");
		ruta.setDescripcion("Descripción");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		Set<Comentario> comentarios = new HashSet<>();
		ruta.setComentarios(comentarios);

		// Crear comentario asociado a la ruta y al usuario
		Comentario c = new Comentario();
		c.setTexto("Buen camino");
		c.setFechaComentario(LocalDate.now());
		c.setUsuario(user);
		c.setRuta(ruta);
		ruta.getComentarios().add(c);

		// Persistir la ruta; los comentarios deben persistirse por cascade
		ruta = rutaService.anadirRuta(ruta);
 		rutaRepository.flush();

		// Comprobar que el comentario se guardó
		// Buscar la ruta por título y luego usar comentarioRepository para obtener comentarios por idRuta
		java.util.List<Ruta> rutas = rutaRepository.findByTituloContainingIgnoreCase("Ruta de prueba");
		assertThat(rutas).isNotEmpty();
		Long idRuta = rutas.get(0).getIdRuta();
		java.util.List<Comentario> comentariosPersist = comentarioRepository.findByRutaIdRuta(idRuta);
		assertThat(comentariosPersist).hasSize(1);
		assertThat(comentariosPersist.get(0).getTexto()).isEqualTo("Buen camino");
	}

	@Test
	void testEliminacionEnCascadaDeRutaAComentario() {
		// Crear y persistir usuario (autor)
		Usuario user = new Usuario();
		user.setEmail("autor2@example.com");
		user.setPassword("pwd2");
		user.setNombre("Autor Prueba 2");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		// Usuario de prueba no administrador
		user.setAdmin(false);
		usuarioService.anadirUsuario(user);

		// Crear ruta con comentario y persistir
		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta a eliminar");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		Set<Comentario> comentarios2 = new HashSet<>();
		ruta.setComentarios(comentarios2);

		Comentario c1 = new Comentario();
		c1.setTexto("Comentario 1");
		c1.setFechaComentario(LocalDate.now());
		c1.setUsuario(user);
		c1.setRuta(ruta);
		ruta.getComentarios().add(c1);

		ruta = rutaService.anadirRuta(ruta);
 		rutaRepository.flush();

		// Recuperar ruta, eliminarla y comprobar que los comentarios también se eliminan
		java.util.List<Ruta> persistedList = rutaRepository.findByTituloContainingIgnoreCase("Ruta a eliminar");
		assertThat(persistedList).isNotEmpty();
		Ruta persistedRuta = persistedList.get(0);
		Long rutaId = persistedRuta.getIdRuta();

		// eliminar
		rutaService.eliminarRutaPorId(rutaId);
		rutaRepository.flush();

		// No debe quedar ningún comentario para esa ruta
		java.util.List<Comentario> comentariosAfter = comentarioRepository.findByRutaIdRuta(rutaId);
		assertThat(comentariosAfter).isEmpty();
	}

	@Test
	void testCrearUsuarioBasico() {
		Usuario u = new Usuario();
		u.setEmail("basicuser@example.com");
		u.setPassword("pwd");
		u.setNombre("Basic User");
		u.setFechaRegistro(LocalDate.now());
		u.setActivo(true);
		// marcar como usuario normal
		u.setAdmin(false);
		usuarioService.anadirUsuario(u);
		usuarioRepository.flush();

		java.util.Optional<Usuario> found = usuarioRepository.findByEmail("basicuser@example.com");
		assertThat(found).isPresent();
		assertThat(found.get().getNombre()).isEqualTo("Basic User");
	}

	@Test
	void testEliminarUsuarioBasico() {
		Usuario u = new Usuario();
		u.setEmail("deluser@example.com");
		u.setPassword("pwd");
		u.setNombre("Del User");
		u.setFechaRegistro(LocalDate.now());
		u.setActivo(true);
		// usuario regular
		u.setAdmin(false);
		usuarioService.anadirUsuario(u);
		usuarioRepository.flush();

		java.util.Optional<Usuario> persisted = usuarioRepository.findByEmail("deluser@example.com");
		assertThat(persisted).isPresent();
		usuarioService.eliminarUsuario(persisted.get());
		usuarioRepository.flush();

		java.util.Optional<Usuario> after = usuarioRepository.findByEmail("deluser@example.com");
		assertThat(after).isEmpty();
	}

	@Test
	void testModificarUsuarioBasico() {
		Usuario u = new Usuario();
		u.setEmail("moduser@example.com");
		u.setPassword("pwd");
		u.setNombre("Original Name");
		u.setFechaRegistro(LocalDate.now());
		u.setActivo(true);
		// no admin en este caso
		u.setAdmin(false);
		usuarioService.anadirUsuario(u);
		usuarioRepository.flush();

		// modificar mediante servicio
		u.setNombre("Nombre Modificado");
		usuarioService.modificarUsuario(u);
		usuarioRepository.flush();

		java.util.Optional<Usuario> found = usuarioRepository.findByEmail("moduser@example.com");
		assertThat(found).isPresent();
		assertThat(found.get().getNombre()).isEqualTo("Nombre Modificado");
	}
}
