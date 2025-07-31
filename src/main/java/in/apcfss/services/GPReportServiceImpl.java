package in.apcfss.services;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import in.apcfss.common.CommonModels;
import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.repositories.GPReportRepo;
import in.apcfss.repositories.LegacyAssignmentRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class GPReportServiceImpl implements GPReportService {

	@Autowired
	HttpServletRequest request;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Autowired
	CommonMethodService service;

	@Autowired
	AssignedCasesToSectionService AssignedCaseservice;


	@Autowired
	AssignedCasesToSectionRepo assignedCasesToSectionRepo;

	@Autowired
	GPReportRepo gpReportRepo;


	@Override
	public List<Map<String, Object>> CaseWiseDataGPCases(String roleId, Authentication authentication,
			String pwCounterFlag) {
		String  condition = "",  sql = "", heading = "", 
				Condition1="";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		if (roleId != null && roleId.equals("6")) { // GPO

			String counter_pw_flag =pwCounterFlag;

			condition = " and a.case_status=6 and e.gp_id='" + userId + "' ";

			if (userId.equals("public-prosecutor@ap.gov.in")) {

				Condition1 = " and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' ";
			}

			if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

				Condition1 = " and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA'  ";
			}

			if (counter_pw_flag.equals("PR")) {
				heading = "Parawise Remarks submitted Cases List";

				condition += " and (pwr_uploaded='No' or pwr_uploaded='Yes') and (coalesce(pwr_approved_gp,'0')='0' or coalesce(pwr_approved_gp,'No')='No' )";

			}
			if (counter_pw_flag.equals("COUNTER")) {
				heading = "Counters filed Cases List";

				condition += " and pwr_uploaded='Yes' and coalesce(pwr_approved_gp,'No')='Yes' and (counter_filed='No' or counter_filed='Yes') and coalesce(counter_approved_gp,'F')='F'";

			}
		}

		sql = "select type_name_reg,'Legacy' as legacy_ack_flag, reg_no, reg_year, to_char(dt_regis,'dd-mm-yyyy') as dt_regis, a.cino,pet_name, "
				+ "case when length(scanned_document_path) > 10 then scanned_document_path else '-' end as scanned_document_path ,"
				+ "(case when pend_disp='P' then 'Pending' else 'Disposed' end) as status, "
				+ "(case when (category_service='NON-SERVICE' or a.category_service is null or a.category_service=' ') then 'NON-SERVICE' else 'SERVICE' end) as servicetype from ecourts_case_data a "
				+ " inner join ecourts_olcms_case_details od on (a.cino=od.cino)"
				+ " inner join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code and a.assigned_to=e.gp_id) "
				+ " inner join dept_new d on (a.dept_code=d.dept_code) " + " where assigned=true " + condition
				+ " and coalesce(a.ecourts_case_status,'')!='Closed' " + Condition1 + " ";

		sql += "order by reg_year,type_name_reg,reg_no";
		map.put("HEADING", heading);

		System.out.println("Gp common Leg SQL:" + sql);
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public List<Map<String, Object>> CaseWiseDataNewGPCases(String roleId, Authentication authentication,
			String pwCounterFlag) {
		String   condition = "",   heading = "",  
				Condition1="";
		Map<String, Object> map = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		if (userId.equals("public-prosecutor@ap.gov.in")) {

			Condition1 = " and  ack_type='NEW' and inserted_by like 'PP%'  ";
		}

		if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

			Condition1 = " and  ack_type='NEW' and inserted_by like 'PP%'   ";
		}

		if (roleId != null && roleId.equals("6")) { // GPO

			String counter_pw_flag =pwCounterFlag;

			condition = " and a.case_status=6 and e.gp_id='" + userId + "' ";

			if (counter_pw_flag.equals("PR")) {
				heading = "Parawise Remarks submitted Cases List";

				condition += " and (pwr_uploaded='No' or pwr_uploaded='Yes') and (coalesce(pwr_approved_gp,'0')='0' or coalesce(pwr_approved_gp,'No')='No' )";

			}
			if (counter_pw_flag.equals("COUNTER")) {
				heading = "Counters filed Cases List";

				condition += " and pwr_uploaded='Yes' and coalesce(pwr_approved_gp,'No')='Yes' and (counter_filed='No' or counter_filed='Yes') and coalesce(counter_approved_gp,'F')='F'";

			}
		}

		String sql1 = "select (select case_full_name from case_type_master ctm where ctm.sno::text=b.casetype::text) as type_name_reg,'New' as legacy_ack_flag, reg_no, petitioner_name,reg_year, "
				+ "inserted_time::date as dt_regis, a.ack_no as cino, "
				+ "case when length(ack_file_path) > 10 then ack_file_path else '-' end as scanned_document_path,COALESCE(NULLIF(servicetpye,'null'), 'NON-SERVICES') as servicetype "
				+ " from ecourts_gpo_ack_depts a inner join ecourts_gpo_ack_dtls b on (a.ack_no=b.ack_no)"
				+ " inner join ecourts_olcms_case_details od on (a.ack_no=od.cino )"  //--and od.respondent_slno=a.respondent_slno
				+ " inner join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code and a.assigned_to=e.gp_id) "
				+ " inner join dept_new d on (a.dept_code=d.dept_code)  where assigned=true " + condition
				+ " and coalesce(a.ecourts_case_status,'')!='Closed' " + Condition1 + " ";

		sql1 += "order by reg_year,type_name_reg,reg_no";
		map.put("HEADING", heading);

		System.out.println("gp New SQL:" + sql1);
		return jdbcTemplate.queryForList(sql1);
	}

	@Override
	public Map<String, Object> getcaseStatusUpdateGPReport(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody, String caseNo,
			String caseType) {

		Map<String, Object> map = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		List<Map<String, Object>> data = new ArrayList<>();
		try {

			String cIno = caseNo;

			if (cIno != null && !cIno.equals("") && caseType.equals("Legacy")) {

				System.out.println("IN CASE STATUS UPDATE METHOD :" + cIno);

				data = AssignedCaseservice.getCaseData(cIno);

				String case_no = null;
				if (data != null && !data.isEmpty() && data.size() > 0) {

					case_no = (data.get(0)).get("type_name_reg") + "/" + (data.get(0)).get("reg_no") + "/"
							+ (data.get(0)).get("reg_year");
					System.out.println("case_no--->" + case_no);
					if (roleId != null && (roleId.equals("4") || roleId.equals("5") || roleId.equals("10"))) {
						map.put("SHOWBACKBTN", "SHOWBACKBTN");
					}

					if (roleId != null && roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {
						if ((data.get(0).get("section_officer_updated")).equals("T")) {
							System.out.println("dept code-3,5:" + deptCode.substring(3, 5));

							if (deptCode.substring(3, 5) == "01" || deptCode.substring(3, 5).equals("01")) {

								map.put("SHOWMLOBTN", "SHOWMLOBTN");
							} else {
								map.put("SHOWNOBTN", "SHOWNOBTN");
							}
						}
					} else if (roleId != null && roleId.equals("4")
							&& (data.get(0).get("mlo_no_updated")).equals("T")) {
						// MLO TO SECT DEPT
						map.put("SHOWSECDEPTBTN", "SHOWSECDEPTBTN");
					} else if (roleId != null && (roleId.equals("5") || roleId.equals("10"))
							&& (data.get(0).get("mlo_no_updated")).equals("T")) {
						// NO TO HOD/DEPT
						map.put("SHOWHODDEPTBTN", "SHOWHODDEPTBTN");
					} else if ((roleId.equals("3") || roleId.equals("9"))
							&& (data.get(0).get("mlo_no_updated")).equals("T")) {

						//abstractReqBody.set("GPSLIST", jdbcTemplate.queryForList(sql));

						getGPSList();
						System.out.println("gpslist---" +getGPSList());

						map.put("SHOWGPBTN", "SHOWGPBTN");
					} else if (roleId.equals("6")) { // GP LOGIN
						map.put("SHOWGPAPPROVEBTN", "SHOWGPAPPROVEBTN");
					}
					map.put("USERSLIST", data);
				}

				data = AssignedCaseservice.getActlist(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("actlist", data);
				}

				data = AssignedCaseservice.getOtherDocumentsList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("otherDocumentsList", data);
				}

				data = AssignedCaseservice.getMappedInstructionList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("MappedInstructionList", data);
				}

				data = AssignedCaseservice.getMappedPWRList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("MappedPWRList", data);
				}


				data = AssignedCaseservice.getOrderList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("orderlist", data);
				}

				data = AssignedCaseservice.getIAFILINGLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("IAFILINGLIST", data);
				}

				data = AssignedCaseservice.getINTERIMORDERSLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("INTERIMORDERSLIST", data);
				}

				data = AssignedCaseservice.getLINKCASESLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("LINKCASESLIST", data);
				}

				data = AssignedCaseservice.getOBJECTIONSLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("OBJECTIONSLIST", data);
				}

				data = AssignedCaseservice.getCASEHISTORYLIST(cIno);

				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("CASEHISTORYLIST", data);
				}

				data = AssignedCaseservice.getPETEXTRAPARTYLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("PETEXTRAPARTYLIST", data);
				}

				data = AssignedCaseservice.getRESEXTRAPARTYLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("RESEXTRAPARTYLIST", data);
				}

				data = AssignedCaseservice.getACTIVITIESDATA(cIno);
				map.put("ACTIVITIESDATA", data);

				data = AssignedCaseservice.getOLCMSCASEDATAForUpdate(cIno);
				map.put("OLCMSCASEDATA", data);

				if (data != null && !data.isEmpty()) {
					System.out.println("olcmscasedata    :  " + data);
					System.err.println("-------------hjghfhgfh--------"+ data.get(0).get("action_to_perfom"));

					abstractReqBody.setPetitionDocumentOld( data.get(0).get("petition_document")+"");
					map.put("petitionDocumentOld", data.get(0).get("petition_document"));

					System.out.println("petitionDocumentOld---------" + abstractReqBody.getPetitionDocumentOld());

					abstractReqBody.setCounterFileCopyOld(data.get(0).get("counter_filed_document")+"");

					map.put("counterFileCopyOld", data.get(0).get("counter_filed_document") + "");

					map.put("counterFileCopyOld2", data.get(0).get("counter_filed_document2") + "");


					map.put("counterFileCopyOld3", data.get(0).get("counter_filed_document3") + "");

					abstractReqBody.setJudgementOrderOld(data.get(0).get("judgement_order")+"");
					map.put("judgementOrderOld", data.get(0).get("judgement_order") + "");
					abstractReqBody.setActionTakenOrderOld(data.get(0).get("action_taken_order")+"" );
					map.put("actionTakenOrderOld", data.get(0).get("action_taken_order") + "");

					abstractReqBody.setCounterFiled(data.get(0).get("counter_filed")+"" );

					abstractReqBody.setActionToPerform( data.get(0).get("action_to_perfom")+"");
					abstractReqBody.setEcourtsCaseStatus( data.get(0).get("ecourts_case_status")+"");

					System.out.println("action_to_perfom:" + data.get(0).get("action_to_perfom")+"");

					System.out.println("remarks::" + data.get(0).get("remarks"));

					Object remarksObj = data.get(0).get("remarks");
					String remarks = remarksObj != null ? remarksObj.toString() : null;

					if (remarks != null && !remarks.isEmpty()) {
						String action = String.valueOf(data.get(0).get("action_to_perfom"));

						if ("Parawise Remarks".equals(action)) {
							abstractReqBody.setRemarks(remarks+"");
						} else if ("Counter Affidavit".equals(action)) {
							abstractReqBody.setRemarks2(remarks+"");
						}
					}

					abstractReqBody.setParawiseRemarksSubmitted( data.get(0).get("pwr_uploaded")+"");
					abstractReqBody.setParawiseRemarksCopyOld(data.get(0).get("pwr_uploaded_copy")+"");
					map.put("parawiseRemarksCopyOld", data.get(0).get("pwr_uploaded_copy") + "");

					map.put("parawiseRemarksCopyOld2", data.get(0).get("pwr_uploaded_copy2") + "");

					map.put("parawiseRemarksCopyOld3", data.get(0).get("pwr_uploaded_copy3") + "");

					abstractReqBody.setParawiseRemarksDt(data.get(0).get("pwr_submitted_date")+"");
					abstractReqBody.setDtPRReceiptToGP( data.get(0).get("pwr_received_date")+"");
					abstractReqBody.setPwr_gp_approved(data.get(0).get("pwr_approved_gp")+"");
					abstractReqBody.setDtPRApprovedToGP(data.get(0).get("pwr_gp_approved_date")+"");
					abstractReqBody.setAppealFiled(data.get(0).get("appeal_filed")+"");
					abstractReqBody.setAppealFileCopyOld(data.get(0).get("appeal_filed_copy")+"");
					System.out.println("appealFileCopyOld---------- " + abstractReqBody.getAppealFileCopyOld());
					abstractReqBody.setAppealFiledDt(data.get(0).get("appeal_filed_date")+"");
					abstractReqBody.setActionToPerform( data.get(0).get("action_to_perfom")+"");
					abstractReqBody.setCounter_approved_gp(data.get(0).get("counter_approved_gp")+"");

					System.out.println("actionToPerform:" + abstractReqBody.getActionToPerform());
					System.out.println("counter_approved_gp:" + abstractReqBody.getCounter_approved_gp());
					// 1. View old Parawise Remarks & Enable to Upload Parawise Remarks and send to
					// Department and disable Counter Update.

					System.out.println("Pwr_gp_approved----->>"+abstractReqBody.getPwr_gp_approved());
					if ( abstractReqBody.getActionToPerform().equals("Parawise Remarks")) {
						// a. APPROVED
						if (abstractReqBody.getPwr_gp_approved().equals("Yes")) {
							// disable Submission
							map.put("pwrsuccessMsg", "Parawise Remarks was submitted and approved.");
							map.put("PWRSUBMITION", "DISABLE");
						}
						// b. NOT APPROVED
						else if (abstractReqBody.getPwr_gp_approved().equals("No")) {
							// enable upload & entry.
							map.put("PWRSUBMITION", "ENABLE");
						}
					}
					// 2. View Counter uploaded by Dept. and Disable Parawise Remarks Updation and
					// enable Counter Upload by GP.
					else if ( abstractReqBody.getActionToPerform().equals("Counter Affidavit")) {
						// a. PWR NOT APPROVED
						if ( abstractReqBody.getPwr_gp_approved().equals("No")) {
							map.put("countererrorMsg", "Parawise Remarks was not submitted/approved.");
							map.put("COUNTERSUBMITION", "DISABLE");
						}
						// b. PWR APPROVED COUNTER NOT APPROVED
						else if (abstractReqBody.getPwr_gp_approved().equals("Yes")
								&& ! abstractReqBody.getCounter_approved_gp().equals("T")) {
							map.put("COUNTERSUBMITION", "ENABLE");
						}
						// c. COUNTER APPROVED
						if (abstractReqBody.getPwr_gp_approved().equals("Yes")
								&& (abstractReqBody.getCounter_approved_gp())
								.equals("T")) {
							map.put("countersuccessMsg", "Counter submitted and finalized by GP.");
							map.put("COUNTERSUBMITION", "DISABLE");
						}
					}

					map.put("STATUSUPDATEBTN", "STATUSUPDATEBTN");
				}

				// Dept. Instructions

				data = AssignedCaseservice.getDEPTNSTRUCTIONS(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("DEPTNSTRUCTIONS", data);
				}

				map.put("fileCino", cIno);

				map.put("HEADING", " Details for the Case No. :" + cIno + " Case Reg Number: " + case_no);

			}else if (caseType.equals("New")) {

				System.out.println("NEW CASE STATUS UPDATE METHOD :" + cIno);

				data = AssignedCaseservice.getCaseDataNEW(cIno);

				if (data != null && !data.isEmpty() && data.size() > 0) {

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
							&&  (data.get(0).get("mlo_no_updated")).equals("T")) {
						// MLO TO SECT DEPT
						map.put("SHOWSECDEPTBTN", "SHOWSECDEPTBTN");
					} else if (roleId != null && (roleId.equals("5") || roleId.equals("10"))
							&&  (data.get(0).get("mlo_no_updated")).equals("T")) {
						// NO TO HOD/DEPT
						map.put("SHOWHODDEPTBTN", "SHOWHODDEPTBTN");
					} else if ((roleId.equals("3") || roleId.equals("9"))
							&&  (data.get(0).get("mlo_no_updated")).equals("T")) {
						getGPSList();

						map.put("SHOWGPBTN", "SHOWGPBTN");
					} else if (roleId.equals("6")) { // GP LOGIN
						map.put("SHOWGPAPPROVEBTN", "SHOWGPAPPROVEBTN");
					}
					map.put("USERSLIST", data);
				}

				data = AssignedCaseservice.getActlist(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("actlist", data);
				}

				data = AssignedCaseservice.getOtherDocumentsList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("otherDocumentsList", data);
				}

				data = AssignedCaseservice.getMappedInstructionList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("MappedInstructionList", data);
				}

				data = AssignedCaseservice.getMappedPWRList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("MappedPWRList", data);
				}


				data = AssignedCaseservice.getOrderList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("orderlist", data);
				}

				data = AssignedCaseservice.getIAFILINGLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("IAFILINGLIST", data);
				}

				data = AssignedCaseservice.getINTERIMORDERSLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("INTERIMORDERSLIST", data);
				}

				data = AssignedCaseservice.getLINKCASESLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("LINKCASESLIST", data);
				}

				data = AssignedCaseservice.getOBJECTIONSLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("OBJECTIONSLIST", data);
				}

				data = AssignedCaseservice.getCASEHISTORYLIST(cIno);

				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("CASEHISTORYLIST", data);
				}

				data = AssignedCaseservice.getPETEXTRAPARTYLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("PETEXTRAPARTYLIST", data);
				}

				data = AssignedCaseservice.getRESEXTRAPARTYLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("RESEXTRAPARTYLIST", data);
				}

				data = AssignedCaseservice.getACTIVITIESDATA(cIno);
				map.put("ACTIVITIESDATA", data);

				data = AssignedCaseservice.getOLCMSCASEDATAForUpdate(cIno);
				map.put("OLCMSCASEDATA", data);

				if (data != null && !data.isEmpty()) {

					abstractReqBody.setPetitionDocumentOld(CommonModels.checkStringObject(data.get(0).get("petition_document")));
					map.put("petitionDocumentOld", data.get(0).get("petition_document"));
					System.out.println("petitionDocumentOld---------" + abstractReqBody.getPetitionDocumentOld());

					abstractReqBody.setCounterFileCopyOld( data.get(0).get("counter_filed_document")+"");
					map.put("counterFileCopyOld", data.get(0).get("counter_filed_document"));

					abstractReqBody.setJudgementOrderOld(data.get(0).get("judgement_order")+""  );
					map.put("judgementOrderOld", data.get(0).get("judgement_order") + "");
					abstractReqBody.setActionTakenOrderOld(data.get(0).get("action_taken_order")+"" );
					map.put("actionTakenOrderOld", data.get(0).get("action_taken_order") + "");

					abstractReqBody.setCounterFiled(data.get(0).get("counter_filed")+"");
					System.out.println("action_to_perfom:" + data.get(0).get("action_to_perfom"));
					System.out.println("action_to_perfom:" + data.get(0).get("action_to_perfom"));
					System.out.println("remarks::" + data.get(0).get("remarks"));

					Object remarksObj = data.get(0).get("remarks");
					String remarks = remarksObj != null ? remarksObj.toString() : null;

					if (remarks != null && !remarks.isEmpty()) {
						String action = String.valueOf(data.get(0).get("action_to_perfom"));

						if ("Parawise Remarks".equals(action)) {
							abstractReqBody.setRemarks(remarks+"");
						} else if ("Counter Affidavit".equals(action)) {
							abstractReqBody.setRemarks2(remarks+"");
						}
					}

					abstractReqBody.setEcourtsCaseStatus(data.get(0).get("ecourts_case_status")+"");
					// formBean.setDyna("relatedGp" , data.get(0).get("corresponding_gp"));
					abstractReqBody.setParawiseRemarksSubmitted(data.get(0).get("pwr_uploaded")+"");
					abstractReqBody.setParawiseRemarksCopyOld(CommonModels.checkStringObject(data.get(0).get("pwr_uploaded_copy")));
					map.put("parawiseRemarksCopyOld", data.get(0).get("pwr_uploaded_copy") + "");
					abstractReqBody.setParawiseRemarksDt(data.get(0).get("pwr_submitted_date")+"");
					abstractReqBody.setDtPRReceiptToGP(data.get(0).get("pwr_received_date")+"");
					abstractReqBody.setPwr_gp_approved(data.get(0).get("pwr_approved_gp")+"");
					abstractReqBody.setDtPRApprovedToGP( data.get(0).get("pwr_gp_approved_date")+"");
					abstractReqBody.setAppealFiled(data.get(0).get("appeal_filed")+"");
					abstractReqBody.setAppealFileCopyOld(CommonModels.checkStringObject(data.get(0).get("appeal_filed_copy")+""));
					abstractReqBody.setAppealFiledDt(data.get(0).get("appeal_filed_date")+"");
					abstractReqBody.setActionToPerform(data.get(0).get("action_to_perfom")+"");
					abstractReqBody.setCounter_approved_gp(data.get(0).get("counter_approved_gp")+"");

					System.out.println("actionToPerform:" + abstractReqBody.getActionToPerform());
					System.out.println("counter_approved_gp:" + abstractReqBody.getCounter_approved_gp());
					// 1. View old Parawise Remarks & Enable to Upload Parawise Remarks and send to
					// Department and disable Counter Update.
					if (CommonModels.checkStringObject(abstractReqBody.getActionToPerform())
							.equals("Parawise Remarks")) {
						// a. APPROVED
						System.out.println("--------------------pr");
						if (abstractReqBody.getPwr_gp_approved().equals("Yes")) {
							// disable Submission
							map.put("pwrsuccessMsg", "Parawise Remarks was submitted and approved.");
							map.put("PWRSUBMITION", "DISABLE");
						}
						// b. NOT APPROVED
						else if (abstractReqBody.getPwr_gp_approved().equals("No")) {
							// enable upload & entry.
							map.put("PWRSUBMITION", "ENABLE");
						}
					}
					// 2. View Counter uploaded by Dept. and Disable Parawise Remarks Updation and
					// enable Counter Upload by GP.
					else if (CommonModels.checkStringObject(abstractReqBody.getActionToPerform())
							.equals("Counter Affidavit")) {
						// a. PWR NOT APPROVED
						System.out.println("--------------------ca");
						if (CommonModels.checkStringObject(abstractReqBody.getPwr_gp_approved()).equals("No")) {
							map.put("countererrorMsg", "Parawise Remarks was not submitted/approved.");
							System.out.println("DISABLE");
							map.put("COUNTERSUBMITION", "DISABLE");
						}
						// b. PWR APPROVED COUNTER NOT APPROVED
						else if (CommonModels.checkStringObject(abstractReqBody.getPwr_gp_approved()).equals("Yes")
								&& !CommonModels.checkStringObject(abstractReqBody.getCounter_approved_gp())
								.equals("T")) {
							System.out.println("ENABLE");
							map.put("COUNTERSUBMITION", "ENABLE");
						}
						// c. COUNTER APPROVED
						if (CommonModels.checkStringObject(abstractReqBody.getPwr_gp_approved()).equals("Yes")
								&& CommonModels.checkStringObject(abstractReqBody.getCounter_approved_gp())
								.equals("T")) {
							map.put("countersuccessMsg", "Counter submitted and finalized by GP.");
							map.put("COUNTERSUBMITION", "DISABLE");
						}
					}

					map.put("STATUSUPDATEBTN", "STATUSUPDATEBTN");
				}

				data = AssignedCaseservice.getDEPTNSTRUCTIONS(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("DEPTNSTRUCTIONS", data);
				}

				map.put("HEADING", " Details for the Case No. :" + cIno);
				map.put("fileCino", cIno);

			}
			if (cIno != null) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("msg", "Details for the Case No. :" + cIno);
			} 
			else {
				map.put("msg", "Invalid Cino. / No Records Found to display.");
				map.put("Status", false);
				map.put("scode", "02");
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	@GetMapping("/getGPSList")
	public List<Map<String, Object>> getGPSList() {
		return gpReportRepo.getGPSList(); // Call instance method, not static
	}


	@Override
	public List<Map<String, Object>> casewisedataViewInstruction(Authentication authentication ) {
		String  condition = "",  sql = "", heading = "", 
				Condition1="";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();

		if (userId.equals("public-prosecutor@ap.gov.in")) {

			Condition1 = " and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA' ";
		}

		if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

			Condition1 = " and (type_name_reg like '%CRL%'  or type_name_reg='RT')  and type_name_reg!='TRCRLA'  ";
		}

		sql = "select type_name_reg, reg_no,reg_year, to_char(dt_regis,'dd-mm-yyyy') as dt_regis, pet_name,a.cino, "
				+ "(case when pend_disp='P' then 'Pending' else 'Disposed' end) as status ,  "
				+ " (case when (category_service='NON-SERVICE' or d.category_service is null or d.category_service=' ') then 'NON-SERVICE' else 'SERVICE' end) as servicetype ,"
				+ " case when length(scanned_document_path) > 10 then scanned_document_path else '-' end as scanned_document_path,legacy_ack_flag "
				+ " from (select distinct cino,legacy_ack_flag from ecourts_dept_instructions where legacy_ack_flag='Legacy') a inner join ecourts_case_data d on (a.cino=d.cino)"
				+ " where d.dept_code in (select dept_code from ecourts_mst_gp_dept_map where gp_id='" + userId + "' "
				+ Condition1 + ")";

		System.out.println("Gp ViewInstruction SQL:" + sql);
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> casewisedataNewViewInstruction(Authentication authentication ) {
		String Condition1="";
		Map<String, Object> map = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		if (userId.equals("public-prosecutor@ap.gov.in")) {

			Condition1 = " and  ack_type='NEW' and inserted_by like 'PP%'  ";
		}

		if (userId.equals("addl-pubprosecutor@ap.gov.in")) {

			Condition1 = " and  ack_type='NEW' and inserted_by like 'PP%'   ";
		}

		String sql_new = "select distinct d.ack_no ,(select case_full_name from case_type_master ctm where ctm.sno::text=e.casetype::text) as type_name_reg, "
				+ " e.reg_no, e.reg_year, petitioner_name,to_char(e.inserted_time,'dd-mm-yyyy') as dt_regis, a.cino,COALESCE(NULLIF(servicetpye,'null'), 'NON-SERVICES') as servicetype , "
				+ "  case when length(ack_file_path) > 10 then ack_file_path else '-' end as scanned_document_path,legacy_ack_flag  "
				+ " from (select distinct cino,dept_code,legacy_ack_flag from ecourts_dept_instructions where legacy_ack_flag='New') a "
				+ " inner join ecourts_gpo_ack_depts d on (d.ack_no=a.cino)   inner join ecourts_gpo_ack_dtls e on (d.ack_no=e.ack_no)  "  
				+ " where d.dept_code in (select dept_code from ecourts_mst_gp_dept_map where gp_id='" + userId + "' "
				+ Condition1 + " )  ";

		System.out.println("Gp SQL:" + sql_new);
		return jdbcTemplate.queryForList(sql_new);
	}
	@Override
	public List<Map<String, Object>> viewCaseDetails(Authentication authentication ,String caseNo, String caseType, int caseYear) {

		Map<String, Object> map = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sql = "select type_name_reg,reg_no,reg_year, to_char(dt_regis,'dd-mm-yyyy') as dt_regis,cino , case when length(scanned_document_path) > 10 then scanned_document_path else '-' end as scanned_document_path  from ecourts_case_data a "
				+ " inner join dept_new d on (a.dept_code=d.dept_code)   inner join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code) "
				+ " where reg_year > 0 and d.display = true  and e.gp_id='" + userId + "' ";

		sql += "and reg_year='" + caseYear + "' ";

		sql += "order by reg_year,type_name_reg,reg_no";

		System.out.println("SQL:" + sql);
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public Map<String, Object> DailyStatusEntryReport(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cino, String serno, String caseType) {

		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> data = new ArrayList<>();
		String cIno = cino;

		try {
		    abstractReqBody.setCino(cIno);
		    abstractReqBody.setSerno(serno);

		    if ("Legacy".equals(caseType)) {

		        data = gpReportRepo.getLegacyDailyStatusEntryReport(cIno);
		        System.out.println("ecourts SQL (Legacy): " + data);

		        if (data != null && !data.isEmpty()) {
		            List<Map<String, Object>> modifiableData = new ArrayList<>();

		            for (Map<String, Object> row : data) {
		                Map<String, Object> mutableRow = new HashMap<>(row);
		                if (mutableRow.get("is_scandocs_exists") == null) {
		                    mutableRow.put("is_scandocs_exists", "false");
		                }
		                modifiableData.add(mutableRow);
		            }

		            map.put("CASESLISTOLD", modifiableData);
		            abstractReqBody.setCino((String) modifiableData.get(0).get("cino"));

		            List<Map<String, Object>> existData = gpReportRepo.getLegacyDept_InstructionsReport(cIno);
		            System.out.println("sql--" + existData);
		            map.put("existData", existData);
		            map.put("msg", "Records Found");
		            map.put("status", true);
		            map.put("scode", "01");

		        } else {
		            map.put("msg", "No Records Found");
		            map.put("status", false);
		            map.put("scode", "02");
		        }

		    } else {
		        data = gpReportRepo.getNewDailyStatusEntryReport(cIno);
		        System.out.println("ecourts SQL (New): " + data);

		        if (data != null && !data.isEmpty()) {
		            map.put("CASESLISTNEW", data);
		            abstractReqBody.setCino((String) data.get(0).get("cino")); // optional change

		            List<Map<String, Object>> existData = gpReportRepo.getNewDept_InstructionsReport(cIno);
		            map.put("existDataNew", existData);
		            map.put("sdesc", "Records Found");
		            map.put("status", true);
		            map.put("scode", "01");
		             

		        } else {
		            map.put("sdesc", "No Records Found");
		            map.put("status", false);
		            map.put("scode", "02");
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    map.put("sdesc", "Error occurred: " + e.getMessage());
		    map.put("status", false);
		    map.put("scode", "02");
		}
		return map;

	}
	@Override
	public ResponseEntity<Map<String, Object>> getSubmitCategoryLegacyDSE(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cIno ,String serno) {

		String userId = null,  caseType="" ;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		int b = 0;
		int serNO = 0;
		try {
			caseType = "Legacy";

			System.out.println("cIno---"+cIno);

			System.out.println("caseType---"+caseType+serno);
			if (serno != null && !serno.trim().isEmpty()) {
				serNO = Integer.parseInt(serno.trim());
			}
			System.out.println("serNO---"+serNO);
			//abstractReqBody.setChangeLetter("changeLetter",abstractReqBody.getChangeLetter());
			String status_flag="D";

			b = gpReportRepo.updateLegacyDSE(abstractReqBody.getDaily_status(), abstractReqBody.getChangeLetter(), userId,serNO,cIno,serNO);

			System.out.println("a--->"+b);

			if( b>0) {

				b = gpReportRepo.INSERTLegacyDSE_case_activities( cIno,userId,request.getRemoteAddr(),abstractReqBody.getDaily_status(),abstractReqBody.getChangeLetter() );

				return CommonQueryAPIUtils.manualResponse("01", "Dialy Status details saved successfully.");

			}else {
				return CommonQueryAPIUtils.manualResponse("02", "Error in submission. Kindly try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}
	@Override
	public ResponseEntity<Map<String, Object>> getSubmitCategoryNewDSE(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cIno ,String serno) {

		String userId = null,  caseType="" ;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		int b = 0;
		int serNO = 0;
		try {
			caseType = "New";

			System.out.println("cIno---"+cIno);

			System.out.println("caseType---"+caseType+serno);
			if (serno != null && !serno.trim().isEmpty()) {
				serNO = Integer.parseInt(serno.trim());
			}
			System.out.println("serNO---"+serNO);
			//abstractReqBody.setChangeLetter("changeLetter",abstractReqBody.getChangeLetter());
			String status_flag="D";

			b = gpReportRepo.updateNewDSE(abstractReqBody.getDaily_status(), abstractReqBody.getChangeLetter(), userId,serNO,cIno,serNO);

			System.out.println("a--->"+b);

			if( b>0) {

				b = gpReportRepo.INSERTNewDSE_case_activities( cIno,userId,request.getRemoteAddr(),abstractReqBody.getDaily_status(),abstractReqBody.getChangeLetter() );

				return CommonQueryAPIUtils.manualResponse("01", "Dialy Status details saved successfully.");

			}else {
				return CommonQueryAPIUtils.manualResponse("02", "Error in submission. Kindly try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}

	@Override
	public ResponseEntity<Map<String, Object>> ApprovedByGp(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno){ 
		Map<String, Object> response = new HashMap<>(); 
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : ""; 
		int a=0;
		String msg="";
		Map<String, Object> map = new HashMap<>();
		System.err.println(cIno);
		try {
			System.out.println("cino is: " + cIno);

			if (cIno != null && !cIno.equals("")) {
				System.out.println("Gp Approve");

				String actionPerformed = !CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("") &&
						!CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("0") ? abstractReqBody.getActionToPerform().toString() + " Approved" :"CASE DETAILS UPDATED";
				String deptCodeC = "", distCodeC = "",  assigned2Emp = "";
				int newStatus = 0;
				List<Map<String, Object>> caseData = assignedCasesToSectionRepo.caseData(cIno);

				if (caseData != null) {

					deptCodeC = CommonModels.checkStringObject(caseData.get(0).get("dept_code"));
					distCodeC = CommonModels.checkStringObject(caseData.get(0).get("dist_id"));
					System.out.println("deptCodeC::" + deptCodeC);
					System.out.println("distCodeC::" + distCodeC);


					if (deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION SECT // DEPT 
						newStatus = 5; 

						assigned2Emp = gpReportRepo.ecourts_return_back_to_Scret_Section(cIno, deptCodeC);

						msg="Returned Case to Section Officer (Sect. Dept.):" + cIno;

					} else if (!deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION // HOD 
						newStatus = 9;

						assigned2Emp = gpReportRepo.ecourts_return_back_to_SectionHOD(cIno, deptCodeC);

						msg= "Returned Case to Section Officer (HOD):" + cIno;

					} else if (!distCodeC.equals("") && !distCodeC.equals("0")) {// SECTION DIST
						newStatus = 10;

						assigned2Emp = gpReportRepo.ecourts_return_back_to_SectionDIST(cIno, deptCodeC);

						msg="Returned Case to Section Officer (District):" + cIno;
					}

					System.out.println("assigned2Emp::=======" + assigned2Emp); 
				}

				a = gpReportRepo.backinsertPWRApprove(cIno);

				System.out.println(" a is " + a + " sql is" + a); 
				System.out.println("PWR-/CNTER--------------"+abstractReqBody.getActionToPerform());

				String pwrFile1="";
				String pwrFile2="";
				String pwrFile3="";

				String counterFile1="";
				String counterFile2="";
				String counterFile3="";
				List<Map<String, Object>> data = assignedCasesToSectionRepo.getOLCMSCASEDATAForUpdate(cIno);
				if ("Parawise Remarks".equals(abstractReqBody.getActionToPerform())) {

					if (abstractReqBody.getParawiseRemarksCopy() != null &&
							!abstractReqBody.getParawiseRemarksCopy().toString().trim().isEmpty()) {

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

								System.out.println("pwrFile1 1: " + doc1);
							}
							// Update request object only if file list was populated from DB
							abstractReqBody.setParawiseRemarksCopy(file);
						}


						if (file != null && file.size() > 1 && file.get(1) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");
							doc2 = file.get(1);

						} else {
							System.out.println("No uploaded files   pwr");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty()&& data.get(0).get("pwr_uploaded_copy2") !=null) {
								doc2 = (String) data.get(0).get("pwr_uploaded_copy2");

								System.out.println("pwrFile2 2: " + doc2);
							}
							// Update request object only if file list was populated from DB
							abstractReqBody.setParawiseRemarksCopy(file);
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

								System.out.println("pwrFile3 3: " + doc3);
							}
							// Update request object only if file list was populated from DB
							abstractReqBody.setParawiseRemarksCopy(file);
						}

						// Assign final values
						pwrFile1 = doc1;
						pwrFile2 = doc2;
						pwrFile3 = doc3;

						System.out.println("pwrFile1 1: " + pwrFile1);
						System.out.println("pwrFile2 2: " + pwrFile2);
						System.out.println("pwrFile3 3: " + pwrFile3);

						System.out.println(" ParawiseRemarksCopy----------"+abstractReqBody.getPwr_uploaded_copy());

						if (!data.isEmpty() && data.get(0).get("pwr_uploaded_copy") !=null) {
							abstractReqBody.setPwr_uploaded_copy(data.get(0).get("pwr_uploaded_copy")+"");

							a = gpReportRepo.insertPWRApprove_courts_case_activities(
									cIno, userId, request.getRemoteAddr(), abstractReqBody.getRemarks(),
									abstractReqBody.getPwr_uploaded_copy(), assigned2Emp);
						}
					}

					a = gpReportRepo.updatePWRApprove_olcms_case_details(
							abstractReqBody.getRemarks(), userId, pwrFile1,pwrFile2,pwrFile3, cIno);

					a = gpReportRepo.updatePWRApprove_ecourts_case_data(newStatus, assigned2Emp, cIno);


					msg= "Parawise Remarks Approved successfully for Case (" + cIno + ").";

				} else if ("Counter Affidavit".equals(abstractReqBody.getActionToPerform())) {

					if (abstractReqBody.getCounterFileCopy() != null &&
							!abstractReqBody.getCounterFileCopy().toString().trim().isEmpty()) {

						List<String> file = abstractReqBody.getCounterFileCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null &&file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files

							System.out.println("Use uploaded files   counter");
							doc1 = file.get(0);

						} else {

							System.out.println("No uploaded files   counter");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document") !=null) {
								doc1 = (String) data.get(0).get("counter_filed_document");

								System.out.println("counterFile1 1: " + doc1);

							}

							// Set to request body
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

								System.out.println("counterFile2 2: " + doc2);

							}

							// Set to request body
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

								System.out.println("counterFile3 3: " + doc3);
							}

							// Set to request body
							abstractReqBody.setCounterFileCopy(file);
						}

						// Final assignment
						counterFile1 = doc1;
						counterFile2 = doc2;
						counterFile3 = doc3;

						System.out.println("counterFile1 1: " + counterFile1);
						System.out.println("counterFile2 2: " + counterFile2);
						System.out.println("counterFile3 3: " + counterFile3);

						System.out.println("Counter_filed_document---counterFileCopy-------"+abstractReqBody.getCounter_filed_document());


						if (!data.isEmpty() && data.get(0).get("counter_filed_document") != null) {
							abstractReqBody.setCounter_filed_document( data.get(0).get("counter_filed_document")+"");

							a = gpReportRepo.insertCounterApprove_courts_case_activities(
									cIno, userId, request.getRemoteAddr(), abstractReqBody.getRemarks(),
									abstractReqBody.getCounter_filed_document(), assigned2Emp);
						}
					}

					a = gpReportRepo.updateCounterApprove_olcms_case_details(
							userId, abstractReqBody.getRemarks(), userId, counterFile1,counterFile2,counterFile3, cIno);

					a = gpReportRepo.updateCounterApprove_ecourts_case_data(newStatus, assigned2Emp, cIno);
					msg= "Counter Affidavit finalized successfully for Case (" + cIno + ").";
				}


				System.out.println("final  approval of counter /pwr"+a);
				if (a > 0) {
					gpReportRepo.insertCounterApproveFINAL_courts_case_activities(
							cIno, actionPerformed, userId, request.getRemoteAddr(), abstractReqBody.getRemarks());

					return CommonQueryAPIUtils.manualResponse("01", msg);
				} else {
					return CommonQueryAPIUtils.manualResponse("02", "Error while processing Case (" + cIno + ").");
				}
			} else { 
				map.put("errorMsg", "Invalid Cino :" + cIno); 
			} 
		} catch (Exception e) { 
			System.err.println(e.getMessage());
			e.printStackTrace(); 
		} 
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}
	@Override
	public ResponseEntity<Map<String, Object>> RejectedByGp(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno){ 

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : ""; 
		int a=0;
		String msg="";
		Map<String, Object> map = new HashMap<>();
		try {
			System.out.println("cino is: " + cIno);

			if (cIno != null && !cIno.equals("")) {
				System.out.println("Gp Reject");

				String deptCodeC = "", distCodeC = "",  assigned2Emp = "";
				int newStatus = 0;
				List<Map<String, Object>> caseData = assignedCasesToSectionRepo.caseData(cIno);

				if (caseData != null) {

					deptCodeC = CommonModels.checkStringObject(caseData.get(0).get("dept_code"));
					distCodeC = CommonModels.checkStringObject(caseData.get(0).get("dist_id"));
					System.out.println("deptCodeC::" + deptCodeC);
					System.out.println("distCodeC::" + distCodeC);

					if (deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION SECT // DEPT 
						newStatus = 5; 

						assigned2Emp = gpReportRepo.ecourts_reject_back_to_Scret_Section(cIno, deptCodeC);

						msg="Returned Case to Section Officer (Sect. Dept.):" + cIno;

					} else if (!deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION // HOD 
						newStatus = 9;

						assigned2Emp = gpReportRepo.ecourts_reject_back_to_SectionHOD(cIno, deptCodeC);

						msg="Returned Case to Section Officer (HOD):" + cIno;

					} else if (!distCodeC.equals("") && !distCodeC.equals("0")) {// SECTION DIST
						newStatus = 10;

						assigned2Emp = gpReportRepo.ecourts_reject_back_to_SectionDIST(cIno, deptCodeC);

						msg="Returned Case to Section Officer (District):" + cIno;
					}

					System.out.println("assigned2Emp::=======" + assigned2Emp); 
				}

				String actionPerformed = "";
				actionPerformed = !CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("") &&
						!CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("0") ? abstractReqBody.getActionToPerform().toString() + " Returned" :"CASE DETAILS UPDATED";

				System.out.println("actionPerformed----------------"+actionPerformed+abstractReqBody.getActionToPerform());
				if ("Parawise Remarks".equals(abstractReqBody.getActionToPerform())) {

					a = gpReportRepo.updateCounterRejectPWR_ecourts_case_data(newStatus, assigned2Emp, cIno);
					System.out.println("SQL REJECT PWR:" + a);

				} else if ("Counter Affidavit".equals(abstractReqBody.getActionToPerform())) {

					a = gpReportRepo.updateCounterRejectCOUNTER_ecourts_case_data(newStatus, assigned2Emp, cIno);

					System.out.println("SQL REJECT COUNTER :" + a);

				} else if (CommonModels.checkStringObject(abstractReqBody.getCounterFiled()).equals("Yes")) {

					a = gpReportRepo.updateCounterRejectCOUNTERFILLED_ecourts_case_data(newStatus, assigned2Emp, cIno);
					System.out.println("SQL reject counterFiled:" + a);

					a = gpReportRepo.updateCounterRejectCOUNTERFILLED_olcms_case_details(cIno);

				} else if (CommonModels.checkStringObject(abstractReqBody.getCounterFiled()).equals("No")
						&& CommonModels.checkStringObject(abstractReqBody.getParawiseRemarksSubmitted()).equals("Yes")) {

					a = gpReportRepo.updateCounterRejectCOUNTERPWR_ecourts_case_data(newStatus, assigned2Emp, cIno);
					System.out.println("SQL Reject COUNTER PWR:" + a);
				}

				a = gpReportRepo.updateCounterRejectBackUpolcms_case_details(cIno);

				if (a > 0) {

					a = gpReportRepo.insertCounterReject_courts_case_activities(
							cIno, actionPerformed,userId, request.getRemoteAddr(), abstractReqBody.getRemarks(), assigned2Emp);
					return CommonQueryAPIUtils.manualResponse("01", msg);
				} else {
					return CommonQueryAPIUtils.manualResponse("02", "Error while forwarding the case details (" + cIno + ").");
				}
			} else {
				return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}

	@Override
	public String getApproveGPNew(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno){ 

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : ""; 
		String pwrFile1="";
		String pwrFile2="";
		String pwrFile3="";

		String counterFile1="";
		String counterFile2="";
		String counterFile3="";
		int a=0;
		String msg="",msgS="";

		try {
			System.out.println("cino is: " + cIno+userId);

			if (cIno != null && !cIno.equals("")) {
				String actionPerformed = !CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("") &&
						!CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("0") ? abstractReqBody.getActionToPerform().toString() + " Approved" :"CASE DETAILS UPDATED";
				String deptCodeC = "", distCodeC = "",   assigned2Emp = "" ;
				int respondent_no=0;
				int newStatus=0;
				List<Map<String, Object>> caseData = gpReportRepo.caseDataGPNew(cIno,userId);

				if (caseData != null) {

					deptCodeC = CommonModels.checkStringObject(caseData.get(0).get("dept_code"));
					distCodeC = CommonModels.checkStringObject(caseData.get(0).get("dist_id"));
					respondent_no = CommonModels.checkIntObject(caseData.get(0).get("respondent_slno"));
					System.out.println("deptCodeC::" + deptCodeC);
					System.out.println("distCodeC::" + distCodeC);
					System.out.println("respondent_no::" + respondent_no);
					if (deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION SECT // DEPT
						newStatus = 5;

						assigned2Emp = gpReportRepo.ecourts_return_back_to_Scret_SectionNEW(cIno, deptCodeC);

						msgS = "Returned Case to Section Officer (Sect. Dept.)";
					} else if (!deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION // HOD
						newStatus =9;
						msgS = "Returned Case to Section Officer (HOD)";

						assigned2Emp = gpReportRepo.ecourts_return_back_to_SectionHODNEW(cIno, deptCodeC);

					} else if (!distCodeC.equals("") && !distCodeC.equals("0")) {// SECTION DIST
						newStatus = 10;
						msgS = "Returned Case to Section Officer (District)";

						assigned2Emp = gpReportRepo.ecourts_return_back_to_SectionDISTNEW(cIno, deptCodeC);
					}
				}
				System.out.println("assigned2Emp---"+assigned2Emp);
				List<Map<String, Object>> data = assignedCasesToSectionRepo.getOLCMSCASEDATAForUpdate(cIno);
				msgS = "Case details (" + cIno + ") updated successfully.";

				a = gpReportRepo.backinsertPWRApproveNEW(cIno,respondent_no);

				if ("Parawise Remarks".equals(abstractReqBody.getActionToPerform())) {

					if (abstractReqBody.getParawiseRemarksCopy() != null &&
							!abstractReqBody.getParawiseRemarksCopy().toString().trim().isEmpty()) {

						List<String> file = abstractReqBody.getParawiseRemarksCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");
							doc1 = file.get(0);

						} else {
							System.out.println("No uploaded files   pwr");

							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy") !=null) {
								doc1 = (String) data.get(0).get("pwr_uploaded_copy");

								System.out.println("pwrFile1 1: " + doc1);
							}
							// Update request object only if file list was populated from DB
							abstractReqBody.setParawiseRemarksCopy(file);
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

								System.out.println("pwrFile2 2: " + doc2);
							}
							// Update request object only if file list was populated from DB
							abstractReqBody.setParawiseRemarksCopy(file);
						}

						if (file != null && file.size() > 2 && file.get(2) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");

							doc3 = file.get(2);

						} else {
							System.out.println("No uploaded files   pwr");
							// No uploaded files – fallback to database

							if ( data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy3") !=null) {

								doc3 = (String) data.get(0).get("pwr_uploaded_copy3");

								System.out.println("pwrFile3 3: " + doc3);
							}
							// Update request object only if file list was populated from DB
							abstractReqBody.setParawiseRemarksCopy(file);
						}

						// Assign final values
						pwrFile1 = doc1;
						pwrFile2 = doc2;
						pwrFile3 = doc3;

						System.out.println("pwrFile1 1: " + pwrFile1);
						System.out.println("pwrFile2 2: " + pwrFile2);
						System.out.println("pwrFile3 3: " + pwrFile3);

						System.out.println(" ParawiseRemarksCopy----------"+abstractReqBody.getPwr_uploaded_copy());

						if (!data.isEmpty()) {
							abstractReqBody.setPwr_uploaded_copy(data.get(0).get("pwr_uploaded_copy")+"");

							a = gpReportRepo.insertPWRApprove_courts_case_activities(
									cIno, userId, request.getRemoteAddr(), abstractReqBody.getRemarks(),
									abstractReqBody.getPwr_uploaded_copy(), assigned2Emp);
						}
					}

					System.out.println("PWR   "+a);
					a = gpReportRepo.updatePWRApprove_olcms_case_detailsNEW(
							abstractReqBody.getRemarks(), userId, pwrFile1,pwrFile2,pwrFile3, cIno,respondent_no);

					a = gpReportRepo.updatePWRApprove_ecourts_case_dataNEW(newStatus, assigned2Emp, cIno,respondent_no);

					msgS = "Parawise Remarks Approved successfully for Case (" + cIno + ").";

				} else if ("Counter Affidavit".equals(abstractReqBody.getActionToPerform())) {

					if (abstractReqBody.getCounterFileCopy() != null &&
							!abstractReqBody.getCounterFileCopy().toString().trim().isEmpty()) {

						List<String> file = abstractReqBody.getCounterFileCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null &&file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files

							System.out.println("Use uploaded files   counter");
							doc1 = file.get(0);

						} else {

							System.out.println("No uploaded files   counter");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document") !=null) {
								doc1 = (String) data.get(0).get("counter_filed_document");

								System.out.println("counterFile1 1: " + doc1);

							}

							// Set to request body
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

								System.out.println("counterFile2 2: " + doc2);

							}

							// Set to request body
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

								System.out.println("counterFile3 3: " + doc3);
							}

							// Set to request body
							abstractReqBody.setCounterFileCopy(file);
						}

						// Final assignment
						counterFile1 = doc1;
						counterFile2 = doc2;
						counterFile3 = doc3;

						System.out.println("counterFile1 1: " + counterFile1);
						System.out.println("counterFile2 2: " + counterFile2);
						System.out.println("counterFile3 3: " + counterFile3);

						System.out.println("Counter_filed_document---counterFileCopy-------"+abstractReqBody.getCounter_filed_document());

						if (!data.isEmpty() && data.get(0).get("counter_filed_document") != null) {
							abstractReqBody.setCounter_filed_document( data.get(0).get("counter_filed_document")+"");

							a = gpReportRepo.insertCounterApprove_courts_case_activities(
									cIno, userId, request.getRemoteAddr(), abstractReqBody.getRemarks(),
									abstractReqBody.getCounter_filed_document(), assigned2Emp);
						}
					}
					System.out.println("COUNTER   "+a);
					a = gpReportRepo.updateCounterApprove_olcms_case_detailsNEW(
							userId, abstractReqBody.getRemarks(), userId, counterFile1,counterFile2,counterFile3, cIno,respondent_no);

					a = gpReportRepo.updateCounterApprove_ecourts_case_dataNEW(newStatus, assigned2Emp, cIno,respondent_no);
					msgS = "Counter Affidavit finalized successfully for Case (" + cIno + ").";
				}

				System.out.println("a================"+a);
				if (a > 0) {

					gpReportRepo.insertCounterApproveFINAL_courts_case_activitiesNEW(
							cIno, actionPerformed, userId, request.getRemoteAddr(), abstractReqBody.getRemarks());

					msg=msgS;

				} else {

					msg="Error while Approve PWR/COUNTER the case details (" + cIno + ").";
				}
			} else {

				msg="Error Invalid Cino Data:" + cIno;
			}
		} catch (Exception e) { 
			System.err.println(e.getMessage());
			e.printStackTrace(); 
		} 
		return msg;
	}

	@Override
	public String getRejectGPNew(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno){ 

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : ""; 
		int a=0;
		String msg="",msgS="";
		try {
			System.out.println("cino is: " + cIno+userId);

			if (cIno != null && !cIno.equals("")) {

				String actionPerformed = "";
				actionPerformed = !CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("") &&
						!CommonModels.checkStringObject(abstractReqBody.getActionToPerform()).equals("0") ? abstractReqBody.getActionToPerform().toString() + " Returned" :"CASE DETAILS UPDATED";

				String deptCodeC = "", distCodeC = "",  assigned2Emp = "";
				int respondent_slno=0;
				int newStatus=0;
				List<Map<String, Object>> caseData = gpReportRepo.caseDataGPNew(cIno,userId);

				if (caseData != null) {

					deptCodeC = CommonModels.checkStringObject(caseData.get(0).get("dept_code"));
					distCodeC = CommonModels.checkStringObject(caseData.get(0).get("dist_id"));
					respondent_slno = CommonModels.checkIntObject(caseData.get(0).get("respondent_slno"));
					System.out.println("deptCodeC::" + deptCodeC);
					System.out.println("distCodeC::" + distCodeC);
					System.out.println("respondent_slno::" + respondent_slno);
					if (deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION SECT
						// DEPT
						newStatus = 5;

						msgS = "Returned Case to Section Officer (Sect. Dept.)";

						assigned2Emp = gpReportRepo.ecourts_return_back_to_Scret_SectionNEW(cIno, deptCodeC);

					} else if (!deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION
						// HOD
						newStatus = 9;
						msgS = "Returned Case to Section Officer (HOD)";

						assigned2Emp = gpReportRepo.ecourts_return_back_to_SectionHODNEW(cIno, deptCodeC);

					} else if (!distCodeC.equals("") && !distCodeC.equals("0")) {// SECTION DIST
						newStatus = 10;
						msgS = "Returned Case to Section Officer (District)";

						assigned2Emp = gpReportRepo.ecourts_return_back_to_SectionDISTNEW(cIno, deptCodeC);
					}

					System.out.println("assigned2Emp==========>"+assigned2Emp); 
				}else {
					msg="Error Invalid caseData :" + cIno;
				}

				if ("Parawise Remarks".equals(abstractReqBody.getActionToPerform())) {

					a = gpReportRepo.updatePWRApprove_ecourts_case_dataPWRNEWGPREJECT(newStatus, assigned2Emp, cIno,respondent_slno);
					 System.out.println("Calling updatePWRApprove_ecourts_case_dataPWRNEWGPREJECT with params:");
					    System.out.println("newStatus = " + newStatus);
					    System.out.println("assignedToEmp = " + assigned2Emp);
					    System.out.println("cino = " + cIno);
					    System.out.println("respondentSlNo = " + respondent_slno);
					System.out.println("Parawise==========>"+a); 

				} else if ("Counter Affidavit".equals(abstractReqBody.getActionToPerform())) {

					a = gpReportRepo.updatePWRApprove_ecourts_case_dataCOUNTERNEWGPREJECT(newStatus, assigned2Emp, cIno,respondent_slno);

					System.out.println("Counter==========>"+a); 
				} else if ("Yes".equals(abstractReqBody.getCounterFiled())) {

					a = gpReportRepo.updatePWRApprove_ecourts_case_dataCOUNTERFILEDNEWGPREJECT(newStatus, assigned2Emp, cIno,respondent_slno);

					a = gpReportRepo.updatePWRApprove_ecourts_case_dTLSCOUNTERFILEDNEWGPREJECT(cIno,respondent_slno);
					System.out.println("CounterFiled==========>"+a); 
				} else if (CommonModels.checkStringObject(abstractReqBody.getCounterFiled()).equals("No")
						&& CommonModels.checkStringObject(abstractReqBody.getParawiseRemarksSubmitted()).equals("Yes")) {


					a = gpReportRepo.updatePWRApprove_ecourts_case_dataCOUNTERFILEDPWRSUBNEWGPREJECT(newStatus, assigned2Emp, cIno,respondent_slno);
					System.out.println("CounterFiled=====ParawiseRemarksSubmitted=====>"+a); 
				}

				a = gpReportRepo.ecourts_olcms_case_details_logNEWGPREJECT(cIno,respondent_slno);
				System.out.println("a==========>"+a); 
				if (a > 0) {

					a = gpReportRepo.insertCounterApproveFINAL_courts_case_activitiesNEWreject(
							cIno, actionPerformed, userId, request.getRemoteAddr(), abstractReqBody.getRemarks());
					msg=msgS;
					System.out.println("FINAL==========>"+a); 
				} else {

					msg="Error while Reject PWR/COUNTER the case details (" + cIno + ").";
				}
			} else {
				msg="Error Invalid Cino :" + cIno;
			}
		} catch (Exception e) { 
			System.err.println(e.getMessage());
			e.printStackTrace(); 
		} 
		return msg;
	}

}
