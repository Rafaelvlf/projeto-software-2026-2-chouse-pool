package br.insper.chouse.pool.repository;

import br.insper.chouse.pool.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsByUsuarioIdAndEnqueteId(Long usuarioId, Long enqueteId);
}
