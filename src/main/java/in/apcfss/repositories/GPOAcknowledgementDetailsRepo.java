package in.apcfss.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.GPOAckDetailsEntity;
import jakarta.transaction.Transactional;

@Repository
public interface GPOAcknowledgementDetailsRepo extends JpaRepository<GPOAckDetailsEntity, Long>{

	@Query(nativeQuery = true,value = "select :deptId || lpad(cast(:distId as text), 2, '0') || to_char(now(), 'yyyymmddhhmissms')")
	String getAckNo(@Param("deptId") String deptId,@Param("distId") int distId);

	@Query(nativeQuery = true,value = "select :deptId || lpad(nextval('ecourts_gpo_hc_ack_gen_seq')::text, 7, '0')")
	String getHCAckNo(@Param("deptId") String deptId);

	@Query(nativeQuery = true,value = "select tablename from district_mst where district_id=:distId")
	String getTableName(@Param("distId") Integer distId);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_dtls set ack_file_path=:ackPath, barcode_file_path=:barCodeFilePath where ack_no=:ackNo")
	int updateFilepaths(@Param("ackPath") String ackPath,@Param("barCodeFilePath") String barCodeFilePath,@Param("ackNo") String ackNo);

	@Query(nativeQuery = true,value = "select slno,a.ack_no ,a.distid , a.petitioner_name, a.advocateccno ,a.advocatename ,a.case_category, a.casetype as nature_of_petition,a.mode_filing , "
			+ " a.maincaseno, split_part(a.maincaseno, '/', 1) as main_case_type,split_part(a.maincaseno, '/', 3) as main_case_year, "
			+ " split_part(a.maincaseno, '/', 2) as main_case_no, "
			+ " a.remarks ,  a.inserted_by , a.inserted_ip, upper(trim(dm.district_name)) as district_name, "
			+ " upper(trim(cm.case_full_name)) as  case_full_name,a.reg_year,a.reg_no,a.crime_no ,a.crime_year ,a.name_of_the_police_station ,a.bail_petition_type ,a.court_name ,a.charge_sheet_no ,a.seb_name   from ecourts_gpo_ack_dtls a "
			+ " left join district_mst dm on (a.distid=dm.district_id) "
			+ " left join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) where a.inserted_by=:userId and a.ack_no=:ackNo  ")
	List<Map<String, Object>> getDisplayAckEditFormList(@Param("userId") String userId,@Param("ackNo")  String ackNo);

