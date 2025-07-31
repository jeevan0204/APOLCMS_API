package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface GPOAcknowledgementService {

	ResponseEntity<Map<String, Object>> saveGPOAckForm(Authentication authentication,GPOAckDetailsReqBody reqbody, HttpServletRequest request);

	String getMobileNo(String tableName, String employeeId);

	List<Map<String, Object>> getAcknowledementsList(UserDetailsImpl userPrincipal);

	List<Map<String, Object>> gpoAcknowledementsListAll(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,String ackDate,String ackType);

	List<Map<String, Object>> getDisplayAckEditFormList(Authentication authentication,
			 String ackNo);

	List<Map<String, Object>> getData2(String ackNo);

	
	String getUpdateAckDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);

	Integer getDeleteAckDetails(Authentication authentication,   String ackNo);

	List<Map<String, Object>> getExistingCaseDetails(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, String ackDate, String ackType);
	
	

}
