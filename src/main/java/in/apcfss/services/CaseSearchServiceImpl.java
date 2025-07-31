package in.apcfss.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import in.apcfss.common.CommonModels;
import in.apcfss.common.Itext_pdf_setting;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.GPOAcknowledgementDeptsRepo;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class CaseSearchServiceImpl implements CaseSearchService {

	@Value("${ack_files}")
	private String ackFiles;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	GPOAcknowledgementDeptsRepo deptsRepo;
	@Override
	public List<Map<String, Object>> getCaseTypesList(Authentication authentication) {
		List<Map<String, Object>> sql=null;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

		sql=deptsRepo.GP_ecourts_case_type_master_new();
		List<String> ppDeoUserIds = Arrays.asList("PP-DEO-I", "PP-DEO-II", "PP-DEO-III", "PP-DEO-IV", "PP-DEO-V");
		if (ppDeoUserIds.contains(userId)) {

			sql=deptsRepo.PP_ecourts_case_type_master_new();
		}
		List<Map<String, Object>> responseList = new ArrayList<>();

		for (Map<String, Object> rawMap : sql) {
			Map<String, Object> map = new HashMap<>();
			for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
			responseList.add(map);
		}
		return responseList;
	}
	@Override
	public List<Map<String, Object>> getCasetype() {
		List<Map<String, Object>> sql=null;
		List<Map<String, Object>> responseList = new ArrayList<>();
		try {
			
			sql=deptsRepo.PP_List();

			for (Map<String, Object> rawMap : sql) {
				Map<String, Object> map = new HashMap<>();
				for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
				responseList.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseList;
	}
	@Override
	public List<Map<String, Object>> getCasesListData(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sql = null, sqlCondition = "", roleId = "", distId = "", deptCode = "";

		try {
			roleId = userPrincipal.getRoleId();
			deptCode = userPrincipal.getDeptCode();

			if (CommonModels.checkIntObject(abstractReqBody.getRegYear()) > 0) {
				sqlCondition += " and a.reg_year='" + CommonModels.checkIntObject(abstractReqBody.getRegYear()) + "' ";
			}
			if (!CommonModels.checkStringObject(abstractReqBody.getRegNo()).equals("")) {
				sqlCondition += " and a.reg_no='" + CommonModels.checkStringObject(abstractReqBody.getRegNo()) + "' ";
			}
			if (!CommonModels.checkStringObject(abstractReqBody.getCaseType()).equals("")
					&& !CommonModels.checkStringObject(abstractReqBody.getCaseType()).equals("0")) {
				sqlCondition += " and a.type_name_reg='" + CommonModels.checkStringObject(abstractReqBody.getCaseType())
				+ "' ";
			}
			if (!CommonModels.checkStringObject(abstractReqBody.getCino()).equals("")) {
				sqlCondition += " and a.cino='" + CommonModels.checkStringObject(abstractReqBody.getCino()) + "' ";
			}

			sql = "select a.*, b.orderpaths from ecourts_case_data a left join" + " ("
					+ " select cino, string_agg('<a href=\"./'||order_document_path||'\" target=\"_new\" class=\"btn btn-sm btn-info\"><i class=\"glyphicon glyphicon-save\"></i><span>'||order_details||'</span></a><br/>','- ') as orderpaths"
					+ " from "
					+ " (select * from (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_interimorder where order_document_path is not null and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) x1" + " union"
					+ " (select cino, order_document_path,order_date,order_details||' Dt.'||to_char(order_date,'dd-mm-yyyy') as order_details from ecourts_case_finalorder where order_document_path is not null"
					+ " and  POSITION('RECORD_NOT_FOUND' in order_document_path) = 0"
					+ " and POSITION('INVALID_TOKEN' in order_document_path) = 0 ) order by cino, order_date desc) c group by cino ) b"
					+ " on (a.cino=b.cino) where 1=1 " + sqlCondition + " ";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}
	@Override
	public String getCasesList(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody){
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String sql = "", sqlCondition = "", roleId = "", distId = "", deptCode = "";
		String cinoo = "0";
		try {
			roleId = userPrincipal.getRoleId();
			deptCode = userPrincipal.getDeptCode();

			Integer regYear = CommonModels.checkIntObject(abstractReqBody.getRegYear());
			String regNo = CommonModels.checkStringObject(abstractReqBody.getRegNo());
			String caseType = CommonModels.checkStringObject(abstractReqBody.getCaseType());
			String cino = CommonModels.checkStringObject(abstractReqBody.getCino());


			
			  if (regYear != null && regYear > 0) { sqlCondition += " and a.reg_year='" +
			  regYear + "' "; }
			  
			  if (regNo != null && !regNo.isEmpty()) { sqlCondition += " and a.reg_no='" +
			  regNo + "' "; }
			  
			  if (!caseType.isEmpty() && !caseType.equals("0")) { sqlCondition +=
			  " and a.type_name_reg='" + caseType + "' "; }
			  
			  if (!cino.isEmpty()) { sqlCondition += " and a.cino='" + cino + "' "; }
			 
			System.out.println("cino----"+cino+"sqlCondition---"+sqlCondition);
			sql = "select cino from ecourts_case_data a where 1=1 " + sqlCondition;

			//sql=deptsRepo.getCasesListExisting(regYear,regNo,caseType,cino);
			
			System.out.println("SQL:" + sql);

			System.out.println("cinoo:" + cinoo);

			cinoo = jdbcTemplate.queryForObject(sql, String.class);
			cinoo = StringUtils.defaultIfBlank(cinoo, "0");  // fallback if blank
		} catch (EmptyResultDataAccessException e) {
			cinoo = "0"; // fallback if no row returned
		}
		return cinoo;
	}

	public String generateAckBarCodePdf128(String ackNo, GPOAckDetailsReqBody reqbody) {

		System.out.println("generateAckBarCodePdf128");
		Document document = null;
		PdfWriter writer = null;
		String pdfFilePath = "", filepath = "";
		try {
			String fileName = ackNo + "_barCode-A1" + ".pdf";
			LocalDateTime da_ti2 = LocalDateTime.now();
			DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm:ss a");
			String testDateString = da_ti2.format(dtf1);

			Itext_pdf_setting pdfsetting = new Itext_pdf_setting();
			int subhead = 8;
			pdfFilePath = ackNo + "_barCode-A1";
			document = new Document(PageSize.A6.rotate());
			document.setMargins(10, 10, 10, 10);
			document.setPageSize(PageSize.A6);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			writer = PdfWriter.getInstance(document, outputStream);

			document.open();
			PdfContentByte cb = writer.getDirectContent();

			Barcode128 barcode128 = new Barcode128();
			document.add(pdfsetting.para("Acknowledgement No.:", subhead, Paragraph.ALIGN_LEFT, 0, 2));
			barcode128.setCode(ackNo);
			System.out.println("ack---------" + ackNo);

			Image code128Image = barcode128.createImageWithBarcode(cb, null, null);

			code128Image.scaleToFit(250f, 250f);
			code128Image.scaleAbsoluteHeight(50f);
			document.add(code128Image);
			document.add(pdfsetting.para(testDateString + "                         " + "APOLCMS", subhead,
					Paragraph.ALIGN_LEFT, 2, 2));
			// document.add(code39Image);

			document.close();
			//Map<String, Object> isUploadedBarcodeFile = CommonQueryAPIUtils.uploadFileToAWSS3Bucket(outputStream.toByteArray(), fileName, ackFiles);
			//System.out.println("isUploadedFile----------" + isUploadedBarcodeFile);
			System.out.println("BAR CODE pdfFilePath:" + pdfFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdfFilePath;
	}
	public Integer getUpdateBarcodeFile(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		Integer sql = null;
		GPOAckDetailsReqBody reqbody=new GPOAckDetailsReqBody();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String cinNo = getCasesList(authentication, abstractReqBody);
		String barCodeFilePath = generateAckBarCodePdf128(cinNo, reqbody);
		String userId = userPrincipal.getUserId()+ "";
		try {
			//sql = "update ecourts_case_data set barcode_file_path='" + barCodeFilePath + "' ,hc_scan_legacy_by='"
			//		+ userId + "', hc_scan_legacy_date='" + new Date()
			//		+ "',scanned_document_path='uploads/scandocs/'||'" + cinNo + "'||'/'||'" + cinNo
			//		+ "'||'.pdf'  where cino='" + cinNo + "'";
			System.out.println("cinNo---"+cinNo);

			String updated_file="uploads/scandocs/"+cinNo+"/"+cinNo+".pdf";
			sql=deptsRepo.getUpdateBarcodeFile(barCodeFilePath,userId,new Date(),updated_file,cinNo);
			System.out.println("sql---"+sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

}
