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
public interface AssignedNewCasesToEmpRepo extends JpaRepository<MyEntity, Long> {

	@Query(nativeQuery = true, value = " select advocatename,advocateccno,cm.case_short_name as case_type,maincaseno,to_char(inserted_time,'dd-MONTH-yyyy') as inserted_time,petitioner_name,"
			+ "  services_flag,reg_year,reg_no,upper(trim(mode_filing)) as mode_filing,case_category,dm.district_name,ack_file_path,barcode_file_path,ack_no ,coalesce(a.hc_ack_no,'-') as hc_ack_no  "
			+ "  from  ecourts_gpo_ack_dtls a "
			+ "  left join district_mst dm on (a.distid=dm.district_id) "
			+ "  left join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) where ack_no=:cIno ")
	List<Map<String, Object>> getÙsersList(@Param("cIno") String cIno);


	@Query(nativeQuery = true, value = " select respondent_slno||'.'||case when district_name is not null then 'District Collector-'||district_name else description end||'-('||coalesce(servicetpye,'')||')' as description"
			+ "   from ecourts_gpo_ack_depts a left join dept_new b using (dept_code)"
			+ "   left join district_mst d on (a.dist_id=d.district_id) where ack_no=:cIno order by respondent_slno ")
	List<Map<String, Object>> getRespodentList(@Param("cIno") String cIno);

	@Query(nativeQuery = true, value = " select upper(trim(case when dm.district_name is null then '' "
			+ "  else dm.district_name end))||'-'||(case when emp_section is null then ''"
			+ "  else emp_section end)||'-'|| (case when emp_post is null then ''"
			+ "  else emp_post end)||'-'|| (case when dept_code is null then ''"
			+ "  else dept_code end)||'-'||(case when emp_user_id is null then ''"
			+ "  else emp_user_id end) as other_selection_type  from ecourts_ack_assignment_dtls eaad "
			+ "  left join district_mst dm on (eaad.dist_id=dm.district_id) where ackno=:cIno ")
	List<Map<String, Object>> getOtherRespodentList(@Param("cIno") String cIno);


	@Query(nativeQuery = true, value = " select cino,action_type,inserted_by,inserted_on,assigned_to,remarks as remarks, coalesce(uploaded_doc_path,'-') as uploaded_doc_path "
			+ "  from ecourts_case_activities where cino=:cIno order by inserted_on ")
	List<Map<String, Object>> getActivitiesData(@Param("cIno") String cIno);




	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Action Taken Order' , :userId , CAST(:remoteAddr AS inet), :remarks ,:actionTakenOrder) ")
	int insertCase_activities(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("actionTakenOrder") String actionTakenOrder);




	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Judgement Order' , :userId , CAST(:remoteAddr AS inet), :remarks ,:judgementOrder) ")
	int insertBack_case_activitiesClosedJudgementOrder( @Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("judgementOrder") String judgementOrder);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Appeal Copy' , :userId , CAST(:remoteAddr AS inet), :remarks ,:appealFileCopy) ")
	int insertBack_case_activitiesClosedAppealFileCopy( @Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("appealFileCopy") String appealFileCopy);



