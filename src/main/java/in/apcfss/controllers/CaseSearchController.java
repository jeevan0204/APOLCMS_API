package in.apcfss.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.CaseSearchService;
import in.apcfss.services.LegacyCasesAssignmentService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "CaseSearchController - Access, Refresh", description = "Case Search Controller")
public class CaseSearchController {

	@Autowired
	UsersRepo repo;

	@Autowired
	CaseSearchService service;

	CommonMethodsController commonMethodController;

	@GetMapping("/SearchCase")
	public List<Map<String, Object>> SearchCase(Authentication authentication ) {

		List<Map<String, Object>> responseList = service.getCaseTypesList(authentication);
		return responseList;
	}

	@GetMapping("/getCasesListData")
	public Map<String, Object> getCasesListData(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		Map<String, Object> map = new HashMap<>();
		GPOAckDetailsReqBody reqbody=new GPOAckDetailsReqBody();
		Boolean STR=false;
		try {
			String cinNo = service.getCasesList(authentication,abstractReqBody);

			List<Map<String, Object>> crcasetype  = service.getCasetype();
			
			System.out.println("crcasetype----"+crcasetype);

			String caseType = abstractReqBody.getCaseType() + "";

			//caseType = (caseType != null && !caseType.trim().isEmpty()) ? caseType.trim() : null;
			//map.put("caseType", caseType);
			System.out.println("caseType----"+caseType);
			
			for(int i=0; i< crcasetype.size(); i++) {

				//System.out.println("------"+crcasetype.get(i).get("case_type"));
				
				if(caseType.equals(crcasetype.get(i).get("case_type"))==true) {

					STR=true;
				}
			}
			System.out.println("STR-----"+STR);
			
			if (cinNo != null && !cinNo.equals("") && !cinNo.equals("0") && !StringUtils.isBlank(cinNo) ) {

				String barCodeFilePath = service.generateAckBarCodePdf128(cinNo, reqbody);
				int a = 0;
				if (barCodeFilePath != null) {

					a = service.getUpdateBarcodeFile(authentication,abstractReqBody);
				}
				if (a > 0) {

					List<Map<String, Object>> data = service.getCasesListData(authentication,abstractReqBody);
					// System.out.println("data=" + data);
					if (data != null && !data.isEmpty() && data.size() > 0) {
						for (int i = 0; i < data.size(); i++) {
							if (data.get(i).get("is_scandocs_exists") == null) {
								data.get(i).put("is_scandocs_exists", "false");
							}
						}
						map.put("CASESLIST", data);
						map.put("status", true);
						map.put("scode", "01");
					}else {
						map.put("errorMsg", "Records Found but failed to fetch data.");
						map.put("status", false);
						map.put("scode", "02");
					}
				}
				else {
					map.put("errorMsg", "Records Found but failed to generate Bar code.");
				}
			}


			else if( cinNo.equals("0") && STR==true ){

				map.put("ackType", "OLD");

				map.put("errorMsg",
						"No Records Found in APOLCMS. Enter the below details to generate case details for criminal.");
				// return ppoAcknowledgementController.displayAckForm(authentication,abstractReqBody);
			} 
			else {

				map.put("ackType", "OLD");
				map.put("errorMsg",
						"No Records Found in APOLCMS. Enter the below details to generate case details.");
				//return GPOAcknowledgementController.displayAckForm(authentication,abstractReqBody);
			}

		}catch (EmptyResultDataAccessException e) {
			e.getMessage();
			System.out.println("empty cino---");
		} finally {
			map.put("saveAction", "INSERT");
			map.put("caseTypesList", service.getCaseTypesList(authentication));

		}
		return map;
	}

}
