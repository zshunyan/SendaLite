package unex.cume.mdai.SendaLite.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import unex.cume.mdai.SendaLite.model.Comentario;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByRutaIdRuta(Long idRuta);
    List<Comentario> findByUsuarioIdUsuario(Long idUsuario);
}
