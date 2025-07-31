package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface AcknowledgementsReportService {
	
	List<Map<String, Object>> getACKDATA(Authentication authentication);

	List<Map<String, Object>> getACKDATAExistingCase(Authentication authentication);

	List<Map<String, Object>> getUSERWISEACKDATA(Authentication authentication,String ackDate);

	
}
