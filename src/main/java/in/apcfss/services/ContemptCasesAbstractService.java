package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface ContemptCasesAbstractService {


	
	List<Map<String, Object>> getSecdeptwiseData(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getDeptWiseData(UserDetailsImpl userPrincipal,String roleId,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getContemptCasesListdata(UserDetailsImpl userPrincipal,String deptCode, String deptNmae, String caseStatus,String reportLevel,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getDeptNameWiseData(UserDetailsImpl userPrincipal,String deptCode, String deptName);
	
	
	
}
