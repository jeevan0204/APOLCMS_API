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
import in.apcfss.services.HCOrdersIssuedReportService;
import in.apcfss.services.NextListingDtAbstractService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "ContemptCasesAbstract - Access, Refresh", description = "Contempt Cases Abstract Report")
public class NextListingDtAbstractController {

	@Autowired
	NextListingDtAbstractService  service;

	@Autowired
	UsersRepo repo;

	@GetMapping("NextListingDtAbstract")
	public Map<String, Object> getNextListingDtAbstract(Authentication authentication) {

		System.out.println("Contempt: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			
			System.out.println("list--"+repo.getJudgesList());

			if (userPrincipal.getRoleId().equals("5") || userPrincipal.getRoleId().equals("9")
					|| userPrincipal.getRoleId().equals("10")) {
				String deptCode = (String) userPrincipal.getDeptCode();
				String deptName = repo.getDeptName(deptCode);
				return getHodWiseDetails(authentication,deptCode,deptName);
			}else {

				response = CommonQueryAPIUtils.apiService("data",
						service.getNextListingDtSecWise(userPrincipal));
			}

		} catch (Exception e) {
			e.printStackTrace();

		}  
		return response;
	}

	@GetMapping("HodWiseDetails")
	public Map<String, Object> getHodWiseDetails(Authentication authentication,@RequestParam("deptCode") String deptCode,  @RequestParam("deptName") String deptName) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			 
			response = CommonQueryAPIUtils.apiService("data",
					service.getHodWiseDetails(userPrincipal,deptCode,deptName ));
			//System.out.println("response---"+response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("NextListingDtCasesLists")
	public Map<String, Object> getNextListingDtCasesLists(Authentication authentication,@RequestParam("deptCode") String deptCode,@RequestParam("deptName") String deptName, 
			@RequestParam("caseStatus") String caseStatus ) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	 
		System.out.println("getHCCgetCasesList-------" + userPrincipal.getUserId());
		System.out.println("caseStatus-------" +caseStatus);
		Map<String, Object> response = new HashMap<>();
		try {
			response = CommonQueryAPIUtils.apiService("data",
					service.getNextListingDtCasesLists(userPrincipal,deptCode,deptName,caseStatus ));
			//System.out.println("response" + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
