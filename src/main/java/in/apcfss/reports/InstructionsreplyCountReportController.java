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
import in.apcfss.services.DistrictWiseFinalOrdersImplReportService;
import in.apcfss.services.DistrictWiseInterimOrderImplReportService;
import in.apcfss.services.InstructionsreplyCountReportService;
import in.apcfss.services.ScanDocsCountReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "InstructionsreplyCountReportController - Access, Refresh", description = "Instructions reply Count Report Controller")
public class InstructionsreplyCountReportController {

	@Autowired
	InstructionsreplyCountReportService service;

	@GetMapping("InstructionsreplyCountReport")
	public Map<String, Object> InstructionsreplyCountReport(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("InstructionsreplyCountReport: ");
		Map<String, Object> response = new HashMap<>();
		String show_flag="N";
		try {
			List<Map<String, Object>> data = service.getReplyCountData(authentication,abstractReqBody );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("ReplyCountData", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "Instructions Reply Count Report");
				response.put("sec_show", "Secretariat ");
				show_flag="Y";
				
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
	
	@GetMapping("InstructionsreplyCountSecDeptdata")
	public Map<String, Object> InstructionsreplyCountSecDeptdata(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("InstructionsreplyCountReport: ");
		Map<String, Object> response = new HashMap<>();
		String show_flag="N";
		try {
			List<Map<String, Object>> data = service.getReplyCountSecDeptdata(authentication,abstractReqBody );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {

				response.put("status", true);
				response.put("scode", "01");
				response.put("ReplyCountData", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "Instructions Reply Count Report");
				response.put("sec_show", "");
				show_flag="Y";
				
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
	@GetMapping("InstructionsReplyCountReportViewData")
	public Map<String, Object> InstructionsReplyCountReportViewData(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("InstructionsreplyCountReport: ");
		Map<String, Object> response = new HashMap<>();
		String show_flag="N";
		String heading="";
		String counter_pw_flag = CommonModels.checkStringObject(abstractReqBody.getPwCounterFlag());
		try {
			
			if(counter_pw_flag.equals("DeptCount")) 
			{
				heading = "Department Count List for Instructions reply Count View Report";
			}else if(counter_pw_flag.equals("InstCount")) 
			{
				heading = "Instruction Count List for Instructions reply Count View Report";
			}
			else if(counter_pw_flag.equals("ReplyInstCount")) 
			{
				heading = "Reply Instruction Count List for Instructions reply Count View Report";
			}
			List<Map<String, Object>> data = service.getReplyCountReportViewData(authentication,abstractReqBody );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				response.put("status", true);
				response.put("scode", "01");
				response.put("ReplyCountData", data);
				response.put("sdesc", "Data Found");
				response.put("HEADING", "Instructions Reply Count Report");
				response.put("sec_show", "");
				show_flag="Y";
				
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
