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
public class SectionOfficerWiseInstructionsReportServiceImpl implements SectionOfficerWiseInstructionsReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getSectionOfficerWiseInstructionsReport(Authentication authentication, String fromDate,
			String toDate, String section_code){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "",condition="" ;

		try {

			if (roleId != null && roleId.equals("4")) { // MLO
				condition = " and b.dept_code='" + deptCode + "' "; //and b.case_status=2
			} else if (roleId != null && roleId.equals("5")) { // NO
				condition = " and  a.inserted_by = '" +userId+"'";
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
				condition = " and b.dept_code='" + deptCode + "' ";  //and b.case_status=3
			} else if (roleId != null && roleId.equals("2")) { // DC
				condition = " and b.case_status=7 and b.dist_id='" + distId + "'";
			} else if (roleId != null && roleId.equals("10")) { // DC-NO
				condition = " and b.dept_code='" + deptCode + "'  and b.dist_id='" + distId + "'";  
			}
			
			if (section_code.equals("N")) {
				
				if (fromDate!= null && !fromDate.toString().contentEquals("")) {
					sqlCondition += " and edi.insert_time >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				}

				if (toDate!= null && !toDate.toString().contentEquals("")) {
					sqlCondition += " and edi.insert_time <= to_date('" +toDate+ "','dd-mm-yyyy') ";
				}
				
			}else {


				if (fromDate!= null && !fromDate.toString().contentEquals("")) {
					sqlCondition += " and b.date_of_filing >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
				}

				if (toDate!= null && !toDate.toString().contentEquals("")) {
					sqlCondition += " and b.date_of_filing <= to_date('" +toDate+ "','dd-mm-yyyy') ";
				}

			}

			if(section_code!=null && section_code.equals("N"))
			{
				sql=" select fullname_en,global_org_name,designation_name_en,mobile1,b.assigned_to as email,employee_id as emp_id , "
						+ " count(distinct b.ack_no )  as total , "
						+ " sum(case when (edi.instructions is not null and edi.instructions!='') then 1 else 0 end) as instructions, "
						+ " (count(*)-(sum(case when edi.instructions is not null then 1 else 0 end) )) balance "
						+ " from section_officer_details a  inner join  ecourts_gpo_ack_depts b on (a.emailid=b.assigned_to) "
						+ " inner join ecourts_gpo_ack_dtls egad on (b.ack_no=egad.ack_no) "
						+ " inner join nic_data nd on (a.emailid =nd.email) "
						+ " left join ecourts_dept_instructions edi on (edi.cino=b.ack_no and edi.insert_by =a.emailid ) "
						+ " where  1=1 "+condition+"  "+sqlCondition+"  group by 1,2,3,4,5,6 ";
			}
			else {

				sql=" select fullname_en,global_org_name,designation_name_en,mobile1,email,employeeid as emp_id, "
						+ " count(distinct b.cino )  as total ,"
						+ " sum(case when (edi.instructions is not null and edi.instructions!='') then 1 else 0 end) as instructions,"
						+ " (count(distinct b.cino)-(sum(case when edi.instructions is not null then 1 else 0 end) )) balance  "
						+ " from section_officer_details a inner join ecourts_case_data b on (a.emailid =b.assigned_to)  "
						+ " inner join nic_data nd on (a.emailid =nd.email) "
						+ " left join ecourts_dept_instructions edi on (edi.cino=b.cino and edi.insert_by =a.emailid ) "
						+ " where 1=1  "+sqlCondition+"  "+condition+"  "
						+ " group by fullname_en,designation_name_en,global_org_name,mobile1,email,employeeid  ";
			}



			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getAllCasesDetailsInstReport(Authentication authentication, String section_code, String emailId, String ids ){
		String sql = null, sqlCondition = "" ,part="" ;

		try {

			if(ids.equals("INTS")){
				part=" and edi.instructions is not null";
			} if(ids.equals("BALANCE")){
				part=" and edi.instructions is null";
			} else if(ids.equals("TOT")){
				part=" ";
			}

			if (section_code.equals("N")) {
				
				/*
				 * if (fromDate!= null && !fromDate.toString().contentEquals("")) { sqlCondition
				 * += " and edi.insert_time >= to_date('" +fromDate+ "','dd-mm-yyyy') "; }
				 * 
				 * if (toDate!= null && !toDate.toString().contentEquals("")) { sqlCondition +=
				 * " and edi.insert_time <= to_date('" +toDate + "','dd-mm-yyyy') "; }
				 */
				
			}else {

				/*
				 * if (fromDate!= null && !fromDate.toString().contentEquals("")) { sqlCondition
				 * += " and a.date_of_filing >= to_date('" +fromDate+ "','dd-mm-yyyy') "; }
				 * 
				 * if (toDate!= null && !toDate.toString().contentEquals("")) { sqlCondition +=
				 * " and a.date_of_filing <= to_date('" +toDate + "','dd-mm-yyyy') "; }
				 */

			}

			if(section_code!=null && section_code.equals("N"))
			{
				sql = "select a.ack_no,servicetpye,advocatename,advocateccno,casetype,maincaseno,petitioner_name,egd.inserted_time from ecourts_gpo_ack_depts  "
						+ " a inner join ecourts_gpo_ack_dtls egd on (a.ack_no=egd.ack_no)   "
						+ " left join ecourts_dept_instructions edi on (edi.cino=egd.ack_no and edi.insert_by =a.assigned_to )"
						+ "where a.assigned_to='" + emailId+ "' "+part+" "+sqlCondition+" ";
			}
			else {

				sql = "select a.*, "
						+ "coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths, prayer, ra.address from ecourts_case_data a "
						+ " left join nic_prayer_data np on (a.cino=np.cino)"
						+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1)   "
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

}
