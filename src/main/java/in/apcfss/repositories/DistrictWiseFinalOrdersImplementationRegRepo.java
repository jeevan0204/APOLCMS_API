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
public interface DistrictWiseFinalOrdersImplementationRegRepo extends JpaRepository<MyEntity, Long> {

	@Query(nativeQuery = true,value = " select a.*, prayer from ecourts_case_data a left join nic_prayer_data np on (a.cino=np.cino) where a.cino=:cIno  ")
	List<Map<String, Object>> finalCasesImplData(@Param("cIno") String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set appeal_filed='No',appeal_filed_copy=null,appeal_filed_date=null where cino=:cIno  ")
	int AppealFileCopyEMPTY(@Param("cIno") String cIno);


 
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Action Taken Order' , :userId , CAST(:remoteAddr AS inet), :remarks ,:actionTakenOrder) ")
	int insert_activitiesFINALActionTakenOrder(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("actionTakenOrder") String actionTakenOrder);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Judgement Order' , :userId , CAST(:remoteAddr AS inet), :remarks ,:judgementOrder) ")
	int insert_activitiesFINALJudgementOrder(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,@Param("remarks") String remarks,@Param("judgementOrder") String judgementOrder);



	@Query(nativeQuery = true,value = " select count(*) from ecourts_olcms_case_details where cino=:cIno  ")
	int getCinoCount(@Param("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details_log (cino, petition_document, counter_filed_document, judgement_order, action_taken_order, last_updated_by, last_updated_on,"
			+ " counter_filed, remarks, ecourts_case_status, corresponding_gp, pwr_uploaded, pwr_submitted_date, pwr_received_date, pwr_approved_gp, pwr_gp_approved_date, appeal_filed, appeal_filed_copy, "
			+ " appeal_filed_date, pwr_uploaded_copy, counter_approved_gp, action_to_perfom, counter_approved_date, counter_approved_by, respondent_slno, is_orderimplemented, counter_filed_date,"
			+ "  cordered_impl_date, dismissed_copy,final_order_status,no_district_updated ) "
			+ " select cino, petition_document, counter_filed_document, judgement_order, action_taken_order, last_updated_by, last_updated_on, counter_filed, remarks, ecourts_case_status, corresponding_gp,"
			+ " pwr_uploaded, pwr_submitted_date, pwr_received_date, pwr_approved_gp, pwr_gp_approved_date, appeal_filed, appeal_filed_copy, appeal_filed_date, pwr_uploaded_copy, counter_approved_gp, "
			+ " action_to_perfom, counter_approved_date, counter_approved_by, respondent_slno, is_orderimplemented, counter_filed_date, cordered_impl_date, dismissed_copy,"
			+ " final_order_status,no_district_updated  from ecourts_olcms_case_details where cino=:cIno ")
	int ecourts_olcms_case_details_log(@Param("cIno") String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set final_order_status=:ecourtsCaseStatus ,judgement_order=:judgementOrder,cordered_impl_date=to_date(:implementedDt,'yyyy-MM-dd'),"
			+ " remarks=:remarks   , last_updated_by=:userId  , last_updated_on=now(),action_taken_order=:actionTakenOrder ,no_district_updated='T',ecourts_case_status='Implemented' where cino=:cIno ")
	int update_ecourts_olcms_case_details(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("judgementOrder")  String judgementOrder,@Param("implementedDt")  String implementedDt,
			@Param("remarks")  String remarks,@Param("userId")  String userId,@Param("actionTakenOrder")  String actionTakenOrder,@Param("cIno")  String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_olcms_case_details (cino, final_order_status, judgement_order, action_taken_order , last_updated_by, last_updated_on, "
			+ " remarks,ecourts_case_status,cordered_impl_date,no_district_updated) "
			+ " values (:cIno, :ecourtsCaseStatus,:judgementOrder,:actionTakenOrder,:userId,now(),:remarks,'Implemented', to_date(:implementedDt,'yyyy-MM-dd'),'T')")
	int insert_ecourts_olcms_case_details(@Param("cIno") String cIno,@Param("ecourtsCaseStatus")  String ecourtsCaseStatus,@Param("judgementOrder")  String judgementOrder,
			@Param("actionTakenOrder")  String actionTakenOrder,@Param("userId")  String userId,@Param("remarks")  String remarks,@Param("implementedDt")  String implementedDt);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status='99',ecourts_case_status='Closed' where cino=:cIno ")
	int update_ecourts_case_data_Implemented(@Param("cIno") String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks )"
			+ " values ( :cIno,:actionPerformed,:userId,CAST(:remoteAddr AS inet),:remarks ) ")
	int insert_ecourts_case_activities_Implemented(@Param("cIno") String cIno,@Param("actionPerformed")  String actionPerformed,@Param("userId")  String userId,
			@Param("remoteAddr") String remoteAddr,@Param("remarks")  String remarks);


 
	 
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Appeal Copy' , :userId , CAST(:remoteAddr AS inet), :remarks ,:appealFileCopy) ")
	int insert_activitiesFINALappealFileCopy(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks,@Param("appealFileCopy") String appealFileCopy);



	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set final_order_status=:ecourtsCaseStatus ,appeal_filed='Yes', appeal_filed_copy=:appealFileCopy, "
			+ " appeal_filed_date=to_date(:appealFiledDt,'yyyy-MM-dd') ,last_updated_by=:userId  , last_updated_on=now(),no_district_updated='T' ,remarks=:remarks where cino=:cIno ")
	int update_ecourts_olcms_case_detailsAPPEAL(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("appealFileCopy")  String appealFileCopy,@Param("appealFiledDt")  String appealFiledDt,
			@Param("userId") String userId,@Param("remarks")  String remarks,@Param("cIno")  String cIno);



	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details (cino, final_order_status,appeal_filed, appeal_filed_copy, last_updated_by, last_updated_on,  "
			+ " remarks,  appeal_filed_date,no_district_updated)  "
			+ " values ( :cIno,:ecourtsCaseStatus,'Yes', :appealFileCopy,:userId , now(),:remarks),to_date(:appealFiledDt,'yyyy-MM-dd') ")
	int insert_ecourts_olcms_case_detailsAPPEAL(@Param("cIno")  String cIno,@Param("ecourtsCaseStatus")  String ecourtsCaseStatus,@Param("appealFileCopy") String appealFileCopy,
			@Param("userId")  String userId,@Param("remarks")  String remarks, @Param("appealFiledDt") String appealFiledDt);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status='2' ,assigned=false ,assigned_to=null,ecourts_case_status='Pending',section_officer_updated=null,mlo_no_updated=null  where cino=:cIno ")
	int UPDATEecourts_case_dataR4(@Param("cIno") String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status='4' ,assigned=false ,assigned_to=null,ecourts_case_status='Pending',section_officer_updated=null,mlo_no_updated=null  where cino=:cIno ")
	int UPDATEecourts_case_dataR9(@Param("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status='8' ,assigned=false ,assigned_to=null,ecourts_case_status='Pending',section_officer_updated=null,mlo_no_updated=null  where cino=:cIno ")
	int UPDATEecourts_case_dataELSE(@Param("cIno") String cIno);


	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set appeal_filed='No',appeal_filed_copy=null,appeal_filed_date=null where cino=:cIno ")
	int UPDATEecourts_olcms_case_detailsDismissed(@Param("cIno") String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks, uploaded_doc_path ) "
			+ " values (:cIno , 'Uploaded Dismissed Copy' , :userId , CAST(:remoteAddr AS inet), :remarks ,:dismissedFileCopy) ")
	int insert_activitiesFINALDismissedFileCopy(@Param("cIno") String cIno,  @Param("userId") String userId,@Param("remoteAddr") String remoteAddr,
			@Param("remarks") String remarks,@Param("dismissedFileCopy") String dismissedFileCopy);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_olcms_case_details set final_order_status=:ecourtsCaseStatus ,remarks=:remarks   ,ecourts_case_status='dismissed' ,"
			+ " dismissed_copy=:dismissedFileCopy , last_updated_by=:userId,last_updated_on=now(),no_district_updated='T'  where cino=:cIno ")
	int update_ecourts_olcms_case_detailsDismissedFileCopy(@Param("ecourtsCaseStatus") String ecourtsCaseStatus,@Param("remarks") String remarks,
			@Param("dismissedFileCopy") String dismissedFileCopy,@Param("userId") String userId,@Param("cIno") String cIno);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_olcms_case_details (cino, final_order_status,ecourts_case_status, dismissed_copy,  last_updated_by, last_updated_on,  remarks,no_district_updated) "
			+ " values (:cIno ,:ecourtsCaseStatus,'dismissed',:dismissedFileCopy,:userId , now(),:remarks,'T') ")
	int insert_ecourts_olcms_case_detailsDismissedFileCopy(@Param("cIno") String cIno,@Param("ecourtsCaseStatus") String ecourtsCaseStatus,
			@Param("dismissedFileCopy")	String dismissedFileCopy,@Param("userId") String userId,@Param("remarks") String remarks);



	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "update ecourts_case_data set case_status='99',ecourts_case_status='dismissed' where cino=:cIno ")
	int update_ecourts_case_dataDismissedFileCopy(@Param("cIno") String cIno);

	
	
	
	
	
	
	
	
}
