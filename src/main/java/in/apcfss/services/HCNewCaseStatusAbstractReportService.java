package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface HCNewCaseStatusAbstractReportService {
	
	List<Map<String, Object>> getHCNewCaseStatusSECWISE(Authentication authentication, String dofFromDate,
			String dofToDate, String caseTypeId, String districtId, String regYear, String deptId,
			String petitionerName, String respodentName, String serviceType1 );
	
	List<Map<String, Object>> getHODwisedetails(Authentication authentication, String dofFromDate, String dofToDate,
			String caseTypeId, String districtId, String regYear, String deptId, String petitionerName,
			String respodentName, String serviceType1,String deptName);

	List<Map<String, Object>> getHccNewCasesList(Authentication authentication, String dofFromDate, String dofToDate,
			String caseTypeId, String districtId, String regYear, String deptId, String petitionerName,
			String respodentName, String serviceType1, String deptName, String caseStatus, String reportLevel,
			String caseCategory, String deptType);


	

	
}
