package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface HCCaseDocsUploadAbstractService {


	
	List<Map<String, Object>> getSecdeptwiseData(UserDetailsImpl userPrincipal);
	List<Map<String, Object>> getDeptWiseData(UserDetailsImpl userPrincipal,String deptCode,  String deptName);
	List<Map<String, Object>> getHCCaseDocsUploadCasesList(UserDetailsImpl userPrincipal,String deptCode,  String deptName,String caseStatus );

	
	
	
}
