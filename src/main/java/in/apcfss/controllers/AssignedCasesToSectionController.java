package in.apcfss.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AssignedCasesToSectionService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "Assigned Cases To Section Controller - Access, Refresh", description = "Assigned Cases To Section")
public class AssignedCasesToSectionController {

	@Autowired
	AssignedCasesToSectionService service;
	
	@Autowired
	AssignedCasesToSectionRepo assignedCasesToSectionRepo;
	

	@GetMapping("AssignedCasesToSection")
	public Map<String, Object> getAssignedCasesToSection(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("AssignedCasesToSection: ");
		Map<String, Object> response = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId = userPrincipal.getRoleId();
			if (roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {
				service.getEMPList(userPrincipal);
				service.getNoList(userPrincipal);
			}

			response = CommonQueryAPIUtils.apiService("data",
					service.getAssignedCasesToSectionList(userPrincipal, abstractReqBody));

		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;
	}

	@GetMapping("AssignedCasesToSections")
	public Map<String, Object> getCino(Authentication authentication, @RequestParam("cino") String cino,
			@RequestParam("SHOWPOPUP") String SHOWPOPUP, @RequestParam("fileCino") String fileCino) {

		System.out.println("getCino: " + cino);
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());

			String cIno = "";

			String viewDisplay = SHOWPOPUP;

			if (!viewDisplay.equals("") && viewDisplay.equals("SHOWPOPUP")) {
				cIno = cino;
			} else {
				cIno = fileCino;
				System.out.println("cino----------" + cIno);
			}

			if (cIno != null && !cIno.equals("")) {

				List<Map<String, Object>> data = service.getUSERSLIST(cIno);

				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("USERSLIST", data);

				}
				data = service.getDocumentsList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("DocumentsList", data);

				}

				data = service.getActlist(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("actlist", data);
				}

