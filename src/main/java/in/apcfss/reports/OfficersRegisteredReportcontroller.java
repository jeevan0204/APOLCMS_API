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
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DistrictWiseInterimOrderImplReportController - Access, Refresh", description = "District Wise InterimOrderImpl Report Controller")
public class OfficersRegisteredReportcontroller {

	@Autowired
	OfficersRegisteredReportService service;
	
	@Autowired
	CommonMethodsController commonMethodController;

	@GetMapping("OfficersRegistered")
	public Map<String, Object> OfficersRegistered(Authentication authentication,@RequestParam("districtId") String districtId,@RequestParam("officerType") String officerType ) {

		System.out.println("OfficersRegistered: ");
		Map<String, Object> response = new HashMap<>();
		String Heading="";
		try {
			List<Map<String, Object>> data = service.getOfficersRegistered(authentication,districtId,officerType);
			
			if ((CommonModels.checkStringObject(officerType).equals("DNO") && districtId.equals("ALL") )) {

				Heading="Nodal Officer (Legal - District Level) Details ";

			} 

			else if (CommonModels.checkStringObject(officerType).equals("DNO") ) {

				Heading="Nodal Officer (Legal - District Level) Details ";
			} 

			else if (CommonModels.checkStringObject(officerType).equals("NO")) {

				Heading="Nodal Officer (Legal) Details ";
			} 

			else if (CommonModels.checkStringObject(officerType).equals("MLOSUBJECT")) {

				Heading="Middle Level Officers (MLO Subject) Details ";
			} 
			else {

				Heading="Middle Level Officers (Legal) Details ";
				response.put("officerType", "MLO");
			}

			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("EMPWISEDATA", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", Heading);

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

}
