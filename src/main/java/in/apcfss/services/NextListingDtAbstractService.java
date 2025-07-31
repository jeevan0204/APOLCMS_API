package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface NextListingDtAbstractService {


	
	List<Map<String, Object>> getNextListingDtSecWise(UserDetailsImpl userPrincipal );
	List<Map<String, Object>> getHodWiseDetails(UserDetailsImpl userPrincipal,String deptCode,String deptName );
	List<Map<String, Object>> getNextListingDtCasesLists(UserDetailsImpl userPrincipal,String deptCode, String deptNmae, String caseStatus  );
	
	
	
}
