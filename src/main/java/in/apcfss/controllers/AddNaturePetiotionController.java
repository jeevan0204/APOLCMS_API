package in.apcfss.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AddNaturePetiotionService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "AddNaturePetiotionController - Access, Refresh", description = "Add Nature Petiotion Controller")
public class AddNaturePetiotionController {

	@Autowired
	AddNaturePetiotionService service;
	
	@PostMapping("NaturePetitionMst")
	public Map<String, Object> getSaveDetails(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {
		 
		return service.getSaveDetails(abstractReqBody);
	}

	@PostMapping("saveAddAdvocatecceDetails")
	public Map<String, Object> saveAddAdvocatecceDetails(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {
			 
		return service.saveAddAdvocatecceDetails(abstractReqBody);
	}
}
