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

import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AcknowledgementsReportService;
import in.apcfss.services.ScanDocsCountReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "ScanDocsCountReportController - Access, Refresh", description = "ScanDocs Count Report Controller")
public class ScanDocsCountReportController {

	@Autowired
	ScanDocsCountReportService service;

	@GetMapping("ScanDocsCountReport")
	public Map<String, Object> ScanDocsCountReport(Authentication authentication ) {

		System.out.println("ScanDocsCountReport: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.ScanDocsCountReport(authentication);
			
			List<Map<String, Object>> data1 = service.getScanCountForNewCases(authentication);
			if ((data != null && !data.isEmpty() && data.size() > 0 ) && (data1 != null && !data1.isEmpty() && data1.size() > 0 )   ){

				response.put("status", true);
				response.put("scode", "01");
				response.put("SCANDOCS", data);
				response.put("NEWSCANDOCS", data1);
				response.put("sdesc", "Data Found");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

	@GetMapping("NotAvailableOldScanDocsCount")
	public Map<String, Object> getNotAvailableOldScanDocsCount(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody, @RequestParam("year")  String year,@RequestParam("month")  String month ) {

		System.out.println("NotAvailableOldScanDocsCount: "+year);
		Map<String, Object> response = new HashMap<>();
		try {
			//String viewDisplay = abstractReqBody.getSHOWPOPUP() + "";
			//String viewDisplay = SHOWPOPUP;

			//if (!viewDisplay.equals("") && viewDisplay.equals("SHOWPOPUP")) {

			//	year = abstractReqBody.getParameter("year") + "";
			//	month = abstractReqBody.getParameter("month") + "";
			//}
			if ( month != null && !month.equals("") ) {
				List<Map<String, Object>> data = service.getNotAvailableOldScanDocsCount(authentication,year,month);

				if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
					response.put("NotAvailableScanList", data);
					response.put("status", true);
					response.put("scode", "01");
					response.put("sdesc", "Data Found");
				}else {
					response.put("status", false);
					response.put("scode", "02");
					response.put("sdesc", "No Records Found.");
				}  
			}else {
				response.put("errorMsg", "Invalid Month.");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

}
