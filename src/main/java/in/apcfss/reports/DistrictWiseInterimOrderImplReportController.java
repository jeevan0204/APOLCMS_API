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
import in.apcfss.services.DistrictWiseFinalOrdersImplReportService;
import in.apcfss.services.DistrictWiseInterimOrderImplReportService;
import in.apcfss.services.ScanDocsCountReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DistrictWiseInterimOrderImplReportController - Access, Refresh", description = "District Wise InterimOrderImpl Report Controller")
public class DistrictWiseInterimOrderImplReportController {

	@Autowired
	DistrictWiseInterimOrderImplReportService service;

	@GetMapping("InterimOrdersImplReport")
	public Map<String, Object> InterimOrdersImplReport(Authentication authentication,@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate ) {

		System.out.println("InterimOrdersImplReport: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getInterimOrdersImplReport(authentication,fromDate,toDate);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("interimORDERSREPORT", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
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
	
	@GetMapping("CasesListInterim")
	public Map<String, Object> CasesListInterim(Authentication authentication,@RequestParam("caseStatus") String caseStatus,@RequestParam("distid") String distid, @RequestParam("distName") String distName ) {

		System.out.println("CasesListInterim: ");
		Map<String, Object> response = new HashMap<>();
		try {
			String heading="Cases List for "+distName;
			
			if(!caseStatus.equals("")) {
				if(caseStatus.equals("CLOSED")){
						heading+=" Closed Cases List";
					}
				
				if(caseStatus.equals("INTERIMORDER")) {
					heading+=" Interim orders implemented";
				}
				if(caseStatus.equals("APPEALFILEDINTERIMORDER")) {
					heading+="  Appeal Interim orders";
				}
				if(caseStatus.equals("DISMISSEDINTERIMORDER")) {
					heading+="  Dismissed Interim Orders";
				}
				if(caseStatus.equals("PENDINGINTERIMORDER")) {
					heading+="  District Collector Interim Orders";
				}
				
			}
			
			List<Map<String, Object>> data = service.getCasesListInterim(authentication,caseStatus,distid,distName);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).get("is_scandocs_exists") == null) {
						data.get(i).put("is_scandocs_exists", "false");
					}
					if (data.get(i).get("is_final_exists") == null) {
						data.get(i).put("is_final_exists", "false");
					}

					if ((data.get(i).get("judgement_order")) != null) {
						//System.out.println("-------f");

						response.put("file", "judgement");
					}
					
					if ((data.get(i).get("appeal_filed")) != null && (data.get(i).get("appeal_filed")).equals("Yes")) {
					//	System.out.println("-------a---------");
						response.put("file", "appeal");
						data.get(i).put("appeal_filed", "yes");

					}
					if ((data.get(i).get("dismissed_copy")) != null) {
						//System.out.println("-------d");
						response.put("file", "dismissed");
					}
				}
				
				response.put("status", true);
				response.put("scode", "01");
				response.put("CASESLIST", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", heading);
				
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

	@GetMapping("CCCasesReportInterim")
	public Map<String, Object> CCCasesReportInterim(Authentication authentication,@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate ) {

		System.out.println("CCCasesReportInterim: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getCCCasesReportInterim(authentication,fromDate,toDate);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("CCCASESREPORT", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "District Wise Contempt Cases Report");
				
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
	@GetMapping("NewCasesReportInterim")
	public Map<String, Object> NewCasesReport(Authentication authentication ) {

		System.out.println("NewCasesReportInterim: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getNewCasesReportInterim(authentication );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("FRESHCASESREPORT", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "District Wise Fresh Cases Report");
				
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
	
	@GetMapping("LegacyCasesReportInterim")
	public Map<String, Object> LegacyCasesReportInterim(Authentication authentication,@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate ) {

		System.out.println("LegacyCasesReportInterim: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getLegacyCasesReportInterim(authentication,fromDate,toDate );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("LEGACYCASESREPORT", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "District Wise Legacy Cases Report");
				
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
