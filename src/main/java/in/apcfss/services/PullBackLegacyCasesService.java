package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface PullBackLegacyCasesService {
	
	List<Map<String, Object>> getPullBackLegacyCasesList(UserDetailsImpl userPrincipal );

	ResponseEntity<Map<String, Object>> getsendCaseBackLegacyCases(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody );

	
}
