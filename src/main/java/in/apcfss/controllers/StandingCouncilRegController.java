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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.DistrictWiseFinalOrdersImplementationRegService;
import in.apcfss.services.EcourtsDeptInstructionService;
import in.apcfss.services.StandingCouncilRegService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DistrictWiseFinalOrdersImplementationRegController - Access, Refresh", description = "District Wise FinalOrders ImplementationReg Controller")
public class StandingCouncilRegController {

	@Autowired
	StandingCouncilRegService service;

	@GetMapping("/StandingCounsel")
	public Map<String, Object> StandingCounsel(Authentication authentication ) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("StandingCounsel");
		try {
			List<Map<String, Object>> rawList = service.getStandingCounsel( authentication );

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("Standard_Council_Data", rawList);
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

 

	@PostMapping("/saveStandingCounselEmployee")
	public Map<String, Object> saveStandingCounselEmployee(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		System.out.println("saveStandingCounselEmployee: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			 
			String rawList = service.saveStandingCounselEmployee(userPrincipal, abstractReqBody,request );
			System.out.println("rawList---"+rawList);
			if (rawList != null && !rawList.isEmpty()  && !rawList.toLowerCase().contains("error")) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);

			}else {
				map.put("sdesc", "No Data Found");
				map.put("data", rawList);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
	        map.put("status", false);
	        map.put("scode", "99");
	        map.put("sdesc", "Error Exception Error");
	    }
		return map;
	}




}
