package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface OfficersRegisteredReportService {
	 
	List<Map<String, Object>> getOfficersRegistered(Authentication authentication, String districtId, String officerType);


}
