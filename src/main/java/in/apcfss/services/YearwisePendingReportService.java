package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface YearwisePendingReportService {
	 
	List<Map<String, Object>> getYearWisePendingCasesReport(Authentication authentication, String fromDate,
			String toDate, String section_code);

	
	List<Map<String, Object>> getCasesListSectionOfficerYear(Authentication authentication,   String section_code, String emailId, String ids, String date);
}
