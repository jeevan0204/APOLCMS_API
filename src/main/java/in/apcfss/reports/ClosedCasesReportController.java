package in.apcfss.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.services.ClosedCasesReportService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "ContemptCasesAbstract - Access, Refresh", description = "Contempt Cases Abstract Report")
public class ClosedCasesReportController {

	@Autowired
	ClosedCasesReportService service;

	@GetMapping("ClosedCasesReport")
	public Map<String, Object> getClosedCasesReport(Authentication authentication ) {

		System.out.println("Contempt: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
		 
				response = CommonQueryAPIUtils.apiService("data", service.getClosedCasesReport(userPrincipal));

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

	 
}
