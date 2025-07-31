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
public interface EcourtsDeptInstructionsRepo extends JpaRepository<MyEntity, Long> {

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = "insert into ecourts_dept_instructions (cino, instructions , upload_fileno,dept_code ,dist_code,insert_by,legacy_ack_flag,status_instruction_flag ,generated_file)"
			+ " values (:cIno, :ins,:fileName,:deptCode,:distId,:userid,:oldNewType,:status_flag,:fileName2)   ")
	int insert_ecourts_dept_instructions(@Param("cIno") String cIno,@Param("ins") String ins,@Param("fileName") String fileName,@Param("deptCode") String deptCode,@Param("distId") int distId,
			@Param("userid")	String userid,@Param("oldNewType") String oldNewType,@Param("status_flag") String status_flag,@Param("fileName2") String fileName2);

	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value = " insert into ecourts_case_activities (cino , action_type , inserted_by , inserted_ip, remarks,uploaded_doc_path) "
			+ " values (:cIno,'SUBMITTED INSTRUCTIONS TO GP',:userid,CAST(:remoteAddr AS inet),:ins,:fileName)   ")
	int ecourts_case_activities_instructions(@Param("cIno") String cIno,@Param("userid") String userid,@Param("remoteAddr") String remoteAddr,@Param("ins") String ins,
			@Param("fileName") String fileName);


	
	@Query(nativeQuery = true,value = " select instructions,to_char(insert_time,'dd-mm-yyyy HH:mi:ss') as insert_time,case when  (upload_fileno like '%PDF%' or upload_fileno like '%pdf%') then upload_fileno else '-' end as upload_fileno,"
			+ "  reply_instructions,case when  (reply_upload_fileno like '%PDF%' or reply_upload_fileno like '%pdf%') then reply_upload_fileno else '-' end as reply_upload_fileno,"
			+ "  case when  (generated_file like '%PDF%' or generated_file like '%pdf%') then generated_file else '-' end as generated_file,to_char(reply_insert_time ,'dd-mm-yyyy HH:mi:ss') as reply_insert_time"
			+ "  from ecourts_dept_instructions where cino=:cIno  order by 1   ")
	List<Map<String, Object>> InstructionLegacyExistDataOld(@Param("cIno") String cIno);


	
	@Query(nativeQuery = true,value = " select a.slno ,ad.respondent_slno, a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , a.remarks ,  inserted_by , inserted_ip, upper(trim(district_name)) as district_name,"
			+ "  upper(trim(case_full_name)) as  case_full_name, a.ack_file_path, case when services_id='0' then null else services_id end as services_id,services_flag,"
			+ "  to_char(a.inserted_time,'dd-mm-yyyy') as generated_date,"
			+ "  getack_dept_desc(a.ack_no::text) as dept_descs , coalesce(a.hc_ack_no,'-') as hc_ack_no "
			+ "  from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no)"
			+ "  left join district_mst dm on (ad.dist_id=dm.district_id)"
			+ "  left join dept_new dmt on (ad.dept_code=dmt.dept_code)"
			+ "  inner join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name)"
			+ "  where a.delete_status is false and ack_type='NEW'    and (a.ack_no=:ackNoo or a.hc_ack_no=:ackNoo )  and respondent_slno='1' order by a.inserted_time desc   ")
	List<Map<String, Object>> InstructionCASESLISTNEW(@Param("ackNoo")  String ackNoo);


	@Query(nativeQuery = true,value = " select instructions,to_char(insert_time,'dd-mm-yyyy HH:mi:ss') as insert_time,case when  (upload_fileno like '%PDF%' or upload_fileno like '%pdf%') then upload_fileno else '-' end as upload_fileno,"
			+ " reply_instructions,case when  (reply_upload_fileno like '%PDF%' or reply_upload_fileno like '%pdf%') then reply_upload_fileno else '-' end as reply_upload_fileno,"
			+ " case when  (generated_file like '%PDF%' or generated_file like '%pdf%') then generated_file else '-' end as generated_file,to_char(reply_insert_time ,'dd-mm-yyyy HH:mi:ss') as reply_insert_time "
			+ " from ecourts_dept_instructions where cino=:ackNoo order by 1  ")
	List<Map<String, Object>> InstructionExistDataNew(@Param("ackNoo")  String ackNoo);
	
	
	
	
	
	
	
	
	
	
	
	
}
