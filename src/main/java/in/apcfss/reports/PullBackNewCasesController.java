package in.apcfss.reports;

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
import in.apcfss.services.PullBackNewCasesService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "EcourtsDeptInstructionController - Access, Refresh", description = "Ecourts DeptInstruction Controller")
public class PullBackNewCasesController {

	@Autowired
	PullBackNewCasesService service;
	
	@GetMapping("/PullBackNewCasesList")
	public Map<String, Object> CasesListEcourtsDept(Authentication authentication ) {

		System.out.println("PullBackNewCasesList: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			//String roleId = userPrincipal.getRoleId();
			
			List<Map<String, Object>> rawList = service.getPullBackNewCasesList(userPrincipal);
			 
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
	
	@PostMapping("/sendCaseBackNewCases")
	public Map<String, Object> sendCaseBackNewCases(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		System.out.println("sendCaseBackNewCases: ");
		Map<String, Object> map = new HashMap<>();
		try {

			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			
			String rawList = service.getSendCaseBackNewCases(userPrincipal, abstractReqBody,request );
			System.out.println("rawList---"+rawList);
			 if (rawList != null && !rawList.isEmpty()  && !rawList.contains("error") ) {

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
	 
	 
	
}
