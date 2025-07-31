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
public class ContemptCasesAbstractServiceImpl implements ContemptCasesAbstractService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getSecdeptwiseData(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "",heading="",deptId="",deptName="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";

		deptId = (String) userPrincipal.getDeptCode();
		deptName = repo.getDeptName(deptId);
		try {

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
					&& !abstractReqBody.getDistId().toString().contentEquals("0") && !abstractReqBody.getDistId().equals("ALL") ) {
				sqlCondition += " and a.dist_id='" + abstractReqBody.getDistId().toString().trim() + "' ";
			}

			if (abstractReqBody.getRegYear() != null && !abstractReqBody.getRegYear().toString().contentEquals("")
					&& !abstractReqBody.getRegYear().equals("ALL") /* && (abstractReqBody.getRegYear().length()) > 0 */) { 
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

			sql = "select x.reporting_dept_code as deptcode, upper(d1.description) as description,sum(total_cases) as total_cases,sum(withsectdept) as withsectdept,sum(withmlo) as withmlo,sum(withhod) as withhod,sum(withnodal) as withnodal,sum(withsection) as withsection, sum(withdc) as withdc, sum(withdistno) as withdistno,sum(withsectionhod) as withsectionhod, sum(withsectiondist) as withsectiondist, sum(withgpo) as withgpo, sum(closedcases) as closedcases, sum(goi) as goi, sum(psu) as psu, sum(privatetot) as privatetot  from ("
					+ "select a.dept_code , case when reporting_dept_code='CAB01' then d.dept_code else reporting_dept_code end as reporting_dept_code,count(*) as total_cases, "
					+ "sum(case when case_status=1 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectdept, "
					+ "sum(case when (case_status is null or case_status=2)  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withmlo, "
					+ "sum(case when case_status=3  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withhod, "
					+ "sum(case when case_status=4  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withnodal, "
					+ "sum(case when case_status=5 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsection, "
					+ "sum(case when case_status=7  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withdc, "
					+ "sum(case when case_status=8  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withdistno, "
					+ "sum(case when case_status=9 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectionhod, "
					+ "sum(case when case_status=10 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectiondist, "
					+ "sum(case when case_status=6 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withgpo, "
					+ " sum(case when case_status=99  or pend_disp='D' or   coalesce(ecourts_case_status,'')='Closed' then 1 else 0 end) as closedcases, "
					+ "sum(case when case_status=96 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi, "
					+ "sum(case when case_status=97 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as psu, "
					+ "sum(case when case_status=98 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as privatetot "
					+ "from ecourts_case_data a " + "inner join dept_new d on (a.dept_code=d.dept_code) "
					+ "where d.display = true  and a.case_type_id='6' " + sqlCondition;

			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9") || roleId.equals("10"))
				sql += " and (reporting_dept_code='" + deptId+ "' or a.dept_code='"
						+deptId + "')";

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" + userPrincipal.getDistId() + "' ";
				userPrincipal.setDistId( userPrincipal.getDistId());
			}

			sql += " group by a.dept_code,d.dept_code ,reporting_dept_code ) x inner join dept_new d1 on (x.reporting_dept_code=d1.dept_code)"
					+ " group by x.reporting_dept_code, d1.description order by 1";


			heading="Sect. Dept. Wise Contempt Cases Abstract Report " + deptName;

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);

			System.out.println("unspecified SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getDeptWiseData(UserDetailsImpl userPrincipal,String roleId,HCCaseStatusAbstractReqBody abstractReqBody) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "",deptId="",deptName="";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		try {
			deptId = (String) userPrincipal.getDeptCode();

			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptId = (String) userPrincipal.getDeptCode();
				deptName = repo.getDeptName(deptId);
			} else {
				deptId = (String) userPrincipal.getDeptCode();
				deptName = repo.getDeptName(deptId);
			}

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
					&& !abstractReqBody.getRegYear().equals("ALL") /* && (abstractReqBody.getRegYear().length()) > 0 */) { 
				sqlCondition += " and a.reg_year='" + abstractReqBody.getRegYear() + "' "; 
			}


			if (abstractReqBody.getPetitionerName() != null && !abstractReqBody.getPetitionerName().toString().contentEquals("")
					&& !abstractReqBody.getPetitionerName().toString().contentEquals("0")) {
				//sqlCondition += " and replace(replace(a.pet_name,' ',''),'.','') ilike  '%"+abstractReqBody.getPetitionerName()+"%'";
				sqlCondition += " and a.pet_name ilike  '%"+abstractReqBody.getPetitionerName()+"%'";
			}

			if (abstractReqBody.getRespodentName() != null && !abstractReqBody.getRespodentName().toString().contentEquals("")
					&& !abstractReqBody.getRespodentName().toString().contentEquals("0")) {
				//sqlCondition += " and replace(replace(a.res_name,' ',''),'.','') ilike  '%"+abstractReqBody.getRespodentName()+"%'";
				sqlCondition += " and  a.res_name  ilike  '%"+abstractReqBody.getRespodentName()+"%'";
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

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" + userPrincipal.getDistId() + "' ";
				userPrincipal.setDistId(  userPrincipal.getDistId());
			}

			sql = "select a.dept_code as deptcode , upper(d.description) as description,count(*) as total_cases, "
					+ "sum(case when case_status=1 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectdept, "
					+ "sum(case when (case_status is null or case_status=2)  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withmlo, "
					+ "sum(case when case_status=3  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withhod, "
					+ "sum(case when case_status=4  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withnodal, "
					+ "sum(case when case_status=5 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsection, "
					+ "sum(case when case_status=7  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withdc, "
					+ "sum(case when case_status=8  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withdistno, "
					+ "sum(case when case_status=9 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectionhod, "
					+ "sum(case when case_status=10 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectiondist, "
					+ "sum(case when case_status=6 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withgpo, "
					+ "sum(case when case_status=99  or pend_disp='D' or   coalesce(ecourts_case_status,'')='Closed' then 1 else 0 end) as closedcases, "
					+ "sum(case when case_status=96 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi, "
					+ "sum(case when case_status=97 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as psu, "
					+ "sum(case when case_status=98 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as privatetot "
					+ "from ecourts_case_data a " + "inner join dept_new d on (a.dept_code=d.dept_code) "
					+ "where d.display = true  and a.case_type_id='6' and (d.reporting_dept_code='" + deptId + "' or a.dept_code='" + deptId
					+ "') " + sqlCondition + "group by a.dept_code , d.description order by 1";

			String heading="HOD Wise Contempt Cases Abstract Report for " + deptName;

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);

			System.out.println("SQL:" + sql);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getDeptNameWiseData(UserDetailsImpl userPrincipal,String deptCode, String deptName) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "" ;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		try {
			//deptId = (String) userPrincipal.getDeptCode();

			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptCode = (String) userPrincipal.getDeptCode();
				deptName = repo.getDeptName(deptCode);
			} /*
			 * else { deptCode = (String) userPrincipal.getDeptCode(); deptName =
			 * repo.getDeptName(deptCode); }
			 */
			System.out.println("deptCode-----"+deptCode+"deptName---"+deptName);

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" + userPrincipal.getDistId() + "' ";
				userPrincipal.setDistId( userPrincipal.getDistId());
			}

			sql = "select a.dept_code as deptcode , upper(d.description) as description,count(*) as total_cases, "
					+ "sum(case when case_status=1 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectdept, "
					+ "sum(case when (case_status is null or case_status=2)  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withmlo, "
					+ "sum(case when case_status=3  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withhod, "
					+ "sum(case when case_status=4  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withnodal, "
					+ "sum(case when case_status=5 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsection, "
					+ "sum(case when case_status=7  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withdc, "
					+ "sum(case when case_status=8  and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withdistno, "
					+ "sum(case when case_status=9 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectionhod, "
					+ "sum(case when case_status=10 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withsectiondist, "
					+ "sum(case when case_status=6 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as withgpo, "
					+ "sum(case when case_status=99  or pend_disp='D' or   coalesce(ecourts_case_status,'')='Closed' then 1 else 0 end) as closedcases, "
					+ "sum(case when case_status=96 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi, "
					+ "sum(case when case_status=97 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as psu, "
					+ "sum(case when case_status=98 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as privatetot "
					+ "from ecourts_case_data a " + "inner join dept_new d on (a.dept_code=d.dept_code) "
					+ "where d.display = true  and a.case_type_id='6' and (d.reporting_dept_code='" + deptCode + "' or a.dept_code='" + deptCode
					+ "') " + sqlCondition + "group by a.dept_code , d.description order by 1";

			String heading="HOD Wise Contempt Cases Abstract Report for " + deptName;

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);

			System.out.println("SQL:" + sql);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getContemptCasesListdata(UserDetailsImpl userPrincipal,String deptCode, String deptName,String caseStatus  ,String reportLevel,HCCaseStatusAbstractReqBody abstractReqBody) {
		String sql = null, sqlCondition = "", roleId="",  deptId = "", heading = "",
				userId="",condition="",sqlCondition1="";
		List<Map<String, Object>> dataa = new ArrayList<>();
		try {
			userId = userPrincipal.getUserId();
			roleId = userPrincipal.getRoleId();

			//deptCode = (String) userPrincipal.getDeptCode();
			//deptName = repo.getDeptName(deptCode);

			heading = "Contempt Cases List for " + deptName;

			if (!caseStatus.equals("")) {
				if (caseStatus.equals("withSD")) {
					sqlCondition = " and case_status=1 and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Sect. Dept. Login";
				}
				if (caseStatus.equals("withMLO")) {
					sqlCondition = " and (case_status is null or case_status=2)  and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at MLO Login";
				}
				if (caseStatus.equals("withHOD")) {
					sqlCondition = " and case_status=3  and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at HOD Login";
				}
				if (caseStatus.equals("withNO")) {
					sqlCondition = " and case_status=4  and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Nodal Officer(HOD) Login";
				}
				if (caseStatus.equals("withSDSec")) {
					sqlCondition = " and case_status=5 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Section Officers Login (Sect Dept.)";
				}
				if (caseStatus.equals("withsection")) {
					sqlCondition = " and case_status=5 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Section Officers Login (Sect Dept.)";
				}
				if (caseStatus.equals("withDC")) {
					sqlCondition = " and case_status=7  and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at District Collector Login";
				}
				if (caseStatus.equals("withDistNO")) {
					sqlCondition = " and case_status=8  and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Nodal Officer(District) Login";
				}
				if (caseStatus.equals("withHODSec")) {
					sqlCondition = " and case_status=9 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Section Officer(HOD) Login";
				}
				if (caseStatus.equals("withDistSec")) {
					sqlCondition = " and case_status=10 and coalesce(assigned,'f')='t' and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Section Officer(District) Login";
				}
				if (caseStatus.equals("withGP")) {
					sqlCondition = " and case_status=6 and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at GP Login";
				}
				if (caseStatus.equals("closed")) {
					sqlCondition = " and (case_status=99  or pend_disp='D' or   coalesce(ecourts_case_status,'')='Closed') ";
					heading += " All Closed Cases ";
				}
				if (caseStatus.equals("goi")) {
					sqlCondition = " and (case_status=96 or coalesce(ecourts_case_status,'')!='Closed') ";
					heading += " Govt. of India ";
				}
				if (caseStatus.equals("psu")) {
					sqlCondition = " and (case_status=97 or coalesce(ecourts_case_status,'')!='Closed') ";
					heading += " PSU ";
				}
				if (caseStatus.equals("Private")) {
					sqlCondition = " and (case_status=98 or coalesce(ecourts_case_status,'')!='Closed') ";
					heading += " Private ";
				}
			}

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
					&& !abstractReqBody.getDistId().toString().contentEquals("0") && !abstractReqBody.getDistId().equals("ALL")) {
				sqlCondition += " and a.dist_id='" + abstractReqBody.getDistId().toString().trim() + "' ";
			}

			if (abstractReqBody.getRegYear() != null && !abstractReqBody.getRegYear().toString().contentEquals("")
					&& !abstractReqBody.getRegYear().equals("ALL") /* && (abstractReqBody.getRegYear().length()) > 0 */) { 
				sqlCondition += " and a.reg_year='" + abstractReqBody.getRegYear() + "' "; 
			}


			if (abstractReqBody.getPetitionerName() != null && !abstractReqBody.getPetitionerName().toString().contentEquals("")
					&& !abstractReqBody.getPetitionerName().toString().contentEquals("0")) {
				//sqlCondition += " and replace(replace(a.pet_name,' ',''),'.','') ilike  '%"+abstractReqBody.getPetitionerName()+"%'";
				sqlCondition += " and a.pet_name ilike  '%"+abstractReqBody.getPetitionerName()+"%'";
			}

			if (abstractReqBody.getRespodentName() != null && !abstractReqBody.getRespodentName().toString().contentEquals("")
					&& !abstractReqBody.getRespodentName().toString().contentEquals("0")) {
				//sqlCondition += " and replace(replace(a.res_name,' ',''),'.','') ilike  '%"+abstractReqBody.getRespodentName()+"%'";
				sqlCondition += " and  a.res_name  ilike  '%"+abstractReqBody.getRespodentName()+"%'";
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



			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" + userPrincipal.getDistId() + "' ";
				userPrincipal.setDistId( userPrincipal.getDistId());
			}

			System.out.println("roleId--"+roleId);

			if (roleId.equals("6") )
				condition= " inner join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code) ";


			sql = "select a.*, ";

			if (caseStatus.equals("withDistSec") || caseStatus.equals("withHODSec") || caseStatus.equals("withSDSec")) {
				sql+=" nda.fullname_en as fullname, nda.designation_name_en as designation, nda.post_name_en as post_name, nda.email, nda.mobile1 as mobile,dim.district_name , nda.org_unit_name_en, ";
			}

			sql+= " 'Pending at '||ecs.status_description||' ' as current_status, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths,"
					+ " case when (prayer is not null and coalesce(trim(prayer),'')!='' and length(prayer) > 2) then substr(prayer,1,250) else '-' end as prayer, prayer as prayer_full,"
					+ " ra.address from ecourts_case_data a "
					+ " left join nic_prayer_data np on (a.cino=np.cino)"
					+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1) ";

			if (caseStatus.equals("withDistSec")) {
				sql+=" left join nic_data_all nda on (a.dept_code=substr(nda.global_org_name,1,5) and a.assigned_to=nda.email and nda.is_primary='t' and coalesce(a.dist_id,'0')=coalesce(nda.dist_id,'0')) ";
			}
			else if (caseStatus.equals("withHODSec") || caseStatus.equals("withSDSec")) {
				sql+=" left join nic_data_all nda on (a.dept_code=substr(nda.global_org_name,1,5) and a.assigned_to=nda.email and nda.is_primary='t') ";
			}

			sql+=" left join ecourts_mst_case_status ecs on (a.case_status=ecs.status_id) ";
			sql+=" left join district_mst dim on (a.dist_id=dim.district_id) ";

			sql+= " left join"
					+ " ("
					+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
					+ " from "
					+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1" + " union"
					+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
					+ " on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code) "+condition+" where d.display = true  and a.case_type_id='6' ";


			if(reportLevel.equals("SD")) {
				sql += " and (reporting_dept_code='" + deptCode + "' or a.dept_code='" + deptCode + "') ";
			}
			else if(reportLevel.equals("HOD")) {
				sql += " and a.dept_code='" + deptCode + "' ";
			}

			sql += sqlCondition;



			Map<String, Object> map = new HashMap<>();
			map.put("HEADING",heading);

			map.put("data",jdbcTemplate.queryForList(sql));
			dataa.add(map);
			System.out.println("ecourts SQL:" + sql);



		}catch (Exception e) {

			e.printStackTrace();
		}
		return dataa;
	}

}
