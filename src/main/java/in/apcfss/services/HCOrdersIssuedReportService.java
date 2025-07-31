package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface HCOrdersIssuedReportService {


	
	List<Map<String, Object>> getSecDeptWiseDataforHCOIssued(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getDeptWiseDataHCOIssued(UserDetailsImpl userPrincipal,String deptCode,String deptName,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getCaseslistDataHCOIssued(UserDetailsImpl userPrincipal,String deptCode, String deptNmae, String caseStatus,String reportLevel,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getCasesListNew(UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);
	
	
	
}
