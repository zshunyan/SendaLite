package unex.cume.mdai.SendaLite.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import unex.cume.mdai.SendaLite.model.Ruta;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByTituloContainingIgnoreCase(String titulo);

    // Evita N+1: traer las rutas con su autor en una sola consulta
    @Query("select r from Ruta r join fetch r.autor")
    List<Ruta> findAllWithAutor();

    // Traer una ruta junto con autor, comentarios (y sus usuarios) y valoraciones (y sus usuarios)
    @Query("select distinct r from Ruta r " +
           "left join fetch r.comentarios c " +
           "left join fetch c.usuario cu " +
           "left join fetch r.valoraciones v " +
           "left join fetch v.usuario vu " +
           "join fetch r.autor where r.idRuta = :id")
    Optional<Ruta> findByIdWithDetalles(@Param("id") Long id);
}
