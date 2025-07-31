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

@Service
public class NextListingDtAbstractServiceImpl implements NextListingDtAbstractService  {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getNextListingDtSecWise(UserDetailsImpl userPrincipal ) {
		String condition = "", sqlCondition1 = "", sql = "" ;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";

		try {

			if (roleId.equals("6") )
				condition= "  inner join ecourts_mst_gp_dept_map e on (e.dept_code=dn.dept_code)  where dn.display = true ";
			if (userId.equals("public-prosecutor@ap.gov.in")) {

				sqlCondition1 += " and e.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				sqlCondition1 += " and e.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA'  ";
			}

			sql = "select a1.reporting_dept_code as deptcode,dn1.description,sum(total) as  total,sum(today) as today, sum(tomorrow) as tomorrow,sum(week1) as week1, "
					+ " sum(week2) as week2, sum(week3) as week3,sum(week4) as week4 from ( "
					+ " select case when reporting_dept_code= 'CAB01' then a.dept_code  else reporting_dept_code  end as reporting_dept_code, a.dept_code,  "
					+ " count(distinct a.cino) as total, "
					+ " count(distinct case when (date_next_list = current_date) then a.cino end) as today, "
					+ " count(distinct case when (date_next_list = current_date + 1 ) then a.cino end) as tomorrow, "
					+ " count(distinct case when (date_next_list > current_date and date_next_list <= current_date + 7) then a.cino end) as week1, "
					+ " count(distinct case when (date_next_list > current_date + 7 and date_next_list <= current_date + 14) then a.cino end) as week2, "
					+ " count(distinct case when (date_next_list > current_date + 14 and date_next_list <= current_date + 21) then a.cino end) as week3, "
					+ "  count(distinct case when (date_next_list > current_date + 21 and date_next_list <= current_date + 28) then a.cino end) as week4 "
					+ "  from ecourts_case_data a "
					+ " inner join dept_new dn on (a.dept_code=dn.dept_code) "+condition+" "+sqlCondition1+" ";

			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9"))
				sql += " and (dn.reporting_dept_code='" +userPrincipal.getDeptCode()+ "' or dn.dept_code='"
						+ userPrincipal.getDeptCode() + "')";
			else if (roleId.equals("2")) {
				sql += " and a.dist_id='" + userPrincipal.getDistId()+ "'";
			}

			sql += " group by reporting_dept_code,a.dept_code) a1"

					+ " inner join dept_new dn1 on (a1.reporting_dept_code=dn1.dept_code) "
					+ " group by a1.reporting_dept_code,dn1.description" + " order by 1";
			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getHodWiseDetails(UserDetailsImpl userPrincipal,String deptCode,String deptName ) {
		String   condition = "", sqlCondition = "", sql = "" ;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId =userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {

			System.out.println("0----"+ deptCode.substring(3, 5));
			if ((roleId.equals("6"))) {
				condition = " inner join ecourts_mst_gp_dept_map emgm on (a.dept_code=emgm.dept_code)  ";
				sqlCondition += " and emgm.gp_id='" + userId + "'";
			}

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("17"))) { 
				sqlCondition += " and (a.dept_code='" + deptCode + "' or dn.reporting_dept_code='" + deptCode + "') "; 
			} 

			if( deptCode.substring(3, 5).equals("01")){
				sqlCondition += " and (a.dept_code='" + deptCode + "' or dn.reporting_dept_code='" + deptCode + "') ";
			}else {

				sqlCondition += " and (a.dept_code='" + deptCode + "') ";
			}

			if (roleId.equals("2")) {
				sql += " and a.dist_id='" + userPrincipal.getDistId() + "'";
			}

			sql = "select a.dept_code as deptcode,dn.description,count(distinct a.cino) as total,"
					+ " count(distinct case when (date_next_list = current_date) then a.cino end) as today,"
					+ " count(distinct case when (date_next_list = current_date + 1) then a.cino end) as tomorrow ,"
					+ " count(distinct case when (date_next_list > current_date and date_next_list <= current_date + 7) then a.cino end) as week1 ,  "
					+ " count(distinct case when (date_next_list > current_date + 7 and date_next_list <= current_date + 14) then a.cino end) as week2 ,  "
					+ " count(distinct case when (date_next_list > current_date + 14 and date_next_list <= current_date + 21) then a.cino end) as week3 ,  "
					+ " count(distinct case when (date_next_list > current_date + 21 and date_next_list <= current_date + 28) then a.cino end) as week4 "
					+ " from ecourts_case_data a "
					+ " inner join dept_new dn on (a.dept_code=dn.dept_code)  " + condition + " "
					+ " where dn.display = true  " + sqlCondition + " ";

			sql += "group by a.dept_code,dn.description order by 1";

			System.out.println("hod sql ==" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}



	@Override
	public List<Map<String, Object>> getNextListingDtCasesLists(UserDetailsImpl userPrincipal,String deptCode, String deptName,String caseStatus    ) {
		String sql = null, sqlCondition = "", roleId="",   heading = "" ,
				userId="",condition="",sqlCondition1="";
		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		try {

			heading = "Cases List for " + deptName;

			if (!caseStatus.equals("")) {
				if (caseStatus.equals("today")) {
					sqlCondition = " and (date_next_list = current_date) ";
					heading += " Hearing on Today";
				}
				if (caseStatus.equals("tomorrow")) {
					sqlCondition = " and (date_next_list = current_date+1) ";
					heading += " Hearing on Tomorrow";
				}
				if (caseStatus.equals("week1")) {
					sqlCondition = " and (date_next_list > current_date and date_next_list <= current_date+7) ";
					heading += " Hearing on next 7 days";
				}
				if (caseStatus.equals("week2")) {
					sqlCondition = " and (date_next_list > current_date+7 and date_next_list <= current_date+14) ";
					heading += " Hearing on next 8 - 14 days";
				}
				if (caseStatus.equals("week3")) {
					sqlCondition = " and (date_next_list > current_date+14 and date_next_list <= current_date+21) ";
					heading += " Hearing on next 15 - 21 days";
				}
				if (caseStatus.equals("week4")) {
					sqlCondition = " and (date_next_list > current_date+21 and date_next_list <= current_date+28) ";
					heading += " Hearing on next 21 - 28 days";
				}
			}

			if (roleId.equals("6") )
				condition= " inner join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code) ";
			if (userId.equals("public-prosecutor@ap.gov.in")) {

				sqlCondition1 += " and e.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				sqlCondition1 += " and e.gp_id='"+userId+"' and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA'  ";
			}

			sql = "select distinct a.cino,a.* from ecourts_case_data a "
					+ "  inner join dept_new d on (a.dept_code=d.dept_code) "+condition+" where d.display = true "+sqlCondition1+" ";

			if (roleId.equals("2")) {
				sql += " and a.dist_id='" +userPrincipal.getDistId()+ "'";
			}

			sql += " and (reporting_dept_code='" + deptCode + "' or a.dept_code='" + deptCode + "') " + sqlCondition;

			sql += " order by date_next_list asc";
			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);
			System.out.println("caseslist------ "+sql);



		}catch (Exception e) {

			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
}
