package in.apcfss.reports;

import java.util.ArrayList;
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
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AcknowledgementsReportService;
import in.apcfss.services.DeleteAckCasesListService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DeleteAckCasesListController - Access, Refresh", description = "Delete Ack Cases List Controller")
public class DeleteAckCasesListController {

	@Autowired
	DeleteAckCasesListService service;
	
	@Autowired
	UsersRepo repo;

	@GetMapping("DeleteCasesListData")
	public Map<String, Object> getDeleteCasesListData(Authentication authentication ,HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("DeleteCasesListData: ");

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String roleId=userPrincipal.getRoleId();

		Map<String, Object> response = new HashMap<>();
		try {

			if (roleId.equals("13") || roleId.equals("14")) {
				List<Map<String, Object>> data = service.getDeleteCasesListData(authentication,abstractReqBody);

				if (data != null && !data.isEmpty() && data.size() > 0 ){
					response.put("show_flag", "Y");
					response.put("status", true);
					response.put("scode", "01");
					response.put("DATA", data);
					response.put("HEADING","List Of Deleted Cases");
					response.put("sdesc", "Records Found.");
				}else {
					response.put("status", false);
					response.put("scode", "02");
					response.put("sdesc", "No Records found to display");
				}  
			}

		} catch (Exception e) {
			response.put("sdesc", "Exception occurred : No Records found to display");
			e.printStackTrace();

		} 
		return response;
	}
	
	@GetMapping("getdataOfPendingCases")
	public Map<String, Object> getdataOfPendingCases(Authentication authentication ,HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("getdataOfPendingCases: ");

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String roleId=userPrincipal.getRoleId();

		Map<String, Object> response = new HashMap<>();
		try {

			if (roleId.equals("13") || roleId.equals("14")) {
				List<Map<String, Object>> data = service.getdataOfPendingCases(authentication,abstractReqBody);

				if (data != null && !data.isEmpty() && data.size() > 0 ){
					response.put("show_flag", "Y");
					response.put("status", true);
					response.put("scode", "01");
					response.put("DATA", data);
					response.put("HEADING","List Of Total Pending cases Against The Government Of Andhra Pradesh Report");
					response.put("sdesc", "Records Found.");
				}else {
					response.put("status", false);
					response.put("scode", "02");
					response.put("sdesc", "No Records found to display");
				}  
			}

		} catch (Exception e) {
			response.put("sdesc", "Exception occurred : No Records found to display");
			e.printStackTrace();

		} 
		return response;
	}

	@GetMapping("/getCaseTypeMstNEW")
	public List<Map<String, Object>> getCaseTypeMstNEW(Authentication authentication) {
		 
	    List<Map<String, Object>> rawList = repo.caseTypeMstNEW();
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}
	
	@GetMapping("/getResAdvList")
	public List<Map<String, Object>> getResAdvList(Authentication authentication) {
		 
	    List<Map<String, Object>> rawList = repo.getResAdvList();
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    for (Map<String, Object> rawMap : rawList) {
	        Map<String, Object> map = new HashMap<>();
	        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
	            map.put(entry.getKey(), entry.getValue());
	        }
	        responseList.add(map);
	    }

	    return responseList;
	}
	
	
}
