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
public class DistrictWiseInterimOrderImplReportServiceImpl implements DistrictWiseInterimOrderImplReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	 
	@Override
	public List<Map<String, Object>> getInterimOrdersImplReport(Authentication authentication, String fromDate, String toDate) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "";
		try {

		
			if (fromDate!= null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and order_date >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and order_date <= to_date('" +toDate+ "','dd-mm-yyyy') ";
			}
			
			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9") || roleId.equals("10"))
				sqlCondition += " and ( a.dept_code='"+ deptCode+ "')";

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" +distId+ "' ";
				//userPrincipal.setDistrictId(distId);
			}

			sql=" select dm.district_id, dm.district_name, "
					+ " coalesce(d.casescount,'0') casescount, "
					+ " coalesce(d.order_implemented,'0') order_implemented, "
					+ " coalesce(d.appeal_filed,'0') appeal_filed, "
					+ " coalesce(d.dismissed_copy,'0') dismissed_copy,  "
					
					+ " coalesce(casescount-(order_implemented + appeal_filed+dismissed_copy),'0') as pending,   "
					+ " "
					+ " case when coalesce(d.casescount,'0') > 0 then round((((coalesce(order_implemented,'0')::int4 + coalesce(appeal_filed,'0')::int4 + coalesce(dismissed_copy,'0')::int4) * 100) / coalesce(d.casescount,'0')) , 0) else 0 end as actoin_taken_percent "
					
					+ " from district_mst dm "
					+ " left join ( select dist_id,count( a.cino) as casescount,   "
					+ " sum(case when final_order_status='interim' and b.sr_no='1' then 1 else 0 end) as order_implemented ,  "
					+ " sum(case when final_order_status='appeal_interim' and b.sr_no='1' then 1 else 0 end) as appeal_filed ,"
					+ " sum(case when final_order_status='dismissed_interim' and b.sr_no='1' then 1 else 0 end) as dismissed_copy from  ecourts_case_data a  "
					+ " inner join ecourts_case_interimorder b on (a.cino=b.cino)  LEFT join dept_new dn on (a.dept_code=dn.dept_code) "
					+ " inner join ecourts_olcms_case_details ocd on (a.cino=ocd.cino)   "
					+ " where   dist_id!='0' and dist_id is not null AND order_document_path is not null and POSITION('RECORD_NOT_FOUND' in order_document_path)= 0 and POSITION('INVALID_TOKEN' in order_document_path)= 0 and 1=1  " +sqlCondition 
					+ " group by dist_id ) d on (dist_id=dm.district_id) order by casescount desc ";
			
			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getCasesListInterim(Authentication authentication, String caseStatus, String distid,  String distName) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int loginDistId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "";
		try {
			String value="";
		
			if(!caseStatus.equals("")) {
				if(caseStatus.equals("CLOSED")){
						sqlCondition+= " and coalesce(a.ecourts_case_status,'')='Closed' ";
					}
				
				if(caseStatus.equals("INTERIMORDER")) {
					value=",eocd.judgement_order,eocd.action_taken_order,eocd.remarks,eocd.cordered_impl_date";
					sqlCondition+=" and (final_order_status='interim') ";
				}
				if(caseStatus.equals("APPEALFILEDINTERIMORDER")) {
					value= " , eocd.appeal_filed, eocd.appeal_filed_copy,eocd.appeal_filed_date ";
					sqlCondition+=" and  ( final_order_status='appeal_interim') ";
				}
				if(caseStatus.equals("DISMISSEDINTERIMORDER")) {
					value= " ,eocd.dismissed_copy ";
					sqlCondition+=" and ( final_order_status='dismissed_interim') ";
				}
				if(caseStatus.equals("PENDINGINTERIMORDER")) {
					sqlCondition+=" and  case_status=7 ";
				}
				
			}

			if(roleId.equals("2")){
				sqlCondition+=" and a.dist_id='"+loginDistId+"'";
			}else if(roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9"))
			{
				sqlCondition += " and (a.dept_code='" + deptCode + "') " ;
			}
			
			 if(distid != null
						&& !CommonModels.checkStringObject(distid).contentEquals("")) {
					sqlCondition+=" and a.dist_id='"+distid+"'";
			   }
				
				if (deptCode != null && !deptCode.toString().contentEquals("")
						&& !deptCode.toString().contentEquals("0")) {
					sqlCondition += " and a.dept_code='" + deptCode + "' ";
				}
				
				System.out.println("---"+loginDistId);
			
				sql = "select a.*, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1,b.is_interim_exists, b.orderpaths, prayer,"
						+ " ra.address "+value+" from ecourts_case_data a  "
						+ " left join nic_prayer_data np on (a.cino=np.cino)"
						+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1)"
						+ "  inner join ecourts_olcms_case_details eocd on (a.cino=eocd.cino)  "
						+ " inner join ( select cino,is_interim_exists, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths "
						+ " from  (select cino,is_interim_exists, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null "
						+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0 and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) c group by cino,is_interim_exists ) b on (a.cino=b.cino)"
						+ " inner join dept_new d on (a.dept_code=d.dept_code) where d.display = true   "+sqlCondition+"    ";
				
				System.out.println("ecourts SQL:" + sql);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getCCCasesReportInterim(Authentication authentication, String fromDate, String toDate) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "";
		try {
		
			if (fromDate != null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and dt_regis >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and dt_regis <= to_date('" +toDate+ "','dd-mm-yyyy') ";
			}
			
			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9") || roleId.equals("10"))
				sqlCondition += " and (reporting_dept_code='" +deptCode+ "' or a.dept_code='"
						+deptCode+ "')";

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" +distId+ "' ";
				//userPrincipal.setDistrictId(distId);
			}

			sql= "select  district_id,dm.district_name,coalesce(d.casescount,'0') as casescount,coalesce(d.counterscount,'0') as counterscount  "
					+ "  from district_mst dm left join "
					+ "( select dist_id,count(distinct a.cino) as casescount,   "
					+ " sum(case when length(counter_filed_document)> 10 then 1 else 0 end) as counterscount  "
					+ " from ecourts_case_interimorder eci inner join ecourts_case_data a  on (a.cino=eci.cino)  "
					+ " inner join ecourts_olcms_case_details ocd on (a.cino=ocd.cino) "
					+ " inner join dept_new dn on (a.dept_code=dn.dept_code)  "
					+ " where type_name_reg='CC' " +sqlCondition
					+ " group by a.dist_id  ) d on  (d.dist_id=dm.district_id) order by casescount desc" ;

			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> getNewCasesReportInterim(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "";
		try {
 
			
			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9") || roleId.equals("10"))
				sqlCondition += " and (reporting_dept_code='" +deptCode+ "' or a.dept_code='"
						+deptCode+ "')";

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" +distId+ "' ";
				//userPrincipal.setDistrictId(distId);
			}
			
			sql= "select district_id, district_name,coalesce(d.casescount,'0') casescount,coalesce(d.counterscount,'0') counterscount  "
					+ " from district_mst dm left join  "
					+ " ( select dist_id,count(distinct a.ack_no) as casescount,   "
					+ "  sum(case when length(counter_filed_document)> 10 then 1 else 0 end) as counterscount     "
					+ " from  ecourts_gpo_ack_depts a inner join ecourts_gpo_ack_dtls b on (a.ack_no=b.ack_no)    "
					+ " inner join ecourts_olcms_case_details ocd on (a.ack_no=ocd.cino) inner join dept_new dn on (a.dept_code=dn.dept_code) "
					+ " where  ack_type='NEW' and a.respondent_slno='1'   "+sqlCondition //inserted_time::date >= current_date - 30 
					+ "group by dist_id ) d on d.dist_id=dm.district_id order by casescount desc  ";

			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getLegacyCasesReportInterim(Authentication authentication, String fromDate, String toDate){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String sql = null, sqlCondition = "";
		try {

		
			if (fromDate != null && !fromDate.toString().contentEquals("")) {
				sqlCondition += " and dt_regis >= to_date('" +fromDate+ "','dd-mm-yyyy') ";
			}
			if (toDate!= null && !toDate.toString().contentEquals("")) {
				sqlCondition += " and dt_regis <= to_date('" +toDate+ "','dd-mm-yyyy') ";
			}
			
			if (roleId.equals("3") || roleId.equals("4") || roleId.equals("5") || roleId.equals("9") || roleId.equals("10"))
				sqlCondition += " and (reporting_dept_code='" +deptCode+ "' or a.dept_code='"
						+deptCode+ "')";

			if (roleId.equals("2") || roleId.equals("10")) {
				sqlCondition += " and a.dist_id='" +distId+ "' ";
				//userPrincipal.setDistrictId(distId);
			}

			sql ="select district_id, district_name,coalesce(d.casescount,'0') casescount,coalesce(d.counterscount,'0') counterscount "
					+ " from district_mst dm left join "
					+ " (select dist_id,count(distinct a.cino) as casescount,   "
					+ " sum(case when length(counter_filed_document)> 10 then 1 else 0 end) as counterscount      "
					+ " from  ecourts_case_data a  inner join dept_new dn on (a.dept_code=dn.dept_code)  "
					+ " inner join ecourts_olcms_case_details ocd on (a.cino=ocd.cino) "
					+ " inner join ecourts_case_interimorder eci  on (ocd.cino=eci.cino)  "
					+ " where 1=1  "+sqlCondition
					+ " group by dist_id ) d on d.dist_id=dm.district_id order by casescount desc ";

			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
}
