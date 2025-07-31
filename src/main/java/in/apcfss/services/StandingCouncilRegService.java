package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface StandingCouncilRegService {

	List<Map<String, Object>> getStandingCounsel(Authentication authentication);

	
	String saveStandingCounselEmployee(UserDetailsImpl userPrincipal, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);

	
	 
	
	
}
