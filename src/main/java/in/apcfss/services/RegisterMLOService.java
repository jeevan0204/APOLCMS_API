package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface RegisterMLOService {
	 
	List<Map<String, Object>> getDesignationList(String deptCode);

	
	List<Map<String, Object>> getregisterMLOList(String deptCode);

	String saveMloDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBod,
			HttpServletRequest request);

	String updateEmployeeDetailsMlo(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBod,
			HttpServletRequest request, String deptCode);

 
}
