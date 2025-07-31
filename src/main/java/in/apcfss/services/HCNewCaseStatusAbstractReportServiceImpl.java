package in.apcfss.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
 
import in.apcfss.entities.UserDetailsImpl;

@Service
public class HCNewCaseStatusAbstractReportServiceImpl implements HCNewCaseStatusAbstractReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;
 
	@Override
	public List<Map<String, Object>> getHCNewCaseStatusSECWISE(Authentication authentication, String dofFromDate,
			String dofToDate, String caseTypeId, String districtId, String regYear, String deptId,
			String petitionerName, String respodentName, String serviceType1) {
	 
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		String sqlCondition = "",condition="",sql="", ConditionNew = "";
		try {
			
			System.out.println("SECWISE deptCode=====>"+deptCode);
			if (dofFromDate != null
					&& !dofFromDate.toString().contentEquals("")) {
				sqlCondition += " and b.inserted_time >= to_date('" +dofFromDate
						+ "','dd-mm-yyyy') ";
			}
			if (dofToDate!= null && !dofToDate.toString().contentEquals("")) {
				sqlCondition += " and b.inserted_time <= to_date('" +dofToDate
						+ "','dd-mm-yyyy') ";
			}
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and trim(b.casetype)='" +caseTypeId.toString().trim() + "' ";
			}
			if (caseTypeId!= null && caseTypeId.equals("4")) {
				sqlCondition += " and (trim(b.casetype)='" +caseTypeId.toString().trim() + "' or maincaseno like '%CC%') ";
			}
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and b.distid='" +districtId.toString().trim() + "' ";
			}
			if (regYear!= null && !regYear.equals("ALL") && !regYear.toString().contentEquals("0")) {
				sqlCondition += " ";// and a.reg_year='" + CommonModels.checkIntObject(formBean.getDyna("regYear"))+
									// "'
			}
			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and a.dept_code='" +deptId.toString().trim() + "' ";
			}

			//
			if (petitionerName != null
					&& !petitionerName.toString().contentEquals("")
					&& !petitionerName.toString().contentEquals("0")) {
			 
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"
						+ petitionerName+ "%'";

			}

			if (respodentName!= null
					&& !respodentName.toString().contentEquals("")
					&& !respodentName.toString().contentEquals("0")) {
				 
				sqlCondition += " and replace(replace(res_name,' ',''),'.','') ilike  '%"
						+respodentName + "%'";

			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {

				sqlCondition += " and a.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			

			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9")
					|| roleId.equals("10"))
				sql += " and (reporting_dept_code='" + deptCode+ "' or a.dept_code='"
						+deptCode+ "')";

			if (roleId.equals("2") || roleId.equals("10")) {
				sql += " and a.dist_id='" +distCode+ "' ";
				//formBean.setDyna("districtId", session.getAttribute("dist_id"));
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
					+ "sum(case when case_status=99 or coalesce(ecourts_case_status,'')='Closed' then 1 else 0 end) as closedcases, "
					+ "sum(case when case_status=96 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi, "
					+ "sum(case when case_status=97 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as psu, "
					+ "sum(case when case_status=98 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as privatetot "
					+ "from ecourts_gpo_ack_depts  a "
					+ " inner join ecourts_gpo_ack_dtls b on (a.ack_no=b.ack_no) inner join dept_new d on (a.dept_code=d.dept_code)"
					+ " where b.ack_type='NEW'    " + sqlCondition; // and respondent_slno=1
			
			sql += " group by a.dept_code,d.dept_code ,reporting_dept_code ) x inner join dept_new d1 on (x.reporting_dept_code=d1.dept_code)"
					+ " group by x.reporting_dept_code, d1.description order by 1";

			System.out.println("SQL:show sec DeptWise " + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getHODwisedetails(Authentication authentication, String dofFromDate, String dofToDate,
			String caseTypeId, String districtId, String regYear, String deptId, String petitionerName,
			String respodentName, String serviceType1,String deptName) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		 
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sqlCondition = "",condition="",sql="", ConditionNew = "";
		try {
		
			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
				deptName = jdbcTemplate.queryForObject(
						"select upper(description) as description from dept_new where dept_code='" + deptCode + "'",
						String.class);
			}  
			if (dofFromDate != null
					&& !dofFromDate.toString().contentEquals("")) {
				sqlCondition += " and b.inserted_time >= to_date('" +dofFromDate
						+ "','dd-mm-yyyy') ";
			}
			if (dofToDate!= null && !dofToDate.toString().contentEquals("")) {
				sqlCondition += " and b.inserted_time <= to_date('" + dofToDate
						+ "','dd-mm-yyyy') ";
			}
			if (caseTypeId != null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and trim(b.casetype)='" +caseTypeId.toString().trim() + "'  ";
			}
			
			if (caseTypeId!= null && caseTypeId.equals("4")) {
				sqlCondition += " and (trim(b.casetype)='" +caseTypeId.toString().trim() + "' or maincaseno like '%CC%') ";
			}
			
			
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and b.distid='" +districtId.toString().trim() + "' ";
			}
			if (regYear!= null && regYear.equals("ALL")
					&& !regYear.toString().contentEquals("0")) {
				sqlCondition += "  ";// and a.reg_year='" + CommonModels.checkIntObject(formBean.getDyna("regYear"))
										// + "'
			}

			if (petitionerName!= null
					&& !petitionerName.toString().contentEquals("")
					&& !petitionerName.toString().contentEquals("0")) {
				sqlCondition += " and replace(replace(b.petitioner_name,' ',''),'.','') ilike  '%"
						+petitionerName+ "%'";

			}

			if (respodentName!= null
					&& !respodentName.toString().contentEquals("")
					&& !respodentName.toString().contentEquals("0")) {
				sqlCondition += " and replace(replace(a.res_name,' ',''),'.','') ilike  '%"
						+respodentName+ "%'";

			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {

				sqlCondition += " and a.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
		 

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" + distCode + "' ";
				//formBean.setDyna("districtId", session.getAttribute("dist_id"));
			}
			
			System.out.println("HODwise deptCode=====>"+deptId);

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
					+ "sum(case when case_status=99 or coalesce(ecourts_case_status,'')='Closed' then 1 else 0 end) as closedcases, "
					+ "sum(case when case_status=96 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi, "
					+ "sum(case when case_status=97 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as psu, "
					+ "sum(case when case_status=98 and coalesce(ecourts_case_status,'')!='Closed' then 1 else 0 end) as privatetot "
					+ "from ecourts_gpo_ack_depts  a "
					+ " inner join ecourts_gpo_ack_dtls b on (a.ack_no=b.ack_no) inner join dept_new d on (a.dept_code=d.dept_code)"
					+ " where b.ack_type='NEW'  and (d.reporting_dept_code='" + deptId // and respondent_slno=1
					+ "' or a.dept_code='" + deptId + "') " + sqlCondition
					+ "group by a.dept_code , d.description order by 1";
			
			System.out.println("HOD new--:" + sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getHccNewCasesList(Authentication authentication, String dofFromDate, String dofToDate,
			String caseTypeId, String districtId, String regYear, String deptId, String petitionerName,
			String respodentName, String serviceType1, String deptName, String caseStatus, String reportLevel,
			String caseCategory, String deptType) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		 
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		String sqlCondition = "",condition="",sql="", ConditionNew = "",heading="";
		try {
		System.out.println("deptCode---"+deptCode);
			heading = "New Cases List for " + deptName;
			 System.out.println("caseStatus----" + caseStatus);
			if (!caseStatus.equals("")) {
				if (caseStatus.equals("withSD")) {
					sqlCondition = " and case_status=1 and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Sect Dept. Login";
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
					heading += " Pending at Sction Officer(District) Login";
				}
				if (caseStatus.equals("withGP")) {
					sqlCondition = " and case_status=6 and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at GP Login";
				}
				if (caseStatus.equals("closed")) {
					sqlCondition = " and (case_status=99 or coalesce(ecourts_case_status,'')='Closed') ";
					heading += " All Closed Cases ";
				}
				if (caseStatus.equals("goi")) {
					sqlCondition = " and (case_status=96 or coalesce(ecourts_case_status,'')='Closed') ";
					heading += " Pending at Govt. of India ";
				}
				if (caseStatus.equals("psu")) {
					sqlCondition = " and (case_status=97 or coalesce(ecourts_case_status,'')='Closed') ";
					heading += " Pending at PSU ";
				}
				if (caseStatus.equals("Private")) {
					sqlCondition = " and case_status=98 and coalesce(ecourts_case_status,'')!='Closed' ";
					heading += " Pending at Private ";
				}
			}
			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
				deptName = jdbcTemplate.queryForObject(
						"select upper(description) as description from dept_new where dept_code='" + deptCode + "'",
						String.class);
			}  

			System.out.println("district iddddddd  " + districtId);
			if (dofFromDate!= null
					&& !dofFromDate.toString().contentEquals("")) {
				sqlCondition += " and e.inserted_time >= to_date('" +dofFromDate
						+ "','dd-mm-yyyy') ";
			}
			if (dofToDate!= null && !dofToDate.toString().contentEquals("")) {
				sqlCondition += " and e.inserted_time <= to_date('" +dofToDate
						+ "','dd-mm-yyyy') ";
			}
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and trim(e.casetype)='" +caseTypeId.toString().trim() + "' ";
			}

			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and a.dist_id='" +districtId.toString().trim() + "' ";
			}

			if (regYear!= null && !regYear.equals("ALL")
					&&!regYear.toString().contentEquals("0")) {
				sqlCondition += "  "; 
			}

			if (petitionerName!= null
					&& !petitionerName.toString().contentEquals("")
					&& !petitionerName.toString().contentEquals("0")) {
				sqlCondition += " and replace(replace(e.petitioner_name,' ',''),'.','') ilike  '%"
						+petitionerName + "%'";

			}

			if (respodentName!= null
					&& !respodentName.toString().contentEquals("")
					&& !respodentName.toString().contentEquals("0")) {
				sqlCondition += " and replace(replace(a.res_name,' ',''),'.','') ilike  '%"
						+ respodentName + "%'";

			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {

				sqlCondition += " and a.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" +distCode + "' ";
				//formBean.setDyna("districtId", session.getAttribute("dist_id"));
			}

			/*
			 * if (actionType.equals("SDWISE")) { } else if (actionType.equals("HODWISE")) {
			 * }
			 */

			System.out.println("roleId--" + roleId);

			 
			if (roleId.equals("6"))
				condition = " inner join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code) ";

			if (caseCategory != null && !caseCategory.equals("")) {

				sqlCondition += " and trim(disposal_type)='" + caseCategory.trim() + "'  and trim(d.description)='"
						+ deptType.trim() + "'  ";
				heading += caseCategory.trim() + ", Department : " + deptType.trim();
			}

			sql = "select a.ack_no,file_found,advocatename,advocateccno,cm.case_short_name,maincaseno,to_char(e.inserted_time,'dd-mm-yyyy') as inserted_time,petitioner_name,"
					+ "getack_dept_desc(a.ack_no::text) as dept_descs,"
					+ "	 services_flag,reg_year,reg_no,mode_filing,case_category,dm.district_name,coalesce(e.hc_ack_no,'-') as hc_ack_no,barcode_file_path, "
					+ " coalesce(trim(e.ack_file_path),'-') as scanned_document_path1,'' as orderpaths "
					+ " from  ecourts_gpo_ack_depts  a "
					+ " inner join ecourts_gpo_ack_dtls e on (a.ack_no=e.ack_no) left join scanned_affidavit_new_cases_count sc on (sc.ack_no=e.ack_no or  sc.ack_no=e.hc_ack_no) "
					+ " inner join dept_new d on (a.dept_code=d.dept_code) "
					+ " inner join district_mst dm on (e.distid=dm.district_id) "
					+ " inner join case_type_master cm on (e.casetype=cm.sno::text or e.casetype=cm.case_short_name) "
					+ " " + condition + " where e.ack_type='NEW' "+sqlCondition+" "; 

			 
			
			if (reportLevel.equals("SD")) {
				sql += " and (reporting_dept_code='" + deptId + "' or a.dept_code='" + deptId + "') ";
			} else {
				// if(reportLevel.equals("HOD")) {
				sql += " and a.dept_code='" + deptId + "' ";
			}

			
			
		System.out.println("sql----"+sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	
	 

}
