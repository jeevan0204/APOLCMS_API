package in.apcfss.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class HCCaseDistWiseAbstractReportServiceImpl implements HCCaseDistWiseAbstractReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getHCCaseDistWiseAbstract(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody) {
		String   condition = "",  sql = "",heading="",deptIdUser="",deptName="",deptCode="",districtId="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";

		deptIdUser = (String) userPrincipal.getDeptCode();
		deptName = repo.getDeptName(deptIdUser);
		try {
			//deptCode = abstractReqBody.getDeptId();
			//districtId = abstractReqBody.getDistrictId();

			System.out.println("dist"+districtId);
		//	String subcon = "";

			if (abstractReqBody.getDistrictId() != null
					&& !abstractReqBody.getDistrictId().toString().contentEquals("")
					&& !abstractReqBody.getDistrictId().toString().contentEquals("0")) {
				condition += " and a.dist_id='" +abstractReqBody.getDistrictId().toString().trim() + "' ";
			}

			if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
					&& !abstractReqBody.getDeptId().toString().contentEquals("0")) {
				condition += " and a.dept_code='" +abstractReqBody.getDeptId().toString().trim() + "' ";

			}

			if (abstractReqBody.getCategoryServiceId() != null
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("")
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("0")) {


				if (abstractReqBody.getCategoryServiceId().equals("NON-SERVICE")) {
					condition += "and (a.category_service='" + abstractReqBody.getCategoryServiceId().toString().trim()+ "'  or a.category_service is null or a.category_service=' ')";
				}

				else {
					condition += " and a.category_service='" + abstractReqBody.getCategoryServiceId().toString().trim()+ "'  ";
				}
			}

			if(roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9")) {
				condition+=" and ( a.dept_code='"+(String) userPrincipal.getDeptCode()+"')";
				userPrincipal.setDeptCode((String) userPrincipal.getDeptCode());

			}else if(roleId.equals("2")){
				condition+=" and a.dist_id='"+userPrincipal.getDistId()+"'";
				userPrincipal.setDistId( userPrincipal.getDistId());
			}

			else //if(roleId.equals("3") || roleId.equals("4"))
			{
				//condition="";

				if (abstractReqBody.getDistrictId() != null
						&& !abstractReqBody.getDistrictId().toString().contentEquals("")
						&& !abstractReqBody.getDistrictId().toString().contentEquals("0")) {
					condition += " and a.dist_id='" + abstractReqBody.getDistrictId().toString().trim() + "' ";
				}

				if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
						&& !abstractReqBody.getDeptId().toString().contentEquals("0")) {
					condition += " and a.dept_code='" + abstractReqBody.getDeptId().toString().trim() + "' ";
				}

			}

			sql="select dm.district_id,dm.district_name,count(*) as total_cases,"

						+ "sum(case when case_status=7 then 1 else 0 end) as pending_dc, "
						+ "sum(case when case_status=8 then 1 else 0 end) as pending_dno, "
						+ "sum(case when case_status=10 then 1 else 0 end) as pending_dsec, "

						+ "sum(case when scanned_document_path is not null and length(scanned_document_path)>10 then 1 else 0 end) as olcms_uploads, "
						+ "sum(case when petition_document is not null and length(petition_document)>10 then 1 else 0 end) as petition_uploaded , "
						+ "sum(case when a.ecourts_case_status='Closed' then 1 else 0 end) as closed_cases , "
						+ "sum(case when  case_status='98' then 1 else 0 end) as private_cases,"
						+ "sum(case when a.ecourts_case_status='Pending' and counter_filed_document is not null and length(counter_filed_document)>10  then 1 else 0 end) as counter_uploaded,"
						+ " sum(case when a.ecourts_case_status='Pending' and pwr_uploaded_copy is not null and length(pwr_uploaded_copy)>10  then 1 else 0 end) as pwrcounter_uploaded ,"
						+ " sum(case when counter_approved_gp='Yes' then 1 else 0 end) as counter_approved_gp,"
						+ " count(distinct case when length(action_taken_order)> 10   and final_order_status='final' then action_taken_order else null end) as final_order ,"
						+ " count(distinct case when length(appeal_filed_copy)> 10 and final_order_status='appeal' then appeal_filed_copy else null end) as appeal_order ,"
						+ " count(distinct case when length(dismissed_copy)> 10 and final_order_status='dismissed'  and b.ecourts_case_status='dismissed' then dismissed_copy else null end) as dismissed_order  "
						+ "from ecourts_case_data a "
						+ "left join apolcms.ecourts_olcms_case_details b on (b.cino=a.cino)"
						+ "inner join ecourts_case_finalorder ecf on (a.cino=ecf.cino)"
						+ "inner join district_mst dm on (dm.district_id=a.dist_id)"
						+ "inner join dept_new dn on (dn.dept_code=a.dept_code) where 1=1 "+condition+" "
						+ "group by dm.district_id,dm.district_name ";

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING", "Dist. Wise Final Order Case processing Abstract Report");

			//	map.put("HEADING",heading);
			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getCasesListHCCaseStatus(UserDetailsImpl userPrincipal,String distid, String distName, String caseStatus,HCCaseStatusAbstractReqBody abstractReqBody) {
		String sql = null, sqlCondition = "",  heading = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		String deptCode=userPrincipal.getDeptCode()!=null ? userPrincipal.getDeptCode() :"0";
		Map<String, Object> map = new HashMap<>();
		try {

			System.out.println("caseStatus--"+caseStatus);
			
			if(!caseStatus.equals("") && caseStatus!=null) {
				if(caseStatus.equals("CLOSED")){
					sqlCondition+= " and coalesce(a.ecourts_case_status,'')='Closed' ";
					heading+=" Closed Cases List";
				}
				if(caseStatus.equals("PET")) {
					sqlCondition+=" and eocd.petition_document is not null and length(eocd.petition_document)>10 ";
					heading+=" Petition Documets Uploaded";
				}
				if(caseStatus.equals("COUNTERUPLOADED")) {
					sqlCondition+=" and eocd.counter_filed_document is not null  and length(eocd.counter_filed_document)>10  ";
					heading+=" Counter Uploaded Cases";
				}
				if(caseStatus.equals("PWRUPLOADED")) {
					sqlCondition+= " and eocd.pwr_uploaded_copy is not null  and length(eocd.pwr_uploaded_copy)>10 ";
					heading+=" Parawise Remarks Uploaded Cases List";
				}
				if(caseStatus.equals("GPCOUNTER")) {
					sqlCondition+=" and eocd.counter_approved_gp='Yes' ";
					heading+=" and Counters Filed";
				}
				if(caseStatus.equals("SCANNEDDOC")) {
					sqlCondition+=" and scanned_document_path is not null and length(scanned_document_path)>10 ";
					heading+=" and Documents Scanned at APOLCMS Cell, High Court";
				}
				if(caseStatus.equals("FINALORDER")) {
					sqlCondition+=" and final_order_status='final' ";
					heading+="  Final orders implemented";
				}
				if(caseStatus.equals("APPEALORDER")) {
					sqlCondition+=" and final_order_status='appeal' ";
					heading+="  Appeal Final orders";
				}
				if(caseStatus.equals("DISMISSEDORDER")) {
					sqlCondition+=" and final_order_status='dismissed' ";
					heading+="  Dismissed Final Orders";
				}
				if(caseStatus.equals("DC")) {
					sqlCondition+=" and  case_status=7 ";
					heading+="  District Collector Final Orders";
				}
				if(caseStatus.equals("DNO")) {
					sqlCondition+=" and case_status=8 ";
					heading+=" District Nodal officer Final Orders";
				}
				if(caseStatus.equals("DSEC")) {
					sqlCondition+=" and case_status=10 ";
					heading+="   secretariat Final Orders";
				}
				if(caseStatus.equals("PRIVATE")) {
					sqlCondition+=" and case_status=98 ";
					heading+="   Private Cases";
				}

			}

			if(roleId.equals("2")){
				sqlCondition+=" and a.dist_id='"+userPrincipal.getDistId()+"'";
			}else if(roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9"))
			{
				sqlCondition += " and (a.dept_code='" + deptCode + "') " ;
			}

			if(distid != null
					&& !distid.contentEquals("")) {
				sqlCondition+=" and a.dist_id='"+distid+"'";
			}

			if (abstractReqBody.getDistrictId() != null && !abstractReqBody.getDistrictId().toString().contentEquals("")
					&& !abstractReqBody.getDistrictId().toString().contentEquals("0")) {
				sqlCondition += " and a.dist_id='" +abstractReqBody.getDistrictId().toString().trim() + "' ";
			}

			if (deptCode != null && !deptCode.toString().contentEquals("")
					&& !deptCode.toString().contentEquals("0")) {
				sqlCondition += " and a.dept_code='" + deptCode + "' ";
			}
			if (abstractReqBody.getCategoryServiceId() != null
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("")
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("0")) {


				if (abstractReqBody.getCategoryServiceId().equals("NON-SERVICE")) {
					sqlCondition += "and (a.category_service='" + abstractReqBody.getCategoryServiceId().toString().trim()+ "'  or a.category_service is null or a.category_service=' ')";
				}

				else {
					sqlCondition += " and a.category_service='" +abstractReqBody.getCategoryServiceId().toString().trim()+ "'  ";
				}
			}

			System.out.println("---"+abstractReqBody.getDistId());

			sql = "select a.*, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths, prayer, ra.address from ecourts_case_data a  "
					+ " left join nic_prayer_data np on (a.cino=np.cino)"
					+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1)"
					+ "  left join ecourts_olcms_case_details eocd on (a.cino=eocd.cino)  "
					+ " inner join ( select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths "
					+ " from  (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null "
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0 and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) c group by cino ) b on (a.cino=b.cino)"
					+ " inner join dept_new d on (a.dept_code=d.dept_code) where d.display = true   "+sqlCondition+"    ";

			System.out.println("ecourts SQL:" + sql);
			map.put("HEADING", heading);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getCasesReportList(UserDetailsImpl userPrincipal ,HCCaseStatusAbstractReqBody abstractReqBody) {
		String sql = null, sqlCondition = "",  heading = "",condition="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		//String deptCode=userPrincipal.getDeptCode()!=null ? userPrincipal.getDeptCode() :"0";
		Map<String, Object> map = new HashMap<>();
		try {

			
			if (abstractReqBody.getDistId() != null
					&& !abstractReqBody.getDistId().toString().contentEquals("")
					&& !abstractReqBody.getDistId().toString().contentEquals("0") && !abstractReqBody.getDistId().equals("ALL")) {
				condition += " and a.dist_id='" +abstractReqBody.getDistId().toString().trim() + "' ";
			}

			if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
					&& !abstractReqBody.getDeptId().toString().contentEquals("0") && !abstractReqBody.getDeptId().equals("ALL")) {
				condition += " and a.dept_code='" +abstractReqBody.getDeptId().toString().trim() + "' ";

			}

			if (abstractReqBody.getCategoryServiceId() != null
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("")
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("0") && !abstractReqBody.getCategoryServiceId().equals("ALL")) {


				if (abstractReqBody.getCategoryServiceId().equals("NON-SERVICE")) {
					condition += "and (a.category_service='" + abstractReqBody.getCategoryServiceId().toString().trim()+ "'  or a.category_service is null or a.category_service=' ')";
				}

				else {
					condition += " and a.category_service='" + abstractReqBody.getCategoryServiceId().toString().trim()+ "'  ";
				}
			}

			if(roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9")) {
				condition+=" and ( a.dept_code='"+(String) userPrincipal.getDeptCode()+"')";
				userPrincipal.setDeptCode((String) userPrincipal.getDeptCode());

			}else if(roleId.equals("2")){
				condition+=" and a.dist_id='"+userPrincipal.getDistId()+"'";
				userPrincipal.setDistId( userPrincipal.getDistId());
			}

			else //if(roleId.equals("3") || roleId.equals("4"))
			{

				if (abstractReqBody.getDistId() != null
						&& !abstractReqBody.getDistId().toString().contentEquals("")
						&& !abstractReqBody.getDistId().toString().contentEquals("0") && !abstractReqBody.getDistId().equals("ALL")) {
					condition += " and a.dist_id='" + abstractReqBody.getDistId().toString().trim() + "' ";
				}

				if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
						&& !abstractReqBody.getDeptId().toString().contentEquals("0") && !abstractReqBody.getDeptId().equals("ALL")) {
					condition += " and a.dept_code='" + abstractReqBody.getDeptId().toString().trim() + "' ";
				}

			}

			sql="select dm.district_id,dm.district_name,count(*) as total_cases,"

						+ "sum(case when case_status=7 then 1 else 0 end) as pending_dc, "
						+ "sum(case when case_status=8 then 1 else 0 end) as pending_dno, "
						+ "sum(case when case_status=10 then 1 else 0 end) as pending_dsec, "

						+ "sum(case when scanned_document_path is not null and length(scanned_document_path)>10 then 1 else 0 end) as olcms_uploads, "
						+ "sum(case when petition_document is not null and length(petition_document)>10 then 1 else 0 end) as petition_uploaded , "
						+ "sum(case when a.ecourts_case_status='Closed' then 1 else 0 end) as closed_cases , "
						+ "sum(case when  case_status='98' then 1 else 0 end) as private_cases,"
						+ "sum(case when a.ecourts_case_status='Pending' and counter_filed_document is not null and length(counter_filed_document)>10  then 1 else 0 end) as counter_uploaded,"
						+ " sum(case when a.ecourts_case_status='Pending' and pwr_uploaded_copy is not null and length(pwr_uploaded_copy)>10  then 1 else 0 end) as pwrcounter_uploaded ,"
						+ " sum(case when counter_approved_gp='Yes' then 1 else 0 end) as counter_approved_gp,"
						+ " count(distinct case when length(action_taken_order)> 10   and final_order_status='final' then action_taken_order else null end) as final_order ,"
						+ " count(distinct case when length(appeal_filed_copy)> 10 and final_order_status='appeal' then appeal_filed_copy else null end) as appeal_order ,"
						+ " count(distinct case when length(dismissed_copy)> 10 and final_order_status='dismissed'  and b.ecourts_case_status='dismissed' then dismissed_copy else null end) as dismissed_order  "
						+ "from ecourts_case_data a "
						+ "left join apolcms.ecourts_olcms_case_details b on (b.cino=a.cino)"
						+ "inner join ecourts_case_finalorder ecf on (a.cino=ecf.cino)"
						+ "inner join district_mst dm on (dm.district_id=a.dist_id)"
						+ "inner join dept_new dn on (dn.dept_code=a.dept_code) where 1=1 "+condition+" "
						+ "group by dm.district_id,dm.district_name ";

			map.put("HEADING", "Dist. Wise Final Order Case processing Abstract Report");

			//	map.put("HEADING",heading);
			System.out.println("SQL:" + sql);

		}  catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

}
