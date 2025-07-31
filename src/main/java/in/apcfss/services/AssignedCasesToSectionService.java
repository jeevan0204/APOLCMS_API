package in.apcfss.services;

import java.util.List;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface AssignedCasesToSectionService {


	
	List<Map<String, Object>> getAssignedCasesToSectionList(UserDetailsImpl userPrincipal ,HCCaseStatusAbstractReqBody abstractReqBody);
	List<Map<String, Object>> getEMPList(UserDetailsImpl userPrincipal);
	List<Map<String, Object>> getNoList(UserDetailsImpl userPrincipal);
	
	List<Map<String, Object>> getCaseData(String cIno);
	List<Map<String, Object>> getCaseDataNEW(String cIno);
	
	List<Map<String, Object>> getUSERSLIST(String cIno);
	List<Map<String, Object>> getDocumentsList(String cIno);
	List<Map<String, Object>> getActlist(String cIno);
	
	List<Map<String, Object>> getOtherDocumentsList(String cIno);
	List<Map<String, Object>> getMappedInstructionList(String cIno);
	List<Map<String, Object>> getMappedPWRList(String cIno);
	
	List<Map<String, Object>> getOrderList(String cIno);
	List<Map<String, Object>> getIAFILINGLIST(String cIno);
	List<Map<String, Object>> getINTERIMORDERSLIST(String cIno);
	List<Map<String, Object>> getLINKCASESLIST(String cIno);
	List<Map<String, Object>> getOBJECTIONSLIST(String cIno);
	List<Map<String, Object>> getCASEHISTORYLIST(String cIno);
	List<Map<String, Object>> getPETEXTRAPARTYLIST(String cIno);
	List<Map<String, Object>> getRESEXTRAPARTYLIST(String cIno);
	List<Map<String, Object>> getACTIVITIESDATA(String cIno);
	List<Map<String, Object>> getOLCMSCASEDATA(String cIno);
	List<Map<String, Object>> getDEPTNSTRUCTIONS(String cIno);
	
	List<Map<String, Object>> getUsersListForUpdate(Authentication authentication,String cIno );
	List<Map<String, Object>> getActlistForUpdate(String cIno);
	List<Map<String, Object>> getOrderlistForUpdate(String cIno);
	List<Map<String, Object>> getIAFILINGLISTForUpdate(String cIno);
	List<Map<String, Object>> getINTERIMORDERSLISTForUpdate(String cIno);
	List<Map<String, Object>> getLINKCASESLISTForUpdate(String cIno);
	List<Map<String, Object>> getOBJECTIONSLISTForUpdate(String cIno);
	List<Map<String, Object>> getCASEHISTORYLISTForUpdate(String cIno);
	List<Map<String, Object>> getPETEXTRAPARTYLISTForUpdate(String cIno);
	List<Map<String, Object>> getRESEXTRAPARTYLISTForUpdate(String cIno);
	List<Map<String, Object>> getACTIVITIESDATAForUpdate(String cIno);
	List<Map<String, Object>> getDataStatus(String cIno);
	List<Map<String, Object>> getOLCMSCASEDATAForUpdate(String cIno);
	
	ResponseEntity<Map<String, Object>> getupdateCaseDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno );
	ResponseEntity<Map<String, Object>> getforwardCaseDetails(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request, String cIno);
	ResponseEntity<Map<String, Object>> getforwardCaseDetails2GP(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno );
	ResponseEntity<Map<String, Object>> getSendBackCaseDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno );
	ResponseEntity<Map<String, Object>> getGPApprove(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno );
	ResponseEntity<Map<String, Object>> getGPReject(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request, String cIno );
	
	String getassignMultiCases2SectionLegacy(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request );
	
	String getAssignToDeptHODSendBackLegacy(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request);
	List<Map<String, Object>> getEMPList(Authentication authentication);
	List<Map<String, Object>> getNoList(Authentication authentication);
	
	
	 

	
	
}
