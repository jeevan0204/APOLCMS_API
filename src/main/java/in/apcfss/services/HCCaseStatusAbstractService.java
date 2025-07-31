package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface HCCaseStatusAbstractService {


	List<Map<String, Object>> getHODwisedetails(UserDetailsImpl userPrincipal, String deptId, String deptName,HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getLegacyReportList(String roleId, String userid, UserDetailsImpl userPrincipal,HCCaseStatusAbstractReqBody abstractReqBody);

	List<Map<String, Object>> getHCCgetCasesList(UserDetailsImpl userPrincipal, String caseStatus, 
			String deptCode,String deptName,String reportLevel,HCCaseStatusAbstractReqBody abstractReqBody);


}
