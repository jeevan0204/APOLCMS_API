package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface EcourtsCaseSearchService {


	List<Map<String, Object>> getCaseTypesListShrtNEW(Authentication authentication);

	List<Map<String, Object>> getSearchCasesList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody,String SelectCaseType);
	
	List<Map<String, Object>> getYearbyCasetypes(Authentication authentication, String caseType);

	List<Map<String, Object>> getNumberbyYear(Authentication authentication, String caseType, int year);

	
	 
	
	
}
