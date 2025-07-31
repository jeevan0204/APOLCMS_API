package in.apcfss.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.SMSCreationEntity;


@Repository
public interface SMSCreationRepo extends JpaRepository<SMSCreationEntity, Long>{
	

}
