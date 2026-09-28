package br.insper.chouse.pool.repository;

import br.insper.chouse.pool.model.Enquete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnqueteRepository extends JpaRepository<Enquete, Long> {
}