				data = service.getOrderList(cIno);

				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("orderlist", data);
				}

				data = service.getIAFILINGLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("IAFILINGLIST", data);
				}

				data = service.getINTERIMORDERSLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("INTERIMORDERSLIST", data);
				}

				data = service.getLINKCASESLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("LINKCASESLIST", data);
				}

				data = service.getOBJECTIONSLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("OBJECTIONSLIST", data);
				}

				data = service.getCASEHISTORYLIST(cIno);

				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("CASEHISTORYLIST", data);
				}

				data = service.getPETEXTRAPARTYLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("PETEXTRAPARTYLIST", data);
				}

				data = service.getRESEXTRAPARTYLIST(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("RESEXTRAPARTYLIST", data);
				}

				data = service.getACTIVITIESDATA(cIno);
				map.put("ACTIVITIESDATA", data);
				data = service.getOLCMSCASEDATA(cIno);
				map.put("OLCMSCASEDATA", data);

				map.put("HEADING", "Case Details for CINO : " + cIno);
				map.put("fileCino", cIno);

			} else {
				map.put("HEADING", "Invalid Cino.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return map;
	}

	@GetMapping("caseStatusUpdate")
	public Map<String, Object> getcaseStatusUpdate(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, @RequestParam("cino") String cino) {
		System.out.println("caseStatusUpdate: ");
		Map<String, Object> map = new HashMap<>();
		String roleId = null, cIno = null, deptCode = "";
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		// String userId = userPrincipal.getUserId() != null ?
		// userPrincipal.getUserId().toString() : "";
		roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		deptCode = (String) userPrincipal.getDeptCode();
		List<Map<String, Object>> dataList = new ArrayList<>();
		System.out.println("cino---" + cino);
		System.out.println("roleId---" + roleId);
		System.out.println("deptCode---" + deptCode);

		String subDeptCode = "";

		if (deptCode != null) {
			deptCode = deptCode.trim();

			if (deptCode.length() >= 5) {
				subDeptCode = deptCode.substring(3, 5);
			}
		}

		System.out.println("dept code-3,5: [" + subDeptCode + "]");

		try {
			if (cino == null) {
				cIno = abstractReqBody.getFileCino();
				System.out.println("cIno--------" + cIno);
			} else {
				cIno = cino;
			}

			if (cIno != null && !cIno.equals("")) {

				List<Map<String, Object>> data = service.getUsersListForUpdate(authentication, cIno);
				Map<String, Object> row = data.get(0);
				Object section_officer_updated = row.get("section_officer_updated");
				Object mlo_no_updated = row.get("mlo_no_updated");

				System.out.println("section_officer_updated---" + section_officer_updated);
				System.out.println("mlo_no_updated---" + mlo_no_updated);

				if (data != null && !data.isEmpty() && data.size() > 0) {

					if (roleId != null && (roleId.equals("4") || roleId.equals("5") || roleId.equals("10"))) {
						map.put("SHOWBACKBTN", "SHOWBACKBTN");
					}
					if (roleId != null && roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {

						System.out.println("OUT deptCode:" + deptCode);
						System.out.println("dept code-3,5:" + subDeptCode);
						// if (row.get("section_officer_updated").equals("T")) {
						if (section_officer_updated != null && "T".equals(section_officer_updated.toString().trim())) {

							System.out.println("IN deptCode:" + deptCode);
							System.out.println("dept code-3,5:[" + subDeptCode + "]");

							if ("01".equals(subDeptCode)) {
								map.put("SHOWMLOBTN", "SHOWMLOBTN");
							} else {
								System.out.println("SHOWNOBTN---" + subDeptCode);
								map.put("SHOWNOBTN", "SHOWNOBTN");
							}
						}

					} else if (roleId != null && roleId.equals("4") && "T".equals(mlo_no_updated.toString().trim())) {
						// MLO TO SECT DEPT
						map.put("SHOWSECDEPTBTN", "SHOWSECDEPTBTN");
					} else if (roleId != null && (roleId.equals("5") || roleId.equals("10"))
							&& "T".equals(mlo_no_updated.toString().trim())) {
						// NO TO HOD/DEPT
						map.put("SHOWHODDEPTBTN", "SHOWHODDEPTBTN");
					} else if ((roleId.equals("3") || roleId.equals("9"))
							&& ("T".equals(mlo_no_updated.toString().trim()))) {
						System.out.println("mlo_no_updated-------"+mlo_no_updated);
						
						getGPSList(authentication);
						System.out.println("gpslist---" + getGPSList(authentication));
						 
						map.put("SHOWGPBTN", "SHOWGPBTN");
					} else if (roleId.equals("6")) { // GP LOGIN
						map.put("SHOWGPAPPROVEBTN", "SHOWGPAPPROVEBTN");
					}
					map.put("USERSLIST", data);
				}
				data = service.getActlistForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("actlist", data);
				}

				data = service.getOrderlistForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("orderlist", data);
				}
				data = service.getIAFILINGLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("IAFILINGLIST", data);
				}
				data = service.getINTERIMORDERSLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("INTERIMORDERSLIST", data);
				}
				data = service.getLINKCASESLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("LINKCASESLIST", data);
				}
				data = service.getOBJECTIONSLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("OBJECTIONSLIST", data);
				}
				data = service.getCASEHISTORYLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("CASEHISTORYLIST", data);
				}
				data = service.getPETEXTRAPARTYLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("PETEXTRAPARTYLIST", data);
				}
				data = service.getRESEXTRAPARTYLISTForUpdate(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("RESEXTRAPARTYLIST", data);
				}
				data = service.getACTIVITIESDATAForUpdate(cIno);

				map.put("ACTIVITIESDATA", data);

				// List<Map<String, Object>> data_status = service.getDataStatus(cIno);
				List<Map<String, Object>> data_status = service.getDataStatus(cIno);
				map.put("OLCMSCASEDATA", data_status);
				String caseStatus = data_status.get(0).get("ecourts_case_status") + "";

				System.out.println("caseStatus--" + caseStatus);

				data = service.getOLCMSCASEDATAForUpdate(cIno);

				map.put("OLCMSCASEDATA", data);

				if (data != null && !data.isEmpty()) {
					System.out.println("olcmscasedata    :  " + data);

					abstractReqBody.setPetitionDocumentOld(data.get(0).get("petition_document")+"");
					map.put("petitionDocumentOld", data.get(0).get("petition_document") + "");

					abstractReqBody.setCounterFileCopyOld(data.get(0).get("counter_filed_document")+"");

					map.put("counterFileCopyOld", data.get(0).get("counter_filed_document"));

					map.put("counterFileCopyOld2", data.get(0).get("counter_filed_document2"));

					map.put("counterFileCopyOld3", data.get(0).get("counter_filed_document3"));

					abstractReqBody.setJudgementOrderOld( data.get(0).get("judgement_order")+"");

					map.put("judgementOrderOld", data.get(0).get("judgement_order"));

					abstractReqBody.setActionTakenOrderOld(data.get(0).get("action_taken_order")+"");
					map.put("actionTakenOrderOld", data.get(0).get("action_taken_order"));

					data.get(0).get("last_updated_by"); // formbean.setDyna("" ,
					data.get(0).get("last_updated_on");
					abstractReqBody.setCounterFiled(data.get(0).get("counter_filed")+"");
					abstractReqBody.setRemarks(data.get(0).get("remarks")+"");

					abstractReqBody.setEcourtsCaseStatus(data.get(0).get("ecourts_case_status")+"");

					map.put("ecourtsCaseStatus", abstractReqBody.getEcourtsCaseStatus());

					abstractReqBody.setParawiseRemarksSubmitted(data.get(0).get("pwr_uploaded")+"");
					abstractReqBody.setParawiseRemarksCopyOld(data.get(0).get("pwr_uploaded_copy")+"");
					map.put("parawiseRemarksCopyOld", data.get(0).get("pwr_uploaded_copy"));

					map.put("parawiseRemarksCopyOld2", data.get(0).get("pwr_uploaded_copy2"));

					map.put("parawiseRemarksCopyOld3", data.get(0).get("pwr_uploaded_copy3"));

					abstractReqBody.setParawiseRemarksDt(data.get(0).get("pwr_submitted_date")+"");
					abstractReqBody.setDtPRReceiptToGP(data.get(0).get("pwr_received_date")+"");
					abstractReqBody.setPwr_gp_approved(data.get(0).get("pwr_approved_gp")+"");
					abstractReqBody.setDtPRApprovedToGP(data.get(0).get("pwr_gp_approved_date")+"");
					abstractReqBody.setAppealFiled(data.get(0).get("appeal_filed")+"");

					abstractReqBody.setAppealFileCopyOld(data.get(0).get("appeal_filed_copy")+"");

					map.put("appealFileCopyOld", data.get(0).get("appeal_filed_copy"));

					System.out.println("appealFileCopyOld---------- " + data.get(0).get("appeal_filed_copy"));

					abstractReqBody.setAppealFiledDt(data.get(0).get("appeal_filed_date")+"");
					abstractReqBody.setActionToPerform(data.get(0).get("action_to_perfom")+"");

					map.put("actionToPerform", data.get(0).get("action_to_perfom"));

					map.put("STATUSUPDATEBTN", "STATUSUPDATEBTN");
				}
				dataList.add(map);
				map.put("HEADING", "Update Status for Case :" + cIno);
				
				if (cIno != null) {
				    dataList.add(map); // Add after populating map

				    // Optionally, if you're returning this list in a response map:
				    Map<String, Object> response = new HashMap<>();
				    response.put("data", dataList);
				    response.put("status", true);
				    response.put("scode", "01");
				    response.put("sdesc", "Data found");
				    return response;
				}  
				
			} else {
				map.put("HEADING", "Invalid Cino. / No Records Found to display.");
				map.put("sdesc", "No Data Found");
				map.put("status", false);
				map.put("scode", "02");
			}

		 
				
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}  

		return map;
	}

	@PostMapping("/updateCaseDetails")
	public Map<String, Object> updateCaseDetails(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<>();
		String cIno = abstractReqBody.getFileCino();

		if (cIno != null && !cIno.isEmpty()) {
			// Get data from both methods
			ResponseEntity<Map<String, Object>> updateDetails = service.getupdateCaseDetails(authentication,
					abstractReqBody, request, cIno);

			response.put("caseStatus", updateDetails.getBody());
			// response.put("cinoData", cinoData);
			Map<String, Object> updateBody = updateDetails.getBody();
			String scode = updateBody != null ? (String) updateBody.get("scode") : null;

			if ("01".equals(scode)) {

				return getcaseStatusUpdate(authentication, abstractReqBody, cIno);
			}
			else {
				response.put("Status", false);
				response.put("scode", "02");
				response.put("getData", "not updated");
				return response;
			}
		} else {
			response.put("message", "Invalid Cino: " + cIno);
			
			return response;
		}

	}

	@PostMapping("/forwardCaseDetails")
	public ResponseEntity<Map<String, Object>> getforwardCaseDetails(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {
		// Map<String, Object> map = new HashMap<>();
		String cIno = abstractReqBody.getFileCino();
 
		return service.getforwardCaseDetails(authentication, abstractReqBody, request, cIno);
	}

	@PostMapping("/forwardCaseDetails2GP")
	public ResponseEntity<Map<String, Object>> getforwardCaseDetails2GP(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		String cIno = abstractReqBody.getFileCino();
		 
		return service.getforwardCaseDetails2GP(authentication, abstractReqBody, request, cIno);
	}

	@PostMapping("/sendBackCaseDetails")
	public ResponseEntity<Map<String, Object>> getSendBackCaseDetails(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		String cIno = abstractReqBody.getFileCino();

		return service.getSendBackCaseDetails(authentication, abstractReqBody, request, cIno);
	}

	@PostMapping("/gpApprove")
	public ResponseEntity<Map<String, Object>> getGPApprove(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		String cIno = abstractReqBody.getFileCino();
		
		return service.getGPApprove(authentication, abstractReqBody, request, cIno);
	}

	@PostMapping("/gpReject")
	public ResponseEntity<Map<String, Object>> getGPReject(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		String cIno = abstractReqBody.getFileCino();
 
		return service.getGPReject(authentication, abstractReqBody, request, cIno);
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
	
	@PostMapping("assignMultiCases2SectionLegacy")
	public Map<String, Object> assignMultiCases2SectionLegacy(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request ) {

		System.out.println("assignMultiCases2SectionLegacy: ");
		Map<String, Object> map = new HashMap<>();

		String msg = service.getassignMultiCases2SectionLegacy(authentication, abstractReqBody, request);
		System.out.println("msg---"+msg);
		if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error") ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", msg);
		}else {
			map.put("sdesc", msg);
			map.put("status", false);
			map.put("scode", "02");
		}
		return map;
	}
	
	@PostMapping("AssignToDeptHODSendBackLegacy")
	public Map<String, Object> AssignToDeptHODSendBackLegacy(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request ) {

		System.out.println("assignMultiCases2SectionLegacy: ");
		Map<String, Object> map = new HashMap<>();

		String msg = service.getAssignToDeptHODSendBackLegacy(authentication, abstractReqBody, request);
		System.out.println("msg---"+msg);
		if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error") ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", msg);
		}else {
			map.put("sdesc", msg);
			map.put("status", false);
			map.put("scode", "02");
		}
		return map;
	}
	@GetMapping("/getEMPList")
	public Map<String, Object> getEMPList(Authentication authentication) {
		
	    List<Map<String, Object>> rawList = service.getEMPList(authentication);
	   
	    Map<String, Object> map = new HashMap<>();
	    
	    if (rawList != null && !rawList.isEmpty()    ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", "Data Found");
			map.put("data", rawList);
			 
		}else {
			map.put("sdesc", "No Data Found");
			map.put("status", false);
			map.put("scode", "02");
		}
	     
	    return map;
	}
	
	@GetMapping("/getNoList")
	public Map<String, Object> getNoList(Authentication authentication) {
		
	    List<Map<String, Object>> rawList = service.getNoList(authentication);
	    Map<String, Object> map = new HashMap<>();
	    
	    if (rawList != null && !rawList.isEmpty() ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("data", rawList);
			map.put("sdesc", "Data found");
		}else {
			map.put("sdesc", "Data not found");
			map.put("status", false);
			map.put("scode", "02");
			map.put("data", rawList);
		}

	    return map;
	}
	
}
