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
import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AcknowledgementsReportService;
import in.apcfss.services.DistrictWiseFinalOrdersImplReportService;
import in.apcfss.services.DistrictWiseInterimOrderImplReportService;
import in.apcfss.services.OfficersRegisteredReportService;
import in.apcfss.services.ScanDocsCountReportService;
import in.apcfss.services.SectionOfficerWiseCaseProcessingReportService;
import in.apcfss.services.SectionOfficerWiseInstructionsReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "SectionOfficerWiseInstructionsReportController - Access, Refresh", description = "Section Officer Wise Instructions Report Controller")
public class SectionOfficerWiseInstructionsReportController {

	@Autowired
	SectionOfficerWiseInstructionsReportService service;

	@GetMapping("SectionOfficerWiseInstructionsReport")
	public Map<String, Object> SectionOfficerWiseInstructionsReport(Authentication authentication,@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate,
			@RequestParam("section_code") String section_code    ) {

		System.out.println("SectionOfficerWiseInstructionsReport: ");
		Map<String, Object> response = new HashMap<>();
		 
		try {
			List<Map<String, Object>> data = service.getSectionOfficerWiseInstructionsReport(authentication,fromDate,toDate,section_code);

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

	@GetMapping("AllCasesDetailsInstReport")
	public Map<String, Object> AllCasesDetailsInstReport(Authentication authentication,
			@RequestParam("section_code") String section_code,@RequestParam("emailId") String emailId,@RequestParam("ids") String ids    ) {

		System.out.println("AllCasesDetailsInstReport: ");
		Map<String, Object> response = new HashMap<>();
		 
		try {
			List<Map<String, Object>> data = service.getAllCasesDetailsInstReport(authentication, section_code,emailId,ids );

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
				response.put("HEADING", "Section Officer Wise Instructions Report");

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
