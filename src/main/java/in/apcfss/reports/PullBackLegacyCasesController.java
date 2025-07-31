package in.apcfss.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.ClosedCasesReportService;
import in.apcfss.services.ContemptCasesAbstractService;
import in.apcfss.services.PullBackLegacyCasesService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "PullBackLegacyCases - Access, Refresh", description = "Pull Back Legacy Cases")
public class PullBackLegacyCasesController {

	@Autowired
	PullBackLegacyCasesService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("PullBackLegacyCasesList")
	public Map<String, Object> getPullBackLegacyCasesList(Authentication authentication ) {

		System.out.println("PullBackLegacyCasesList: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
		 
				response = CommonQueryAPIUtils.apiService("data", service.getPullBackLegacyCasesList(userPrincipal));

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return response;
	}

	@PostMapping("sendCaseBackLegacyCases")
	public ResponseEntity<Map<String, Object>> getsendCaseBackLegacyCases(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {
		System.out.println("sendCaseBackLegacyCases: ");
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println("User ID: " + userPrincipal.getUserId());
		return service.getsendCaseBackLegacyCases(userPrincipal,abstractReqBody);
	}

}
