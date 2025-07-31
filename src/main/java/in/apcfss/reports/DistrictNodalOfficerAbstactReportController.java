package in.apcfss.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import in.apcfss.services.DistrictNodalOfficerAbstactReportService;
import in.apcfss.services.PullBackLegacyCasesService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "DistrictNodalOfficerAbstactReport - Access, Refresh", description = "District Nodal Officer Abstact Report")
public class DistrictNodalOfficerAbstactReportController {

	@Autowired
	DistrictNodalOfficerAbstactReportService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("DistrictNodalOfficerAbstactReport")
	public Map<String, Object> getDistrictNodalOfficerAbstactReport(Authentication authentication ) {

		System.out.println("DistrictNodalOfficerAbstactReport: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
		 
				response = CommonQueryAPIUtils.apiService("data", service.getDistrictNodalOfficerAbstactReport(userPrincipal));

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return response;
	}
	
	@GetMapping("getEmpListData")
	public Map<String, Object> getEmpListData(Authentication authentication,
			@RequestParam("districtId") int districtId, @RequestParam("district_name") String district_name) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			 
			response = CommonQueryAPIUtils.apiService("data",
					service.getEmpListData(userPrincipal, districtId, district_name));
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
