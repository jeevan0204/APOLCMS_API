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
public class EcourtsDeptInstructionServiceImpl implements EcourtsDeptInstructionService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	EcourtsDeptInstructionsRepo InstructionsRepo;

	@Override
	public List<Map<String, Object>> getAckList(UserDetailsImpl userPrincipal, HCCaseStatusAbstractReqBody abstractReqBody) {

		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		int distId=userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		Map<String, Object> map = new HashMap<>();
		String sqlCondition="", sql="";

		try {

			if (!(roleId.equals("1") || roleId.equals("7") || roleId.equals("2") || roleId.equals("3")
					|| roleId.equals("4"))) {

				sqlCondition += " and dmt.dept_code='" + deptCode + "' ";
			}

			if (roleId.equals("2")) {// District Collector
				sqlCondition += " and (case_status is null or case_status=7) and b.dist_id='" + distId + "' ";

			}

			if (roleId.equals("3")) {// Secretariat Department
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
						+ "') ";
			}
			if (roleId.equals("4")) {// MLO
				sqlCondition += " and (dmt.dept_code='" + deptCode + "' or dmt.reporting_dept_code='" + deptCode
						+ "') ";
			}

			if (roleId.equals("10")) { // District Nodal Officer
				sqlCondition += " and (case_status is null or case_status=8) and dist_id='" + distId + "'";
			} else if (roleId.equals("5") || roleId.equals("9")) { // NO & HOD
				sqlCondition += " and (case_status is null or case_status in (3,4)) ";
			} else if (roleId.equals("3") || roleId.equals("4")) {// MLO & Sect. Dept.
				sqlCondition += " and (case_status is null or case_status in (1, 2)) and coalesce(assigned,'f')='f' ";
			} 
			else if (roleId != null && roleId.equals("8")) { // SECTION OFFICER - SECT. DEPT
				sqlCondition = " and b.dept_code='" + deptCode + "' and case_status=5 and assigned_to='" + userid
						+ "' and coalesce(assigned,'t')='t' ";
			} else if (roleId != null && roleId.equals("11")) { // SECTION OFFICER - HOD
				sqlCondition = " and b.dept_code='" + deptCode + "' and case_status=9 and assigned_to='" + userid
						+ "' and coalesce(assigned,'t')='t'  ";
			} else if (roleId != null && roleId.equals("12")) { // SECTION OFFICER - DISTRICT
				sqlCondition = " and b.dept_code='" + deptCode + "' and b.dist_id='" + distId
						+ "' and case_status=10 and assigned_to='" + userid + "' and coalesce(assigned,'t')='t'  ";
			}


			if (roleId.equals("6")) {

				map.put("HEADING", "Daily Status Entry");
			} else {

				map.put("HEADING", "Instructions Entry");
			}

			sql = "select b.ack_no as value,b.ack_no as label from ecourts_gpo_ack_dtls a "
					+ " inner join ecourts_gpo_ack_depts b on (a.ack_no=b.ack_no) left join dept_new dmt on (B.dept_code=dmt.dept_code)  "
					+ " where ack_type='NEW' and inserted_by not like 'PP%' and delete_status is false  " + sqlCondition + "  order by b.ack_no";
			System.out.println("SQL:" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}




	public List<Map<String, Object>> getYearByCaseTypeYear(Authentication authentication, String caseType) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		 
		List<Object> params = new ArrayList<>();
	    StringBuilder sql = new StringBuilder(
	        "SELECT DISTINCT reg_year AS value, reg_year AS label " +
	        "FROM ecourts_case_data a " +
	        "INNER JOIN ecourts_case_type_master_new b ON (a.type_name_reg = b.case_type) " +
	        "WHERE b.case_type = ? "
	    );
	    params.add(caseType);

	    try {
	        if (!StringUtils.isBlank(deptCode)) {
	            sql.append("AND a.dept_code = ? ");
	            params.add(deptCode);
	        }

	        if ("8".equals(roleId) || "11".equals(roleId) || "12".equals(roleId)) {
	            sql.append("AND assigned_to = ? ");
	            params.add(userid);
	        }

	        sql.append("ORDER BY reg_year DESC");

	        System.out.println("Final SQL: " + sql);
	        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }

	}

	public List<Map<String, Object>> getNumberbyCaseType(Authentication authentication, String caseType, int regYear) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode=userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
		String userid = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String sql="";
		try {

			String condition = "";
			if (!StringUtils.isBlank(deptCode) && !deptCode.equals(null)) {
				condition = "and a.dept_code='" + deptCode + "' ";
			}
			if (roleId.equals("8") || roleId.equals("11") || roleId.equals("12")) {
				condition += "  and assigned_to='" + userid + "'";
			}

			sql = "select DISTINCT reg_no as value,reg_no as label from ecourts_case_data a "
					+ "inner join ecourts_case_type_master_new b on (a.type_name_reg=b.case_type) where b.case_type='"
					+ caseType + "' and a.reg_year='" + regYear + "'  " + condition + " ORDER BY reg_no DESC";
			System.out.println(" getCaseNumberList fgg=" + sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public Map<String, Object> getCasesListEcourtsDept(UserDetailsImpl userPrincipal,
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
				map.put("showInstructions", "data");

				//map.put("oldNewType", "New");

				if (roleId.equals("6")) { // || !caseType.equals(null)
					ackNoo = abstractReqBody.getCino().toString();

				} else {
					ackNoo = abstractReqBody.getAckNoo().toString();
				}

			} else {

				if (roleId.equals("6")) { // || !caseType.equals(null)
					cino = abstractReqBody.getCino().toString();

				} else {
					System.out.println("caseTypes---"+caseTypes);
					if (caseTypes == null || caseTypes.isEmpty() || caseTypes.equals("null")) {
						System.out.println("formbean maincase----"+abstractReqBody.getCaseType1() + "/" + abstractReqBody.getMainCaseNo()+ "/" + abstractReqBody.getRegYear1());

						cino = abstractReqBody.getCaseType1() + "/" + abstractReqBody.getMainCaseNo()+ "/" + abstractReqBody.getRegYear1();
					}
					else {

						cino = caseTypes + "/" + mainCaseNo + "/" + regYear;
					}
				}

				if (caseTypes != null && !caseTypes.contentEquals("") && !caseTypes.contentEquals("0")) {

					sqlCondition += " and a.type_name_reg='" + caseTypes.trim() + "' ";
				}else {
					sqlCondition += " and a.type_name_reg='" + abstractReqBody.getCaseType1()+ "' ";
				}

				if (mainCaseNo != null && !mainCaseNo.contentEquals("") && !mainCaseNo.contentEquals("0")) {
					sqlCondition += " and a.reg_no='" + mainCaseNo.trim() + "' ";
				}else {
					sqlCondition += " and a.reg_no='" +abstractReqBody.getMainCaseNo()+ "' ";
				}

				if (regYear != null && !regYear.contentEquals("") && !regYear.contentEquals("0")) {
					sqlCondition += " and a.reg_year='" + regYear.trim() + "' ";
				}else {
					sqlCondition += " and a.reg_year='" + abstractReqBody.getRegYear1()+ "' ";
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
	public String getSubmitCategoryecourtsDeptInstruction(UserDetailsImpl userPrincipal,
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
			if ("REV03".equals(deptCode) && cIno != null && abstractReqBody != null) {

				ackPath = generateAckPdf_Jeev(cIno, abstractReqBody);
				ins = abstractReqBody.getSlno4().toString();
			}else {

				ins = abstractReqBody.getInstructions().toString();
			}

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


	public String generateAckPdf_Jeev(String ackNo, HCCaseStatusAbstractReqBody abstractReqBody) throws Exception {

		Document document = null;
		PdfWriter writer = null;
		PdfPTable table = null;

		String pdfFilePath = "";

		try {
			String fileName = "Instruction_"+ackNo + ".pdf";
 
			String basePath = ApplicationVariables.InstructionPath; 

			pdfFilePath = Paths.get(basePath, fileName).toString(); // safer path join
			
			File file = new File(pdfFilePath);

			// Ensure parent directories exist
			File parentDir = file.getParentFile();
			if (!parentDir.exists()) {
			    parentDir.mkdirs();
			}

			Itext_pdf_setting pdfsetting = new Itext_pdf_setting();
			int head = 14;
			int subhead = 12;

			document = new Document(PageSize.A4, 5, 5, 5, 5);
			document.setMargins(30, 30, 30, 30);
			document.setPageSize(PageSize.A4);
			writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			BaseFont bf_courier = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
			HeaderFooter footer = new HeaderFooter(
					new Phrase("Page No.:" + document.getPageNumber(), new Font(bf_courier, 8, Font.NORMAL)), true);
			footer.setBorder(Rectangle.NO_BORDER);
			footer.setAlignment(Element.ALIGN_RIGHT);
			document.setFooter(footer);

			document.open();

			String slno1=abstractReqBody.getSlno1().toString();
			String slno2=abstractReqBody.getSlno2().toString();
			//String slno3=formBean.getDyna("slno3").toString();
			String slno3a=abstractReqBody.getSlno3a().toString();
			String slno3b=abstractReqBody.getSlno3b().toString();
			String slno3c=abstractReqBody.getSlno3c().toString();
			String slno3d=abstractReqBody.getSlno3d().toString();
			String slno3e=abstractReqBody.getSlno3e().toString();
			String slno4=abstractReqBody.getSlno4().toString();
			String slno5=abstractReqBody.getSlno5().toString();
			String slno6=abstractReqBody.getSlno6().toString();
			String slno7=abstractReqBody.getSlno7().toString();
			String slno8=abstractReqBody.getSlno8().toString();
			String slno8a=abstractReqBody.getSlno8a().toString();
			String slno8b=abstractReqBody.getSlno8b().toString();
			String slno9=abstractReqBody.getSlno9().toString();
			String slno10=abstractReqBody.getSlno10().toString();
			String slno11=abstractReqBody.getSlno11().toString();
			String slno12=abstractReqBody.getSlno12().toString();
			String slno13=abstractReqBody.getSlno13().toString();
			String slno14=abstractReqBody.getSlno14().toString();

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
			document.add(pdfsetting.para("ANDHRA PRADESH ONLINE LEGAL CASE MONITORING SYSTEM (APOLCMS)", head,Paragraph.ALIGN_CENTER, 90, 3));
			document.add(pdfsetting.para("GOVERNMENT OF ANDHRA PRADESH", subhead, Paragraph.ALIGN_CENTER, 0, 2));

			document.add(pdfsetting.para("Instructions to the Government Pleader for Taxes before the Hon’ble High Court of Andhra Pradesh, Amaravati", subhead, Paragraph.ALIGN_CENTER, 0, 3));

			table = pdfsetting.table(2, 100);
			pdfsetting.border = 1;

			// Define font styles
			// Create table with 3 columns
			PdfPTable table3 = new PdfPTable(3);
			table3.setWidthPercentage(100);
			table3.setSpacingBefore(10f);
			table3.setSpacingAfter(10f);

			// Set column widths
			float[] columnWidths3 = {0.5f, 3f, 4f};
			table3.setWidths(columnWidths3);

			// Add Table Header
			table3.addCell(createCell("Sl. No.", headerFont, Element.ALIGN_CENTER, Color.LIGHT_GRAY));
			table3.addCell(createCell("Description", headerFont, Element.ALIGN_CENTER, Color.LIGHT_GRAY));
			table3.addCell(createCell("Details", headerFont, Element.ALIGN_CENTER, Color.LIGHT_GRAY));

			// Add table data
			addRow(table3, "1", "Name of the Petitioner / W.P.No.", ""+slno1+"");
			addRow(table3, "2", "Name of the Act", ""+slno2+"");

			addRow(table3, "3", "The information relating to the Service of Notice/Order (Clearly mentioning the above particulars substantiates the department’s "
					+ " compliance with due process in issuing and serving the notices/orders effectively)"," 3a. Notice/Order DIN No. & Date: "+slno3a+" \n 3b. Mode of Service: "+slno3b+" \n 3c. Date of Dispatch: "+slno3c+" \n 3d. Date of Actual Service: "+slno3d+" \n 3e. Date of Acknowledgment : "+slno3e+"");
			addRow(table3, "4", "Brief facts of the case (clearly narrate the issue under dispute)", ""+slno4+"");
			addRow(table3, "5", "Issues raised by the petitioner (Briefly mention  the main issues or allegations raised by the petitioner)", ""+slno5+"");
			addRow(table3, "6", "Counter points of the Department against issues raised by the petitioner in column No.4 (clearly & precisely outline the stand of the Department in response to each issue raised by the petitioner) ", ""+slno6+"");
			addRow(table3, "7", "Relevant provisions of the act (mention applicable sections, rules, notification, circulars and case laws to support the departmental stance)", ""+slno7+"");
			addRow(table3, "8", "Mention the following points clearly indicating the reasons for opposing the writ petition:", ""+slno8+"");
			addRow(table3, "8a", "Maintainability (Generally, if there exists an effective and alternative statutory remedy (e.g., appeal, revision), the writ petition may not be maintainable unless there are exceptional circumstances such as violation of fundamental rights, natural justice, or jurisdictional errors)", ""+slno8a+"");
			addRow(table3, "8b", "Limitation period (Although no explicit limitation period is fixed for writ petitions, courts expect petitions to be filed promptly and without undue delay. Delayed petitions might be dismissed on grounds of laches (unexplained delay) unless adequately justified)", ""+slno8b+"");
			addRow(table3, "9", "Mention specific reasons and material on record based upon which the assessment orders is passed by invoking Section 74 of the CGST/APGST Act 2017(in Fraud cases)", ""+slno9+"");
			addRow(table3, "10", "Specific instructions based on the above to the G.P. for effective argument of the case.\n (Provide the gist of the case supporting the Departmental stance)	", ""+slno10+" ");
			addRow(table3, "11", "Prayer \n (Request dismissal of the petition clearly stating no valid grounds exist warranting judicial intervention, seeking upholding of the departmental action as lawful and justified)", " "+slno11+" ");

			// Add Respondent Details
			document.add(table3);

			document.add(pdfsetting.para("Name of the Respondent:   "+slno12+" ", subhead, Paragraph.ALIGN_LEFT, 0, 3));
			document.add(pdfsetting.para("Designation:    "+slno13+" ", subhead, Paragraph.ALIGN_LEFT, 0, 3));
			document.add(pdfsetting.para("Mobile No:    "+slno14+" ", subhead, Paragraph.ALIGN_LEFT, 0, 3));
			document.add(pdfsetting.para("Signature", subhead, Paragraph.ALIGN_RIGHT, 0, 1));

			document.add(pdfsetting.para("Note", subhead, Paragraph.ALIGN_LEFT, 0, 1));
			document.add(pdfsetting.para("1). The above instructions shall be submitted through the APOLCMS only.", subhead, Paragraph.ALIGN_LEFT, 0, 1));
			document.add(pdfsetting.para("2). If the notices/orders have been uploaded to the official GST portal, the respondent may attach screenshots as proof of compliance. ", subhead, Paragraph.ALIGN_LEFT, 0, 1));
			document.add(pdfsetting.para("3). The GP office for Taxes is requested to download all relevant instructions from the APOLCMS and requested to present comprehensive arguments before the Hon’ble High Court of Andhra Pradesh.", subhead, Paragraph.ALIGN_LEFT, 0, 1));
			document.add(pdfsetting.para("4). The Proper Officer is required to monitor the progress of the case, maintain regular communication with the GP office, and ensure that all necessary updates and documents are provided promptly. This proactive monitoring and coordination will facilitate effective representation and timely disposal of cases before the Hon’ble High Court.", subhead, Paragraph.ALIGN_LEFT, 0, 1));
			// Add table to document


			document.close();
			System.out.println("PDF Created Successfully!table3");

			System.out.println("" + pdfFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (document != null)
				document.close();
		}
		return pdfFilePath;
	}

	public static PdfPCell createCell(String text, Font font, int alignment, Color bgColor) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(alignment);
		cell.setBackgroundColor(bgColor);
		cell.setPadding(5);
		return cell;
	}

	// Helper method to create table content rows
	public static void addRow(PdfPTable table, String col1, String col2, String col3) {
		Font contentFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
		table.addCell(createCell(col1, contentFont, Element.ALIGN_CENTER, Color.WHITE));
		table.addCell(createCell(col2, contentFont, Element.ALIGN_LEFT, Color.WHITE));
		table.addCell(createCell(col3, contentFont, Element.ALIGN_LEFT, Color.WHITE));
	}






}
