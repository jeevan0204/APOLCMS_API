package in.apcfss.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl; 
import in.apcfss.services.HCNewCaseStatusAbstractReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "HCNewCaseStatusAbstractReportController - Access, Refresh", description = "HC NewCase Status Abstract Report Controller")
public class HCNewCaseStatusAbstractReportController {

	@Autowired
	HCNewCaseStatusAbstractReportService service;

	@GetMapping("/HCNewCaseStatusAbstractReport")
	public Map<String, Object> AcksAbstractReport(Authentication authentication ,
			@Param("dofFromDate") String dofFromDate,@Param("dofToDate") String dofToDate,@Param("caseTypeId") String caseTypeId,@Param("districtId") String districtId,
	@Param("regYear") String regYear,@Param("deptId") String deptId,@Param("petitionerName") String petitionerName,@Param("respodentName") String respodentName,
	@Param("serviceType1") String serviceType1,@Param("deptName") String deptName  ) {

		System.out.println("AcksAbstractReport: ");
		Map<String, Object> response = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {
			
			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {

				return HODwisedetails(authentication,dofFromDate,dofToDate,caseTypeId,districtId,regYear,deptId,petitionerName,respodentName,serviceType1,deptName);
			}else {
			List<Map<String, Object>> data = service.getHCNewCaseStatusSECWISE(authentication,dofFromDate,dofToDate,caseTypeId,districtId,regYear,deptId,petitionerName,respodentName,serviceType1 );
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ){

				response.put("secdeptwisenewcases", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
				response.put("HEADING", "Sect. Dept. Wise High Court New Cases Abstract Report");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  
			}
			response.put("SHOWFILTERS", "SHOWFILTERS");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("errorMsg", "Exception occurred : No Records found to display");

		} 
		return response;
	}
	
	@GetMapping("/HODwisedetails")
	public Map<String, Object> HODwisedetails(Authentication authentication ,
			@Param("dofFromDate") String dofFromDate,@Param("dofToDate") String dofToDate,@Param("caseTypeId") String caseTypeId,@Param("districtId") String districtId,
	@Param("regYear") String regYear,@Param("deptId") String deptId,@Param("petitionerName") String petitionerName,@Param("respodentName") String respodentName,
	@Param("serviceType1") String serviceType1,@Param("deptName") String deptName  ) {

		Map<String, Object> response = new HashMap<>();
		
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal(); 
		try {
			List<Map<String, Object>> data = service.getHODwisedetails(authentication,dofFromDate,dofToDate,caseTypeId,districtId,regYear,deptId,petitionerName,respodentName,serviceType1,deptName);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
				response.put("deptwisenewcases", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  
			
			response.put("SHOWFILTERS", "SHOWFILTERS");
		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/HccNewCasesList")
	public Map<String, Object> HccNewCasesList(Authentication authentication ,@Param("dofFromDate") String dofFromDate,@Param("dofToDate") String dofToDate,@Param("caseTypeId") String caseTypeId,@Param("districtId") String districtId,
			@Param("regYear") String regYear,@Param("deptId") String deptId,@Param("petitionerName") String petitionerName,@Param("respodentName") String respodentName,
			@Param("serviceType1") String serviceType1,
			@Param("deptName") String deptName,@Param("caseStatus") String caseStatus,
			@Param("reportLevel") String reportLevel ,@Param("caseCategory") String caseCategory,@Param("deptType") String deptType   ) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getHccNewCasesList(authentication,dofFromDate,dofToDate,caseTypeId,districtId,regYear,deptId,petitionerName,respodentName,serviceType1,deptName,caseStatus,reportLevel,caseCategory,deptType);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
				response.put("deptwisenewcases", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  
			
			response.put("SHOWFILTERS", "SHOWFILTERS");
		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	 
}