	@Query(nativeQuery = true, value = " select count(*) from ecourts_olcms_case_details where cino=:cIno and respondent_slno=:resident_id ")
	int olcms_case_detailsCount(@Param("cIno") String cIno,@Param("resident_id") int resident_id);

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = " insert into ecourts_olcms_case_details_log (cino ,  petition_document   ,    counter_filed_document  ,    judgement_order  , action_taken_order  ,last_updated_by ,  last_updated_on ,  counter_filed , remarks  ,"
			+ "  ecourts_case_status ,  corresponding_gp, pwr_uploaded , pwr_submitted_date, pwr_received_date , pwr_approved_gp ,  pwr_gp_approved_date , appeal_filed ,  appeal_filed_copy, appeal_filed_date, pwr_uploaded_copy, "
			+ "  counter_approved_gp ,action_to_perfom, counter_approved_date ,counter_approved_by , respondent_slno, cordered_impl_date , dismissed_copy , final_order_status  ,no_district_updated)  "
			+ "  select cino ,  petition_document   ,    counter_filed_document  ,    judgement_order  , action_taken_order  ,last_updated_by ,  last_updated_on ,  counter_filed , remarks  ,"
			+ "  ecourts_case_status ,  corresponding_gp, pwr_uploaded , pwr_submitted_date, pwr_received_date , pwr_approved_gp ,  pwr_gp_approved_date , appeal_filed ,  appeal_filed_copy, appeal_filed_date, pwr_uploaded_copy,"
			+ "  counter_approved_gp ,action_to_perfom, counter_approved_date ,counter_approved_by , respondent_slno, cordered_impl_date , dismissed_copy , final_order_status  ,no_district_updated "
			+ "  from ecourts_olcms_case_details where cino=:cIno and respondent_slno=:resident_id ")
	int insert_case_details_log(@Param("cIno") String cIno,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set ecourts_case_status=:ecourtsCaseStatus,appeal_filed=:appealFiled,appeal_filed_date=to_date(:appealFiledDt,'yyyy-MM-dd'),"
			+ " remarks=:remarks ,last_updated_by=:userId,last_updated_on=now(),action_to_perfom=:actionToPerform , petition_document=:petitionDocument, "
			+ " action_taken_order=:actionTakenOrder, judgement_order=:judgementOrder, appeal_filed_copy=:appealFileCopy  where cino=:cIno and  respondent_slno=:resident_id ")
	int update_ecourts_olcms_case_details(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("appealFiled") String appealFiled,@Param("appealFiledDt") String appealFiledDt,
			@Param("remarks") String remarks,@Param("userId")  String userId, @Param("actionToPerform") String actionToPerform,@Param("petitionDocument") String petitionDocument, 
			@Param("actionTakenOrder") String actionTakenOrder,@Param("judgementOrder") String judgementOrder,@Param("appealFileCopy") String appealFileCopy,@Param("cIno") String cIno,@Param("resident_id") int resident_id);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details (cino, ecourts_case_status, petition_document, appeal_filed, appeal_filed_copy, judgement_order, action_taken_order,"
			+ "	last_updated_by, last_updated_on, remarks, appeal_filed_date, action_to_perfom,respondent_slno) "
			+ " values (:cIno,:ecourtsCaseStatus,:petitionDocument,:appealFiled,:appealFileCopy,:judgementOrder, :actionTakenOrder,:userId,now(),:remarks,to_date(:appealFiledDt,'yyyy-MM-dd'),:actionToPerform ,:resident_id    )")
	int insert_ecourts_olcms_case_details( @Param("cIno") String cIno, @Param("ecourtsCaseStatus") String ecourtsCaseStatus, @Param("petitionDocument") String petitionDocument,
			@Param("appealFiled") String appealFiled, @Param("appealFileCopy") String appealFileCopy, @Param("judgementOrder") String judgementOrder,
			@Param("actionTakenOrder") String actionTakenOrder, @Param("userId") String userId, @Param("remarks") String remarks,  @Param("appealFiledDt") String appealFiledDt,
			@Param("actionToPerform") String actionToPerform,@Param("resident_id") int resident_id);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set ecourts_case_status=:ecourtsCaseStatus ,section_officer_updated='T'  where ack_no=:cIno and dept_code=:deptCode  and respondent_slno=:resident_id    ")
	int update_ecourts_gpo_ack_depts(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks ) "
			+ " values (:cIno, :actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks)")
	int insert_closedFinal_ecourts_case_activities(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed,@Param("userId") String userId,
			@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks);



	@Query(nativeQuery = true,value = " SELECT cino, case when length(petition_document) > 0 then petition_document else null end as petition_document,"
			+ "  case when length(counter_filed_document) > 0 then counter_filed_document else null end as counter_filed_document,counter_filed_document2,counter_filed_document3,"
			+ "  case when length(judgement_order) > 0 then judgement_order else null end as judgement_order,"
			+ "  case when length(action_taken_order) > 0 then action_taken_order else null end as action_taken_order,"
			+ "  last_updated_by, last_updated_on, counter_filed, remarks, ecourts_case_status, corresponding_gp,"
			+ "  pwr_uploaded, to_char(pwr_submitted_date,'yyyy-MM-dd') as pwr_submitted_date, to_char(pwr_received_date,'yyyy-MM-dd') as pwr_received_date,"
			+ "  pwr_approved_gp, to_char(pwr_gp_approved_date,'yyyy-MM-dd') as pwr_gp_approved_date, appeal_filed,"
			+ "  appeal_filed_copy, to_char(appeal_filed_date,'yyyy-MM-dd') as appeal_filed_date, pwr_uploaded_copy,pwr_uploaded_copy2,pwr_uploaded_copy3,action_to_perfom,counter_filed_date"
			+ "  FROM apolcms.ecourts_olcms_case_details where cino=:cIno and respondent_slno=:resident_id   ")
	List<Map<String, Object>> getOLCMSCASEDATAForUpdate(@Param("cIno") String cIno ,@Param("resident_id") int resident_id);




	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno, 'Uploaded Counter',:userId,CAST(:remoteAddr AS inet),:remarks,:counterFiledDocument)")
	int insert_PendingCounterEcourts_case_activities(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr")  String remoteAddr,@Param("remarks") String remarks,
			@Param("counterFiledDocument") String counterFiledDocument);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno, 'Uploaded Parawise Remarks',:userId,CAST(:remoteAddr AS inet),:remarks,:parawiseRemarksCopy)")
	int insert_PendingPWREcourts_case_activities(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,
			@Param("parawiseRemarksCopy")	String parawiseRemarksCopy);


	@Query(nativeQuery = true, value = "select count(*) from ecourts_olcms_case_details where cino=:cIno and respondent_slno=:resident_id  ")
	int getCinoCount2(@Param("cIno") String cIno ,@Param("resident_id") int resident_id);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,  counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,"
			+ "	 pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,"
			+ "	 cordered_impl_date,dismissed_copy,final_order_status,no_district_updated) "
			+ " select cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,"
			+ "	counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,"
			+ "	 pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated  "
			+ " from ecourts_olcms_case_details where cino=:cIno and respondent_slno=:resident_id  ")
	int insert_Pending_ecourts_olcms_case_details_log(@Param("cIno") String cIno  ,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set ecourts_case_status=:ecourtsCaseStatus , counter_filed=:counterFiled,remarks=:remarks,petition_document=:petitionDocument,"
			+ " last_updated_by=:userId,last_updated_on=now(), counter_filed_document=:counterFileCopy,counter_filed_document2=:counterFileCopy2,counter_filed_document3=:counterFileCopy3, "
			+ " counter_filed_date=to_date(:counterFiledDt ,'yyyy-MM-dd'), pwr_uploaded_copy=:parawiseRemarksCopy, pwr_uploaded_copy2=:parawiseRemarksCopy2, pwr_uploaded_copy3=:parawiseRemarksCopy3,"
			+ " corresponding_gp=:relatedGp,pwr_uploaded=:parawiseRemarksSubmitted,pwr_submitted_date=to_date(:parawiseRemarksDt,'yyyy-MM-dd'),"
			+ " pwr_received_date=to_date(:dtPRReceiptToGP,'yyyy-MM-dd'),pwr_gp_approved_date=to_date(:dtPRApprovedToGP,'yyyy-MM-dd'),action_to_perfom=:actionToPerform  where cino=:cIno and respondent_slno=:resident_id  ")
	int update_Pending_ecourts_olcms_case_details(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("counterFiled") String counterFiled,@Param("remarks") String remarks,
			@Param("petitionDocument") String petitionDocument,@Param("userId") String userId,@Param("counterFileCopy") String counterFileCopy,@Param("counterFileCopy2") String counterFileCopy2,
			@Param("counterFileCopy3") String counterFileCopy3, @Param("counterFiledDt") String counterFiledDt,@Param("parawiseRemarksCopy")  String parawiseRemarksCopy,
			@Param("parawiseRemarksCopy2") String parawiseRemarksCopy2,@Param("parawiseRemarksCopy3") String parawiseRemarksCopy3,@Param("relatedGp") String relatedGp,
			@Param("parawiseRemarksSubmitted") String parawiseRemarksSubmitted,@Param("parawiseRemarksDt") String parawiseRemarksDt,@Param("dtPRReceiptToGP") String dtPRReceiptToGP,
			@Param("dtPRApprovedToGP")  String dtPRApprovedToGP,@Param("actionToPerform") String actionToPerform,@Param("cIno") String cIno ,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details (cino, ecourts_case_status, petition_document, counter_filed_date,counter_filed_document,"
			+ "counter_filed_document2,counter_filed_document3, last_updated_by, last_updated_on,counter_filed, "
			+ " remarks,  corresponding_gp, pwr_uploaded, pwr_submitted_date, pwr_received_date,"
			+ "	 pwr_gp_approved_date, pwr_uploaded_copy,pwr_uploaded_copy2,pwr_uploaded_copy3, action_to_perfom ,respondent_slno) "
			+ " values (:cIno,:ecourtsCaseStatus ,:petitionDocument,to_date(:counterFiledDt,'yyyy-MM-dd'),:counterFileCopy,"
			+ " :counterFileCopy2,:counterFileCopy3,:userId,now(), :counterFiled,"
			+ " :remarks, :relatedGp, :parawiseRemarksSubmitted,to_date(:parawiseRemarksDt ,'yyyy-MM-dd'),to_date(:dtPRReceiptToGP ,'yyyy-MM-dd'),"
			+ "to_date(:dtPRApprovedToGP ,'yyyy-MM-dd'),:parawiseRemarksCopy,:parawiseRemarksCopy2,:parawiseRemarksCopy3, :actionToPerform,:resident_id )")
	int insert_Pending_ecourts_olcms_case_details(@Param("cIno") String cIno, @Param("ecourtsCaseStatus") String ecourtsCaseStatus, @Param("petitionDocument") String petitionDocument,
			@Param("counterFiledDt") String counterFiledDt,@Param("counterFileCopy") String counterFileCopy,@Param("counterFileCopy2") String counterFileCopy2,
			@Param("counterFileCopy3") String counterFileCopy3,@Param("userId") String userId,@Param("counterFiled") String counterFiled,@Param("remarks") String remarks,@Param("relatedGp") String relatedGp,
			@Param("parawiseRemarksSubmitted") String parawiseRemarksSubmitted,@Param ("parawiseRemarksDt") String parawiseRemarksDt,@Param("dtPRReceiptToGP")  String dtPRReceiptToGP,@Param("dtPRApprovedToGP") String dtPRApprovedToGP,
			@Param("parawiseRemarksCopy") String parawiseRemarksCopy,@Param("parawiseRemarksCopy2") String parawiseRemarksCopy2,@Param("parawiseRemarksCopy3") String parawiseRemarksCopy3,
			@Param("actionToPerform") String actionToPerform,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set ecourts_case_status=:ecourtsCaseStatus  , mlo_no_updated='T'   where ack_no=:cIno  and dept_code=:deptCode and respondent_slno=:resident_id ")
	int update_Pending_ecourts_gpo_ack_depts(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode,
			@Param("resident_id") 	int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set ecourts_case_status=:ecourtsCaseStatus  , section_officer_updated='T'   where ack_no=:cIno  and dept_code=:deptCode and respondent_slno=:resident_id ")
	int update_Pending_ecourts_gpo_ack_deptsELSE(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode,
			@Param("resident_id") 	int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks ) "
			+ "values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks )")
	int insert_PendingFINALEcourts_case_activities(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_gpo_ack_depts set ecourts_case_status=:ecourtsCaseStatus, case_status='98' where ack_no=:cIno  and dept_code=:deptCode ")
	int update_ecourts_gpo_ack_deptsPrivate(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_gpo_ack_depts set ecourts_case_status=:ecourtsCaseStatus, case_status='98' where ack_no=:cIno  and dept_code=:deptCode ")
	int update_ecourts_gpo_ack_deptsPrivateELSE(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks) ")
	int insert_ecourts_case_activitiesPrivate(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks);




	//forwards---cases================================================================
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer where ack_no=:cIno "
			+ "  and section_officer_updated='T' and mlo_no_updated='T' and case_status='2' and dept_code=:deptCode and respondent_slno=:resident_id ")
	int ecourts_case_data_frwd_Sec(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno ,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer where ack_no=:cIno "
			+ " and section_officer_updated='T' and mlo_no_updated='T' and case_status='4'  and dept_code=:deptCode and respondent_slno=:resident_id  ")
	int ecourts_case_data_frwd_Hod(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer where ack_no=:cIno "
			+ " and section_officer_updated='T' and mlo_no_updated='T' and case_status='8'  and dept_code=:deptCode and respondent_slno=:resident_id  ")
	int ecourts_case_data_frwd_DcToHod(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);



	@Query(nativeQuery = true,value = " select trim(emailid) from mlo_details where user_id=:deptCode  ")
	String getfwdOfficerMlo(@Param("deptCode") String deptCode);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer where ack_no=:cIno "
			+ " and section_officer_updated='T' and case_status='5'  and dept_code=:deptCode and respondent_slno=:resident_id  ")
	int getFwrdOffMlo(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);





	@Query(nativeQuery = true,value = " select emailid from nodal_officer_details where dept_id=:deptCode and user_id not like '%DC-%' ")
	String getgetfwdOfficerNHOD(@Param("deptCode") String deptCode);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer where ack_no=:cIno "
			+ " and section_officer_updated='T' and case_status='9'  and dept_code=:deptCode and respondent_slno=:resident_id  ")
	int getFwrdOffNHOD(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);


	@Query(nativeQuery = true,value = " select emailid from nodal_officer_details where dept_id=:deptCode and coalesce(dist_id,0)=:distId ")
	String getfwdOfficerNHODDIST(@Param("deptCode") String deptCode,@Param("distId") Integer distId);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer where ack_no=:cIno "
			+ " and section_officer_updated='T' and case_status='10' and dept_code=:deptCode and respondent_slno=:resident_id  ")
	int getFwrdOffNHODDIST(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks)"
			+ " values (:cIno,'CASE FORWARDED',:userId,CAST(:remoteAddr AS inet),:fwdOfficer,:remarks) ")
	int Insert_ecourts_case_activitiesFinalFrwd(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("fwdOfficer") String fwdOfficer,
			@Param("remarks") String remarks);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_ack_assignment_dtls (ackno, dept_code, emp_section, emp_post, emp_id, inserted_time, inserted_ip, inserted_by, emp_user_id)"
			+ " values (:cIno,:deptCode,:empSection,:empPost,:empId,now(),CAST(:remoteAddr AS inet),:userId,:fwdOfficer   ) ")
	int Insert_ecourts_ack_assignment_dtlsFinalFrwd(@Param("cIno") String cIno, @Param("deptCode") String deptCode,@Param("empSection") String empSection,@Param("empPost") String empPost,
			@Param("empId") String empId, @Param("timestamp") Timestamp timestamp , @Param("remoteAddr")  String remoteAddr,@Param("userId")  String userId,@Param("fwdOfficer")  String fwdOfficer);

