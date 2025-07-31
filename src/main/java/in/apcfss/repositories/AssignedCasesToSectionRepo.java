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
public interface AssignedCasesToSectionRepo extends JpaRepository<MyEntity, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Petition' , :userId , CAST(:remoteAddr AS inet), :remarks ,:petitionDocument) ")
	int insertBack_case_activitiesPending(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("petitionDocument")  String petitionDocument);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Action Taken Order' , :userId , CAST(:remoteAddr AS inet), :remarks ,:actionTakenOrder) ")
	int insertBack_case_activitiesClosedActionTakenOrder(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("actionTakenOrder") String actionTakenOrder);

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



	@Query(nativeQuery = true, value = "select count(*) from ecourts_olcms_case_details where cino=:cIno")
	int getCinoCount(@Param("cIno") String cIno);

	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,  "
			+ "	counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,"
			+ "	 pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated) "
			+ " select cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,  counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,"
			+ "	pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,"
			+ "	counter_approved_by,respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated  from ecourts_olcms_case_details where cino=:cIno")
	int insertBack_ecourts_olcms_case_details_log(@Param("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set ecourts_case_status=:ecourtsCaseStatus,appeal_filed=:appealFiled,appeal_filed_date=to_date(:appealFiledDt,'yyyy-MM-dd'),"
			+ " remarks=:remarks ,last_updated_by=:userId,last_updated_on=now(),action_to_perfom=:actionToPerform , petition_document=:petitionDocument, "
			+ " action_taken_order=:actionTakenOrder, judgement_order=:judgementOrder, appeal_filed_copy=:appealFileCopy  where cino=:cIno  ")
	int update_ecourts_olcms_case_details(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("appealFiled") String appealFiled,@Param("appealFiledDt") String appealFiledDt,
			@Param("remarks") String remarks,@Param("userId")  String userId, @Param("actionToPerform") String actionToPerform,@Param("petitionDocument") String petitionDocument, 
			@Param("actionTakenOrder") String actionTakenOrder,@Param("judgementOrder") String judgementOrder,@Param("appealFileCopy") String appealFileCopy,@Param("cIno") String cIno);

	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details (cino, ecourts_case_status, petition_document, appeal_filed, appeal_filed_copy, judgement_order, action_taken_order,"
			+ "	last_updated_by, last_updated_on, remarks, appeal_filed_date, action_to_perfom) "
			+ " values (:cIno,:ecourtsCaseStatus,:petitionDocument,:appealFiled,:appealFileCopy,:judgementOrder, :actionTakenOrder,:userId,now(),:remarks,to_date(:appealFiledDt,'yyyy-MM-dd'),:actionToPerform     )")
	int insert_ecourts_olcms_case_details( @Param("cIno") String cIno, @Param("ecourtsCaseStatus") String ecourtsCaseStatus, @Param("petitionDocument") String petitionDocument,
			@Param("appealFiled") String appealFiled, @Param("appealFileCopy") String appealFileCopy, @Param("judgementOrder") String judgementOrder,
			@Param("actionTakenOrder") String actionTakenOrder, @Param("userId") String userId, @Param("remarks") String remarks,  @Param("appealFiledDt") String appealFiledDt,
			@Param("actionToPerform") String actionToPerform);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , section_officer_updated='T'   where cino=:cIno  ")
	int update_ecourts_case_data( @Param("ecourtsCaseStatus") String ecourtsCaseStatus, @Param("cIno") String cIno);




	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks ) "
			+ " values (:cIno, :actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks)")
	int insert_closedFinal_ecourts_case_activities(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed,@Param("userId") String userId,
			@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks);


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

	 

	@Query(nativeQuery = true, value = "select count(*) from ecourts_olcms_case_details where cino=:cIno")
	int getCinoCount2(@Param("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,  counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,"
			+ "	 pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,"
			+ "	 cordered_impl_date,dismissed_copy,final_order_status,no_district_updated) "
			+ " select cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,"
			+ "	counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,"
			+ "	 pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated  "
			+ " from ecourts_olcms_case_details where cino=:cIno ")
	int insert_Pending_ecourts_olcms_case_details_log(@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set ecourts_case_status=:ecourtsCaseStatus , counter_filed=:counterFiled,remarks=:remarks,petition_document=:petitionDocument,"
			+ " last_updated_by=:userId,last_updated_on=now(), counter_filed_document=:counterFileCopy,counter_filed_document2=:counterFileCopy2,counter_filed_document3=:counterFileCopy3, "
			+ " counter_filed_date=to_date(:counterFiledDt ,'yyyy-MM-dd'), pwr_uploaded_copy=:parawiseRemarksCopy, pwr_uploaded_copy2=:parawiseRemarksCopy2, pwr_uploaded_copy3=:parawiseRemarksCopy3,"
			+ " corresponding_gp=:relatedGp,pwr_uploaded=:parawiseRemarksSubmitted,pwr_submitted_date=to_date(:parawiseRemarksDt,'yyyy-MM-dd'),"
			+ " pwr_received_date=to_date(:dtPRReceiptToGP,'yyyy-MM-dd'),pwr_gp_approved_date=to_date(:dtPRApprovedToGP,'yyyy-MM-dd'),action_to_perfom=:actionToPerform  where cino=:cIno  ")
	int update_Pending_ecourts_olcms_case_details(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("counterFiled") String counterFiled,@Param("remarks") String remarks,
			@Param("petitionDocument") String petitionDocument,@Param("userId") String userId,@Param("counterFileCopy") String counterFileCopy,@Param("counterFileCopy2") String counterFileCopy2,
			@Param("counterFileCopy3") String counterFileCopy3, @Param("counterFiledDt") String counterFiledDt,@Param("parawiseRemarksCopy")  String parawiseRemarksCopy,
			@Param("parawiseRemarksCopy2") String parawiseRemarksCopy2,@Param("parawiseRemarksCopy3") String parawiseRemarksCopy3,@Param("relatedGp") String relatedGp,
			@Param("parawiseRemarksSubmitted") String parawiseRemarksSubmitted,@Param("parawiseRemarksDt") String parawiseRemarksDt,@Param("dtPRReceiptToGP") String dtPRReceiptToGP,
			@Param("dtPRApprovedToGP")  String dtPRApprovedToGP,@Param("actionToPerform") String actionToPerform,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details (cino, ecourts_case_status, petition_document, counter_filed_date,counter_filed_document,"
			+ "counter_filed_document2,counter_filed_document3, last_updated_by, last_updated_on,counter_filed, "
			+ " remarks,  corresponding_gp, pwr_uploaded, pwr_submitted_date, pwr_received_date,"
			+ "	 pwr_gp_approved_date, pwr_uploaded_copy,pwr_uploaded_copy2,pwr_uploaded_copy3, action_to_perfom ) "
			+ " values (:cIno,:ecourtsCaseStatus ,:petitionDocument,to_date(:counterFiledDt,'yyyy-MM-dd'),:counterFileCopy,"
			+ " :counterFileCopy2,:counterFileCopy3,:userId,now(), :counterFiled,"
			+ " :remarks, :relatedGp, :parawiseRemarksSubmitted,to_date(:parawiseRemarksDt ,'yyyy-MM-dd'),to_date(:dtPRReceiptToGP ,'yyyy-MM-dd'),"
			+ "to_date(:dtPRApprovedToGP ,'yyyy-MM-dd'),:parawiseRemarksCopy,:parawiseRemarksCopy2,:parawiseRemarksCopy3, :actionToPerform )")
	int insert_Pending_ecourts_olcms_case_details(@Param("cIno") String cIno, @Param("ecourtsCaseStatus") String ecourtsCaseStatus, @Param("petitionDocument") String petitionDocument,
			@Param("counterFiledDt") String counterFiledDt,@Param("counterFileCopy") String counterFileCopy,@Param("counterFileCopy2") String counterFileCopy2,
			@Param("counterFileCopy3") String counterFileCopy3,@Param("userId") String userId,@Param("counterFiled") String counterFiled,@Param("remarks") String remarks,@Param("relatedGp") String relatedGp,
			@Param("parawiseRemarksSubmitted") String parawiseRemarksSubmitted,@Param ("parawiseRemarksDt") String parawiseRemarksDt,@Param("dtPRReceiptToGP")  String dtPRReceiptToGP,@Param("dtPRApprovedToGP") String dtPRApprovedToGP,
			@Param("parawiseRemarksCopy") String parawiseRemarksCopy,@Param("parawiseRemarksCopy2") String parawiseRemarksCopy2,@Param("parawiseRemarksCopy3") String parawiseRemarksCopy3,
			@Param("actionToPerform") String actionToPerform);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , mlo_no_updated='T' where cino=:cIno  ")
	int update_Pending_ecourts_case_data4510(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , section_officer_updated='T' where cino=:cIno  ")
	int update_Pending_ecourts_case_dataELSE(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks ) "
			+ "values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks )")
	int insert_PendingFINALEcourts_case_activities(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks);

	//----------------------------------------------------------
	
	
	
	
	@Query(nativeQuery = true, value = "select a.*, prayer from ecourts_case_data a left join nic_prayer_data np on (a.cino=np.cino) where a.cino=:cIno")
	List<Map<String, Object>> getCaseData(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select  a.ack_no ,  dept_code,respondent_slno  , (select district_name from district_mst dm where dm.district_id::text=b.distid::text) as district_name, "
			+ "  servicetpye  ,    advocatename , advocateccno,   (select case_short_name from case_type_master ctm where ctm.sno::text=b.casetype::text) as casetype, "
			+ " maincaseno ,   remarks , inserted_time::date ,  inserted_by ,  ack_file_path   , "
			+ " petitioner_name    ,  reg_year  , reg_no , mode_filing  , case_category  ,  hc_ack_no "
			+ " from ecourts_gpo_ack_depts a inner join ecourts_gpo_ack_dtls b on (a.ack_no=b.ack_no) where b.ack_no=:cIno   ")
	List<Map<String, Object>> getCaseDataNEW(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select a.*, prayer from ecourts_case_data a  left join nic_prayer_data np on (a.cino=np.cino) where a.cino=:cIno")
	List<Map<String, Object>> getUSERSLIST(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select COALESCE(hc_ack_no, '-')  as hc_ack_no ,d.case_short_name,c.ack_no from ecourts_case_data a inner join ecourts_gpo_ack_dtls c on ((a.type_name_reg ||'/'||a.reg_no ||'/'||a.reg_year)=maincaseno)"
			+ "  inner join case_type_master d on (d.sno=c.casetype::int) where a.cino=:cIno   ")
	List<Map<String, Object>> getDocumentsList(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select * from ecourts_case_acts where cino=:cIno   ")
	List<Map<String, Object>> getActlist(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select coalesce(c.hc_ack_no,'-') as hc_ack_no,c.ack_no,d.case_short_name from ecourts_case_data a "
			+ " inner join ecourts_gpo_ack_dtls c on ((a.type_name_reg ||'/'||a.reg_no ||'/'||a.reg_year)=maincaseno) inner join case_type_master d on (d.sno=c.casetype::int) where a.cino=:cIno ")
	List<Map<String, Object>> getOtherDocumentsList(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select coalesce(c.hc_ack_no,'-') as hc_ack_no,c.ack_no,d.instructions,case when  (d.upload_fileno like '%PDF%' or d.upload_fileno like '%pdf%') then d.upload_fileno else '-' end as upload_fileno,"
			+ " case when  (generated_file like '%PDF%' or generated_file like '%pdf%') then generated_file else '-' end as generated_file  from ecourts_case_data a "
			+ " inner join ecourts_gpo_ack_dtls c on ((a.type_name_reg ||'/'||a.reg_no ||'/'||a.reg_year)=maincaseno)  inner join ecourts_dept_instructions d on (c.ack_no=d.cino) where a.cino=:cIno ")
	List<Map<String, Object>> getMappedInstructionList(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select coalesce(c.hc_ack_no,'-') as hc_ack_no,c.ack_no,d.pwr_uploaded ,d.pwr_submitted_date ,coalesce(d.pwr_uploaded_copy,'-') as pwr_uploaded_copy from ecourts_case_data a "
			+ " inner join ecourts_gpo_ack_dtls c on ((a.type_name_reg ||'/'||a.reg_no ||'/'||a.reg_year)=maincaseno)  inner join ecourts_olcms_case_details  d on (c.ack_no=d.cino)  where a.cino=:cIno ")
	List<Map<String, Object>> getMappedPWRList(@Param("cIno") String cIno);
	
	
	
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_finalorder where cino=:cIno   ")
	List<Map<String, Object>> getOrderList(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_iafiling where cino=:cIno   ")
	List<Map<String, Object>> getIAFILINGLIST(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_interimorder where cino=:cIno   ")
	List<Map<String, Object>> getINTERIMORDERSLIST(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_link_cases where cino=:cIno   ")
	List<Map<String, Object>> getLINKCASESLIST(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_objections where cino=:cIno   ")
	List<Map<String, Object>> getOBJECTIONSLIST(@Param("cIno") String cIno);
	
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_historyofcasehearing where cino=:cIno order by hearing_date desc  ")
	List<Map<String, Object>> getCASEHISTORYLIST(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_pet_extra_party where cino=:cIno ")
	List<Map<String, Object>> getPETEXTRAPARTYLIST(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select b.party_no,b.res_name as party_name, b.address from nic_resp_addr_data b left join ecourts_res_extra_party a on (b.cino=a.cino and b.party_no-1=coalesce(trim(a.party_no),'0')::int4) where b.cino=:cIno order by b.party_no")
	List<Map<String, Object>> getRESEXTRAPARTYLIST(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select cino,action_type,inserted_by,to_char(inserted_on,'dd-Mon-yyyy hh24:mi:ss PM') as inserted_on,"
			+ " assigned_to,remarks as remarks, coalesce(uploaded_doc_path,'-') as uploaded_doc_path from ecourts_case_activities where cino=:cIno and cino!='null' order by inserted_on desc ")
	List<Map<String, Object>> getACTIVITIESDATA(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "SELECT cino, case when length(petition_document) > 0 then petition_document else null end as petition_document,"
			+ " case when length(counter_filed_document) > 0 then counter_filed_document else null end as counter_filed_document, "
			+ " case when length(judgement_order) > 0 then judgement_order else null end as judgement_order, "
			+ " case when length(action_taken_order) > 0 then action_taken_order else null end as action_taken_order, "
			+ " last_updated_by, last_updated_on, counter_filed, remarks, ecourts_case_status, corresponding_gp, "
			+ " pwr_uploaded, to_char(pwr_submitted_date,'yyyy-MM-dd') as pwr_submitted_date, to_char(pwr_received_date,'yyyy-MM-dd') as pwr_received_date, "
			+ " pwr_approved_gp, to_char(pwr_gp_approved_date,'yyyy-MM-dd') as pwr_gp_approved_date, appeal_filed, "
			+ " appeal_filed_copy, to_char(appeal_filed_date,'yyyy-MM-dd') as appeal_filed_date, pwr_uploaded_copy,to_char(counter_filed_date,'yyyy-MM-dd') as counter_filed_date,action_to_perfom "
			+ " FROM apolcms.ecourts_olcms_case_details  where cino=:cIno ")
	List<Map<String, Object>> getOLCMSCASEDATA(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "SELECT cino, case when length(petition_document) > 0 then petition_document else null end as petition_document,cordered_impl_date,final_order_status, "
			+ " case when length(counter_filed_document) > 0 then counter_filed_document else null end as counter_filed_document,"
			+ "case when length(dismissed_copy) > 0 then dismissed_copy else null end as dismissed_copy,"
			+ " case when length(judgement_order) > 0 then judgement_order else null end as judgement_order,"
			+ " case when length(action_taken_order) > 0 then action_taken_order else null end as action_taken_order,"
			+ " last_updated_by, last_updated_on, counter_filed, remarks, ecourts_case_status, corresponding_gp, "
			+ " pwr_uploaded, to_char(pwr_submitted_date,'mm/dd/yyyy') as pwr_submitted_date, to_char(pwr_received_date,'mm/dd/yyyy') as pwr_received_date, "
			+ " pwr_approved_gp, to_char(pwr_gp_approved_date,'mm/dd/yyyy') as pwr_gp_approved_date, appeal_filed, "
			+ " appeal_filed_copy, to_char(appeal_filed_date,'mm/dd/yyyy') as appeal_filed_date, pwr_uploaded_copy  "
			+ " FROM apolcms.ecourts_olcms_case_details where cino=:cIno ")
	List<Map<String, Object>> getOLCMSCASEDATAFINALandINTERIM(@Param("cIno") String cIno);
	
	
	
	
	
	@Query(nativeQuery = true, value = "select cino,instructions, to_char(insert_time,'dd-Mon-yyyy hh24:mi:ss PM') as insert_time,coalesce(insert_by,'0') as insert_by,legacy_ack_flag,"
			+ "  case when  (upload_fileno like '%PDF%' or upload_fileno like '%pdf%') then upload_fileno else '-' end as upload_fileno,"
			+ " case when  (generated_file like '%PDF%' or generated_file like '%pdf%') then generated_file else '-' end as generated_file,"
			+ " status_instruction_flag,reply_flag,slno,reply_serno, reply_instructions ,"
			+ " case when  (reply_upload_fileno like '%PDF%' or reply_upload_fileno like '%pdf%') then reply_upload_fileno else '-' end as reply_upload_fileno, reply_insert_time, reply_insert_by "
			+ " from ecourts_dept_instructions where cino=:cIno order by insert_time::timestamp desc ")
	List<Map<String, Object>> getDEPTNSTRUCTIONS(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select a.*, prayer from ecourts_case_data a left join nic_prayer_data np on (a.cino=np.cino) where a.cino=:cIno")
	List<Map<String, Object>> getUsersListForUpdate(@Param("authentication") Authentication authentication,@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select emailid as value, full_name||' ('|| replace(emailid,'@ap.gov.in','') ||')' as label from ecourts_mst_gps a "
			+ " inner join ecourts_mst_gp_dept_map b on (a.emailid=b.gp_id) where b.dept_code=:deptCode  order by emailid")
	List<Map<String, Object>> getGPSList(@Param("deptCode") String deptCode);
	
	
	@Query(nativeQuery = true, value = "select * from ecourts_case_acts where cino=:cIno ")
	List<Map<String, Object>> getActlistForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_finalorder where cino=:cIno ")
	List<Map<String, Object>> getOrderlistForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_iafiling where cino=:cIno ")
	List<Map<String, Object>> getIAFILINGLISTForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_interimorder where cino=:cIno ")
	List<Map<String, Object>> getINTERIMORDERSLISTForUpdate(@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_link_cases where cino=:cIno ")
	List<Map<String, Object>> getLINKCASESLISTForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_case_objections where cino=:cIno ")
	List<Map<String, Object>> getOBJECTIONSLISTForUpdate(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_historyofcasehearing where cino=:cIno ")
	List<Map<String, Object>> getCASEHISTORYLISTForUpdate(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select  * from apolcms.ecourts_pet_extra_party where cino=:cIno ")
	List<Map<String, Object>> getPETEXTRAPARTYLISTForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select b.party_no,b.res_name as party_name, b.address from nic_resp_addr_data b left join ecourts_res_extra_party a on (b.cino=a.cino and b.party_no-1=coalesce(trim(a.party_no),'0')::int4) where b.cino=:cIno order by b.party_no ")
	List<Map<String, Object>> getRESEXTRAPARTYLISTForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select cino,action_type,inserted_by,to_char(inserted_on,'dd-Mon-yyyy hh24:mi:ss PM') as inserted_on,"
			+ " assigned_to,remarks as remarks, coalesce(uploaded_doc_path,'-') as uploaded_doc_path from ecourts_case_activities where cino =:cIno and cino!='null' order by inserted_on desc")
	List<Map<String, Object>> getACTIVITIESDATAForUpdate(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "select case_status,ecourts_case_status from ecourts_case_data where cino=:cIno ")
	List<Map<String, Object>> getDataStatus(@Param("cIno") String cIno);
	
	
	@Query(nativeQuery = true, value = "SELECT cino, case when length(petition_document) > 0 then petition_document else null end as petition_document,"
			+ " case when length(counter_filed_document) > 0 then counter_filed_document else null end as counter_filed_document,  "
			+ " case when length(judgement_order) > 0 then judgement_order else null end as judgement_order,  "
			+ " case when length(action_taken_order) > 0 then action_taken_order else null end as action_taken_order,  "
			+ " last_updated_by, last_updated_on, counter_filed, remarks, ecourts_case_status, corresponding_gp, "
			+ " pwr_uploaded, to_char(pwr_submitted_date,'yyyy-MM-dd') as pwr_submitted_date, to_char(pwr_received_date,'yyyy-MM-dd') as pwr_received_date,  "
			+ " pwr_approved_gp, to_char(pwr_gp_approved_date,'yyyy-MM-dd') as pwr_gp_approved_date, appeal_filed,   "
			+ " appeal_filed_copy, to_char(appeal_filed_date,'yyyy-MM-dd') as appeal_filed_date, pwr_uploaded_copy ,pwr_uploaded_copy2 ,pwr_uploaded_copy3,"
			+ " counter_filed_document2,counter_filed_document3,to_char(counter_filed_date,'yyyy-MM-dd') as counter_filed_date,dismissed_copy,action_to_perfom  "
			+ " FROM apolcms.ecourts_olcms_case_details where cino=:cIno ")
	List<Map<String, Object>> getOLCMSCASEDATAForUpdate(@Param("cIno") String cIno);

	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus,case_status='98' where cino=:cIno  and dept_code=:deptCode ")
	int update_ecourts_case_dataPrivate(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks) ")
	int insert_ecourts_case_activitiesPrivate(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , case_status='96'  where cino=:cIno  and dept_code=:deptCode ")
	int update_ecourts_case_dataGoI(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks) ")
	int insert_ecourts_case_activitiesGoI(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , case_status='95'  where cino=:cIno  and dept_code=:deptCode ")
	int update_ecourts_case_dataCentralTax(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks) ")
	int insert_ecourts_case_activitiesCentralTax(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , case_status='94'  where cino=:cIno  and dept_code=:deptCode ")
	int update_ecourts_case_dataIncomeTax(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks) ")
	int insert_ecourts_case_activitiesIncomeTax(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set ecourts_case_status=:ecourtsCaseStatus , case_status='97'  where cino=:cIno  and dept_code=:deptCode ")
	int update_ecourts_case_dataPSU(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("cIno") String cIno,@Param("deptCode") String deptCode);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks) ")
	int insert_ecourts_case_activitiesPSU(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks);
	
	//forwards---cases================================================================
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno  and mlo_no_updated='T' and case_status='2' ")
	int ecourts_case_data_frwd_Sec(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno  and mlo_no_updated='T' and case_status='4' ")
	int ecourts_case_data_frwd_Hod(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno  and mlo_no_updated='T' and case_status='8' ")
	int ecourts_case_data_frwd_DcToHod(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select trim(emailid) from mlo_details where user_id=:deptCode")
	String getFwrdOffMlo(@Param("deptCode") String deptCode);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno   and section_officer_updated='T' and case_status='5' ")
	int ecourts_case_data_frwd_Mlo(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select emailid from nodal_officer_details where dept_id=:deptCode  and user_id not like '%DC-%'")
	String getFwrdOffNo(@Param("deptCode") String deptCode);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno   and section_officer_updated='T' and case_status='9' ")
	int ecourts_case_data_frwd_No(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	
	@Query(nativeQuery = true, value = "select emailid from nodal_officer_details where dept_id=:deptCode  and coalesce(dist_id,0)=:distId   ")
	String getFwrdOffDNo(@Param("deptCode") String deptCode,@Param("distId") int distId);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno   and section_officer_updated='T' and case_status='10' ")
	int ecourts_case_data_frwd_DNo(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks)"
			+ " values (:cIno,'CASE FORWARDED',:userId,CAST(:remoteAddr AS inet),:fwdOfficer,:remarks) ")
	int Insert_ecourts_case_activitiesFinalFrwd(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("fwdOfficer") String fwdOfficer,
			@Param("remarks") String remarks);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_emp_assigned_dtls (cino, dept_code, emp_section, emp_post, emp_id, inserted_time, inserted_ip, inserted_by, emp_user_id)"
			+ " values (:cIno,:deptCode,:empSection,:empPost,:empId,now(),CAST(:remoteAddr AS inet),:userId,:fwdOfficer   ) ")
	int Insert_ecourts_case_emp_assigned_dtlsFinalFrwd(@Param("cIno") String cIno, @Param("deptCode") String deptCode,@Param("empSection") String empSection,@Param("empPost") String empPost,
			@Param("empId") String empId,@Param("remoteAddr")  String remoteAddr,@Param("userId")  String userId,@Param("fwdOfficer")  String fwdOfficer);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno   and mlo_no_updated='T' and case_status='1' ")
	int ecourts_case_data_frwd_secToGp(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:fwdOfficer  where cino=:cIno  and mlo_no_updated='T' and case_status='3' ")
	int ecourts_case_data_frwd_HodToGp(@Param("newStatus") int newStatus,@Param("fwdOfficer") String fwdOfficer,@Param("cIno") String cIno);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks)"
			+ " values (:cIno,'CASE FORWARDED TO GP',:userId,CAST(:remoteAddr AS inet),:fwdOfficer,:remarks   ) ")
	int Insert_ecourts_case_activitiesFinalFrwdGP(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("fwdOfficer") String fwdOfficer,
			@Param("remarks") String remarks);
	 
	 
	 
	@Query(nativeQuery = true, value = " select  emailid from section_officer_details ad inner join ecourts_case_activities ea on ((ad.employeeid=ea.assigned_to) or (ad.emailid=ea.assigned_to)) where ea.cino=:cIno"
			+ " and ea.action_type ilike 'CASE ASSSIGNED%' order by ea.inserted_on desc limit 1  ")
	String getbackToSectionUser(@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:backToSectionUser  where cino=:cIno and section_officer_updated='T' and case_status='2' ")
	int update_ecourts_case_data_bck_MloToSection(@Param("newStatus") int newStatus,@Param("backToSectionUser") String backToSectionUser,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:backToSectionUser  where cino=:cIno and section_officer_updated='T' and case_status='4' ")
	int update_ecourts_case_data_bck_NoToSection(@Param("newStatus") int newStatus,@Param("backToSectionUser") String backToSectionUser,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status=:newStatus , assigned_to=:backToSectionUser  where cino=:cIno and section_officer_updated='T' and case_status='8' ")
	int update_ecourts_case_data_bck_DNoToDSection(@Param("newStatus") int newStatus,@Param("backToSectionUser") String backToSectionUser,@Param("cIno") String cIno);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks)"
			+ " values (:cIno,'CASE SENT BACK',:userId,CAST(:remoteAddr AS inet),:backToSectionUser,:remarks   ) ")
	int Insert_ecourts_case_activitiesFinalSendBack(@Param("cIno") String cIno,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("backToSectionUser") String backToSectionUser,
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
			+ "dismissed_copy,final_order_status,no_district_updated  from ecourts_olcms_case_details where cino=:cIno ")
	int INSERTecourts_olcms_case_details_log(@Param("cIno") String cIno);
	
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set pwr_approved_gp='Yes',pwr_gp_approved_date=current_date  where cino=:cIno ")
	int update_ecourts_olcms_case_detailsPWRAPPROVE(@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set counter_approved_gp='T',counter_approved_date=current_date, counter_approved_by=:userId where cino=:cIno ")
	int update_ecourts_olcmsCOUNTERAFFIDAVITAPPROVE(@Param("userId") String userId,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set counter_approved_gp='T',counter_approved_date=current_date, counter_approved_by=:userId where cino=:cIno ")
	int update_ecourts_olcmsCounterFiled(@Param("userId") String userId,@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set pwr_approved_gp='Yes',pwr_gp_approved_date=current_date  where cino=:cIno ")
	int update_ecourts_olcmsParawiseRemarksSubmitted(@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks)"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks   ) ")
	int Insert_ecourts_case_activitiesFinalAPPROVE(@Param("cIno") String cIno,@Param("actionPerformed") String actionPerformed, @Param("userId") String userId,
			@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks);
	 
	
	@Query(nativeQuery = true, value = "select dept_code,dist_id from ecourts_case_data where cino=:cIno   ")
	List<Map<String, Object>> caseData(@Param("cIno") String cIno);
	 
	
	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED' and "
			+ " assigned_to in (select emailid from mlo_details where user_id=:deptCodeC ) order by inserted_on desc limit 1  ")
	String ScetionSecDept(@Param("cIno") String cIno,@Param("deptCodeC") String deptCodeC);
	
	
	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED'  and "
			+ " assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) = 0) order by inserted_on desc limit 1 ")
	String ScetionHOD(@Param("cIno") String cIno,@Param("deptCodeC") String deptCodeC);
	
	
	@Query(nativeQuery = true, value = "select inserted_by from ecourts_case_activities where cino=:cIno and action_type='CASE FORWARDED'  and "
			+ " assigned_to in (select emailid from nodal_officer_details where dept_id=:deptCodeC and coalesce(dist_id,0) > 0) order by inserted_on desc limit 1 ")
	String ScetionDIST(@Param("cIno") String cIno,@Param("deptCodeC") String deptCodeC);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp,section_officer_updated=null, "
			+ " mlo_no_updated=null where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int ReturnBackPWR(@Param("newStatus") int newStatus,@Param("assigned2Emp") String assigned2Emp,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp,section_officer_updated=null, "
			+ " mlo_no_updated=null where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int ReturnBackCOUNTERAFFIDAVIT(@Param("newStatus") int newStatus,@Param("assigned2Emp") String assigned2Emp,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp  "
			+ "   where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int ReturnBackCounterFiled(@Param("newStatus") int newStatus,@Param("assigned2Emp") String assigned2Emp,@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " update ecourts_olcms_case_details set counter_approved_gp='F' where cino=:cIno  ")
	int ReturnBackcounter_approved_gp(@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  case_status=:newStatus, assigned_to=:assigned2Emp  "
			+ " where cino=:cIno and section_officer_updated='T' and mlo_no_updated='T' ")
	int ReturnBackParawiseRemarksSubmitted(@Param("newStatus") int newStatus,@Param("assigned2Emp") String assigned2Emp,@Param("cIno") String cIno);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino,petition_document, counter_filed_document,   "
			+ "judgement_order,action_taken_order,last_updated_by,last_updated_on,  counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,"
			+ "pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,appeal_filed,appeal_filed_copy,appeal_filed_date,pwr_uploaded_copy,"
			+ "counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated) "
			+ " select cino,petition_document, counter_filed_document,   judgement_order,action_taken_order,last_updated_by,last_updated_on,  "
			+ "counter_filed,remarks,ecourts_case_status,corresponding_gp,pwr_uploaded,pwr_submitted_date,pwr_received_date,pwr_approved_gp,pwr_gp_approved_date,"
			+ "appeal_filed,appeal_filed_copy,appeal_filed_date,pwr_uploaded_copy,counter_approved_gp,action_to_perfom,counter_approved_date,counter_approved_by,"
			+ "respondent_slno,cordered_impl_date,dismissed_copy,final_order_status,no_district_updated  from ecourts_olcms_case_details where cino=:cIno  ")
	int ReturnBackecourts_olcms_case_details_log(@Param("cIno") String cIno);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, assigned_to)"
			+ " values (:cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks,:assigned2Emp   ) ")
	int ReturnBackecourts_olcms_case_details_log(@Param("cIno") String cIno, @Param("actionPerformed") String actionPerformed,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks")	String remarks,@Param("assigned2Emp") String assigned2Emp);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set dept_code=:login_deptId,assigned=true, assigned_to=:emailId,case_status=:newStatusCode, dist_id=:user_dist where cino=:cIno ")
	int ecourts_case_dataMultiCases2SectionLegacy(@Param("login_deptId") String login_deptId,@Param("emailId") String emailId,@Param("newStatusCode") int newStatusCode,@Param("user_dist") int user_dist,@Param("cIno") String cIno);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ,dist_id) "
			+ " values (:cIno,:activityDesc, :userId, CAST(:remoteAddr AS inet),:emailId,:caseRemarks,:user_dist) ")
	int insert_case_activitiesMultiCases2SectionLegacy(@Param("cIno") String cIno,@Param("activityDesc")  String activityDesc,@Param("userId")  String userId,@Param("remoteAddr")  String remoteAddr,@Param("emailId")  String emailId,
			@Param("caseRemarks") 	String caseRemarks,@Param("user_dist")  int user_dist);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, assigned_to , remarks ) "
			+ " values (:splitId1,'CASE ASSSIGNED',:userId,CAST(:remoteAddr AS inet) ,:sendBack_dept_code,NULL  ) ")
	int DeptHODSendBackLegacyCase_activities(@Param("splitId1") String splitId1,@Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("sendBack_dept_code") String sendBack_dept_code);
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set  dept_code=:sendBack_dept_code,case_status=:newStatusCode, dist_id=:dist_id ,assigned_to=null,assigned=null where cino in (:splitId1 ) ")
	int DeptHODSendBackLegacy_ecourts_case_data(@Param("sendBack_dept_code") String sendBack_dept_code,@Param("newStatusCode") int newStatusCode,@Param("dist_id") int dist_id,
			@Param("splitId1")	String splitId1);
	
	
	@Query(nativeQuery = true, value = "select * from ecourts_case_finalorder where cino=:cIno  ")
	List<Map<String, Object>> getFinalData(@Param("cIno") String cIno);
	
	@Query(nativeQuery = true, value = "select * from ecourts_case_interimorder where cino=:cIno  ")
	List<Map<String, Object>> getInterimData(@Param("cIno") String cIno);
	
	
	
	
	
	
	
	
	
	
	
	
}
