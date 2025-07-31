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

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AcknowledgementsReportService;
import in.apcfss.services.DistrictWiseFinalOrdersImplReportService;
import in.apcfss.services.DistrictWiseInterimOrderImplReportService;
import in.apcfss.services.EcourtsLeacyCaseMappedWithNewAckNoReportService;
import in.apcfss.services.ScanDocsCountReportService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "EcourtsLeacyCaseMappedWithNewAckNoReportController - Access, Refresh", description = "Ecourts Leacy Case Mapped With NewAckNo Report Controller")
public class EcourtsLeacyCaseMappedWithNewAckNoReportController {

	@Autowired
	EcourtsLeacyCaseMappedWithNewAckNoReportService service;

	@GetMapping("/DeptWiseMAP")
	public Map<String, Object> DeptWiseMAP(Authentication authentication,@RequestParam(value = "districtId", required = false, defaultValue = "") String districtId,
		    @RequestParam(value = "deptId", required = false, defaultValue = "") String deptId,
		    @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
		    @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
		    @RequestParam(value = "advcteName", required = false, defaultValue = "") String advcteName,
		    @RequestParam(value = "petitionerName", required = false, defaultValue = "") String petitionerName,
		    @RequestParam(value = "serviceType1", required = false, defaultValue = "") String serviceType1,
		    @RequestParam(value = "caseTypeId", required = false, defaultValue = "") String caseTypeId) {

		System.out.println("DeptWiseMAP: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getDeptWiseMAP(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				for (int i = 0; i < data.size(); i++) {

					if (data.get(i).get("file_found") == null) {
						data.get(i).put("file_found", "No");
					}
					//System.out.println("---------------" + data.get(i).get("file_found"));
				}
				response.put("status", true);
				response.put("scode", "01");
				response.put("DEPTWISEACKS", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17") || roleId.equals("19"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/DeptWiseMAPslno")
	public Map<String, Object> DeptWiseMAPslno(Authentication authentication,@RequestParam(value = "districtId", required = false, defaultValue = "") String districtId,
		    @RequestParam(value = "deptId", required = false, defaultValue = "") String deptId,
		    @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
		    @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
		    @RequestParam(value = "advcteName", required = false, defaultValue = "") String advcteName,
		    @RequestParam(value = "petitionerName", required = false, defaultValue = "") String petitionerName,
		    @RequestParam(value = "serviceType1", required = false, defaultValue = "") String serviceType1,
		    @RequestParam(value = "caseTypeId", required = false, defaultValue = "") String caseTypeId) {

		System.out.println("DeptWiseMAP: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getDeptWiseMAPslno(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				 
				response.put("status", true);
				response.put("scode", "01");
				response.put("DEPTWISEACKSslno", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17") || roleId.equals("19"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/DistWiseMAP")
	public Map<String, Object> DistWiseMAP(Authentication authentication,@RequestParam(value = "districtId", required = false, defaultValue = "") String districtId,
		    @RequestParam(value = "deptId", required = false, defaultValue = "") String deptId,
		    @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
		    @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
		    @RequestParam(value = "advcteName", required = false, defaultValue = "") String advcteName,
		    @RequestParam(value = "petitionerName", required = false, defaultValue = "") String petitionerName,
		    @RequestParam(value = "serviceType1", required = false, defaultValue = "") String serviceType1,
		    @RequestParam(value = "caseTypeId", required = false, defaultValue = "") String caseTypeId) {

		System.out.println("DistWiseMAP: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getDistWiseMAP(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				 
				response.put("status", true);
				response.put("scode", "01");
				response.put("DISTWISEACKS", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17") || roleId.equals("19"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	@GetMapping("/UserWiseMAP")
	public Map<String, Object> UserWiseMAP(Authentication authentication,@RequestParam(value = "districtId", required = false, defaultValue = "") String districtId,
		    @RequestParam(value = "deptId", required = false, defaultValue = "") String deptId,
		    @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
		    @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
		    @RequestParam(value = "advcteName", required = false, defaultValue = "") String advcteName,
		    @RequestParam(value = "petitionerName", required = false, defaultValue = "") String petitionerName,
		    @RequestParam(value = "serviceType1", required = false, defaultValue = "") String serviceType1,
		    @RequestParam(value = "caseTypeId", required = false, defaultValue = "") String caseTypeId ) {

		System.out.println("UserWiseMAP: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getUserWiseMAP(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				 
				response.put("status", true);
				response.put("scode", "01");
				response.put("USERWISEACKS", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17") || roleId.equals("19"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/CaseWiseAcksAbstractMAPslno")
	public Map<String, Object> CaseWiseAcksAbstractMAPslno(Authentication authentication,@RequestParam(value = "districtId", required = false, defaultValue = "") String districtId,
		    @RequestParam(value = "deptId", required = false, defaultValue = "") String deptId,
		    @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
		    @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
		    @RequestParam(value = "advcteName", required = false, defaultValue = "") String advcteName,
		    @RequestParam(value = "petitionerName", required = false, defaultValue = "") String petitionerName,
		    @RequestParam(value = "serviceType1", required = false, defaultValue = "") String serviceType1,
		    @RequestParam(value = "caseTypeId", required = false, defaultValue = "") String caseTypeId,
		    @RequestParam(value = "inserted_by", required = false, defaultValue = "") String inserted_by) {

		System.out.println("CaseWiseAcksAbstractMAPslno: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getCaseWiseAcksAbstractMAPslno(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId,inserted_by);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				for (int i = 0; i < data.size(); i++) {

					if (data.get(i).get("file_found") == null) {
						data.get(i).put("file_found", "No");
					}
					//System.out.println("---------------" + data.get(i).get("file_found"));
				}
				response.put("status", true);
				response.put("scode", "01");
				response.put("CASEWISEACKS", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17") || roleId.equals("19"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("/ShowCaseWiseAcksAbstractMAP")
	public Map<String, Object> ShowCaseWiseAcksAbstractMAP(Authentication authentication,
			@RequestParam(value = "districtId", required = false, defaultValue = "") String districtId,
		    @RequestParam(value = "deptId", required = false, defaultValue = "") String deptId,
		    @RequestParam(value = "fromDate", required = false, defaultValue = "") String fromDate,
		    @RequestParam(value = "toDate", required = false, defaultValue = "") String toDate,
		    @RequestParam(value = "advcteName", required = false, defaultValue = "") String advcteName,
		    @RequestParam(value = "petitionerName", required = false, defaultValue = "") String petitionerName,
		    @RequestParam(value = "serviceType1", required = false, defaultValue = "") String serviceType1,
		    @RequestParam(value = "caseTypeId", required = false, defaultValue = "") String caseTypeId,
		    @RequestParam(value = "inserted_by", required = false, defaultValue = "") String inserted_by ) {

		System.out.println("ShowCaseWiseAcksAbstractMAP: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		Map<String, Object> response = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getShowCaseWiseAcksAbstractMAP(authentication,districtId,deptId,fromDate,toDate,advcteName,petitionerName,serviceType1,caseTypeId,inserted_by);
			
			if ((data != null && !data.isEmpty() && data.size() > 0 )   ) {
				for (int i = 0; i < data.size(); i++) {

					if (data.get(i).get("file_found") == null) {
						data.get(i).put("file_found", "No");
					}
					//System.out.println("---------------" + data.get(i).get("file_found"));
				}
				response.put("status", true);
				response.put("scode", "01");
				response.put("CASEWISEACKS", data);
				response.put("sdesc", "Data Found");
				//response.put("HEADING", "District Wise Cases Interim Orders Implementation Report");
				
			}else {
				response.put("status", false);
				response.put("scode", "02");
				response.put("sdesc", "No Records found to display");
			}  
			
			if ((roleId.equals("1") || roleId.equals("7") || roleId.equals("14") || roleId.equals("17") || roleId.equals("19"))) {
				response.put("SHOWUSERWISE", "SHOWUSERWISE");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}
	
}
