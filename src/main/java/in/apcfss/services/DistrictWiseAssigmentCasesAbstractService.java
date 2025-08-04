package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface DistrictWiseAssigmentCasesAbstractService {

	List<Map<String, Object>> getDistrictWiseAssigmentCasesList(Authentication authentication, String section_code);

	List<Map<String, Object>> getDistrictWiseAssigmentCasesDetails(Authentication authentication, String section_code, String actionType, String email, String deptName, String uploadValue, String dist_id);

	
	 
	
	
}
