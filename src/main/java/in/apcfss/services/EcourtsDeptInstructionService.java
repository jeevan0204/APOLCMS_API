package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface EcourtsDeptInstructionService {

	List<Map<String, Object>> getAckList(UserDetailsImpl userPrincipal, HCCaseStatusAbstractReqBody abstractReqBody);
	 
	List<Map<String, Object>> getYearByCaseTypeYear(Authentication authentication, String caseType);

	
	List<Map<String, Object>> getNumberbyCaseType(Authentication authentication, String caseType, int regYear);

	
	Map<String, Object> getCasesListEcourtsDept(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody, String caseType);

	
	String getSubmitCategoryecourtsDeptInstruction(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request);

	
	 
	
	
}
