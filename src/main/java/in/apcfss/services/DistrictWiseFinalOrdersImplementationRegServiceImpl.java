package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.repositories.DistrictWiseFinalOrdersImplementationRegRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class DistrictWiseFinalOrdersImplementationRegServiceImpl implements DistrictWiseFinalOrdersImplementationRegService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	DistrictWiseFinalOrdersImplementationRegRepo FinalOrdersImplRepo;

	@Autowired
	AssignedCasesToSectionRepo assignedCasesToSectionRepo;

	@Override
	public List<Map<String, Object>> getCASESLIST(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String  sql="",condition="";

		try {
			System.out.println("roleId===="+roleId);
			if (roleId != null && roleId.equals("4")) { // MLO
				condition += " and a.dept_code='" + deptCode + "' and a.case_status=2";
			} else if (roleId != null && roleId.equals("5")) { // NO
				condition += " and a.dept_code='" + deptCode + "' and a.case_status=4";
			} else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				condition += " and a.dept_code='" + deptCode + "' and a.case_status=5 and a.assigned_to='" + userId
						+ "'";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				condition += " and a.dept_code='" + deptCode + "' and a.case_status=9 and a.assigned_to='" + userId
						+ "'";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				condition +=" and a.dept_code='" + deptCode + "' and dist_id='" + distId
						+ "' and a.case_status=10 and a.assigned_to='" + userId + "'";
			}

			else if (roleId != null && roleId.equals("3")) { // SECT DEPT
				condition += " and a.dept_code='" + deptCode + "' and a.case_status=1";
			} else if (roleId != null && roleId.equals("9")) { // HOD
				condition += " and a.dept_code='" + deptCode + "' and a.case_status=3";
			} else if (roleId != null && roleId.equals("2")) { // DC
				condition += " and a.case_status=7 and dist_id='" + distId + "'";
			} else if (roleId != null && roleId.equals("10")) { // DC-NO
				condition += " and a.dept_code='" + deptCode + "'  and a.dist_id='" + distId + "'"; // and
				// a.case_status=8
			} else if (roleId != null && roleId.equals("6")) { // GPO

				String counter_pw_flag = CommonModels.checkStringObject(abstractReqBody.getPwCounterFlag());

				condition += " and a.case_status=6 and e.gp_id='" + userId + "' ";

				if (counter_pw_flag.equals("PR")) {
					condition += " and pwr_uploaded='No' and (coalesce(pwr_approved_gp,'0')='0' or coalesce(pwr_approved_gp,'No')='No' )";
				}
				if (counter_pw_flag.equals("COUNTER")) {

					condition += " and pwr_uploaded='Yes' and counter_filed='No' and coalesce(counter_approved_gp,'F')='F'";
				}
			}

			sql = "select a.*, b.orderpaths , prayer, ra.address   from ecourts_case_data a "
					+ " left join nic_prayer_data np on (a.cino=np.cino) "
					+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1)  left join" + " ("
					+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
					+ " from "
					+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0  order by cino, order_date desc) c group by cino ) b"
					+ " on (a.cino=b.cino) " + " left join ecourts_olcms_case_details od on (a.cino=od.cino)"
					+ " where b.orderpaths is not null and coalesce(a.ecourts_case_status,'')!='Closed' and assigned=true "
					+ condition + " order by a.cino ";

			System.out.println("AssignedCasesToSectionAction unspecified SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public Map<String, Object> getCaseStatusUpdate(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cIno) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		String sql="";
		try {
			if (cIno != null && !cIno.equals("")) {

				System.out.println("IN CASE STATUS UPDATE METHOD :" + cIno);

				List<Map<String, Object>> data = FinalOrdersImplRepo.finalCasesImplData(cIno);

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
							&& CommonModels.checkStringObject(data.get(0).get("mlo_no_updated")).equals("T")) {
						// MLO TO SECT DEPT
						map.put("SHOWSECDEPTBTN", "SHOWSECDEPTBTN");
					} else if (roleId != null && (roleId.equals("5") || roleId.equals("10"))
							&& CommonModels.checkStringObject(data.get(0).get("mlo_no_updated")).equals("T")) {
						// NO TO HOD/DEPT
						map.put("SHOWHODDEPTBTN", "SHOWHODDEPTBTN");
					} else if ((roleId.equals("3") || roleId.equals("9"))
							&& CommonModels.checkStringObject(data.get(0).get("mlo_no_updated")).equals("T")) {

						getGPSList(authentication);
						System.out.println("gpslist---" + getGPSList(authentication));
						map.put("SHOWGPBTN", "SHOWGPBTN");
					} else if (roleId.equals("6")) { // GP LOGIN
						map.put("SHOWGPAPPROVEBTN", "SHOWGPAPPROVEBTN");
					}
					map.put("USERSLIST", data);
				}

				data = assignedCasesToSectionRepo.getOLCMSCASEDATAFINALandINTERIM(cIno);
				map.put("OLCMSCASEDATA", data);


				List<Map<String, Object>> data_final = assignedCasesToSectionRepo.getFinalData(cIno);
				map.put("data_final", data_final);


				map.put("HEADING", "Case Details for CINO : " + cIno);
				map.put("fileCino", cIno);

				if (data != null) {

					if (data_final != null && !data_final.isEmpty() && data_final.size() > 0) {
						/// request.setAttribute("RESEXTRAPARTYLIST", data);
						map.put("final_order", (data_final.get(0).get("order_document_path")).toString());
					}

					abstractReqBody.setJudgementOrderOld(CommonModels.checkStringObject(data.get(0).get("judgement_order")));
					map.put("judgementOrderOld", abstractReqBody.getJudgementOrderOld());
					abstractReqBody.setActionTakenOrderOld(CommonModels.checkStringObject(data.get(0).get("action_taken_order")));

					map.put("actionTakenOrderOld", abstractReqBody.getActionTakenOrderOld());
					abstractReqBody.setDismissedFileCopyOld(CommonModels.checkStringObject(data.get(0).get("dismissed_copy")));

					map.put("dismissedFileCopyOld", abstractReqBody.getDismissedFileCopyOld());


					abstractReqBody.setRemarks(data.get(0).get("remarks")+"");
					abstractReqBody.setEcourtsCaseStatus(data.get(0).get("final_order_status")+"");
					abstractReqBody.setImplementedDt(data.get(0).get("cordered_impl_date")+"");

					abstractReqBody.setAppealFileCopyOld(CommonModels.checkStringObject(data.get(0).get("appeal_filed_copy")));

					map.put("appealFileCopyOld", abstractReqBody.getAppealFileCopyOld());
					abstractReqBody.setAppealFiledDt(data.get(0).get("appeal_filed_date")+"");

					map.put("STATUSUPDATEBTN", "STATUSUPDATEBTN");

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
	public String getupdateCaseDetails(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno) {

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		String msg="";

		int a=0;
		try {
			if (cIno != null && !cIno.equals("")) {
				String actionPerformed = "";

				actionPerformed = (abstractReqBody.getActionToPerform() != null
						&& !abstractReqBody.getActionToPerform().equals(""))
						? abstractReqBody.getActionToPerform().toString()
								: "CASE DETAILS UPDATED";
				List<Map<String, Object>> data = assignedCasesToSectionRepo.getOLCMSCASEDATAForUpdate(cIno);
				// STATUS final
				if (abstractReqBody.getEcourtsCaseStatus()!= null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("final")) {

					if (abstractReqBody.getAppealFileCopy()!= null
							&& !abstractReqBody.getAppealFileCopy().toString().equals("")) {

						a=FinalOrdersImplRepo.AppealFileCopyEMPTY(cIno);

					}

					if (abstractReqBody.getActionTakenOrder() != null
							&& !abstractReqBody.getActionTakenOrder().toString().equals("")) {


						System.out.println("ActionTakenOrder is" + abstractReqBody.getActionTakenOrder());

					} else {

						System.out.println(" old ActionTakenOrder is" + data.get(0).get("action_taken_order"));

						abstractReqBody.setActionTakenOrder(data.get(0).get("action_taken_order"));
					}

					a = FinalOrdersImplRepo.insert_activitiesFINALActionTakenOrder(cIno, userId,
							request.getRemoteAddr(), abstractReqBody.getRemarks(), abstractReqBody.getActionTakenOrder());


					if (abstractReqBody.getJudgementOrder()!= null
							&& !abstractReqBody.getJudgementOrder().toString().equals("")) {

						System.out.println("getJudgementOrder is" + abstractReqBody.getJudgementOrder());

					} else {

						System.out.println(" old getJudgementOrder is" + data.get(0).get("judgement_order"));

						abstractReqBody.setJudgementOrder(data.get(0).get("judgement_order"));
					}

					a = FinalOrdersImplRepo.insert_activitiesFINALJudgementOrder(cIno, userId,
							request.getRemoteAddr(), abstractReqBody.getRemarks(), abstractReqBody.getJudgementOrder());


					if (FinalOrdersImplRepo.getCinoCount(cIno) > 0) {

						a = FinalOrdersImplRepo.ecourts_olcms_case_details_log(cIno);

						a = FinalOrdersImplRepo.update_ecourts_olcms_case_details(
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getJudgementOrder(),abstractReqBody.getImplementedDt(),
								abstractReqBody.getRemarks(),userId,abstractReqBody.getActionTakenOrder(),cIno);

					} else {

						a = FinalOrdersImplRepo.insert_ecourts_olcms_case_details(
								cIno,abstractReqBody.getEcourtsCaseStatus(),abstractReqBody.getJudgementOrder(),abstractReqBody.getActionTakenOrder(),userId,
								abstractReqBody.getRemarks(),abstractReqBody.getImplementedDt());
					}

					System.out.println("SQL:" + a);

					a += FinalOrdersImplRepo.update_ecourts_case_data_Implemented(cIno);

					a += FinalOrdersImplRepo.insert_ecourts_case_activities_Implemented(cIno,actionPerformed,userId,request.getRemoteAddr(),abstractReqBody.getRemarks());

					if (a > 0) {
						msg="Case details updated successfully for Cino :" + cIno;
					} else {
						msg="Error while updating the case details for Cino :" + cIno;
					}

				}else if (abstractReqBody.getEcourtsCaseStatus()!= null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("appeal")) {

					if (abstractReqBody.getAppealFileCopy() != null
							&& !abstractReqBody.getAppealFileCopy().toString().equals("")) {

						//updateSql += ", appeal_filed_copy='" + appeal_filed_copy + "'";

						System.out.println("ActionTakenOrder is" + abstractReqBody.getAppealFileCopy());

					} else {

						System.out.println(" old appeal_filed_copy is" + data.get(0).get("appeal_filed_copy"));

						abstractReqBody.setAppealFileCopy(data.get(0).get("appeal_filed_copy"));
					}


					a = FinalOrdersImplRepo.insert_activitiesFINALappealFileCopy(cIno, userId,
							request.getRemoteAddr(), abstractReqBody.getRemarks(), abstractReqBody.getAppealFileCopy());

					if (FinalOrdersImplRepo.getCinoCount(cIno) > 0) {

						a = FinalOrdersImplRepo.ecourts_olcms_case_details_log(cIno);

						a = FinalOrdersImplRepo.update_ecourts_olcms_case_detailsAPPEAL(
								abstractReqBody.getEcourtsCaseStatus(), abstractReqBody.getAppealFileCopy(),abstractReqBody.getAppealFiledDt(),
								userId,abstractReqBody.getRemarks(),cIno);

					} else {

						a = FinalOrdersImplRepo.insert_ecourts_olcms_case_detailsAPPEAL(
								cIno,abstractReqBody.getEcourtsCaseStatus(),abstractReqBody.getAppealFileCopy(),userId,abstractReqBody.getRemarks(),abstractReqBody.getAppealFiledDt());

					}

					if(roleId.equals("4"))
					{
						a += FinalOrdersImplRepo.UPDATEecourts_case_dataR4(cIno);
					}
					else if(roleId.equals("9"))
					{
						a += FinalOrdersImplRepo.UPDATEecourts_case_dataR9(cIno);
					}
					else {
						a += FinalOrdersImplRepo.UPDATEecourts_case_dataELSE(cIno);

					}
					System.out.println("final a=========>"+a);
					if (a > 0) {
						msg="Case details updated successfully for Cino :" + cIno;
					} else {
						msg="Error while updating the case details for Cino :" + cIno;
					}

				}else if (abstractReqBody.getEcourtsCaseStatus()!= null
						&& abstractReqBody.getEcourtsCaseStatus().toString().equals("dismissed")) {

					if (abstractReqBody.getAppealFileCopy()!= null
							&& !abstractReqBody.getAppealFileCopy().toString().equals("")) {
						a = FinalOrdersImplRepo.UPDATEecourts_olcms_case_detailsDismissed(cIno);

					}

					if (abstractReqBody.getDismissedFileCopy() != null
							&& !abstractReqBody.getDismissedFileCopy().toString().equals("")) {

						System.out.println("getDismissedFileCopy is" + abstractReqBody.getDismissedFileCopy());

					} else {

						System.out.println(" old dismissed_copy is" + data.get(0).get("dismissed_copy"));

						abstractReqBody.setDismissedFileCopy(data.get(0).get("dismissed_copy")+"");
					}


					a = FinalOrdersImplRepo.insert_activitiesFINALDismissedFileCopy(cIno, userId,
							request.getRemoteAddr(), abstractReqBody.getRemarks(), abstractReqBody.getDismissedFileCopy());


					if (FinalOrdersImplRepo.getCinoCount(cIno) > 0) {
						
						a = FinalOrdersImplRepo.ecourts_olcms_case_details_log(cIno);

						a = FinalOrdersImplRepo.update_ecourts_olcms_case_detailsDismissedFileCopy(
								abstractReqBody.getEcourtsCaseStatus(),
								abstractReqBody.getRemarks(),abstractReqBody.getDismissedFileCopy(),userId,cIno);

					} else {
						
						a = FinalOrdersImplRepo.insert_ecourts_olcms_case_detailsDismissedFileCopy(
								cIno,abstractReqBody.getEcourtsCaseStatus(),abstractReqBody.getDismissedFileCopy(),userId,abstractReqBody.getRemarks() );

					}

					a += FinalOrdersImplRepo.update_ecourts_case_dataDismissedFileCopy(cIno);

					if (a > 0) {
						msg="Case details updated successfully for Cino :" + cIno;
					} else {
						msg="Error while updating the case details for Cino :" + cIno;
					}

				} 

			}else {
				msg="Invalid Cino :" + cIno;
			}
		}  catch (Exception e) {

			msg="Error in Submission. Kindly try again.";
			e.printStackTrace();
		}

		return msg;
	}

	@GetMapping("/getGPSList")
	public List<Map<String, Object>> getGPSList(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode();
		List<Map<String, Object>> rawList = assignedCasesToSectionRepo.getGPSList(deptCode);
		List<Map<String, Object>> responseList = new ArrayList<>();

		for (Map<String, Object> rawMap : rawList) {
			Map<String, Object> map = new HashMap<>();
			for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
			responseList.add(map);
		}

		return responseList;
	}


}
