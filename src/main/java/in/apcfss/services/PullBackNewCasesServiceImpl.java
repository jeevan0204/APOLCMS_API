package in.apcfss.services;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.PullBackNewCasesRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PullBackNewCasesServiceImpl implements PullBackNewCasesService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	PullBackNewCasesRepo pullBackNewRepo;
	@Override
	public List<Map<String, Object>> getPullBackNewCasesList(UserDetailsImpl userPrincipal) {

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String sqlCondition="", sql="" ;
		try {

			if (roleId.equals("2")) {

				sqlCondition += " and f.dist_id='" + distId + "'  ";
			}
			if (roleId.equals("4")) {
				sqlCondition += " and   f.dept_code='" + deptCode
						+ "' and  case_status = '5' and  (f.dist_id='0' or f.dist_id is null) ";
			}

			if (roleId.equals("5")) {

				sqlCondition += " and   f.dept_code='" + deptCode
						+ "' and  case_status = '9' and  (f.dist_id='0' or f.dist_id is null) ";
			}
			if (roleId.equals("10")) {

				sqlCondition += " and  f.dist_id='" + distId + "' and  f.dept_code='" + deptCode
						+ "' and  case_status = '10' ";
			}

			sql = " select a.ack_no,inserted_time::Date as inserted_time,* from ecourts_gpo_ack_dtls a inner join ecourts_gpo_ack_depts f on (a.ack_no=f.ack_no) "
					+ " where assigned_to is not null and  f.assigned=true  " + sqlCondition
					+ " order by f.assigned_to";

			System.out.println("ecourts SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public String getSendCaseBackNewCases(UserDetailsImpl userPrincipal, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request) {

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid=userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String msg="" ,cIno="";
		String[] ids_split = null;
		int a=0;
		try {
			cIno =  abstractReqBody.getCino();
			
			System.out.println("cIno--"+cIno);
			if (cIno != null && !cIno.equals("")) {

				String ids = cIno;
				ids_split = ids.split("@");
				
				String ackNo=ids_split[0];
				int respondentId=Integer.parseInt(ids_split[1]);
				
				System.out.println("ids---0--" + ackNo);
				System.out.println("ids---1--" + respondentId);
			 
				int backStatus = 0;

				if (roleId.equals("4")) {// MLO
					backStatus = 2;
				} else if (roleId.equals("5")) {// NO
					backStatus = 4;
				} else if (roleId.equals("10")) {// NO-DIST
					backStatus = 8;
					 
				}
				 
				if (roleId.equals("10")) {

					a =pullBackNewRepo.update_ack_deptsRole10(backStatus,deptCode,distCode,ackNo,respondentId);
					System.out.println("a--"+a);
				} else if (roleId.equals("2")) {

					a =pullBackNewRepo.update_ack_deptsRole2(backStatus,distCode,ackNo,respondentId);
					System.out.println("a--"+a);
				} else {

					a =pullBackNewRepo.update_ack_deptsRoleELSE(backStatus,deptCode,ackNo,respondentId);
					System.out.println("a--"+a);
				}

				a += pullBackNewRepo.insert_ecourts_case_emp_assigned_dtls_log(ackNo);
				System.out.println("a--"+a);
 
				a += pullBackNewRepo.insert_ecourts_case_activities(ackNo,userid,request.getRemoteAddr(),userid);
				System.out.println("a--"+a);
 
				a += pullBackNewRepo.delete_ecourts_case_emp_assigned_dtls(ackNo);
				System.out.println("a--"+a);

				if (a > 0) {
					msg="Case reverted back to MLO/No";
				}else {
					msg="Error in Case revertal";
				}

			} else {
				request.setAttribute("errorMsg", "Invalid CIN No.");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return msg;
	}

}
