package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface HCCaseDocsUploadStatusAbstractReportNewService {


	
	List<Map<String, Object>> getSecdeptwiseData(UserDetailsImpl userPrincipal, String dofFromDate, String dofToDate, String caseTypeId,String districtId, String deptId, String petitionerName,String serviceType1,String advocateName);
	List<Map<String, Object>> getDeptWiseData(UserDetailsImpl userPrincipal,String deptCode,  String deptName );
	List<Map<String, Object>> getHCCaseDocsUploadCasesList(UserDetailsImpl userPrincipal,String deptCode,  String deptName,String caseStatus,  String respondenttype );

	
	
	
}
