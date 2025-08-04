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
public class EcourtsLeacyCaseMappedWithNewAckNoReportServiceImpl implements EcourtsLeacyCaseMappedWithNewAckNoReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	 
	@Override
	public List<Map<String, Object>> getDeptWiseMAP(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userid=userPrincipal.getUserId()!= null ? userPrincipal.getUserId().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition = "",ConditionNew="";
		try {
			System.out.println("roleId---" + roleId);
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
				
			}
			if (advcteName!= null && !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"+advcteName+"%'";
			}
			if (petitionerName!= null && !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"+petitionerName+"%'";
			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				
				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" +caseTypeId.toString().trim() + "' ";
			}
			 

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("19")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dm.dept_code='" + deptCode + "' or dm.reporting_dept_code='" + deptCode + "') ";
			}
			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distId+ "' ";
				//formBean.setDyna("districtId", distCode);
			}
			if ((roleId.equals("6"))) {
				
				if(userid.equals("gp-services1@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Services-I' ";
				}
				if(userid.equals("gp-services2@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Service-II'";
				}
				if( userid.equals("gp-services3@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Service-III'";
				}
				if( userid.equals("gp-services4@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Services-IV' ";
				}
				
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select d.dept_code,upper(description) as description,count(distinct  c.cino) as acks from ecourts_gpo_ack_dtls ad "
					+ " inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) "  //distinct
					+ "inner join dept_new dm on (d.dept_code=dm.dept_code)  inner join ecourts_case_data c on ((c.type_name_reg ||'/'||c.reg_no ||'/'||c.reg_year)=maincaseno) " + condition + " "
					+ " where ad.delete_status is false  and ad.ackno_updated='true'  "+ConditionNew+" " + sqlCondition //and respondent_slno=1 
					+ " group by d.dept_code,description " + " order by d.dept_code,description";

			System.out.println("SQL:showDeptWise:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getDeptWiseMAPslno(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userid=userPrincipal.getUserId()!= null ? userPrincipal.getUserId().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition = "",ConditionNew="";
		try {
			System.out.println("roleId---" + roleId);
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
				
			}
			if (advcteName!= null && !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"+advcteName+"%'";
			}
			if (petitionerName!= null && !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"+petitionerName+"%'";
			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				
				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" +caseTypeId.toString().trim() + "' ";
			}
			 

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("19")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dm.dept_code='" + deptCode + "' or dm.reporting_dept_code='" + deptCode + "') ";
			}
			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distId+ "' ";
				//formBean.setDyna("districtId", distCode);
			}
			if ((roleId.equals("6"))) {
				
				if(userid.equals("gp-services1@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Services-I' ";
				}
				if(userid.equals("gp-services2@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Service-II'";
				}
				if( userid.equals("gp-services3@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Service-III'";
				}
				if( userid.equals("gp-services4@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Services-IV' ";
				}
				
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select d.dept_code,upper(description) as description,count(distinct  c.cino) as acks from ecourts_gpo_ack_dtls ad "
					+ " inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) "  //distinct
					+ "inner join dept_new dm on (d.dept_code=dm.dept_code)  inner join ecourts_case_data c on ((c.type_name_reg ||'/'||c.reg_no ||'/'||c.reg_year)=maincaseno) " + condition + " "
					+ " where ad.delete_status is false  and ad.ackno_updated='true'  "+ConditionNew+" " + sqlCondition //and respondent_slno=1 
					+ " group by d.dept_code,description " + " order by d.dept_code,description";

			System.out.println("SQL:showDeptWise Distinct:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getDistWiseMAP(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userid=userPrincipal.getUserId()!= null ? userPrincipal.getUserId().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition = "",ConditionNew="";
		try {
			System.out.println("roleId---" + roleId);
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
				
			}
			if (advcteName!= null && !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"+advcteName+"%'";
			}
			if (petitionerName!= null && !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"+petitionerName+"%'";
			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				
				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" +caseTypeId.toString().trim() + "' ";
			}
			 

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("19")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode + "') ";
			}
			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distId+ "' ";
				//formBean.setDyna("districtId", distCode);
			}
			if ((roleId.equals("6"))) {
				
				if(userid.equals("gp-services1@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Services-I' ";
				}
				if(userid.equals("gp-services2@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Service-II'";
				}
				if( userid.equals("gp-services3@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Service-III'";
				}
				if( userid.equals("gp-services4@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Services-IV' ";
				}
				
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select distid,district_name, count(distinct  c.cino) as acks from ecourts_gpo_ack_dtls ad "  //distinct
					+ " inner join district_mst dm on (ad.distid=dm.district_id) "
					+ " inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) "
					+ "inner join dept_new dmt on (d.dept_code=dmt.dept_code)  inner join ecourts_case_data c on ((c.type_name_reg ||'/'||c.reg_no ||'/'||c.reg_year)=maincaseno) " + condition + " "
							+ "where ad.delete_status is false  and ad.ackno_updated='true'  "+ConditionNew+"  " + sqlCondition //and respondent_slno=1 
					+ " group by distid,dm.district_name order by district_name";

			System.out.println("SQL:showDistWise" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getUserWiseMAP(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userid=userPrincipal.getUserId()!= null ? userPrincipal.getUserId().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition = "",ConditionNew="";
		try {
			System.out.println("roleId---" + roleId);
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and d.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
				
			}
			if (advcteName!= null && !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"+advcteName+"%'";
			}
			if (petitionerName!= null && !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"+petitionerName+"%'";
			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				
				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" +caseTypeId.toString().trim() + "' ";
			}
			 

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("19")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode + "') ";
			}
			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distId+ "' ";
				//formBean.setDyna("districtId", distCode);
			}
			if ((roleId.equals("6"))) {
				
				if(userid.equals("gp-services1@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Services-I' ";
				}
				if(userid.equals("gp-services2@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Service-II'";
				}
				if( userid.equals("gp-services3@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Service-III'";
				}
				if( userid.equals("gp-services4@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Services-IV' ";
				}
				
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select inserted_by,count(distinct c.cino) as acks from ecourts_gpo_ack_dtls ad "  //distinct
					+ " inner join district_mst dm on (ad.distid=dm.district_id) "
					+ " inner join ecourts_gpo_ack_depts d on (ad.ack_no=d.ack_no) "
					+ "inner join dept_new dmt on (d.dept_code=dmt.dept_code)  inner join ecourts_case_data c on ((c.type_name_reg ||'/'||c.reg_no ||'/'||c.reg_year)=maincaseno) where ad.delete_status is false  and ad.ackno_updated='true'  " + sqlCondition //and respondent_slno=1 
					+ " group by inserted_by";

			System.out.println("SQL:showUserWise" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getCaseWiseAcksAbstractMAPslno(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId,String inserted_by) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userid=userPrincipal.getUserId()!= null ? userPrincipal.getUserId().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition = "",ConditionNew="";
		try {
			System.out.println("roleId---" + roleId);
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and a.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and ad.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and a.inserted_time::date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and a.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
				
			}
			if (advcteName!= null && !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"+advcteName+"%'";
			}
			if (petitionerName!= null && !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"+petitionerName+"%'";
			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				
				sqlCondition += " and a.services_flag='" +serviceType1.toString().trim() + "' ";

			}
			
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and a.casetype='" +caseTypeId.toString().trim() + "' ";
			}
			 
			if (inserted_by!= null
					&& !inserted_by.toString().contentEquals("")
					&& !inserted_by.toString().contentEquals("0")) {
				sqlCondition += " and a.inserted_by='" +inserted_by+ "' ";
			}

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("19")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode + "') ";
			}
			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distId+ "' ";
				//formBean.setDyna("districtId", distCode);
			}
			if ((roleId.equals("6"))) {
				
				if(userid.equals("gp-services1@ap.gov.in") )    {

					ConditionNew=" and a.inserted_by='Services-I' ";
				}
				if(userid.equals("gp-services2@ap.gov.in") )    {

					ConditionNew=" and a.inserted_by='Service-II'";
				}
				if( userid.equals("gp-services3@ap.gov.in"))    {

					ConditionNew=" and a.inserted_by='Service-III'";
				}
				if( userid.equals("gp-services4@ap.gov.in"))    {

					ConditionNew=" and a.inserted_by='Services-IV' ";
				}
				
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select distinct cino,STRING_AGG(distinct a.ack_no,';') ack_no_list,file_found, maincaseno , upper(trim(dmt.description)) as description  "
					+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no) left join scanned_affidavit_new_cases_count sc on (sc.ack_no=a.ack_no or  sc.ack_no=a.hc_ack_no)"
					+ " inner join dept_new dmt on (ad.dept_code=dmt.dept_code)  " + condition + " "
					+ " inner join ecourts_case_data c on ((c.type_name_reg ||'/'||c.reg_no ||'/'||c.reg_year)=maincaseno) "
					+ " where a.delete_status is false and a.ackno_updated='true'    "+ConditionNew+" " + sqlCondition //and respondent_slno=1 
					+ " group by cino ,file_found,maincaseno ,description";

			System.out.println("Distinct SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getShowCaseWiseAcksAbstractMAP(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId,String inserted_by){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userid=userPrincipal.getUserId()!= null ? userPrincipal.getUserId().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition = "",ConditionNew="";
		try {
			System.out.println("roleId---" + roleId);
			if (districtId!= null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				sqlCondition += " and ad.distid='" +districtId.toString().trim() + "' ";
			}

			if (deptId!= null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				sqlCondition += " and ad.dept_code='" +deptId.toString().trim() + "' ";
			}

			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and ad.inserted_time::date <= to_date('" +toDate
						+ "','dd-mm-yyyy') ";
				
			}
			if (advcteName!= null && !advcteName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"+advcteName+"%'";
			}
			if (petitionerName!= null && !petitionerName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(petitioner_name,' ',''),'.','') ilike  '%"+petitionerName+"%'";
			}
			if (serviceType1!= null
					&& !serviceType1.toString().contentEquals("")
					&& !serviceType1.toString().contentEquals("0")) {
				
				sqlCondition += " and d.servicetpye='" +serviceType1.toString().trim() + "' ";

			}
			
			if (caseTypeId!= null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and ad.casetype='" +caseTypeId.toString().trim() + "' ";
			}
			 
			if (inserted_by!= null
					&& !inserted_by.toString().contentEquals("")
					&& !inserted_by.toString().contentEquals("0")) {
				sqlCondition += " and a.inserted_by='" +inserted_by+ "' ";
			}
			

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("14") || roleId.equals("19")
					|| roleId.equals("6") || roleId.equals("17"))) {
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode + "') ";
			}
			

			if (roleId.equals("2")) {
				sqlCondition += " and ad.distid='" + distId+ "' ";
				//formBean.setDyna("districtId", distCode);
			}
			if ((roleId.equals("6"))) {
				
				if(userid.equals("gp-services1@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Services-I' ";
				}
				if(userid.equals("gp-services2@ap.gov.in") )    {

					ConditionNew=" and d.servicetpye='Service-II'";
				}
				if( userid.equals("gp-services3@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Service-III'";
				}
				if( userid.equals("gp-services4@ap.gov.in"))    {

					ConditionNew=" and d.servicetpye='Services-IV' ";
				}
				
				condition = " left join ecourts_mst_gp_dept_map egm on (egm.dept_code=d.dept_code) ";
				sqlCondition += " and egm.gp_id='" + userid + "'";
			}

			sql = "select distinct cino,STRING_AGG(distinct a.ack_no,';') ack_no_list,file_found, maincaseno ,   upper(trim(dm.district_name)) as districtname  "
					+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no) inner join district_mst dm on (a.distid=dm.district_id) left join scanned_affidavit_new_cases_count sc on (sc.ack_no=a.ack_no or  sc.ack_no=a.hc_ack_no) "
				    + " inner join dept_new dmt on (ad.dept_code=dmt.dept_code)  " + condition + " "
					+ " inner join ecourts_case_data c on ((c.type_name_reg ||'/'||c.reg_no ||'/'||c.reg_year)=maincaseno)  "
					+ " where a.delete_status is false  and a.ackno_updated='true'   "+ConditionNew+" " + sqlCondition //and respondent_slno=1 
					+ " group by cino ,file_found,maincaseno ,districtname";

			System.out.println("District Wise SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	 
}
