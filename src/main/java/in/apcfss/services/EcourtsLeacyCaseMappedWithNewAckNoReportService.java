package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface EcourtsLeacyCaseMappedWithNewAckNoReportService {
	 
	List<Map<String, Object>> getDeptWiseMAP(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId);

	
	List<Map<String, Object>> getDeptWiseMAPslno(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId);

	List<Map<String, Object>> getDistWiseMAP(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId);

	List<Map<String, Object>> getUserWiseMAP(Authentication authentication, String districtId, String deptId,
			String fromDate, String toDate, String advcteName, String petitionerName, String serviceType1,
			String caseTypeId);

	List<Map<String, Object>> getCaseWiseAcksAbstractMAPslno(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId,String inserted_by);

	List<Map<String, Object>> getShowCaseWiseAcksAbstractMAP(Authentication authentication, String districtId,
			String deptId, String fromDate, String toDate, String advcteName, String petitionerName,
			String serviceType1, String caseTypeId,String inserted_by);

	
}
