package in.apcfss.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommonMethodServiceImpl implements CommonMethodService {
	
	public static final String requestUrl = "https://cdacsms.apcfss.in/services/APCfssSmsGateWayReq/sendTextSms";
	 
	public static final String userpass = "APGOVT-APCFSS:Apcfss@2020"; // Production // Credentials

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getEmpSectionsList(String distCode, String deptCode, String tableName) {
		String sql = "";
		if (deptCode != null && deptCode != "") {
			sql = "select trim(employee_identity) as value,trim(employee_identity) as label from " + tableName
					+ " where substr(trim(global_org_name),1,5)='" + deptCode + "' and trim(employee_identity)!='NULL' "
					+ " and trim(email) "
					+ "not in (select userid from user_roles where role_id in (4,5)) group by trim(employee_identity) order by 1";
			System.out.println(deptCode + ":getEmpDeptSectionsList Id sql:" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpPostsList(String distCode, String deptCode, String tableName,
			String empSec) {
		String sql = "";
		if (deptCode != null && deptCode != "") {
			sql = "select trim(post_name_en) as value, trim(post_name_en) as label from " + tableName
					+ " where substr(trim(global_org_name),1,5)='" + deptCode + "' and trim(employee_identity)!='NULL'"
					+ " and trim(employee_identity)=trim('" + empSec + "') "
					+ "  and trim(email) not in (select userid from user_roles where role_id in (4,5)) group by post_name_en";
			System.out.println(deptCode + ":getEmpPostsList sql:" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpsList(String distCode, String deptCode, String tableName, String empSec,
			String empPost) {
		String sql = "";
		if (deptCode != null && deptCode != "") {
			sql = "select distinct trim(email) as value,"
					+ " trim(fullname_en)||' - '||trim(org_unit_name_en) as label from " + tableName
					+ " where trim(employee_identity)=trim('" + empSec + "') and trim(post_name_en)=trim('" + empPost
					+ "') and substr(trim(global_org_name),1,5)='" + deptCode + "' "
					+ " and trim(email) not in (select userid from user_roles where role_id in (4,5)) ";

			System.out.println(deptCode + ":getEmpsList Id :sql" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpPostsListWithMandal(String empDept, String distCode, String tableName,
			String empSec, String mndlCode) {
		String sql = "";
		if (empDept != null && empDept != "") {
			sql = "select trim(post_name_en) as value, trim(post_name_en) as label from " + tableName
					+ " where substr(trim(global_org_name),1,5)='" + empDept + "' and trim(employee_identity)!='NULL'"
					+ " and trim(employee_identity)=trim('MANDAL') and tehsilcode='" + mndlCode + "' "
					+ "  and trim(email) not in (select userid from user_roles where role_id in (4,5)) group by post_name_en";
			System.out.println(empDept + ":getEmpPostsListWithMandal sql:" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpPostsListWithMandalandVill(String empDept, String distCode, String tableName,
			String empSec, String mndlCode, String village) {
		String sql = "";
		String tehsilcode[] = village.split("-");
		String tehsilcode_1 = tehsilcode[0];
		String tehsilcode_2 = tehsilcode[1];

		System.out.println("tehsilcode_1--" + tehsilcode_1 + "tehsilcode_2--" + tehsilcode_2);

		if (empDept != null && empDept != "") {
			sql = "select trim(post_name_en) as value, trim(post_name_en) as label from " + tableName
					+ " where substr(trim(global_org_name),1,5)='" + empDept + "' and trim(employee_identity)!='NULL'"
					+ " and trim(employee_identity)=trim('VILLAGE') and tehsilcode='" + tehsilcode_1
					+ "' and villagecode='" + tehsilcode_2 + "'  " // --and tehsilcode='"+mndlCode+"'
					+ "  and trim(email) not in (select userid from user_roles where role_id in (4,5)) group by post_name_en";
			System.out.println(empDept + ":getEmpPostsListWithMandalandVill sql:" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getMandalList(String empDept, String distCode, String tableName, String empSec) {
		String sql = "";
		if (empDept != null && empDept != "") {
			sql = "select distinct trim(tehsilcode) as value,trim(tehsil) as label from " + tableName + " where "
					+ "substr(trim(global_org_name),1,5)='" + empDept + "' " + "and trim(employee_identity)='" + empSec
					+ "' and trim(email) not in (select userid from user_roles where role_id in (4,5)) order by 1 ";
			System.out.println(empDept + ":getEmpPostsListWithMandal sql:" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getVillageList(String empDept, String distCode, String tableName, String empSec) {
		String sql = "";
		if (empDept != null && empDept != "") {
			sql = "select distinct trim(tehsilcode||'-'||villagecode) as value,trim(tehsil||' Mandal-'||village||' Village') as label from "
					+ tableName + " where " + "substr(trim(global_org_name),1,5)='" + empDept
					+ "' and trim(employee_identity)='" + empSec + "' and "
					+ "trim(email) not in (select userid from user_roles where role_id in (4,5))  order by 1" + " ";
			System.out.println(empDept + ":getEmpPostsListWithMandal sql:" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpsEmailsListWithMandal(String tableName, String empDept, String distCode,
			String empSec, String empPost, String mndlCode) {
		String sql = "";
		if (empDept != null && empDept != "") {
			sql = "select distinct trim(email) as value,"
					+ "  trim(fullname_en)||' - '||trim(org_unit_name_en) as label  from " + tableName
					+ " where trim(employee_identity)=trim('" + empSec + "') and trim(post_name_en)=trim('" + empPost
					+ "') and substr(trim(global_org_name),1,5)='" + empDept + "' and tehsilcode='" + mndlCode + "' "
					+ " and trim(email) not in (select userid from user_roles where role_id in (4,5))  ";

			System.out.println(empDept + ":getEmpsEmailsListWithMandal Id :sql" + sql);
		}

		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpsEmailsListWithMandalAndVill(String tableName, String empDept,
			String distCode, String empSec, String empPost, String mndlCode, String vlgCode) {

		String sql = "";
		String tehsilcode[] = vlgCode.split("-");
		String tehsilcode_1 = tehsilcode[0];
		String tehsilcode_2 = tehsilcode[1];

		System.out.println("tehsilcode_1 :" + tehsilcode_1);

		if (empDept != null && empDept != "") {
			sql = "select distinct trim(email) as value,"
					+ " trim(fullname_en)||' - '||trim(org_unit_name_en) as label from " + tableName
					+ " where trim(employee_identity)=trim('" + empSec + "') and trim(post_name_en)=trim('" + empPost
					+ "') and substr(trim(global_org_name),1,5)='" + empDept + "' and tehsilcode='" + tehsilcode_1
					+ "'  " + " and villagecode='" + tehsilcode_2 + "' "
					+ " and trim(email) not in (select userid from user_roles where role_id in (4,5))";

			System.out.println(empDept + ":getEmpsEmailsListWithMandalAndVill Id :sql" + sql);
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpsEmailsList(String tableName, String empDept, String distCode, String empSec,
			String empPost) {

		String sql = "";
		if (empDept != null && empDept != "") {
			sql = "select distinct trim(email) as value,"
					+ " trim(fullname_en)||' - '||trim(org_unit_name_en) as label from " + tableName
					+ " where trim(employee_identity)=trim('" + empSec + "') and trim(post_name_en)=trim('" + empPost
					+ "') and substr(trim(global_org_name),1,5)='" + empDept + "' "
					+ " and trim(email) not in (select userid from user_roles where role_id in (4,5))";

			System.out.println(empDept + ":getEmpsList Id :sql" + sql);
		}

		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getCategorywiseHodReport(String caseCategory) {
		String condition = "";

		if (caseCategory.equals("legacyService")) {
			condition = "and category_service='SERVICE' ";
		} else if (caseCategory.equals("legacynonService")) {
			condition = "and (category_service is null or category_service='NON-SERVICE' OR category_service=' ') ";
		} else {
			condition = "";
		}

		String sql = "select b.dept_code,c.description,category_service,count(b.cino) as total from ecourts_case_data b "
				+ "inner join dept_new c on (c.dept_code=b.dept_code) WHERE b.dept_code!='0' " + condition
				+ " group by b.dept_code,description,category_service";

		System.out.println("sql legacy category  : " + sql);
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getCategorywiseHodReportNew(String caseCategory) {

		String condition = "";

		if (caseCategory.equals("NewService")) {
			condition = "and (servicetpye!='NON-SERVICES' and servicetpye is not null and servicetpye!='' and servicetpye!='0' )";
		} else if (caseCategory.equals("newnonService")) {
			condition = "and (servicetpye='NON-SERVICES' or servicetpye is null or servicetpye='' or servicetpye='0' )";
		} else {
			condition = "";
		}

		String sql = "select b.dept_code,c.description,servicetpye,count(a.ack_no) as total from ecourts_gpo_ack_dtls a "
				+ "inner join ecourts_gpo_ack_depts b on (a.ack_no=b.ack_no) inner join dept_new c on (c.dept_code=b.dept_code) where ack_type='NEW' and b.dept_code!='0' "
				+ "and respondent_slno='1' " + condition + " group by b.dept_code,description,servicetpye";
		System.out.println("sql new category  : " + sql);
		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getEmployeesList( String deptId,String designationId,String tableName,String roleId) {
		String sql = "";
		sql = "select distinct employee_id as value , fullname_en||' - '||org_unit_name_en as label from "+tableName+" where substring(global_org_name,1,5)='"+deptId+"' and designation_id='"+designationId+"' order by 2 ";

		if (roleId != null && roleId != "") {
			if (roleId != null && roleId == "3") {
			sql = "select distinct employee_id as value,  fullname_en ||' - '||org_unit_name_en as label from " + tableName+" "
					+ "where substring(global_org_name,1,5)='" + deptId + "' and designation_id='"+ designationId +"' and email not in (select emailid from mlo_details) order by 2 ";

		}else if (roleId == "5") {
			sql = " select distinct employee_id as value,fullname_en||' - '||org_unit_name_en as label from " +tableName+" where substring(global_org_name,1,5)='" + deptId + "' and designation_id='" + designationId+"'  and email not in (select emailid from nodal_officer_details where dept_id='"+ deptId+"' ') order by 2)a ";
			
			}
		}
		System.out.println(deptId + ":getEmpsList Id :sql" + sql);
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEmpDetails(String empId, String designationId, String tableName) {
		String sql = "";

		if (empId != null) {
			sql = "select distinct employee_id||'#'||replace(mobile1, 'NULL', '')||'#'||replace(email, 'NULL', '') as value from "
					+ tableName + " where employee_id ='" + empId + "' and designation_id='" + designationId + "'";
		}
		
		System.out.println("getEmpDetails SQL:" + sql);
		return jdbcTemplate.queryForList(sql);
	}

	
}
