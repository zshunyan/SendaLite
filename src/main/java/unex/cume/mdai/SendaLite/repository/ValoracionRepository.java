package unex.cume.mdai.SendaLite.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import unex.cume.mdai.SendaLite.model.Valoracion;
import java.util.Optional;
import java.util.List;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    Optional<Valoracion> findByUsuarioIdUsuarioAndRutaIdRuta(Long idUsuario, Long idRuta);
    List<Valoracion> findByRutaIdRuta(Long idRuta);
}
