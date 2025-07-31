package in.apcfss.services;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.LegacyAssignmentRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LegacyCasesAssignmentServiceImpl implements LegacyCasesAssignmentService {

	@Autowired
	HttpServletRequest request;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Autowired
	CommonMethodService service;

	@Autowired
	LegacyAssignmentRepo legacyrepo;

	@Autowired
	CommonMethodsController commonMethodsController;

	String entryIp() {
		return request.getRemoteAddr();
	}

	@Override
	public List<Map<String, Object>> getHighCourtCasesListdata(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody) {
		String ConditionService = "", condition = "", sqlCondition = "", sql = "", heading = "", deptId = "",
				deptName = "", deptCode = "";
		Integer distId = 0;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		deptCode = (String) userPrincipal.getDeptCode();
		deptName = repo.getDeptName(deptId);
		distId = userPrincipal.getDistId();
		try {

			if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
					&& !abstractReqBody.getDeptId().toString().contentEquals("0")
					&& !abstractReqBody.getDeptId().equals("ALL")) {
				sqlCondition += " and  a.dept_code ='" + abstractReqBody.getDeptId() + "'";
			}

			if (abstractReqBody.getDofFromDate() != null
					&& !abstractReqBody.getDofFromDate().toString().contentEquals("")) {
				sqlCondition += " and a.dt_regis >= to_date('" + abstractReqBody.getDofFromDate() + "','yyyy-mm-dd') ";
			}
			if (abstractReqBody.getDofToDate() != null
					&& !abstractReqBody.getDofToDate().toString().contentEquals("")) {
				sqlCondition += " and a.dt_regis <= to_date('" + abstractReqBody.getDofToDate() + "','yyyy-mm-dd') ";
			}
			if (abstractReqBody.getCaseTypeId() != null && !abstractReqBody.getCaseTypeId().toString().contentEquals("")
					&& !abstractReqBody.getCaseTypeId().toString().contentEquals("0")
					&& !abstractReqBody.getCaseTypeId().equals("ALL")) {
				sqlCondition += " and trim(a.type_name_reg)='" + abstractReqBody.getCaseTypeId().toString().trim()
						+ "' ";
			}
			if (abstractReqBody.getDistId() != null && !abstractReqBody.getDistId().toString().contentEquals("")
					&& !abstractReqBody.getDistId().toString().contentEquals("0")
					&& !abstractReqBody.getDistId().equals("ALL")) {
				sqlCondition += " and a.dist_id='" + abstractReqBody.getDistId().toString().trim() + "' ";
			}

			if (abstractReqBody.getRegYear() != null && !abstractReqBody.getRegYear().toString().contentEquals("")
					&& !abstractReqBody.getRegYear()
					.equals("ALL") /* && (abstractReqBody.getRegYear().length()) > 0 */) {
				sqlCondition += " and a.reg_year='" + abstractReqBody.getRegYear() + "' ";
			}

			if (abstractReqBody.getPurpose() != null && !abstractReqBody.getPurpose().toString().contentEquals("")
					&& !abstractReqBody.getPurpose().toString().contentEquals("0")) {
				sqlCondition += " and trim(purpose_name)='" + abstractReqBody.getPurpose().trim() + "' ";
			}
			if (abstractReqBody.getPetitionerName() != null
					&& !abstractReqBody.getPetitionerName().toString().contentEquals("")
					&& !abstractReqBody.getPetitionerName().toString().contentEquals("0")) {
				sqlCondition += " and a.pet_name ilike  '%" + abstractReqBody.getPetitionerName() + "%'";
			}

			if (abstractReqBody.getRespodentName() != null
					&& !abstractReqBody.getRespodentName().toString().contentEquals("")
					&& !abstractReqBody.getRespodentName().toString().contentEquals("0")) {
				sqlCondition += " and  a.res_name  ilike  '%" + abstractReqBody.getRespodentName() + "%'";
			}
			if (abstractReqBody.getCategoryServiceId() != null
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("")
					&& !abstractReqBody.getCategoryServiceId().toString().contentEquals("0")) {

				if (abstractReqBody.getCategoryServiceId().equals("NON-SERVICE")) {
					sqlCondition += "and (a.category_service='"
							+ abstractReqBody.getCategoryServiceId().toString().trim()
							+ "'  or a.category_service is null or a.category_service=' ')";
				}

				else {
					sqlCondition += " and a.category_service='"
							+ abstractReqBody.getCategoryServiceId().toString().trim() + "'  ";
				}
			}

			String condition1 = "";
			String condition2 = "";

			if (!roleId.equals("2") && !roleId.equals("6")) { // District Nodal Officer
				sqlCondition += " and a.dept_code='" + deptCode + "' ";
			}

			if (roleId.equals("2")) { // District Collector

				sqlCondition += " and a.case_status=7 and a.dist_id='" + distId + "'";
			} else if (roleId.equals("10")) { // District Nodal Officer
				sqlCondition += " and a.case_status=8 and a.dist_id='" + distId + "'";
			} else if (roleId.equals("5") || roleId.equals("9")) {// NO & HOD
				sqlCondition += " and (a.case_status in (3,4) or a.case_status is null)";
			} else if (roleId.equals("3") || roleId.equals("4")) {// MLO & Sect. Dept.
				sqlCondition += " and (a.case_status is null or a.case_status in (1, 2))";
			} else if (roleId.equals("15")) {// MLO Subject.
				sqlCondition += " and  a.case_status=12 ";
			}

			else if (roleId.equals("6")) {// GP
				condition1 = " inner join ecourts_mst_gp_dept_map emgd on (a.dept_code=emgd.dept_code) ";
				condition2 = " and a.case_status is null or a.case_status=2 ";
			}

			sql = "SELECT cino, date_of_filing, fil_no, fil_year, date_next_list, bench_name, coram, pet_name, dist_name, purpose_name, res_name, pet_adv, res_adv,"
					+ " CONCAT(type_name_fil, '/', a.reg_no, '/', a.reg_year) AS case_reg_no, COALESCE(TRIM(a.scanned_document_path), '-') AS scanned_document_path "
					+ "FROM ecourts_case_data a  " + condition1 + " WHERE ";

			sql += " coalesce(assigned,'f') = 'f' " + sqlCondition
					+ " AND coalesce(ecourts_case_status, '') != 'Closed' " + condition2;

			Map<String, Object> map = new HashMap<>();
			map.put("HEADING", heading);

			System.out.println("unspecified SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (abstractReqBody.getDistId() != null)
				abstractReqBody.setDistId(abstractReqBody.getDistId());
			if (abstractReqBody.getDofFromDate() != null)
				abstractReqBody.setDofFromDate(abstractReqBody.getDofFromDate());
			if (abstractReqBody.getDofToDate() != null)
				abstractReqBody.setDofToDate(abstractReqBody.getDofToDate());
			if (abstractReqBody.getPurpose() != null)
				abstractReqBody.setPurpose(abstractReqBody.getPurpose());
			if (abstractReqBody.getRegYear() != null)
				abstractReqBody.setRegYear(abstractReqBody.getRegYear());
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public ResponseEntity<Map<String, Object>> getassign2DeptHOD(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody) {///All SECRETARIATE,HOD ASSIGNMENT
		String sql = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		int a = 0;
		try {
			String selectedIds = abstractReqBody.getSelectedCaseIds();
			String mloSubjectId = abstractReqBody.getMloSubjectId();
			String caseDept = abstractReqBody.getCaseDept();
			System.out.println("selectedIds---" + selectedIds + "---" + mloSubjectId + "caseDept---" + caseDept);

			String selectedCaseIds = "";
			if (abstractReqBody.getSelectedCaseIds() != null && !abstractReqBody.getSelectedCaseIds().equals("")
					&& abstractReqBody.getCaseDept() != null && !abstractReqBody.getCaseDept().equals("0")) {
				for (String newCaseId : selectedIds.toString().split(",")) {
					selectedCaseIds += "'" + newCaseId + "',";

					System.out.println("newCaseId::::::" + newCaseId);

					a += legacyrepo.insertEcourtsCaseActivitiesHOD(newCaseId, "CASE ASSSIGNED", userId,
							InetAddress.getByName(entryIp()), abstractReqBody.getCaseDept(), null);

					System.out.println(a + ":ACTIVITIES SQL:" + sql + "---a---" + a);
				}

				int assign2deptId = legacyrepo.getDeptCodeByDeptName(caseDept);

				String caseNewDept = abstractReqBody.getCaseDept() + "";
				String newStatusCode = "4";
				if (caseNewDept.contains("01")) {
					newStatusCode = "2";
				} else {
					newStatusCode = "4";
				}
				Integer newStatusCode_int = Integer.parseInt(newStatusCode);


				for (String cIno : selectedIds.toString().split(",")) {
					if (cIno != null && !cIno.equals("")) {
						a += legacyrepo.updateValuesDeptHOD(assign2deptId, caseDept, newStatusCode_int,
								cIno);

						System.out.println("Executing SQL: update ecourts_case_data set dept_id=" + assign2deptId
								+ ", dept_code='" + abstractReqBody.getCaseDept() + "', case_status="
								+ newStatusCode_int + " where cino in (" + cIno + ")");

						System.out.println("UPDATE SQL:" + a);
					}
				}


				return CommonQueryAPIUtils.manualResponse("01",
						"Case/Cases successfully moved to selected Department / HOD.");

			} else if (abstractReqBody.getSelectedCaseIds() != null && !abstractReqBody.getSelectedCaseIds().equals("")
					&& abstractReqBody.getMloSubjectId() != null && !abstractReqBody.getMloSubjectId().equals("0")) {
				for (String newCaseId : selectedIds.toString().split(",")) {
					selectedCaseIds += "'" + newCaseId + "',";

					System.out.println("newCaseId::::::" + newCaseId);

					a += legacyrepo.insertEcourtsCaseActivitiesMLO(newCaseId, "CASE ASSSIGNED TO MLO (SUBJECT)", userId,
							InetAddress.getByName(entryIp()), abstractReqBody.getMloSubjectId(), null);

					// a += jdbcTemplate.update(sql);
					System.out.println(a + ":ACTIVITIES SQL:" + a);
				}

				if (selectedCaseIds != null && !selectedCaseIds.equals("")) {
					selectedCaseIds = selectedCaseIds.substring(0, selectedCaseIds.length() - 1);

					a += legacyrepo.updateValuesDeptMLO(abstractReqBody.getMloSubjectId(), selectedCaseIds);

					System.out.println("UPDATE SQL:a--" + a);

					return CommonQueryAPIUtils.manualResponse("01",
							"Case/Cases successfully moved to selected MLO (Subject).");
				} else {
					return CommonQueryAPIUtils.manualResponse("02",
							"Error in assigning Case to Department/HOD. Kindly try again.");
				}
			}

			else {
				return CommonQueryAPIUtils.manualResponse("02", "Error : Case assignment failed .</font>");

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return CommonQueryAPIUtils.catchResponse(e);
		}

	}

	@Override
	public ResponseEntity<Map<String, Object>> getassignMultiCases2Section(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody) {///All section officers assignment
		String sql = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		int a = 0;
		int b = 0;
		try {

			if (abstractReqBody.getSelectedCaseIds() != null && !abstractReqBody.getSelectedCaseIds().equals("")
					&& abstractReqBody.getEmpDept() != null && !abstractReqBody.getEmpDept().equals("0")) {
				int caseDist = abstractReqBody.getCaseDist();
				int caseDist1 = abstractReqBody.getCaseDist1();

				String empDeptCode = abstractReqBody.getEmpDept();

				System.out.println("distCode---" + caseDist1);

				String tableName = commonMethodsController.getTableName(caseDist1 + "");

				int assign2deptId = legacyrepo.getDeptCodeByDeptName(empDeptCode);

				System.out.println("assign2deptId::" + assign2deptId);

				sql = "select distinct trim(email) from " + tableName + " where substring(global_org_name,1,5)='"
						+ abstractReqBody.getEmpDept() + "' and trim(employee_identity)='"
						+ abstractReqBody.getEmpSection() + "' and " + "trim(post_name_en)='"
						+ abstractReqBody.getEmpPost() + "' and " + "trim(employee_id)='"
						+ abstractReqBody.getEmployeeId() + "' and " + "email is not null ";
				System.out.println("email-----" + sql);
				String emailId = jdbcTemplate.queryForObject(sql, String.class);
				System.out.println("emailId:" + emailId);

				String selectedIds = abstractReqBody.getSelectedCaseIds();
				System.err.println("selectedIds-------" + selectedIds);

				for (String cIno : selectedIds.toString().split(",")) {
					if (cIno != null && !cIno.equals("")) {
						System.out.println("cino-----------" + cIno);
						int insertAssigned = legacyrepo.insertEcourts_Case_Emp_Assigned_Dtls(cIno,
								abstractReqBody.getEmpDept(), abstractReqBody.getEmpSection(),
								abstractReqBody.getEmpPost(), abstractReqBody.getEmployeeId(),
								new Timestamp(new Date().getTime()), InetAddress.getByName(entryIp()), userId, emailId);

						System.out.println("UPDATE 1 SQL:" + insertAssigned);

						String newStatusCode = "0", activityDesc = "";
						if ((caseDist1) > 0) { // Dist. - Section Officer
							newStatusCode = "10";
							activityDesc = "CASE ASSSIGNED TO Section Officer (District)";
						} else if (empDeptCode.contains("01")) { // Sect. Dept. - Section Officer
							newStatusCode = "5";
							activityDesc = "CASE ASSSIGNED TO Section Officer (Sect. Dept.)";
						} else { // HOD - Section Officer.
							newStatusCode = "9";
							activityDesc = "CASE ASSSIGNED TO Section Officer (HOD)";
						}

						int newStatusCodeInt = Integer.parseInt(newStatusCode);

						int updateCase = legacyrepo.updateValuesMultiCaseToSection(assign2deptId, empDeptCode, emailId,
								newStatusCodeInt, caseDist1, cIno);

						System.out.println("UPDATE 2 SQL:" + updateCase);

						System.out.println("Executing SQL: update ecourts_case_data set dept_id=" + assign2deptId
								+ ", dept_code='" + empDeptCode + "', assigned=true" + ", assigned_to='" + emailId
								+ "', case_status=" + newStatusCodeInt + ", dist_id=" + caseDist1 + " where cino='"
								+ cIno + "'");

						int insertActivity = legacyrepo.insertecourtsCaseActivitiesMultiCaseToSection(cIno,
								activityDesc, userId, InetAddress.getByName(entryIp()), emailId,
								abstractReqBody.getCaseRemarks(), caseDist1);
						System.out.println("UPDATE 3 SQL:" + insertActivity);

						a = insertAssigned + updateCase + insertActivity;

					}

				}
				/*
				 * if (a > 0) { System.out.println("0000000000000000000"+a); return
				 * CommonQueryAPIUtils.manualResponse(
				 * "01","Case successfully Assigned to Selected Employee."); } else { return
				 * CommonQueryAPIUtils.manualResponse("02",
				 * "Error in assigning Cases. Kindly try again."); }
				 */

				int userCount = legacyrepo.getUserCountByEmail(emailId.trim());

				if (userCount > 0) {

					return CommonQueryAPIUtils.manualResponse("01", "Case successfully Assigned to Selected Employee.");
				} else {

					int newRoleId = 8;
					if ((caseDist1) > 0) { // Dist. - Section Officer
						newRoleId = 12;
					} else if (abstractReqBody.getEmpDept().toString().contains("01")) { // Sect. Dept. - Section
						// Officer
						newRoleId = 8;
					} else { // HOD - Section Officer.
						newRoleId = 11;
					}

					sql = "insert into section_officer_details (emailid, dept_id,designation,employeeid,mobileno,aadharno,inserted_by,inserted_ip, dist_id) "
							+ " select distinct b.email,d.sdeptcode||d.deptcode,b.designation_id,b.employee_id,b.mobile1,uid,'"
							+ userPrincipal.getUserId() + "','" + request.getRemoteAddr() + "'::inet," + caseDist1
							+ " from " + tableName + " b inner join dept_new d on (d.dept_code='"
							+ abstractReqBody.getEmpDept().trim() + "')" + " where b.employee_id='"
							+ abstractReqBody.getEmployeeId().trim() + "' and trim(b.employee_identity)='"
							+ abstractReqBody.getEmpSection().trim() + "' and trim(b.post_name_en)='"
							+ abstractReqBody.getEmpPost().trim() + "'";
					System.out.println("Executing Query: " + sql);
					b += jdbcTemplate.update(sql);

					System.out.println("NEW SECTION OFFICER CREATION SQL:" + b);

					sql = " INSERT INTO users (userid, password, password_text, user_description, created_by, created_on, created_ip, dept_id, dept_code, user_type, dist_id)  "
							+ " SELECT DISTINCT b.email, md5('olcms@123'), 'olcms@123', b.fullname_en, '"
							+ userPrincipal.getUserId() + "',  now() , '" + request.getRemoteAddr() + "'::inet,  "
							+ " d.dept_id, d.dept_code, '" + newRoleId + "', " + caseDist1 + "  " + " FROM " + tableName
							+ " b  " + " INNER JOIN dept_new d ON d.dept_code = '" + abstractReqBody.getEmpDept().trim()
							+ "'  " + " WHERE b.employee_id = '" + abstractReqBody.getEmployeeId().trim() + "'  "
							+ " AND TRIM(b.employee_identity) = '" + abstractReqBody.getEmpSection().trim() + "'  "
							+ " AND TRIM(b.post_name_en) = '" + abstractReqBody.getEmpPost().trim() + "'";
					System.out.println("USER CREATION SQL:" + sql);
					b += jdbcTemplate.update(sql);

					System.out.println("b:" + b);

					sql = "select distinct mobile1 from " + tableName + " b " + " where b.employee_id='"
							+ abstractReqBody.getEmployeeId().trim() + "' and trim(b.employee_identity)='"
							+ abstractReqBody.getEmpSection().trim() + "' and trim(b.post_name_en)='"
							+ abstractReqBody.getEmpPost().trim() + "'" + " and mobile1 is not null";

					System.out.println("MOBILE SQL:" + sql);

					String mobileNo = jdbcTemplate.queryForObject(sql, String.class);
					System.out.println("mobileNo---" + mobileNo);

					b += legacyrepo.insertUsersRoles(emailId, newRoleId);
					System.out.println("INSERT ROLE SQL:" + b);

					if (b == 3) {
						String smsText = "Your User Id is " + emailId
								+ " and Password is olcms@123 to Login to https://apolcms.ap.gov.in/ Portal. Please do not share with anyone. \r\n-APOLCMS";
						String templateId = "1007784197678878760";
						mobileNo = "8500909816";
						System.out.println(mobileNo + "" + smsText + "" + templateId);
						if (mobileNo != null && !mobileNo.equals("")) {
							// mobileNo = "8096553869";
							// System.out.println("mobileNo::"+mobileNo);
							// service.sendSMS(mobileNo, smsText, templateId);
						}

						return CommonQueryAPIUtils.manualResponse("01",
								"Cases successfully Assigned to Selected Employee & User Login created successfully. Login details sent to Registered Mobile No.");
					} else {
						return CommonQueryAPIUtils.manualResponse("02", "Error in assigning Cases. Kindly try again.");
					}
				}

			} else
				return CommonQueryAPIUtils.manualResponse("02", "Error : Invalid Data.</font>");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return CommonQueryAPIUtils.manualResponse("02",
					"Error in assigning Case to Department/HOD. Kindly try again.");
		} finally {

			if (abstractReqBody.getDistId() != null)
				abstractReqBody.setDistId(abstractReqBody.getDistId());
			if (abstractReqBody.getDofFromDate() != null)
				abstractReqBody.setDofFromDate(abstractReqBody.getDofFromDate());
			if (abstractReqBody.getDofToDate() != null)
				abstractReqBody.setDofToDate(abstractReqBody.getDofToDate());
			if (abstractReqBody.getPurpose() != null)
				abstractReqBody.setPurpose(abstractReqBody.getPurpose());
			if (abstractReqBody.getRegYear() != null)
				abstractReqBody.setRegYear(abstractReqBody.getRegYear());
		}

	}

	@Override
	public ResponseEntity<Map<String, Object>> getassign2DistCollector(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody) { ///All DC,DCNO ASSIGNMENT
		String sql = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		int a = 0;
		int caseDist = abstractReqBody.getCaseDist();
		String successMsg = "";
		String distDept = abstractReqBody.getDistDept();
		try {
			String selectedCaseIds = "";
			String officerType = abstractReqBody.getOfficerType();
			if (abstractReqBody.getSelectedCaseIds() != null && !abstractReqBody.getSelectedCaseIds().equals("")) {
				String selectedIds = abstractReqBody.getSelectedCaseIds();
				System.out.println("selectedIds---" + selectedIds);
				// for (String newCaseId :
				// abstractReqBody.getSelectedCaseIds().toString().split(",")) {
				for (String newCaseId : selectedIds.toString().split(",")) {
					System.out.println("newCaseId--" + newCaseId);
					selectedCaseIds += "'" + newCaseId.trim() + "',";
					// if (newCaseId != null && !newCaseId.equals("")) {
					if (officerType.equals("DC")) {
						System.out.println("officerType:" + officerType);
						a = legacyrepo.insertActivityDC(newCaseId, userId, InetAddress.getByName(entryIp()),
								caseDist, caseDist);
					} else if (officerType.equals("DC-NO")) {

						a = legacyrepo.insertActivityDCNO(newCaseId, userId, InetAddress.getByName(entryIp()),
								abstractReqBody.getDistDept(), caseDist);
					}
					System.out.println("insert SQL:" + a);
					// }
				}
				for (String cIno : selectedIds.toString().split(",")) {
					if (cIno != null && !cIno.equals("")) {

						if (officerType.equals("DC")) {

							a = legacyrepo.updateDC(caseDist, cIno);

							successMsg = "Case/Cases successfully moved to selected District Collector Login";

						} else if (officerType.equals("DC-NO")) {

							System.out.println("caseDept--" + distDept);
							int assign2deptId = legacyrepo.getDeptCodeByDeptName(distDept);

							System.out.println("assign2deptId--" + assign2deptId);
							a = legacyrepo.updateDCNO(assign2deptId, caseDist, abstractReqBody.getDistDept(), cIno);

							successMsg = "Case/Cases successfully moved to selected District Nodal Officer Login";
						}
					}
				}

				System.out.println("UPDATE SQL:" + a);

				return CommonQueryAPIUtils.manualResponse("01", successMsg);

			} else
				return CommonQueryAPIUtils.manualResponse("02", "Error : Case assignment failed .</font>");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return CommonQueryAPIUtils.catchResponse(e);
		} finally {

			if (abstractReqBody.getDistId() != null)
				abstractReqBody.setDistId(abstractReqBody.getDistId());
			if (abstractReqBody.getDofFromDate() != null)
				abstractReqBody.setDofFromDate(abstractReqBody.getDofFromDate());
			if (abstractReqBody.getDofToDate() != null)
				abstractReqBody.setDofToDate(abstractReqBody.getDofToDate());
			if (abstractReqBody.getPurpose() != null)
				abstractReqBody.setPurpose(abstractReqBody.getPurpose());
			if (abstractReqBody.getRegYear() != null)
				abstractReqBody.setRegYear(abstractReqBody.getRegYear());
		}

	}

	@Override
	public List<Map<String, Object>> empDeptListToAssignCases(UserDetailsImpl userPrincipal, String deptCode,
			String typeCode) {
		String sql = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		List<Map<String, Object>> s = null;
		try {
			if (typeCode != null && !typeCode.equals("") && !typeCode.equals("0")) {
				if (deptCode != null && !deptCode.isEmpty() && deptCode != "0") {
					// System.out.println(deptCode+"----------------inner----------");
					if (typeCode.equals("S-HOD") || typeCode.equals("SD-SO")) {

						if (deptCode.substring(3, 5).equals("01")) {
							sql = " select dept_code as value,dept_code||'-'||upper(description) as label from dept_new where (dept_code='"
									+ deptCode + "' or reporting_dept_code='" + deptCode
									+ "') and display=true order by dept_code ";
						} else {
							sql = " select dept_code as value,dept_code||'-'||upper(description) as label from dept_new where reporting_dept_code in (select reporting_dept_code from dept_new where dept_code='"
									+ deptCode + "') and display=true order by dept_code ";
						}

					} else if (typeCode.equals("DC") || typeCode.equals("DC-NO")) {

						if (deptCode.substring(3, 5).equals("01")) {
							sql = " select dept_code as value,dept_code||'-'||upper(description)  as label from dept_new  "
									+ " where (dept_code='"+ deptCode + "' and reporting_dept_code='" + deptCode+ "') and display=true order by dept_code ";
						}else {

							sql = " select dept_code as value,dept_code||'-'||upper(description)  as label from dept_new  "
									+ " where (dept_code='"+ deptCode + "' ) and display=true order by dept_code ";
						}
					}else if (typeCode.equals("D-HOD") || typeCode.equals("OD-SO")) {

						sql = " select dept_code as value,dept_code||'-'||upper(description)  as label from dept_new where (dept_code!='"
								+ deptCode + "' and reporting_dept_code!='" + deptCode
								+ "') and display=true order by dept_code ";
					} else if (typeCode.equals("DC-SO")) {

						sql = " select dept_code as value,dept_code||'-'||upper(description) as label from "
								+ "dept_new where deptcode!='01' and display=true order by dept_code ";
					} else {

						sql = " select dept_code as value,dept_code||'-'||upper(description) as label "
								+ "from dept_new where display=true order by dept_code ";
					}
					System.out.println(deptCode + ":getDeptList :sql" + sql);
					s = jdbcTemplate.queryForList(sql);
				} else {
					// System.out.println(deptCode+"----------------outttt----------");
					sql = " select dept_code as value,dept_code||'-'||upper(description) as label from"
							+ " dept_new where display=true order by dept_code ";
					System.out.println(deptCode + ":getEmpsList :sql" + sql);
					s = jdbcTemplate.queryForList(sql);
				}
			}

			System.out.println("unspecified SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
}
