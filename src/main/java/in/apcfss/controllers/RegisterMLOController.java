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
import in.apcfss.services.RegisterNodalOfficerService; 
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "RegisterMLOController - Access, Refresh", description = "Register MLO Controller")
public class RegisterMLOController {

	@Autowired
	RegisterMLOService service;
	
	@GetMapping("/DesignationListMLO")
	public Map<String, Object> DesignationListMLO(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> map = new HashMap<>();
		System.out.println("registerMLO: ");
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
	
	@GetMapping("/registerMLO")
	public Map<String, Object> registerMLO(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> map = new HashMap<>();
		System.out.println("registerMLO: ");
		try {
			String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
			
			List<Map<String, Object>> rawList = service.getregisterMLOList(deptCode);

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

	@PostMapping("/saveEmployeeDetailsMLO")
	public Map<String, Object> saveEmployeeDetailsMLO(Authentication authentication, @RequestBody HCCaseStatusAbstractReqBody abstractReqBod,HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		System.out.println("saveEmployeeDetailsMLO: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId=userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : ""; 
		String rawList="";
		try {
			if (!(roleId.toString().trim().equals("3") ||roleId.toString().trim().equals("2"))) {
				map.put("sdesc", "Unauthorized to access this service");
			} else if (roleId.toString().trim().equals("3") ||roleId.toString().trim().equals("2")) {
				  rawList = service.saveMloDetails(authentication,abstractReqBod,request);
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
	@PostMapping("/updateEmployeeDetailsMlo")
	public Map<String, Object> updateEmployeeDetailsMlo(Authentication authentication, @RequestBody HCCaseStatusAbstractReqBody abstractReqBod,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		System.out.println("updateEmployeeDetailsMlo: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId=userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String rawList="";
		try {
			if (!(roleId.toString().trim().equals("3") ||roleId.toString().trim().equals("2"))) {
				map.put("sdesc", "Unauthorized to access this service");
			} else if (roleId.toString().trim().equals("3") ||roleId.toString().trim().equals("2")) {
				  rawList = service.updateEmployeeDetailsMlo(authentication,abstractReqBod,request,deptCode);
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
