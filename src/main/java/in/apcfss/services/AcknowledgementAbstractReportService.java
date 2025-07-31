package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface AcknowledgementAbstractReportService {
	
	List<Map<String, Object>> getAcksAbstractReportHODWise(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId);

	
	List<Map<String, Object>> getDISTWISEACKS(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId);

	List<Map<String, Object>> getshowUserWise(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId);

	List<Map<String, Object>> getshowCaseWiseAcksAbstract(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId,String inserted_by);


	

	
}
