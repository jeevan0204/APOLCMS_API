package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface HCCaseDistWiseAbstractReportService {


	
	List<Map<String, Object>> getHCCaseDistWiseAbstract(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getCasesListHCCaseStatus(UserDetailsImpl userPrincipal ,String distid, String distName, String caseStatus,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getCasesReportList(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);
	
}
