package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.apcfss.common.CommonModels;
import in.apcfss.common.Itext_pdf_setting;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.model.ApplicationVariables;
import in.apcfss.repositories.EcourtsCaseMappingRepo;
import in.apcfss.repositories.EcourtsDeptInstructionsRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class EcourtsCaseMappingWithNewAckNoServiceImpl implements EcourtsCaseMappingWithNewAckNoService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	EcourtsCaseMappingRepo mappingRepo;

	@Override
	public List<Map<String, Object>> getEcourtsCaseMappingWithNewAckNo(Authentication authentication, String caseTypeId,
			String deptId, String districtId, String dofFromDate, String dofToDate, String advocateName,
			String categoryServiceId) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distCode=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sqlCondition = "",condition="",sql="";

		try {

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
			Date date = new Date(); 

			System.out.println("date--"+formatter.format(date)+"   FROM--"+dofFromDate +" TO--------"+dofToDate );

			if (dofFromDate!= null && !dofFromDate.toString().contentEquals("")) {
				System.out.println("FROMMM");
				condition += " and a.inserted_time::date >= to_date('" +dofFromDate
						+ "','dd-mm-yyyy') ";

			} /*
			 * else { condition +=
			 * " and a.inserted_time::date >= to_date('"+formatter.format(date)
			 * +"','dd-mm-yyyy')  "; abstractReqBody.setFromDate(formatter.format(date)); }
			 */
			if (dofToDate!= null && !dofToDate.toString().contentEquals("")) {
				condition += " and a.inserted_time::date <= to_date('"+dofToDate+ "','dd-mm-yyyy') ";

			} /*
			 * else { condition +=
			 * " and a.inserted_time::date <= to_date('"+formatter.format(date)+
			 * "','dd-mm-yyyy')     "; abstractReqBody.setToDate(formatter.format(date)); }
			 */
			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("3"))) {

				sqlCondition += " and ad.dept_code='" + deptCode + "' ";
			}


			if (districtId != null && !districtId.toString().contentEquals("")
					&& !districtId.toString().contentEquals("0")) {
				condition += " and a.distid='" +districtId.toString().trim() + "' ";
			} 

			if (deptId != null && !deptId.toString().contentEquals("")
					&& !deptId.toString().contentEquals("0")) {
				condition += " and ad.dept_code='" + deptId.toString().trim() + "' ";
			}



			if (roleId.equals("2")) {// District Collector
				sqlCondition += " and (case_status is null or case_status=7) and ad.dist_id='" + distCode + "' ";

				//abstractReqBody.setDistrictId(String.valueOf(distCode));
			}

			if (roleId.equals("3")) {// Secretariat Department
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode + "') ";
			}

			if (caseTypeId != null && !caseTypeId.toString().contentEquals("")
					&& !caseTypeId.toString().contentEquals("0")) {
				sqlCondition += " and a.casetype='" +caseTypeId.toString().trim() + "' ";
			}

			if (advocateName != null
					&& !advocateName.toString().contentEquals("")) {
				sqlCondition += " and replace(replace(advocatename,' ',''),'.','') ilike  '%"
						+advocateName+ "%'";
			}

			else if (roleId.equals("10")) { // District Nodal Officer
				sqlCondition += " and (case_status is null or case_status=8) and dist_id='" + distCode + "'";
			} else if (roleId.equals("5") || roleId.equals("9")) { // NO & HOD
				sqlCondition += " and (case_status is null or case_status in (3,4))";
			} else if (roleId.equals("3") || roleId.equals("4")) {// MLO & Sect. Dept.
				sqlCondition += " and (case_status is null or case_status in (1, 2))";
			}
			else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				sqlCondition = " and ad.dept_code='" + deptCode + "' and case_status=5 and ad.assigned_to='" + userid
						+ "'";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				sqlCondition = " and ad.dept_code='" + deptCode + "' and case_status=9 and ad.assigned_to='" + userid
						+ "'";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				sqlCondition = " and ad.dept_code='" + deptCode + "' and dist_id='" + distCode
						+ "' and case_status=10 and ad.assigned_to='" + userid + "'";
			}

			sql = "select DISTINCT a.ack_no,file_found,to_char(a.inserted_time,'dd-mm-yyyy') as generated_date,  case when hc_ack_no is null then a.ack_no else hc_ack_no end as hc_ack_no "
					+ " from ecourts_gpo_ack_depts ad inner join ecourts_gpo_ack_dtls a on (ad.ack_no=a.ack_no) left join scanned_affidavit_new_cases_count sc on (sc.ack_no=a.ack_no or  sc.ack_no=a.hc_ack_no) "
					+ " left join dept_new dmt on (ad.dept_code=dmt.dept_code)"
					+ " where a.delete_status is false  and ack_type='NEW' and a.ackno_updated is null  " 
					+ sqlCondition + " "+condition+" order by to_char(a.inserted_time,'dd-mm-yyyy') desc";

			System.out.println("CASES SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getEcourtsCaseMappingWithNewAckNos(Authentication authentication, String case_type,
			Integer case_year, Integer case_number) {
		List<Map<String, Object>> response = null;

		String finalCaseType = null;
		Integer finalCaseYear = null;
		Integer finalCaseNumber = null;

		// Assuming case_type is a String or Object to be converted to String
		if (case_type != null && !case_type.toString().trim().isEmpty()) {
			finalCaseType = case_type.toString().trim();
		}
		if (case_year != null && case_year > 0) {
			finalCaseYear = case_year;
		}
		if (case_number != null && case_number > 0) {
			finalCaseNumber = case_number;
		}

		System.out.println("Entry values === " + finalCaseType + ", " + finalCaseYear + ", " + finalCaseNumber);

		// Call the repository method (ensure it accepts null if any value is optional)
		if (finalCaseType != null && finalCaseYear != null && finalCaseNumber != null) {
			response = mappingRepo.getCinoDetails(finalCaseType, finalCaseYear, finalCaseNumber);
		}		return response;
	}

	@Override
	public String getsubmitDetailsForNewAckNo(Authentication authentication, String caseType1, int regYear1,
			int mainCaseNo, String ackNo) {
		String 	message="";
		String sql="";
		String mainCase="";
		int a=0;
		int b=0;
		try {
			System.out.println("ackNo---"+ackNo);

			if (caseType1 != null && !caseType1.toString().trim().isEmpty()) {
				mainCase=caseType1+"/"+mainCaseNo+"/"+regYear1;
			}
			System.out.println("mainCaseNo----"+mainCase);

			if (ackNo != null && !ackNo.contentEquals("") ) {

				a = mappingRepo.getsubmitDetailsMappingNewTable(mainCase,mainCase,ackNo);
				System.out.println("a--"+a);

				b = mappingRepo.getsubmitDetailsMappingLegacyTable(ackNo,caseType1,mainCaseNo,regYear1);
				System.out.println("a&b values===="+a+"--b---"+b);
				
				if(a > 0 && b > 0) { 
					message="Acknowledgement details updated successfully with Ack No.:" + ackNo;

				}else {
					message="Failed to save Data. Kindly try again.";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}


}
