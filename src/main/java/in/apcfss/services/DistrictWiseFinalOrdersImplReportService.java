package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface DistrictWiseFinalOrdersImplReportService {
	 
	List<Map<String, Object>> getFinalOrdersReport(Authentication authentication, String fromDate, String toDate);

	
	List<Map<String, Object>> getCasesListForHCFinal(Authentication authentication, String caseStatus, String distid, String distName);

	List<Map<String, Object>> getCCCasesReport(Authentication authentication, String fromDate, String toDate);

	List<Map<String, Object>> getNewCasesReport(Authentication authentication);

	List<Map<String, Object>> getLegacyCasesReport(Authentication authentication, String fromDate, String toDate);

	
}
