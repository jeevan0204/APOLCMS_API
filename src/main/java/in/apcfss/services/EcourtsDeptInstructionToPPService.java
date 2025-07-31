package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface EcourtsDeptInstructionToPPService {

	List<Map<String, Object>> getAckList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);
	 
	Map<String, Object> getCasesListEcourtsDeptNewPP(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody, String caseType);

	
	String getSubmitCategoryEcourtsDeptNewPP(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request);

	
	 
	
	
}
