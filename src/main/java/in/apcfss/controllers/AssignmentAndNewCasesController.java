package in.apcfss.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AssignmentAndNewCasesService;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "CaseSearchController - Access, Refresh", description = "Case Search Controller")
public class AssignmentAndNewCasesController {

	@Autowired
	AssignmentAndNewCasesService service;

	@Autowired
	UsersRepo repo;

	@GetMapping("/AssignmentAndNewCasesList")
	public Map<String, Object> AssignmentAndNewCasesList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		Map<String, Object> map = new HashMap<>();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		try {

			List<Map<String, Object>> data = service.getAssignmentAndNewCasesList(authentication,abstractReqBody);
			// System.out.println("data=" + data);
			if (data != null && !data.isEmpty() && data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).get("file_found") == null) {
						data.get(i).put("file_found", "No");
					}
				}
				map.put("CASEWISEACKS", data);
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Records Found");
			}else {
				map.put("sdesc", "No Records Found");
				map.put("status", false);
				map.put("scode", "02");
			}


			if (roleId.equals("2") || roleId.equals("10")) {
				map.put("login_type_dc", "login_type_dc");
			} else {
				map.put("login_type", "login_type");
			}

		}catch (Exception e) {
			e.printStackTrace();
		} 
		return map;
	}
	@PostMapping("AssignToDeptHOD")
	public Map<String, Object> getassign2DeptHOD(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("AssignToDeptHOD: ");
		Map<String, Object> map = new HashMap<>();

		String msg = service.getassign2DeptHOD(authentication,abstractReqBody);

		if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", msg);
		}else {
			map.put("sdesc", msg);
			map.put("status", false);
			map.put("scode", "02");
		}
		return map;
	}

	@PostMapping("AssignMultiCasesToSection")
	public Map<String, Object> AssignMultiCasesToSection(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("AssignToDeptHOD: ");
		Map<String, Object> map = new HashMap<>();

		String msg = service.getAssignMultiCasesToSection(authentication,abstractReqBody);

		if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error") ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", msg);
		}else {
			map.put("sdesc", msg);
			map.put("status", false);
			map.put("scode", "02");
		}
		return map;
	}
	@PostMapping("AssignToDistCollector")
	public Map<String, Object> AssignToDistCollector(Authentication authentication,@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {

		System.out.println("AssignToDistCollector: ");
		Map<String, Object> map = new HashMap<>();

		String msg = service.getAssignToDistCollector(authentication,abstractReqBody);
		System.out.println("-"+msg);
		if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error") ) {

			map.put("status", true);
			map.put("scode", "01");
			map.put("sdesc", msg);
		}else {
			map.put("sdesc", msg);
			map.put("status", false);
			map.put("scode", "02");
		}
		return map;
	}
	 
}
