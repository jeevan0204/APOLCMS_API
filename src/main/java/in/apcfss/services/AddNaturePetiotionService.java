package in.apcfss.services;

import java.util.Map;

import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

public interface AddNaturePetiotionService {
 

	Map<String, Object> getSaveDetails(HCCaseStatusAbstractReqBody abstractReqBody);

	Map<String, Object> saveAddAdvocatecceDetails(HCCaseStatusAbstractReqBody abstractReqBody);
}
