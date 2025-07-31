package in.apcfss.services;

import java.util.List;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
 

public interface EcourtsCaseMappingWithNewAckNoService {


	List<Map<String, Object>> getEcourtsCaseMappingWithNewAckNo(Authentication authentication, String caseTypeId,
			String deptId, String districtId, String dofFromDate, String dofToDate, String advocateName,
			String categoryServiceId);

	
	List<Map<String, Object>> getEcourtsCaseMappingWithNewAckNos(Authentication authentication, String case_type,
			Integer case_year, Integer case_number);


	
	String getsubmitDetailsForNewAckNo(Authentication authentication, String caseType1, int regYear1,
			int mainCaseNo, String ackNo);

 

 
 
	
	
}
