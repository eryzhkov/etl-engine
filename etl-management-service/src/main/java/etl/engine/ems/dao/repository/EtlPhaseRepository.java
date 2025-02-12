package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtlPhaseRepository extends JpaRepository<EtlPhase, Integer> {

    Optional<EtlPhase> findEtlPhaseByCode(String code);
}