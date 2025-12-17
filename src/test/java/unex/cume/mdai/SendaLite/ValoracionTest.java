package unex.cume.mdai.SendaLite;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.PersistenceException;
import unex.cume.mdai.SendaLite.model.Dificultad;
import unex.cume.mdai.SendaLite.model.Ruta;
import unex.cume.mdai.SendaLite.model.TipoActividad;
import unex.cume.mdai.SendaLite.model.Usuario;
import unex.cume.mdai.SendaLite.model.Valoracion;
import unex.cume.mdai.SendaLite.service.ValoracionService;
import unex.cume.mdai.SendaLite.service.UsuarioService;
import unex.cume.mdai.SendaLite.service.RutaService;
import unex.cume.mdai.SendaLite.repository.ValoracionRepository;
import unex.cume.mdai.SendaLite.repository.UsuarioRepository;
import unex.cume.mdai.SendaLite.repository.RutaRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // usar BD embebida (H2) para tests aislados
@Import({ValoracionService.class, UsuarioService.class, RutaService.class})
public class ValoracionTest {

	@Autowired
	private ValoracionRepository valoracionRepository;

	@Autowired
	private ValoracionService valoracionService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RutaService rutaService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RutaRepository rutaRepository;

	@Test
	void testUnicidadUsuarioRuta() {
		Usuario user = new Usuario();
		user.setEmail("unique@example.com");
		user.setPassword("pwd");
		user.setNombre("Unique User");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Unique");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setValoraciones(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Valoracion v1 = new Valoracion();
		v1.setPuntuacion(9);
		v1.setFechaValoracion(LocalDate.now());
		v1.setUsuario(user);
		v1.setRuta(ruta);
		valoracionService.anadirValoracion(v1);
		valoracionRepository.flush();

		Valoracion v2 = new Valoracion();
		v2.setPuntuacion(8);
		v2.setFechaValoracion(LocalDate.now());
		v2.setUsuario(user);
		v2.setRuta(ruta);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			// la violación de unicidad puede ocurrir al persistir o al hacer flush,
			// por eso envolvemos ambas operaciones
			valoracionService.anadirValoracion(v2);
			valoracionRepository.flush();
		});
	}

	@Test
	void testPersistirValoracionYConsulta() {
		Usuario user = new Usuario();
		user.setEmail("persist@example.com");
		user.setPassword("pwd");
		user.setNombre("Persist User");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Persist");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setValoraciones(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Valoracion v = new Valoracion();
		v.setPuntuacion(7);
		v.setFechaValoracion(LocalDate.now());
		v.setUsuario(user);
		v.setRuta(ruta);
		valoracionService.anadirValoracion(v);

		valoracionRepository.flush();

		List<Valoracion> list = valoracionRepository.findByRutaIdRuta(ruta.getIdRuta());
		assertThat(list).hasSize(1);
		assertThat(list.get(0).getPuntuacion()).isEqualTo(7);
	}

	@Test
	void testCrearValoracionBasica() {
		// Crear usuario y ruta
		Usuario user = new Usuario();
		user.setEmail("basicval@example.com");
		user.setPassword("pwd");
		user.setNombre("Basic Val");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Basic");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setValoraciones(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Valoracion v = new Valoracion();
		v.setPuntuacion(6);
		v.setFechaValoracion(LocalDate.now());
		v.setUsuario(user);
		v.setRuta(ruta);
		valoracionService.anadirValoracion(v);
		valoracionRepository.flush();
		Long id = v.getIdValoracion();

		Valoracion found = valoracionRepository.findById(id).orElse(null);
		assertThat(found).isNotNull();
		assertThat(found.getPuntuacion()).isEqualTo(6);
	}

	@Test
	void testEliminarValoracionBasica() {
		Usuario user = new Usuario();
		user.setEmail("delval@example.com");
		user.setPassword("pwd");
		user.setNombre("Del Val");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Del Val");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setValoraciones(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Valoracion v = new Valoracion();
		v.setPuntuacion(5);
		v.setFechaValoracion(LocalDate.now());
		v.setUsuario(user);
		v.setRuta(ruta);
		valoracionService.anadirValoracion(v);
		valoracionRepository.flush();
		Long id = v.getIdValoracion();

		Valoracion toRemove = valoracionRepository.findById(id).orElse(null);
		valoracionService.eliminarValoracion(toRemove);
		valoracionRepository.flush();

		Valoracion shouldBeNull = valoracionRepository.findById(id).orElse(null);
		assertThat(shouldBeNull).isNull();
	}

	@Test
	void testModificarValoracionBasica() {
		Usuario user = new Usuario();
		user.setEmail("modval@example.com");
		user.setPassword("pwd");
		user.setNombre("Mod Val");
		user.setFechaRegistro(LocalDate.now());
		user.setActivo(true);
		user = usuarioService.anadirUsuario(user);

		Ruta ruta = new Ruta();
		ruta.setTitulo("Ruta Mod Val");
		ruta.setDescripcion("Desc");
		ruta.setFechaCreacion(LocalDate.now());
		ruta.setActiva(true);
        ruta.setDificultad(Dificultad.MEDIA);
        ruta.setTipoActividad(TipoActividad.SENDERISMO);
		ruta.setAutor(user);
		ruta.setValoraciones(new HashSet<>());
		ruta = rutaService.anadirRuta(ruta);

		Valoracion v = new Valoracion();
		v.setPuntuacion(4);
		v.setFechaValoracion(LocalDate.now());
		v.setUsuario(user);
		v.setRuta(ruta);
		valoracionService.anadirValoracion(v);
		valoracionRepository.flush();

		// modificar mediante servicio
		v.setPuntuacion(10);
		valoracionService.modificarValoracion(v);
		Long id = v.getIdValoracion();
		Valoracion updated = valoracionRepository.findById(id).orElse(null);
		assertThat(updated.getPuntuacion()).isEqualTo(10);
	}
}
