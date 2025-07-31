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
import in.apcfss.services.EcourtsDeptInstructionService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DistrictWiseInterimOrdersImplController - Access, Refresh", description = "District Wise InterimOrdersImpl Controller")
public class DistrictWiseInterimOrdersImplController {

	@Autowired
	DistrictWiseInterimOrdersImplService service;

	@GetMapping("/InterimOrderImplementation")
	public Map<String, Object> InterimOrderImplementation(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("InterimOrderImplementation: ");
		try {
			List<Map<String, Object>> rawList = service.getInterimCASESLIST( authentication,  abstractReqBody);

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("CASESLIST", rawList);
				map.put("HEADING", "Assigned Cases List");
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

	@GetMapping("/InterimOrdersImplementedCaseStatusUpdate")
	public Map<String, Object> InterimOrdersImplementedCaseStatusUpdate(Authentication authentication,
			 HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("InterimOrdersImplementedCaseStatusUpdate: ");
		Map<String, Object> map = new HashMap<>();
		try {
			String cIno = abstractReqBody.getFileCino();
			Map<String, Object> rawList = service.getCaseStatusUpdate(authentication, abstractReqBody,cIno);
			if (rawList != null && !rawList.isEmpty()  ) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);

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

	@PostMapping("/InterimOrdersImplementedUpdateCaseDetails")
	public Map<String, Object> InterimOrdersImplementedUpdateCaseDetails(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		System.out.println("InterimOrdersImplementedUpdateCaseDetails: ");
		Map<String, Object> map = new HashMap<>();
		String cIno = abstractReqBody.getFileCino();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			 
			String rawList = service.getupdateCaseDetails(userPrincipal, abstractReqBody,request,cIno );
			System.out.println("rawList---"+rawList);
			if (rawList != null && !rawList.isEmpty()  && !rawList.toLowerCase().contains("error") ) {

				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data Found");
				map.put("data", rawList);

			}else {
				map.put("data", rawList);
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
