package in.apcfss.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.repositories.HodWiseReportRepo;
import in.apcfss.services.CommonMethodService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "Hod Wise Report - Access, Refresh", description = "Hod Wise Report")
@SecurityRequirement(name = "bearerAuth")
public class HodwiseReportController {

	@Autowired
	HodWiseReportRepo repo;
	
	@Autowired
	CommonMethodService commonMethodService;

	@GetMapping("getHODwiseReport")
	public Map<String, Object> getHODwiseReport() {
		Map<String, Object> response = new HashMap<>();
		response = CommonQueryAPIUtils.apiService("data", repo.getHodwiseData());
		System.out.println("response" + response);
		return response;
	}

	@GetMapping("getHODwiseNewReport")
	public Map<String, Object> getHODwiseNewReport() {
		Map<String, Object> response = new HashMap<>();
		response = CommonQueryAPIUtils.apiService("data", repo.getHodwiseNewData());
		System.out.println("response new" + response);
		return response;
	}

	@GetMapping("getCategorywiseHodReport")
	public Map<String, Object> getCategorywiseHodReport(@RequestParam("caseCategory") String caseCategory) {
		Map<String, Object> response = new HashMap<>();
		if (caseCategory.equals("legacyService") || caseCategory.equals("legacynonService")) {
			
			response = CommonQueryAPIUtils.apiService("data", commonMethodService.getCategorywiseHodReport(caseCategory));
			response.put("caseType", "old");
			
		} else if (caseCategory.equals("NewService") || caseCategory.equals("newnonService")) {
			
			response = CommonQueryAPIUtils.apiService("data", commonMethodService.getCategorywiseHodReportNew(caseCategory));
			response.put("caseType", "New");
		}
		System.out.println("response get--" + response);
		return response;
	}
}
