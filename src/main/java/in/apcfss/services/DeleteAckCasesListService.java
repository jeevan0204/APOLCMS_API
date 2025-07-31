package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface DeleteAckCasesListService {
	

	List<Map<String, Object>> getDeleteCasesListData(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getdataOfPendingCases(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);

	
}
