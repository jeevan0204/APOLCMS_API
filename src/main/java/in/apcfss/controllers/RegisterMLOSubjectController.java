package in.apcfss.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.RegisterMLOService;
import in.apcfss.services.RegisterMLOSubjectService;
import in.apcfss.services.RegisterNodalOfficerService; 
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "RegisterMLOSubjectController - Access, Refresh", description = "Register MLO Subject Controller")
public class RegisterMLOSubjectController {

	@Autowired
	RegisterMLOSubjectService service;
	
	@GetMapping("/DesignationListMLOSubject")
	public Map<String, Object> DesignationListMLOSubject(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> map = new HashMap<>();
		System.out.println("DesignationListMLOSubject: ");
		try {
			String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
			
				List<Map<String, Object>> DesignationList = service.getDesignationList(deptCode);
			
			if (DesignationList != null && !DesignationList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Designation List found");
				map.put("DesignationList", DesignationList);
				
			}else {
				map.put("sdesc", "You have Zero Designation List.");
				map.put("status", false);
				map.put("scode", "02");
				map.put("DesignationList", DesignationList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}
	
	@GetMapping("/registerMLOSubject")
	public Map<String, Object> registerMLOSubject(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> map = new HashMap<>();
		System.out.println("registerMLO: ");
		try {
			String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
			
			List<Map<String, Object>> rawList = service.getRegisterMLOSubjectList(deptCode);

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("List_data", rawList);
			}else {
				map.put("sdesc", "You have Zero Employees List.");
				map.put("status", false);
				map.put("scode", "02");
				map.put("List_data", rawList);
			}
			map.put("saveAction", "INSERT");
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}

	@PostMapping("/saveEmployeeDetailsMLOSubject")
	public Map<String, Object> saveEmployeeDetailsMLOSubject(Authentication authentication, @RequestBody HCCaseStatusAbstractReqBody abstractReqBod,HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		System.out.println("saveEmployeeDetailsMLO: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId=userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : ""; 
		String rawList="";
		try {
			if (!(roleId.toString().trim().equals("3") ||roleId.toString().trim().equals("2"))) {
				map.put("sdesc", "Unauthorized to access this service");
			} else if (roleId.toString().trim().equals("3") ||roleId.toString().trim().equals("2")) {
				  rawList = service.saveMloSubjectDetails(authentication,abstractReqBod,request);
			}
			if (rawList != null && !rawList.isEmpty() && !rawList.toLowerCase().contains("error") ) {
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
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}
	 

}
