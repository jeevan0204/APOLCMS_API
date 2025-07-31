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
public interface GPReportRepo extends JpaRepository<MyEntity, Long> {



	@Query(nativeQuery = true, value = "select emailid as value , first_name||' '||last_name||' - '||designation as label from ecourts_mst_gps order by 1")
	List<Map<String, Object>> getGPSList();


	@Query(nativeQuery = true, value = "select distinct d.ack_no ,(select case_full_name from case_type_master ctm where ctm.sno::text=e.casetype::text) as type_name_reg, "
			+ " e.reg_no, e.reg_year, petitioner_name,to_char(e.inserted_time,'dd-mm-yyyy') as dt_regis, a.cino, COALESCE(NULLIF(servicetpye,'null'), 'NON-SERVICES') as servicetype , "
			+ " case when length(ack_file_path) > 10 then ack_file_path else '-' end as scanned_document_path,legacy_ack_flag "
			+ " from (select distinct cino,dept_code,legacy_ack_flag from ecourts_dept_instructions where legacy_ack_flag='New') a "
			+ " inner join ecourts_gpo_ack_depts d on (d.ack_no=a.cino)  inner join ecourts_gpo_ack_dtls e on (d.ack_no=e.ack_no) "
			+ " where d.dept_code in (select dept_code from ecourts_mst_gp_dept_map where gp_id=:userId ) ")
	List<Map<String, Object>> viewInstructionsCasesNew(@Param ("userId") String userId);



	@Query(nativeQuery = true, value = "select a.*,   nda.fullname_en as fullname,'Legacy' as legacy_ack_flag , nda.designation_name_en as designation, nda.post_name_en as post_name, nda.email, nda.mobile1 as mobile,dim.district_name ,"
			+ "   'Pending at '||ecs.status_description||'' as current_status, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths, "
			+ "   case when (prayer is not null and coalesce(trim(prayer),'')!='' and length(prayer) > 2) then substr(prayer,1,250) else '-' end as prayer, prayer as prayer_full, ra.address from ecourts_case_data a "
			+ "   left join nic_prayer_data np on (a.cino=np.cino)"
			+ "   left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1) "
			+ "   left join district_mst dim on (a.dist_id=dim.district_id)   "
			+ "    inner join ecourts_mst_case_status ecs on (a.case_status=ecs.status_id)"
			+ "    left join nic_data_all nda on (a.dept_code=substr(nda.global_org_name,1,5) and a.assigned_to=nda.email and nda.is_primary='t' and coalesce(a.dist_id,'0')=coalesce(nda.dist_id,'0'))"
			+ "    left join ("
			+ "   select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
			+ "   from   "
			+ "    (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null "
			+ "    and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0 "
			+ "   and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1  "
			+ "   union "
			+ "    (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null  "
			+ "    and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0  "
			+ "   and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b  "
			+ "   on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code) where d.display = true and a.cino=:cIno   ")
	List<Map<String, Object>> getLegacyDailyStatusEntryReport(@Param ("cIno") String cIno);


	@Query(nativeQuery = true, value = "select instructions,to_char(insert_time,'dd-Mon-yyyy hh24:mi:ss PM') as insert_time,coalesce(upload_fileno,'-') as upload_fileno,slno,reply_flag,reply_serno"
			+ " from ecourts_dept_instructions where cino=:cIno  order by insert_time desc limit 1 ")
	List<Map<String, Object>> getLegacyDept_InstructionsReport(@Param ("cIno") String cIno);


