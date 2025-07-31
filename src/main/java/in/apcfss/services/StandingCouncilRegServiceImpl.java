package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.StandingCouncilRegRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class StandingCouncilRegServiceImpl implements StandingCouncilRegService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Autowired
	StandingCouncilRegRepo stadingRepo;

	@Override
	public List<Map<String, Object>> getStandingCounsel(Authentication authentication ) {
		List<Map<String, Object>> response = null;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println("dept code: " + userPrincipal.getDeptCode());
		try {

			response = stadingRepo.selectStanding_council(userPrincipal.getDeptCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String saveStandingCounselEmployee(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request ) {

		String dept_code=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String dept_name = repo.getDeptName(dept_code);
		String msg="",sql="";

		int count=0;
		try {
			System.out.println("description" + dept_name);

			String employeeName = abstractReqBody.getEmployeeName()+ "";
			String employeeCode = abstractReqBody.getEmployeeCode() + "";

			String fromDate = abstractReqBody.getFromDate() + "";
			String toDate = abstractReqBody.getToDate()+ "";

			String mobileNo = abstractReqBody.getMobileNo()+ "";

			String emailId = abstractReqBody.getEmailId()+ "";
			String aadharNo = abstractReqBody.getAadharNo()+ "";
			String remarks = abstractReqBody.getRemarks() + "";
			String deptId = abstractReqBody.getDeptId()+ "";

			String letterPath = abstractReqBody.getChangeLetter(); 

			System.out.println("change latter path---" + letterPath);
			System.out.println("employeeName:" + employeeName);
			System.out.println("employeeCode:" + employeeCode); 
			System.out.println("mobileNo:" + mobileNo);
			System.out.println("emailId:" + emailId);
			System.out.println("aadharNo:" + aadharNo);
 
			//if (employeeName.isEmpty() && employeeCode.isEmpty() && fromDate.isEmpty()  && toDate.isEmpty() && mobileNo.isEmpty() && emailId.isEmpty()&& aadharNo.isEmpty() && remarks.isEmpty() ) {
				Integer slno = jdbcTemplate.queryForObject(" select nextval('apolcms.standard_council_mst_seq')",Integer.class);
				count=stadingRepo.INSERTStanding_councilEMP_DATA(slno,employeeName,employeeCode, fromDate, toDate,
						mobileNo,emailId,aadharNo,letterPath,remarks,new Timestamp(new Date().getTime()) ,userId, request.getRemoteAddr(),deptId   );


				Integer GpSlno = jdbcTemplate.queryForObject(" select nextval('apolcms.ecourts_gps_latest_slno_seq')",
						Integer.class);
				count+=stadingRepo.INSERT_mst_gps(GpSlno,employeeName,mobileNo,dept_name,emailId);
				System.out.println("Gp Insertion Sql:" + count);


				count+=stadingRepo.INSERT_users(emailId,employeeName,userId,request.getRemoteAddr(),dept_code);
				System.out.println("USER CREATION SQL:" + count);

				count+=stadingRepo.INSERT_user_roles(emailId);
				System.out.println("USERS ROLE SQL:" + count);

				Integer slnos = jdbcTemplate.queryForObject("select nextval('apolcms.olcms_sms_creation_seq')",
						Integer.class);
				String smsText = "Your User Id is " + emailId
						+ " and Password is olcms@123 to Login to https://apolcms.ap.gov.in/ Portal. Please do not share with anyone. \r\n-APOLCMS";

				count+=stadingRepo.INSERT_olcms_sms_creation(slnos,employeeCode,emailId,mobileNo,smsText,new Timestamp(new Date().getTime()),request.getRemoteAddr(),userId );
				System.out.println("SMS--- SQL:" + count);

				System.out.println("smsText--------" + smsText);

				if (count >= 5) {
					msg="Standing Counsel Details Saved succesfully.";

				} else {
					msg="Error while Saving  the Standing Counsel Details. Please resubmit.";
				}

		} catch (Exception e) {

			msg="Error in Submission. Kindly try again.";
			e.printStackTrace();
		}

		return msg;
	}
	 

}
