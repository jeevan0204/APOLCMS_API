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
public interface LegacyAssignmentRepo extends JpaRepository<MyEntity, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into apolcms.ecourts_case_activities "
			+ "(cino , action_type , inserted_by , inserted_ip, assigned_to , remarks) "
			+ " values(:cino, 'CASE ASSSIGNED', :userId, :remoteAddr, :assignedto,  NULL )")
	Integer insertEcourtsCaseActivitiesHOD(@Param("cino") String cino, @Param("action_type") String action_type,
			@Param("userId") String userId, @Param("remoteAddr") InetAddress remoteAddr, 
			@Param("assignedto") String assignedto, @Param ("remarks") String remarks);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into apolcms.ecourts_case_activities "
			+ "(cino , action_type , inserted_by , inserted_ip, assigned_to , remarks) "
			+ " values(:cino, 'CASE ASSSIGNED TO MLO (SUBJECT)', :userId, :remoteAddr, :assignedto,  NULL )")
	Integer insertEcourtsCaseActivitiesMLO(@Param("cino") String cino, @Param("action_type") String action_type,
			@Param("userId") String userId, @Param("remoteAddr") InetAddress remoteAddr, 
			@Param("assignedto") String assignedto, @Param ("remarks") String remarks);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value= "update ecourts_case_data set dept_id=:assign2deptId, dept_code=:caseDept,case_status=:case_status where cino in (:selectedCaseIds)")
	int updateValuesDeptHOD(@Param("assign2deptId") int assign2deptId,@Param("caseDept") String caseDept,@Param("case_status") int case_status ,@Param("selectedCaseIds") String selectedCaseIds);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status='12', assigned_to=:assigned_to where cino in (:cino )")
	int updateValuesDeptMLO(@Param("assigned_to") String assigned_to,@Param("cino") String cino);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into apolcms.ecourts_case_emp_assigned_dtls "
			+ "(cino , dept_code , emp_section , emp_post, emp_id , inserted_time,inserted_ip,inserted_by,emp_user_id) "
			+ " values(:cIno , :empDept , :empSection , :empPost, :employeeId , :timestamp,:byName,:userId,:emailId )")
	Integer insertEcourts_Case_Emp_Assigned_Dtls(@Param("cIno") String cIno, @Param("empDept") String empDept, @Param("empSection") String empSection,
			@Param("empPost") String empPost,@Param("employeeId") String employeeId,@Param("timestamp") Timestamp  timestamp,
			@Param("byName") InetAddress byName,   @Param ("userId") String userId, @Param ("emailId") String emailId);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value ="update ecourts_case_data set dept_id=:assign2deptId,dept_code=:empDeptCode,assigned=true,assigned_to=:emailId,case_status=:newStatusCodeInt,dist_id=:distCode  where TRIM(cino) = TRIM(:cIno)")
	int updateValuesMultiCaseToSection(@Param("assign2deptId") int assign2deptId,@Param("empDeptCode") String empDeptCode,@Param("emailId") String emailId, @Param("newStatusCodeInt") int newStatusCodeInt,
			@Param("distCode") int distCode,@Param("cIno") String cIno);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value="insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id) "
			+ " values (:cIno,:activityDesc,:userId ,:byName,:emailId,:caseRemarks,:distCode) ")
	int insertecourtsCaseActivitiesMultiCaseToSection(@Param("cIno") String cIno, @Param("activityDesc") String activityDesc,@Param("userId") String userId,
			@Param("byName") InetAddress byName, @Param("emailId") String emailId, @Param("caseRemarks") String caseRemarks,@Param("distCode") int distCode);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into user_roles (userid, role_id)  values(:emailId, :newRoleId)")
	int insertUsersRoles(@Param("emailId") String emailId, @Param("newRoleId") int newRoleId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id)"
			+ " values (:newCaseId,'CASE ASSSIGNED',:userId ,:byName,:caseDistSame,'null',:caseDist) ")
	int insertActivityDC(@Param("newCaseId") String newCaseId, @Param("userId")  String userId,@Param("byName") InetAddress byName,@Param("caseDistSame") int caseDistSame,@Param("caseDist") int caseDist);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id)"
			+ " values (:newCaseId,'CASE ASSSIGNED',:userId ,:byName,:caseDept,'null',:caseDist) ")
	int  insertActivityDCNO(@Param("newCaseId") String newCaseId, @Param("userId") String userId, @Param("byName") InetAddress byName, @Param("caseDept") String caseDept, @Param("caseDist") int caseDist);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  dist_id=:caseDist,case_status=7  where cino in (:selectedCaseIds)")
	int  updateDC(@Param("caseDist") int caseDist,@Param("selectedCaseIds") String selectedCaseIds);

	
	@Query(nativeQuery = true, value = "select dept_id from dept_new where dept_code=:caseDept")
	int getDeptCodeByDeptName(@Param("caseDept") String caseDept);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set dept_id=:assign2deptId,dist_id=:caseDist,dept_code=:distDept,case_status=8  where cino in (:selectedCaseIds)")
	int updateDCNO(@Param("assign2deptId") int assign2deptId,@Param("caseDist") int caseDist,@Param("distDept") String distDept,@Param("selectedCaseIds") String selectedCaseIds);

	
			@Query(nativeQuery = true, value = "select count(*) from users where trim(userid)=:emailId")
	int getUserCountByEmail(@Param("emailId") String emailId);



}
