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
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "ContemptCasesAbstract - Access, Refresh", description = "Contempt Cases Abstract Report")
public class ContemptCasesAbstractController {

	@Autowired
	ContemptCasesAbstractService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("ContemptCasesAbstractReport")
	public Map<String, Object> getContemptCasesAbstractReport(Authentication authentication,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("Contempt: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			  String roleId =userPrincipal.getRoleId();
			  HCCaseStatusAbstractReqBody sanitized = new HCCaseStatusAbstractReqBody();

			    if (abstractReqBody.getRegYear() != null) sanitized.setRegYear(abstractReqBody.getRegYear());
			    if (abstractReqBody.getDofFromDate() != null) sanitized.setDofFromDate(abstractReqBody.getDofFromDate());
			    if (abstractReqBody.getDofToDate() != null) sanitized.setDofToDate(abstractReqBody.getDofToDate());
			    if (abstractReqBody.getPetitionerName() != null) sanitized.setPetitionerName(abstractReqBody.getPetitionerName());
			    if (abstractReqBody.getRespodentName() != null) sanitized.setRespodentName(abstractReqBody.getRespodentName());
			    if (abstractReqBody.getCategoryServiceId() != null) sanitized.setCategoryServiceId(abstractReqBody.getCategoryServiceId());
			    if (abstractReqBody.getCaseTypeId() != null) sanitized.setCaseTypeId(abstractReqBody.getCaseTypeId());
			    if (abstractReqBody.getDistId() != null) sanitized.setDistId(abstractReqBody.getDistId());
			    if (abstractReqBody.getDeptId() != null) sanitized.setDeptId(abstractReqBody.getDeptId());
			 
			if (userPrincipal.getRoleId().equals("5") || userPrincipal.getRoleId().equals("9")
					|| userPrincipal.getRoleId().equals("10")) {
				
				return getHODwisedetails(authentication,roleId,sanitized);
			}else {

				response = CommonQueryAPIUtils.apiService("data",
						service.getSecdeptwiseData(userPrincipal,sanitized));
				
				response.put("HEADING", "Sect. Dept. Wise Contempt Cases Abstract Report");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
		return response;
	}

	@GetMapping("getDeptWiseData")
	public Map<String, Object> getHODwisedetails(Authentication authentication, String roleId,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> response = new HashMap<>();
		String deptId=null,deptName="";
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			roleId= userPrincipal.getRoleId();
			
			if (roleId.equals("5") || roleId.equals("9") || roleId.equals("10")) {
				deptId = (String) userPrincipal.getDeptCode();
				deptName = repo.getDeptName(deptId);
			} else {
				deptId = (String) userPrincipal.getDeptCode();
				deptName = repo.getDeptName(deptId);
			}
			
			HCCaseStatusAbstractReqBody sanitized = new HCCaseStatusAbstractReqBody();

		    if (abstractReqBody.getRegYear() != null) sanitized.setRegYear(abstractReqBody.getRegYear());
		    if (abstractReqBody.getDofFromDate() != null) sanitized.setDofFromDate(abstractReqBody.getDofFromDate());
		    if (abstractReqBody.getDofToDate() != null) sanitized.setDofToDate(abstractReqBody.getDofToDate());
		    if (abstractReqBody.getPetitionerName() != null) sanitized.setPetitionerName(abstractReqBody.getPetitionerName());
		    if (abstractReqBody.getRespodentName() != null) sanitized.setRespodentName(abstractReqBody.getRespodentName());
		    if (abstractReqBody.getCategoryServiceId() != null) sanitized.setCategoryServiceId(abstractReqBody.getCategoryServiceId());
		    if (abstractReqBody.getCaseTypeId() != null) sanitized.setCaseTypeId(abstractReqBody.getCaseTypeId());
		    if (abstractReqBody.getDistId() != null) sanitized.setDistId(abstractReqBody.getDistId());
		    if (abstractReqBody.getDeptId() != null) sanitized.setDeptId(abstractReqBody.getDeptId());
			
			response = CommonQueryAPIUtils.apiService("data",
					service.getDeptWiseData(userPrincipal,roleId,sanitized));
			//System.out.println("response---"+response); 
			response.put("HEADING", "HOD Wise Contempt Cases Abstract Report for " + deptName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	@GetMapping("getDeptNameWiseData")
	public Map<String, Object> getDeptNameWiseData(Authentication authentication,@RequestParam("deptCode") String deptCode,@RequestParam("deptName") String deptName) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			System.out.println("deptCode: " + deptCode);

			response = CommonQueryAPIUtils.apiService("data",
					service.getDeptNameWiseData(userPrincipal,deptCode,deptName));
			//System.out.println("response---"+response);
			response.put("HEADING", "HOD Wise Contempt Cases Abstract Report for " + deptName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@GetMapping("ContemptCasesListdata")
	public Map<String, Object> getContemptCasesListdata(Authentication authentication,@RequestParam("deptCode") String deptCode,@RequestParam("deptName") String deptName, 
			@RequestParam("caseStatus") String caseStatus, 
			@RequestParam("reportLevel") String reportLevel,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		String heading = "Contempt Cases List for " + deptName;

		if (!caseStatus.equals("")) {
			if (caseStatus.equals("withSD")) {
				heading += " Pending at Sect. Dept. Login";
			}
			if (caseStatus.equals("withMLO")) {
				heading += " Pending at MLO Login";
			}
			if (caseStatus.equals("withHOD")) {
				heading += " Pending at HOD Login";
			}
			if (caseStatus.equals("withNO")) {
				heading += " Pending at Nodal Officer(HOD) Login";
			}
			if (caseStatus.equals("withSDSec")) {
				heading += " Pending at Section Officers Login (Sect Dept.)";
			}
			if (caseStatus.equals("withsection")) {
				heading += " Pending at Section Officers Login (Sect Dept.)";
			}
			if (caseStatus.equals("withDC")) {
				heading += " Pending at District Collector Login";
			}
			if (caseStatus.equals("withDistNO")) {
				heading += " Pending at Nodal Officer(District) Login";
			}
			if (caseStatus.equals("withHODSec")) {
				heading += " Pending at Section Officer(HOD) Login";
			}
			if (caseStatus.equals("withDistSec")) {
				heading += " Pending at Section Officer(District) Login";
			}
			if (caseStatus.equals("withGP")) {
				heading += " Pending at GP Login";
			}
			if (caseStatus.equals("closed")) {
				heading += " All Closed Cases ";
			}
			if (caseStatus.equals("goi")) {
				heading += " Govt. of India ";
			}
			if (caseStatus.equals("psu")) {
				heading += " PSU ";
			}
			if (caseStatus.equals("Private")) {
				heading += " Private ";
			}
		}
		
		HCCaseStatusAbstractReqBody sanitized = new HCCaseStatusAbstractReqBody();

	    if (abstractReqBody.getRegYear() != null) sanitized.setRegYear(abstractReqBody.getRegYear());
	    if (abstractReqBody.getDofFromDate() != null) sanitized.setDofFromDate(abstractReqBody.getDofFromDate());
	    if (abstractReqBody.getDofToDate() != null) sanitized.setDofToDate(abstractReqBody.getDofToDate());
	    if (abstractReqBody.getPetitionerName() != null) sanitized.setPetitionerName(abstractReqBody.getPetitionerName());
	    if (abstractReqBody.getRespodentName() != null) sanitized.setRespodentName(abstractReqBody.getRespodentName());
	    if (abstractReqBody.getCategoryServiceId() != null) sanitized.setCategoryServiceId(abstractReqBody.getCategoryServiceId());
	    if (abstractReqBody.getCaseTypeId() != null) sanitized.setCaseTypeId(abstractReqBody.getCaseTypeId());
	    if (abstractReqBody.getDistId() != null) sanitized.setDistId(abstractReqBody.getDistId());
	    if (abstractReqBody.getDeptId() != null) sanitized.setDeptId(abstractReqBody.getDeptId());
		System.err.println("getHCCgetCasesList-------" + userPrincipal.getUserId());
		System.err.println("caseStatus-------" +caseStatus);
		Map<String, Object> response = new HashMap<>();
		try {
			response = CommonQueryAPIUtils.apiService("data",
					service.getContemptCasesListdata(userPrincipal,deptCode,deptName,caseStatus,reportLevel,sanitized));
			//System.out.println("response" + response);
			response.put("HEADING",heading);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
