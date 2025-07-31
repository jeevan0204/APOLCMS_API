package in.apcfss.controllers;

import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.GPOAckDeptsEntity;
import in.apcfss.entities.GPOAckGen;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.PPOAcknowledgementRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.GPOAcknowledgementService;
import in.apcfss.services.PPOAcknowledgementService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "PPO Acknowledgement Services - Access, Refresh", description = "PPO Acknowledgement Controller") //For Edit and Update SERVICES USED (Acknowledgement Services Only)
@SecurityRequirement(name = "bearerAuth")
public class PPOAcknowledgementController {

	@Autowired
	PPOAcknowledgementService ppoAckService;

	@Autowired
	PPOAcknowledgementRepo PPORepo;

	@PostMapping("savePPOAckDetails")
	public ResponseEntity<Map<String, Object>> savePPOAckDetails(Authentication authentication,@RequestBody GPOAckDetailsReqBody dto,HttpServletRequest request) {
		System.out.println("Received Data:");
		System.out.println("District ID: " + dto.getDistId());
		System.out.println("Petitioner Name: " + dto.getPetitionerName());
		System.out.println("Advocate CC No: " + dto.getAdvocateCCno());
		System.out.println("Case Category: " + dto.getCaseCategory());
		System.out.println("Case Type: " + dto.getCaseType());
		System.out.println("Filing Mode: " + dto.getFilingMode());
		System.out.println("Case Type 1: " + dto.getCaseType1());
		System.out.println("Registration Year: " + dto.getRegYear1());
		System.out.println("Remarks: " + dto.getRemarks());

		System.out.println("\nGpOackForm List:");
		for (GPOAckGen form : dto.getGpOackForm()){
			System.out.println("--------------------------------");

			System.out.println("Department ID: " + form.getDepartmentId());
			System.out.println("Display Dept: " + form.getDispalyDept());
			System.out.println("Display Dist: " + form.getDispalyDist());
			System.out.println("Section Selection: " + form.getSectionSelection());
			System.out.println("Emp Dept: " + form.getEmpDept());
			System.out.println("Emp Section: " + form.getEmpSection());
			System.out.println("Emp Post: " + form.getEmpPost());
			System.out.println("Employee ID: " + form.getEmployeeId());
			System.out.println("Other Dist: " + form.getOtherDist());
			System.out.println("Mandal: " + form.getMandal());
			System.out.println("Village: " + form.getVillage());
			System.out.println("Service Type: " + form.getServiceType());
		}

		return ppoAckService.savePPOAckDetails(authentication,dto,request);
	}

	@GetMapping("/getCaseTypeListPP")
	public List<Map<String, Object>> getCaseTypeListPP(Authentication authentication) {
		
	    List<Map<String, Object>> rawList = PPORepo.getCaseTypeListPP();
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}
	
	@GetMapping("/getNaturePetitionListPP")
	public List<Map<String, Object>> getNaturePetitionListPP(Authentication authentication) {
		
	    List<Map<String, Object>> rawList = PPORepo.getNaturePetitionListPP();
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}
	
	@GetMapping("/getDepartmentListPP")
	public List<Map<String, Object>> getDepartmentListPP(Authentication authentication) {
		
	    List<Map<String, Object>> rawList = PPORepo.getDepartmentListPP();
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}
	
	@GetMapping("/getStationListPP")
	public List<Map<String, Object>> getStationListPP(@RequestParam("distId")  String distId) {
		
		int distIdInt=Integer.parseInt(distId);
	    List<Map<String, Object>> rawList = PPORepo.getStationListPP(distIdInt);
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}
	@GetMapping("/getSebStationListPP")
	public List<Map<String, Object>> getSebStationListPP(@RequestParam("distId")  String distId) {
		
		int distIdInt=Integer.parseInt(distId);
	    List<Map<String, Object>> rawList = PPORepo.getSebStationListPP(distIdInt);
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}

}
