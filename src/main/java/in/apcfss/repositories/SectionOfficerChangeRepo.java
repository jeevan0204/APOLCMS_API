package in.apcfss.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.apcfss.entities.SectionOfficerChangeMst;

public interface SectionOfficerChangeRepo extends JpaRepository<SectionOfficerChangeMst, Integer> {

	//SectionOfficerChangeMst saveDetails(SectionOfficerChangeMst changeMstEntity);

}
