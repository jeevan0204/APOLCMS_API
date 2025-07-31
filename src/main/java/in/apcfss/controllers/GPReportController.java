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
import in.apcfss.repositories.GPReportRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.GPReportService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "Legacy Cases Assignment Controller - Access, Refresh", description = "Legacy Cases Assignment")
public class GPReportController {

	@Autowired
	UsersRepo repo;

	@Autowired
	GPReportService service;

	@Autowired
	GPReportRepo gpReportRepo;

	@GetMapping("GPReport")
	public Map<String, Object> getHighCourtCasesListdata(Authentication authentication,@RequestParam ("pwCounterFlag") String pwCounterFlag) {

		System.out.println("GPReport: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId=userPrincipal.getRoleId();
			if (roleId != null && roleId.equals("6")) {

				List<Map<String, Object>> data = service.CaseWiseDataGPCases(roleId, authentication, pwCounterFlag);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					response.put("CASEWISEDATA", data);
					response.put("show_flag_leg", "Y");
				}else {
					response.put("show_flag_leg", "N");
				}


				List<Map<String, Object>> data1 = service.CaseWiseDataNewGPCases(roleId, authentication, pwCounterFlag);
				if (data1 != null && !data1.isEmpty() && data1.size() > 0) {
					response.put("CASEWISEDATANEW", data1);
					response.put("total", data1.size());
					response.put("show_flag_New", "Y");

				}else {
					response.put("show_flag_New", "N");
				}

				if (data != null && !data.isEmpty()  || data1 != null && !data1.isEmpty()  ) {
					response.put("status", true);
					response.put("scode", "01");
				}else {
					response.put("status", false);
					response.put("scode", "02");
				}

				response.put("yearId_2022", 1);
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	@GetMapping("/caseStatusUpdateGPReport")
	public Map<String, Object> getcaseStatusUpdateGPReport(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam ("caseNo") String caseNo ,@RequestParam ("caseType") String caseType) {
		System.out.println("caseStatusUpdateGPReport: ");
		Map<String, Object> response = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		response = service.getcaseStatusUpdateGPReport(authentication,abstractReqBody,caseNo,caseType);
		return response;
	}

	@GetMapping("viewInstructionsCases")
	public Map<String, Object> viewInstructionsCases(Authentication authentication ) {

		System.out.println("viewInstructionsCases: ");
		Map<String, Object> response = new HashMap<>();
		//Map<String, Object> map = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId=userPrincipal.getRoleId();
			if (roleId != null && roleId.equals("6")) {

				List<Map<String, Object>> data = service.casewisedataViewInstruction( authentication );
				if (data != null && !data.isEmpty() && data.size() > 0) {
					response.put("CASEWISEDATA", data);
					response.put("show_flag_leg", "Y");
				}else {
					response.put("show_flag_leg", "N");
				}


				List<Map<String, Object>> data1 = service.casewisedataNewViewInstruction( authentication );
				if (data1 != null && !data1.isEmpty() && data1.size() > 0) {
					response.put("CASEWISEDATANEW", data1);
					response.put("total", data1.size());
					response.put("show_flag_New", "Y");

				}else {
					response.put("show_flag_New", "N");
				}

				if (data != null && !data.isEmpty()  || data1 != null && !data1.isEmpty()  ) {
					response.put("status", true);
					response.put("scode", "01");
				}else {
					response.put("status", false);
					response.put("scode", "02");
				}

				response.put("yearId_2022", 1);
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

	@GetMapping("viewInstructionsCasesNew")
	public Map<String, Object> viewInstructionsCasesNew(Authentication authentication) {
		System.out.println("viewInstructionsCasesNew:");
		Map<String, Object> response = new HashMap<>();

		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			String userId = userPrincipal.getUserId();

			List<Map<String, Object>> data = gpReportRepo.viewInstructionsCasesNew(userId);

			if (data != null && !data.isEmpty()) {
				response.put("CASEWISEDATA", data);
				response.put("show_flag_leg", "Y");
				response.put("status", true);
				response.put("scode", "01");
			} else {
				response.put("show_flag_leg", "N");
				response.put("status", false);
				response.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", e.getMessage());
		}

		return response;
	}
	@GetMapping("viewCaseDetails")
	public Map<String, Object> viewCaseDetails(Authentication authentication,@RequestParam ("caseNo") String caseNo,@RequestParam ("caseType") String caseType,@RequestParam ("caseYear") int caseYear) {
		System.out.println("viewCaseDetails:");
		Map<String, Object> response = new HashMap<>();

		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			String userId = userPrincipal.getUserId();

			List<Map<String, Object>> data = service.viewCaseDetails(authentication,caseNo,caseType,caseYear);

			if (data != null && !data.isEmpty()) {
				response.put("CASEWISEDATA", data);
				response.put("show_flag_leg", "Y");
				response.put("status", true);
				response.put("scode", "01");
				response.put("msg", "Case Details for Case No.:" + caseType + "/" + caseNo + "/" + caseYear);
			} else {
				response.put("show_flag_leg", "N");
				response.put("status", false);
				response.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", e.getMessage());
		}

		return response;
	}
	@GetMapping("/caseStatusUpdatecauselist")
	public Map<String, Object> caseStatusUpdatecauselist(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam ("caseNo") String caseNo ,@RequestParam ("caseType") String caseType) {
		System.out.println("caseStatusUpdatecauselist:");
		Map<String, Object> response = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		response = service.getcaseStatusUpdateGPReport(authentication,abstractReqBody,caseNo,caseType);
		return response;
	}


	@GetMapping("/DailyStatusEntryReport")
	public Map<String, Object> DailyStatusEntryReport(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam ("cino") String cino ,@RequestParam ("serno") String serno,@RequestParam ("caseType") String caseType) {
		System.out.println("DailyStatusEntryReport:");
		Map<String, Object> response = new HashMap<>();

		response = service.DailyStatusEntryReport(authentication,abstractReqBody,cino,serno,caseType);
		return response;
	}

	@PostMapping("/getSubmitCategoryLegacyDSE") 
	public ResponseEntity<Map<String, Object>> getSubmitCategoryLegacyDSE(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) { 
		System.out.println("getSubmitCategoryLegacyDSE:");
		String cIno = abstractReqBody.getCino();
		String serno = abstractReqBody.getSerno();

		return service.getSubmitCategoryLegacyDSE(authentication, abstractReqBody, cIno,serno );
	}

	@PostMapping("/getSubmitCategoryNewDSE") 
	public ResponseEntity<Map<String, Object>> getSubmitCategoryNewDSE(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) { 
		// Map<String, Object> map = new HashMap<>(); 
		System.out.println("getSubmitCategoryNewDSE:");
		String cIno = abstractReqBody.getCino();
		String serno = abstractReqBody.getSerno();

		return service.getSubmitCategoryNewDSE(authentication, abstractReqBody, cIno,serno );
	}
	@PostMapping("/ApproveGP") 
	public ResponseEntity<Map<String, Object>> ApprovedByGp(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest
			request) {
		// Map<String, Object> map = new HashMap<>(); 
		System.out.println("ApproveGP:");
		String cIno = abstractReqBody.getFileCino();
		System.err.println("controller----------------"+cIno);

		return service.ApprovedByGp(authentication, abstractReqBody, request, cIno);
	}
	@PostMapping("/RejectGP") 
	public ResponseEntity<Map<String, Object>> RejectGP(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest
			request) {
		System.out.println("RejectGP:");
		// Map<String, Object> map = new HashMap<>(); 
		String cIno = abstractReqBody.getFileCino();

		return service.RejectedByGp(authentication, abstractReqBody, request, cIno);
	}
	
	@PostMapping("/ApproveGPNew") 
	public Map<String, Object> ApproveGPNew(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest
			request) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("ApproveGP:");
		String cIno = abstractReqBody.getFileCino();
		System.err.println("controller----------------"+cIno);

		try {
			String msg = service.getApproveGPNew(authentication, abstractReqBody, request, cIno);

			if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", msg);
			}else {
				map.put("sdesc", msg);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}  
		return map;
		
	}
	@PostMapping("/RejectGPNew") 
	public Map<String, Object> RejectGPNew(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest
			request) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("ApproveGP:");
		String cIno = abstractReqBody.getFileCino();
		System.err.println("controller----------------"+cIno);

		try {
			String msg = service.getRejectGPNew(authentication, abstractReqBody, request, cIno);

			if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", msg);
			}else {
				map.put("sdesc", msg);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}  
		return map;
		
	}

}   
