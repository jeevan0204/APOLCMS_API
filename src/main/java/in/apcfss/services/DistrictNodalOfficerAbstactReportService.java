package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface DistrictNodalOfficerAbstactReportService {
	
	List<Map<String, Object>> getDistrictNodalOfficerAbstactReport(UserDetailsImpl userPrincipal);
	List<Map<String, Object>> getEmpListData(UserDetailsImpl userPrincipal, int districtId, String district_name);
	
}
