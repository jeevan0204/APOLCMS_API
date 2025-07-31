package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface SectionOfficerWiseInstructionsReportService {


	List<Map<String, Object>> getSectionOfficerWiseInstructionsReport(Authentication authentication, String fromDate,
			String toDate, String section_code);

	
	List<Map<String, Object>> getAllCasesDetailsInstReport(Authentication authentication, String section_code, String emailId, String ids );
}
