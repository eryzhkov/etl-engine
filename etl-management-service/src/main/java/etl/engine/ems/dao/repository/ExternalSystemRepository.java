package etl.engine.ems.dao.repository;

import etl.engine.ems.dao.entity.ExternalSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExternalSystemRepository extends JpaRepository<ExternalSystem, UUID> {

}
