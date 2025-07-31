package in.apcfss.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AssignedCasesToSectionServiceImpl implements AssignedCasesToSectionService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	AssignedCasesToSectionRepo assignedCasesToSectionRepo;

	@Override
	public List<Map<String, Object>> getAssignedCasesToSectionList(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody) {
		String condition = "", sql = "", condition_gp = "", deptCode = "";
		Integer distId = 0;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		deptCode = (String) userPrincipal.getDeptCode();

		distId = userPrincipal.getDistId();
		try {

			if (roleId != null && roleId.equals("4")) { // MLO
				condition = " and a.dept_code='" + deptCode + "' and a.case_status in(2)";
			} else if (roleId != null && roleId.equals("5")) { // NO
				condition = " and a.dept_code='" + deptCode + "' and a.case_status in (4)";
			} else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				condition = " and a.dept_code='" + deptCode + "' and a.case_status=5 and a.assigned_to='" + userId
						+ "'";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				condition = " and a.dept_code='" + deptCode + "' and a.case_status=9 and a.assigned_to='" + userId
						+ "'";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				condition = " and a.dept_code='" + deptCode + "' and dist_id='" + distId
						+ "' and a.case_status=10 and a.assigned_to='" + userId + "'";
			}

			else if (roleId != null && roleId.equals("3")) { // SECT DEPT
				condition = " and a.dept_code='" + deptCode + "' and a.case_status in(1)";
			} else if (roleId != null && roleId.equals("9")) { // HOD
				condition = " and a.dept_code='" + deptCode + "' and a.case_status in(3)";
			} else if (roleId != null && roleId.equals("2")) { // DC
				condition = " and a.case_status=7 and dist_id='" + distId + "'";
			} else if (roleId != null && roleId.equals("10")) { // DC-NO
				condition = " and a.dept_code='" + deptCode + "' and a.case_status=8 and a.dist_id='" + distId + "'";
			} else if (roleId != null && roleId.equals("6")) { // GPO

				String counter_pw_flag = abstractReqBody.getPwCounterFlag(); // request.getParameter("pwCounterFlag")
				// +// "";

				condition = " and a.case_status=6 and e.gp_id='" + userId + "' ";
				condition_gp = " left join ecourts_mst_gp_dept_map e on (a.dept_code=e.dept_code) ";
				if (counter_pw_flag.equals("PR")) {
					condition += " and pwr_uploaded='No' and (coalesce(pwr_approved_gp,'0')='0' or coalesce(pwr_approved_gp,'No')='No' )";
				}
				if (counter_pw_flag.equals("COUNTER")) {
					condition += " and pwr_uploaded='Yes' and counter_filed='No' and coalesce(counter_approved_gp,'F')='F'";
				}
			}

			sql = "select a.* , od.pwr_uploaded, od.counter_filed, od.pwr_approved_gp, coalesce(od.counter_approved_gp,'-') as counter_approved_gp "
					+ " ,case when pwr_uploaded='Yes' then 'Parawise Remarks Uploaded' else 'Parawise Remarks not Submitted' end as casestatus1,"
					+ " case when pwr_approved_gp='Yes' then 'Parawise Remarks Approved by GP' else 'Parawise Remarks Not Approved by GP' end as casestatus2,"
					+ " case when counter_filed='Yes' then 'Counter Filed' else 'Counter Not Filed' end as casestatus3,"
					+ " case when counter_approved_gp='T' then 'Counter Approved by GP' else 'Counter Not Approved by GP' end as casestatus4 "
					+ " ,coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1 "
					+ " from ecourts_case_data a  left join ecourts_olcms_case_details od on (a.cino=od.cino)" + " "
					+ condition_gp + " where assigned=true " + condition
					+ " and coalesce(a.ecourts_case_status,'')!='Closed' " + " order by a.cino";

			System.out.println("AssignedCasesToSectionAction unspecified SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEMPList(UserDetailsImpl userPrincipal) {

		String sql = null, deptCode = null, roleId = null, part = "";
		Integer distCode = 0;
		try {
			roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
			deptCode = (String) userPrincipal.getDeptCode();
			distCode = userPrincipal.getDistId();

			if (roleId.equals("8")) { // sec Section Officer
				part = " a.DEPT_CODE='" + deptCode + "'  and user_type='8'";
			} else if (roleId.equals("11")) { // Section Officer (HOD)
				part = " a.DEPT_CODE='" + deptCode + "' and user_type='11' ";
			} else {
				part = " a.dist_id='" + distCode + "' and user_type='12' and a.dept_code='" + deptCode + "'   "; // Section
				// Officer
				// (District)
			}

			sql = "select userid as value,user_description as label from users a where " + part
					+ " order by user_description";

			System.out.println("sql--emp---" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getNoList(UserDetailsImpl userPrincipal) {

		String sql = null, deptCode = null, roleId = null, part = "";
		int distCode = 0;
		try {
			roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
			deptCode = (String) userPrincipal.getDeptCode();
			distCode = userPrincipal.getDistId();

			if (roleId.equals("8")) { // MLO
				part = " DEPT_CODE='" + deptCode + "'  and user_type='4'";
			} else if (roleId.equals("11")) { // NODAL
				part = " DEPT_CODE='" + deptCode + "' and user_type='5' ";
			} else {
				part = " dept_code='" + deptCode + "' and user_type='10'  and dist_id='" + distCode + "' "; // DNO
			}

			sql = "select dept_code as value,userid||'----'||user_description as label from users where  " + part
					+ " order by user_description";

			System.out.println("sql--no---" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);

	}

	public List<Map<String, Object>> getCaseData(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getCaseData(cIno);
		return response;
	}

	public List<Map<String, Object>> getCaseDataNEW(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getCaseDataNEW(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getUSERSLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getUSERSLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getDocumentsList(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getDocumentsList(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getActlist(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getActlist(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOtherDocumentsList(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOtherDocumentsList(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getMappedInstructionList(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getMappedInstructionList(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getMappedPWRList(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getMappedPWRList(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOrderList(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOrderList(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getIAFILINGLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getIAFILINGLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getINTERIMORDERSLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getINTERIMORDERSLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getLINKCASESLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getLINKCASESLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOBJECTIONSLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOBJECTIONSLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getCASEHISTORYLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getCASEHISTORYLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getPETEXTRAPARTYLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getPETEXTRAPARTYLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getRESEXTRAPARTYLIST(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getRESEXTRAPARTYLIST(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getACTIVITIESDATA(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getACTIVITIESDATA(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOLCMSCASEDATA(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOLCMSCASEDATA(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getDEPTNSTRUCTIONS(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getDEPTNSTRUCTIONS(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getUsersListForUpdate(Authentication authentication, String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getUsersListForUpdate(authentication, cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getActlistForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getActlistForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOrderlistForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOrderlistForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getIAFILINGLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getIAFILINGLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getINTERIMORDERSLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getINTERIMORDERSLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getLINKCASESLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getLINKCASESLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOBJECTIONSLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOBJECTIONSLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getCASEHISTORYLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getCASEHISTORYLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getPETEXTRAPARTYLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getPETEXTRAPARTYLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getRESEXTRAPARTYLISTForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getRESEXTRAPARTYLISTForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getACTIVITIESDATAForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getACTIVITIESDATAForUpdate(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getDataStatus(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getDataStatus(cIno);
		return response;
	}

	@Override
	public List<Map<String, Object>> getOLCMSCASEDATAForUpdate(String cIno) {

		List<Map<String, Object>> response = null;
		response = assignedCasesToSectionRepo.getOLCMSCASEDATAForUpdate(cIno);
		return response;
	}

	@Override
	public ResponseEntity<Map<String, Object>> getupdateCaseDetails(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno) {

		String counterFile1 = null;
		String counterFile2 = null;
		String counterFile3 = null;
		String pwrFile1 = null;
		String pwrFile2 = null;
		String pwrFile3 = null;
		String userId = null, roleId = null, deptCode = "", empId = null, empSection = null, empPost = null;

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String ipAddress = request.getRemoteAddr();

		Map<String, Object> map = new HashMap<>();
		int sql = 0;
		int query = 0;
		String updateSql = "";
		String actionPerformed = "";
		String remarks = "";

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		// Integer deptId = userPrincipal.getDeptId();
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		System.out.println("deptCode------" + deptCode);

		try {
			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {

				actionPerformed = (abstractReqBody.getActionToPerform() != null
						&& !abstractReqBody.getActionToPerform().equals(""))
						? abstractReqBody.getActionToPerform().toString()
								: "CASE DETAILS UPDATED";

				remarks = (abstractReqBody.getRemarks() != null)
						? abstractReqBody.getRemarks().toString().replace("'", "")
								: "";

				if (abstractReqBody.getEcourtsCaseStatus() != null
						&& (abstractReqBody.getEcourtsCaseStatus().toString().equals("Closed")
								|| abstractReqBody.getEcourtsCaseStatus().toString().equals("Pending"))) {
					List<Map<String, Object>> data = getOLCMSCASEDATAForUpdate(cIno);
					if (abstractReqBody.getPetitionDocument() != null
							&& !abstractReqBody.getPetitionDocument().toString().equals("")) {

						System.out.println("PetitionDocument is" + abstractReqBody.getPetitionDocument());

					} else {

						System.out.println("old PetitionDocument is" + data.get(0).get("petition_document"));

						abstractReqBody.setPetitionDocument(data.get(0).get("petition_document"));
					}

					sql = assignedCasesToSectionRepo.insertBack_case_activitiesPending(cIno, userId, ipAddress, remarks,
							abstractReqBody.getPetitionDocument());
				}
				// STATUS CLOSED
				if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("Closed")) {
					List<Map<String, Object>> data = getOLCMSCASEDATAForUpdate(cIno);
					if (abstractReqBody.getActionTakenOrder() != null
							&& !abstractReqBody.getActionTakenOrder().toString().equals("")) {

						System.out.println("ActionTakenOrder is" + abstractReqBody.getActionTakenOrder());

					} else {

						System.out.println(" old ActionTakenOrder is" + data.get(0).get("action_taken_order"));

						abstractReqBody.setActionTakenOrder(data.get(0).get("action_taken_order"));
					}

					sql = assignedCasesToSectionRepo.insertBack_case_activitiesClosedActionTakenOrder(cIno, userId,
							ipAddress, remarks, abstractReqBody.getActionTakenOrder());

					if (abstractReqBody.getJudgementOrder() != null
							&& !abstractReqBody.getJudgementOrder().toString().equals("")) {

						System.out.println("JudgementOrder is" + abstractReqBody.getJudgementOrder());

					} else {

						System.out.println(" pdf is" + data.get(0).get("judgement_order"));

						abstractReqBody.setJudgementOrder(data.get(0).get("judgement_order"));
					}

					sql = assignedCasesToSectionRepo.insertBack_case_activitiesClosedJudgementOrder(cIno, userId,
							ipAddress, remarks, abstractReqBody.getJudgementOrder());

					if (abstractReqBody.getAppealFiled() != null
							&& abstractReqBody.getAppealFiled().toString().equals("Yes")
							&& abstractReqBody.getAppealFileCopy() != null
							&& !abstractReqBody.getAppealFileCopy().toString().equals("")) {

						System.out.println(" pdf is" + abstractReqBody.getAppealFileCopy());

					} else {

						System.out.println("appeal pdf is" + data.get(0).get("appeal_filed_copy"));

						abstractReqBody.setAppealFileCopy(data.get(0).get("appeal_filed_copy"));
					}
					sql = assignedCasesToSectionRepo.insertBack_case_activitiesClosedAppealFileCopy(cIno, userId,
							ipAddress, remarks, abstractReqBody.getAppealFileCopy());

					if (assignedCasesToSectionRepo.getCinoCount(cIno) > 0) {

						query = assignedCasesToSectionRepo.insertBack_ecourts_olcms_case_details_log(cIno);

						query = assignedCasesToSectionRepo.update_ecourts_olcms_case_details(
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getAppealFiled(),
								abstractReqBody.getAppealFiledDt(), remarks, userId,
								abstractReqBody.getActionToPerform(), abstractReqBody.getPetitionDocument(),
								abstractReqBody.getActionTakenOrder(), abstractReqBody.getJudgementOrder(),
								abstractReqBody.getAppealFileCopy(), cIno);
					} else {

						System.out.println("else ---" + abstractReqBody.getPetitionDocument());

						query = assignedCasesToSectionRepo.insert_ecourts_olcms_case_details(cIno,
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getPetitionDocument(),
								abstractReqBody.getAppealFiled(), abstractReqBody.getAppealFileCopy(),
								abstractReqBody.getJudgementOrder(), abstractReqBody.getActionTakenOrder(), userId,
								remarks, abstractReqBody.getAppealFiledDt(), abstractReqBody.getActionToPerform());
					}

					System.out.println("SQL:" + query);
					// int a = jdbcTemplate.update(query);

					query = assignedCasesToSectionRepo.update_ecourts_case_data(abstractReqBody.getEcourtsCaseStatus(),
							cIno);

					query = assignedCasesToSectionRepo.insert_closedFinal_ecourts_case_activities(cIno, actionPerformed,
							userId, ipAddress, remarks);

					System.out.println("query--" + query);

					if (query > 0) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);
					} else {

						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}
				}
				// STATUS
				// Pending////////////////////////////////////////////////////////////////////////////////////=================================================
				else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("Pending")) {

					if (abstractReqBody.getCounterFiled() != null
							&& abstractReqBody.getCounterFiled().toString().equals("Yes")) {
						List<Map<String, Object>> data = assignedCasesToSectionRepo.getOLCMSCASEDATAForUpdate(cIno);
						List<String> file = abstractReqBody.getCounterFileCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   counter");
							doc1 = file.get(0);

						} else {

							System.out.println("No uploaded files   counter");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document")!=null) {
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

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document2")!=null) {

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

							if (data != null && !data.isEmpty() && data.get(0).get("counter_filed_document3")!=null) {

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

						query = assignedCasesToSectionRepo.insert_PendingCounterEcourts_case_activities(cIno, userId,
								ipAddress, remarks, counterFile1);
					}

					if (abstractReqBody.getParawiseRemarksSubmitted() != null
							&& abstractReqBody.getParawiseRemarksSubmitted().toString().equals("Yes")) {
						List<Map<String, Object>> data = assignedCasesToSectionRepo.getOLCMSCASEDATAForUpdate(cIno);
						List<String> file = abstractReqBody.getParawiseRemarksCopy();

						String doc1 = null, doc2 = null, doc3 = null;

						if (file != null && file.size() > 0 && file.get(0) != null && file.stream().anyMatch(f -> f != null && !f.trim().isEmpty())) {
							// Use uploaded files
							System.out.println("Use uploaded files   pwr");
							doc1 = file.get(0);


						} else {
							System.out.println("No uploaded files   pwr");
							// No uploaded files – fallback to database

							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy")!=null) {
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

							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy2")!=null) {

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

							if (data != null && !data.isEmpty() && data.get(0).get("pwr_uploaded_copy3")!=null) {

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

						query = assignedCasesToSectionRepo.insert_PendingPWREcourts_case_activities(cIno, userId,
								ipAddress, remarks, pwrFile1);
					}
					System.out.println("cIno--lassssssssssssssssssssst-" + cIno);
					if (assignedCasesToSectionRepo.getCinoCount2(cIno) > 0) {

						System.out.println("cino---count > 0---condition" + cIno);

						query += assignedCasesToSectionRepo.insert_Pending_ecourts_olcms_case_details_log(cIno);

						query += assignedCasesToSectionRepo.update_Pending_ecourts_olcms_case_details(
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getCounterFiled(), remarks,
								abstractReqBody.getPetitionDocument(), userId, counterFile1, counterFile2,
								counterFile3, abstractReqBody.getCounterFiledDt(), pwrFile1, pwrFile2, pwrFile3,
								abstractReqBody.getRelatedGp(), abstractReqBody.getParawiseRemarksSubmitted(),
								abstractReqBody.getParawiseRemarksDt(), abstractReqBody.getDtPRReceiptToGP(),
								abstractReqBody.getDtPRApprovedToGP(), abstractReqBody.getActionToPerform(), cIno);
					} else {

						query += assignedCasesToSectionRepo.insert_Pending_ecourts_olcms_case_details(cIno,
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getPetitionDocument(),
								abstractReqBody.getCounterFiledDt(), counterFile1, counterFile2, counterFile3,
								userId, abstractReqBody.getCounterFiled(), abstractReqBody.getRemarks(),
								abstractReqBody.getRelatedGp(), abstractReqBody.getParawiseRemarksSubmitted(),
								abstractReqBody.getParawiseRemarksDt(), abstractReqBody.getDtPRReceiptToGP(),
								abstractReqBody.getDtPRApprovedToGP(), pwrFile1, pwrFile2, pwrFile3,
								abstractReqBody.getActionToPerform());

					}
					System.out.println("SQL:====================insert ot update ---------" + query);

					if (roleId != null && (roleId.equals("4") || roleId.equals("5") || roleId.equals("10"))) {// MLO
						// /////
						// NO//
						// /////
						// Dist-NO

						query += assignedCasesToSectionRepo
								.update_Pending_ecourts_case_data4510(abstractReqBody.getEcourtsCaseStatus(), cIno);

					} else {

						query += assignedCasesToSectionRepo
								.update_Pending_ecourts_case_dataELSE(abstractReqBody.getEcourtsCaseStatus(), cIno);
					}
					System.out.println("ipAddress---" + ipAddress);

					query = assignedCasesToSectionRepo.insert_PendingFINALEcourts_case_activities(cIno,
							actionPerformed, userId, ipAddress, remarks);
					System.out.println("query=======final================================>>" + query);

					if (query > 0) {
						System.out.println("Case details updated successfully for Cino :" + cIno);
						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);

					} else {
						System.out.println("Error while updating the case details for Cino :" + cIno);
						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}

				} else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("Private")) {

					int a = 0;
					System.out.println("deptCode---" + deptCode);

					a += assignedCasesToSectionRepo.update_ecourts_case_dataPrivate(
							abstractReqBody.getEcourtsCaseStatus(), cIno, deptCode);

					System.out.println("private update query : " + a);

					a += assignedCasesToSectionRepo.insert_ecourts_case_activitiesPrivate(cIno, actionPerformed,
							userId, request.getRemoteAddr(), remarks);

					System.out.println("a----5-------------" + a);

					if (a > 0) {

						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);

					} else {

						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}

				} else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("GoI")) {

					int a = 0;
					System.out.println("deptCode---" + deptCode);
					a = assignedCasesToSectionRepo
							.update_ecourts_case_dataGoI(abstractReqBody.getEcourtsCaseStatus(), cIno, deptCode);
					System.out.println("GoI update query : " + a);

					a = assignedCasesToSectionRepo.insert_ecourts_case_activitiesGoI(cIno, actionPerformed, userId,
							request.getRemoteAddr(), remarks);

					System.out.println("a----5-------------" + a);

					if (a > 0) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);

					} else {

						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}

				} else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("CentralTax")) {

					int a = 0;
					System.out.println("deptCode---" + deptCode);
					a = assignedCasesToSectionRepo.update_ecourts_case_dataCentralTax(
							abstractReqBody.getEcourtsCaseStatus(), cIno, deptCode);
					System.out.println("CentralTax update query : " + a);

					a = assignedCasesToSectionRepo.insert_ecourts_case_activitiesCentralTax(cIno, actionPerformed,
							userId, request.getRemoteAddr(), remarks);

					System.out.println("update-----4------------" + a);

					if (a > 0) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);

					} else {

						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}

				} else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("IncomeTax")) {

					int a = 0;
					System.out.println("deptCode---" + deptCode);
					a = assignedCasesToSectionRepo.update_ecourts_case_dataIncomeTax(
							abstractReqBody.getEcourtsCaseStatus(), cIno, deptCode);
					System.out.println("IncomeTax update query : " + a);

					a = assignedCasesToSectionRepo.insert_ecourts_case_activitiesIncomeTax(cIno, actionPerformed,
							userId, request.getRemoteAddr(), remarks);
					System.out.println("update-----4------------" + a);

					if (a > 0) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);

					} else {

						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}

				} else if (abstractReqBody.getEcourtsCaseStatus() != null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("PSU")) {

					int a = 0;
					System.out.println("deptCode---" + deptCode);
					a = assignedCasesToSectionRepo
							.update_ecourts_case_dataPSU(abstractReqBody.getEcourtsCaseStatus(), cIno, deptCode);

					System.out.println("PSU update query : " + a);

					a = assignedCasesToSectionRepo.insert_ecourts_case_activitiesPSU(cIno, actionPerformed, userId,
							request.getRemoteAddr(), remarks);

					System.out.println("update-----4------------" + a);

					if (a > 0) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Case details updated successfully for Cino :" + cIno);

					} else {

						return CommonQueryAPIUtils.manualResponse("02",
								"Error while updating the case details for Cino :" + cIno);
					}
				}

			} else {
				return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return CommonQueryAPIUtils.catchResponse(e);
		}
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}

	@Override
	public ResponseEntity<Map<String, Object>> getforwardCaseDetails(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno) {

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

		try {
			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {

				int a = 0;
				if (roleId != null && roleId.equals("4")) {// FROM MLO to SECT DEPT.
					fwdOfficer = deptCode;
					newStatus = 1;
					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_Sec(newStatus, fwdOfficer, cIno);

					System.out.println("frwd_Sec---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to Secretary.";
				} else if (roleId != null && roleId.equals("5")) {// FROM NO TO HOD

					fwdOfficer = deptCode;
					newStatus = 3;

					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_Hod(newStatus, fwdOfficer, cIno);

					System.out.println("frwd_Hod---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to HOD.";
				} else if (roleId != null && roleId.equals("10")) {// FROM Dist-NO TO HOD
					fwdOfficer = deptCode;
					newStatus = 3;

					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_DcToHod(newStatus, fwdOfficer, cIno);

					System.out.println("frwd_Dc to Hod---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to HOD.";
				}

				else if ((roleId != null && roleId.equals("8"))
						&& (deptCode.substring(3, 5) == "01" || deptCode.substring(3, 5).equals("01"))) { // FROM //
					// SECTION-SECT.
					// // TO MLO

					String email = assignedCasesToSectionRepo.getFwrdOffMlo(deptCode);
					System.out.println("getFwrdOffMlo------" + email);
					fwdOfficer = email;
					newStatus = 2;

					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_Mlo(newStatus, fwdOfficer, cIno);

					System.out.println("frwd_Mlo---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to MLO.";
				} else if (roleId != null && roleId.equals("11")) {// FROM SECTION(HOD) TO NO-HOD

					String email = assignedCasesToSectionRepo.getFwrdOffNo(deptCode);
					System.out.println("getFwrdOffNo------" + email);

					fwdOfficer = email;
					newStatus = 4;

					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_No(newStatus, fwdOfficer, cIno);
					System.out.println("frwd_No---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to Nodal Officer.";
				} else if (roleId != null && roleId.equals("12")) {// FROM SECTION(DIST) TO NO-HOD-DIST

					String email = assignedCasesToSectionRepo.getFwrdOffDNo(deptCode, distId);
					System.out.println("getFwrdOffDNo------" + email);
					fwdOfficer = email;
					newStatus = 8;

					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_DNo(newStatus, fwdOfficer, cIno);
					System.out.println("frwd_DNo---" + a);
					msg = "Case details (" + cIno + ") forwarded successfully to Dist Nodal Officer.";
				}

				System.out.println("count====================================================================" + a);

				if (a > 0) {

					a = assignedCasesToSectionRepo.Insert_ecourts_case_activitiesFinalFrwd(cIno, userId,
							request.getRemoteAddr(), fwdOfficer, abstractReqBody.getRemarks());

					System.out.println("a----------" + a);
					a = assignedCasesToSectionRepo.Insert_ecourts_case_emp_assigned_dtlsFinalFrwd(cIno, deptCode,
							empSection, empPost, empId, request.getRemoteAddr(), userId, fwdOfficer);

					System.out.println("b----------" + a);
					return CommonQueryAPIUtils.manualResponse("01", msg);
				}
				return CommonQueryAPIUtils.manualResponse("02",
						"Error while forwarding the case details for Cino  (" + cIno + ") ");

			} else {
				return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}

	@Override
	public ResponseEntity<Map<String, Object>> getforwardCaseDetails2GP(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno) {

		String userId = null, roleId = null, deptCode = "", msg = "";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		System.out.println("deptCode------" + deptCode);
		int newStatus = 0;
		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);
		int a = 0;
		try {

			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {

				String fwdOfficer = abstractReqBody.getGpCode();
				System.out.println("gpo code---------" + fwdOfficer);

				if (roleId != null && roleId.equals("3")) {// FROM SECT DEPT TO GP.
					newStatus = 6;

					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_secToGp(newStatus, fwdOfficer, cIno);

					System.out.println("frwd_secToGp---" + a);

					msg = "Case details (" + cIno + ") forwarded successfully to GP for Approval.";

				} else if (roleId != null && roleId.equals("9")) {// FROM HOD TO GP
					newStatus = 6;
					System.out.println("newStatus----" + newStatus + "fwdOfficer---" + fwdOfficer + "-----" + cIno);
					a = assignedCasesToSectionRepo.ecourts_case_data_frwd_HodToGp(newStatus, fwdOfficer, cIno);

					System.out.println("frwd_HodToGp---" + a);

					msg = "Case details (" + cIno + ") forwarded successfully to GP for Approval.";
				}

				System.out.println("A COUNT:" + a);

				if (a > 0) {

					a = assignedCasesToSectionRepo.Insert_ecourts_case_activitiesFinalFrwdGP(cIno, userId,
							request.getRemoteAddr(), fwdOfficer, abstractReqBody.getRemarks());

					System.out.println("activitiesFinalFrwdGP-- " + a);
					return CommonQueryAPIUtils.manualResponse("01", msg);
				}
				return CommonQueryAPIUtils.manualResponse("02",
						"Error while forwarding the case details for Cino  (" + cIno + ") ");

			} else {
				return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CommonQueryAPIUtils.manualResponse("02", "Invalid Cino :" + cIno);
	}

	@Override
	public ResponseEntity<Map<String, Object>> getSendBackCaseDetails(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno) {

		String userId = null, roleId = null, deptCode = "", msg = "";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		System.out.println("deptCode------" + deptCode);
		int newStatus = 0;
		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);
		int a = 0;
		try {
			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {

				String backToSectionUser = assignedCasesToSectionRepo.getbackToSectionUser(cIno);

				System.out.println("sendBackCaseDetails--sql---" + backToSectionUser + "-" + roleId);

				if (roleId != null && roleId.equals("4")) {// FROM MLO to Section.
					newStatus = 5;

					a += assignedCasesToSectionRepo.update_ecourts_case_data_bck_MloToSection(newStatus,
							backToSectionUser, cIno);

					System.out.println("bck_MloToSection---" + a);

					msg = "Case details (" + cIno + ") returned back to Section successfully.";

				} else if (roleId != null && roleId.equals("5")) {// FROM NO TO Section
					newStatus = 9;

					a += assignedCasesToSectionRepo.update_ecourts_case_data_bck_NoToSection(newStatus,
							backToSectionUser, cIno);

					System.out.println("bck_NoToSection---" + a);

					msg = "Case details (" + cIno + ") returned back to Section successfully.";

				} else if (roleId != null && roleId.equals("10")) {// FROM Dist-NO TO Dist-Section
					newStatus = 10;

					a += assignedCasesToSectionRepo.update_ecourts_case_data_bck_DNoToDSection(newStatus,
							backToSectionUser, cIno);

					System.out.println("bck_DNoToDSection---" + a);

					msg = "Case details (" + cIno + ") returned back to Section Successfully.";

				}

				System.out.println("A COUNT---" + a);

				if (a > 0) {

					a += assignedCasesToSectionRepo.Insert_ecourts_case_activitiesFinalSendBack(cIno, userId,
							request.getRemoteAddr(), backToSectionUser, abstractReqBody.getRemarks());

					System.out.println("FinalSendBack-- " + a);

					return CommonQueryAPIUtils.manualResponse("01", msg);

				} else {
					return CommonQueryAPIUtils.manualResponse("02",
							"Error while returned the case details for Cino :" + cIno);
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
	public ResponseEntity<Map<String, Object>> getGPApprove(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno) {

		String userId = null, roleId = null, deptCode = "", msg = "";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		System.out.println("deptCode------" + deptCode);

		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);
		try {

			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {
				int a = 0;
				String actionPerformed = "";
				actionPerformed = !abstractReqBody.getActionToPerform().equals("")
						&& !abstractReqBody.getActionToPerform().equals("0")
						? abstractReqBody.getActionToPerform().toString() + " Approved"
								: "CASE DETAILS UPDATED";

				a = assignedCasesToSectionRepo.INSERTecourts_olcms_case_details_log(cIno);

				System.out.println("olcms_case_details_log---" + a);

				if (abstractReqBody.getActionToPerform().toString().equals("Parawise Remarks")) {

					a = assignedCasesToSectionRepo.update_ecourts_olcms_case_detailsPWRAPPROVE(cIno);

					System.out.println("PWRAPPROVE---" + a);

					msg = "Parawise Remarks Approved successfully for Case (" + cIno + ").";

				} else if (abstractReqBody.getActionToPerform().toString().equals("Counter Affidavit")) {

					a = assignedCasesToSectionRepo.update_ecourts_olcmsCOUNTERAFFIDAVITAPPROVE(userId, cIno);

					System.out.println("COUNTERAFFIDAVITAPPROVE---" + a);

					msg = "Counter Affidavit Approved successfully for Case (" + cIno + ").";

				} else if (abstractReqBody.getCounterFiled().equals("Yes")) {

					a = assignedCasesToSectionRepo.update_ecourts_olcmsCounterFiled(userId, cIno);

					System.out.println("CounterFiled---" + a);

					msg = "Counter Affidavit Approved successfully for Case (" + cIno + ").";

				} else if (abstractReqBody.getCounterFiled().equals("No")
						&& abstractReqBody.getParawiseRemarksSubmitted().equals("Yes")) {

					a = assignedCasesToSectionRepo.update_ecourts_olcmsParawiseRemarksSubmitted(cIno);

					System.out.println("ParawiseRemarksSubmitted---" + a);

					msg = "Parawise Remarks Approved successfully for Case (" + cIno + ").";
				}
				if (a > 0) {

					a = assignedCasesToSectionRepo.Insert_ecourts_case_activitiesFinalAPPROVE(cIno, actionPerformed,
							userId, request.getRemoteAddr(), abstractReqBody.getRemarks());

					System.out.println("FinalSendBack-- " + a);
					return CommonQueryAPIUtils.manualResponse("01", msg);
				} else {

					return CommonQueryAPIUtils.manualResponse("02",
							"Error while Approved the case details (" + cIno + ").");
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
	public ResponseEntity<Map<String, Object>> getGPReject(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno) {

		String userId = null, roleId = null, deptCode = "", msg = "";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		System.out.println("deptCode------" + deptCode);
		int newStatus = 0;
		System.out.println("deptCode::" + deptCode.substring(3, 5));
		System.out.println("roleId::" + roleId);

		try {

			System.out.println("Cino----start----------" + cIno);
			if (cIno != null && !cIno.equals("")) {
				int a = 0;
				String actionPerformed = "";
				actionPerformed = !abstractReqBody.getActionToPerform().equals("")
						? abstractReqBody.getActionToPerform().toString() + " Returned"
								: "CASE DETAILS UPDATED";

				String deptCodeC = "", distCodeC = "", assigned2Emp = "";

				List<Map<String, Object>> caseData = assignedCasesToSectionRepo.caseData(cIno);

				if (caseData != null) {

					deptCodeC = (String) caseData.get(0).get("dept_code");
					distCodeC = (String) caseData.get(0).get("dist_id");
					System.out.println("deptCodeC::" + deptCodeC);
					System.out.println("distCodeC::" + distCodeC);
					if (deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION SECT
						// DEPT
						newStatus = 5;

						assigned2Emp = assignedCasesToSectionRepo.ScetionSecDept(cIno, deptCodeC);
						msg = "Returned Case to Section Officer (Sect. Dept.)";

					} else if (!deptCodeC.contains("01") && (distCodeC.equals("") || distCodeC.equals("0"))) {// SECTION
						// HOD
						newStatus = 9;

						assigned2Emp = assignedCasesToSectionRepo.ScetionHOD(cIno, deptCodeC);
						msg = "Returned Case to Section Officer (HOD)";

					} else if (!distCodeC.equals("") && !distCodeC.equals("0")) {// SECTION DIST
						newStatus = 10;
						assigned2Emp = assignedCasesToSectionRepo.ScetionDIST(cIno, deptCodeC);
						msg = "Returned Case to Section Officer (District)";
					}

					System.out.println("assigned2Emp---" + assigned2Emp);
				}

				if (abstractReqBody.getActionToPerform().toString().equals("Parawise Remarks")) {

					a = assignedCasesToSectionRepo.ReturnBackPWR(newStatus, assigned2Emp, cIno);

					msg = "Parawise Remarks Returned for Case (" + cIno + "). ";

				} else if (abstractReqBody.getActionToPerform().toString().equals("Counter Affidavit")) {

					a = assignedCasesToSectionRepo.ReturnBackCOUNTERAFFIDAVIT(newStatus, assigned2Emp, cIno);

					msg = "Counter Affidavit Returned for Case (" + cIno + "). ";

				} else if (abstractReqBody.getCounterFiled().equals("Yes")) {

					a = assignedCasesToSectionRepo.ReturnBackCounterFiled(newStatus, assigned2Emp, cIno);

					a = assignedCasesToSectionRepo.ReturnBackcounter_approved_gp(cIno);

					msg = "Counter Affidavit Returned for Case (" + cIno + "). ";

				} else if (abstractReqBody.getCounterFiled().equals("No")
						&& abstractReqBody.getParawiseRemarksSubmitted().equals("Yes")) {

					a = assignedCasesToSectionRepo.ReturnBackParawiseRemarksSubmitted(newStatus, assigned2Emp, cIno);

					msg = "Parawise Remarks Returned for Case (" + cIno + "). ";

				}

				a = assignedCasesToSectionRepo.ReturnBackecourts_olcms_case_details_log(cIno);

				System.out.println("a count----" + a);
				if (a > 0) {

					a = assignedCasesToSectionRepo.ReturnBackecourts_olcms_case_details_log(cIno, actionPerformed,
							userId, request.getRemoteAddr(), abstractReqBody.getRemarks(), assigned2Emp);
					return CommonQueryAPIUtils.manualResponse("01", msg);
				} else {

					return CommonQueryAPIUtils.manualResponse("02",
							"Error while Returned the case details (" + cIno + ").");
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
	public String getassignMultiCases2SectionLegacy(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		String userId = null, roleId = null, login_deptId = "", msg = "" ;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		login_deptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int user_dist = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		System.out.println("user_dist---"+user_dist);
		try {
			if (abstractReqBody.getSelectedCaseIds() != null && !abstractReqBody.getSelectedCaseIds().isEmpty()
					&& abstractReqBody.getEmployeeId() != null && !abstractReqBody.getEmployeeId().isEmpty()
					&& !abstractReqBody.getEmployeeId().equals("0")) {

				String emailId = abstractReqBody.getEmployeeId().toString();

				System.out.println("emailId:"+emailId);

				for (String cIno : abstractReqBody.getSelectedCaseIds().toString().split(",")) {
					if (cIno != null && !cIno.equals("")) {

						int newStatusCode = 0;
						String activityDesc = "";
						if ((user_dist) > 0) { // Dist. - Section Officer
							newStatusCode = 10;
							activityDesc = "CASE ASSSIGNED TO Section Officer (District)";
						} else if (login_deptId.contains("01")) { // Sect. Dept. - Section Officer
							newStatusCode = 5;
							activityDesc = "CASE ASSSIGNED TO Section Officer (Sect. Dept.)";
						} else { // HOD - Section Officer.
							newStatusCode = 9;
							activityDesc = "CASE ASSSIGNED TO Section Officer (HOD)";
						}

						int a = assignedCasesToSectionRepo.ecourts_case_dataMultiCases2SectionLegacy(login_deptId,emailId,newStatusCode,user_dist,cIno);
						System.out.println("a---1--"+a);
						a += assignedCasesToSectionRepo.insert_case_activitiesMultiCases2SectionLegacy(cIno,activityDesc,userId,request.getRemoteAddr(),emailId,abstractReqBody.getCaseRemarks(),user_dist);
						System.out.println("a---2--"+a);
						if (a > 0) {
							msg ="Case successfully Assigned to Selected Employee.";
						} else {
							msg ="Error : Case assignment failed .</font>";
						}
					}else {
						msg ="Error in cIno Number. Kindly try again.";
					}
				}
			} else {
				msg ="Error in assigning Cases. Kindly try again.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg="Error in assigning Case to Department / HOD. Kindly try again.";
		}
		return msg;
	}
	@Override
	public String getAssignToDeptHODSendBackLegacy(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		String userId = null,   msg = "" ;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		int user_dist = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		System.out.println("user_dist---"+user_dist);
		try {
			String selectedCaseIds = "";
			String[] ids_split = null;

			String caseIds = abstractReqBody.getSelectedCaseIds();
			String deptCode = CommonModels.checkStringObject(abstractReqBody.getSendBack_dept_code());

			if (caseIds != null && !caseIds.isEmpty() && deptCode != null && !deptCode.equals("0") &&  !deptCode.isEmpty()  ) {
				for (String newCaseId : CommonModels.checkStringObject(abstractReqBody.getSelectedCaseIds())
						.split(",")) {

					String ids = newCaseId;
					ids_split = ids.split("@");
					System.out.println("ids--" + ids_split[0]);

					selectedCaseIds += "'" + ids_split[0] + "',";

					int a = assignedCasesToSectionRepo.DeptHODSendBackLegacyCase_activities(ids_split[0],userId,request.getRemoteAddr(),abstractReqBody.getSendBack_dept_code());
					System.out.println(a + ":ACTIVITIES SQL:" + a);

					String caseNewDept = CommonModels.checkStringObject(abstractReqBody.getSendBack_dept_code());
					int newStatusCode = 4;
					int dist_id=0;
					if (caseNewDept.contains("01")) {
						newStatusCode = 2;
						dist_id=0;
					} else if(user_dist==0) {
						newStatusCode = 4;
						dist_id=0;
					}else {
						newStatusCode = 8;
						dist_id=user_dist;
					}

					a += assignedCasesToSectionRepo.DeptHODSendBackLegacy_ecourts_case_data(abstractReqBody.getSendBack_dept_code(),newStatusCode,dist_id,ids_split[0]);
					System.out.println("ecourts_case_data----"+a);
				}

				msg="Case/Cases successfully moved to selected Department / MLO/Dist Nodal /Nodal.";

			} else
				msg="Error : Case sendback failed .</font>";
		}  catch (Exception e) {
			e.printStackTrace();
			msg="Error in assigning Case to Department / HOD. Kindly try again.";
		}
		return msg;
	}
	public List<Map<String, Object>> getEMPList(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String part="" , sql="";
		try {

			if(roleId.equals("8")){ //sec Section Officer
				part =" a.DEPT_CODE='" + deptCode +"'  and user_type='8'";
			}else if(roleId.equals("11") ) { //Section Officer (HOD)
				part =" a.DEPT_CODE='" + deptCode +"' and user_type='11' ";
			}else {
				part =" a.dist_id='" + distCode +"' and user_type='12' and a.dept_code='"+deptCode+"'   ";   //Section Officer (District)
			}

			sql = "select userid as value,user_description as label from users a where "+part+" order by user_description";

			System.out.println("sql--emp---"+sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);

	}


	public List<Map<String, Object>> getNoList(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String part="" , sql="";
		try {

			if(roleId.equals("8")){ //MLO
				part =" DEPT_CODE='" + deptCode +"'  and user_type='4'";
			}else if(roleId.equals("11") ) { //NODAL
				part =" DEPT_CODE='" + deptCode +"' and user_type='5' ";
			}else {
				part =" dept_code='"+deptCode+"' and user_type='10'  and dist_id='" + distCode +"' ";   //DNO
			}

			sql = "select dept_code as value,userid||'----'||user_description as label from users where  "+part+" order by user_description";

			System.out.println("sql--emp---"+sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql);

	}
}
