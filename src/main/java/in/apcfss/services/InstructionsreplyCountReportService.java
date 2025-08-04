package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface InstructionsreplyCountReportService {

	List<Map<String, Object>> getReplyCountData(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getReplyCountSecDeptdata(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getReplyCountReportViewData(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	
	 
	
	
}
