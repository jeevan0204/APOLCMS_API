package in.apcfss.reports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import in.apcfss.services.ContemptCasesAbstractService;
import in.apcfss.services.HCCaseDistWiseAbstractReportService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "HC Case Dist Wise Abstract Report - Access, Refresh", description = "HC Case Dist Wise Abstract Report")
public class HCCaseDistWiseAbstractReportController {

	@Autowired
	HCCaseDistWiseAbstractReportService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("HCCaseDistWiseAbstract")
	public Map<String, Object> getHCCaseDistWiseAbstract(Authentication authentication,
			@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("HC Case Dist Wise Abstract Report: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			response = CommonQueryAPIUtils.apiService("data",
					service.getHCCaseDistWiseAbstract(userPrincipal, abstractReqBody));

		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;
	}

	@GetMapping("getCasesListHCCaseStatusAbstract")
	public Map<String, Object> getCasesListHCCaseStatus(Authentication authentication,
			@RequestParam("distid") String distid, @RequestParam("distName") String distName,
			@RequestParam("caseStatus") String caseStatus, @Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> response = new HashMap<>();
		try {
			HCCaseStatusAbstractReqBody sanitized = new HCCaseStatusAbstractReqBody();

		    if (abstractReqBody.getCategoryServiceId() != null) sanitized.setCategoryServiceId(abstractReqBody.getCategoryServiceId());
		    if (abstractReqBody.getDistId() != null) sanitized.setDistId(abstractReqBody.getDistId());
		    if (abstractReqBody.getDeptId() != null) sanitized.setDeptId(abstractReqBody.getDeptId());
			
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			String heading = "Cases List for " + distName;
			response = CommonQueryAPIUtils.apiService("data",
					service.getCasesListHCCaseStatus(userPrincipal, distid, distName, caseStatus, sanitized));
			response.put("heading", heading);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	@GetMapping("CasesReportList")
	public Map<String, Object> getCasesReportList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			response = CommonQueryAPIUtils.apiService("data",
					service.getCasesReportList(userPrincipal, abstractReqBody));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
