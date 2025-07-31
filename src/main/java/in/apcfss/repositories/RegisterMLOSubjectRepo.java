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
public interface RegisterMLOSubjectRepo extends JpaRepository<MyEntity, Long> {
	
	@Query(nativeQuery = true, value = "select distinct designation_id::int4 as value , designation_name_en as label from nic_data where substring(global_org_name,1,5)=:deptCode "
			+ " and trim(upper(designation_name_en))<>'MINISTER' order by designation_name_en asc   ")
	List<Map<String, Object>> getDesignationList(@Param("deptCode") String deptCode);

	@Query(nativeQuery = true, value = " select slno, user_id, designation, employeeid, mobileno, emailid, aadharno, b.fullname_en, designation_name_en from mlo_details a "
			+ " inner join (select distinct employee_id,designation_id,designation_name_en,fullname_en from nic_data) b on (a.employeeid=b.employee_id and a.designation=b.designation_id)"
			+ "  where a.user_id=:deptCode   ")
	List<Map<String, Object>> getRegisterMLOSubjectList(@Param("deptCode") String deptCode);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into mlo_subject_details (user_id, designation, employeeid, mobileno, emailid, aadharno, inserted_by, inserted_ip, inserted_time, subject_desc) "
			+ " values (:deptCode   ,:designationId,:employeeId,:mobileNo,:emailId,:aadharNo,:userId, CAST(:remoteAddr AS inet),now(),:subjectDesc ) ")
	int insert_mlo_subject_details(@Param("deptCode") String deptCode, @Param("designationId")  String designationId,@Param("employeeId")  String employeeId,
			@Param("mobileNo") String mobileNo,@Param("emailId")  String emailId,@Param("aadharNo")  String aadharNo,@Param("userId")  String userId,@Param("remoteAddr")  String remoteAddr,
			@Param("subjectDesc") String subjectDesc); 
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into users (userid, password,password_text, user_description, created_by, created_on, created_ip, dept_id , dept_code,user_type)  "
			+ " select a.emailid, md5('olcms@123'),'olcms@123', b.fullname_en,:userId,now(),CAST(:remoteAddr AS inet),:deptId,:deptCode,'15'  "
			+ " from mlo_details a inner join (select distinct employee_id,designation_id,designation_name_en,fullname_en from nic_data) b on (a.employeeid=b.employee_id and a.designation=b.designation_id) "
			+ " where employeeid=:employeeId and a.designation=:designationId  ")
	int insert_usersMLO(@Param("userId")  String userId,@Param("remoteAddr")   String remoteAddr,@Param("deptId") int deptId,@Param("deptCode")   String deptCode,@Param("employeeId")   String employeeId,@Param("designationId")   String designationId);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into user_roles (userid, role_id) values (:emailId,'4')")
	int insert_user_rolesMLO(@Param("emailId") String emailId);

	
	
}
