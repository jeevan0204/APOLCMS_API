package in.apcfss.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.LegacyAssignmentRepo;
import in.apcfss.repositories.PullBackLegacyCasesRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PullBackLegacyCasesServiceImpl implements PullBackLegacyCasesService {

	@Autowired
	HttpServletRequest request;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	String entryIp() {
		return request.getRemoteAddr();
	}

	@Autowired
	PullBackLegacyCasesRepo pullBackrepo;

	@Override
	public List<Map<String, Object>> getPullBackLegacyCasesList(UserDetailsImpl userPrincipal ) {
		String  sqlCondition = "", sql = "" ;

		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";

		try {
			if (roleId.equals("4")) {
				sqlCondition += "    a.dept_code='" + userPrincipal.getDeptCode()
				+ "' and  case_status = '5' and (dist_id='0' or dist_id is null) ";

			}

			if (roleId.equals("5")) {

				sqlCondition += "    a.dept_code='" + userPrincipal.getDeptCode()
				+ "' and  case_status = '9' and (dist_id='0' or dist_id is null) ";

			}
			if (roleId.equals("10")) {

				Integer  distId = userPrincipal.getDistId();
				sqlCondition += "   a.dist_id='" + distId + "' and  a.dept_code='" + userPrincipal.getDeptCode()
				+ "' and  case_status = '10' ";
			}


			sql = "select date_of_filing,cino ,  dt_regis ,dist_name, type_name_reg , reg_no , reg_year,assigned_to from ecourts_case_data a "
					+ " where assigned_to is not null and assigned=true and " + sqlCondition
					+ " order by  assigned_to  ";

			System.out.println("ecourts SQL  :" + sql);

		}  catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public ResponseEntity<Map<String, Object>> getsendCaseBackLegacyCases(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody ) {
		String  sqlCondition = "", sql = ""  ;

		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ?  userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ?  userPrincipal.getDeptCode().toString() : "";
		String distCode = userPrincipal.getDistId() != null ?  userPrincipal.getDistId().toString() : "";

		try {
			int a = 0;
			// cIno = CommonModels.checkStringObject(formBean.getDyna("cINO"));

			String cIno = abstractReqBody.getCino();

			System.out.println("cIno-----------" + cIno);
			if (cIno != null && !cIno.equals("")) {

				int backStatus = 0;

				if (roleId.equals("4")) {// MLO
					backStatus = 2;
				} else if (roleId.equals("5")) {// NO
					backStatus = 4;
				} else if (roleId.equals("10")) {// NO-DIST
					backStatus = 8;
				} else if (roleId.equals("2")) {// DIST
					backStatus = 7;
				}
				if (roleId.equals("10")) {
					a = pullBackrepo.updateRoll10(backStatus,deptCode, distCode, cIno);

					System.out.println("UPDATE SQL10--:" + sql);
				} else if (roleId.equals("2")) {

					a = pullBackrepo.updateRoll2(backStatus,distCode, cIno);
					System.out.println("UPDATE SQL2--:" + sql);
				} else {
					a = pullBackrepo.updateRollElse(backStatus,deptCode, cIno);

					System.out.println("UPDATE SQL else --:" + sql);

				}
				a = pullBackrepo.insertBack_case_emp_assigned(cIno);

				System.out.println("UPDATE SQL insert:" + sql);
				a = pullBackrepo.insertBack_case_activities(cIno, userId, InetAddress.getByName(entryIp()),userId);

				System.out.println("INSERT ACTIVITIES SQL:" + sql);

				a = pullBackrepo.deletecase_emp_assigned(cIno);
				System.out.println("DELETE SQL:" + sql);

				if (a > 0) {
					return CommonQueryAPIUtils.manualResponse("01",
							"Case reverted back to MLO/No");
					//map.put("successMsg", "Case reverted back to MLO/No");
				}

			} else {
				//request.setAttribute("errorMsg", "Invalid CIN No.");
				return CommonQueryAPIUtils.manualResponse("02","Invalid CIN No.");
			}
			return CommonQueryAPIUtils.manualResponse("02",
					"Invalid CIN No.");
		}  catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return CommonQueryAPIUtils.catchResponse(e);
		}
		//return null;
	}

}
