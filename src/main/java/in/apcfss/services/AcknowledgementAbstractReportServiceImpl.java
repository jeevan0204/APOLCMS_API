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
import in.apcfss.repositories.GPOAcknowledgementDetailsRepo;

@Service
public class AcknowledgementAbstractReportServiceImpl implements AcknowledgementAbstractReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getAcksAbstractReportHODWise(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId) {
	 
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sqlCondition = "",condition="",sql="", ConditionNew = "";
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
			Date date = new Date(); 

			System.out.println("date--"+formatter.format(date)+"   FROM--"+fromDate +" TO--------"+toDate );

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				System.out.println("FROMMM");
				condition += " and a.inserted_time::date >= to_date('" +fromDate
						+ "','dd-mm-yyyy') ";

			}  
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				condition += " and a.inserted_time::date <= to_date('"+toDate+ "','dd-mm-yyyy') ";

			}  
			 
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" + fromDate
						+ "','dd-mm-yyyy') ";

			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";

			}
			if (advcteName!= null
					&& !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"
						+advcteName+ "%'";
			}
			if (petitionerName!= null
					&& !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"
						+petitionerName+ "%'";
			}

			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";

			}

			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" + caseTypeId.toString().trim() + "'  ";
			}
			
			if (caseTypeId!= null && caseTypeId.equals("4")) {
				sqlCondition += " and (trim(ad.casetype)='" +caseTypeId.toString().trim() + "' or maincaseno like '%CC%') ";
			}
			
			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dm.dept_code='" + deptCode + "' or dm.reporting_dept_code='" + deptCode + "') ";
			}
			System.out.println("roleId---" + roleId);

			
			if ((roleId.equals("6"))) {

				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";

				if (userid.equals("public-prosecutor@ap.gov.in")) {

					ConditionNew += "  and inserted_by like 'PP%' and coalesce(ecourts_case_status,'')!='Closed' ";
				}

				if (userid.equals("addl-pubprosecutor@ap.gov.in")) {

					ConditionNew += "  and inserted_by like 'PP%' and coalesce(ecourts_case_status,'')!='Closed'  ";
				}

			}

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distCode + "' ";
				
			}

			if (roleId.equals("10")) {
				sqlCondition += " and ad.distid='" + distCode + "' and (dm.dept_code='" + deptCode
						+ "' or dm.reporting_dept_code='" + deptCode + "') ";
				//formBean.setDyna("districtId", distCode);
			}

			sql = "select d.dept_code,upper(description) as description,count(distinct ad.ack_no) as acks from ecourts_gpo_ack_dtls ad  inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) " // distinct
					+ "inner join dept_new dm on (d.dept_code=dm.dept_code) " + condition + " "
					+ " where ack_type='NEW'   " + ConditionNew + " " + sqlCondition // and respondent_slno=1
					+ " group by d.dept_code,description " + " order by d.dept_code,description";

			System.out.println("SQL:showDeptWise " + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getDISTWISEACKS(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sqlCondition = "",condition="",sql="", ConditionNew = "";
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
			Date date = new Date(); 

			System.out.println("date--"+formatter.format(date)+"   FROM--"+fromDate +" TO--------"+toDate );

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				System.out.println("FROMMM");
				condition += " and a.inserted_time::date >= to_date('" +fromDate
						+ "','dd-mm-yyyy') ";

			}  
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				condition += " and a.inserted_time::date <= to_date('"+toDate+ "','dd-mm-yyyy') ";

			}  

			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" + fromDate
						+ "','dd-mm-yyyy') ";

			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";

			}
			if (advcteName!= null
					&& !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"
						+advcteName+ "%'";
			}
			if (petitionerName!= null
					&& !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"
						+petitionerName+ "%'";
			}

			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {

				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";
			}
			

			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" + caseTypeId.toString().trim() + "'  ";
			}
			
			if (caseTypeId!= null && caseTypeId.equals("4")) {
				sqlCondition += " and (trim(ad.casetype)='" +caseTypeId.toString().trim() + "' or maincaseno like '%CC%') ";
			}
			
			
			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14")
					|| roleId.equals("17") || roleId.equals("6"))) {
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
						+ "') ";
			}

			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distCode + "' ";
				//formBean.setDyna("districtId", distCode);
			}

			if (roleId.equals("10")) {
				sqlCondition += " and ad.distid='" + distCode + "' ";
				//formBean.setDyna("districtId", distCode);
			}
 
			if ((roleId.equals("6"))) {
 
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select distid,district_name,count(distinct ad.ack_no) as acks from ecourts_gpo_ack_dtls ad " // distinct
					+ " inner join district_mst dm on (ad.distid=dm.district_id) "
					+ " inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) "
					+ "inner join dept_new dmt on (d.dept_code=dmt.dept_code) " + condition + " "
					+ " where ack_type='NEW'  " + ConditionNew + "  " + sqlCondition // and respondent_slno=1
					+ " group by distid,dm.district_name order by district_name";

			System.out.println("SQL:showDistWise" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getshowUserWise(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sqlCondition = "" ,sql="" ;
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
			Date date = new Date(); 

			System.out.println("date--"+formatter.format(date)+"   FROM--"+fromDate +" TO--------"+toDate );

		
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" + fromDate
						+ "','dd-mm-yyyy') ";

			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";

			}
			if (advcteName!= null
					&& !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"
						+advcteName+ "%'";
			}
			if (petitionerName!= null
					&& !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"
						+petitionerName+ "%'";
			}

			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {

				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";
			}
			

			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" + caseTypeId.toString().trim() + "'  ";
			}
			
			if (caseTypeId!= null && caseTypeId.equals("4")) {
				sqlCondition += " and (trim(ad.casetype)='" +caseTypeId.toString().trim() + "' or maincaseno like '%CC%') ";
			}
			
			
			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14")
					|| roleId.equals("17") || roleId.equals("6"))) {
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
						+ "') ";
			}

			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distCode + "' ";
				//formBean.setDyna("districtId", distCode);
			}

			if (roleId.equals("10")) {
				sqlCondition += " and ad.distid='" + distCode + "' ";
				//formBean.setDyna("districtId", distCode);
			}
 
			 

			sql = "select inserted_by,count(distinct ad.ack_no) as acks from ecourts_gpo_ack_dtls ad " // distinct
					+ " inner join district_mst dm on (ad.distid=dm.district_id) "
					+ " inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) "
					+ "inner join dept_new dmt on (d.dept_code=dmt.dept_code)" + " where ack_type='NEW'  "
					+ sqlCondition // and respondent_slno=1
					+ " group by inserted_by";

			System.out.println("SQL:showUserWise" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	
	
	@Override
	public List<Map<String, Object>> getshowCaseWiseAcksAbstract(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId,String inserted_by) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String condition = "", ConditionNew = "";
		String sqlCondition = "" ,sql="" ;
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
			Date date = new Date(); 

			System.out.println("date--"+formatter.format(date)+"   FROM--"+fromDate +" TO--------"+toDate );

		
			if (districtId != null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and a.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and ad.dept_code='" +deptId.toString().trim() + "' ";
			}

			 

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and a.inserted_time::date >= to_date('" +fromDate
						+ "','dd-mm-yyyy') ";
			}
			if (toDate != null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and a.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
			}
			if (advcteName!= null
					&& !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"
						+advcteName+ "%'";
			}
			if (petitionerName != null
					&& !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"
						+petitionerName+ "%'";
			}
			 

			if (serviceType1 != null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {

				sqlCondition += " and ad.servicetpye='" +serviceType1.toString().trim() + "' ";
			}

			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" + caseTypeId.toString().trim() + "'  ";
			}
			
			if (caseTypeId!= null && caseTypeId.equals("4")) {
				sqlCondition += " and (trim(ad.casetype)='" +caseTypeId.toString().trim() + "' or maincaseno like '%CC%') ";
			}
			
			if (inserted_by!= null && !inserted_by.toString().contentEquals("")
					&& !inserted_by.toString().contentEquals("0")) {
				sqlCondition += " and a.inserted_by='" +inserted_by + "' ";
			}
			
			
			if ((roleId.equals("6"))) {
 
				condition = " inner join ecourts_mst_gp_dept_map egm on (egm.dept_code=ad.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "' ";

				if (userid.equals("public-prosecutor@ap.gov.in")) {

					ConditionNew += "  and inserted_by like 'PP%' and coalesce(ecourts_case_status,'')!='Closed' ";
				}

				if (userid.equals("addl-pubprosecutor@ap.gov.in")) {

					ConditionNew += "  and inserted_by like 'PP%' and coalesce(ecourts_case_status,'')!='Closed'  ";
				}
			}

			if (roleId.equals("2")) {
				sqlCondition += " and a.distid='" + distCode + "' ";
				//formBean.setDyna("districtId", distCode);
			}

			if (roleId.equals("10")) {
				System.out.println("10 ---------------comming");
				sqlCondition += " and a.distid='" + distCode + "' and (dmt.dept_code='" + deptCode
						+ "' or dmt.reporting_dept_code='" + deptCode + "')";
				//formBean.setDyna("districtId", distCode);
			}

			if (roleId.equals("13")) {
			//	formBean.setDyna("inserted_by", userid);
			}

			sql = "select  distinct a.ack_no ,file_found, a.slno ,distid,mode_filing , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip, upper(trim(district_name)) as district_name, "
					+ "upper(trim(case_full_name )) as  case_full_name, a.ack_file_path, case when services_id='0' then null else services_id end as services_id,services_flag, "
					+ "to_char(a.inserted_time,'dd-mm-yyyy') as generated_date, (a.ack_no::text) as dept_descs ,a.inserted_time, coalesce(a.hc_ack_no,'-') as hc_ack_no "
					+ ", getack_dept_desc(a.ack_no) as depart_descs, a.petitioner_name"
					+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no) left join scanned_affidavit_new_cases_count sc on (sc.ack_no=a.ack_no or  sc.ack_no=a.hc_ack_no)"
					+ " inner join district_mst dm on (a.distid=dm.district_id) "
					+ " inner join dept_new dmt on (ad.dept_code=dmt.dept_code)  " + condition + " "
					+ " inner join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) "
					+ " where a.delete_status is false and ack_type='NEW'   " + ConditionNew + " " + sqlCondition
					+ " order by district_name desc"; // and respondent_slno=1

			System.out.println("SQL:" + sql); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	 

}