//send back details
	

	@Query(nativeQuery = true, value = " select emailid from section_officer_details ad inner join ecourts_case_activities ea on (ad.emailid=ea.assigned_to)  "
			+ " where ea.cino=:cIno  and ea.action_type ilike 'CASE ASSSIGNED%' order by ea.inserted_on desc limit 1  ")
	String getbackToSectionUser(@Param("cIno") String cIno);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:backToSectionUser  "
			+ " where ack_no=:cIno and section_officer_updated='T' and case_status='2' and dept_code=:deptCode and respondent_slno=:resident_id ")
	int update_ecourts_case_data_bck_MloToSection(@Param("newStatus") int newStatus,@Param("backToSectionUser") String backToSectionUser,@Param("cIno") String cIno,@Param("deptCode")  String deptCode,
			@Param("resident_id") 	int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:backToSectionUser  "
			+ " where ack_no=:cIno and section_officer_updated='T' and case_status='4' and dept_code=:deptCode and respondent_slno=:resident_id ")
	int update_ecourts_case_data_bck_NoToSection(@Param("newStatus") int newStatus,@Param("backToSectionUser") String backToSectionUser,@Param("cIno") String cIno,@Param("deptCode")  String deptCode,
			@Param("resident_id") 	int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:backToSectionUser  "
			+ " where ack_no=:cIno and section_officer_updated='T' and case_status='8' and dept_code=:deptCode and respondent_slno=:resident_id ")
	int update_ecourts_case_data_bck_DNoToDSection(@Param("newStatus") int newStatus,@Param("backToSectionUser") String backToSectionUser,@Param("cIno") String cIno,@Param("deptCode")  String deptCode,
			@Param("resident_id") 	int resident_id);


	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks)"
			+ " values (:cIno,'CASE SENT BACK',:userId,CAST(:remoteAddr AS inet),:backToSectionUser,:remarks   ) ")
	int Insert_ecourts_case_activitiesFinalSendBack(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("backToSectionUser") String backToSectionUser,
			@Param("remarks") String remarks);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer  where ack_no=:cIno   "
			+ " and section_officer_updated='T' and mlo_no_updated='T' and case_status='1' and dept_code=:deptCode and respondent_slno=:resident_id ")
	int ecourts_case_data_frwd_secToGp(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_gpo_ack_depts set case_status=:newStatus , assigned_to=:fwdOfficer  where ack_no=:cIno   "
			+ " and section_officer_updated='T' and mlo_no_updated='T' and case_status='3' and dept_code=:deptCode and respondent_slno=:resident_id ")
	int ecourts_case_data_frwd_HodToGp(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno,@Param("deptCode") String deptCode,@Param("resident_id") int resident_id);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks)"
			+ " values (:cIno,'CASE FORWARDED TO GP',:userId,CAST(:remoteAddr AS inet),:fwdOfficer,:remarks   ) ")
	int Insert_ecourts_case_activitiesFinalFrwd2GP(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("fwdOfficer") String fwdOfficer,
			@Param("remarks") String remarks);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino,petition_document, counter_filed_document,  "
			+ " judgement_order,action_taken_order,last_updated_by,last_updated_on,  counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,"
			+ "pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,pwr_uploaded_copy,"
			+ "counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated)"
			+ " select cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,  counter_filed,remarks,"
			+ "ecourts_case_status,corresponding_gp,pwr_uploaded,pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,"
			+ "appeal_filed_date,pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,cordered_impl_date,"
			+ "dismissed_copy,final_order_status,no_district_updated  from ecourts_olcms_case_details where cino=:cIno  and respondent_slno=:resident_id  ")
	int INSERTecourts_olcms_case_details_log(@Param("cIno") String cIno,@Param("resident_id") int resident_id);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set pwr_approved_gp='Yes',pwr_gp_approved_date=current_date  where cino=:cIno and respondent_slno=:resident_id ")
	int update_ecourts_olcms_case_detailsPWRAPPROVE(@Param("cIno") String cIno,@Param("resident_id") int resident_id);


	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "  update ecourts_olcms_case_details set counter_approved_gp='T',counter_approved_date=current_date, counter_approved_by=:userId "
			+ " where cino=:cIno and respondent_slno:resident_id ")
	int update_ecourts_olcmsCOUNTERAFFIDAVITAPPROVE(@Param("userId") String userId,@Param("cIno") String cIno,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "  update ecourts_olcms_case_details set counter_approved_gp='T',counter_approved_date=current_date, counter_approved_by=:userId "
			+ " where cino=:cIno and respondent_slno:resident_id ")
	int update_ecourts_olcmsCounterFiled(@Param("userId") String userId,@Param("cIno") String cIno,@Param("resident_id") int resident_id);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set pwr_approved_gp='Yes',pwr_gp_approved_date=current_date  where cino=:cIno and respondent_slno:resident_id ")
	int update_ecourts_olcmsParawiseRemarksSubmitted(@Param("cIno") String cIno,@Param("resident_id") int resident_id);

	 
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks)"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks   ) ")
	int Insert_ecourts_case_activitiesFinalAPPROVE(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,
			@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks);

	
	@Query(nativeQuery = true, value = " select ca.cino, action_type,inserted_by, to_char(inserted_on,'dd-mm-yyyy hh:MM:ss') as inserted_on , "
			+ "  coalesce(employee_id||','||nd.fullname_en||','||nd.post_name_en,'-') as assigned_to, remarks, coalesce(uploaded_doc_path,'-') as uploaded_doc_path, ca.dist_id , dn.dept_code||' - '||dn.description as deptdesc,"
			+ "  nd.fullname_en,nd.post_name_en , dm.district_name"
			+ "  from ecourts_case_activities ca inner join ecourts_gpo_ack_depts ecd on (ecd.ack_no =ca.cino) "
			+ "  left join (select distinct employee_id,fullname_en,post_name_en from nic_data) nd on (ca.assigned_to=employee_id)"
			+ "  left join dept_new dn on (ca.assigned_to=dn.dept_code)"
			+ "  left join district_mst dm on (ca.dist_id=dm.district_id)  where ca.cino=:cIno  and respondent_slno=:resident_id and ca.cino!='null' order by inserted_on ")
	List<Map<String, Object>> getActivitiesDataNEW(@Param("cIno") String cIno,@Param("resident_id") int resident_id);

	
	@Query(nativeQuery = true, value = " SELECT cino, case when length(petition_document) > 0 then petition_document else null end as petition_document,"
			+ " case when length(counter_filed_document) > 0 then counter_filed_document else null end as counter_filed_document,counter_filed_document2,counter_filed_document3,"
			+ " case when length(judgement_order) > 0 then judgement_order else null end as judgement_order,"
			+ " case when length(action_taken_order) > 0 then action_taken_order else null end as action_taken_order,"
			+ " last_updated_by, last_updated_on, counter_filed, remarks, ecourts_case_status, corresponding_gp,"
			+ "  pwr_uploaded, to_char(pwr_submitted_date,'yyyy-MM-dd') as pwr_submitted_date, to_char(pwr_received_date,'yyyy-MM-dd') as pwr_received_date,"
			+ "  pwr_approved_gp, to_char(pwr_gp_approved_date,'yyyy-MM-dd') as pwr_gp_approved_date, appeal_filed,"
			+ "  appeal_filed_copy, to_char(appeal_filed_date,'yyyy-MM-dd') as appeal_filed_date, pwr_uploaded_copy,pwr_uploaded_copy2,pwr_uploaded_copy3,action_to_perfom,counter_filed_date"
			+ "  FROM apolcms.ecourts_olcms_case_details where cino=:cIno and respondent_slno=:resident_id ")
	List<Map<String, Object>> getOLCMSCASEDATANEW(@Param("cIno") String cIno,@Param("resident_id") int resident_id);

 

}
