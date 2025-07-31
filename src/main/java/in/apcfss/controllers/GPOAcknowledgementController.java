package in.apcfss.controllers;

import org.springframework.web.bind.annotation.RestController;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.GPOAckDeptsEntity;
import in.apcfss.entities.GPOAckGen;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.GPOAcknowledgementService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "GPO Acknowledgement Services - Access, Refresh", description = "GPO Acknowledgement Controller")
@SecurityRequirement(name = "bearerAuth")
public class GPOAcknowledgementController {

	@Autowired
	GPOAcknowledgementService gpoAckService;

	@Autowired
	UsersRepo usersRepo;


	@PostMapping("SaveGPOAckForm")
	public ResponseEntity<Map<String, Object>> postGPOAckForm(Authentication authentication,@RequestBody GPOAckDetailsReqBody dto,HttpServletRequest request) {
		System.out.println("Received Data:");
		System.out.println("District ID: " + dto.getDistId());
		System.out.println("Petitioner Name: " + dto.getPetitionerName());
		System.out.println("Advocate CC No: " + dto.getAdvocateCCno());
		System.out.println("Case Category: " + dto.getCaseCategory());
		System.out.println("Case Type: " + dto.getCaseType());
		System.out.println("Filing Mode: " + dto.getFilingMode());
		System.out.println("Case Type 1: " + dto.getCaseType1());
		System.out.println("Registration Year: " + dto.getRegYear1());
		System.out.println("Remarks: " + dto.getRemarks());

		System.out.println("\nGpOackForm List:");
		for (GPOAckGen form : dto.getGpOackForm()){
			System.out.println("--------------------------------");

			System.out.println("Department ID: " + form.getDepartmentId());
			System.out.println("Display Dept: " + form.getDispalyDept());
			System.out.println("Display Dist: " + form.getDispalyDist());
			System.out.println("Section Selection: " + form.getSectionSelection());
			System.out.println("Emp Dept: " + form.getEmpDept());
			System.out.println("Emp Section: " + form.getEmpSection());
			System.out.println("Emp Post: " + form.getEmpPost());
			System.out.println("Employee ID: " + form.getEmployeeId());
			System.out.println("Other Dist: " + form.getOtherDist());
			System.out.println("Mandal: " + form.getMandal());
			System.out.println("Village: " + form.getVillage());
			System.out.println("Service Type: " + form.getServiceType());
		}

		return gpoAckService.saveGPOAckForm(authentication,dto,request);
		//return "ok";
	}

	@GetMapping("loadMainCaseNoDetails")
	public ResponseEntity<?> getLoadMainCaseNoDetails(@RequestParam String caseTypeCode)
	{
		System.out.println("caseTypeCode----: "+caseTypeCode);
		int count=usersRepo.isExitsMainCaseNo(caseTypeCode);
		if(count > 0)
		{
			return CommonQueryAPIUtils.manualResponse("01","Case details found in APOLCMS.");
		}
		else {
			return CommonQueryAPIUtils.manualResponse("02", "Case details not found in APOLCMS.");
		}
	}

	@GetMapping("loadAdvocateName")
	public String loadAdvocateName(@RequestParam String advocate_code)
	{
		System.out.println("advocate_code----: "+advocate_code);
		String advName=usersRepo.getAdvName(Integer.parseInt(advocate_code));
		return advName;


	}

