package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface AssignmentAndNewCasesService {
 
	List<Map<String, Object>> getAssignmentAndNewCasesList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	String getassign2DeptHOD(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);
	
	String getAssignMultiCasesToSection(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);

	String getAssignToDistCollector(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);

}
