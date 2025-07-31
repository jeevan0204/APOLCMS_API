package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface DistrictWiseInterimOrderImplReportService {
	 
	List<Map<String, Object>> getInterimOrdersImplReport(Authentication authentication, String fromDate, String toDate);

	
	List<Map<String, Object>> getCasesListInterim(Authentication authentication, String caseStatus, String distid,  String distName);

	List<Map<String, Object>> getCCCasesReportInterim(Authentication authentication, String fromDate, String toDate);

	List<Map<String, Object>> getNewCasesReportInterim(Authentication authentication);

	List<Map<String, Object>> getLegacyCasesReportInterim(Authentication authentication, String fromDate, String toDate);

	
}
