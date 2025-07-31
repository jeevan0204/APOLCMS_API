package in.apcfss.controllers;

import java.time.Year;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.services.CommonMethodService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "CommonMethod Services - Access, Refresh", description = "Common Method Controller")
@SecurityRequirement(name = "bearerAuth")
public class CommonMethodsController {

	@Autowired
	UsersRepo usersRepo;
	
	@Autowired
	CommonMethodService service;

	public String getTableName(String distId) {
		String tableName = "nic_data";

		// Trim to remove leading and trailing spaces
		if (distId != null && !distId.trim().isEmpty() && !distId.trim().equals("undefined")) {
			try {
				int distIdInt = Integer.parseInt(distId.trim());
				if (distIdInt > 0) {
					tableName = usersRepo.getTableName(distIdInt);
					System.out.println("dist::Id " + distIdInt + " - tableName:: " + tableName);
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid distId format: " + distId);
			}
		}

		return tableName;
	}

	@GetMapping("getDepartmentList")
	public List<Map<String, Object>> getDepartmentList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		
		System.out.println("deptCode--"+deptCode+"--roleId-----"+roleId);
		List<Map<String, Object>> responseList=null;
		if (roleId.equals("2")) {
			 responseList =usersRepo.getDistrictList();
		}  
		if (roleId.equals("1") || roleId.equals("7")) {
			responseList =usersRepo.getDepartmentList();
		} else if (roleId.equals("2") || roleId.equals("10")) {

			responseList =usersRepo.getDepartmentList1();
		} else {
			responseList =usersRepo.getDepartmentList2(deptCode);
		}

		return responseList;
	}
	

	@GetMapping("getDistrictList")
	public List<Map<String, Object>> getDistrictList() {
		return usersRepo.getDistrictList();
	}

	@GetMapping("getMandalList")
	public List<Map<String, Object>> getMandalList(@RequestParam String empDept, @RequestParam String distCode,
			@RequestParam String empSec) {
		String tableName = getTableName(distCode);
		return service.getMandalList(empDept, distCode, tableName, empSec);
	}

	@GetMapping("getVillageList")
	public List<Map<String, Object>> getVillageList(@RequestParam String empDept, @RequestParam String distCode,
			@RequestParam String empSec) {
		String tableName = getTableName(distCode);
		return service.getVillageList(empDept, distCode, tableName, empSec);
	}

	@GetMapping("getDesignationList")
	public List<Map<String, Object>> getDesignationList() {
		return usersRepo.getDesignationList();
	}

	@GetMapping("getGPsList")
	public List<Map<String, Object>> getGPsList() {
		return usersRepo.getGPsList();
	}

	@GetMapping("getCaseTypesList")
	public List<Map<String, Object>> getCaseTypesList() {
		return usersRepo.getCaseTypesList();
	}
	
	@GetMapping("getCaseTypesListOldTableWithSNO")
	public List<Map<String, Object>> getCaseTypesListOldTableWithSNO() {
	    return usersRepo.getCaseTypesListOldTableWithSNO();
	}

	@GetMapping("getServiceTypesList")
	public List<Map<String, Object>> getServiceTypesList() {
		return usersRepo.getServiceTypesList();
	}

	@GetMapping("getCaseTypesListShrt")
	public List<Map<String, Object>> getCaseTypesListShrt() {
		return usersRepo.getCaseTypesListShrt();
	}

	@GetMapping("getOtherdistList")
	public List<Map<String, Object>> getOtherdistList() {
		return usersRepo.getOtherdistList();
	}

	@GetMapping("getOtherDeptList")
	public List<Map<String, Object>> getOtherDeptList() {
		return usersRepo.getOtherDeptList();
	}

	@GetMapping("getYearsList")
	public List<Map<String, String>> getYearsList() {
	    int startYear = 1980;
	    int currentYear = Year.now().getValue();

	    return IntStream.iterate(currentYear, year -> year >= startYear, year -> year - 1)
	            .mapToObj(year -> {
	                Map<String, String> yearMap = new HashMap<>();
	                yearMap.put("value", String.valueOf(year));
	                yearMap.put("label", String.valueOf(year));
	                return yearMap;
	            })
	            .collect(Collectors.toList());
	}
	
	@GetMapping("getCategoryServiceList")
	public List<Map<String, Object>> getCategoryServiceList() {
		return usersRepo.getCategoryServiceList();
	}
	
	@GetMapping("getEmpDeptSectionsList")
	public List<Map<String, Object>> getEmpDeptSectionsList(@RequestParam String distCode,
			@RequestParam String deptCode) {
		String tableName = getTableName(distCode);
		System.out.println("---tablename: " + tableName + "dist---" + distCode + "dept---" + deptCode);

		return service.getEmpSectionsList(distCode, deptCode, tableName);
	}

