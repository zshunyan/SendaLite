package unex.cume.mdai.SendaLite;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import unex.cume.mdai.SendaLite.model.Comentario;
import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.model.Dificultad;
import unex.cume.mdai.SendaLite.model.TipoActividad;
import unex.cume.mdai.SendaLite.service.ComentarioService;
import unex.cume.mdai.SendaLite.service.RutaService;
import unex.cume.mdai.SendaLite.service.UsuarioService;
import unex.cume.mdai.SendaLite.repository.ComentarioRepository;
import unex.cume.mdai.SendaLite.repository.RutaRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // ajusta si quieres H2 en memoria
@Import({ComentarioService.class, RutaService.class, UsuarioService.class})
public class ComentarioTest {

	@Autowired
	private ComentarioRepository comentarioRepository;

	@Autowired
	private ComentarioService comentarioService;

	@Autowired
	private RutaService rutaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RutaRepository rutaRepository;

	@Test
	void testPersistirComentarioYConsulta() {
		Usuario user = new Usuario();
		user.setEmail("coment@example.com");
		user.setPassword("pwd");
		user.setNombre("Usuario Coment");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Comentarios");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setComentarios(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		// Crear y persistir usando el método seguro por ids
		Comentario saved = comentarioService.create(ruta.getIdRuta(), user.getIdUsuario(), "Comentario de prueba");
		comentarioRepository.flush();

		java.util.List<Comentario> resultados = comentarioRepository.findByRutaIdRuta(ruta.getIdRuta());
		assertThat(resultados).hasSize(1);
		assertThat(resultados.get(0).getTexto()).isEqualTo("Comentario de prueba");
	}

	@Test
	void testEliminarRutaEliminaComentarios() {
		Usuario user = new Usuario();
		user.setEmail("delcoment@example.com");
		user.setPassword("pwd");
		user.setNombre("Usuario Del");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta a borrar comentarios");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setComentarios(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		// crear comentario mediante servicio (por ids)
		Comentario saved = comentarioService.create(ruta.getIdRuta(), user.getIdUsuario(), "Comentario 1");
		comentarioRepository.flush();

		Long rutaId = ruta.getIdRuta();

		rutaService.eliminarRutaPorId(rutaId);
		comentarioRepository.flush();

		java.util.List<Comentario> after = comentarioRepository.findByRutaIdRuta(rutaId);
		assertThat(after).isEmpty();
	}

	@Test
	void testCrearComentarioBasico() {
		Usuario user = new Usuario();
		user.setEmail("basiccomment@example.com");
		user.setPassword("pwd");
		user.setNombre("Basic Commenter");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Basic Comment");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setComentarios(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Comentario saved = comentarioService.create(ruta.getIdRuta(), user.getIdUsuario(), "Comentario básico");
		comentarioRepository.flush();

		java.util.List<Comentario> results = comentarioRepository.findByRutaIdRuta(ruta.getIdRuta());
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getTexto()).isEqualTo("Comentario básico");
	}

	@Test
	void testEliminarComentarioBasico() {
		Usuario user = new Usuario();
		user.setEmail("delcommentuser@example.com");
		user.setPassword("pwd");
		user.setNombre("Del Commenter");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Del Comment");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setComentarios(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Comentario saved = comentarioService.create(ruta.getIdRuta(), user.getIdUsuario(), "A eliminar");
		comentarioRepository.flush();
		Long id = saved.getIdComentario();


		Comentario toRemove = comentarioRepository.findById(id).orElse(null);
		comentarioService.eliminarComentario(toRemove);
		comentarioRepository.flush();

		Comentario shouldBeNull = comentarioRepository.findById(id).orElse(null);
		assertThat(shouldBeNull).isNull();
	}

	@Test
	void testModificarComentarioBasico() {
		Usuario user = new Usuario();
		user.setEmail("modcommentuser@example.com");
		user.setPassword("pwd");
		user.setNombre("Mod Commenter");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Mod Comment");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setComentarios(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Comentario saved = comentarioService.create(ruta.getIdRuta(), user.getIdUsuario(), "Original");
		comentarioRepository.flush();

		// modificar mediante servicio usando la API por id
		comentarioService.update(saved.getIdComentario(), "Modificado");
		Long id = saved.getIdComentario();

		Comentario found = comentarioRepository.findById(id).orElse(null);
		assertThat(found.getTexto()).isEqualTo("Modificado");
	}
}
