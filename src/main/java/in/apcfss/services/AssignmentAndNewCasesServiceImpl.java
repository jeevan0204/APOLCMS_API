package in.apcfss.services;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.common.CommonModels;
import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignmentAndNewCasesRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AssignmentAndNewCasesServiceImpl implements AssignmentAndNewCasesService {

	@Autowired
	HttpServletRequest request;

	@Autowired
	JdbcTemplate jdbcTemplate;

	String entryIp() {
		return request.getRemoteAddr();
	}

	@Autowired
	AssignmentAndNewCasesRepo AssignmentRepo;
 

	@Autowired
	CommonMethodsController commonMethodsController;

	@Override
	public List<Map<String, Object>> getAssignmentAndNewCasesList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sql = null, sqlCondition = "", roleId = "", distId = "", deptCode = "",userid="";
		Integer distCode=0;

		try {
			roleId =  userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
			deptCode =  userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
			distCode = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

			userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";


			if (abstractReqBody.getCaseTypeId() != null && !StringUtils.isBlank(abstractReqBody.getCaseTypeId().toString())
					&& !abstractReqBody.getCaseTypeId().toString().contentEquals("0")) {
				sqlCondition += " and a.casetype='" +abstractReqBody.getCaseTypeId().toString().trim() + "' ";
			}


			if (abstractReqBody.getAdvcteName() != null
					&& !abstractReqBody.getAdvcteName().toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"
						+abstractReqBody.getAdvcteName()+ "%'";
			}

			if (abstractReqBody.getDistrictId() != null && abstractReqBody.getDistrictId() != ""
					&& !CommonModels.checkStringObject(abstractReqBody.getDistrictId()).contentEquals("")
					&& !CommonModels.checkStringObject(abstractReqBody.getDistrictId()).contentEquals("0")) {
				sqlCondition += " and ad.dist_id='" + abstractReqBody.getDistrictId().toString().trim() + "' ";

				abstractReqBody.setDistrictId(abstractReqBody.getDistrictId());
			}
			if (abstractReqBody.getDeptId() != null
					&& !CommonModels.checkStringObject(abstractReqBody.getDeptId()).contentEquals("")
					&& !CommonModels.checkStringObject(abstractReqBody.getDeptId()).contentEquals("0")) {
				sqlCondition += " and ad.dept_code='" + abstractReqBody.getDeptId().toString().trim() + "' ";

				abstractReqBody.setDeptId(abstractReqBody.getDeptId());
			}
			if (abstractReqBody.getFromDate() != null
					&& !CommonModels.checkStringObject(abstractReqBody.getFromDate()).contentEquals("")) {
				sqlCondition += " and a.inserted_time::date >= to_date('" +abstractReqBody.getFromDate()
				+ "','dd-mm-yyyy') ";
				abstractReqBody.setFromDate(abstractReqBody.getFromDate());
			}
			if (abstractReqBody.getToDate() != null
					&& !CommonModels.checkStringObject(abstractReqBody.getToDate()).contentEquals("")) {
				sqlCondition += " and a.inserted_time::date <= to_date('" +abstractReqBody.getToDate()
				+ "','dd-mm-yyyy') ";
				abstractReqBody.setToDate(abstractReqBody.getToDate());
			}


			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("3") || roleId.equals("4"))) {

				sqlCondition += " and dmt.dept_code='" + deptCode + "' ";
			}

			if (roleId.equals("2")) {// District Collector
				sqlCondition += " and (case_status is null or case_status=7) and ad.dist_id='" + distCode + "' ";

				abstractReqBody.setDistrictId(String.valueOf(distCode));
			}

			if (roleId.equals("3")) {// Secretariat Department
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
						+ "') ";
			}
			if (roleId.equals("4")) {// MLO
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
						+ "') ";
			}

			else if (roleId.equals("10")) { // District Nodal Officer
				sqlCondition += " and (case_status is null or case_status=8) and dist_id='" + distCode + "'";
			} else if (roleId.equals("5") || roleId.equals("9")) { // NO & HOD
				sqlCondition += " and (case_status is null or case_status in (3,4))";
			} else if (roleId.equals("3") || roleId.equals("4")) {// MLO & Sect. Dept.
				sqlCondition += " and (case_status is null or case_status in (1, 2))";
			}

			sql = "select  a.ack_no,a.slno , file_found,ad.respondent_slno , a.inserted_time,distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip, upper(trim(district_name)) as district_name, "
					+ " upper(trim(case_full_name)) as  case_full_name, a.ack_file_path, case when services_id='0' then null else services_id end as services_id,services_flag, "
					+ " to_char(a.inserted_time,'yyyy-MM-dd') as generated_date, getack_dept_desc(a.ack_no::text) as dept_descs, coalesce(a.hc_ack_no,'-') as hc_ack_no "
					+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no) left join scanned_affidavit_new_cases_count sc on (sc.ack_no=a.ack_no or  sc.ack_no=a.hc_ack_no) "
					+ " left join district_mst dm on (ad.dist_id=dm.district_id) "
					+ " left join dept_new dmt on (ad.dept_code=dmt.dept_code)"
					+ " inner join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) "
					+ " where a.delete_status is false and coalesce(assigned,'f')='f'  and ack_type='NEW' and inserted_by not like 'PP%' " 
					+ sqlCondition + "  order by a.inserted_time desc";

			System.out.println("CASES SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public String getassign2DeptHOD(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		String msg="";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		try {

			String selectedCaseIds = "";
			String[] ids_split = null;

			int a=0;

			System.out.println("ids---"+abstractReqBody.getSelectedCaseIds());

			if (!CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds()).equals("")
					&& !CommonModels.checkStringObject(abstractReqBody.getCaseDept()).equals("0") && !CommonModels.checkStringObject(abstractReqBody.getCaseDept()).equals("")) {
				for (String newCaseId : CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds()).split(",")) {

					System.out.println("newCaseId---"+newCaseId);

					String ids = newCaseId;
					ids_split = ids.split("@");
					System.out.println("ids--" + ids_split[0]);
					System.out.println("ids--" + ids_split[1]);

					selectedCaseIds += "'" + ids_split[0] + "',";

					System.out.println("newCaseId::::::" + ids_split[0]);

					a += AssignmentRepo.ecourts_case_activitiesAssign2DeptHOD(ids_split[0],userId,request.getRemoteAddr(),CommonModels.checkStringObject(abstractReqBody.getCaseDept()));

					System.out.println(a + ":ACTIVITIES SQL:" + a);

					a += AssignmentRepo.ecourts_gpo_ack_depts_logAssign2DeptHOD(ids_split[0],Integer.parseInt(ids_split[1]));
					System.out.println("INSERT SQL:" + a);

					String caseNewDept = CommonModels.checkStringObject(abstractReqBody.getCaseDept());

					System.out.println("caseNewDept--"+caseNewDept);
					int newStatusCode = 4;
					if (caseNewDept.contains("01")) {
						newStatusCode = 2;
					} else {
						newStatusCode = 4;
					}

					a += AssignmentRepo.ecourts_gpo_ack_deptsAssign2DeptHOD(caseNewDept,newStatusCode,ids_split[0],Integer.parseInt(ids_split[1]));
					System.out.println("UPDATE SQL:" + a);
				}

				msg= "Case/Cases successfully moved to selected Department / HOD.";
			} else
				msg="Error : Case assignment failed .</font>";
		} catch (Exception e) {

			msg="Error in assigning Case to Department/HOD. Kindly try again.";
			e.printStackTrace();
		}
		return msg;

	}
	@Override
	public String getAssignMultiCasesToSection(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		String msg="" ;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String emailId="";
		try {

			int a=0;
			String login_deptId =userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
			int user_dist = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
			System.out.println("user_dist---" + user_dist);

			String officerType = CommonModels.checkStringObject(abstractReqBody.getOfficerType());
			if (!CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds()).equals("")
					&& !CommonModels.checkStringObject(abstractReqBody.getEmpDept()).equals("0") && !CommonModels.checkStringObject(abstractReqBody.getEmpDept()).equals("")) {

				String caseDist1 = CommonModels.checkStringObject(abstractReqBody.getCaseDist1());
				String empDeptCode = CommonModels.checkStringObject(abstractReqBody.getEmpDept());

				String tableName = commonMethodsController.getTableName(caseDist1+"");

				System.out.println("tableName---"+tableName);

				String sql = " select distinct trim(email) from " + tableName+ " where substring(global_org_name,1,5)='" +empDeptCode+ "' "
						+ " and trim(employee_identity)='" + abstractReqBody.getEmpSection()+ "' "
						+ " and trim(post_name_en)='" + abstractReqBody.getEmpPost()+ "' and trim(employee_id)='"+ abstractReqBody.getEmployeeId()+ "' and email is not null ";

				System.out.println("sql======:" + sql);
				emailId=jdbcTemplate.queryForObject(sql, String.class);

				System.out.println("emailId+++++++++++======:" + emailId);
				for (String cIno : CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds()).split(",")) {

					System.out.println("cino ===============" + cIno);

					String ids = cIno;
					String[] ids_split = ids.split("@");
					System.out.println("ids--" + ids_split[0]);
					System.out.println("ids--" + ids_split[1]);

					if (ids_split[0] != null && !ids_split[0].equals("")) {

						int insertAssigned = AssignmentRepo.insertEcourts_ack_assignment_dtls(ids_split[0],
								abstractReqBody.getEmpDept(), abstractReqBody.getEmpSection(),
								abstractReqBody.getEmpPost(), abstractReqBody.getEmployeeId(),
								new Timestamp(new Date().getTime()), request.getRemoteAddr(), userId, emailId);

						System.out.println("assigned multi cases__________________ " + insertAssigned);

						int newStatusCode = 0;
						String activityDesc = "";
						if (Integer.parseInt(caseDist1) > 0) { // Dist. - Section Officer
							newStatusCode = 10;
							activityDesc = "CASE ASSSIGNED TO Section Officer (District)";
						} else if (empDeptCode.contains("01")) { // Sect. Dept. - Section Officer
							newStatusCode = 5;
							activityDesc = "CASE ASSSIGNED TO Section Officer (Sect. Dept.)";
						} else { // HOD - Section Officer.
							newStatusCode = 9;
							activityDesc = "CASE ASSSIGNED TO Section Officer (HOD)";
						}
						System.out.println("newStatusCode--" + newStatusCode);

						a += AssignmentRepo.ecourts_gpo_ack_depts_logAssignMultiCasesToSection(ids_split[0],Integer.parseInt(ids_split[1]));
						System.out.println("INSERT SQL:" + a);
						System.out.println("officerType--" + officerType);

						if (officerType.equals("DC") || officerType.equals("DC-SO")) {

							a += AssignmentRepo.ecourts_gpo_ack_deptsAssignMultiCasesToSectionDC(empDeptCode,emailId,newStatusCode,Integer.parseInt(caseDist1),ids_split[0],Integer.parseInt(ids_split[1]));
						}
						else if(officerType.equals("DC-NO"))
						{
							newStatusCode=8;

							a += AssignmentRepo.ecourts_gpo_ack_deptsAssignMultiCasesToSectionDCNO(empDeptCode,emailId,newStatusCode,Integer.parseInt(caseDist1),ids_split[0],Integer.parseInt(ids_split[1]));
						}
						else {

							a += AssignmentRepo.ecourts_gpo_ack_deptsAssignMultiCasesToSectionELSE(empDeptCode,emailId,newStatusCode,Integer.parseInt(caseDist1),ids_split[0],Integer.parseInt(ids_split[1]));
						}

						a += AssignmentRepo.ecourts_case_activitiesAssignMultiCasesToSection(ids_split[0],activityDesc,userId,request.getRemoteAddr(),emailId,abstractReqBody.getCaseRemarks(),Integer.parseInt(caseDist1));
						System.out.println("a:----" + a);

						if (a > 0) {
							msg="Case successfully Assigned to Selected Employee.";
						} else {
							msg="Error in assigning Cases. Kindly try again--.";
						}
					}
				}

				int b = 0;
				if (Integer.parseInt(AssignmentRepo.userCount(emailId.trim())) > 0) {

					msg="Case successfully Assigned to Selected Employee.";
				} else {

					int newRoleId = 8;
					if (Integer.parseInt(caseDist1) > 0) { // Dist. - Section Officer
						newRoleId = 12;
					} else if (CommonModels.checkStringObject(abstractReqBody.getEmpDept()).contains("01")) { // Sect. // Dept.- // Section // Officer
						newRoleId = 8;
					} else { // HOD - Section Officer.
						newRoleId = 11;
					}

					// NEW SECTION OFFICER CREATION

					sql = " insert into section_officer_details (emailid, dept_id,designation,employeeid,mobileno,aadharno,inserted_by,inserted_ip, dist_id) "
							+ " select distinct b.email,d.sdeptcode||d.deptcode,b.designation_id,b.employee_id,b.mobile1,uid,'"
							+ userPrincipal.getUserId() + "','" + request.getRemoteAddr() + "'::inet," + caseDist1
							+ " from " + tableName + " b inner join dept_new d on (d.dept_code='"
							+ abstractReqBody.getEmpDept().trim() + "')" + " where b.employee_id='"
							+ abstractReqBody.getEmployeeId().trim() + "' and trim(b.employee_identity)='"
							+ abstractReqBody.getEmpSection().trim() + "' and trim(b.post_name_en)='"
							+ abstractReqBody.getEmpPost().trim() + "'";

					System.out.println("Executing Query: " + sql);
					b += jdbcTemplate.update(sql);

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


					b += AssignmentRepo.insertUsersRoles(emailId, newRoleId);
					System.out.println("INSERT ROLE SQL:" + b);

					System.out.println("newRoleId--"+newRoleId);

					if (b == 3) {
						String smsText = "Your User Id is " + emailId
								+ " and Password is olcms@123 to Login to https://apolcms.ap.gov.in/ Portal. Please do not share with anyone. \r\n-APOLCMS";
						String templateId = "1007784197678878760";
						// mobileNo = "8500909816";
						System.out.println(mobileNo + "" + smsText + "" + templateId);
						if (mobileNo != null && !mobileNo.equals("")) {
							// mobileNo = "8500909816";
							System.out.println("mobileNo::" + mobileNo);
							//sendSMSDao.sendSMS(mobileNo, smsText, templateId);
							//smsService.sendSMS(mobileNo, smsText, templateId);
						}
						System.out.println("sendSMS----");
						msg="Cases successfully Assigned to Selected Employee & User Login created successfully. Login details sent to Registered Mobile No.";

					} else {

						msg="Error in assigning Cases. Kindly try again.";
					}
				}

			} else
				msg="Error : Invalid Data.</font>";
		} catch (Exception e) {

			msg="Error in assigning Cases. Kindly try again.";
			e.printStackTrace();
		}
		return msg;
	}
	
	@Override
	public String getAssignToDistCollector(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		String msg = "";
		int a=0;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		try {
			String[] ids_split = null;
			String selectedCaseIds = "";
			
			System.out.println("selected acks-----"+abstractReqBody.getSelectedCaseIds());
			
			String officerType = CommonModels.checkStringObject(abstractReqBody.getOfficerType());
			if (!CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds()).equals("")) {
				for (String newCaseId : CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds()).split(",")) {

					String ids = newCaseId;
					ids_split = ids.split("@");
					System.out.println("ids--" + ids_split[0]);
					System.out.println("ids--" + ids_split[1]);

					selectedCaseIds += "'" + ids_split[0] + "',";

					if (officerType.equals("DC")) {

						a= AssignmentRepo.insertActivitiesDC(ids_split[0],userId,request.getRemoteAddr(),
								CommonModels.checkStringObject(abstractReqBody.getCaseDist()),CommonModels.checkIntObject(abstractReqBody.getCaseDist()));
						System.out.println("a--1---"+a);
					} else if (officerType.equals("DC-NO")) {

						a= AssignmentRepo.insertActivitiesDCNO(ids_split[0],userId,request.getRemoteAddr(),
								CommonModels.checkStringObject(abstractReqBody.getDistDept()),CommonModels.checkIntObject(abstractReqBody.getCaseDist()));
						System.out.println("a--2---"+a);
					}

					String distDept = CommonModels.checkStringObject(abstractReqBody.getCaseDist());
					System.out.println("+distDept--" + distDept);

					String successMsg = "";
					if (officerType.equals("DC")) {

						a= AssignmentRepo.insertAck_depts_logDC(ids_split[0],CommonModels.checkIntObject(abstractReqBody.getCaseDist()),CommonModels.checkIntObject(ids_split[1]));
						System.out.println("a--3---"+a);
						a= AssignmentRepo.updateAck_ack_deptsDC(CommonModels.checkIntObject(abstractReqBody.getCaseDist()),abstractReqBody.getDistDept(),ids_split[0],Integer.parseInt(ids_split[1]));
						System.out.println("a--4---"+a);
						msg = "Case/Cases successfully moved to selected District Collector Login";

					} else if (officerType.equals("DC-NO")) {
						 
						a= AssignmentRepo.insertAck_depts_logDCNO(ids_split[0],Integer.parseInt(ids_split[1]));
						System.out.println("a--5---"+a);
						a= AssignmentRepo.updateAck_ack_deptsDCNO(abstractReqBody.getDistDept(),CommonModels.checkIntObject(abstractReqBody.getCaseDist()),ids_split[0],Integer.parseInt(ids_split[1])); 
						System.out.println("a--6---"+a);
						msg = "Case/Cases successfully moved to selected District Nodal Officer Login";
					}
					 
				}

			} else
				msg ="Error : Case assignment failed .</font>";
		} catch (Exception e) {

			msg="Error in assigning Case to Department/HOD. Kindly try again.";
			e.printStackTrace();
		}
		return msg;

	}
	
	
 
}
