package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.repositories.DistrictWiseFinalOrdersImplementationRegRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public abstract class DistrictWiseAssigmentCasesAbstractServiceImpl implements DistrictWiseAssigmentCasesAbstractService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getDistrictWiseAssigmentCasesList(Authentication authentication, String section_code){
	
	UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String  sql="" ;

		try {
			if(roleId.equals("9") ) {

				if(section_code!=null && section_code.equals("N"))
				{
					sql=" select  case when district_name IS NULL then '"+deptCode+" / HOD' else district_name end AS district_name,coalesce(dist_id,'0') as dist_id , total,uploaded,(total-uploaded) as not_uploaded  from "
							+ " (select b.district_name,c.dist_id,"
							+ " sum(case when c.ecourts_case_status in ('Pending','Closed','Private')  then 1 else 0 end) as uploaded,"
							+ " count( a.ack_no) as total from ecourts_gpo_ack_dtls a inner join ecourts_gpo_ack_depts c on (a.ack_no=c.ack_no) left join district_mst b on (c.dist_id=b.district_id)"
							+ " left join ecourts_olcms_case_details ecod on(a.ack_no=ecod.cino )"
							+ " where dept_code='"+deptCode+"' group by c.dist_id,district_name )x order by district_name ";

				}
				else {
                     System.out.println("---");
					sql=" select  case when district_name IS NULL then '"+deptCode+" / HOD' else district_name end AS district_name,coalesce(dist_id,'0') as dist_id , total,uploaded,(total-uploaded) as not_uploaded  from "
							+ " (select b.district_name,dist_id,"
							+ " sum(case when a.ecourts_case_status in ('Pending','Closed','Private') then 1 else 0 end) as uploaded,"
							+ " count(*) as total from ecourts_case_data a left join district_mst b on (a.dist_id=b.district_id)"
							+ " left join ecourts_olcms_case_details ecod on(a.cino=ecod.cino )"
							+ " where dept_code='"+deptCode+"' group by dist_id,district_name )x order by district_name ";
				}

			}
			System.out.println("InterimOrderFinalOrderService SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getDistrictWiseAssigmentCasesDetails(Authentication authentication, String section_code, String actionType, String email, String deptName, String uploadValue, String dist_id){
	
	UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		int distCode = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String  sql="",sqlCondition="" ;

		try {
			if (uploadValue.equals("U")) {
				
				sqlCondition = " and a.ecourts_case_status in ('Pending','Closed','Private')";
			
			} else if (uploadValue.equals("NU")) {
			
				sqlCondition = " and a.ecourts_case_status is null ";
			} 
			
			if(section_code!=null && section_code.equals("N"))
			{
				sql = "select  a.ack_no,servicetpye,advocatename,advocateccno,casetype,maincaseno,petitioner_name,egd.inserted_time from ecourts_gpo_ack_depts  a "
						+ " inner join ecourts_gpo_ack_dtls egd on (a.ack_no=egd.ack_no) left join ecourts_olcms_case_details ecod on(a.ack_no=ecod.cino )"
						+ "where (dept_code='"+userId+"') and  (dist_id='"+distCode+"')  "+sqlCondition+" ";

			}
			else {
				sql = "select a.*, "
						+ "coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1 from ecourts_case_data a "
						+ " inner join dept_new d on (a.dept_code=d.dept_code) left join ecourts_olcms_case_details ecod on(a.cino=ecod.cino ) where (d.dept_code='"+userId+"' OR reporting_dept_code='"+userId+"') and  (dist_id='"+distCode+"') "+sqlCondition+" ";

			}
			System.out.println("InterimOrderFinalOrderService SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}


}
