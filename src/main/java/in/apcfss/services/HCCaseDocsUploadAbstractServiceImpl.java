package in.apcfss.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;

@Service
public class HCCaseDocsUploadAbstractServiceImpl implements HCCaseDocsUploadAbstractService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getSecdeptwiseData(UserDetailsImpl userPrincipal) {
		String ConditionService = "", condition = "", sqlCondition = "",sql="",  heading="",deptId="",deptName="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";

		try {
			if (userId.equals("public-prosecutor@ap.gov.in")) {

				sqlCondition += " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=dn.dept_code) "
						+ " where dn.display = true    and egm.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(a.ecourts_case_status,'')!='Closed' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				sqlCondition += " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=dn.dept_code) "
						+ " where dn.display = true    and egm.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(a.ecourts_case_status,'')!='Closed' ";
			}

			sql="select a1.reporting_dept_code as deptcode,dn1.description,sum(total_cases) as  total_cases,sum(olcms_uploads) as olcms_uploads, "
					+ " sum(petition_uploaded) as petition_uploaded,sum(closed_cases) as closed_cases, "
					+ " sum(counter_uploaded) as counter_uploaded, sum(pwrcounter_uploaded) as pwrcounter_uploaded,"
					+ "sum(counter_approved_gp) as counter_approved_gp "
					+ " from ( "
					+ " select case when reporting_dept_code='CAB01' then a.dept_code else reporting_dept_code end as reporting_dept_code,a.dept_code,count(*) as total_cases"
					+ ",sum(case when scanned_document_path is not null and length(scanned_document_path)>10 then 1 else 0 end) as olcms_uploads, "
					+ " sum(case when petition_document is not null and length(petition_document)>10 then 1 else 0 end) as petition_uploaded "
					+ ", sum(case when a.ecourts_case_status='Closed' then 1 else 0 end) as closed_cases "
					+ ", sum(case when a.ecourts_case_status='Pending' and counter_filed_document is not null and length(counter_filed_document)>10  then 1 else 0 end) as counter_uploaded"
					+ ", sum(case when a.ecourts_case_status='Pending' and pwr_uploaded_copy is not null and length(pwr_uploaded_copy)>10  then 1 else 0 end) as pwrcounter_uploaded "
					+ ", sum(case when counter_approved_gp='Yes' then 1 else 0 end) as counter_approved_gp from ecourts_case_data a "
					+ " left join apolcms.ecourts_olcms_case_details b using (cino)inner join dept_new dn on (a.dept_code=dn.dept_code) "+sqlCondition+" ";


			if(roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9"))
				sql+=" and (dn.reporting_dept_code='"+userPrincipal.getDeptCode()+"' or dn.dept_code='"+userPrincipal.getDeptCode()+"')";
			else if(roleId.equals("2")){
				sql+=" and a.dist_id='"+userPrincipal.getDistId()+"'";
			}

			sql+= " group by reporting_dept_code,a.dept_code) a1"

				+ " inner join dept_new dn1 on (a1.reporting_dept_code=dn1.dept_code) "
				+ " group by a1.reporting_dept_code,dn1.description"
				+ " order by 1";



			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getDeptWiseData(UserDetailsImpl userPrincipal,String deptCode,  String deptName) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "",deptId="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		 String roleId =userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {

			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptId = userPrincipal.getDeptCode();
				deptName = jdbcTemplate.queryForObject(
						"select upper(description) as description from dept_new where dept_code='" + deptId + "'", String.class);
				// CommonModels.checkStringObject(session.getAttribute("dept_code"));
			} else {
				deptId = deptCode;
				deptName = repo.getDeptName(deptId);
			}

			String sqlConditionnew="",sqlConditionnew1="";

