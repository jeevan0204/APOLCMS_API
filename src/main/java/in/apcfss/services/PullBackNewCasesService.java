package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface PullBackNewCasesService {

	List<Map<String, Object>> getPullBackNewCasesList(UserDetailsImpl userPrincipal);

	
	String getSendCaseBackNewCases(UserDetailsImpl userPrincipal, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);

	
	 
	
	
}
