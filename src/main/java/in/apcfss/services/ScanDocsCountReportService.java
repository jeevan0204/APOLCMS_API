package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface ScanDocsCountReportService {
	 
	List<Map<String, Object>> ScanDocsCountReport(Authentication authentication);

	List<Map<String, Object>> getScanCountForNewCases(Authentication authentication);

	List<Map<String, Object>> getNotAvailableOldScanDocsCount(Authentication authentication, String year, String month);

	
}
