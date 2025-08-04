package in.apcfss.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import in.apcfss.services.DistrictWiseAssigmentCasesAbstractService; 
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DistrictWiseAssigmentCasesAbstractController - Access, Refresh", description = "District Wise Assigment Cases Abstract Controller")
public class DistrictWiseAssigmentCasesAbstractController {

	@Autowired
	DistrictWiseAssigmentCasesAbstractService service;

	@GetMapping("/DistrictWiseAssigmentCasesList")
	public Map<String, Object> getDistrictWiseAssignmentCasesList(
	        Authentication authentication,
	        @RequestParam("section_code") String sectionCode) {

	    System.out.println("Fetching District Wise Assignment Cases Abstract");
	    Map<String, Object> response = new HashMap<>();

	    try {
	        List<Map<String, Object>> data = service.getDistrictWiseAssigmentCasesList(authentication, sectionCode);

	        if (data != null && !data.isEmpty()) {
	            response.put("status", true);
	            response.put("scode", "01");
	            response.put("interimORDERSREPORT", data);
	            response.put("sdesc", "Data Found");
	            response.put("HEADING", "District Wise Assignment Cases Abstract");
	        } else {
	            response.put("status", false);
	            response.put("scode", "02");
	            response.put("sdesc", "No Records Found to Display");
	        }

	        // Normalize section code
	        if (sectionCode == null || sectionCode.trim().isEmpty() || sectionCode.equalsIgnoreCase("null")) {
	            response.put("section", "L");
	        } else {
	            response.put("section", sectionCode);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", false);
	        response.put("scode", "99");
	        response.put("sdesc", "An error occurred while processing the request");
	    }

	    return response;
	}
	
	
	@GetMapping("DistrictWiseAssigmentCasesDetails")
	public Map<String, Object> getDistrictWiseAssignmentCasesDetails(
	        Authentication authentication,
	        @RequestParam("section_code") String sectionCode,
	        @RequestParam("actionType") String actionType,
	        @RequestParam("email") String email,
	        @RequestParam("deptName") String deptName,
	        @RequestParam("uploadValue") String uploadValue,
	        @RequestParam("dist_id") String distId) {

	    System.out.println("Fetching District Wise Assignment Cases Details");

	    Map<String, Object> response = new HashMap<>();

	    try {
	        List<Map<String, Object>> data = service.getDistrictWiseAssigmentCasesDetails(
	                authentication, sectionCode, actionType, email, deptName, uploadValue, distId);

	        if (data != null && !data.isEmpty()) {
	            for (Map<String, Object> item : data) {
	                item.putIfAbsent("is_scandocs_exists", "false");
	            }
	            response.put("status", true);
	            response.put("scode", "01");
	            response.put("AssigmentCasesDetails", data);
	            response.put("sdesc", "Data Found");
	            response.put("HEADING", "District Wise Assignment Cases Details");
	        } else {
	            response.put("status", false);
	            response.put("scode", "02");
	            response.put("sdesc", "No Records Found to Display");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", false);
	        response.put("scode", "99");
	        response.put("sdesc", "An error occurred while processing the request");
	    }

	    return response;
	}
	
}
