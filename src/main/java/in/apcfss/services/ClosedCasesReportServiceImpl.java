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
public class ClosedCasesReportServiceImpl implements ClosedCasesReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getClosedCasesReport(UserDetailsImpl userPrincipal ) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "",heading="",deptCode="",deptName="";
		Integer distId=0;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";

		try {
			deptCode = (String) userPrincipal.getDeptCode();
			deptName = repo.getDeptName(deptCode);
			distId =  userPrincipal.getDistId();

			if (roleId != null && roleId.equals("4")) { // MLO / NO
				condition = " and dept_code='" + deptCode + "'";
			} else if (roleId != null && roleId.equals("5")) { // NO
				condition = " and dept_code='" + deptCode + "'";
			} else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER
				condition = " and assigned_to='" + userId + "'";
			} else if (roleId != null && roleId.equals("3")) { // SECT DEPT
				condition = " and dept_code='" + deptCode + "'";
			} else if (roleId != null && roleId.equals("9")) { // HOD
				condition = " and dept_code='" + deptCode + "'";
			} else if (roleId != null && roleId.equals("6")) { // GPO
				// condition = " and a.case_status=4";
			} else if (roleId != null && roleId.equals("10")) { // DNO
				condition = " and dept_code='" + deptCode + "' and dist_id='" + distId + "'";
			}

			sql = "select a.*, b.orderpaths from ecourts_case_data a left join" + " ("
					+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
					+ " from "
					+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1" + " union"
					+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
					+ " on (a.cino=b.cino) where coalesce(ecourts_case_status,'')='Closed' " + condition + " ";
			System.out.println("ecourts closed SQL:" + sql);


			heading="Closed Cases Report " + deptName;

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);

			System.out.println("unspecified SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	 
}
