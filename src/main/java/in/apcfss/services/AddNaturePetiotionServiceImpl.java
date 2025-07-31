package in.apcfss.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.GPOAcknowledgementDetailsRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class AddNaturePetiotionServiceImpl implements AddNaturePetiotionService {

	@Autowired
	GPOAcknowledgementDetailsRepo GPOAckRepo;

	@Override
	public Map<String, Object> getSaveDetails(HCCaseStatusAbstractReqBody abstractReqBody) { ///All DC,DCNO ASSIGNMENT
		String caseFullName = "";
		String msg = "faild",sql="";
		Map<String, Object> map = new HashMap<>();
		try {
			int slno = 0;

			caseFullName = CommonModels.checkStringObject(abstractReqBody.getCaseFullName());

			Integer ex_adv_code_count=GPOAckRepo.CaseTypeExist(caseFullName);

			if (ex_adv_code_count == 0) {

				slno=GPOAckRepo.CaseTypeAdd();

				System.out.println("slno======>"+slno);

				if (caseFullName != null && !caseFullName.isEmpty()) {

					int a=GPOAckRepo.INSERTCaseType(slno,caseFullName,caseFullName);

					System.out.println("a-------------->"+a);
					if (a > 0) {
						map.put("status", true);
						map.put("scode", "01");
						map.put("sdesc", "Nature Petition Master (Case Type) saved successfully");

					} else {
						map.put("status", false);
						map.put("scode", "02");
						map.put("sdesc","Invalid Nature Petition Master (Case Type). Kindly try again.");
					}
				} else {
				map.put("status", false);
				map.put("scode", "02");
				map.put("sdesc","Error in saving Invalid Nature Petition Master (Case Type) Details. Kindly try again with valid data.");
				map.put("saveAction", "INSERT");
				}
				
			} else {
				map.put("status", false);
				map.put("scode", "02");
				map.put("sdesc", "Entered Case Full Name already exists.Kindly try again.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	@Override
	public Map<String, Object> saveAddAdvocatecceDetails(HCCaseStatusAbstractReqBody abstractReqBody) { ///All DC,DCNO ASSIGNMENT
		String advocateName="";
		Map<String, Object> map = new HashMap<>();
		int advocateCode = 0;
		try {
			int slno = 0;
			advocateCode = CommonModels.checkIntObject(abstractReqBody.getAdvocateCode());
			advocateName = CommonModels.checkStringObject(abstractReqBody.getAdvocateName());

			int ex_adv_code_count = GPOAckRepo.AdvocateCCCODE(advocateCode); 

			if (ex_adv_code_count == 0) {

				slno =GPOAckRepo.AdvocateCCCODECount();  

			  System.out.println("slno======>"+slno);

				if (advocateCode != 0 && advocateName != null) {

					int a = GPOAckRepo.insert_advocate_ccs(slno,advocateCode,advocateName);  
					System.out.println("a--" + a);
					if (a > 0) {
						map.put("status", true);
						map.put("scode", "01");
						map.put("sdesc", "Advocate cce details saved successfully");
					} else {
						map.put("status", false);
						map.put("scode", "02");
						map.put("sdesc", "Invalid Advocate cce details. Kindly try again.");
					}
				} else {
					map.put("status", false);
					map.put("scode", "02");
					map.put("sdesc","Error in saving Invalid Advocate cce details. Kindly try again with valid data.");
				map.put("saveAction", "INSERT");
				}
			} else {
				map.put("status", false);
				map.put("scode", "02");
				map.put("sdesc", "Your Advocate Code already exists.Kindly try again.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
