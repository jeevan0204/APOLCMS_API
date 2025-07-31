package in.apcfss.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.PullBackLegacyCasesRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class DistrictNodalOfficerAbstactReportServiceImpl implements DistrictNodalOfficerAbstactReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CommonMethodsController commonMethodsController;

	@Override
	public List<Map<String, Object>> getDistrictNodalOfficerAbstactReport(UserDetailsImpl userPrincipal ) {
		String  sql = "" ;
		Map<String, Object> map = new HashMap<>();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ?  userPrincipal.getDeptCode().toString() : "";
		String distCode = userPrincipal.getDistId() != null ?  userPrincipal.getDistId().toString() : "";

		try {
		
			map.put("HEADING", "District Nodal Officer (Legal) Abstract ");

			sql = "select dist_id as  distid,upper(b.district_name) as district_name,count(*) as acks From nodal_officer_details a "
					+ "inner join district_mst b on (a.dist_id=b.district_id) where 1=1 ";

			if (!deptCode.equals("") && !deptCode.equals("0"))
				sql += " and a.dept_id='" + deptCode + "'";

			if (!distCode.equals("") && !distCode.equals("0"))
				sql += " and a.dist_id='" + distCode + "'";

			sql += " group by a.dist_id,b.district_name order by district_name ";

			System.out.println("SQL:" + sql);

		}  catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getEmpListData(UserDetailsImpl userPrincipal, int districtId, String district_name ) {
		String   sql = ""  ;
		Map<String, Object> map = new HashMap<>();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ?  userPrincipal.getDeptCode().toString() : "";
		int distCode = userPrincipal.getDistId() != null ? Integer.parseInt(userPrincipal.getDistId().toString()) : 0;
		
		System.out.println("distCode--"+distCode+"districtId--"+districtId);

		try {
			int dist = roleId.equals("2") ? distCode : districtId;
			String tableName = "";
			 tableName = commonMethodsController.getTableName(dist+"");
			 System.out.println("dist--" + dist+"tableName---"+tableName);
			map.put("HEADING", "District Nodal Officer (Legal) Details - "+ district_name+ " District ");

			sql = "select m.dept_id,upper(d.description) as description,trim(nd.fullname_en) as fullname_en, trim(nd.designation_name_en) as designation_name_en,m.mobileno,m.emailid from nodal_officer_details m "
					+ "inner join (select distinct employee_id,fullname_en,designation_name_en, designation_id from "
					+ tableName + ") nd on (m.employeeid=nd.employee_id and m.designation=nd.designation_id)"
					+ "inner join users u on (m.emailid=u.userid)"
					+ "inner join dept_new d on (m.dept_id=d.dept_code)" + "where m.dist_id='" + dist + "'";

			if (!deptCode.equals("") && !deptCode.equals("0"))
				sql += " and  m.dept_id='" + deptCode + "'";

			sql += " order by 1";

			System.out.println("SQL:" + sql);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return jdbcTemplate.queryForList(sql);
	}
	 

}
