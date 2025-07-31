package in.apcfss.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import in.apcfss.entities.MyEntity;
import jakarta.transaction.Transactional;

@Repository
public interface EcourtsCaseSearchRepo extends JpaRepository<MyEntity, Long> {

	@Query(nativeQuery = true, value = "select  upper(trim(case_type)) as value,upper(trim(case_type)) as label from ecourts_case_type_master_new order by sno  ")
	List<Map<String, Object>> getCaseTypesListShrtNEW();

	@Query(nativeQuery = true, value = "select DISTINCT reg_year as value,reg_year as label from ecourts_case_data a inner join ecourts_case_type_master_new b on (a.type_name_reg=b.case_type)"
			+ "  where b.case_type=:caseType ORDER BY reg_year DESC  ")
	List<Map<String, Object>> getYearbyCasetypes(@Param("caseType") String caseType);

	@Query(nativeQuery = true, value = "select DISTINCT reg_no as value,reg_no as label from ecourts_case_data a "
			+ "inner join ecourts_case_type_master_new b on (a.type_name_reg=b.case_type) where b.case_type=:caseType  and a.reg_year=:year ORDER BY reg_no DESC ")
	List<Map<String, Object>> getNumberbyYear(@Param("caseType") String caseType,@Param("year")  int year);

	
}
