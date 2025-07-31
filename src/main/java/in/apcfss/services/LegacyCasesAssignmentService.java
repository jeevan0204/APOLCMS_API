package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface LegacyCasesAssignmentService {


	
	List<Map<String, Object>> getHighCourtCasesListdata(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);
	ResponseEntity<Map<String, Object>> getassign2DeptHOD(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody );
	
	ResponseEntity<Map<String, Object>> getassignMultiCases2Section(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody );
	
	ResponseEntity<Map<String, Object>> getassign2DistCollector(UserDetailsImpl userPrincipal, HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> empDeptListToAssignCases(UserDetailsImpl userPrincipal,String deptCode,String chkdVal);
}
