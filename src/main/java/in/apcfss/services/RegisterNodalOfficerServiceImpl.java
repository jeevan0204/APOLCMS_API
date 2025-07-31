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
import in.apcfss.repositories.RegisterNodalOfficerRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class RegisterNodalOfficerServiceImpl implements RegisterNodalOfficerService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CommonMethodsController commonMethodController;

	@Autowired
	RegisterNodalOfficerRepo registerNodalOfficerRepo;

	@Override
	public List<Map<String, Object>> getNodalList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String  sql="" ;
		String tableName=commonMethodController.getTableName(String.valueOf(distId));
		try {
			if(roleId.trim().equals("2"))
			{
				sql = "select slno, user_id, designation, employeeid, mobileno, emailid, aadharno, case when b.fullname_en is null then 'TRANSFERRED / PROMOTED' else b.fullname_en end as fullname_en, designation_name_en,upper(trim(d.description)) as description,d.dept_code"
						+ ", case when b.fullname_en is null then 'TRANSFERRED' else null end as status "
						+ "from nodal_officer_details a " + "left join ( "
						+ "select distinct employee_id,fullname_en from " + tableName + " "
						+ ") b on (a.employeeid=b.employee_id) " + "left join ( "
						+ "select distinct designation_id, designation_name_en from " + tableName + " "
						+ ") c on (a.designation=c.designation_id) "
						+ "inner join dept_new d on (a.dept_id=d.dept_code) " + "where a.user_id='" + userId
						+ "' order by d.reporting_dept_code, d.dept_code";

			}else{
				sql="select d.dept_code, upper(trim(d.description)) as description,slno, user_id, designation, employeeid, mobileno, emailid, aadharno, "
						+ "case when b.fullname_en is null then 'TRANSFERRED / PROMOTED' else b.fullname_en end as fullname_en, designation_name_en ,"
						+ " case when b.fullname_en is null then 'TRANSFERRED' else null end as status"
						+ " from dept_new d left join (select slno, user_id, designation, employeeid, mobileno, emailid, aadharno, dept_id,dist_id from nodal_officer_details) a  on (a.dept_id=d.dept_code) "
						+ " left join (select distinct employee_id,fullname_en,designation_name_en, designation_id from nic_data) b on (a.employeeid=b.employee_id and a.designation=b.designation_id) "
						+ " where (reporting_dept_code='"+deptCode+"' or dept_code='"+deptCode+"') and substr(dept_code,4,2)!='01' and d.display= true and  coalesce(a.dist_id,0)=0 order by d.reporting_dept_code, d.dept_code";
			}

			System.out.println("InterimOrderFinalOrderService SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getDistNodalDepartmentList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sql ="";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String TableName=commonMethodController.getTableName(String.valueOf(distId));
		try {
			sql = " select dept_code as value,dept_code||'-'||upper(trim(description)) as label from dept_new where dept_code in (select distinct substring(global_org_name,1,5) from "+TableName+" ) "
					+ " and deptcode!='01'  and  dept_code not in (select dept_id from  nodal_officer_details where dist_id='"+distId+"'  ) order by sdeptcode,deptcode";

			System.out.println("getDistNodalDepartmentList SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getNodalDepartmentList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		List<Map<String, Object>> data =null;
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		try {
			data=registerNodalOfficerRepo.getNodalDepartmentList(deptCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	@Override
	public List<Map<String, Object>> getHodEmployeeDetails(Authentication authentication,String deptId){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String  sql="" ;
		String tableName=commonMethodController.getTableName(String.valueOf(distId));
		try {
			if(roleId.trim().equals("2"))
			{
				sql = "select slno, user_id, designation, employeeid, mobileno, emailid, aadharno, case when b.fullname_en is null then 'TRANSFERRED / PROMOTED' else b.fullname_en end as fullname_en, designation_name_en,upper(trim(d.description)) as description,d.dept_code"
						+ ", case when b.fullname_en is null then 'TRANSFERRED' else null end as status "
						+ "from nodal_officer_details a " + "left join ( "
						+ "select distinct employee_id,fullname_en from " + tableName + " "
						+ ") b on (a.employeeid=b.employee_id) " + "left join ( "
						+ "select distinct designation_id, designation_name_en from " + tableName + " "
						+ ") c on (a.designation=c.designation_id) "
						+ "inner join dept_new d on (a.dept_id=d.dept_code) " + "where a.user_id='" + userId
						+ "'  and a.dept_id='" + deptId + "'";
				
			}else {
				
				sql="select d.dept_code, upper(trim(d.description)) as description,slno, user_id, designation, employeeid, mobileno, emailid, aadharno, case when b.fullname_en is null then 'TRANSFERRED / PROMOTED' else b.fullname_en end as fullname_en, designation_name_en "
						+ ", case when b.fullname_en is null then 'TRANSFERRED' else null end as status "
						+ "from dept_new d "
						+ "left join (select slno, user_id, designation, employeeid, mobileno, emailid, aadharno, b.fullname_en, designation_name_en, a.dept_id from nodal_officer_details a   "
						+ "left join (select distinct employee_id,fullname_en from nic_data) b on (a.employeeid=b.employee_id)   "
						+ "left join (select distinct designation_id, designation_name_en from nic_data ) c on (a.designation=c.designation_id)   "
						+ "where user_id='"+userId+"' and coalesce(a.dist_id,0)=0) b on (d.dept_code = b.dept_id) where reporting_dept_code='"+deptId+"'  and b.dept_id='" + deptId + "' and d.display= true order by 1"
						+ "";
			}

			System.out.println("InterimOrderFinalOrderService SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getRegistrerNodalDesignationList(Authentication authentication,String deptCode){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sql ="";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String TableName=commonMethodController.getTableName(String.valueOf(distId));
		try {
			
			  sql="select distinct designation_id::int4 as value, designation_name_en as label from "+TableName+" where substring(global_org_name,1,5)='"
					+deptCode+ "'  and trim(upper(designation_name_en))<>'MINISTER' order by designation_id::int4 desc";
			System.out.println("getRegistrerNodalDesignationList SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	
	private String safeTrim(String value) {
	    return value != null ? value.trim() : "";
	}
	
	@Override
	public String getSaveEmployeeDetailsNodalOfficer(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBod,HttpServletRequest request){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sql ="",sMs = null;
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		
		String tableName=commonMethodController.getTableName(String.valueOf(distId));
		int status = 0;
		int a=0;
		String deptCode=abstractReqBod.getDeptId();
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

				 status+=registerNodalOfficerRepo.insert_nodal_officer_details(deptCode,userId,designationId,employeeId,mobileNo,emailId,aadharNo,userId,request.getRemoteAddr(),distId);

				if (status == 1){
					if(registerNodalOfficerRepo.usersCount(emailId) > 0) {
						 
						a=registerNodalOfficerRepo.users_log(emailId);
						
						a=registerNodalOfficerRepo.delete_user_roles(emailId);
						
						a=registerNodalOfficerRepo.delete_users(emailId);
					}
					if(tableName.equals("nic_data")) {
						
						sql="insert into users (userid, password,password_text, user_description, created_by, created_on, created_ip, dept_id, dept_code, user_type) select a.emailid, md5('olcms@123'),'olcms@123', b.fullname_en, '"
								+ userId + "', now(),'" + request.getRemoteAddr()
								+ "',d.dept_id,d.sdeptcode||d.deptcode as deptcode, 5 from nodal_officer_details a "
								+ "inner join (select distinct employee_id,fullname_en,designation_id,designation_name_en, substr(global_org_name,1,5) as dept_code  from nic_data) b on (a.employeeid=b.employee_id and a.dept_id=b.dept_code and a.designation=b.designation_id) "
								+ "inner join dept_new d on (d.dept_code=b.dept_code) "
								+ " where employeeid='"+employeeId+"' and a.dept_id='"+deptCode+"'";
						
						//status+=registerNodalOfficerRepo.insert_usersRegisterNodal(userId,request.getRemoteAddr(),employeeId,deptCode);
						
					}
					else {
						sql = "insert into users (userid, password,password_text, user_description, created_by, created_on, created_ip, dept_id, dept_code, dist_id, user_type) "
								+ " select a.emailid, md5('olcms@123'),'olcms@123', b.fullname_en, '"
								+ userId + "', now(),'" + request.getRemoteAddr()
								+ "',d.dept_id,d.dept_code as deptcode,"
								+ CommonModels.checkIntObject(distId)
								+ ", 10 from nodal_officer_details a "

								+ "inner join (select distinct employee_id,fullname_en,designation_id,designation_name_en, substr(global_org_name,1,5) as dept_code  from "
								+ tableName
								+ ") b on (a.employeeid=b.employee_id and a.dept_id=b.dept_code and a.designation=b.designation_id) "
								+ "inner join dept_new d on (d.dept_code=b.dept_code) "

								+ " inner join district_mst dm on (dm.short_name='" + userId + "') where employeeid='" + employeeId + "' and a.dept_id='"
								+deptCode+ "' and a.dist_id='" + CommonModels.checkIntObject(distId) + "'";
						
					}
					
					status+=jdbcTemplate.update(sql);
					System.out.println("SQL::"+sql);
					System.out.println("status::"+status);
					if(tableName.equals("nic_data")) {
						status+=registerNodalOfficerRepo.insert_user_rolesRegisterNodal(emailId);
					}
					else {
						status+=registerNodalOfficerRepo.insert_user_roles10RegisterNodal(emailId);
						
					}
					System.out.println("STATUS:"+status);
					if(status>0) {
						 
						String smsText="Your User Id is "+emailId+" and Password is olcms@123 to Login to https://apolcms.ap.gov.in/ Portal. Please do not share with anyone. \r\n-APOLCMS";
						String templateId="1007784197678878760";
						//sendSMSDao.sendSMS(mobileNo, smsText, templateId);

						sMs="Nodal Officer details saved & User Login created succesfully. Login details sent to Registered Mobile No.";
						 
					}
					else {
						
						sMs="Error while registering Nodal Officer(Legal).";
					}
				}
				else
					sMs="Error. Nodal Officer(Legal) not Registered due to wrong data submitted or Mobile No. (or) email Id (or) Aadhaar has been already registered.";
			}else {
	            sMs = "Error: Mobile number must be 10 digits and Aadhar number must be 12 digits.";
	        } 

		} catch (Exception e) {
			e.printStackTrace();
			sMs = "Error: Invalid input or unexpected error occurred while saving Nodal Officer Details.";
		}
		return sMs;
	}
	

}
