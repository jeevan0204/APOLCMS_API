package in.apcfss.controllers;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.SectionOfficerChangeMstPayLoad;
import in.apcfss.services.SectionOfficerChangeService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SectionOfficerChange - etc...", description = "Sectionofficer Change Controller")
@RestController
public class SectionOfficerChangeRequestController {

	@Autowired
	CommonQueryAPIUtils commonQueryAPIUtils;

	@Autowired
	UsersRepo usersRepo;

	@Autowired
	SectionOfficerChangeService sectionOfficerChangeService;

	@PostMapping("/SectionChange")
	ResponseEntity<?> SectionOfficerChange(Authentication authentication,@RequestBody SectionOfficerChangeMstPayLoad sectionOfficerChangeMstPayLoad)
	{

		return sectionOfficerChangeService.saveDetails(authentication,sectionOfficerChangeMstPayLoad);
	}

	@GetMapping("/GetExistingEmpName")
	public List<Map<String, Object>> getExistingEmpName(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String deptCode = userPrincipal.getDeptCode(); 
		String userType = userPrincipal.getRoleId(); 
		System.out.println("deptCode--"+deptCode+"userType--"+userType);
		return usersRepo.getExistingEmpName(deptCode,userType);
	}
	@GetMapping("getNewDepartmentList")
	public List<Map<String, Object>> getNewDepartmentList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String deptCode = userPrincipal.getDeptCode(); 
		return usersRepo.getNewDepartmentList(deptCode);
	}

	@GetMapping("getNewDesignationList")
	public List<Map<String, Object>> getNewDesignationList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String deptCode = userPrincipal.getDeptCode(); 
		return usersRepo.getNewDesignationList(deptCode);
	}
	@GetMapping("getEmpList")
	public List<Map<String, Object>> getEmpList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		String deptCode = userPrincipal.getDeptCode(); 
		return usersRepo.getEmpList(deptCode);
	}
}