			if (userId.equals("public-prosecutor@ap.gov.in")) {

				sqlConditionnew += " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=dn.dept_code) ";
				sqlConditionnew1 += "  and egm.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(a.ecourts_case_status,'')!='Closed' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				sqlConditionnew += " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=dn.dept_code) ";
				sqlConditionnew1 += "  and egm.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(a.ecourts_case_status,'')!='Closed'";
			}

			sql = "select a.dept_code as deptcode,dn.description,count(*) as total_cases,"
					+ " sum(case when scanned_document_path is not null and length(scanned_document_path)>10 then 1 else 0 end) as olcms_uploads, "
					+ "sum(case when petition_document is not null and length(petition_document)>10  then 1 else 0 end) as petition_uploaded ,  "
					+ " sum(case when judgement_order is not null and length(judgement_order)>10  then 1 else 0 end) as judgement_order_uploaded ,"
					+ " sum(case when action_taken_order is not null and length(action_taken_order)>10  then 1 else 0 end) as action_taken_order_uploaded  ,"
					+ " sum(case when a.ecourts_case_status='Closed' then 1 else 0 end) as closed_cases ,"
					+ " sum(case when a.ecourts_case_status='Pending' and counter_filed_document is not null  and length(counter_filed_document)>10 then 1 else 0 end) as counter_uploaded ,"
					+ " sum(case when a.ecourts_case_status='Pending' and pwr_uploaded_copy is not null  and length(pwr_uploaded_copy)>10 then 1 else 0 end) as pwrcounter_uploaded  ,"
					+ " sum(case when pwr_approved_gp='Yes' and pwr_uploaded_copy is not null  and length(pwr_uploaded_copy)>10 then 1 else 0 end) as pwr_approved_gp,"
					+ " sum(case when pwr_approved_gp='No' and pwr_uploaded_copy is not null  and length(pwr_uploaded_copy)>10 then 1 else 0 end) as pwr_not_approved_gp,"
					+ " sum(case when counter_approved_gp='T' and counter_filed_document is not null  and length(counter_filed_document)>10  then 1 else 0 end) as counter_approved_gp ,"
					+ " sum(case when counter_approved_gp='No' and counter_filed_document is not null  and length(counter_filed_document)>10 then 1 else 0 end) as counter_not_approved_gp "
					+ " from ecourts_case_data a "
					+ " left join apolcms.ecourts_olcms_case_details b using (cino) "
					+ " inner join dept_new dn on (a.dept_code=dn.dept_code) "+sqlConditionnew+" "
					+ " where dn.display = true "+sqlConditionnew1+" and (dn.reporting_dept_code='" + deptId + "' or a.dept_code='" + deptId
					+ "') ";

			if(roleId.equals("2") || roleId.equals("10")){
				sql+=" and a.dist_id='"+userPrincipal.getDeptId()+"'";
			}

			// + "where dn.reporting_dept_code='AGC01' or a.dept_code='AGC01' "
			sql+= "group by a.dept_code,dn.description order by 1";

			System.out.println("SQL:" + sql);

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING","HOD Wise Case processing Abstract for " + deptName);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getHCCaseDocsUploadCasesList(UserDetailsImpl userPrincipal,String deptCode,String deptName,String caseStatus ) {
		String sql = null, sqlCondition = "", roleId="",  deptId = "", heading = "",
				userId="",condition="",sqlCondition1="";
		List<Map<String, Object>> dataa = new ArrayList<>();
		try {
			//roleId = CommonModels.checkStringObject(session.getAttribute("role_id"));
			//deptCode = CommonModels.checkStringObject(formBean.getDyna("deptId"));
			//caseStatus = CommonModels.checkStringObject(formBean.getDyna("caseStatus"));
			//actionType = CommonModels.checkStringObject(formBean.getDyna("actionType"));
			// = CommonModels.checkStringObject(formBean.getDyna("deptName"));

			heading="Cases List for Dept. :"+deptName;


			userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
			String sqlConditionnew="",sqlConditionnew1="";
			if (userId.equals("public-prosecutor@ap.gov.in")) {

				sqlConditionnew += " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlConditionnew1 += "  and egm.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(a.ecourts_case_status,'')!='Closed' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				sqlConditionnew += " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlConditionnew1 += "  and egm.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(a.ecourts_case_status,'')!='Closed'";
			}


			if(!caseStatus.equals("")) {
				if(caseStatus.equals("CLOSED")){
					sqlCondition= " and coalesce(a.ecourts_case_status,'')='Closed' ";
					heading+=", Closed Cases List";
				}
				if(caseStatus.equals("PET")) {
					sqlCondition=" and petition_document is not null and length(petition_document)>10 ";
					heading+=" (Petition Documents Uploaded)";
				}
				if(caseStatus.equals("JUD")) {
					sqlCondition=" and judgement_order is not null and length(judgement_order)>10 ";
					heading+=" (Judgement Order Documents Uploaded)";
				}
				if(caseStatus.equals("ACT")) {
					sqlCondition=" and action_taken_order is not null and length(action_taken_order)>10 ";
					heading+=" (Action Taken Order Documents Uploaded)";
				}
				if(caseStatus.equals("COUNTERUPLOADED")) {
					sqlCondition=" and counter_filed_document is not null  and length(counter_filed_document)>10  ";
					heading+=" (Counter Uploaded)";
				}
				if(caseStatus.equals("PWRUPLOADED")) {
					sqlCondition= " and pwr_uploaded_copy is not null  and length(pwr_uploaded_copy)>10 ";
					heading+=" (Parawise Remarks Uploaded)";
				}
				if(caseStatus.equals("GPAPPROVEPWR")) {
					sqlCondition=" and pwr_approved_gp='Yes' ";
					heading+=" (Parawise Remarks Approved By GP)";
				}
				if(caseStatus.equals("GPAPPROVENOTPWR")) {
					sqlCondition=" and pwr_approved_gp='No' ";
					heading+=" (Parawise Remarks Not Approved By GP)";
				}
				if(caseStatus.equals("GPAPPROVECOUNTER")) {
					sqlCondition=" and counter_approved_gp='T' ";
					heading+=" (Counters Approved By GP)";
				}
				if(caseStatus.equals("GPNOTAPPROVECOUNTER")) {
					sqlCondition=" and counter_approved_gp='No' ";
					heading+=" (Counters Not Approved By GP)";
				}
				if(caseStatus.equals("SCANNEDDOC")) {
					sqlCondition=" and scanned_document_path is not null and length(scanned_document_path)>10 ";
					heading+=" (Documents Scanned at APOLCMS Cell, High Court)";
				}
			}


			sql = "select a.*, b.orderpaths from ecourts_case_data a left join"
					+ " ("
					+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
					+ " from "
					+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1"
					+ " union"
					+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
					+ " on (a.cino=b.cino) "
					+ " left join apolcms.ecourts_olcms_case_details cd on (a.cino=cd.cino) "
					+ "	inner join dept_new d on (a.dept_code=d.dept_code) where d.display = true "
					+ "";




			sql = "select a.*, "
					+ " coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths, prayer, ra.address from ecourts_case_data a "
					+ " left join  ecourts_olcms_case_details a1 on (a.cino=a1.cino)"
					+ " left join nic_prayer_data np on (a.cino=np.cino)"
					+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and ra.party_no=1) "
					+ " left join"
					+ " ("
					+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
					+ " from "
					+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1" + " union"
					+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
					+ " on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code) "+sqlConditionnew+" where d.display = true "+sqlConditionnew1+" ";


			if(roleId.equals("2") || roleId.equals("10")){
				sql+=" and a.dist_id='"+userPrincipal.getDistId()+"'";
			}

			sql += " and (reporting_dept_code='" + deptCode + "' or a.dept_code='" + deptCode + "') " + sqlCondition;

			System.out.println("ecourts SQL:" + sql);
			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);
			return jdbcTemplate.queryForList(sql);

		}catch (Exception e) {

			e.printStackTrace();
		}
		return dataa;
	}

}
