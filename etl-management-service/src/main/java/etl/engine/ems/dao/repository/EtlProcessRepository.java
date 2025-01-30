package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.EtlProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EtlProcessRepository extends JpaRepository<EtlProcess, UUID> {

}