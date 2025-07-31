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
public interface EcourtsCaseMappingRepo extends JpaRepository<MyEntity, Long> {

	
	@Query(nativeQuery = true, value = "select  cino from ecourts_case_data where type_name_reg=:case_type  and reg_year=:case_year and reg_no=:case_number   ")
	List<Map<String, Object>> getCinoDetails(@Param("case_type") String case_type,@Param("case_year")  int case_year,@Param("case_number")  int case_number);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_dtls set maincaseno=:mainCaseNo , maincaseno_updated=:mainCaseNo2 , ackno_updated=true where ack_no=:ackNo ")
	int getsubmitDetailsMappingNewTable(@Param("mainCaseNo") String mainCaseNo,@Param("mainCaseNo2")  String mainCaseNo2,@Param("ackNo")  String ackNo);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set new_ack_updated=true,ack_no=:ackNo where type_name_reg=:caseType and reg_no=:mainCase and reg_year=:regyear ")
	int getsubmitDetailsMappingLegacyTable(String ackNo, String caseType, int mainCase, int regyear);

	
}
