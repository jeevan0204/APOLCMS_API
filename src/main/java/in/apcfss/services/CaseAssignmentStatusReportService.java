package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface CaseAssignmentStatusReportService {

	List<Map<String, Object>> getCaseAssignmentStatus(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);


	List<Map<String, Object>> getCaseAssignmentStatusList(Authentication authentication, String actionType,
			String deptId, String deptName);

	
	 
	
	
}
