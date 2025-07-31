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
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "EcourtsDeptInstructionController - Access, Refresh", description = "Ecourts DeptInstruction Controller")
public class EcourtsDeptInstructionController {

	@Autowired
	EcourtsDeptInstructionService service;

	@GetMapping("/EcourtsDeptInstructionNew")
	public Map<String, Object> EcourtsDeptInstructionNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("EcourtsDeptInstructionNew: ");
		Map<String, Object> response = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			//String roleId = userPrincipal.getRoleId();

			response = CommonQueryAPIUtils.apiService("data",
					service.getAckList(userPrincipal, abstractReqBody));

		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;
	}


	@GetMapping("/YearByCaseTypeYear")
	public Map<String, Object> YearByCaseTypeYear(Authentication authentication,@Param("caseType")  String caseType) {



		List<Map<String, Object>> rawList = service.getYearByCaseTypeYear(authentication,caseType);

		Map<String, Object> map = new HashMap<>();

		if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", "Data found");
			map.put("data", rawList);
		}else {
			map.put("sdesc", "No Data found");
			map.put("status", false);
			map.put("scode", "02");
		}

		return map;
	}


	@GetMapping("/NumberbyCaseType")
	public Map<String, Object> NumberbyCaseType(Authentication authentication,@Param("caseType")  String caseType,@Param("regYear") String regYear) {

		List<Map<String, Object>> rawList = service.getNumberbyCaseType(authentication,caseType,Integer.parseInt(regYear));

		Map<String, Object> map = new HashMap<>();

		if (rawList != null && !rawList.isEmpty() ) {  //&& !rawList.contains("error")

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", "Data found");
			map.put("data", rawList);

		}else {
			map.put("sdesc", "No Data found");
			map.put("status", false);
			map.put("scode", "02");
		}

		return map;
	}


	@PostMapping("/CasesListEcourtsDept")
	public Map<String, Object> CasesListEcourtsDept(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("EcourtsDeptInstructionNew: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId = userPrincipal.getRoleId();

			String caseType = "";

			if (roleId.equals("6")) { 
				caseType = abstractReqBody.getCaseType()+ "";

			} else {
				caseType = abstractReqBody.getOldNewType()+ "";   

			}

			Map<String, Object> rawList = service.getCasesListEcourtsDept(userPrincipal, abstractReqBody,caseType);
			System.out.println("rawList---"+rawList);
			if (rawList != null && !rawList.isEmpty()  ) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);
				map.put("oldNewType", caseType);
				if (roleId.equals("6")) {  

					map.put("service_type", "service_type");
					map.put("HEADING", "Daily Status Entry");
					map.put("status_entry", "Daily Status ");

				} else {

					map.put("HEADING", "Instructions Entry");
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


	@PostMapping("/SubmitCategoryecourtsDeptInstruction")
	public Map<String, Object> SubmitCategoryecourtsDeptInstruction(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		System.out.println("EcourtsDeptInstructionNew: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String roleId = userPrincipal.getRoleId();
			String rawList = service.getSubmitCategoryecourtsDeptInstruction(userPrincipal, abstractReqBody,request );
			System.out.println("rawList---"+rawList);
			if (rawList != null && !rawList.isEmpty()  && !rawList.contains("error") ) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);

				if (roleId.equals("6")) {

					map.put("HEADING", "Daily Status Entry");
					map.put("status_entry", "Daily Status ");

				} else {

					map.put("HEADING", "Instructions Entry");
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
