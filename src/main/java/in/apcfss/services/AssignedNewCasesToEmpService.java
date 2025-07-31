package in.apcfss.services;

import java.util.List;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface AssignedNewCasesToEmpService {
	

	List<Map<String, Object>> getAssignedNewCasesToEmp(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getÙsersList(String cIno);

	List<Map<String, Object>> getRespodentList(String cIno);

	List<Map<String, Object>> getOtherRespodentList(String cIno);

	List<Map<String, Object>> getActivitiesData(String cIno);

	Map<String, Object> getCaseStatusUpdate(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody,String ackNo);

	String getUpdateCaseDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request);

	String getforwardCaseDetailsNew(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);

	
	  String getsendBackCaseDetailsNew(Authentication authentication,
	  HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request);

	  
	String getforwardCaseDetails2GPNew(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);

	
	String getGpApproveNew(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);
	 
	
	
}