	@Query(nativeQuery = true,value = "select ack_no, dept_code, respondent_slno, servicetpye, dept_category, dept_distcoll, dist_id from ecourts_gpo_ack_depts where ack_no=:ackNo")
	List<Map<String, Object>> getData2(@Param("ackNo") String ackNo);

	
	 
	
	@Query(nativeQuery = true,value = "select slno , a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip, "
			+ " upper(trim(dm.district_name)) as district_name,  upper(trim(case_full_name)) as  case_full_name, a.ack_file_path, "
			+ " case when services_id='0' then null else services_id end as services_id,services_flag, "
			+ " STRING_AGG(distinct gd.dept_code,',') as dept_codes, "
			+ " STRING_AGG(distinct gd.description||'-'||gd.servicetpye||case when coalesce(gd.dept_category,'0')!='0' then '-'||gd.dept_category else '' end ,', ') as dept_descs, "
			+ " STRING_AGG(distinct upper(trim(ead2.description))||'-'||ead2.emp_section||'-'|| ead2.emp_post||'-'|| ead2.emp_user_id, ',') AS other_selection_types, "
			+ " a.barcode_file_path, to_char(inserted_time,'dd-mm-yyyy') as generated_date, "
			+ " mode_filing, case_category, coalesce(a.hc_ack_no,'-') as hc_ack_no,a.crime_no ,a.crime_year ,a.name_of_the_police_station ,a.bail_petition_type ,a.court_name ,a.charge_sheet_no ,a.seb_name  "
			+ " from ecourts_gpo_ack_dtls a  left join district_mst dm on (a.distid=dm.district_id) "
			+ " left join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name)"
			+ " left join (select ack_no,respondent_slno,a1.dept_code, "
			+ " case when dm1.description is not null then dm1.description when dm2.district_id is not null then 'District Collector, '||dm2.district_name end as description ,servicetpye , coalesce(dept_category,'0') as dept_category "
			+ " from ecourts_gpo_ack_depts a1 left join dept_new dm1 on (a1.dept_code=dm1.dept_code) "
			+ " left join district_mst dm2 on (a1.dist_id=dm2.district_id) "
			+ " order by respondent_slno) gd on (a.ack_no=gd.ack_no)  "
			+ " left join"
			+ " (select ackno,emp_section,eaad.dept_code,emp_post,emp_user_id, dm3.district_name,"
			+ " case when dn2.description is not null then dn2.description when dm3.district_id is not null then 'District Collector, '||dm3.district_name end as description"
			+ " from ecourts_ack_assignment_dtls eaad"
			+ " left join dept_new dn2  on (eaad.dept_code=dn2.dept_code) "
			+ " left join district_mst dm3 on (eaad.dist_id=dm3.district_id)) ead2 on (a.ack_no=ead2.ackno)"
			+ " where a.inserted_by=:userId  and a.delete_status is false and ack_type=:ackType    and inserted_time::date=current_date"
			+ " group by slno , a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip, dm.district_name, case_full_name,"
			+ "a.ack_file_path, services_id,services_flag, inserted_time,a.barcode_file_path, reg_year, reg_no, ack_type, a.mode_filing, a.case_category, a.hc_ack_no  order by inserted_time desc ")
	List<Map<String, Object>> getAcknowledementsList(@Param("userId") String userId,@Param("ackType") String ackType);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_dtls set distid=:distId ,petitioner_name=:petitionerName,advocateccno=:advocateCCno,advocatename=:advocateName,"
			+ "  case_category=:caseCategory,casetype=:caseType,mode_filing=:filingMode,maincaseno=:mainCaseNo,maincaseno_updated=:mainCaseNo2,ackno_updated=:ackno_updated,remarks=:remarks , "
			+ " crime_no=:crimeNo , crime_year=:crimeYear , name_of_the_police_station=:policeStationName , bail_petition_type=:bailPetitionType , court_name=:courtName , charge_sheet_no=:chargeSheetNo , seb_name=:sebName  "
			+ " where ack_no=:ackNo     ")
	int updateEcourts_gpo_ack_dtls(@Param("distId") int distId,@Param("petitionerName") String petitionerName,@Param("advocateCCno") String advocateCCno,@Param("advocateName") String advocateName,
			@Param("caseCategory") String caseCategory,@Param("caseType") String caseType,@Param("filingMode") String filingMode,@Param("mainCaseNo") String mainCaseNo,@Param("mainCaseNo2") String mainCaseNo2,
			@Param("ackno_updated") String ackno_updated,@Param("remarks") String remarks,
			@Param("crimeNo") String crimeNo,
			@Param("crimeYear") int crimeYear,
			@Param("policeStationName") String policeStationName,
			@Param("bailPetitionType") String bailPetitionType,
			@Param("courtName") String courtName,@Param("chargeSheetNo") String chargeSheetNo,@Param("sebName") String sebName,
			@Param("ackNo") String ackNo);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "INSERT INTO apolcms.ecourts_gpo_ack_depts_bk(ack_no, dept_code, respondent_slno, assigned, case_status, assigned_to, ecourts_case_status, dist_id, section_officer_updated, mlo_no_updated, "
			+ " designation, mandalid, villageid, servicetpye, dept_category, dept_distcoll,ip_address,deleted_time ) "
			+ " SELECT ack_no, dept_code, respondent_slno, assigned, case_status, assigned_to, ecourts_case_status, dist_id, section_officer_updated, mlo_no_updated,"
			+ " designation, mandalid, villageid, servicetpye, dept_category, dept_distcoll,:remoteAddr  as ip_address,now() as deleted_time FROM apolcms.ecourts_gpo_ack_depts where ack_no=:ackNo  ")
	int ecourts_gpo_ack_depts_bk(@Param("remoteAddr") String remoteAddr,@Param("ackNo") String ackNo);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " delete from ecourts_gpo_ack_depts where ack_no=:ackNo  ")
	int deleteEcourts_gpo_ack_depts(@Param("ackNo") String ackNo);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " INSERT INTO apolcms.ecourts_gpo_ack_depts  (ack_no, dept_code, respondent_slno, assigned, case_status, assigned_to,servicetpye, dept_category, dept_distcoll, dist_id)"
			+ "  VALUES (:ackNo,:deptId, :respondentId,:assigned,:newStatusCode,:employeeId, :serviceType, :deptCategory, :departmentId, :dispalyDist) ")
	int ecourts_gpo_ack_depts_Loop(@Param("ackNo") String ackNo,@Param("deptId") String deptId,@Param("respondentId") int respondentId,@Param("assigned") boolean assigned,
			@Param("newStatusCode") int newStatusCode,@Param("employeeId") String employeeId,   @Param("serviceType") String serviceType,@Param("deptCategory") String deptCategory,
			@Param("departmentId") String departmentId,@Param("dispalyDist") int dispalyDist);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_gpo_ack_depts_log select * from ecourts_gpo_ack_depts where ack_no=:ackNo  ")
	int DeleteBAck_ecourts_gpo_ack_depts_log(@Param("ackNo") String ackNo);

	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_gpo_ack_dtls_log select * from ecourts_gpo_ack_dtls  where ack_no=:ackNo  ")
	int DeleteBAck_ecourts_gpo_ack_dtls_log(@Param("ackNo") String ackNo);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "delete from ecourts_gpo_ack_depts  where ack_no=:ackNo  ")
	int Delete_ecourts_gpo_ack_depts(@Param("ackNo") String ackNo);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "delete from ecourts_gpo_ack_dtls  where ack_no=:ackNo  ")
	int Delete_ecourts_gpo_ack_dtls(@Param("ackNo") String ackNo);

	
	@Query(nativeQuery = true,value = "select count(*) from case_type_master where case_full_name =:caseFullName  ")
	int CaseTypeExist(@Param("caseFullName") String caseFullName);

	@Query(nativeQuery = true,value = "select max(sno)+1 from case_type_master limit 1 ")
	int CaseTypeAdd();

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into case_type_master ( sno, case_full_name, case_short_name)   values (:slno,:caseFullName,:caseFullName2) ")
	int INSERTCaseType(@Param("slno") int slno,@Param("caseFullName") String caseFullName,@Param("caseFullName2") String caseFullName2);
	
	@Query(nativeQuery = true,value = "select count(*) from ecourts_mst_advocate_ccs where advocate_code=:advocateCode  ")
	int AdvocateCCCODE(@Param("advocateCode") int advocateCode);

	@Query(nativeQuery = true,value = "select max(slno)+1 from ecourts_mst_advocate_ccs limit 1")
	int AdvocateCCCODECount();

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_mst_advocate_ccs ( slno,advocate_code, advocate_name)  values (:slno,:advocateCode, upper(:advocateName)) ")
	int insert_advocate_ccs(@Param("slno") int slno,@Param("advocateCode")  int advocateCode,@Param("advocateName")  String advocateName);

	
	@Query(nativeQuery = true,value = "select inserted_by, count(*) as total, sum(case when ack_type='NEW' then 1 else 0 end) as new_acks, "
			+ "  sum(case when ack_type='OLD' then 1 else 0 end) as existing_acks "
			+ "  from ecourts_gpo_ack_dtls where inserted_time::date=TO_DATE(:ackDate,'DD-MM-YYYY') group by inserted_by  ") //order by  desc
	List<Map<String, Object>> getUserWiseAckData(@Param("ackDate") String ackDate);

}
