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
public interface AssignmentAndNewCasesRepo extends JpaRepository<MyEntity, Long> {



	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks )"
			+ " values (:splitId,'CASE ASSSIGNED',:userId ,CAST(:remoteAddr AS inet),:caseDept,NULL) ")
	int ecourts_case_activitiesAssign2DeptHOD(@Param("splitId") String splitId,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("caseDept") String caseDept);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "INSERT INTO apolcms.ecourts_gpo_ack_depts_log(ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id)"
			+ " SELECT ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id "
			+ " FROM apolcms.ecourts_gpo_ack_depts  where ack_no in (:splitId1) and respondent_slno=:splitId2 ")
	int ecourts_gpo_ack_depts_logAssign2DeptHOD(@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update ecourts_gpo_ack_depts set  dept_code=:caseNewDept  ,case_status=:newStatusCode where ack_no in (:splitId1) and respondent_slno=:splitId2 ")
	int ecourts_gpo_ack_deptsAssign2DeptHOD(@Param("caseNewDept") String caseNewDept,@Param("newStatusCode") int newStatusCode,@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " insert into apolcms.ecourts_ack_assignment_dtls"
			+ " (ackno , dept_code , emp_section , emp_post, emp_id , inserted_time,inserted_ip,inserted_by,emp_user_id) "
			+ " values(:splitId1 , :empDept , :empSection , :empPost, :employeeId , :timestamp,CAST(:remoteAddr AS inet),:userId,:emailId ) ")
	int insertEcourts_ack_assignment_dtls(@Param("splitId1") String splitId1,@Param("empDept") String empDept,@Param("empSection") String empSection,@Param("empPost") String empPost,
			@Param("employeeId") String employeeId,@Param("timestamp") Timestamp timestamp,@Param("remoteAddr") String remoteAddr,@Param("userId") String userId,@Param("emailId") String emailId);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "INSERT INTO apolcms.ecourts_gpo_ack_depts_log(ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id)"
			+ "   SELECT ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id "
			+ "   FROM apolcms.ecourts_gpo_ack_depts where ack_no=:splitId1 and respondent_slno=:splitId2  ")
	int ecourts_gpo_ack_depts_logAssignMultiCasesToSection(@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update ecourts_gpo_ack_depts set dept_code=:empDeptCode , assigned=true, assigned_to=:emailId ,case_status=:newStatusCode , "
			+ "  dist_id=:caseDist1  where ack_no=:splitId1  and respondent_slno=:splitId2  ")
	int ecourts_gpo_ack_deptsAssignMultiCasesToSectionDC(@Param("empDeptCode") String empDeptCode,@Param("emailId") String emailId,@Param("newStatusCode") int newStatusCode,
			@Param("caseDist1")	int caseDist1,@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update ecourts_gpo_ack_depts set dept_code=:empDeptCode , assigned=true, assigned_to=:emailId ,case_status=:newStatusCode , "
			+ "  dist_id=:caseDist1  where ack_no=:splitId1  and respondent_slno=:splitId2  ")
	int ecourts_gpo_ack_deptsAssignMultiCasesToSectionDCNO(@Param("empDeptCode") String empDeptCode,@Param("emailId") String emailId,@Param("newStatusCode") int newStatusCode,
			@Param("caseDist1")	int caseDist1,@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update ecourts_gpo_ack_depts set dept_code=:empDeptCode , assigned=true, assigned_to=:emailId ,case_status=:newStatusCode , "
			+ "  dist_id=:caseDist1  where ack_no=:splitId1  and respondent_slno=:splitId2  ")
	int ecourts_gpo_ack_deptsAssignMultiCasesToSectionELSE(@Param("empDeptCode") String empDeptCode,@Param("emailId") String emailId,@Param("newStatusCode") int newStatusCode,
			@Param("caseDist1")	int caseDist1,@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);

	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id)"
			+ " values (:splitId1,:activityDesc,:userId,CAST(:remoteAddr AS inet),:emailId, :caseRemarks,:caseDist1 )  ")
	int ecourts_case_activitiesAssignMultiCasesToSection(@Param("splitId1") String splitId1,@Param("activityDesc") String activityDesc,@Param("userId") String userId,
			@Param("remoteAddr") String remoteAddr,@Param("emailId") String emailId,@Param("caseRemarks") String caseRemarks,@Param("caseDist1") int caseDist1);

	
	@Query(nativeQuery = true, value = "select count(*) from users where trim(userid)=:emailId")
	String userCount(@Param("emailId") String emailId);

	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into user_roles (userid, role_id)  values(:emailId, :newRoleId)")
	int insertUsersRoles(@Param("emailId") String emailId, @Param("newRoleId") int newRoleId);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id) "
			+ "   values (:splitId1,'CASE ASSSIGNED',:userId,CAST(:remoteAddr AS inet),:caseDist1,NULL,:caseDist2   ) ")
	int insertActivitiesDC(@Param("splitId1") String splitId1,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("caseDist1") String caseDist1,
			@Param("caseDist2") int caseDist2);

	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id) "
			+ "   values ( :splitId1,'CASE ASSSIGNED',:userId,CAST(:remoteAddr AS inet),:distDept,NULL,:caseDist1 ) ")
	int insertActivitiesDCNO(@Param("splitId1") String splitId1,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("distDept") String distDept,
			@Param("caseDist1") int caseDist1);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " INSERT INTO apolcms.ecourts_gpo_ack_depts_log(ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id) "
			+ "  (SELECT ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id"
			+ "  FROM apolcms.ecourts_gpo_ack_depts  where ack_no in (:splitId1 ) and dist_id=:caseDist  and respondent_slno=:splitId2) ")
	int insertAck_depts_logDC(@Param("splitId1") String splitId1,@Param("caseDist") int caseDist,@Param("splitId2") int splitId2);

	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " update ecourts_gpo_ack_depts set case_status=7, dist_id=:caseDist,dept_code=:distDept  where ack_no in (:splitId1 )   and respondent_slno=:splitId2 ")
	int updateAck_ack_deptsDC(@Param("caseDist") int caseDist,@Param("distDept") String distDept,@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " INSERT INTO apolcms.ecourts_gpo_ack_depts_log(ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id)"
			+ "  (SELECT ack_no, dept_code, respondent_slno, assigned, assigned_to, case_status, dist_id "
			+ "  FROM apolcms.ecourts_gpo_ack_depts where ack_no in (:splitId1 ) and respondent_slno=:splitId2 ) ")
	int insertAck_depts_logDCNO(@Param("splitId1") String splitId1,@Param("splitId2") int splitId2);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " update ecourts_gpo_ack_depts set dept_code=:distDept,dist_id=:caseDist,case_status=8   where ack_no in (:splitId1 )   and respondent_slno=:splitId2 ")
	int updateAck_ack_deptsDCNO(@Param("distDept") String distDept,@Param("caseDist") int caseDist, @Param("splitId1") String splitId1,@Param("splitId2") int splitId2);
 

}
