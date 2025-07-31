package in.apcfss.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.LegacyCasesAssignmentService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "Legacy Cases Assignment Controller - Access, Refresh", description = "Legacy Cases Assignment")
public class LegacyCasesAssignmentController {

	@Autowired
	UsersRepo repo;

	@Autowired
	LegacyCasesAssignmentService service;
	
	
	@GetMapping("/getHighCourtCasesListdata")
	public Map<String, Object> getHighCourtCasesListdata(Authentication authentication,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("Contempt: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			 
			HCCaseStatusAbstractReqBody sanitized = new HCCaseStatusAbstractReqBody();

			if (abstractReqBody.getRegYear() != null) sanitized.setRegYear(abstractReqBody.getRegYear());
			if (abstractReqBody.getDofFromDate() != null) sanitized.setDofFromDate(abstractReqBody.getDofFromDate());
			if (abstractReqBody.getDofToDate() != null) sanitized.setDofToDate(abstractReqBody.getDofToDate());
			if (abstractReqBody.getPetitionerName() != null) sanitized.setPetitionerName(abstractReqBody.getPetitionerName());
			if (abstractReqBody.getRespodentName() != null) sanitized.setRespodentName(abstractReqBody.getRespodentName());
			if (abstractReqBody.getCategoryServiceId() != null) sanitized.setCategoryServiceId(abstractReqBody.getCategoryServiceId());
			if (abstractReqBody.getCaseTypeId() != null) sanitized.setCaseTypeId(abstractReqBody.getCaseTypeId());
			if (abstractReqBody.getDistId() != null) sanitized.setDistId(abstractReqBody.getDistId());
			if (abstractReqBody.getDeptId() != null) sanitized.setDeptId(abstractReqBody.getDeptId());


			response = CommonQueryAPIUtils.apiService("data",
					service.getHighCourtCasesListdata(userPrincipal,sanitized));

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

	@GetMapping("/empDeptListToAssignCases")
	public Map<String, Object> empDeptListToAssignCases(Authentication authentication,@RequestParam String chkdVal) {
		Map<String, Object> response = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode();
		response = CommonQueryAPIUtils.apiService("data",
				service.empDeptListToAssignCases(userPrincipal,deptCode,chkdVal));
		return response;
	}

	@PostMapping("/assign2DeptHOD")
	public ResponseEntity<Map<String, Object>> getassign2DeptHOD(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("assign2DeptHOD: ");
		Map<String, Object> response = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return service.getassign2DeptHOD(userPrincipal,abstractReqBody);
	}

	@PostMapping("/assignMultiCases2Section")
	public ResponseEntity<Map<String, Object>> getassignMultiCases2Section(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("assignMultiCases2Section: ");
		Map<String, Object> response = new HashMap<>();
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		return service.getassignMultiCases2Section(userPrincipal,abstractReqBody);
	}
	
	
	@PostMapping("/assign2DistCollector")
	public ResponseEntity<Map<String, Object>> getassign2DistCollector(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {

		 
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());

			  
		return service.getassign2DistCollector(userPrincipal,abstractReqBody);
	}
}
