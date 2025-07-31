package in.apcfss.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import in.apcfss.common.CommonModels;
import in.apcfss.controllers.AssignedCasesToSectionController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedNewCasesToEmpRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AssignedNewCasesToEmpServiceImpl implements AssignedNewCasesToEmpService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	AssignedCasesToSectionController assignedCasesToSectionController;

	@Autowired
	AssignedNewCasesToEmpRepo assignedNewCasesToEmpRepo;

	@Override
	public List<Map<String, Object>> getAssignedNewCasesToEmp(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody){
		String condition = "", sql = "", condition_gp = "", deptCode = "";
		Integer distCode = 0;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		distCode = userPrincipal.getDistId() != null ? userPrincipal.getDistId(): 0;
		String sqlCondition = "";
		String condition1 = "";
		try {

			if (abstractReqBody.getDistrictId() != null && !abstractReqBody.getDistrictId().toString().contentEquals("")
					&& !abstractReqBody.getDistrictId().toString().contentEquals("0")) {
				sqlCondition += " and a.distid='" +abstractReqBody.getDistrictId().toString().trim() + "' ";
			}

			if (abstractReqBody.getDeptId()!= null && !abstractReqBody.getDeptId().toString().contentEquals("")
					&& !abstractReqBody.getDeptId().toString().contentEquals("0")) {
				sqlCondition += " and ad.dept_code='" +abstractReqBody.getDeptId().toString().trim() + "' ";
			}

			if (abstractReqBody.getFromDate() != null && !abstractReqBody.getFromDate().toString().contentEquals("")) {
				sqlCondition += " and a.inserted_time::date >= to_date('" +abstractReqBody.getFromDate()
				+ "','dd-mm-yyyy') ";
			}
			if (abstractReqBody.getToDate()!= null && !abstractReqBody.getToDate().toString().contentEquals("")) {
				sqlCondition += " and a.inserted_time::date <= to_date('" +abstractReqBody.getToDate()
				+ "','dd-mm-yyyy') ";
			}
			if (abstractReqBody.getCaseTypeId() != null && !abstractReqBody.getCaseTypeId().toString().contentEquals("")
					&& !abstractReqBody.getCaseTypeId().toString().contentEquals("0")) {
				sqlCondition += " and a.casetype='" +abstractReqBody.getCaseTypeId().toString().trim() + "' ";
			}

			if (roleId != null && roleId.equals("4")) { // MLO
				sqlCondition = " and ad.dept_code='" + deptCode
						+ "' and ad.case_status in(2) and coalesce(assigned,'f')='t'";
			} else if (roleId != null && roleId.equals("5")) { // NO
				sqlCondition = " and ad.dept_code='" + deptCode
						+ "' and ad.case_status in(4) and coalesce(assigned,'f')='t'  ";
			} else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				sqlCondition = " and ad.dept_code='" + deptCode + "' and ad.case_status=5 and ad.assigned_to='" + userid
						+ "'";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				sqlCondition = " and ad.dept_code='" + deptCode + "' and ad.case_status=9 and ad.assigned_to='" + userid
						+ "'";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				sqlCondition = " and ad.dept_code='" + deptCode + "' and ad.dist_id='" + distCode
						+ "' and ad.case_status=10 and coalesce(assigned,'f')='t'   and ad.assigned_to='" + userid
						+ "'";
			}

			else if (roleId != null && roleId.equals("3")) { // SECT DEPT
				sqlCondition = " and ad.dept_code='" + deptCode
						+ "' and ad.case_status in(1)  and coalesce(assigned,'f')='t'   ";
			} else if (roleId != null && roleId.equals("9")) { // HOD
				sqlCondition = " and ad.dept_code='" + deptCode
						+ "' and ad.case_status in(3)  and coalesce(assigned,'f')='t' ";
			} else if (roleId != null && roleId.equals("2")) { // DC
				sqlCondition = " and ad.case_status=7 and coalesce(assigned,'f')='t'  and ad.dist_id='" + distCode
						+ "'";
			} else if (roleId != null && roleId.equals("10")) { // DC-NO
				sqlCondition = " and ad.dept_code='" + deptCode
						+ "' and ad.case_status=8 and coalesce(assigned,'f')='t'   and ad.dist_id='" + distCode + "'";
			}

			if (roleId != null && roleId.equals("6")) { // GPO

				condition1 = " inner join ecourts_mst_gp_dept_map emgd on (ad.dept_code=emgd.dept_code) "
						+ " inner join ecourts_olcms_case_details eocd on (eocd.cino=ad.ack_no)";

				sqlCondition += " and counter_filed='Yes' and ad.case_status='6' and coalesce(assigned,'f')='t' and ad.assigned_to='"
						+ userid + "' ";

				String counter_pw_flag = CommonModels.checkStringObject(abstractReqBody.getPwCounterFlag());

				if (counter_pw_flag.equals("PR")) {
					condition1 += " and pwr_uploaded='No' and (coalesce(pwr_approved_gp,'0')='0' or coalesce(pwr_approved_gp,'No')='No' )";
				}
				if (counter_pw_flag.equals("COUNTER")) {
					condition1 += " and pwr_uploaded='Yes' and counter_filed='No' and coalesce(counter_approved_gp,'F')='F'";
				}
			}

			sql = "select  distinct a.ack_no ,a.slno,ad.respondent_slno ,a.inserted_time,file_found, distid , advocatename ,advocateccno , casetype , maincaseno , a.remarks ,  inserted_by , inserted_ip, upper(trim(district_name)) as district_name, "
					+ " upper(trim(case_full_name )) as  case_full_name, a.ack_file_path, case when services_id='0' then null else services_id end as services_id,services_flag, "
					+ " to_char(a.inserted_time,'yyyy-MM-dd') as generated_date, "
					+ " getack_dept_desc(a.ack_no::text) as dept_descs , "
					+ " coalesce(a.hc_ack_no,'-') as hc_ack_no,od.pwr_approved_gp, coalesce(od.counter_approved_gp,'-') as counter_approved_gp  ,"
					+ " case when pwr_uploaded='Yes' then 'Parawise Remarks Uploaded' else 'Parawise Remarks not Submitted' end as casestatus1, "
					+ " case when pwr_approved_gp='Yes' then 'Parawise Remarks Approved by GP' else 'Parawise Remarks Not Approved by GP' end as casestatus2, "
					+ " case when counter_filed='Yes' then 'Counter Filed' else 'Counter Not Filed' end as casestatus3, "
					+ " case when counter_approved_gp='T' then 'Counter Approved by GP' else 'Counter Not Approved by GP' end as casestatus4  "
					+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no)  left join scanned_affidavit_new_cases_count sc on (sc.ack_no=a.ack_no or  sc.ack_no=a.hc_ack_no) "
					+ " left join district_mst dm on (ad.dist_id=dm.district_id) "
					+ " left join dept_new dmt on (ad.dept_code=dmt.dept_code)"
					+ " inner join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) left join ecourts_olcms_case_details od on (ad.ack_no=od.cino )" //and od.respondent_slno=ad.respondent_slno
					+ condition1 + "   "
					+ " where a.delete_status is false and ack_type='NEW' and inserted_by not like 'PP%'   and coalesce(ad.ecourts_case_status,'')!='Closed'  "
					+ sqlCondition + "   order by a.inserted_time desc"; 

			System.out.println("CASES SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getÙsersList(String cIno){

		List<Map<String, Object>> data=null;
		try {
			data=assignedNewCasesToEmpRepo.getÙsersList(cIno);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	@Override
	public List<Map<String, Object>> getRespodentList(String cIno){

		List<Map<String, Object>> data=null;
		try {
			data=assignedNewCasesToEmpRepo.getRespodentList(cIno);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	@Override
	public List<Map<String, Object>> getOtherRespodentList(String cIno){

		List<Map<String, Object>> data=null;
		try {
			data=assignedNewCasesToEmpRepo.getOtherRespodentList(cIno);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	@Override
	public List<Map<String, Object>> getActivitiesData(String cIno){

		List<Map<String, Object>> data=null;
		try {
			data=assignedNewCasesToEmpRepo.getActivitiesData(cIno);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}


	public Map<String, Object> getCaseStatusUpdate(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody,String ackNo) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String msg = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		String deptCode = (String) userPrincipal.getDeptCode();
		//deptName = repo.getDeptName(deptId);
		int distCode = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		//String cIno1[] = ackNo.split("@");

		String cInoAll = ackNo;
		String cIno1[] = cInoAll.split("@");
		String cIno = cIno1[0];

		try {

			System.out.println("cIno-0-" + cIno1[0]);
			System.out.println("cIno-1-" + cIno1[1]);


			System.out.println("cIno--------------" +cIno);
			if (cIno != null && !cIno.equals("")) {

				String sqlCondition = "";

				if (abstractReqBody.getDistrictId() != null
						&& !abstractReqBody.getDistrictId().toString().contentEquals("")
						&& !abstractReqBody.getDistrictId().toString().contentEquals("0")) {
					sqlCondition += " and a.distid='" + abstractReqBody.getDistrictId().toString().trim() + "' ";
					abstractReqBody.setDistrictId(abstractReqBody.getDistrictId());
				}

				if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().toString().contentEquals("")
						&& !abstractReqBody.getDeptId().toString().contentEquals("0")) {
					sqlCondition += " and ad.dept_code='" +abstractReqBody.getDeptId().toString().trim() + "' ";
					abstractReqBody.setDeptId(abstractReqBody.getDeptId());
				}

				if (abstractReqBody.getFromDate() != null
						&& !abstractReqBody.getFromDate().toString().contentEquals("")) {
					sqlCondition += " and a.inserted_time::date >= to_date('" +abstractReqBody.getFromDate()
					+ "','dd-mm-yyyy') ";
					abstractReqBody.setFromDate(abstractReqBody.getFromDate());
				}
				if (abstractReqBody.getToDate() != null && !abstractReqBody.getToDate().toString().contentEquals("")) {
					sqlCondition += " and a.inserted_time::date <= to_date('" +abstractReqBody.getToDate()
					+ "','dd-mm-yyyy') ";
					abstractReqBody.setToDate(abstractReqBody.getToDate());
				}
				if (abstractReqBody.getCaseTypeId() != null && !StringUtils.isBlank(abstractReqBody.getCaseTypeId().toString())
						&& !abstractReqBody.getCaseTypeId().toString().contentEquals("0")) {
					sqlCondition += " and a.casetype='" +abstractReqBody.getCaseTypeId().toString().trim() + "' ";
				}

				if (roleId.equals("2")) {
					sqlCondition += " and ad.dist_id='" + distCode + "' ";
					// formBean.setDyna("districtId", distCode);
				}
				if (roleId.equals("3")) {// Secretariat Department
					sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
							+ "') ";
				}
				if (roleId.equals("9")) {// Secretariat Department
					sqlCondition += " and (dmt.dept_code='" + deptCode + "') ";
				}
				if (roleId.equals("8")) {
					sqlCondition += " and ad.case_status='5' ";
					// formBean.setDyna("districtId", distCode);
				}
				if (roleId.equals("11")) {
					sqlCondition += " and ad.case_status='9' ";
					// formBean.setDyna("districtId", distCode);
				}
				if (roleId.equals("12")) {
					sqlCondition += " and ad.case_status='10' ";
					// formBean.setDyna("districtId", distCode);
				}


				String sql = "select slno , a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip, upper(trim(district_name)) as district_name, "
						+ "upper(trim(case_full_name )) as  case_full_name, a.ack_file_path, case when services_id='0' then null else services_id end as services_id,services_flag, "
						+ "to_char(inserted_time,'yyyy-MM-dd') as generated_date, getack_dept_desc(a.ack_no) as dept_descs,ad.assigned,ad.assigned_to,ad.case_status,ad.ecourts_case_status,ad.section_officer_updated,ad.mlo_no_updated  "
						+ "from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no ) "
						+ "left join district_mst dm on (a.distid=dm.district_id) "
						+ "left join dept_new dmt on (ad.dept_code=dmt.dept_code)"
						+ "inner join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name) "
						+ " where a.delete_status is false and ack_type='NEW' and respondent_slno='" + cIno1[1] + "' "
						+ sqlCondition + " and a.ack_no='" + cIno + "'  order by inserted_time desc";

				System.out.println("CASES SQL:" + sql);

				List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
				// System.out.println("data=" + data);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("USERSLIST", data);
				} else {
					map.put("errorMsg", "No details found.");
				}

				// =======================
				System.out.println("---"+data.get(0).get("section_officer_updated"));

				if (data != null && !data.isEmpty()) {
					if (roleId != null && (roleId.equals("4") || roleId.equals("5") || roleId.equals("10"))) {
						map.put("SHOWBACKBTN", "SHOWBACKBTN");
					}

					if (roleId != null && roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {
						if (CommonModels.checkStringObject(data.get(0).get("section_officer_updated")).equals("T")) {
							System.out.println("dept code-3,5:" + deptCode.substring(3, 5));

							if (deptCode.substring(3, 5) == "01" || deptCode.substring(3, 5).equals("01")) {

								map.put("SHOWMLOBTN", "SHOWMLOBTN");
							} else {
								map.put("SHOWNOBTN", "SHOWNOBTN");
							}
						}
					} else if (roleId != null && roleId.equals("4")
							&& CommonModels.checkStringObject(data.get(0).get("mlo_no_updated")).equals("T")) {
						// MLO TO SECT DEPT
						map.put("SHOWSECDEPTBTN", "SHOWSECDEPTBTN");
					} else if (roleId != null && (roleId.equals("5") || roleId.equals("10"))
							&& CommonModels.checkStringObject(data.get(0).get("mlo_no_updated")).equals("T")) {
						// NO TO HOD/DEPT
						map.put("SHOWHODDEPTBTN", "SHOWHODDEPTBTN");
					} else if ((roleId.equals("3") || roleId.equals("9"))
							&& CommonModels.checkStringObject(data.get(0).get("mlo_no_updated")).equals("T")) {

						assignedCasesToSectionController.getGPSList(authentication);

						map.put("SHOWGPBTN", "SHOWGPBTN");
					} else if (roleId.equals("6")) { // GP LOGIN
						map.put("SHOWGPAPPROVEBTN", "SHOWGPAPPROVEBTN");
					}

					
					data=assignedNewCasesToEmpRepo.getActivitiesDataNEW(cIno,Integer.parseInt(cIno1[1]));
					map.put("ACTIVITIESDATA", data);

					map.put("STATUSUPDATEBTN", "STATUSUPDATEBTN");

					List<Map<String, Object>> data_set=assignedNewCasesToEmpRepo.getOLCMSCASEDATANEW(cIno,Integer.parseInt(cIno1[1]));
					map.put("OLCMSCASEDATA", data_set);

					if (data_set != null && !data_set.isEmpty() && data_set.size() > 0) {

						abstractReqBody.setCounterFileCopyOld(data_set.get(0).get("counter_filed_document")+ "");

						map.put("counterFileCopyOld", data_set.get(0).get("counter_filed_document") + "");

						map.put("counterFileCopyOld2",
								data_set.get(0).get("counter_filed_document2") + "");

						map.put("counterFileCopyOld3",
								data_set.get(0).get("counter_filed_document3") + "");

						abstractReqBody.setJudgementOrderOld(CommonModels.checkStringObject(data_set.get(0).get("judgement_order")));
						map.put("judgementOrderOld",
								CommonModels.checkStringObject(data_set.get(0).get("judgement_order")));
						abstractReqBody.setActionTakenOrderOld(CommonModels.checkStringObject(data_set.get(0).get("action_taken_order")));

						map.put("actionTakenOrderOld",
								CommonModels.checkStringObject(data_set.get(0).get("action_taken_order")));

						abstractReqBody.setCounterFiled(data_set.get(0).get("counter_filed")+"");
						map.put("counterFiled",
								CommonModels.checkStringObject(data_set.get(0).get("counter_filed")));
						abstractReqBody.setRemarks(data_set.get(0).get("remarks")+"");
						abstractReqBody.setEcourtsCaseStatus(data_set.get(0).get("ecourts_case_status")+"");

						map.put("ecourtsCaseStatus",
								CommonModels.checkStringObject(data_set.get(0).get("ecourts_case_status")));

						abstractReqBody.setParawiseRemarksSubmitted(data_set.get(0).get("pwr_uploaded")+"");
						map.put("parawiseRemarksSubmitted",
								CommonModels.checkStringObject(data_set.get(0).get("pwr_uploaded")));
						abstractReqBody.setParawiseRemarksCopyOld(data_set.get(0).get("pwr_uploaded_copy")+"");
						map.put("parawiseRemarksCopyOld", data_set.get(0).get("pwr_uploaded_copy") + "");

						map.put("parawiseRemarksCopyOld2", data_set.get(0).get("pwr_uploaded_copy2") + "");

						map.put("parawiseRemarksCopyOld3", data_set.get(0).get("pwr_uploaded_copy3") + "");

						abstractReqBody.setParawiseRemarksDt(data_set.get(0).get("pwr_submitted_date")+ "");
						map.put("parawiseRemarksDt",
								CommonModels.checkStringObject(data_set.get(0).get("pwr_submitted_date")));
						abstractReqBody.setDtPRReceiptToGP(data_set.get(0).get("pwr_received_date")+ "");

						map.put("dtPRReceiptToGP",
								CommonModels.checkStringObject(data_set.get(0).get("pwr_received_date")));
						abstractReqBody.setPwr_gp_approved(data_set.get(0).get("pwr_approved_gp")+ "");

						map.put("pwr_gp_approved",
								CommonModels.checkStringObject(data_set.get(0).get("pwr_approved_gp")));
						abstractReqBody.setDtPRApprovedToGP(data_set.get(0).get("pwr_gp_approved_date")+ "");

						map.put("dtPRApprovedToGP",
								CommonModels.checkStringObject(data_set.get(0).get("pwr_gp_approved_date")));
						abstractReqBody.setAppealFiled(data_set.get(0).get("appeal_filed")+ "");

						map.put("appealFiled",
								CommonModels.checkStringObject(data_set.get(0).get("appeal_filed")));
						abstractReqBody.setAppealFileCopyOld(CommonModels.checkStringObject(data_set.get(0).get("appeal_filed_copy")));
						map.put("appealFileCopyOld",
								CommonModels.checkStringObject(data_set.get(0).get("appeal_filed_copy")));
						abstractReqBody.setAppealFiledDt(data_set.get(0).get("appeal_filed_date")+ "");

						map.put("appealFiledDt",
								CommonModels.checkStringObject(data_set.get(0).get("appeal_filed_date")));
						abstractReqBody.setActionToPerform(data_set.get(0).get("action_to_perfom")+ "");

						map.put("actionToPerform",
								CommonModels.checkStringObject(data_set.get(0).get("action_to_perfom")));

						map.put("STATUSUPDATEBTN", "STATUSUPDATEBTN");
					}
				}
				map.put("HEADING", "Update Status for Case :" + cIno);
			} else {
				map.put("errorMsg", "Invalid Cino. / No Records Found to display.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public String getUpdateCaseDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String msg = "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";


		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String cIno1[] = CommonModels.checkStringObject(abstractReqBody.getFileCino()).split("@");
		Map<String, Object> map = new HashMap<>();
		int a=0;
		String counterFile1 = null;
		String counterFile2 = null;
		String counterFile3 = null;
		String pwrFile1 = null;
		String pwrFile2 = null;
		String pwrFile3 = null;
		//String cIno1[] = CommonModels.checkStringObject(abstractReqBody.getFileCino()).split("@");
		String cIno=cIno1[0];
		int resident_id=Integer.parseInt(cIno1[1]);
		//int resident_id = abstractReqBody.getResident_id();
		try {

			System.out.println("resident_id--" + resident_id);
			System.out.println("updateCaseDetails---" + cIno);

			if (cIno != null && !cIno.equals("")) {

				String petition_document = "", counter_filed_document = "", pwr_uploaded_copy = "";

				// FormFile myDoc;
				String updateSql = "";
				String actionPerformed = "";
				String remarks = CommonModels.checkStringObject(abstractReqBody.getRemarks()).replace("'", "");

				actionPerformed = !CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("")
						? abstractReqBody.getActionToPerform().toString()
								: "CASE DETAILS UPDATED";

				List<Map<String, Object>> data_set=assignedNewCasesToEmpRepo.getOLCMSCASEDATANEW(cIno,Integer.parseInt(cIno1[1]));
				
				// STATUS CLOSED
				if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("Closed")) {
					if (abstractReqBody.getActionTakenOrder() != null
							&& !abstractReqBody.getActionTakenOrder().toString().equals("")) {

						System.out.println("file  " +abstractReqBody.getActionTakenOrder());

						String photofile = abstractReqBody.getActionTakenOrder();
						System.out.println("ActionTakenOrder is" + abstractReqBody.getActionTakenOrder());
						if (photofile.isEmpty() || photofile == null) {

							System.out.println("old pdf is" + data_set.get(0).get("action_taken_order"));

							abstractReqBody.setActionTakenOrder(data_set.get(0).get("action_taken_order"));
						}  
						updateSql += ", action_taken_order='" + abstractReqBody.getActionTakenOrder()+ "'";

						a = assignedNewCasesToEmpRepo.insertCase_activities(cIno, userId,
								request.getRemoteAddr(), remarks, abstractReqBody.getActionTakenOrder());
					}

					if (abstractReqBody.getJudgementOrder() != null
							&& !abstractReqBody.getJudgementOrder().toString().equals("")) {

						String photofile = abstractReqBody.getJudgementOrder();
						if (photofile.isEmpty() || photofile == null) {

							System.out.println("old pdf is" + data_set.get(0).get("judgement_order"));
							abstractReqBody.setJudgementOrder(data_set.get(0).get("judgement_order"));

						}  
						updateSql += ", judgement_order='" + abstractReqBody.getJudgementOrder() + "'";

						a = assignedNewCasesToEmpRepo.insertBack_case_activitiesClosedJudgementOrder(cIno, userId,
								request.getRemoteAddr(), remarks, abstractReqBody.getJudgementOrder());


					}

					if (abstractReqBody.getAppealFiled() != null
							&& abstractReqBody.getAppealFiled().toString().equals("Yes")
							&& abstractReqBody.getAppealFileCopy() != null
							&& !abstractReqBody.getAppealFileCopy().toString().equals("")) {

						String photofile = abstractReqBody.getAppealFileCopy();
						if (photofile.isEmpty() || photofile == null) {

							abstractReqBody.setAppealFileCopy(data_set.get(0).get("appeal_filed_copy"));
						}  
						updateSql += ", appeal_filed_copy='" + abstractReqBody.getAppealFileCopy()+ "'";


						a = assignedNewCasesToEmpRepo.insertBack_case_activitiesClosedAppealFileCopy(cIno, userId,
								request.getRemoteAddr(), remarks, abstractReqBody.getAppealFileCopy());
					}

					if (assignedNewCasesToEmpRepo.olcms_case_detailsCount(cIno,resident_id) > 0) { 

						a=assignedNewCasesToEmpRepo.insert_case_details_log(cIno,resident_id);

						a = assignedNewCasesToEmpRepo.update_ecourts_olcms_case_details(
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getAppealFiled(),
								abstractReqBody.getAppealFiledDt(), remarks, userId,
								abstractReqBody.getActionToPerform(), abstractReqBody.getPetitionDocument(),
								abstractReqBody.getActionTakenOrder(), abstractReqBody.getJudgementOrder(),
								abstractReqBody.getAppealFileCopy(), cIno,resident_id);

					} else {

						a = assignedNewCasesToEmpRepo.insert_ecourts_olcms_case_details(cIno,
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getPetitionDocument(),
								abstractReqBody.getAppealFiled(), abstractReqBody.getAppealFileCopy(),
								abstractReqBody.getJudgementOrder(), abstractReqBody.getActionTakenOrder(), userId,
								remarks, abstractReqBody.getAppealFiledDt(), abstractReqBody.getActionToPerform(),resident_id);
					}

					a += assignedNewCasesToEmpRepo.update_ecourts_gpo_ack_depts(abstractReqBody.getEcourtsCaseStatus(),
							cIno,deptCode,resident_id); 


					a += assignedNewCasesToEmpRepo.insert_closedFinal_ecourts_case_activities(cIno, actionPerformed,
							userId, request.getRemoteAddr(), remarks); 

					if (a > 0) {
						msg="Case details updated successfully for Ack No :" + cIno;

					} else {

						msg="Error while updating the case details for Ack No :" + cIno;
					}
				}

				else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("Pending")) {
					List<Map<String, Object>> data = assignedNewCasesToEmpRepo.getOLCMSCASEDATAForUpdate(cIno,resident_id);
					if (abstractReqBody.getCounterFiled() != null
							&& abstractReqBody.getCounterFiled().toString().equals("Yes")) {

						List<String> file = abstractReqBody.getCounterFileCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   counter");
							doc1 = file.get(0);

						} else {

							System.out.println("No uploaded files   counter");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document") !=null) {
								doc1 = (String) data.get(0).get("counter_filed_document");

								System.out.println("counterFile1 1: ====" + doc1);

							}

							abstractReqBody.setCounterFileCopy(file);
						}

						if (file != null && file.size() > 1 && file.get(1) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   counter");

							doc2 = file.get(1);

						} else {

							System.out.println("No uploaded files   counter");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document2") !=null) {

								doc2 = (String) data.get(0).get("counter_filed_document2");

								System.out.println("counterFile2 2: ==" + doc2);
							}

							abstractReqBody.setCounterFileCopy(file);
						}

						if (file != null && file.size() > 2 && file.get(2) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   counter");

							doc3 = file.get(2);
						} else {

							System.out.println("No uploaded files   counter");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document3") !=null) {

								doc3 = (String) data.get(0).get("counter_filed_document3");

								System.out.println("counterFile3 3: ====" + doc3);
							}

							abstractReqBody.setCounterFileCopy(file);
						}

						// Final assignment
						counterFile1 = doc1;
						counterFile2 = doc2;
						counterFile3 = doc3;

						System.out.println("counterFile1 1: " + counterFile1);
						System.out.println("counterFile2 2: " + counterFile2);
						System.out.println("counterFile3 3: " + counterFile3);

						a += assignedNewCasesToEmpRepo.insert_PendingCounterEcourts_case_activities(cIno, userId,
								request.getRemoteAddr(), remarks, counterFile1);
					}  

					if (abstractReqBody.getParawiseRemarksSubmitted() != null
							&& abstractReqBody.getParawiseRemarksSubmitted().toString().equals("Yes")) {

						List<String> file = abstractReqBody.getParawiseRemarksCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");
							doc1 = file.get(0);

						} else {
							System.out.println("No uploaded files   pwr");
							// No uploaded files – fallback to database
							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy") !=null) {
								doc1 = (String) data.get(0).get("pwr_uploaded_copy");

								System.out.println("pwrFile1 1:================ " + doc1);
							}
						}

						if (file != null && file.size() > 1 && file.get(1) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");

							doc2 = file.get(1);
						} else {
							System.out.println("No uploaded files   pwr");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy2") !=null) {

								doc2 = (String) data.get(0).get("pwr_uploaded_copy2");

								System.out.println("pwrFile2 2:================ " + doc2);
							}
						}

						if (file != null && file.size() > 2 && file.get(2) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");

							doc3 = file.get(2);

						} else {
							System.out.println("No uploaded files   pwr");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy3") !=null) {

								doc3 = (String) data.get(0).get("pwr_uploaded_copy3");

								System.out.println("pwrFile3 3:===============" + doc3);
							}

						}
						// Assign final values
						pwrFile1 = doc1;
						pwrFile2 = doc2;
						pwrFile3 = doc3;

						System.out.println("pwrFile1 1: " + pwrFile1);
						System.out.println("pwrFile2 2: " + pwrFile2);
						System.out.println("pwrFile3 3: " + pwrFile3);

						a = assignedNewCasesToEmpRepo.insert_PendingPWREcourts_case_activities(cIno, userId,
								request.getRemoteAddr(), remarks, pwrFile1);
					}  

					System.out.println(" Date: " + abstractReqBody.getRelatedGp());
					System.out.println(" Date: " +abstractReqBody.getDtPRReceiptToGP());
					System.out.println(" Date: " +abstractReqBody.getDtPRApprovedToGP());
					System.out.println(" Date: " +abstractReqBody.getParawiseRemarksDt());


					if (assignedNewCasesToEmpRepo.getCinoCount2(cIno,resident_id) > 0) {

						a += assignedNewCasesToEmpRepo.insert_Pending_ecourts_olcms_case_details_log(cIno,resident_id);

						a += assignedNewCasesToEmpRepo.update_Pending_ecourts_olcms_case_details(
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getCounterFiled(), remarks,
								abstractReqBody.getPetitionDocument(), userId, counterFile1, counterFile2,
								counterFile3, abstractReqBody.getCounterFiledDt(), pwrFile1, pwrFile2, pwrFile3,
								abstractReqBody.getRelatedGp(), abstractReqBody.getParawiseRemarksSubmitted(),
								abstractReqBody.getParawiseRemarksDt(), abstractReqBody.getDtPRReceiptToGP(),
								abstractReqBody.getDtPRApprovedToGP(), abstractReqBody.getActionToPerform(), cIno,resident_id);

						System.out.println("update condition-------------"+a);

					} else {
						System.out.println("else condition-------------"+a);

						a += assignedNewCasesToEmpRepo.insert_Pending_ecourts_olcms_case_details(cIno,
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getPetitionDocument(),
								abstractReqBody.getCounterFiledDt(), counterFile1, counterFile2, counterFile3,
								userId, abstractReqBody.getCounterFiled(), abstractReqBody.getRemarks(),
								abstractReqBody.getRelatedGp(), abstractReqBody.getParawiseRemarksSubmitted(),
								abstractReqBody.getParawiseRemarksDt(), abstractReqBody.getDtPRReceiptToGP(),
								abstractReqBody.getDtPRApprovedToGP(), pwrFile1, pwrFile2, pwrFile3,
								abstractReqBody.getActionToPerform(),resident_id);
					}

					System.out.println("Inserting file SQL$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + a);
					System.out.println("counterFiled------------"
							+ CommonModels.checkStringObject(counterFile1));

					System.out.println("a-----2------------" + a);
					if (roleId != null && (roleId.equals("4") || roleId.equals("5") || roleId.equals("10"))) {// MLO /

						a += assignedNewCasesToEmpRepo.update_Pending_ecourts_gpo_ack_depts(abstractReqBody.getEcourtsCaseStatus(), cIno,deptCode,resident_id);
						System.out.println("a-----3------------" + a);
					}

					else {

						a += assignedNewCasesToEmpRepo.update_Pending_ecourts_gpo_ack_deptsELSE(abstractReqBody.getEcourtsCaseStatus(), cIno,deptCode,resident_id);
					}


					a += assignedNewCasesToEmpRepo.insert_PendingFINALEcourts_case_activities(cIno,
							actionPerformed, userId, request.getRemoteAddr(), remarks);

					System.out.println("a----5-------------" + a);
					if (a > 0) {

						msg = "Case details updated successfully for Ack No :" + cIno;

					} else {

						msg = "Error while updating the case details for Ack No :" + cIno;
					}

				} 
				else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("Private")) {


					if (roleId != null && (roleId.equals("4") || roleId.equals("5") || roleId.equals("10"))) {// MLO / // NO / // Dist-NO

						a += assignedNewCasesToEmpRepo.update_ecourts_gpo_ack_deptsPrivate(
								abstractReqBody.getEcourtsCaseStatus(), cIno, deptCode);
					}

					System.out.println("a-sql-----------" + a);

					a += assignedNewCasesToEmpRepo.insert_ecourts_case_activitiesPrivate(cIno, actionPerformed,
							userId, request.getRemoteAddr(), remarks);

					System.out.println("a----5-------------" + a);

					if (a > 0) {
						msg = "Case details updated successfully for Ack No :" + cIno;

					} else {

						msg = "Error while updating the case details for Ack No :" + cIno;
					}

				}else {
					msg = "Error Please Check Details.Error while updating the case details for Ack No :" + cIno;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public String getforwardCaseDetailsNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request ) {

		String userId = null, roleId = null, deptCode = "", msg = "";
		// int distId=0;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		Integer distId = userPrincipal.getDistId();

		String empId = abstractReqBody.getEmployeeId();
		String empSection = abstractReqBody.getEmpSection();
		String empPost = abstractReqBody.getEmpPost();

		System.out.println("deptCode------" + deptCode);
		int newStatus = 0;
		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);
		String fwdOfficer = "";


		//int resident_id = abstractReqBody.getResident_id();
		int a=0;
		String cIno1[] = abstractReqBody.getFileCino().split("@");
		String cIno=cIno1[0];
		int resident_id=Integer.parseInt(cIno1[1]);
		try {
			System.out.println("Cino----start----------" + cIno1[0]+resident_id);
			System.out.println("Cino----resident_id----------" +resident_id);
			if (cIno != null && !cIno.equals("")) {

				System.out.println("deptCode::" + deptCode.substring(3, 5));

				if (roleId != null && roleId.equals("4")) {// FROM MLO to SECT DEPT.
					fwdOfficer = deptCode;
					newStatus = 1;

					a = assignedNewCasesToEmpRepo.ecourts_case_data_frwd_Sec(newStatus, fwdOfficer, cIno,deptCode,resident_id);

					System.out.println("frwd_Sec---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to Secretary. Department";


				} else if (roleId != null && roleId.equals("5")) {// FROM NO TO HOD
					fwdOfficer = deptCode;
					newStatus = 3;

					a = assignedNewCasesToEmpRepo.ecourts_case_data_frwd_Hod(newStatus, fwdOfficer, cIno,deptCode,resident_id);

					System.out.println("frwd_Hod---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to HOD.";

				} else if (roleId != null && roleId.equals("10")) {// FROM Dist-NO TO HOD
					fwdOfficer = deptCode;
					newStatus = 3;
					msg = "Case details (" + cIno + ") forwarded successfully to HOD.";

					a = assignedNewCasesToEmpRepo.ecourts_case_data_frwd_DcToHod(newStatus, fwdOfficer, cIno,deptCode,resident_id);

				}

				else if ((roleId != null && roleId.equals("8"))
						&& (deptCode.substring(3, 5) == "01" || deptCode.substring(3, 5).equals("01"))) { // FROM
					// SECTION-SECT.
					fwdOfficer = assignedNewCasesToEmpRepo.getfwdOfficerMlo(deptCode);												// TO MLO

					newStatus = 2;
					msg = "Case details (" + cIno + ") forwarded successfully to MLO.";

					a = assignedNewCasesToEmpRepo.getFwrdOffMlo(newStatus, fwdOfficer, cIno,deptCode,resident_id);

				} else if (roleId != null && roleId.equals("11")) {// FROM SECTION(HOD) TO NO-HOD

					fwdOfficer = assignedNewCasesToEmpRepo.getgetfwdOfficerNHOD(deptCode);	

					newStatus = 4;
					msg = "Case details (" + cIno + ") forwarded successfully to Nodal Officer.";

					a = assignedNewCasesToEmpRepo.getFwrdOffNHOD(newStatus, fwdOfficer, cIno,deptCode,resident_id);

				} else if (roleId != null && roleId.equals("12")) {// FROM SECTION(DIST) TO NO-HOD-DIST

					fwdOfficer = assignedNewCasesToEmpRepo.getfwdOfficerNHODDIST(deptCode,distId);	

					newStatus = 8;
					msg = "Case details (" + cIno + ") forwarded successfully to Nodal Officer.";

					a = assignedNewCasesToEmpRepo.getFwrdOffNHODDIST(newStatus, fwdOfficer, cIno,deptCode,resident_id);
				}

				System.out.println("################" + a);
				if (a > 0) {

					a = assignedNewCasesToEmpRepo.Insert_ecourts_case_activitiesFinalFrwd(cIno, userId,
							request.getRemoteAddr(), fwdOfficer, abstractReqBody.getRemarks());


					a = assignedNewCasesToEmpRepo.Insert_ecourts_ack_assignment_dtlsFinalFrwd(cIno, deptCode,
							empSection, empPost, empId, new Timestamp(new Date().getTime()),request.getRemoteAddr(), userId, fwdOfficer);
				} else {

					msg="Error while forwarding the case details (" + cIno + ").";
				}
			} else {
				msg="Error Invalid Cino :" + cIno;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public String getsendBackCaseDetailsNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request ) {

		String userId = null, roleId = null, deptCode = "", msg = "" , msgS="";
		// int distId=0;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		System.out.println("deptCode------" + deptCode);
		int newStatus = 0;
		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);

		String cIno1[] = abstractReqBody.getFileCino().split("@");
		String cIno=cIno1[0];
		int resident_id=Integer.parseInt(cIno1[1]);
		int a=0;
		try {
			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {

				String backToSectionUser = assignedNewCasesToEmpRepo.getbackToSectionUser(cIno);

				System.out.println("sendBackCaseDetails--sql---" + backToSectionUser + "-" + roleId);


				if (roleId != null && roleId.equals("4")) {// FROM MLO to Section.
					newStatus = 5;
					msgS = "Case details (" + cIno + ") returned back to section successfully";

					a += assignedNewCasesToEmpRepo.update_ecourts_case_data_bck_MloToSection(newStatus,
							backToSectionUser, cIno,deptCode,resident_id);

				} else if (roleId != null && roleId.equals("5")) {// FROM NO TO Section
					newStatus = 9;
					msgS = "Case details (" + cIno + ") returned back to section successfully";

					a += assignedNewCasesToEmpRepo.update_ecourts_case_data_bck_NoToSection(newStatus,
							backToSectionUser, cIno,deptCode,resident_id);

				} else if (roleId != null && roleId.equals("10")) {// FROM Dist-NO TO Dist-Section
					newStatus = 10;
					msgS = "Case details (" + cIno + ") returned back to section successfully";

					a += assignedNewCasesToEmpRepo.update_ecourts_case_data_bck_DNoToDSection(newStatus,
							backToSectionUser, cIno,deptCode,resident_id);
				}

				System.out.println("a:" + a+"---roleId---  "+roleId);

				if (a > 0) {

					a += assignedNewCasesToEmpRepo.Insert_ecourts_case_activitiesFinalSendBack(cIno, userId,
							request.getRemoteAddr(), backToSectionUser, abstractReqBody.getRemarks());

					msg =  msgS;

				} else {

					msg = "Error while forwarding the case details (" + cIno + ").";
				}
			} else {
				msg = "Error Invalid Cino :" + cIno;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	@Override
	public String getforwardCaseDetails2GPNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request ) {

		String userId = null, roleId = null, deptCode = "", msg = "" , msgS="";
		// int distId=0;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		System.out.println("deptCode------" + deptCode);
		int newStatus = 0;
		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);

		String cIno1[] = abstractReqBody.getFileCino().split("@");
		String cIno=cIno1[0];
		int resident_id=Integer.parseInt(cIno1[1]);
		int a=0;
		System.out.println("file no=========="+cIno);
		try {
			System.out.println("Cino----start----------" + cIno);
			System.out.println("Cino----resident_id----------" + resident_id);
			if (cIno != null && !cIno.equals("")) {
				// System.out.println("counterRemarks::" +
				// formBean.getDyna("ecourtsCaseStatus"));

				System.out.println("deptCode::" + deptCode.substring(3, 5));
				String fwdOfficer = abstractReqBody.getGpCode();
				System.out.println("fwdOfficer---------" + fwdOfficer);

				if (roleId != null && roleId.equals("3")) {// FROM SECT DEPT TO GP.
					newStatus = 6;
					msgS = "Case details (" + cIno + ") forwarded successfully to GP for Approval.";

					a = assignedNewCasesToEmpRepo.ecourts_case_data_frwd_secToGp(newStatus, fwdOfficer, cIno,deptCode,resident_id);

				} else if (roleId != null && roleId.equals("9")) {// FROM HOD TO GP
					newStatus = 6;
					msgS = "Case details (" + cIno + ") forwarded successfully to GP for Approval.";

					a = assignedNewCasesToEmpRepo.ecourts_case_data_frwd_HodToGp(newStatus, fwdOfficer, cIno,deptCode,resident_id);
				}
				System.out.println("Executing query: UPDATE ecourts_gpo_ack_depts SET case_status = " + newStatus
						+ ", assigned_to = '" + fwdOfficer + "'"
						+ " WHERE ack_no = '" + cIno + "'"
						+ " AND section_officer_updated = 'T'"
						+ " AND mlo_no_updated = 'T'"
						+ " AND case_status = '3'"
						+ " AND dept_code = '" + deptCode + "'"
						+ " AND respondent_slno = " + resident_id);

				System.out.println("SQL:" + a);

				if (a > 0) {

					a = assignedNewCasesToEmpRepo.Insert_ecourts_case_activitiesFinalFrwd2GP(cIno, userId,
							request.getRemoteAddr(), fwdOfficer, abstractReqBody.getRemarks());

					msg=msgS;

				} else {

					msg = "Error while forwarding the case details (" + cIno + ").";
				}
			} else {
				msg = "Error Invalid Cino :" + cIno;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public String getGpApproveNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request ) {

		String userId = null, roleId = null, deptCode = "", msg = "" , msgS="";

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String cIno1[] = abstractReqBody.getFileCino().split("@");
		String cIno=cIno1[0];
		int resident_id=Integer.parseInt(cIno1[1]);
		int a=0;
		System.out.println("file no=========="+cIno);
		try {
			System.out.println("Cino----start----------" + cIno);
			System.out.println("Cino----resident_id----------" + resident_id);
			if (cIno != null && !cIno.equals("")) {

				String actionToPerform = abstractReqBody.getActionToPerform();
				String actionPerformed = (!actionToPerform.isEmpty() && !actionToPerform.equals("0"))
						? actionToPerform + " Approved"
								: "CASE DETAILS UPDATED";

				msg = "Case details (" + cIno + ") updated successfully.";

				a += assignedNewCasesToEmpRepo.INSERTecourts_olcms_case_details_log(cIno,resident_id);

				if (abstractReqBody.getActionToPerform().toString().equals("Parawise Remarks")) {

					a = assignedNewCasesToEmpRepo.update_ecourts_olcms_case_detailsPWRAPPROVE(cIno,resident_id);

					msg = "Parawise Remarks Approved successfully for Case (" + cIno + ").";
				} else if (abstractReqBody.getActionToPerform().toString().equals("Counter Affidavit")) {

					msg = "Counter Affidavit Approved successfully for Case (" + cIno + ").";

					a = assignedNewCasesToEmpRepo.update_ecourts_olcmsCOUNTERAFFIDAVITAPPROVE(userId,cIno,resident_id);
				}else if (abstractReqBody.getCounterFiled().equals("Yes")) {

					a = assignedNewCasesToEmpRepo.update_ecourts_olcmsCounterFiled(userId,cIno,resident_id);
					msg = "Counter Affidavit Approved successfully for Case (" + cIno + ").";

				} else if (abstractReqBody.getCounterFiled().equals("No")
						&& abstractReqBody.getParawiseRemarksSubmitted().equals("Yes")) {

					a = assignedNewCasesToEmpRepo.update_ecourts_olcmsParawiseRemarksSubmitted(cIno,resident_id);

					msg = "Parawise Remarks Approved successfully for Case (" + cIno + ").";

				}
				System.out.println("a===========>"+a);
				if (a > 0) {

					a = assignedNewCasesToEmpRepo.Insert_ecourts_case_activitiesFinalAPPROVE(cIno, actionPerformed,
							userId, request.getRemoteAddr(), abstractReqBody.getRemarks());
					msg= msgS;

				} else {

					msg="Error while forwarding the case details (" + cIno + ").";
				}
			} else {
				msg="Error Invalid Cino :" + cIno;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}


}