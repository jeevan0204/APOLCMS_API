package in.apcfss.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.MyEntity;

@Repository
public interface PPOAcknowledgementRepo extends JpaRepository<MyEntity, Long> {

	@Query(nativeQuery = true, value = " select case_type as value,case_type_fullform as label from ecourts_case_type_master_new where (case_type like '%CRL%'  or case_type='RT')  and case_type!='TRCRLA' ")
	List<Map<String, Object>> getCaseTypeListPP();

	@Query(nativeQuery = true, value = " select dept_code as value,dept_code||' - '||upper(description) as label from dept_new where display=true and dept_code like '%LAW03%' OR dept_code like '%HOM%'  order by dept_code ")
	List<Map<String, Object>> getDepartmentListPP();

	@Query(nativeQuery = true, value = " select upper(trim(b.police_station_name)) as value,upper(trim(b.police_station_name))  as label "
			+ " from police_station_mst b where b.district_id=:distIdInt   ORDER BY  upper(trim(b.police_station_name)) asc ")
	List<Map<String, Object>> getStationListPP(@Param("distIdInt") int distIdInt);

	@Query(nativeQuery = true, value = " select distinct upper(trim(b.seb_name)) as value,upper(trim(b.seb_name))  as label from seb_station_mst b where b.district_code=CAST(:distIdInt AS VARCHAR)   ORDER BY  upper(trim(b.seb_name)) asc ")
	List<Map<String, Object>> getSebStationListPP(@Param("distIdInt") int distIdInt);

	@Query(nativeQuery = true, value = " select sno as value,upper(trim( case_full_name)) as label from case_type_master where sno in ('31','75','79','29','39','67','70','93','48','41','54','37','30','51','47','110','131','132','133','134','38','135','136') ")
	List<Map<String, Object>> getNaturePetitionListPP();


	 
}
