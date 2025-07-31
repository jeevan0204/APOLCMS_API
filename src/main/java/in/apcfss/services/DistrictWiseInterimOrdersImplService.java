package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface DistrictWiseInterimOrdersImplService {

	List<Map<String, Object>> getInterimCASESLIST(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);
	 
	Map<String, Object> getCaseStatusUpdate(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String cIno);

	
	String getupdateCaseDetails(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno);

	
	 
	
	
}
