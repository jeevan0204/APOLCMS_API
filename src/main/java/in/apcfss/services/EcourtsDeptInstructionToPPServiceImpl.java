package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import in.apcfss.repositories.EcourtsDeptInstructionsRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class EcourtsDeptInstructionToPPServiceImpl implements EcourtsDeptInstructionToPPService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	EcourtsDeptInstructionsRepo InstructionsRepo;

	@Override
	public List<Map<String, Object>> getAckList(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		String sqlCondition="", sql="";

		try {
			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("3")
					|| roleId.equals("4"))) {
				sqlCondition += " and d.dept_code='" + deptCode + "' ";
			}

			if (roleId.equals("10")) { // District Nodal Officer
				sqlCondition += " and (case_status is null or case_status=8) and b.dist_id='" + distId + "'";
			} else if (roleId.equals("5") || roleId.equals("9")) { // NO & HOD
				sqlCondition += " and (case_status is null or case_status in (3,4)) and coalesce(assigned,'f')='f' ";
			} else if (roleId.equals("3") || roleId.equals("4")) {// MLO & Sect. Dept.
				sqlCondition += " and (case_status is null or case_status in (1, 2))";
			} else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				sqlCondition = " and b.dept_code='" + deptCode + "' and case_status=5 and assigned_to='" + userid
						+ "'";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				sqlCondition = " and b.dept_code='" + deptCode + "' and case_status=9 and assigned_to='" + userid
						+ "'";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				sqlCondition = " and b.dept_code='" + deptCode + "' and b.dist_id='" + distId
						+ "' and case_status=10 and assigned_to='" + userid + "'";
			}

			if (roleId.equals("2")) {// District Collector
				sqlCondition += " and (case_status is null or case_status=7) and b.dist_id='" + distId + "' ";
				// formBean.setDyna("districtId", distCode);
			}

			if (roleId.equals("3")) {// Secretariat Department
				sqlCondition += " and (d.dept_code='" + deptCode + "' or d.reporting_dept_code='" + deptCode + "') ";
			}
			if (roleId.equals("4")) {// MLO
				sqlCondition += " and (d.dept_code='" + deptCode + "' or d.reporting_dept_code='" + deptCode + "') ";
			}

			sql = "select b.ack_no as value,b.ack_no as label from ecourts_gpo_ack_dtls a "
					+ " inner join ecourts_gpo_ack_depts b on (a.ack_no=b.ack_no) left join dept_new d on (b.dept_code=d.dept_code)"
					+ " where ack_type='NEW' and inserted_by like 'PP%' and coalesce(B.ecourts_case_status,'')!='Closed'  "
					+ sqlCondition + "  order by b.ack_no";

			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public Map<String, Object> getCasesListEcourtsDeptNewPP(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody, String caseType) {

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		String sqlCondition="", sql="" ;
		String ackNoo = null;

		String cino = null;
		try {
			String caseTypes = abstractReqBody.getCaseType()+ "";
			String regYear = abstractReqBody.getRegYear()+ "";
			String mainCaseNo = abstractReqBody.getMainCaseNo()+ "";
			if (caseType.equals("New")) {

				//formbean.setDyna("oldNewType", "New");

				if (roleId.equals("6")) { // || !caseType.equals(null)
					ackNoo = abstractReqBody.getCino().toString();

				} else {
					ackNoo = abstractReqBody.getAckNoo().toString();
				}



				if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("3")
						|| roleId.equals("4"))) {
					// sqlCondition += " and (dmt.dept_code='" + deptCode + "' or
					// dmt.reporting_dept_code='"+deptCode+"') ";
					sqlCondition += " and d.dept_code='" + deptCode + "' ";
				}

				if (roleId.equals("2")) {// District Collector
					sqlCondition += " and (case_status is null or case_status=7) and b.dist_id='" + distId + "' ";
					// formBean.setDyna("districtId", distCode);
				}

				if (roleId.equals("3")) {// Secretariat Department
					sqlCondition += " and (d.dept_code='" + deptCode + "' or d.reporting_dept_code='" + deptCode + "') ";
				}
				if (roleId.equals("4")) {// MLO
					sqlCondition += " and (d.dept_code='" + deptCode + "' or d.reporting_dept_code='" + deptCode + "') ";
				}

				if (roleId.equals("10")) { // District Nodal Officer
					sqlCondition += " and (case_status is null or case_status=8) and b.dist_id='" + distId + "'";
				} else if (roleId.equals("5") || roleId.equals("9")) { // NO & HOD
					sqlCondition += " and (case_status is null or case_status in (3,4)) and coalesce(assigned,'f')='f' ";
				} else if (roleId.equals("5") || roleId.equals("4") || roleId.equals("10")) {// MLO & Sect. Dept.
					sqlCondition += " and (case_status is null or case_status in (1, 2))";
				} else if (roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {
					sqlCondition += "  and assigned_to='" + userid + "' and b.dist_id='" + distId + "' ";
				} else if (roleId.equals("12")) {
					sqlCondition += "  and b.case_status=10 and assigned=true ";
				}
			} else {

				//formbean.setDyna("oldNewType", "Legacy");

				if (roleId.equals("6")) { // || !caseType.equals(null)
					cino = abstractReqBody.getCino().toString();

				} else {

					cino = caseTypes + "/" + mainCaseNo + "/" + regYear;
				}

				if (caseTypes != null && !caseTypes.contentEquals("") && !caseTypes.contentEquals("0")) {
					sqlCondition += " and a.type_name_reg='" + caseTypes.trim() + "' ";
				}

				if (mainCaseNo != null && !mainCaseNo.contentEquals("") && !mainCaseNo.contentEquals("0")) {
					sqlCondition += " and a.reg_no='" + mainCaseNo.trim() + "' ";
				}

				if (regYear != null && !regYear.contentEquals("") && !regYear.contentEquals("0")) {
					sqlCondition += " and a.reg_year='" + regYear.trim() + "' ";
				}

				if (roleId.equals("2") || roleId.equals("12")) { // District Collector

					sqlCondition += "  and a.dist_id='" + distId + "'";// and case_status=7
				} else if (roleId.equals("10")) { // District Nodal Officer
					sqlCondition += " and a.dist_id='" + distId + "'";// and case_status=8
				}

				if (roleId.equals("5") || roleId.equals("9")) {// NO & HOD
					sqlCondition += " and a.dept_code='" + deptCode + "' ";
				} else if (roleId.equals("3") || roleId.equals("4")) {// MLO & Sect. Dept.
					sqlCondition += " and a.dept_code='" + deptCode + "' ";
				} else if (roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {
					sqlCondition += "  and assigned_to='" + userid + "'";
				} else if (roleId.equals("6")) {
					sqlCondition += "  and a.cino='" + cino + "'";
				}
			}

			System.out.println("ackNoo--" + ackNoo + "    " + "cino---" + cino);

			List<Map<String, Object>> data = null;

			if (caseType.equals("Legacy")) {

				sql = "select a.*, "
						+ " nda.fullname_en as fullname, nda.designation_name_en as designation, nda.post_name_en as post_name, nda.email, nda.mobile1 as mobile,dim.district_name , "
						+ " 'Pending at '||ecs.status_description||'' as current_status, coalesce(trim(a.scanned_document_path),'-') as scanned_document_path1, b.orderpaths,"
						+ " case when (prayer is not null and coalesce(trim(prayer),'')!='' and length(prayer) > 2) then substr(prayer,1,250) else '-' end as prayer, prayer as prayer_full, ra.address from ecourts_case_data a "

						+ " left join nic_prayer_data np on (a.cino=np.cino)"
						+ " left join nic_resp_addr_data ra on (a.cino=ra.cino and party_no=1) "
						+ " left join district_mst dim on (a.dist_id=dim.district_id) "
						+ " left join ecourts_mst_case_status ecs on (a.case_status=ecs.status_id) "
						+ " left join nic_data_all nda on (a.dept_code=substr(nda.global_org_name,1,5) and a.assigned_to=nda.email and nda.is_primary='t' and coalesce(a.dist_id,'0')=coalesce(nda.dist_id,'0')) "
						+ " left join" + " ("
						+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
						+ " from "
						+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
						+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1" + " union"
						+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
						+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
						+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
						+ " on (a.cino=b.cino) inner join dept_new d on (a.dept_code=d.dept_code) where d.display = true "
						+ sqlCondition;

				System.out.println("ecourts SQL:" + sql);
				data = jdbcTemplate.queryForList(sql);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					for (int i = 0; i < data.size(); i++) {
						if (data.get(i).get("is_scandocs_exists") == null) {
							data.get(i).put("is_scandocs_exists", "false");
						}
					}
					map.put("CASESLISTOLD", data);

					if (caseTypes == null || caseTypes.isEmpty() || caseTypes.equals("null")) {

					}else {
						map.put("showInstructions", "data");
					}
					//map.put("cino", data.get(0).get("cino"));

					String getValue=data.get(0).get("cino")+"";

					List<Map<String, Object>> existData = InstructionsRepo.InstructionLegacyExistDataOld(getValue);
					map.put("existDataOld", existData);
				}  
			} else {

				System.out.println("ecourts SQL:" + sql);
				data = InstructionsRepo.InstructionCASESLISTNEW(ackNoo);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					map.put("CASESLISTNEW", data);

					//map.put("cino", ackNoo);

					List<Map<String, Object>> existData = InstructionsRepo.InstructionExistDataNew(ackNoo);
					map.put("existDataNew", existData);

				}  
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	@Override
	public String getSubmitCategoryEcourtsDeptNewPP(UserDetailsImpl userPrincipal,
			HCCaseStatusAbstractReqBody abstractReqBody,HttpServletRequest request) {

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		String msg="";

		String cIno = abstractReqBody.getCino() + "";
		String oldNewType = CommonModels.checkStringObject(abstractReqBody.getOldNewType());
		String status_flag="";
		int a=0;
		String ackPath ="";
		String ins ="";
		try {
			 if (roleId.equals("6")) {
					 
					status_flag = "D";
				} else {
 
					status_flag = "I";
				}
			System.out.println("nulll----------" + abstractReqBody.getChangeLetter());
			String photofile =  abstractReqBody.getChangeLetter();// getValidateProofDocumentPath().getOriginalFilename();

			ins = abstractReqBody.getInstructions().toString();

			System.out.println("ackPath---"+ackPath);

			//String photodirPath = context.getRealPath("/") + "/uploads/Instruction/";

			//String photoPath = "/uploads/Instruction/" + cIno + "_" + photofile.getOriginalFilename();
			//CommonFunctionUtils.fileUpload(photofile, photodirPath, cIno + "_" + photofile.getOriginalFilename(),
			//		cIno + "_" + photofile.getOriginalFilename(), request);

			String fileName = "Instruction_"+cIno + ".pdf";

			String pdfFilePath = ApplicationVariables.InstructionPath + fileName;

			System.out.println("photopath---" + fileName);

			a =InstructionsRepo.insert_ecourts_dept_instructions(cIno,ins,photofile,deptCode,distId,userid,oldNewType,status_flag,pdfFilePath);
			System.out.println("a========>"+a);
			if (a > 0) {

				a =InstructionsRepo.ecourts_case_activities_instructions(cIno,userid,request.getRemoteAddr(),ins,photofile);

				msg="Instructions data saved successfully.";

			} else {

				msg="Error in submission. Kindly try again.";
			}

		}  catch (Exception e) {

			msg="Error in Submission. Kindly try again.";
			e.printStackTrace();
		}

		return msg;
	}


}
