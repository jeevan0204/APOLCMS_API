package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface CaseSearchService {
 
	List<Map<String, Object>> getCaseTypesList(Authentication authentication);
	List<Map<String, Object>> getCasetype();
	List<Map<String, Object>> getCasesListData(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody);
	String getCasesList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);
	String generateAckBarCodePdf128(String cinNo, GPOAckDetailsReqBody reqbody);
	Integer getUpdateBarcodeFile(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody);
}
