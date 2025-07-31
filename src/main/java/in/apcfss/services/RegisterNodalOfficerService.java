package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface RegisterNodalOfficerService {
	 
	List<Map<String, Object>> getNodalList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getDistNodalDepartmentList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getNodalDepartmentList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getHodEmployeeDetails(Authentication authentication,String deptId);

	
	List<Map<String, Object>> getRegistrerNodalDesignationList(Authentication authentication,String deptId);

	
	String getSaveEmployeeDetailsNodalOfficer(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBod,HttpServletRequest request);

	
}
