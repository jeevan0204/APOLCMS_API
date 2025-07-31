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
public interface RegisterNodalOfficerRepo extends JpaRepository<MyEntity, Long> {

	@Query(nativeQuery = true, value = "select dept_code,dept_code||'-'||upper(trim(description)) as description  "
			+ " from dept_new where display=true and (reporting_dept_code=:deptCode or deptcode=:deptCode  ) and deptcode!='01'  "
			+ " and  dept_code not in (select dept_id from  nodal_officer_details where coalesce(dist_id,0)=0 ) order by sdeptcode,deptcode ")
	List<Map<String, Object>> getNodalDepartmentList(@Param("deptCode") String deptCode);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into nodal_officer_details (dept_id, user_id, designation, employeeid, mobileno, emailid, aadharno, inserted_by, inserted_ip, inserted_time, dist_id)  "
			+ " values (:deptCode , :userId ,:designationId,:employeeId,:mobileNo,:emailId,:aadharNo,:userId2, CAST(:remoteAddr AS inet),now(), :distId) ")
	int insert_nodal_officer_details(@Param("deptCode") String deptCode,@Param("userId")  String userId,@Param("designationId")  String designationId,@Param("employeeId")  String employeeId,
			@Param("mobileNo") String mobileNo,@Param("emailId")  String emailId,@Param("aadharNo")  String aadharNo,@Param("userId2")  String userId2,@Param("remoteAddr")  String remoteAddr,@Param("distId")  int distId);


	@Query(nativeQuery = true, value = "select count(*) from users where userid=:emailId ")
	int usersCount(@Param("emailId") String emailId);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into users_log select * from users where userid=:emailId ")
	int users_log(@Param("emailId") String emailId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "delete from user_roles where userid=:emailId ")
	int delete_user_roles(@Param("emailId") String emailId);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "delete from users where userid=:emailId ")
	int delete_users(@Param("emailId") String emailId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into user_roles (userid, role_id) values (:emailId ,'5') ")
	int insert_user_rolesRegisterNodal(@Param("emailId") String emailId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into user_roles (userid, role_id) values (:emailId ,'10') ")
	int insert_user_roles10RegisterNodal(@Param("emailId") String emailId);

	
	
	

	/*
	 * @Modifying
	 * 
	 * @Transactional
	 * 
	 * @Query(nativeQuery = true, value =
	 * " insert into users (userid, password,password_text, user_description, created_by, created_on, created_ip, dept_id, dept_code, user_type) "
	 * +
	 * " select a.emailid, md5('olcms@123'),'olcms@123', b.fullname_en,:userId , now(),CAST(:remoteAddr AS inet),d.dept_id,d.sdeptcode||d.deptcode as deptcode, 5 from nodal_officer_details a "
	 * +
	 * " inner join (select distinct employee_id,fullname_en,designation_id,designation_name_en, substr(global_org_name,1,5) as dept_code  from nic_data) b on (a.employeeid=b.employee_id and a.dept_id=b.dept_code and a.designation=b.designation_id) "
	 * + " inner join dept_new d on (d.dept_code=b.dept_code)  " +
	 * " where employeeid=:employeeId and a.dept_id=:deptCode     ") int
	 * insert_usersRegisterNodal(@Param("userId") String userId,@Param("remoteAddr")
	 * String remoteAddr,@Param("employeeId") String employeeId,@Param("deptCode")
	 * String deptCode);
	 */
	
}
