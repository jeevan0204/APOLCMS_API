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
public class HCOrdersIssuedReportServiceImpl implements HCOrdersIssuedReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getSecDeptWiseDataforHCOIssued(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody) {
		String ConditionService = "", Condition = "", sqlCondition = "", sql = "",heading="",deptId="",deptName="",Condition1="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		
		try {

			if (abstractReqBody.getDofFromDate() != null && !abstractReqBody.getDofFromDate().toString().contentEquals("")) {
				sqlCondition += " and order_date >= to_date('" + abstractReqBody.getDofFromDate() + "','dd-mm-yyyy') ";
			}
			if (abstractReqBody.getDofToDate() != null && !abstractReqBody.getDofToDate().toString().contentEquals("")) {
				sqlCondition += " and order_date <= to_date('" + abstractReqBody.getDofToDate()+ "','dd-mm-yyyy') ";
			}

			if (userId.equals("public-prosecutor@ap.gov.in")) {
				Condition1 = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				Condition = " and egm.gp_id='" + userId
						+ "' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(ecourts_case_status,'')!='Closed' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				Condition1 = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				Condition = " and egm.gp_id='" + userId
						+ "' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(ecourts_case_status,'')!='Closed' ";
			}

			sql = "select x.reporting_dept_code as deptcode, upper(d1.description) as description,sum(total_cases) as total_cases, sum(interim_order_cases) as interim_order_cases, "
					+ "sum(final_order_cases) as final_order_cases,   sum(interim_orders)  as interim_orders, sum(final_orders) as final_orders from (select a.dept_code , "
					+ "case when reporting_dept_code='CAB01' then d.dept_code else reporting_dept_code end as reporting_dept_code, count(*) as total_cases,  "
					+ "count( io.cino) as interim_order_cases, count( fo.cino) as final_order_cases,sum(coalesce(interim_orders,'0')::int4)  as interim_orders, "
					+ "sum(coalesce(final_orders,'0')::int4) as final_orders  from ecourts_case_data a inner join dept_new d on (a.dept_code=d.dept_code)   "
					+ "left join (select cino, count(*) as interim_orders from ecourts_case_interimorder where order_document_path is not null and  "
					+ "POSITION('RECORD_NOT_FOUND' in order_document_path)= 0 and POSITION('INVALID_TOKEN' in order_document_path)= 0 "
					+ sqlCondition + " group by cino) io on (a.cino=io.cino) "
					+ " left join (select cino, count(*) as final_orders from ecourts_case_finalorder  where 1=1 and order_document_path is not null "
					+ sqlCondition + " group by cino) fo on (a.cino=fo.cino) " + Condition1 + " where d.display = true "
					+ Condition + "";

			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9") || roleId.equals("10"))
				sql += " and (reporting_dept_code='" +userPrincipal.getDeptCode()+ "' or a.dept_code='"
						+ userPrincipal.getDeptCode() + "')";
			else if (roleId.equals("2") || roleId.equals("10")) {
				sql += " and a.dist_id='" + userPrincipal.getDistId()+ "'";
			}

			sql += " group by a.dept_code,d.dept_code ,reporting_dept_code ) x inner join dept_new d1 on (x.reporting_dept_code=d1.dept_code)"
					+ " group by x.reporting_dept_code, d1.description order by 1";

			System.out.println("------------SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getDeptWiseDataHCOIssued(UserDetailsImpl userPrincipal,String deptCode,String deptName,HCCaseStatusAbstractReqBody abstractReqBody) {
		String ConditionService = "", Condition = "", sqlCondition = "", sql = "",deptId="" ,Condition1="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		 String roleId =userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {
			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptId = userPrincipal.getDeptCode();
				deptName = jdbcTemplate.queryForObject(
						"select upper(description) as description from dept_new where dept_code='" + deptId + "'",
						String.class);
			} else {
				deptId =deptCode;
				deptName = deptName;
			}

			if (abstractReqBody.getDofFromDate() != null && !abstractReqBody.getDofFromDate().toString().contentEquals("")) {
				sqlCondition += " and order_date >= to_date('" + abstractReqBody.getDofFromDate() + "','dd-mm-yyyy') ";
			}
			if (abstractReqBody.getDofToDate() != null && !abstractReqBody.getDofToDate().toString().contentEquals("")) {
				sqlCondition += " and order_date <= to_date('" + abstractReqBody.getDofToDate() + "','dd-mm-yyyy') ";
			}

			/*
			 * if (request.getParameter("fromDate") != null &&
			 * !request.getParameter("fromDate").toString().contentEquals("")) {
			 * sqlCondition += " and order_date >= to_date('" +
			 * request.getParameter("fromDate") + "','dd-mm-yyyy') "; } if
			 * (request.getParameter("toDate") != null &&
			 * !request.getParameter("toDate").toString().contentEquals("")) { sqlCondition
			 * += " and order_date <= to_date('" + request.getParameter("toDate") +
			 * "','dd-mm-yyyy') "; }
			 */

			if (userId.equals("public-prosecutor@ap.gov.in")) {
				Condition1 = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				Condition = " and egm.gp_id='" + userId
						+ "' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(ecourts_case_status,'')!='Closed' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				Condition1 = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				Condition = " and egm.gp_id='" + userId
						+ "' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(ecourts_case_status,'')!='Closed' ";
			}

			sql = "select a.dept_code as deptcode , d.description,count(*) as total_cases, "
					+ "count(distinct io.cino) as interim_orders, count(distinct fo.cino) as final_orders from ecourts_case_data a inner join dept_new d on (a.dept_code=d.dept_code) "
					+ "left join (select distinct cino from ecourts_case_interimorder where 1=1 " + sqlCondition
					+ ") io on (a.cino=io.cino) "
					+ "left join (select distinct cino from ecourts_case_finalorder  where 1=1 " + sqlCondition
					+ ") fo on (a.cino=fo.cino) " + "where d.display = true ";

			sql = "select a.dept_code as deptcode , d.description,count(*) as total_cases, "
					+ " count(distinct io.cino) as interim_order_cases, count(distinct fo.cino) as final_order_cases,sum(coalesce(interim_orders,'0')::int4)  as interim_orders, sum(coalesce(final_orders,'0')::int4) as final_orders "
					+ " from ecourts_case_data a inner join dept_new d on (a.dept_code=d.dept_code) "
					+ " left join (select cino,count(*) as interim_orders from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0 "
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0    " + sqlCondition
					+ " group by cino) io on (a.cino=io.cino) "
					+ " left join (select cino,count(*) as final_orders from ecourts_case_finalorder  where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0 "
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0  " + sqlCondition
					+ " group by cino) fo on (a.cino=fo.cino) " + Condition1 + " where d.display = true " + Condition + " ";

			sql += " and (reporting_dept_code='" + deptId + "' or a.dept_code='" + deptId + "') ";
			if (roleId.equals("2")) {
				sql += " and a.dist_id='" + userPrincipal.getDistId() + "' ";
			}

			sql += " group by a.dept_code,d.description order by 1";

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING","HOD Wise High Court Orders Issued Report for " + deptName);
			System.out.println("SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	


	@Override
	public List<Map<String, Object>> getCaseslistDataHCOIssued(UserDetailsImpl userPrincipal,String deptCode, String deptName,String caseStatus  ,
			String reportLevel,HCCaseStatusAbstractReqBody abstractReqBody) {
		String sql = null, sqlCondition = "", roleId="",  deptId = "", heading = "",Condition1="",Condition2="",
				userId="",condition="",sqlCondition1="";
		List<Map<String, Object>> dataa = new ArrayList<>();
		  userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		deptId = userPrincipal.getDeptCode();

		//caseStatus = !caseStatus.equals("") ? caseStatus : CommonModels.checkStringObject(request.getParameter("caseStatus"));
		

		//actionType = CommonModels.checkStringObject(formBean.getDyna("actionType"));
		//actionType = !actionType.equals("") ? actionType : CommonModels.checkStringObject(request.getParameter("actionType"));

		//deptName = CommonModels.checkStringObject(formBean.getDyna("deptName"));
		//deptName = !deptName.equals("") ? deptName : CommonModels.checkStringObject(request.getParameter("deptName"));
	try {
		

		heading = "Cases List for " + deptName;
		String value = " ";
		String value1 = " ";

		// Interim Orders Issued
		// Final Orders Issued
		// Date Filters
		System.out.println("caseStatus-------------" + caseStatus);
		if (!caseStatus.equals("")) {
			if (caseStatus.equals("IO")) {
				heading += " Interim Orders Issued ";

				value = "b.is_interim_exists";
				value1 = "is_interim_exists";
			}
			if (caseStatus.equals("FO")) {
				heading += " Final Orders Issued ";

				value = "b.is_final_exists";
				value1 = "is_final_exists";
			}
		}

		if (abstractReqBody.getDofFromDate() != null && !abstractReqBody.getDofFromDate().toString().contentEquals("")) {
			sqlCondition += " and order_date >= to_date('" + abstractReqBody.getDofFromDate() + "','dd-mm-yyyy') ";
		}
		if (abstractReqBody.getDofToDate()!= null && !abstractReqBody.getDofToDate().toString().contentEquals("")) {
			sqlCondition += " and order_date <= to_date('" +abstractReqBody.getDofToDate()+ "','dd-mm-yyyy') ";
		}

		/*
		 * if (abstractReqBody.getDofToDate() != null &&
		 * !request.getParameter("fromDate").toString().contentEquals("")) {
		 * sqlCondition += " and order_date >= to_date('" +
		 * request.getParameter("fromDate") + "','dd-mm-yyyy') "; } if
		 * (request.getParameter("toDate") != null &&
		 * !request.getParameter("toDate").toString().contentEquals("")) { sqlCondition
		 * += " and order_date <= to_date('" + request.getParameter("toDate") +
		 * "','dd-mm-yyyy') "; }
		 */

		if (userId.equals("public-prosecutor@ap.gov.in")) {
			Condition1 = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
			Condition2 = " and egm.gp_id='" + userId+ "' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(ecourts_case_status,'')!='Closed' ";
		}

		if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

			Condition1 = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
			Condition2 = " and egm.gp_id='" + userId
					+ "' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' and coalesce(ecourts_case_status,'')!='Closed' ";
		}

		sql = "select a.*, b.orderpaths, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1," + value
				+ ", case when (prayer is not null and coalesce(trim(prayer),'')!='' and length(prayer) > 2) then substr(prayer,1,250) else '-' end as prayer, prayer as prayer_full, ra.address"
				+ " from ecourts_case_data a " + " left join nic_prayer_data np on (a.cino=np.cino)"
				+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1) "

				+ "" + condition + " inner join" + " (" + " select cino," + value1
				+ ", string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
				+ " from (select * from";

		if (caseStatus.equals("IO") && roleId.equals("6") && !userId.equals("addl-pubprosecutor@ap.gov.in")
				&& !userId.equals("public-prosecutor@ap.gov.in")) {

			sql += "  (select cino,is_interim_exists, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder "
					+ " where 1=1  " + sqlCondition + ") x1";
		} else if (caseStatus.equals("IO") && roleId.equals("6")) {
			if (userId.equals("addl-pubprosecutor@ap.gov.in") || userId.equals("public-prosecutor@ap.gov.in")) {
				sql += "  (select cino,is_interim_exists, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
						+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 " + sqlCondition + ") x1";
			}
		}

		if (caseStatus.equals("IO") && !roleId.equals("6"))
			sql += "  (select cino,is_interim_exists, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 " + sqlCondition + ") x1";

		if (caseStatus.equals("FO") && !roleId.equals("6"))
			sql += " (select cino,is_final_exists, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 " + sqlCondition + ") x2";

		if (caseStatus.equals("FO") && roleId.equals("6"))
			sql += " (select cino,is_final_exists, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder "
					+ "  where order_document_path is not null and "
					+ "POSITION('RECORD_NOT_FOUND' in order_document_path)= 0 and POSITION('INVALID_TOKEN' in order_document_path)= 0    "
					+ sqlCondition + ") x2";

		sql += " order by cino, order_date desc) c group by cino," + value1 + " ) b"
				+ " on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code) " + Condition1
				+ "  where d.display = true " + Condition2 + " ";

		
		  if (reportLevel.equals("HOD")) 
			  sql += " and (a.dept_code='" + deptCode + "') "; 
		  else if (reportLevel.equals("SD")) 
			  sql += " and (reporting_dept_code='" + deptCode + "' or a.dept_code='" +deptCode + "') ";
		 

		if (roleId.equals("3") || roleId.equals("4")) {
			sql += " and (reporting_dept_code='" +userPrincipal.getDeptCode()+ "' or a.dept_code='"
					+ userPrincipal.getDeptCode() + "') ";
		}
		if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
			sql += " and (a.dept_code='" + userPrincipal.getDeptCode() + "') ";
		}

