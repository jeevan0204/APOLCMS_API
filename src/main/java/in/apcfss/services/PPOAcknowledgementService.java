package in.apcfss.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;

public interface PPOAcknowledgementService {

	ResponseEntity<Map<String, Object>> savePPOAckDetails(Authentication authentication,GPOAckDetailsReqBody reqbody, HttpServletRequest request);
 
}
