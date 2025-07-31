package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface GPReportService {


	 

	List<Map<String, Object>> CaseWiseDataGPCases(String roleId, Authentication authentication,
			String pwCounterFlag);

	List<Map<String, Object>> CaseWiseDataNewGPCases(String roleId, Authentication authentication,
			String pwCounterFlag);

	Map<String, Object> getcaseStatusUpdateGPReport(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody, String caseNo,
			String caseType);

	List<Map<String, Object>> casewisedataViewInstruction(Authentication authentication );

	List<Map<String, Object>> casewisedataNewViewInstruction(Authentication authentication );

	List<Map<String, Object>> viewCaseDetails(Authentication authentication,String caseNo, String caseType, int caseYear);

	Map<String, Object> DailyStatusEntryReport(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cino, String serno, String caseType);

	ResponseEntity<Map<String, Object>> getSubmitCategoryLegacyDSE(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cIno,String serno);
	
	ResponseEntity<Map<String, Object>> getSubmitCategoryNewDSE(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cIno, String serno);
	
	  ResponseEntity<Map<String, Object>> ApprovedByGp(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno);

	ResponseEntity<Map<String, Object>> RejectedByGp(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno);

	String getApproveGPNew(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno);

	String getRejectGPNew(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request, String cIno);

	  
 
	

	  

	 

	 
	 
	 
	 
}
