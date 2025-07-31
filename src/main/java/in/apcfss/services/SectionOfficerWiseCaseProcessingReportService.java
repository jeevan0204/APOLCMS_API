package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface SectionOfficerWiseCaseProcessingReportService {
	 
	List<Map<String, Object>> getCasesListSectionOfficer(Authentication authentication, String fromDate, String toDate,
			String caseTypeId,String section_code);


	List<Map<String, Object>> getAllCasesDetails(Authentication authentication, String section_code, String emailId, String ids, String date);
}
