package in.apcfss.repositories;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.GPOAckDeptsEntity;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.transaction.Transactional;

@Repository
public interface GPOAcknowledgementDeptsRepo extends JpaRepository<GPOAckDeptsEntity, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "INSERT INTO apolcms.ecourts_ack_assignment_dtls "
			+ "(ackno, emp_section, emp_post, emp_id, inserted_time, inserted_ip, dept_code, inserted_by, "
			+ "emp_user_id, counter_filed, counter_file_remarks, counter_filed_on, counter_filed_ip, village_code, tehsilcode, dist_id) "
			+ "VALUES(:ackNo, :empSect, :empPost, :empId, :insertedtime, :remoteAddr, :empDept, :userId, "
			+ ":empId, false, '', NULL, NULL, :village, :mandal, :otherdistid)")
	Integer insertEcourtsAckAssignmentDtls(@Param("ackNo") String ackNo, @Param("empDept") String empDept,
			@Param("empSect") String empSect, @Param("empPost") String empPost, @Param("mandal") int mandal,
			@Param("village") int village, @Param("otherdistid") Integer otherdistid, @Param("empId") String empId,
			@Param("insertedtime") Timestamp insertedtime, @Param("remoteAddr") InetAddress remoteAddr,
			@Param("userId") String userId);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "INSERT INTO apolcms.ecourts_case_activities "
			+ "(cino, action_type, inserted_by, inserted_ip, assigned_to, remarks, inserted_on, uploaded_doc_path, dist_id) "
			+ "VALUES(:ackNo, :activityDesc, :userId, :remoteAddr, :assignedto, :remarks, now(), NULL, :distId)")
	Integer insertEcourtsCaseActivities(@Param("ackNo") String ackNo, @Param("activityDesc") String activityDesc,
			@Param("userId") String userId, @Param("remoteAddr") InetAddress inetAddress, 
			@Param("assignedto") String assignedto, @Param("remarks") String remarks, @Param("distId") Integer distId);

	@Query(nativeQuery = true, value = "select count(*) from users where trim(userid)=:empId")
	int isExistsEmp(@Param("empId") String empId);

	

	@Query(nativeQuery = true, value = "select c.case_full_name||' Petition / File' as case_full_name,b.dept_code,dn.description from ecourts_gpo_ack_dtls a "
			+ " inner join ecourts_gpo_ack_depts b on (a.ack_no=b.ack_no) inner join case_type_master c on (a.casetype=c.sno::text)"
			+ " inner join dept_new dn on (dn.dept_code=b.dept_code) where a.ack_no=:ackNo")
	List<Map<String, Object>> getFileName(@Param("ackNo") String ackNo);

	@Query(nativeQuery = true, value = "select slno , a.ack_no , distid , advocatename ,advocateccno , casetype , "
			+ "coalesce(maincaseno,null,'0') as maincaseno , remarks ,  inserted_by , inserted_ip, "
			+ " upper(trim(district_name)) as district_name,  upper(trim(case_full_name)) as  case_full_name, a.ack_file_path, "
			+ " case when services_id='0' then null else services_id end as services_id,services_flag, "
			+ " STRING_AGG(gd.dept_code,',') as dept_codes, "
			+ " STRING_AGG(gd.description||'-'||gd.servicetpye||case when coalesce(gd.dept_category,'0')!='0' then '-'||gd.dept_category else '' end ,', ') as dept_descs, "
			+ " a.barcode_file_path, to_char(inserted_time,'dd-mm-yyyy') as generated_date, "
			+ " mode_filing, case_category, coalesce(a.hc_ack_no,'-') as hc_ack_no "
			+ " from ecourts_gpo_ack_dtls a  left join district_mst dm on (a.distid=dm.district_id) "
			+ " left join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) "
			+ " left join (select ack_no,respondent_slno,a1.dept_code, "
			+ " case when dm1.description is not null then dm1.description when dm2.district_id is not null then 'District Collector, '||dm2.district_name end as description ,servicetpye , coalesce(dept_category,'0') as dept_category "
			+ " from ecourts_gpo_ack_depts a1 left join dept_new dm1 on (a1.dept_code=dm1.dept_code) "
			+ " left join district_mst dm2 on (a1.dist_id=dm2.district_id) "
			+ " order by respondent_slno) gd on (a.ack_no=gd.ack_no) "
			+ " where a.inserted_by=:userId and a.delete_status is false and a.ack_no=:ackNo "
			+ " group by slno , a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , "
			+ " inserted_ip, district_name, case_full_name,a.ack_file_path, services_id,services_flag, inserted_time,a.barcode_file_path,  "
			+ " reg_year, reg_no, ack_type, a.mode_filing, a.case_category, a.hc_ack_no  order by inserted_time desc")
	List<Map<String, Object>> getListData(@Param("userId") String userId, @Param("ackNo") String ackNo);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update ecourts_case_data set barcode_file_path=:barCodeFilePath  ,hc_scan_legacy_by=:userId,"
			+ " hc_scan_legacy_date=:date ,scanned_document_path=:updated_file  where cino=:cinNo2 ")
	Integer getUpdateBarcodeFile(@Param("barCodeFilePath") String barCodeFilePath,@Param("userId") String userId,@Param("date") Date date,@Param("updated_file") String updated_file ,@Param("cinNo2") String cinNo2);

	
	@Query(nativeQuery = true, value = " select case_type as value,case_type_fullform as label from ecourts_case_type_master_new where case_type='TRCRLA' or (case_type not like '%CRL%'  and case_type !='RT') ")
	List<Map<String, Object>> GP_ecourts_case_type_master_new();

	@Query(nativeQuery = true, value = " select case_type as value,case_type_fullform as label from ecourts_case_type_master_new where (case_type like '%CRL%'  or case_type='RT')  and case_type!='TRCRLA' ")
	List<Map<String, Object>> PP_ecourts_case_type_master_new();

	@Query(nativeQuery = true, value = " select case_type from ecourts_case_type_master_new where case_type like '%CRL%' ")
	List<Map<String, Object>> PP_List();

	@Query(nativeQuery = true, value = " SELECT cino FROM ecourts_case_data a WHERE 1=1   AND (:regYear IS NULL OR a.reg_year = CAST(:regYear AS bigint))  AND (:regNo IS NULL OR a.reg_no = :regNo) "
			+ "   AND (:caseType IS NULL OR a.type_name_reg = :caseType)   AND (:cino IS NULL OR a.cino = :cino)  ")
	String getCasesListExisting(@Param("regYear") Integer regYear, @Param("regNo") String regNo, @Param("caseType") String caseType, @Param("cino") String cino);

	 
}