	@Query(nativeQuery = true, value = "select a.slno ,ad.respondent_slno, a.ack_no,'New' as legacy_ack_flag , distid , advocatename ,advocateccno , casetype , maincaseno , a.remarks ,  inserted_by , inserted_ip, upper(trim(district_name)) as district_name,"
			+ " upper(trim(case_full_name)) as  case_full_name, a.ack_file_path, case when services_id='0' then null else services_id end as services_id,services_flag,"
			+ " to_char(a.inserted_time,'dd-mm-yyyy') as generated_date,"
			+ " getack_dept_desc(a.ack_no::text) as dept_descs , coalesce(a.hc_ack_no,'-') as hc_ack_no "
			+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no) "
			+ " left join district_mst dm on (ad.dist_id=dm.district_id) "
			+ " left join dept_new dmt on (ad.dept_code=dmt.dept_code)"
			+ " inner join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name)"
			+ " where a.delete_status is false and ack_type='NEW'    and (a.ack_no=:cIno  or a.hc_ack_no=:cIno )  and respondent_slno='1'  order by a.inserted_time desc ")
	List<Map<String, Object>> getNewDailyStatusEntryReport(@Param ("cIno") String cIno);


	@Query(nativeQuery = true, value = "select instructions,to_char(insert_time,'dd-Mon-yyyy hh24:mi:ss PM') as insert_time,coalesce(upload_fileno,'-') as upload_fileno,slno,reply_flag,reply_serno"
			+ "  from ecourts_dept_instructions where cino=:cIno  order by insert_time desc limit 1 ")
	List<Map<String, Object>> getNewDept_InstructionsReport(@Param ("cIno") String cIno);


	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED' and assigned_to in (select emailid from mlo_details where user_id=:deptCodeC ) order by inserted_on desc limit 1 ")
	String ecourts_return_back_to_Scret_Section(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED'  and assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) = 0) order by inserted_on desc limit 1 ")
	String ecourts_return_back_to_SectionHOD(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED'  and assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) > 0) order by inserted_on desc limit 1")
	String ecourts_return_back_to_SectionDIST(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ " last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date, "
			+ " pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp , "
			+ " action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy , "
			+ " final_order_status, no_district_updated , is_orderimplemented , counter_filed_date) "
			+ " select cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ " last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date, "
			+ " pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp , "
			+ " action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy , "
			+ " final_order_status, no_district_updated , is_orderimplemented , counter_filed_date from ecourts_olcms_case_details where cino=:cIno  ")
	int backinsertPWRApprove(@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path,assigned_to ) "
			+ " values (:cIno,'GP Approved Parawise Remarks',:userId,CAST(:remoteAddr AS inet),:remarks,:pwr_uploaded_copy,:assigned2Emp )  ")
	int insertPWRApprove_courts_case_activities(@Param ("cIno") String cIno,@Param ("userId") String userId,@Param ("remoteAddr") String remoteAddr,@Param ("remarks") String remarks,
			@Param ("pwr_uploaded_copy") String pwr_uploaded_copy,@Param ("assigned2Emp") String assigned2Emp);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set pwr_approved_gp='Yes',action_to_perfom='Parawise Remarks',pwr_gp_approved_date=current_date"
			+ " , remarks=:remarks,last_updated_by=:userId,last_updated_on=now() , pwr_uploaded_copy=:pwrFile1,pwr_uploaded_copy2=:pwrFile2,pwr_uploaded_copy3=:pwrFile3 where cino=:cIno ")
	int updatePWRApprove_olcms_case_details(@Param ("remarks")String remarks,@Param ("userId") String userId,@Param ("pwrFile1") String pwrFile1,@Param ("pwrFile2") String pwrFile2,@Param ("pwrFile3") String pwrFile3,@Param ("cIno") String cIno );


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp where cino=:cIno ")
	int updatePWRApprove_ecourts_case_data(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path,assigned_to ) "
			+ " values (:cIno,'Counter finalized by GP',:userId,CAST(:remoteAddr AS inet),:remarks,:counter_filed_document,:assigned2Emp )  ")
	int insertCounterApprove_courts_case_activities(@Param ("cIno") String cIno,@Param ("userId") String userId,@Param ("remoteAddr") String remoteAddr,@Param ("remarks") String remarks,
			@Param ("counter_filed_document") String counter_filed_document,@Param ("assigned2Emp") String assigned2Emp);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set counter_filed='Yes',counter_approved_gp='T',counter_approved_date=current_date, action_to_perfom='Counter Affidavit',counter_approved_by=:userId "
			+ " , remarks=:remarks,last_updated_by=:userId,last_updated_on=now() , counter_filed_document=:counterFile1,counter_filed_document2=:counterFile2,counter_filed_document3=:counterFile3  where cino=:cIno ")
	int updateCounterApprove_olcms_case_details(@Param ("userId") String userId,@Param ("remarks") String remarks,@Param ("userId2") String userId2,
			@Param ("counterFile1") String counterFile1,@Param ("counterFile2") String counterFile2,@Param ("counterFile3") String counterFile3,@Param ("cIno")  String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp where cino=:cIno ")
	int updateCounterApprove_ecourts_case_data(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks) "
			+ " values ( :cIno, :actionPerformed, :userId,CAST(:remoteAddr AS inet),:remarks )  ")
	int insertCounterApproveFINAL_courts_case_activities(@Param ("cIno") String cIno,@Param ("actionPerformed") String actionPerformed,@Param ("userId") String userId,
			@Param ("remoteAddr") String remoteAddr,@Param ("remarks") String remarks);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_dept_instructions set reply_flag='Y',reply_instructions=:daily_status, reply_upload_fileno=:changeLetter,reply_insert_by=:userId , "
			+ "  reply_serno=:serno ,legacy_ack_flag='Legacy' ,status_instruction_flag='D',reply_insert_time=now()   where cino=:cIno  and slno=:serno2 ")
	int updateLegacyDSE(@Param ("daily_status") String daily_status,@Param ("changeLetter")  String changeLetter,@Param ("userId") String userId,
			@Param ("serno") int serno,@Param ("cIno") String cIno,@Param ("serno2") int serno2);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks,uploaded_doc_path) "
			+ " values (:cIno,'SUBMITTED DAILY CASE STATUS',:userId,CAST(:remoteAddr AS inet),:daily_status,:changeLetter )  ")
	int INSERTLegacyDSE_case_activities(@Param ("cIno") String cIno,@Param ("userId") String userId,@Param ("remoteAddr") String remoteAddr,@Param ("daily_status") String daily_status,
			@Param ("changeLetter") String changeLetter);




	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_dept_instructions set reply_flag='Y',reply_instructions=:daily_status, reply_upload_fileno=:changeLetter,reply_insert_by=:userId , "
			+ "  reply_serno=:serno ,legacy_ack_flag='New' ,status_instruction_flag='D',reply_insert_time=now()   where cino=:cIno  and slno=:serno2 ")
	int updateNewDSE(@Param ("daily_status") String daily_status,@Param ("changeLetter")  String changeLetter,@Param ("userId") String userId,
			@Param ("serno") int serno,@Param ("cIno") String cIno,@Param ("serno2") int serno2);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks,uploaded_doc_path) "
			+ " values (:cIno,'REPLY INSTRUCTIONS BY GP',:userId,CAST(:remoteAddr AS inet),:daily_status,:changeLetter )  ")
	int INSERTNewDSE_case_activities(@Param ("cIno") String cIno,@Param ("userId") String userId,@Param ("remoteAddr") String remoteAddr,@Param ("daily_status") String daily_status,
			@Param ("changeLetter") String changeLetter);




	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED' and assigned_to in (select emailid from mlo_details where user_id=:deptCodeC ) order by inserted_on desc limit 1")
	String ecourts_reject_back_to_Scret_Section(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);





	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED'  and assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) = 0) order by inserted_on desc limit 1 ")
	String ecourts_reject_back_to_SectionHOD(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED'  and assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) > 0) order by inserted_on desc limit 1")
	String ecourts_reject_back_to_SectionDIST(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int updateCounterRejectPWR_ecourts_case_data(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus, assigned_to=:assigned2Emp where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int updateCounterRejectCOUNTER_ecourts_case_data(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus, assigned_to=:assigned2Emp where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int updateCounterRejectCOUNTERFILLED_ecourts_case_data(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set counter_approved_gp='F' where cino=:cIno ")
	int updateCounterRejectCOUNTERFILLED_olcms_case_details(@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus, assigned_to=:assigned2Emp where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int updateCounterRejectCOUNTERPWR_ecourts_case_data(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by , "
			+ " last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date,"
			+ " pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp ,"
			+ " action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy ,"
			+ " final_order_status, no_district_updated , is_orderimplemented , counter_filed_date)"
			+ " select cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ " last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date,"
			+ " pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp ,"
			+ " action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy ,"
			+ " final_order_status, no_district_updated , is_orderimplemented , counter_filed_date from ecourts_olcms_case_details where cino=:cIno ")
	int updateCounterRejectBackUpolcms_case_details(@Param ("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, assigned_to)"
			+ " values (:cIno, :actionPerformed, :userId,CAST(:remoteAddr AS inet),:remarks,:assigned2Emp ) ")
	int insertCounterReject_courts_case_activities(@Param ("cIno") String cIno,@Param ("actionPerformed") String actionPerformed,@Param ("userId") String userId,
			@Param ("remoteAddr") String remoteAddr, @Param ("remarks") String remarks,@Param ("assigned2Emp") String assigned2Emp);



	@Query(nativeQuery = true, value = "select distinct a.ack_no,b.dist_id,b.dept_code,assigned_to,respondent_slno  from ecourts_gpo_ack_dtls a  inner join ecourts_gpo_ack_depts b on (a.ack_no=b.ack_no)"
			+ "   where b.ack_no=:cIno  and assigned_to=:userId  ")
	List<Map<String, Object>> caseDataGPNew(@Param("cIno") String cIno, @Param("userId")  String userId);


	 
	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno "
			+ " and action_type='CASE FORWARDED' and assigned_to in (select emailid from mlo_details where user_id=:deptCodeC ) order by inserted_on desc limit 1 ")
	String ecourts_return_back_to_Scret_SectionNEW(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	
	@Query(nativeQuery = true, value = " select inserted_by from ecourts_case_activities where cino=:cIno "
			+ "and action_type='CASE FORWARDED' and assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) = 0) order by inserted_on desc limit 1 ")
	String ecourts_return_back_to_SectionHODNEW(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	@Query(nativeQuery = true, value = " select inserted_by from ecourts_case_activities where cino=:cIno "
			+ "and action_type='CASE FORWARDED' and assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) > 0) order by inserted_on desc limit 1 ")
	String ecourts_return_back_to_SectionDISTNEW(@Param ("cIno") String cIno, @Param ("deptCodeC") String deptCodeC);


	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ " last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date, "
			+ " pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp , "
			+ " action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy , "
			+ " final_order_status, no_district_updated , is_orderimplemented , counter_filed_date) "
			+ " select cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ " last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date, "
			+ " pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp , "
			+ " action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy , "
			+ " final_order_status, no_district_updated , is_orderimplemented , counter_filed_date from ecourts_olcms_case_details where cino=:cIno and respondent_slno=:respondent_no  ")
	int backinsertPWRApproveNEW(@Param ("cIno") String cIno,@Param ("respondent_no") int respondent_no);


 

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set pwr_approved_gp='Yes',action_to_perfom='Parawise Remarks',pwr_gp_approved_date=current_date"
			+ " , remarks=:remarks,last_updated_by=:userId,last_updated_on=now() , pwr_uploaded_copy=:pwrFile1,pwr_uploaded_copy2=:pwrFile2,pwr_uploaded_copy3=:pwrFile3 where cino=:cIno and respondent_slno=:respondent_no ")
	int updatePWRApprove_olcms_case_detailsNEW(@Param ("remarks")String remarks,@Param ("userId") String userId,@Param ("pwrFile1") String pwrFile1,@Param ("pwrFile2") String pwrFile2,@Param ("pwrFile3") String pwrFile3,@Param ("cIno") String cIno ,@Param ("respondent_no") int respondent_no);


	 



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set  case_status=:newStatus, assigned_to=:assigned2Emp where ack_no=:cIno and respondent_slno=:respondent_no ")
	int updatePWRApprove_ecourts_case_dataNEW(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno,@Param ("respondent_no") int respondent_no);

 

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set counter_filed='Yes',counter_approved_gp='T',counter_approved_date=current_date,action_to_perfom='Parawise Remarks', counter_approved_by=:userId "
			+ " , remarks=:remarks,last_updated_by=:userId,last_updated_on=now() , counter_filed_document=:counterFile1,counter_filed_document2=:counterFile2,counter_filed_document3=:counterFile3  where cino=:cIno and respondent_slno=:respondent_no  ")
	int updateCounterApprove_olcms_case_detailsNEW(@Param ("userId") String userId,@Param ("remarks") String remarks,@Param ("userId2") String userId2,
			@Param ("counterFile1") String counterFile1,@Param ("counterFile2") String counterFile2,@Param ("counterFile3") String counterFile3,@Param ("cIno")  String cIno,@Param ("respondent_no") int respondent_no);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set  case_status=:newStatus, assigned_to=:assigned2Emp where ack_no=:cIno and respondent_slno=:respondent_no   ")
	int updateCounterApprove_ecourts_case_dataNEW(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno,@Param ("respondent_no") int respondent_no);


	 

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks) "
			+ " values ( :cIno, :actionPerformed, :userId,CAST(:remoteAddr AS inet),:remarks )  ")
	int insertCounterApproveFINAL_courts_case_activitiesNEW(@Param ("cIno") String cIno,@Param ("actionPerformed") String actionPerformed,@Param ("userId") String userId,
			@Param ("remoteAddr") String remoteAddr,@Param ("remarks") String remarks);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_gpo_ack_depts set  case_status=:newStatus, assigned_to=:assigned2Emp, section_officer_updated=null, mlo_no_updated=null "
			+ " where ack_no=:cIno and section_officer_updated='T' and mlo_no_updated='T'  and respondent_slno=:respondent_slno ")
	int updatePWRApprove_ecourts_case_dataPWRNEWGPREJECT(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno,@Param ("respondent_slno") int respondent_slno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_gpo_ack_depts set  case_status=:newStatus, assigned_to=:assigned2Emp ,section_officer_updated=null , mlo_no_updated=null   "
			+ " where ack_no=:cIno and section_officer_updated='T' and mlo_no_updated='T' and respondent_slno=:respondent_slno ")
	int updatePWRApprove_ecourts_case_dataCOUNTERNEWGPREJECT(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno,@Param ("respondent_slno") int respondent_slno);


	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_gpo_ack_depts set  case_status=:newStatus, assigned_to=:assigned2Emp    "
			+ " where ack_no=:cIno and section_officer_updated='T' and mlo_no_updated='T' and respondent_slno=:respondent_slno ")
	int updatePWRApprove_ecourts_case_dataCOUNTERFILEDNEWGPREJECT(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno,@Param ("respondent_slno") int respondent_slno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_olcms_case_details set counter_approved_gp='F' where cino=:cIno and respondent_slno=:respondent_slno ")
	int updatePWRApprove_ecourts_case_dTLSCOUNTERFILEDNEWGPREJECT(@Param ("cIno") String cIno,@Param ("respondent_slno") int respondent_slno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_gpo_ack_depts set  case_status=:newStatus, assigned_to=:assigned2Emp    "
			+ " where ack_no=:cIno and section_officer_updated='T' and mlo_no_updated='T' and respondent_slno=:respondent_slno ")
	int updatePWRApprove_ecourts_case_dataCOUNTERFILEDPWRSUBNEWGPREJECT(@Param ("newStatus") int newStatus,@Param ("assigned2Emp") String assigned2Emp,@Param ("cIno") String cIno,@Param ("respondent_slno") int respondent_slno);


	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_olcms_case_details_log (cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ "  last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date, "
			+ "  pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp ,"
			+ "  action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy ,"
			+ "  final_order_status, no_district_updated , is_orderimplemented , counter_filed_date)"
			+ "  select cino , petition_document ,  counter_filed_document  , judgement_order,action_taken_order ,last_updated_by ,"
			+ "  last_updated_on, counter_filed , remarks, ecourts_case_status , corresponding_gp , pwr_uploaded, pwr_submitted_date ,pwr_received_date,"
			+ "  pwr_approved_gp,pwr_gp_approved_date, appeal_filed ,appeal_filed_copy,  appeal_filed_date , pwr_uploaded_copy , counter_approved_gp ,"
			+ "  action_to_perfom , counter_approved_date , counter_approved_by , respondent_slno ,cordered_impl_date, dismissed_copy ,"
			+ "  final_order_status, no_district_updated , is_orderimplemented , counter_filed_date from ecourts_olcms_case_details where cino=:cIno and respondent_slno=:respondent_slno ")
	int ecourts_olcms_case_details_logNEWGPREJECT(@Param ("cIno") String cIno,@Param ("respondent_slno") int respondent_slno);


	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks) "
			+ " values ( :cIno, :actionPerformed, :userId,CAST(:remoteAddr AS inet),:remarks )  ")
	int insertCounterApproveFINAL_courts_case_activitiesNEWreject(@Param ("cIno") String cIno,@Param ("actionPerformed") String actionPerformed,@Param ("userId") String userId,
			@Param ("remoteAddr") String remoteAddr,@Param ("remarks") String remarks);
}