	@GetMapping("getEmpPostsList")
	public List<Map<String, Object>> getEmpPostsList(@RequestParam String distCode, @RequestParam String deptCode,
			@RequestParam String empSec) {
		String tableName = getTableName(distCode);
		return service.getEmpPostsList(distCode, deptCode, tableName, empSec);
	}

	@GetMapping("getEmpsList")
	public List<Map<String, Object>> getEmpsList(@RequestParam String distCode, @RequestParam String deptCode,
			@RequestParam String empSec, @RequestParam String empPost) {
		String tableName = getTableName(distCode);
		return service.getEmpsList(distCode, deptCode, tableName, empSec, empPost);
	}

	@GetMapping("getDeptbasedSectionList")
	public List<Map<String, Object>> getDeptbasedSectionList(@RequestParam String selectSection) {
		List<Map<String, Object>> list = null;
		if (selectSection.equals("Sec-Section")) {
			list = usersRepo.getSecDeptNames();

		} else {
			list = usersRepo.getHodDeptNames();
		}
		return list;
	}

	@GetMapping("getEmpPostsListWithMandal")
	public List<Map<String, Object>> getEmpPostsListWithMandal(@RequestParam String empDept,
			@RequestParam String distCode, @RequestParam String empSec, @RequestParam String mndlCode) {
		String tableName = getTableName(distCode);
		return service.getEmpPostsListWithMandal(empDept, distCode, tableName, empSec, mndlCode);
	}

	@GetMapping("getEmpPostsListWithMandalandVill")
	public List<Map<String, Object>> getEmpPostsListWithMandalandVill(@RequestParam String empDept,
			@RequestParam String distCode, @RequestParam String empSec, @RequestParam String mndlCode,
			@RequestParam String village) {
		String tableName = getTableName(distCode);
		return service.getEmpPostsListWithMandalandVill(empDept, distCode, tableName, empSec, mndlCode, village);
	}

	@GetMapping("getEmpsEmailsListWithMandal")
	public List<Map<String, Object>> getEmpsEmailsListWithMandal(@RequestParam String empDept,
			@RequestParam String distCode, @RequestParam String empSec, @RequestParam String empPost,
			@RequestParam String mndlCode) {

		String tableName = getTableName(distCode);

		return service.getEmpsEmailsListWithMandal(tableName, empDept, distCode, empSec, empPost, mndlCode);
	}

	@GetMapping("getEmpsEmailsListWithMandalAndVill")
	public List<Map<String, Object>> getEmpsEmailsListWithMandalAndVill(@RequestParam String empDept, @RequestParam String distCode,
			@RequestParam String empSec, @RequestParam String empPost, @RequestParam String mndlCode, String vlgCode) {
		
		String tableName = getTableName(distCode);
		
		return service.getEmpsEmailsListWithMandalAndVill(tableName, empDept, distCode, empSec, empPost, mndlCode,vlgCode);
	}
	
	@GetMapping("/getEmpsEmailsList")
	public List<Map<String, Object>> getEmpsEmailsList(@RequestParam String empDept, @RequestParam String distCode,
			@RequestParam String empSec, @RequestParam String empPost) {
		
		String tableName = getTableName(distCode);

		return service.getEmpsEmailsList(tableName,empDept,distCode,empSec,empPost);
	}
	
	@GetMapping("getJudgesList")
	public List<Map<String, Object>> getJudgesList() {
		return usersRepo.getJudgesList();
	}
	
	@GetMapping("/getPurposeList")
	public List<Map<String, Object>> getPurposeList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return usersRepo.getPurposeList(userPrincipal.getDeptCode());
	}
	
	@GetMapping("/empSectionList")
	public List<Map<String, Object>> empSectionList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return usersRepo.empSectionList(userPrincipal.getDeptCode());
	}
	
	@GetMapping("/getMLOSUBLIST")
	public List<Map<String, Object>> getMLOSUBLIST(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return usersRepo.getMLOSUBLIST(userPrincipal.getDeptCode());
	}
	
	@GetMapping("getEmployeesList")
	public List<Map<String, Object>> getEmployeesList(Authentication authentication, @RequestParam String deptId,@RequestParam String designationId) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId=userPrincipal.getRoleId();
		String tableName = getTableName(String.valueOf(distId));
		return service.getEmployeesList( deptId,designationId,tableName,roleId);
	}
	
	@GetMapping("getEmpDetails")
	public List<Map<String, Object>> getEmpDetails(Authentication authentication, @RequestParam String empId,@RequestParam String designationId) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String tableName = getTableName(String.valueOf(distId));
		return service.getEmpDetails( empId,designationId,tableName );
	}


}
