package in.apcfss.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import in.apcfss.entities.MyEntity;
import jakarta.transaction.Transactional;

@Repository
public interface PullBackLegacyCasesRepo extends JpaRepository<MyEntity, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set assigned=false, assigned_to=null, case_status=:backStatus, dept_code=:deptCode,dist_id=:distCode where cino=:cIno ")
	int updateRoll10( @Param("backStatus") int backStatus,@Param("deptCode") String deptCode, @Param("distCode") String distCode, @Param("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set assigned=false, assigned_to=null, case_status=:backStatus, dist_id=:distCode where cino=:cIno ")
	int updateRoll2( @Param("backStatus") int backStatus,@Param("distCode") String distCode, @Param("cIno") String cIno);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set assigned=false, assigned_to=null, case_status=:backStatus, dept_code=:deptCode where cino=:cIno ")
	int updateRollElse( @Param("backStatus") int backStatus,@Param("deptCode") String deptCode, @Param("cIno") String cIno);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_emp_assigned_dtls_log select * from ecourts_case_emp_assigned_dtls where cino=:cIno ")
	int insertBack_case_emp_assigned(@Param("cIno") String cIno);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ) "
			+ " values (:cIno , 'CASE SENT BACK' , :userId , :byName, :userId2 ,'null') ")
	int insertBack_case_activities(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("byName") InetAddress byName,@Param("userId2") String userId2 );


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "delete from ecourts_case_emp_assigned_dtls where cino=:cIno ")
	int deletecase_emp_assigned(String cIno);


}
