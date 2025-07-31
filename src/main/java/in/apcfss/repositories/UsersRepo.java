package in.apcfss.repositories;


import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import in.apcfss.entities.MyEntity;
import jakarta.transaction.Transactional;

@Repository
public interface UsersRepo extends JpaRepository<MyEntity, Long> {

	@Query(nativeQuery = true, value = """
			SELECT u.userid, user_description, user_type, u.dept_id, u.dept_code, u.dist_id
			FROM users u inner join user_roles ur on ur.userid=u.userid where u.userid=:givenuseId
						""")
	List<Map<String, Object>> getEmployeeData(String givenuseId);

	@Query(nativeQuery = true, value = "select count(*) from users where userid=:username")
	int getExistedUser(String username);

	

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "insert into user_roles (userid, role_id) values (:empid,:newRoleId)")
	int insertUserRoles(String empid, int newRoleId);

	@Query(nativeQuery = true, value = "select dept_code as dept_id ,dept_code||' - '||upper(description) as dept_name from dept_new where display=true order by dept_code")
	List<Map<String, Object>> getDepartmentList();
	
	
	@Query(nativeQuery = true, value = "select dept_code as dept_id,dept_code||'-'||upper(description) as dept_name from dept_new where display=true and substr(dept_code::text,4,5)!='01'  order by dept_code")
	List<Map<String, Object>> getDepartmentList1();
	
	@Query(nativeQuery = true, value = "select dept_code as dept_id,dept_code||'-'||upper(description) as dept_name from dept_new where display=true and reporting_dept_code=:deptCode  or dept_code=:deptCode   order by dept_code")
	List<Map<String, Object>> getDepartmentList2(String deptCode);
	
	
	@Query(nativeQuery = true, value = "select dept_code as dept_id ,dept_code||' - '||upper(description) as dept_name from dept_new where display=true and dept_code=:deptCode  order by dept_code")
	List<Map<String, Object>> getDepartmentList(String deptCode);
	 

	@Query(nativeQuery = true, value = "select district_id as value,upper(trim(district_name))  as label from district_mst order by trim(district_name)")
	List<Map<String, Object>> getDistrictList();

	@Query(nativeQuery = true, value = "select district_id as value,upper(trim(district_name))  as label from district_mst order by trim(district_name)")
	List<Map<String, Object>> getMandalList();

	@Query(nativeQuery = true, value = "select district_id as value,upper(trim(district_name))  as label from district_mst order by trim(district_name)")
	List<Map<String, Object>> getVillageList();

	@Query(nativeQuery = true, value = "select distinct designation_id as value,upper(trim(designation_name_en))  as label from nic_Data where designation_id is not null and designation_id!='' and designation_name_en  is not null  and designation_name_en!='' order by 2")
	List<Map<String, Object>> getDesignationList();

	@Query(nativeQuery = true, value = "select emailid as value, full_name||' - '||coalesce(designation,'') as label from ecourts_mst_gps")
	List<Map<String, Object>> getGPsList();

	@Query(nativeQuery = true, value = "  select upper(trim(case_type )) as value,upper(trim(case_type_fullform ))  as label from ecourts_case_type_master_new order by  sno ")
	List<Map<String, Object>> getCaseTypesList();

	@Query(nativeQuery = true, value = "select  service_desc as value,upper(trim(service_desc))  as label from ecourts_mst_services order by 1")
	List<Map<String, Object>> getServiceTypesList();

	@Query(nativeQuery = true, value = "select district_id as value,upper(trim(district_name))  as label from district_mst order by trim(district_name)")
	List<Map<String, Object>> getOtherdistList();

	@Query(nativeQuery = true, value = "select upper(trim(case_type)) as value,upper(trim(case_type)) as label from ecourts_case_type_master_new  where   (case_type not like '%CRL%'  AND case_type not like '%RT%')  and case_type!='TRCRLA' order by value")
	List<Map<String, Object>> getCaseTypesListShrt();

	@Query(nativeQuery = true, value = "select trim(dept_code) as value, trim(dept_code||'-'||upper(description)) as label from  "
			+ "dept_new where deptcode='01' and display=true order by dept_code")
	List<Map<String, Object>> getOtherDeptList();

	@Query(nativeQuery = true, value = "select tablename from district_mst where district_id=:distIdInt")
	String getTableName(int distIdInt);

	@Query(nativeQuery = true, value = "select trim(dept_code) as value, trim(dept_code||'-'||upper(description)) as label from dept_new where deptcode='01' and display=true order by dept_code")
	List<Map<String, Object>> getSecDeptNames();

	@Query(nativeQuery = true, value = "select trim(dept_code) as value, trim(dept_code||'-'||upper(description)) as label from dept_new where deptcode!='01' and display=true order by dept_code")
	List<Map<String, Object>> getHodDeptNames();

	@Query(nativeQuery = true, value = "select count(*) from ecourts_case_data where type_name_reg||'/'||reg_year||'/'||reg_no=:caseTypeCode")
	int isExitsMainCaseNo(String caseTypeCode);

	
	@Query(nativeQuery = true, value = "select distinct advocate_name as advocate_code from ecourts_mst_advocate_ccs  where  advocate_code=:advocate_code")
	String getAdvName(int advocate_code);
	
	
	@Query(nativeQuery = true, value = "select  distinct category_service as value,category_service as label from  ecourts_case_data where category_service!=' ' and category_service is not null")
	List<Map<String, Object>> getCategoryServiceList();

	@Query(nativeQuery = true, value = "select upper(description) as description from dept_new where dept_code=:deptId")
	String getDeptName(@Param("deptId") String deptId);

	@Query(nativeQuery = true, value = "select count(*) from users where userid=:username and password=:password")
	int isUNPSexists(@Param("username") String username,@Param("password") String password);
	
	
	
	@Query(nativeQuery = true, value = "select USERID as value , user_description as label from USERS  where dept_code=:deptCode AND USER_TYPE=:userType ORDER BY label ")
	List<Map<String, Object>> getExistingEmpName(@Param("deptCode") String deptCode,@Param("userType") String userType);
	
	@Query(nativeQuery = true, value = "select DISTINCT dn.dept_code as value,dn.dept_id||'-'||upper(dn.description) as label  from  dept_new dn  where dn.dept_code=:deptCode  order by 1")
	List<Map<String, Object>> getNewDepartmentList(@Param("deptCode") String deptCode);
	
	
	@Query(nativeQuery = true, value = "select distinct designation_id::int4 as value , designation_name_en as label from nic_Data where substring(global_org_name,1,5)=:deptId order by designation_id::int4")
	List<Map<String, Object>> getNewDesignationList(@Param("deptId") String deptId);
	
	@Query(nativeQuery = true, value = "select distinct designation_id::int4 as value , nic_data.fullname_en   as label from NIC_DATA  where substring(global_org_name,1,5)=:deptCode  ORDER BY label ")
	List<Map<String, Object>> getEmpList(@Param("deptCode") String deptCode);
	
	@Query(nativeQuery = true, value = "select distinct coram as value,coram as label from ecourts_case_data where coram!='' order by coram")
	List<Map<String, Object>> getJudgesList();
	
	@Query(nativeQuery = true, value = "select purpose_name as value,purpose_name as label from apolcms.ecourts_case_data where dept_code=:deptCode group by purpose_name order by 1")
	List<Map<String, Object>> getPurposeList(String deptCode);
	
	@Query(nativeQuery = true, value = "select trim(employee_identity),trim(employee_identity) from nic_data where substr(trim(global_org_name),1,5)=:deptCode and trim(employee_identity)!='NULL' and trim(employee_identity)!='' group by trim(employee_identity) order by 1")
	List<Map<String, Object>> empSectionList(String deptCode);
	
	@Query(nativeQuery = true, value = "select emailid as value, b.fullname_en||'-'|| designation_name_en||' - MLO-'||a.subject_desc as label from mlo_subject_details a  "
			+ "	  inner join (select distinct employee_id,designation_id,designation_name_en,fullname_en from nic_data) b on (a.employeeid=b.employee_id and a.designation=b.designation_id)  "
			+ " where a.user_id=:deptCode ")
	List<Map<String, Object>> getMLOSUBLIST(String deptCode);

	@Query(nativeQuery = true, value = "select case_type as value,case_type as label from ecourts_case_type_master_new order by sno")
	List<Map<String, Object>> caseTypeMstNEW();

	@Query(nativeQuery = true, value = "select distinct res_adv as value,res_adv as label from ecourts_case_data where res_adv is not null and res_adv not in('-','&#039;','.','..','...','....','.....','./',';','0','00','000','0000','00000',',','') order by 1 ")
	List<Map<String, Object>> getResAdvList();
	
	
	@Query(nativeQuery = true, value = "select upper(district_name) as district_name from district_mst where district_id=:districtId")
	String getDistName(@Param("districtId") int districtId);

	@Query(nativeQuery = true, value = "select sno as value,case_type as label from ecourts_case_type_master_new order by sno")
	List<Map<String, Object>> getCaseTypeListNewWithSNO();

	@Query(nativeQuery = true, value = "select sno as value,case_full_name as label from case_type_master order by sno")
	List<Map<String, Object>> getCaseTypesListOldTableWithSNO();

	
}
