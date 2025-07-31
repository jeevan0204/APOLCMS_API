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
public interface StandingCouncilRegRepo extends JpaRepository<MyEntity, Long> {

 
	@Query(nativeQuery = true, value = "select employee_name,employee_code,from_date,to_date,mobile_no,email_id,adhaar_no,standard_council_path,remarks "
			+ " from standard_council_mst where dept_id=:deptCode    ")
	List<Map<String, Object>> selectStanding_council(@Param("deptCode") String deptCode);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " INSERT INTO apolcms.standard_council_mst(slno, employee_name, employee_code, from_date , to_date , mobile_no, "
			+ "  email_id, adhaar_no, standard_council_path, remarks, inserted_time,inserted_by, inserted_ip, is_delete,dept_id) "
			+ "  VALUES(:slno ,:employeeName,:employeeCode,to_date(:fromDate,'yyyy-MM-dd'),to_date(:toDate,'yyyy-MM-dd'),:mobileNo,:emailId,:aadharNo,:letterPath,:remarks,:timestamp,:userId ,CAST(:remoteAddr AS inet),'false',:deptId) ")
	int INSERTStanding_councilEMP_DATA(@Param("slno")  int slno,@Param("employeeName")  String employeeName,@Param("employeeCode")  String employeeCode,
			@Param("fromDate") String fromDate,@Param("toDate")  String toDate,@Param("mobileNo")  String mobileNo,
			@Param("emailId") String emailId,@Param("aadharNo")  String aadharNo,@Param("letterPath")  String letterPath,@Param("remarks")  String remarks,
			@Param("timestamp") Timestamp timestamp,@Param("userId")  String userId,@Param("remoteAddr") String remoteAddr,@Param("deptId")  String deptId);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_mst_gps (slno,full_name, mobile_no, department, emailid,designation, court_name, state)"
			+ " values (:gpSlno,:employeeName,:mobileNo,:dept_name,:emailId,'Standing Counsel','High Court of AP','Andhra Pradesh') ")
	int INSERT_mst_gps(@Param("gpSlno") int gpSlno,@Param("employeeName") String employeeName,@Param("mobileNo") String mobileNo,@Param("dept_name") String dept_name,@Param("emailId") String emailId);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into users (userid, password, password_text,user_description, created_by, created_on, created_ip,user_type,dept_code)"
			+ " values (:emailId,md5('olcms@123'),'olcms@123',:employeeName,:userId,now(),CAST(:remoteAddr AS inet),'29',:dept_code) ")
	int INSERT_users(@Param("emailId") String emailId,@Param("employeeName") String employeeName,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("dept_code") String dept_code);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into user_roles (userid, role_id) values (:emailId,'29') ")
	int INSERT_user_roles(@Param("emailId") String emailId);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into olcms_sms_creation (slno,ack_no,email_id,mobile_no,sms_text,inserted_time,inserted_ip,inserted_by) "
			+ "  values(:slnos,:employeeCode,:emailId,:mobileNo,:smsText,:timestamp,CAST(:remoteAddr AS inet),:userId) ")
	int INSERT_olcms_sms_creation(@Param("slnos")  int slnos,@Param("employeeCode")  String employeeCode,@Param("emailId")  String emailId,@Param("mobileNo")  String mobileNo,@Param("smsText")  String smsText,
			@Param("timestamp")	Timestamp timestamp,@Param("remoteAddr")  String remoteAddr,@Param("userId")  String userId);


}
