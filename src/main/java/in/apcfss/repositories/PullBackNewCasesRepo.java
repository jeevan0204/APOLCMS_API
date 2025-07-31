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
public interface PullBackNewCasesRepo extends JpaRepository<MyEntity, Long> {
 
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set assigned=false, assigned_to=null, case_status=:backStatus , dept_code=:deptCode ,dist_id=:distCode"
			+ "  where ack_no=:ackNo and respondent_slno=:respondentId ")
	int update_ack_deptsRole10(@Param("backStatus") int backStatus,@Param("deptCode")  String deptCode,@Param("distCode")  int distCode,@Param("ackNo")  String ackNo,@Param("respondentId")  int respondentId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set assigned=false, assigned_to=null, case_status=:backStatus , dist_id=:distCode"
			+ "  where ack_no=:ackNo and respondent_slno=:respondentId ")
	int update_ack_deptsRole2(@Param("backStatus") int backStatus, @Param("distCode")  int distCode,@Param("ackNo")  String ackNo,@Param("respondentId")  int respondentId);


	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set assigned=false, assigned_to=null, case_status=:backStatus , dept_code=:deptCode"
			+ "  where ack_no=:ackNo and respondent_slno=:respondentId ")
	int update_ack_deptsRoleELSE(@Param("backStatus") int backStatus,@Param("deptCode")  String deptCode,@Param("ackNo")  String ackNo,@Param("respondentId")  int respondentId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_emp_assigned_dtls_log select * from ecourts_case_emp_assigned_dtls where cino=:ackNo ")
	int insert_ecourts_case_emp_assigned_dtls_log(@Param("ackNo")  String ackNo);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks )"
			+ "  values (:ackNo,'CASE SENT BACK',:userid,CAST(:remoteAddr AS inet),:userid2,NULL  ) ")
	int insert_ecourts_case_activities(@Param("ackNo") String ackNo,@Param("userid")  String userid,@Param("remoteAddr")  String remoteAddr,@Param("userid2")  String userid2);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "delete from ecourts_case_emp_assigned_dtls where cino=:ackNo ")
	int delete_ecourts_case_emp_assigned_dtls(@Param("ackNo") String ackNo);

	
}
