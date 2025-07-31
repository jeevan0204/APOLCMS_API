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
import in.apcfss.services.RegisterNodalOfficerService; 
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "RegisterNodalOfficerController - Access, Refresh", description = "Register NodalOfficer Controller")
public class RegisterNodalOfficerController {

	@Autowired
	RegisterNodalOfficerService service;
	
	@GetMapping("/registerNodal")
	public Map<String, Object> registerNodal(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> map = new HashMap<>();
		System.out.println("registerNodal: ");
		try {
			String roleId=userPrincipal.getRoleId();
			if(roleId.trim().equals("2")) {
				List<Map<String, Object>> data1 = service.getDistNodalDepartmentList(authentication,  abstractReqBody);
				map.put("deptsList", data1);
			}else {
				List<Map<String, Object>> data = service.getNodalDepartmentList(authentication,  abstractReqBody);
				map.put("deptsList", data);
			}
			List<Map<String, Object>> rawList = service.getNodalList(authentication,  abstractReqBody);

			if (rawList != null && !rawList.isEmpty() ) { // && !rawList.contains("error") 
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc","Data found");
				map.put("List_data", rawList);
				//map.put("HEADING", "Assigned Cases List");
			}else {
				map.put("sdesc", "You have Zero cases to Process.");
				map.put("status", false);
				map.put("scode", "02");
			}
			map.put("saveAction", "INSERT");
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}

	@GetMapping("/HodEmployeeDetails")
	public Map<String, Object> HodEmployeeDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> map = new HashMap<>();
		System.out.println("registerNodal: ");
		try {
			String roleId=userPrincipal.getRoleId();
			if(roleId.trim().equals("2")) {
				List<Map<String, Object>> data1 = service.getDistNodalDepartmentList(authentication,  abstractReqBody);
				map.put("deptsList", data1);
			}else {
				List<Map<String, Object>> data = service.getNodalDepartmentList(authentication,  abstractReqBody);
				map.put("deptsList", data);
			}
			String deptId=abstractReqBody.getDeptId() != null ? abstractReqBody.getDeptId().toString() : "";

			List<Map<String, Object>> NodalDesignationList = service.getRegistrerNodalDesignationList(authentication,deptId);
			map.put("NodalDesignationList", NodalDesignationList);
			//if(deptId.equals(null)){
				List<Map<String, Object>> rawList = service.getHodEmployeeDetails(authentication,  deptId);

					if (rawList != null && !rawList.isEmpty() && rawList.size() > 0){
					map.put("status", true);
					map.put("scode", "01");
					map.put("sdesc","Data found");
					map.put("List_data", rawList);
					//map.put("HEADING", "Assigned Cases List");
				}else {
					map.put("sdesc", "You have No Data to Process.");
					map.put("status", false);
					map.put("scode", "02");
					map.put("saveAction", "INSERT");
				}
					/*}
			else{
				map.put("sdesc", "Invalid Hod.");
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}


	@PostMapping("/saveEmployeeDetailsNodalOfficer")
	public Map<String, Object> saveEmployeeDetailsNodalOfficer(Authentication authentication, @RequestBody HCCaseStatusAbstractReqBody abstractReqBod,HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		System.out.println("saveEmployeeDetailsNodalOfficer: ");
		try {
			String rawList = service.getSaveEmployeeDetailsNodalOfficer(authentication,abstractReqBod,request);
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
