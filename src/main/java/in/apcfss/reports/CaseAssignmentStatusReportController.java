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

import in.apcfss.common.CommonModels;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AcknowledgementsReportService;
import in.apcfss.services.CaseAssignmentStatusReportService;
import in.apcfss.services.DistrictWiseFinalOrdersImplReportService;
import in.apcfss.services.DistrictWiseInterimOrderImplReportService;
import in.apcfss.services.InstructionsreplyCountReportService;
import in.apcfss.services.ScanDocsCountReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "InstructionsreplyCountReportController - Access, Refresh", description = "Instructions reply Count Report Controller")
public class CaseAssignmentStatusReportController {

	@Autowired
	CaseAssignmentStatusReportService service;

	@GetMapping("CaseAssignmentStatus")
	public Map<String, Object> CaseAssignmentStatus(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("CaseAssignmentStatus: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getCaseAssignmentStatus(authentication,abstractReqBody );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("DEPTWISEHCCASES", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "Department wise Highcourt Cases Assignment Status Abstract Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("CaseAssignmentStatusList")
	public Map<String, Object> caseAssignmentStatusList(
	        Authentication authentication,
	        @RequestParam String actionType,
	        @RequestParam String deptId,
	        @RequestParam String deptName) {

	    Map<String, Object> response = new HashMap<>();
	    String heading = "";

	    try {
	        // Validate input
	        if (actionType != null && !actionType.isEmpty() && deptId != null && !deptId.isEmpty()) {

	            // Determine heading based on actionType
	            switch (actionType) {
	                case "assigned2HOD":
	                    heading = deptName + " Cases assigned to HOD";
	                    break;
	                case "assigned2SectSec":
	                    heading = deptName + " Cases assigned to Section (Sect. Dept.)";
	                    break;
	                case "assigned2HodSec":
	                    heading = deptName + " Cases assigned to Section (HOD)";
	                    break;
	                default:
	                    heading = deptName + " Cases";
	                    break;
	            }

	            // Fetch data
	            List<Map<String, Object>> data = service.getCaseAssignmentStatusList(authentication, actionType, deptId, deptName);

	            if (data != null && !data.isEmpty()) {
	                response.put("status", true);
	                response.put("scode", "01");
	                response.put("DEPTWISEHCCASES", data);
	                response.put("HEADING", heading);  // optional: include heading in response
	                response.put("sdesc", "Data Found");
	            } else {
	                response.put("status", false);
	                response.put("scode", "02");
	                response.put("sdesc", "No Records found to display");
	            }

	        } else {
	            response.put("status", false);
	            response.put("scode", "03");
	            response.put("sdesc", "Invalid input parameters");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", false);
	        response.put("scode", "99");
	        response.put("sdesc", "Internal Server Error");
	    }

	    return response;
	}
	 
	
}
