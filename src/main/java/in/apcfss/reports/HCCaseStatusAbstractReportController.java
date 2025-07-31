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
import in.apcfss.services.HCCaseStatusAbstractService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "HCCaseStatusAbstractReport - Access, Refresh", description = "HC CaseStatus Abstract Report")
public class HCCaseStatusAbstractReportController {

	@Autowired
	HCCaseStatusAbstractService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("getLegacyAbstractReportList")
	public Map<String, Object> getLegacyData(Authentication authentication,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			System.out.println("Role ID: " + userPrincipal.getRoleId());
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
				String deptId = (String) userPrincipal.getDeptCode();
				String deptName = repo.getDeptName(deptId);
				System.out.println("Calling getHCCHODwisedetails with deptId=" + deptId);
				return getHCCHODwisedetails(authentication, deptId, deptName,sanitized);
			}else {

			response = CommonQueryAPIUtils.apiService("data",
					service.getLegacyReportList(userPrincipal.getRoleId(), userPrincipal.getUserId(), userPrincipal,sanitized));
			//System.out.println("Response from service: " + response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("HCCHODwisedetails")
	public Map<String, Object> getHCCHODwisedetails(Authentication authentication,@RequestBody 
			@RequestParam("deptCode") String deptCode, @RequestParam("deptName") String deptName,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.err.println("HCCHODwisedetails-------" + userPrincipal.getUserId());
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
		
		Map<String, Object> response = new HashMap<>();
		try {
			response = CommonQueryAPIUtils.apiService("data",
					service.getHODwisedetails(userPrincipal, deptCode, deptName,sanitized));
			  //System.out.println("response" + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("HCCgetCasesList")
	public Map<String, Object> getHCCgetCasesList(Authentication authentication,@RequestParam("deptCode") String deptCode,@RequestParam("deptName") String deptName, 
			@RequestParam("status") String status, 
			@RequestParam("level") String level,@Valid @ModelAttribute HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.err.println("getHCCgetCasesList-------" + userPrincipal.getUserId());
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
		Map<String, Object> response = new HashMap<>();
		try {
			response = CommonQueryAPIUtils.apiService("data",
					service.getHCCgetCasesList(userPrincipal,deptCode,deptName,status,level,sanitized));
			//System.out.println("response" + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
