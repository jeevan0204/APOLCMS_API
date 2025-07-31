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
import in.apcfss.services.HCCaseDocsUploadStatusAbstractReportNewService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "HCCaseDocsUploadStatusAbstractReportNewController - Access, Refresh", description = "HC Case Docs Upload Status Abstract Report New  ")
public class HCCaseDocsUploadStatusAbstractReportNewController {

	@Autowired
	HCCaseDocsUploadStatusAbstractReportNewService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("/HCCaseDocsUploadAbstractNew")
	public Map<String, Object> getHCCaseDocsUploadAbstract(Authentication authentication,@RequestParam("dofFromDate") String dofFromDate,@RequestParam("dofToDate") String dofToDate,
			@RequestParam("caseTypeId") String caseTypeId,@RequestParam("districtId") String districtId,@RequestParam("deptId") String deptId,@RequestParam("petitionerName") String petitionerName,
			@RequestParam("serviceType1") String serviceType1,@RequestParam("advocateName") String advocateName) {

		System.out.println("HCCaseDocsUploadAbstractNew: ");
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());

			if (userPrincipal.getRoleId().equals("5") || userPrincipal.getRoleId().equals("9")
					|| userPrincipal.getRoleId().equals("10")) {
				String deptCode = (String) userPrincipal.getDeptCode();
				String deptName = repo.getDeptName(deptCode);
				return getHODwisedetails(authentication ,deptCode,deptName );
			}else {
				response.put("HEADING", "Sect. Dept. Wise Case processing Abstract Report");
				response = CommonQueryAPIUtils.apiService("secData",
						service.getSecdeptwiseData(userPrincipal,dofFromDate,dofToDate,caseTypeId,districtId,deptId,petitionerName ,serviceType1,advocateName));
			}

		} catch (Exception e) {
			e.printStackTrace();

		}  
		return response;
	}

	@GetMapping("HCCaseDocsUploadHODwisedetailsNew")
	public Map<String, Object> getHODwisedetails(Authentication authentication,
			@RequestParam("deptCode") String deptCode,@RequestParam("deptName")  String deptName ) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
			System.out.println("User ID: " + userPrincipal.getUserId());
			 
			response = CommonQueryAPIUtils.apiService("hodData",
					service.getDeptWiseData(userPrincipal,deptCode,deptName ));
			//System.out.println("response---"+response);
			response.put("HEADING", "HOD Wise Case processing Abstract for " + deptName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("HCCaseDocsUploadCasesListNew")
	public Map<String, Object> getDocsUploadCasesListdata(Authentication authentication, @RequestParam("deptId1") String deptId1,@RequestParam("deptName") String deptName ,@RequestParam("caseStatus") String caseStatus  ,@RequestParam("respondenttype")  String respondenttype ) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.err.println("HCCaseDocsUploadCasesListNew-------" + userPrincipal.getUserId());
		//String deptCode = (String) userPrincipal.getDeptCode();
		//String deptName = repo.getDeptName(deptId1);
		Map<String, Object> response = new HashMap<>();
		
		String heading="";
		try {
			if (!caseStatus.equals("")) {

				if (caseStatus.equals("TOTAL")) {
					heading += " Total Cases List";
				}

				if (caseStatus.equals("CLOSED")) {
					heading += " Closed Cases List";
				}

				if (caseStatus.equals("PWRUPLOADED")) {
					heading += " Parawise Remarks Uploaded Cases List";
				}

				if (caseStatus.equals("PWRNOTUPLOADED")) {
					heading += " Parawise Remarks Uploaded Cases List";
				}

				if (caseStatus.equals("PWRAPPROVEDBYGP")) {
					heading += " and Parawise Remarks Approved By Gp";
				}

				if (caseStatus.equals("PWRREJEDBYGP")) {
					heading += " and Parawise Remarks Rejected By Gp";
				}

				if (caseStatus.equals("COUNTERUPLOADED")) {
					heading += " Counter Uploaded Cases";
				}

				if (caseStatus.equals("COUNTERNOTUPLOADED")) {
					heading += " Counter Not Uploaded Cases";
				}

				if (caseStatus.equals("GPCOUNTERAPPROVED")) {
					heading += " and Counters Filed Approved By Gp";
				}

				if (caseStatus.equals("GPCOUNTERREJECTED")) {
					heading += " and Counters Filed Rejected By Gp";
				}

				if (caseStatus.equals("ALL")) {
					 
				}

				if (caseStatus.equals("HOD")) {
				}
			}
			response = CommonQueryAPIUtils.apiService("caseListData",
					service.getHCCaseDocsUploadCasesList(userPrincipal, deptId1,   deptName, caseStatus, respondenttype));
			response.put("HEADING", heading);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
