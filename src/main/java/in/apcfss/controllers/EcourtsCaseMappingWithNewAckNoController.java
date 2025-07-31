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
import in.apcfss.services.EcourtsCaseMappingWithNewAckNoService;
import in.apcfss.services.EcourtsDeptInstructionService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "EcourtsDeptInstructionController - Access, Refresh", description = "Ecourts DeptInstruction Controller")
public class EcourtsCaseMappingWithNewAckNoController {

	@Autowired
	EcourtsCaseMappingWithNewAckNoService service;
	
	@GetMapping("/EcourtsCaseMappingWithNewAckNo")
	public Map<String, Object> EcourtsCaseMappingWithNewAckNo(Authentication authentication,@Param("caseTypeId") 
	String caseTypeId,@Param("deptId") String deptId,@Param("districtId") String districtId,@Param("dofFromDate") String dofFromDate,@Param("dofToDate") String dofToDate,
	@Param("advocateName") String advocateName,@Param("categoryServiceId") String categoryServiceId) {
		
	    List<Map<String, Object>> rawList = service.getEcourtsCaseMappingWithNewAckNo(authentication,caseTypeId,deptId,districtId,dofFromDate,dofToDate,advocateName,categoryServiceId);
	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
	    Map<String, Object> map = new HashMap<>();
	    
	    if (rawList != null && !rawList.isEmpty()   ) {
				for (int i = 0; i < rawList.size(); i++) {

					if (rawList.get(i).get("file_found") == null) {
						rawList.get(i).put("file_found", "No");
					}
				}

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", "Data Found");
			map.put("CASEWISEACKS", rawList);
			 
		}else {
			map.put("sdesc", "No Data Found");
			map.put("status", false);
			map.put("scode", "02");
		}
	    
	    if (roleId.equals("2") || roleId.equals("10")) {
	    	map.put("login_type_dc", "login_type_dc");
		} else {
			map.put("login_type", "login_type");
		}
	     
	    return map;
	}
	
	@GetMapping("/EcourtsCaseMappingWithNewAckNos")
	public Map<String, Object> EcourtsCaseMappingWithNewAckNos(Authentication authentication,@Param("case_type") 
	String case_type,@Param("case_year") Integer case_year,@Param("case_number") Integer case_number) {
		
	    List<Map<String, Object>> rawList = service.getEcourtsCaseMappingWithNewAckNos(authentication,case_type,case_year,case_number);

	    Map<String, Object> map = new HashMap<>();
	    
	    if (rawList != null && !rawList.isEmpty()  ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", "Data Found");
			map.put("CASEWISECINOS", rawList);
			 
		}else {
			map.put("sdesc", "No Data Found");
			map.put("status", false);
			map.put("scode", "02");
		}
	    
	    return map;
	}
	
	@GetMapping("/submitDetailsForNewAckNo")
	public Map<String, Object> submitDetailsForNewAckNo(Authentication authentication,@Param("caseType1") String caseType1,@Param("regYear1") String regYear1,@Param("mainCaseNo") String mainCaseNo,@Param("ackNo") String ackNo) {
		
	    String rawList = service.getsubmitDetailsForNewAckNo(authentication,caseType1,Integer.parseInt(regYear1),Integer.parseInt(mainCaseNo),ackNo);

	    Map<String, Object> map = new HashMap<>();
	    
	    if (rawList != null && !rawList.isEmpty()  && !rawList.contains("error") ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", "Data Found");
			map.put("CASEWISEACKS", rawList);
			 
		}else {
			map.put("sdesc", "No Data Found");
			map.put("status", false);
			map.put("scode", "02");
		}
	    
	    return map;
	}
	 
}
