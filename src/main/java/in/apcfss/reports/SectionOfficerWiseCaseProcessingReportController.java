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

import in.apcfss.services.SectionOfficerWiseCaseProcessingReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "SectionOfficerWiseCaseProcessingReportController - Access, Refresh", description = "Section Officer Wise Case Processing Report Controller")
public class SectionOfficerWiseCaseProcessingReportController {

	@Autowired
	SectionOfficerWiseCaseProcessingReportService service;

	@GetMapping("SectionOfficerWiseCaseProcessingReport")
	public Map<String, Object> SectionOfficerWiseCaseProcessingReport(Authentication authentication,@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate,
			@RequestParam("caseTypeId") String caseTypeId ,@RequestParam("section_code") String section_code    ) {

		System.out.println("SectionOfficerWiseCaseProcessingReport: ");
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<Map<String, Object>> data = service.getCasesListSectionOfficer(authentication,fromDate,toDate,caseTypeId,section_code);

			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("CASESLIST", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", Heading);

			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if(section_code.equals("null") || section_code.equals("")) {
				response.put("section", "L");
			}else {
				response.put("section", section_code);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

	@GetMapping("AllCasesDetails")
	public Map<String, Object> AllCasesDetails(Authentication authentication,@RequestParam("section_code") String section_code,@RequestParam("emailId") String emailId,
			@RequestParam("ids") String ids,@RequestParam("date") String date ) {

		System.out.println("AllCasesDetails: ");
		Map<String, Object> response = new HashMap<>();
		 
		try {
			List<Map<String, Object>> data = service.getAllCasesDetails(authentication,section_code,emailId,ids,date);

			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).get("is_scandocs_exists") == null) {
						data.get(i).put("is_scandocs_exists", "false");
					}
				}
				response.put("status", true);
				response.put("scode", "01");
				response.put("CASESLISTDETAILS", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "Section Officer Wise Case Processing Report");

			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if(section_code.equals("null") || section_code.equals("")) {
				response.put("section", "L");
			}else {
				response.put("section", section_code);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

}
