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
import in.apcfss.services.DistrictWiseFinalOrdersImplementationRegService;
import in.apcfss.services.DistrictWiseInterimOrdersImplService;
import in.apcfss.services.EcourtsCaseSearchService;
import in.apcfss.services.EcourtsDeptInstructionService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "EcourtsCaseSearchController - Access, Refresh", description = "Ecourts Case Search Controller")
public class EcourtsCaseSearchController {

	@Autowired
	EcourtsCaseSearchService service;

	@GetMapping("/CaseTypesListShrtNEW")
	public Map<String, Object> EcourtsCaseSearch(Authentication authentication ) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("EcourtsCaseSearch: ");
		try {
			List<Map<String, Object>> rawList = service.getCaseTypesListShrtNEW( authentication );

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("caseTypesListShrt", rawList); 

			}else {
				map.put("sdesc", "You have Zero cases Types to Process.");
				map.put("status", false);
				map.put("scode", "02");
			}
			map.put("oldNewType", "New");
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}
	@GetMapping("/yearByCaseType")
	public Map<String, Object> yearByCaseType(Authentication authentication ,String caseType) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("yearByCaseType: ");
		try {
			List<Map<String, Object>> rawList = service.getYearbyCasetypes( authentication ,caseType);

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("YearbyCasetypes", rawList); 

			}else {
				map.put("sdesc", "You have Zero cases Types to Process.");
				map.put("status", false);
				map.put("scode", "02");
			}
			map.put("oldNewType", "New");
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}
	@GetMapping("/NumberByCaseType")
	public Map<String, Object> NumberByCaseType(Authentication authentication ,String caseType,String year) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("EcourtsCaseSearch: ");
		try {
			List<Map<String, Object>> rawList = service.getNumberbyYear( authentication,caseType,Integer.parseInt(year) );

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("caseTypesListShrt", rawList); 

			}else {
				map.put("sdesc", "You have Zero cases Types to Process.");
				map.put("status", false);
				map.put("scode", "02");
			}
			map.put("oldNewType", "New");
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}




	@GetMapping("/SearchCasesList")
	public Map<String, Object> SearchCasesList(Authentication authentication ,HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("EcourtsCaseSearch: ");
		String SelectCaseType = abstractReqBody.getOldNewType().toString();
		try {
			List<Map<String, Object>> rawList = service.getSearchCasesList( authentication,abstractReqBody ,SelectCaseType);
			if (rawList != null && !rawList.isEmpty() ) { 
				if(SelectCaseType.equals("Legacy")) {

					for (int i = 0; i < rawList.size(); i++) {
						if (rawList.get(i).get("is_scandocs_exists") == null) {
							rawList.get(i).put("is_scandocs_exists", "false");
						}
					}

					map.put("CASESLISTOLD", rawList); 

				}else {
					map.put("CASESLISTNEW", rawList); 

				}

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
			}else {
				map.put("sdesc", "You have Zero cases to Process.");
				map.put("status", false);
				map.put("scode", "02");
			}


		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}



}
