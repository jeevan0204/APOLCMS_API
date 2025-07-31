package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
 
import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.entities.UserDetailsImpl;

@Service
public class SectionOfficerWiseCaseProcessingReportServiceImpl implements SectionOfficerWiseCaseProcessingReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	CommonMethodsController commomMethodCntrl;

	@Override
	public List<Map<String, Object>> getCasesListSectionOfficer(Authentication authentication, String fromDate, String toDate,
			String caseTypeId,String section_code){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition="" ;

		String dist_table="";
		dist_table = commomMethodCntrl.getTableName(String.valueOf(distId));
		try {

			if (roleId != null && roleId.equals("4")) { // MLO
				condition = " and b.dept_code='" + deptCode + "' "; //and b.case_status=2
			} else if (roleId != null && roleId.equals("5")) { // NO
				condition = " and  a.inserted_by = '" + userId +"'";
			} else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				condition = " and  b.assigned_to='" + userId
						+ "'";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				condition = " and  b.assigned_to='" + userId
						+ "'";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				condition = " and  b.assigned_to='" + userId + "'";
			}

			else if (roleId != null && roleId.equals("3")) { // SECT DEPT
				condition = " and b.dept_code='" + deptCode + "' "; //and b.case_status=1
			} else if (roleId != null && roleId.equals("9")) { // HOD
				condition = " and b.dept_code='" + deptCode + "' "; //and b.case_status=3
			} else if (roleId != null && roleId.equals("2")) { // DC
				condition = " and b.case_status=7 and b.dist_id='" + distId + "'";
			} else if (roleId != null && roleId.equals("10")) { // DC-NO
				condition = " and b.dept_code='" + deptCode + "'  and b.dist_id='" + distId + "'";  
			}


			if (fromDate != null
					&& !fromDate.toString().contentEquals("")) {
				sqlCondition += " and eocd.last_updated_on >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
			}
			if (toDate!= null
					&& !toDate.toString().contentEquals("")) {
				sqlCondition += " and eocd.last_updated_on <= to_date('" + toDate+ "','dd-mm-yyyy') ";
			}

			if(section_code!=null && section_code.equals("N"))
			{
				System.out.println("###########");
				sql="select fullname_en,global_org_name,designation_name_en,mobile1,b.assigned_to as email,employee_id as emp_id ,count(distinct b.ack_no )  as total ,"
						+ " sum(case when b.ecourts_case_status='Pending' and counter_filed_document is not null and length(counter_filed_document)>10  then 1 else 0 end) as counter_uploaded, "
						+ " sum(case when b.ecourts_case_status='Pending' and pwr_uploaded_copy is not null and length(pwr_uploaded_copy)>10  then 1 else 0 end) as pwrcounter_uploaded, "
						+ " sum(case when b.ecourts_case_status='Closed' then 1 else 0 end) as closed_cases,"
						+ " sum(case when b.case_status=96 and coalesce(b.ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi,"
						+ " sum(case when b.case_status=95 and coalesce(b.ecourts_case_status,'')!='Closed' then 1 else 0 end) as central,"
						+ " sum(case when b.case_status=94 and coalesce(b.ecourts_case_status,'')!='Closed' then 1 else 0 end) as incometax from section_officer_details a "
						+ " inner join  ecourts_gpo_ack_depts b on (a.emailid=b.assigned_to) inner join ecourts_gpo_ack_dtls egad on (b.ack_no=egad.ack_no) "
						+ " left join ecourts_olcms_case_details eocd on (eocd.cino=egad.ack_no ) left join ecourts_dept_instructions edi on (edi.cino=b.ack_no and edi.insert_by =a.emailid )"
						+ " inner join "+dist_table+" nd on (a.emailid =nd.email)  "   
						+ " where 1=1  "+condition+"  "+sqlCondition+"   group by 1,2,3,4,5,6 ";
			}
			else {

				if (caseTypeId!= null
						&& !caseTypeId.toString().contentEquals("")
						&& !caseTypeId.toString().contentEquals("0")) {
					sqlCondition += " and trim(b.type_name_reg)='" +caseTypeId.toString().trim()
							+ "' ";
				}  

				sql="select fullname_en,global_org_name,designation_name_en,mobile1,email,employeeid as emp_id,count(distinct b.cino )  as total ,"
						+ " sum(case when b.ecourts_case_status='Pending' and counter_filed_document is not null and length(counter_filed_document)>10  then 1 else 0 end) as counter_uploaded, "
						+ " sum(case when b.ecourts_case_status='Pending' and pwr_uploaded_copy is not null and length(pwr_uploaded_copy)>10  then 1 else 0 end) as pwrcounter_uploaded, "
						+ " sum(case when b.ecourts_case_status='Closed' then 1 else 0 end) as closed_cases,"
						+ " sum(case when b.case_status=96 and coalesce(b.ecourts_case_status,'')!='Closed' then 1 else 0 end) as goi,"
						+ " sum(case when b.case_status=95 and coalesce(b.ecourts_case_status,'')!='Closed' then 1 else 0 end) as central,"
						+ "	sum(case when b.case_status=94 and coalesce(b.ecourts_case_status,'')!='Closed' then 1 else 0 end) as incometax  "
						+ " from section_officer_details a inner join ecourts_case_data b on (a.emailid =b.assigned_to) "
						+ " left join ecourts_olcms_case_details eocd on (eocd.cino=b.cino) left join ecourts_dept_instructions edi on (edi.cino=b.cino and edi.insert_by =a.emailid )"
						+ " inner join "+dist_table+" nd on (a.emailid =nd.email) "  
						+ " where  1=1  "+condition+"  "+sqlCondition+"  group by fullname_en,designation_name_en,global_org_name,mobile1,email,employeeid ";
			}



			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getAllCasesDetails(Authentication authentication , String section_code, String emailId, String ids, String date){
		String sql = null, sqlCondition = "" ,part="" ;

		try {

			if(ids.equals("PWR")) {
				part=" and a.ecourts_case_status='Pending' and eocd.pwr_uploaded_copy is not null and length(pwr_uploaded_copy)>10 ";
			}else if(ids.equals("COUNTER")) {
				part=" and a.ecourts_case_status='Pending' and counter_filed_document is not null and length(counter_filed_document)>10  ";
			}else if(ids.equals("CLOSE")) {
				part=" and a.ecourts_case_status='Closed'";
			}else if(ids.equals("GOI")) {
				part=" and a.case_status=96 ";
			}else if(ids.equals("CENTRAL")) {
				part=" and a.case_status=95 ";
			}else if(ids.equals("INCOMETAX")) {
				part=" and a.case_status=94 ";
			}else if(ids.equals("TOT")){
				part=" ";
			}

			if(date.equals("DATE") ) {
				/*
				 * if (fromDate!= null && !fromDate.toString().contentEquals("")) { sqlCondition
				 * += " and eocd.last_updated_on >= to_date('" +fromDate+ "','dd-mm-yyyy') "; }
				 * if (toDate!= null && !toDate.toString().contentEquals("")) { sqlCondition +=
				 * " and eocd.last_updated_on <= to_date('" +toDate+ "','dd-mm-yyyy') "; }
				 */

			}


			if(section_code!=null && section_code.equals("N"))
			{
				sql = "select a.ack_no,servicetpye,advocatename,advocateccno,casetype,maincaseno,petitioner_name,egd.inserted_time from ecourts_gpo_ack_depts  "
						+ " a inner join ecourts_gpo_ack_dtls egd on (a.ack_no=egd.ack_no)  left join ecourts_olcms_case_details eocd on (eocd.cino=egd.ack_no) "
						+ " left join ecourts_dept_instructions edi on (edi.cino=egd.ack_no and edi.insert_by =a.assigned_to )"
						+ "where a.assigned_to='" + emailId+ "' "+part+" "+sqlCondition+" ";
			}
			else {

				/*
				 * if (caseTypeId!= null && !caseTypeId.toString().contentEquals("") &&
				 * !caseTypeId.toString().contentEquals("0")) { sqlCondition +=
				 * " and trim(a.type_name_reg)='" +caseTypeId.toString().trim() + "' "; }
				 */

				sql = "select a.*, "
						+ " coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths, prayer, ra.address from ecourts_case_data a "
						+ " left join nic_prayer_data np on (a.cino=np.cino)"
						+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1) left join ecourts_olcms_case_details eocd on (eocd.cino=a.cino) "
						+ " left join ecourts_dept_instructions edi on (edi.cino=a.cino and edi.insert_by =a.assigned_to ) "
						+ " left join"
						+ " ("
						+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
						+ " from "
						+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
						+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1" + " union"
						+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
						+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
						+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
						+ " on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code)  where assigned_to='" + emailId+ "'   "+part+ "  "+sqlCondition+" ";

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
