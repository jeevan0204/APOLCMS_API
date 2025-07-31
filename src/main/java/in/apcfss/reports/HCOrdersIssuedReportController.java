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
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "ContemptCasesAbstract - Access, Refresh", description = "Contempt Cases Abstract Report")
public class HCOrdersIssuedReportController {

	@Autowired
	HCOrdersIssuedReportService  service;

	@Autowired
	UsersRepo repo;

	@GetMapping("HCOrdersIssuedReport")
	public Map<String, Object> getHCOrdersIssuedReport(Authentication authentication,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("Contempt: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			
			System.out.println("list--"+repo.getJudgesList());
			
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
				String deptCode = (String) userPrincipal.getDeptCode();
				String deptName = repo.getDeptName(deptCode);
				return HODwisedetailsHCOrdersIssue(authentication,deptCode,deptName,sanitized);
			}else {

				response = CommonQueryAPIUtils.apiService("data",
						service.getSecDeptWiseDataforHCOIssued(userPrincipal,sanitized));
			}

		} catch (Exception e) {
			e.printStackTrace();

		}  
		return response;
	}

	@GetMapping("HODwisedetailsHCOrdersIssued")
	public Map<String, Object> HODwisedetailsHCOrdersIssue(Authentication authentication,@RequestParam("deptCode") String deptCode,  @RequestParam("deptName") String deptName, @Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
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
					service.getDeptWiseDataHCOIssued(userPrincipal,deptCode,deptName,sanitized));
			//System.out.println("response---"+response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}




	@GetMapping("CasesListHCOrdersIssue")
	public Map<String, Object> getCasesListHCOrdersIssue(Authentication authentication,@RequestParam("deptCode") String deptCode,@RequestParam("deptName") String deptName, 
			@RequestParam("caseStatus") String caseStatus, 
			@RequestParam("reportLevel") String reportLevel,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
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
		System.out.println("getHCCgetCasesList-------" + userPrincipal.getUserId());
		System.out.println("caseStatus-------" +caseStatus);
		System.out.println("reportLevel-------" +reportLevel);
		Map<String, Object> response = new HashMap<>();
		try {
			response = CommonQueryAPIUtils.apiService("data",
					service.getCaseslistDataHCOIssued(userPrincipal,deptCode,deptName,caseStatus,reportLevel,sanitized));
			//System.out.println("response" + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	@GetMapping("getCasesListNewHcOrder") 
	public Map<String, Object> getCasesListNewHcOrder(Authentication authentication,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) { 
		Map<String, Object> response = new HashMap<>(); 
		try { 
			UserDetailsImpl userPrincipal =(UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());

			response = CommonQueryAPIUtils.apiService("data",
					service.getCasesListNew(userPrincipal,abstractReqBody));

		} catch (Exception e) { e.printStackTrace(); } return response; }
}
