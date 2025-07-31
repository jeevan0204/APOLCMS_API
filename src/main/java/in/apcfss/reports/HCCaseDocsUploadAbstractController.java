package in.apcfss.reports;

import java.util.HashMap;
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
import in.apcfss.services.HCCaseDocsUploadAbstractService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "HCCaseDocsUploadAbstract - Access, Refresh", description = "Sect. Dept. Wise Case processing Abstract Report")
public class HCCaseDocsUploadAbstractController {

	@Autowired
	HCCaseDocsUploadAbstractService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("HCCaseDocsUploadAbstract")
	public Map<String, Object> getHCCaseDocsUploadAbstract(Authentication authentication) {

		System.out.println("HCCaseDocsUploadAbstract: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());

			if (userPrincipal.getRoleId().equals("5") || userPrincipal.getRoleId().equals("9")
					|| userPrincipal.getRoleId().equals("10")) {
				String deptCode = (String) userPrincipal.getDeptCode();
				String deptName = repo.getDeptName(deptCode);
				return getHODwisedetails(authentication ,deptCode,deptName);
			}else {

				response = CommonQueryAPIUtils.apiService("secData",
						service.getSecdeptwiseData(userPrincipal));
			}

		} catch (Exception e) {
			e.printStackTrace();

		}  
		return response;
	}

	@GetMapping("HCCaseDocsUploadHODwisedetails")
	public Map<String, Object> getHODwisedetails(Authentication authentication, @RequestParam("deptCode") String deptCode,  @RequestParam("deptName") String deptName) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());

			response = CommonQueryAPIUtils.apiService("hodData",
					service.getDeptWiseData(userPrincipal,deptCode,deptName));
			//System.out.println("response---"+response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("HCCaseDocsUploadCasesList")
	public Map<String, Object> getDocsUploadCasesListdata(Authentication authentication, @RequestParam("deptCode") String deptCode,  @RequestParam("deptName") String deptName,@RequestParam("caseStatus") String caseStatus ) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.err.println("getHCCgetCasesList-------" + userPrincipal.getUserId());

		Map<String, Object> response = new HashMap<>();
		try {
			response = CommonQueryAPIUtils.apiService("caseListData",
					service.getHCCaseDocsUploadCasesList(userPrincipal, deptCode,   deptName, caseStatus));
			//System.out.println("response" + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
