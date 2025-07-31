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
import in.apcfss.services.AcknowledgementAbstractReportService; 
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "AcknowledgementAbstractReportController - Access, Refresh", description = "Acknowledgements AbstractReport Controller")
public class AcknowledgementAbstractReportController {

	@Autowired
	AcknowledgementAbstractReportService service;

	@GetMapping("/AcksAbstractReport")
	public Map<String, Object> AcksAbstractReport(Authentication authentication ,
			@Param("districtId") String districtId,@Param("deptId") String deptId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
	@Param("advcteName") String advcteName,@Param("petitionerName") String petitionerName,@Param("serviceType1") String serviceType1,@Param("caseTypeId") String caseTypeId ) {

		System.out.println("AcksAbstractReport: ");
		Map<String, Object> response = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {
			List<Map<String, Object>> data = service.getAcksAbstractReportHODWise(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ){

				response.put("DEPTWISEACKS", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  

			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}
		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/showDistWise")
	public Map<String, Object> getDISTWISEACKS(Authentication authentication ,
			@Param("districtId") String districtId,@Param("deptId") String deptId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
	@Param("advcteName") String advcteName,@Param("petitionerName") String petitionerName,@Param("serviceType1") String serviceType1,@Param("caseTypeId") String caseTypeId ) {

		Map<String, Object> response = new HashMap<>();
		
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {
			List<Map<String, Object>> data = service.getDISTWISEACKS(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
				response.put("DISTWISEACKS", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	@GetMapping("/showUserWise")
	public Map<String, Object> showUserWise(Authentication authentication ,
			@Param("districtId") String districtId,@Param("deptId") String deptId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
	@Param("advcteName") String advcteName,@Param("petitionerName") String petitionerName,@Param("serviceType1") String serviceType1,@Param("caseTypeId") String caseTypeId ) {

		Map<String, Object> response = new HashMap<>();
		
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {
			List<Map<String, Object>> data = service.getshowUserWise(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
				response.put("USERWISEACKS", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/showCaseWiseAcksAbstract")
	public Map<String, Object> showCaseWiseAcksAbstract(Authentication authentication ,
			@Param("districtId") String districtId,@Param("deptId") String deptId,@Param("fromDate") String fromDate,@Param("toDate") String toDate,
	@Param("advcteName") String advcteName,@Param("petitionerName") String petitionerName,@Param("serviceType1") String serviceType1,@Param("caseTypeId") String caseTypeId ,@Param("inserted_by") String inserted_by) {

		Map<String, Object> response = new HashMap<>();
		
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		try {
			List<Map<String, Object>> data = service.getshowCaseWiseAcksAbstract(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId,inserted_by);
			 
			if ((data != null && !data.isEmpty() && data.size() > 0 ) ){
				for (int i = 0; i < data.size(); i++) {

					if (data.get(i).get("file_found") == null) {
						data.get(i).put("file_found", "No");
					}
				}
				response.put("CASEWISEACKS", data);
				response.put("status", true);
				response.put("scode", "01");
				response.put("sdesc", "Records Found.");
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records Found.");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	 
}