		if (roleId.equals("2") || roleId.equals("10")) {
			sql += " and a.dist_id='" + userPrincipal.getDistId()+ "'";
		}
		
		System.out.println("sql---"+sql);

		Map<String, Object> map = new HashMap<>();
		map.put("HEADING",heading);

	}catch (Exception e) {

		e.printStackTrace();
	}
	return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getCasesListNew(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "" ,deptId="",heading="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		try {
			
			String deptCode=abstractReqBody.getDeptId();
			String deptName = repo.getDeptName(deptCode);
			

			/*
			 * caseStatus = CommonModels.checkStringObject(formBean.getDyna("caseStatus"));
			 * caseStatus = !caseStatus.equals("") ? caseStatus :
			 * CommonModels.checkStringObject(request.getParameter("caseStatus"));
			 * 
			 * actionType = CommonModels.checkStringObject(formBean.getDyna("actionType"));
			 * actionType = !actionType.equals("") ? actionType :
			 * CommonModels.checkStringObject(request.getParameter("actionType"));
			 * 
			 * deptName = CommonModels.checkStringObject(formBean.getDyna("deptName"));
			 * deptName = !deptName.equals("") ? deptName :
			 * CommonModels.checkStringObject(request.getParameter("deptName"));
			 */

			heading = "Cases List for " + deptName;

			if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
					&& !abstractReqBody.getDeptId().toString().contentEquals("0") && !abstractReqBody.getDeptId().equals("ALL")) {
				sqlCondition += " and  a.dept_code ='"+abstractReqBody.getDeptId()+"'";
			}
			
			if (abstractReqBody.getDofFromDate()!= null
					&& !abstractReqBody.getDofFromDate().toString().contentEquals("")) {
				sqlCondition += " and a.dt_regis >= to_date('" + abstractReqBody.getDofFromDate()
				+ "','yyyy-mm-dd') ";
			}
			if (abstractReqBody.getDofToDate() != null
					&& !abstractReqBody.getDofToDate().toString().contentEquals("")) {
				sqlCondition += " and a.dt_regis <= to_date('" +abstractReqBody.getDofToDate()
				+ "','yyyy-mm-dd') ";
			}
			if (abstractReqBody.getCaseTypeId() != null && !abstractReqBody.getCaseTypeId().toString().contentEquals("")
					&& !abstractReqBody.getCaseTypeId().toString().contentEquals("0") && !abstractReqBody.getCaseTypeId().equals("ALL")) {
				sqlCondition += " and trim(a.type_name_reg)='" +abstractReqBody.getCaseTypeId().toString().trim()
						+ "' ";
			}
			if (abstractReqBody.getDistId()!= null && !abstractReqBody.getDistId().toString().contentEquals("")
					&& !abstractReqBody.getDistId().toString().contentEquals("0")  && !abstractReqBody.getDistId().equals("ALL")) {
				sqlCondition += " and a.dist_id='" + abstractReqBody.getDistId().toString().trim() + "' ";
			}
			
			if (abstractReqBody.getRegYear() != null
					&& !abstractReqBody.getRegYear().equals("ALL") ) { 
				  sqlCondition += " and a.reg_year='" + abstractReqBody.getRegYear() + "' "; 
			  }
			 

			if (abstractReqBody.getPetitionerName() != null && !abstractReqBody.getPetitionerName().toString().contentEquals("")
					&& !abstractReqBody.getPetitionerName().toString().contentEquals("0")) {
				sqlCondition += " and a.pet_name ilike  '%"+abstractReqBody.getPetitionerName()+"%'";
			}

			if (abstractReqBody.getRespodentName() != null && !abstractReqBody.getRespodentName().toString().contentEquals("")
					&& !abstractReqBody.getRespodentName().toString().contentEquals("0")) {
				sqlCondition += " and  a.res_name  ilike  '%"+abstractReqBody.getRespodentName()+"%'";
			}
			
			if (abstractReqBody.getJudgeName()!= null && !abstractReqBody.getJudgeName().toString().contentEquals("")
					&& !abstractReqBody.getJudgeName().toString().contentEquals("0")) {
				sqlCondition += " and coram  ilike  '%" +abstractReqBody.getJudgeName() + "%'";

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

			sql = "select a.*, b.orderpaths, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, "
					+ " case when (prayer is not null and coalesce(trim(prayer),'')!='' and length(prayer) > 2) then substr(prayer,1,250) else '-' end as prayer, "
					+ " prayer as prayer_full, ra.address from ecourts_case_data a  "
					+ " left join nic_prayer_data np on (a.cino=np.cino) left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1)  "
					+ " inner join ( select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths "
					+ " from (select * from  (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder "
					+ " where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0 and POSITION('INVALID_TOKEN' in order_document_path) = 0  ) x1 "
					+ " order by cino, order_date desc) c group by cino ) b on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code) "
					+ " where d.display = true   " + sqlCondition;

			System.out.println("ecourts SQL:" + sql);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
}
