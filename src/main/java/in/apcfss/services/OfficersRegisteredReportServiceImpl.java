package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;

@Service
public class OfficersRegisteredReportServiceImpl implements OfficersRegisteredReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getOfficersRegistered(Authentication authentication, String districtId, String officerType) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "" ;
		try {
			System.out.println("distdist..."+districtId);
			System.out.println("officerType..."+officerType);
			if ((CommonModels.checkStringObject(officerType).equals("DNO") && districtId.equals("ALL") )) {

				sql = "select m.dept_id,upper(d.description) as description,trim(nd.fullname_en) as fullname_en, trim(nd.designation_name_en) as designation_name_en,m.mobileno,m.emailid from nodal_officer_details m "
						+ "inner join (select distinct employee_id,fullname_en,designation_name_en, designation_id from nic_data) nd on (m.employeeid=nd.employee_id and m.designation=nd.designation_id)"
						+ "inner join users u on (m.emailid=u.userid)"
						+ "inner join dept_new d on (m.dept_id=d.dept_code)";
			} 

			else if (CommonModels.checkStringObject(officerType).equals("DNO") ) {
				String tableName="";
				tableName = getTableName(CommonModels.checkStringObject(districtId));

				sql = "select m.dept_id,upper(d.description) as description,trim(nd.fullname_en) as fullname_en, trim(nd.designation_name_en) as designation_name_en,m.mobileno,m.emailid from nodal_officer_details m "
						+ "inner join (select distinct employee_id,fullname_en,designation_name_en, designation_id from "+tableName+") nd on (m.employeeid=nd.employee_id and m.designation=nd.designation_id)"
						+ "inner join users u on (m.emailid=u.userid)"
						+ "inner join dept_new d on (m.dept_id=d.dept_code)"
						+ "where m.dist_id='"+districtId+"' order by 1";
			} 

			else if (CommonModels.checkStringObject(officerType).equals("NO")) {

				sql = "select m.dept_id,upper(d.description) as description,trim(nd.fullname_en) as fullname_en, trim(nd.designation_name_en) as designation_name_en,m.mobileno,m.emailid from nodal_officer_details m "
						+ "    inner join (select distinct employee_id,fullname_en,designation_name_en from nic_data) nd on (m.employeeid=nd.employee_id)"
						+ "    inner join users u on (m.emailid=u.userid)"
						+ "    inner join dept_new d on (m.dept_id=d.dept_code)"
						+ "    where m.inserted_by ilike '%01'" + "    order by 1";

			} 

			else if (CommonModels.checkStringObject(officerType).equals("MLOSUBJECT")) {

				sql="select d.dept_code as dept_id,upper(d.description) as description,b.fullname_en, b.designation_name_en,m.mobileno,m.emailid from mlo_subject_details m " + 
						" inner join (select distinct employee_id,fullname_en,designation_id, designation_name_en from nic_data) b" + 
						" on (m.employeeid=b.employee_id and m.designation=b.designation_id)" + 
						" inner join users u on (m.emailid=u.userid)" + 
						" inner join dept_new d on (m.user_id=d.dept_code) order by 1";


			} 
			else {
				sql = "select d.dept_code as dept_id,upper(d.description) as description,b.fullname_en, b.designation_name_en,m.mobileno,m.emailid from mlo_details m "
						+" inner join (select distinct employee_id,fullname_en,designation_id, designation_name_en,email from nic_data ) b on ((trim(m.emailid)=trim(b.email)) or (m.employeeid=b.employee_id and m.designation=b.designation_id)) "
						+ "inner join users u on (m.emailid=u.userid)"
						+ "inner join dept_new d on (m.user_id=d.dept_code)" + "order by 1";

			}

			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}

	public  String getTableName(String districtId) {
		String tableName = "nic_data";
		if(districtId!=null && !districtId.equals("") && Integer.parseInt(districtId) > 0)
			tableName =jdbcTemplate.queryForObject("select tablename from district_mst where district_id="+districtId, String.class);
		System.out.println("dist::Id"+districtId+"-tableName::"+tableName);
		return tableName;
	}
	 
}
