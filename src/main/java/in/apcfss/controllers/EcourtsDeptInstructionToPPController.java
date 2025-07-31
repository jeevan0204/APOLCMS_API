package in.apcfss.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.EcourtsDeptInstructionService;
import in.apcfss.services.EcourtsDeptInstructionToPPService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "EcourtsDeptInstructionToPPController - Access, Refresh", description = "Ecourts DeptInstruction PP Controller")
public class EcourtsDeptInstructionToPPController {

	@Autowired
	EcourtsDeptInstructionToPPService service;

	@GetMapping("/EcourtsDeptInstructiontoPP")
	public Map<String, Object> EcourtsDeptInstructionNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("EcourtsDeptInstructiontoPP: ");

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println("User ID: " + userPrincipal.getUserId());
		String roleId = userPrincipal.getRoleId();
		try {
			List<Map<String, Object>> rawList = service.getAckList(authentication,abstractReqBody);

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("data", rawList);
				if (roleId.equals("6")) {

					map.put("HEADING", "Daily Status Entry PP");
				} else {

					map.put("HEADING", "Instructions Entry PP");
				}
			}else {
				map.put("sdesc", "No Data found");
				map.put("status", false);
				map.put("scode", "02");
			}



		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}



	@PostMapping("/getCasesListEcourtsDeptPP")
	public Map<String, Object> CasesListEcourtsDept(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("getCasesListEcourtsDeptPP: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId = userPrincipal.getRoleId();

			String caseType = "";

			if (roleId.equals("6")) { // || !caseType.equals(null)
				caseType = abstractReqBody.getCaseType()+ "";
			} else {
				caseType = abstractReqBody.getOldNewType()+ "";   

			}

			Map<String, Object> rawList = service.getCasesListEcourtsDeptNewPP(userPrincipal, abstractReqBody,caseType);
			System.out.println("rawList---"+rawList);
			if (rawList != null && !rawList.isEmpty()  ) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);
				map.put("oldNewType", caseType);
				if (roleId.equals("6")) {  
					map.put("service_type", "service_type");
					map.put("HEADING", "Daily Status Entry To PP");
					map.put("status_entry", "Daily Status ");

				} else {

					map.put("HEADING", "Instructions Entry To PP");
					map.put("status_entry", "Instructions ");
				}

			}else {
				map.put("sdesc", "No Data Found");
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}


	@PostMapping("/getSubmitCategoryecourtsDeptInstructionPP")
	public Map<String, Object> SubmitCategoryecourtsDeptInstruction(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		System.out.println("getSubmitCategoryecourtsDeptInstructionPP: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId = userPrincipal.getRoleId();
			String rawList = service.getSubmitCategoryEcourtsDeptNewPP(userPrincipal, abstractReqBody,request );
			System.out.println("rawList---"+rawList);
			if (rawList != null && !rawList.isEmpty()  && !rawList.contains("error") ) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);

				if (roleId.equals("6")) {

					map.put("HEADING", "Daily Status Entry PP");
					map.put("status_entry", "Daily Status ");

				} else {

					map.put("HEADING", "Submit Instructions to GP");
					map.put("status_entry", "Instructions ");

				}
			}else {
				map.put("sdesc", "No Data Found");
				map.put("status", false);
				map.put("scode", "02");
			}


		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}




}