	@GetMapping("getAcknowledementsList")
	public Map<String, Object> getAcknowledementsList(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println("getAcknowledementsList-------" + userPrincipal.getUserId());

		Map<String, Object> response = new HashMap<>();
		try {
			//response = CommonQueryAPIUtils.apiService("ACKDATA",gpoAckService.getAcknowledementsList(userPrincipal));
			System.out.println("response-------" + response);
			 
			List<Map<String, Object>> data=gpoAckService.getAcknowledementsList(userPrincipal);
			//response = CommonQueryAPIUtils.apiService("ACKDATA",gpoAckService.gpoAcknowledementsListAll(userPrincipal));
			 if (data.size() > 0) {
		            response.put("status", true);
		            response.put("scode", "01");
		            response.put("data", data);
		            response.put("sdesc", "Data Found");
		        } else {
		            response.put("status", false);
		            response.put("scode", "02");
		            response.put("sdesc", "No data Found ");
		        }
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("GPOAcknowledementsListAll")
	public Map<String, Object> gpoAcknowledementsListAll(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam String ackDate) {

		Map<String, Object> response = new HashMap<>();
		try {
			String ackType = CommonModels.checkStringObject(abstractReqBody.getAckType());
			
			List<Map<String, Object>> data=gpoAckService.gpoAcknowledementsListAll(authentication,abstractReqBody,ackDate,ackType);
			
			 if (data.size() > 0) {
		            response.put("status", true);
		            response.put("scode", "01");
		            
		            response.put("ACKDATA", data);
		            response.put("sdesc", "data found");
		            response.put("HEADING", "Acknowledgements Generated on Dt.:" + ackDate);
		            
		            if (ackType.equals("OLD")) {
		            	response.put("DISPLAYOLD", "DISPLAYOLD");
					}
		        } else {
		            response.put("status", false);
		            response.put("scode", "02");
		            response.put("sdesc", "No data found ");
		        }
			  
					

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	
	@GetMapping("ExistingCaseDetails")
	public Map<String, Object> ExistingCaseDetails(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam String ackDate) {

		Map<String, Object> response = new HashMap<>();
		try {
			String ackType = CommonModels.checkStringObject(abstractReqBody.getAckType());
			
			List<Map<String, Object>> data=gpoAckService.getExistingCaseDetails(authentication,abstractReqBody,ackDate,ackType);
			
			 if (data.size() > 0) {
		            response.put("status", true);
		            response.put("scode", "01");
		            
		            response.put("ACKDATA", data);
		            response.put("sdesc", "data found");
		            response.put("HEADING", "Acknowledgements Generated on Dt.:" + ackDate);
		            
		            if (ackType.equals("OLD")) {
		            	response.put("DISPLAYOLD", "DISPLAYOLD");
					}
		        } else {
		            response.put("status", false);
		            response.put("scode", "02");
		            response.put("sdesc", "No data found ");
		        }
			  
					

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("GPOAck")
	public Map<String, Object> displayAckForm(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (abstractReqBody.getAckType().equals("") || abstractReqBody.getAckType().equals("0")) {
				abstractReqBody.setAckType("NEW");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.put("saveAction", "INSERT");

		}

		return response;
	}
	@GetMapping("displayAckEditForm")
	public Map<String, Object> displayAckEditForm(Authentication authentication,@RequestParam("ackNo") String ackNo) {
		Map<String, Object> response = new HashMap<>();
		HCCaseStatusAbstractReqBody abstractReqBody = new HCCaseStatusAbstractReqBody();
		try {
			System.out.println("Controller ack no" + ackNo);
			response.put("ackId", ackNo);
			if (ackNo != null && !ackNo.contentEquals("")) {
				List<Map<String, Object>> data = gpoAckService.getDisplayAckEditFormList(authentication, ackNo );
				System.out.println("data======" + data);
				if (data != null && !data.isEmpty() && data.size() > 0) {

					abstractReqBody.setAckNo(data.get(0).get("ackNo")+"");
					abstractReqBody.setDistId(data.get(0).get("distid")+"");

					System.out.println("district id==" + data.get(0).get("distid"));
					System.out.println("+++++" + data.get(0).get("distid"));

					abstractReqBody.setPetitionerName(data.get(0).get("petitioner_name")+"");
					abstractReqBody.setAdvocateCCno( data.get(0).get("advocateccno")+"");
					abstractReqBody.setAdvocateName(data.get(0).get("advocatename")+"");
					abstractReqBody.setCaseCategory(data.get(0).get("case_category")+"");
					abstractReqBody.setCaseType(data.get(0).get("nature_of_petition")+"");

					abstractReqBody.setFilingMode(data.get(0).get("mode_filing")+"");
					abstractReqBody.setRegYear(data.get(0).get("reg_year")+"");
					abstractReqBody.setRegNo(data.get(0).get("reg_no")+"");
					abstractReqBody.setCaseType1(data.get(0).get("main_case_type")+"");
					abstractReqBody.setRegYear1(data.get(0).get("main_case_year")+"");
					response.put("regYear1", abstractReqBody.getRegYear1());

					System.out.println("main_case_year==========" + data.get(0).get("main_case_year"));

					abstractReqBody.setMainCaseNo(data.get(0).get("main_case_no")+"");
					abstractReqBody.setRemarks(data.get(0).get("remarks")+"");


					//response.put("Status", true);
					//response.put("scode", "01");
					List<Map<String, Object>> data2 = gpoAckService.getData2(ackNo);
					System.out.println("data2====>" + data2);

					List<ServiceAssignment> assignments = new ArrayList<>();

					for (Map<String, Object> row : data2) {
						ServiceAssignment a = new ServiceAssignment();
						a.setDepartmentId(row.get("dept_distcoll")+"");
						a.setDeptId(row.get("dept_code") + "");
						a.setDeptCategory(row.get("dept_category")+"");
						a.setDistId(row.get("dist_id")+"");
						a.setServiceType(row.get("servicetpye")+"");
						assignments.add(a);
					}

					abstractReqBody.setAssignments(assignments);


					if(data!=null && data2!=null) {
						response.put("status", true);
						response.put("scode", "01");
						response.put("data",data);
						response.put("data2",data2);
					}else {
						response.put("status", false);
						response.put("scode", "02");
						response.put("data","No data found");

					}


				}else {
					return displayAckForm(authentication,abstractReqBody);
				}

			} else {
				response.put("errorMsg", "Invalid Acknowledgement No. Kindly try again.");
				return getAcknowledementsList(authentication);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.put("saveAction", "UPDATE");

			response.put("acknum", abstractReqBody.getAckNo());

		}

		return response;
	}
	@PostMapping("/updateAckDetails")
	public Map<String, Object> getUpdateAckDetails(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();

		String message =gpoAckService.getUpdateAckDetails(authentication,abstractReqBody,request);

		if(message!=null  ) {
			response.put("status", true);
			response.put("scode", "01");
			response.put("sdesc",message);
		}else {
			response.put("status", false);
			response.put("scode", "02");
			response.put("sdesc",message);

		}

		//return getAcknowledementsList(authentication);
		return response;
	}

	@DeleteMapping("/deleteAckDetails")
	public Map<String, Object> deleteAckDetails(Authentication authentication, @RequestParam String ackNo) {
	    Map<String, Object> response = new HashMap<>();
	    //String ackNo = abstractReqBody.getAckNo()!= null ? abstractReqBody.getAckNo().toString() : "";

	    if (!ackNo.isEmpty()) {
	        int result = gpoAckService.getDeleteAckDetails(authentication, ackNo);
	        System.out.println("Deleted record count: " + result);

	        if (result > 0) {
	            response.put("status", true);
	            response.put("scode", "01");
	            response.put("successMsg", "Ack No.: " + ackNo + " deleted successfully.");
	        } else {
	            response.put("status", false);
	            response.put("scode", "02");
	            response.put("errorMsg", "Error in deleting Ack No.: " + ackNo);
	        }
	    } else {
	        response.put("status", false);
	        response.put("scode", "03");
	        response.put("errorMsg", "Invalid Acknowledgement No. Kindly try again.");
	    }

	    return response;
	}

}
