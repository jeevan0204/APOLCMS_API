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
 
import in.apcfss.services.AcknowledgementsReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "AcknowledgementsReportController - Access, Refresh", description = "Acknowledgements Report Controller")
public class AcknowledgementsReportController {

	@Autowired
	AcknowledgementsReportService service;

	@GetMapping("AcksReport")
	public Map<String, Object> AcksReport(Authentication authentication ) {

		System.out.println("AcksReport: ");
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getACKDATA(authentication);
			List<Map<String, Object>> data1=service.getACKDATAExistingCase(authentication);
			if ((data != null && !data.isEmpty() && data.size() > 0 ) && (data1 != null && !data1.isEmpty() && data1.size() > 0) ){

				response.put("ACKDATA", data);
				response.put("ACKDATAExisting", data1);
				response.put("status", true);
				response.put("scode", "01");
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
	
	@GetMapping("userWiseAcksReport")
	public Map<String, Object> getUSERWISEACKDATA(Authentication authentication,@RequestParam("ackDate")  String ackDate ) {

		System.out.println("AcksReport: "+ackDate);
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getUSERWISEACKDATA(authentication,ackDate);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
				response.put("USERWISEACKDATA", data);
				response.put("status", true);
				response.put("scode", "01");
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
	 
}
