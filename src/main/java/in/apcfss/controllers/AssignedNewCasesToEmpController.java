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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.services.AssignedCasesToSectionService;
import in.apcfss.services.AssignedNewCasesToEmpService;
import in.apcfss.utils.CommonQueryAPIUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Tag(name = "Assigned Cases To Section Controller - Access, Refresh", description = "Assigned Cases To Section")
public class AssignedNewCasesToEmpController {

	@Autowired
	AssignedNewCasesToEmpService service;

	@GetMapping("/AssignedNewCasesToEmp")
	public Map<String, Object> AssignedNewCasesToEmp(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {

		System.out.println("AssignedCasesToSection: ");
		Map<String, Object> map = new HashMap<>();
		try {
			List<Map<String, Object>> data = service.getAssignedNewCasesToEmp(authentication, abstractReqBody);

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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@GetMapping("/AckNoPopupView")
	public Map<String, Object> getAckNo(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam("cino") String cino,
			@RequestParam("SHOWPOPUP") String SHOWPOPUP ) {

		System.out.println("AckNoPopupView: ");
		Map<String, Object> map = new HashMap<>();
		String cIno="";
		try {

			String viewDisplay = SHOWPOPUP + "";

			/*
			 * if (!viewDisplay.equals("") && viewDisplay.equals("SHOWPOPUP")) { cIno =
			 * cino;
			 * 
			 * } else { cIno = abstractReqBody.getFileCino(); }
			 */

			if (!viewDisplay.equals("") && viewDisplay.equals("SHOWPOPUP")) {
				cIno = cino;
			} else {
				cIno = abstractReqBody.getFileCino();
				System.out.println("cino----------" + cIno);
			}

			System.out.println("cIno---"+cIno);

			if (cIno != null && !cIno.equals("")) {

				List<Map<String, Object>> data = service.getÙsersList(cIno);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("USERSLIST", data);

				}
				List<Map<String, Object>> data1 = service.getRespodentList(cIno);
				if (data1 != null && !data1.isEmpty() && data1.size() > 0) {
					map.put("Respodent", data1);
				}
				List<Map<String, Object>> Otherdata = service.getOtherRespodentList(cIno);
				if (Otherdata != null && !Otherdata.isEmpty() && Otherdata.size() > 0) {
					map.put("OtherRespodent", Otherdata);
				}

				List<Map<String, Object>> ActivitiesData = service.getActivitiesData(cIno);
				if (ActivitiesData != null && !ActivitiesData.isEmpty() && ActivitiesData.size() > 0) {

					map.put("ACTIVITIESDATA", ActivitiesData);
				}

				map.put("HEADING", "Case Details for ACK NO : " + cIno);
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Records Found");
			} else {
				map.put("sdesc", "Invalid ACK NO.");
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return map;
	}

	@GetMapping("/caseStatusUpdateNew")
	public Map<String, Object> caseStatusUpdateNew(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,@RequestParam("ackNo") String ackNo) {

		Map<String, Object> map = new HashMap<>();

		String cIno1 = ackNo;
		String cInoAll[] = cIno1.split("@");
		String cIno = cInoAll[0];
		try {

			Map<String, Object> data = service.getCaseStatusUpdate(authentication,abstractReqBody,ackNo);

			System.out.println("---"+data);
			if (data != null && !data.isEmpty()) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", "Data found");
				map.put("data", data);
			}else {
				map.put("sdesc", "Data Not found");
				map.put("status", false);
				map.put("scode", "02");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			abstractReqBody.setFileCino(cIno);
			abstractReqBody.setResident_id(Integer.parseInt(cInoAll[1]));
		}
		return map;

	}


	@PostMapping("/updateCaseDetailsNew")
	public Map<String, Object> updateCaseDetailsNew(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody ,HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();
		String cIno1[] = abstractReqBody.getFileCino().split("@");

		String cIno = cIno1[0];

		try {
			if (cIno != null && !cIno.isEmpty()) {

				String msg = service.getUpdateCaseDetails(authentication,
						abstractReqBody,request);

				if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
					map.put("status", true);
					map.put("scode", "01");
					map.put("sdesc", msg);
				}else {
					map.put("sdesc", msg);
					map.put("status", false);
					map.put("scode", "02");
				}
			}else {
				map.put("sdesc", "Invalid Cino");
				map.put("status", false);
				map.put("scode", "02");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			abstractReqBody.setFileCino(cIno);
			abstractReqBody.setResident_id(Integer.parseInt(cIno1[1]));
		}
		return map;
	}
	@PostMapping("/forwardCaseDetailsNew")
	public Map<String, Object> getforwardCaseDetailsNew(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		// String cIno = CommonModels.checkStringObject(abstractReqBody.getFileCino());

		try {

			String msg = service.getforwardCaseDetailsNew(authentication,
					abstractReqBody,request);

			if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", msg);
			}else {
				map.put("sdesc", msg);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			abstractReqBody.setFileCino(abstractReqBody.getFileCino());
			abstractReqBody.setResident_id(abstractReqBody.getResident_id());
		}
		return map;
	}


	@PostMapping("/sendBackCaseDetailsNew")
	public Map<String, Object> sendBackCaseDetailsNew(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();

		try {

			String msg = service.getsendBackCaseDetailsNew(authentication,
					abstractReqBody,request);

			if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", msg);
			}else {
				map.put("sdesc", msg);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}  
		return map;
	}

	@PostMapping("/forwardCaseDetails2GPNew")
	public Map<String, Object> forwardCaseDetails2GPNew(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();

		try {
			String msg = service.getforwardCaseDetails2GPNew(authentication,
					abstractReqBody,request);

			if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", msg);
			}else {
				map.put("sdesc", msg);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}  
		return map;
	}

	@PostMapping("/gpApproveNew")
	public Map<String, Object> gpApproveNew(Authentication authentication,
			@RequestBody HCCaseStatusAbstractReqBody abstractReqBody, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();

		try {
			String msg = service.getGpApproveNew(authentication,
					abstractReqBody,request);

			if (msg != null && !msg.isEmpty() && !msg.toLowerCase().contains("error")) {
				map.put("status", true);
				map.put("scode", "01");
				map.put("sdesc", msg);
			}else {
				map.put("sdesc", msg);
				map.put("status", false);
				map.put("scode", "02");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}  
		return map;
	}


}
