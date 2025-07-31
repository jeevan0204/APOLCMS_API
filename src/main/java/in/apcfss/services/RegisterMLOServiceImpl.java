package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import in.apcfss.common.CommonModels;
import in.apcfss.controllers.CommonMethodsController;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.repositories.DistrictWiseFinalOrdersImplementationRegRepo;
import in.apcfss.repositories.RegisterMLORepo;
import in.apcfss.repositories.RegisterNodalOfficerRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class RegisterMLOServiceImpl implements RegisterMLOService {

	@Autowired
	JdbcTemplate jdbcTemplate;


	@Autowired
	RegisterMLORepo MLORepo;

	@Override
	public List<Map<String, Object>> getDesignationList(String deptCode) {
		List<Map<String, Object>> data =null;
		try {
			data=MLORepo.getDesignationList(deptCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	@Override
	public List<Map<String, Object>> getregisterMLOList(String deptCode) {
		List<Map<String, Object>> data =null;
		try {
			data=MLORepo.getregisterMLOList(deptCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	private String safeTrim(String value) {
	    return value != null ? value.trim() : "";
	}

	@Override
	public String saveMloDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBod,
			HttpServletRequest request){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sMs = null;
		 
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		int deptId = userPrincipal.getDeptId() != null ? userPrincipal.getDeptId()  : 0;

		int status = 0;
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String designationId = abstractReqBod.getDesignationId();
		String employeeId = abstractReqBod.getEmployeeId();
		String mobileNo = safeTrim(abstractReqBod.getMobileNo());
		String emailId = abstractReqBod.getEmailId();
		String aadharNo = safeTrim(abstractReqBod.getAadharNo());  

		System.out.println("mobileNo=aadharNo-----"+mobileNo+"----"+aadharNo);

		try {

			if ( mobileNo.trim().length() == 10 && aadharNo.trim().length() == 12) {
				designationId = designationId.trim();
				employeeId = employeeId.trim();
				mobileNo = mobileNo.trim();
				emailId = emailId.trim();
				aadharNo = aadharNo.trim();

				status=MLORepo.insert_mlo_details(deptCode,designationId,employeeId,mobileNo,emailId,aadharNo,userId,request.getRemoteAddr() );

				if (status == 1){
					status=MLORepo.insert_usersMLO(userId,request.getRemoteAddr(),deptId,deptCode,employeeId,designationId );

					status+=MLORepo.insert_user_rolesMLO(emailId);

					if(status==2) {

						String smsText="Your User Id is "+emailId+" and Password is olcms@123 to Login to https://apolcms.ap.gov.in/ Portal. Please do not share with anyone. \r\n-APOLCMS";
						String templateId="1007784197678878760";
						// mobileNo="9618048663";
						//sendSMSDao.sendSMS(mobileNo, smsText, templateId);
						sMs="Mid Level Officer (Legal) details Registered & Login Credentails created successfully. Login details sent to MLO Registered Mobile no.";
					}
					else {
						sMs="Error:Mid Level Officer (Legal) not Registered due to wrong data submitted or Already registered.";
					}
				}else {
					sMs="Error: Mid Level Officer (Legal) not Registered due to wrong data submitted or Already registered";
				}
			}else {
	            sMs = "Error: Mobile number must be 10 digits and Aadhar number must be 12 digits.";
	        } 

		} catch (Exception e) {
			e.printStackTrace();
			sMs = "Error: Invalid input or unexpected error occurred while saving MLO Details.";
		}
		return sMs;
	}
	@Override
	public String updateEmployeeDetailsMlo(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBod,
			HttpServletRequest request, String deptCode){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String  sMs = null;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		int status = 0;
		int a=0;
		String designationId = abstractReqBod.getDesignationId();
		String employeeId = abstractReqBod.getEmployeeId();
		String mobileNo = abstractReqBod.getMobileNo();
		String emailId = abstractReqBod.getEmailId();
		String aadharNo = abstractReqBod.getAadharNo();
		String mloId=abstractReqBod.getMloId();

		System.out.println("mobileNo=aadharNo-----"+mobileNo+"----"+aadharNo);

		try {

			if ( mobileNo.trim().length() == 10 && aadharNo.trim().length() == 12) {
				designationId = designationId.trim();
				employeeId = employeeId.trim();
				mobileNo = mobileNo.trim();
				emailId = emailId.trim();
				aadharNo = aadharNo.trim();
				mloId=mloId.trim();

				
				status=MLORepo.insert_mlo_details_bkpMLO(userId,request.getRemoteAddr(),mloId,employeeId );
				
				status=MLORepo.update_mlo_detailsMLO(mobileNo,emailId,aadharNo,userId ,request.getRemoteAddr(),mloId,employeeId);
				if (status > 1)
				{
					sMs="Mid Level Officer (Legal) details Registered & Login Credentails created successfully. Login details sent to MLO Registered Mobile no.";
					 
				}
				else
				{
					sMs="Error: Mid Level Officer (Legal) not Registered due to wrong data submitted or Mobile No. (or) email Id (or) Aadhaar has been already registered.";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sMs;
	}


}
