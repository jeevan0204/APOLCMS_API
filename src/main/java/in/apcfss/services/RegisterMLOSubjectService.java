package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface RegisterMLOSubjectService {
	 
	List<Map<String, Object>> getDesignationList(String deptCode);

	
	List<Map<String, Object>> getRegisterMLOSubjectList(String deptCode);

	String saveMloSubjectDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBod,
			HttpServletRequest request);

	 
 
}
