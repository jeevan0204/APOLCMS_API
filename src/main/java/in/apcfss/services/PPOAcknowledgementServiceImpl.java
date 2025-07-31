package in.apcfss.services;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.apcfss.common.Itext_pdf_setting;
import in.apcfss.entities.GPOAckDeptsEntity;
import in.apcfss.entities.GPOAckDetailsEntity;
import in.apcfss.entities.SMSCreationEntity;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.GPOAcknowledgementDeptsRepo;
import in.apcfss.repositories.GPOAcknowledgementDetailsRepo;
import in.apcfss.repositories.SMSCreationRepo;
import in.apcfss.repositories.SectionOfficerDetailsRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.GPOAckDetailsReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody.AckRespondentDTO;
import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PPOAcknowledgementServiceImpl implements PPOAcknowledgementService {

	@Value("${ack_files}")
	private String ackFiles;

	@Autowired
	HttpServletRequest request;

	@Autowired
	GPOAcknowledgementDetailsRepo repo;

	@Autowired
	GPOAcknowledgementDeptsRepo deptsRepo;

	@Autowired
	SMSCreationRepo smsCreationRepo;

	@Autowired
	SectionOfficerDetailsRepo sectionRepo;

	@Autowired
	UsersRepo usersRepo;

	@Autowired
	GPOAcknowledgementService service;

	@Autowired
	JdbcTemplate jdbcTemplate;


	String entryBy() {
		return request.getAttribute("username").toString();
	}

	String entryIp() {
		return request.getRemoteAddr();
	}

	public ResponseEntity<Map<String, Object>> savePPOAckDetails(Authentication authentication,GPOAckDetailsReqBody reqbody,
			HttpServletRequest request) {

		int respondentIds = 0, distId = 0;

		String  hcAckNo = null, ackNo = null, deptId = "", newStatusCode = "0" ;

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		List<Map<String, Object>> data = null;
		GPOAckDetailsEntity savedetailsEntity = null;
		GPOAckDeptsEntity savedeptEntity = null;

		try {
			respondentIds = reqbody.getRespSize();
			System.out.println("respondentIds................" + respondentIds);

			String userId = userPrincipal.getUserId();

			deptId = reqbody.getGpOackForm().get(0).getDispalyDept();
			distId = Integer.parseInt(reqbody.getDistId());
			String acktype = reqbody.getAckType() + "";
			System.out.println("acktype----:" + acktype);

			for (int respondentId = 0; respondentId < respondentIds; respondentId++) {
				System.out.println("for--------" + respondentId);

				if (reqbody.getGpOackForm().get(respondentId).getOtherDist() == null) {
					reqbody.getGpOackForm().get(respondentId).setOtherDist(0);
				}

				if (reqbody.getGpOackForm().get(respondentId).getMandal() == null) {
					reqbody.getGpOackForm().get(respondentId).setMandal(0);
				}
				if (reqbody.getGpOackForm().get(respondentId).getVillage() == null) {
					reqbody.getGpOackForm().get(respondentId).setVillage(0);
				}

				if (distId == 0) {
					distId = Integer.parseInt(reqbody.getDistId());
				}
			}

			System.out.println("deptId.........." + deptId);
			System.out.println("distId.........." + distId);

			if (distId > 0 && deptId != null && !deptId.equals("") && !deptId.equals("0")) {
				try {
					ackNo = repo.getAckNo(deptId, distId);
					hcAckNo = repo.getHCAckNo(deptId.substring(0, 5));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error occurred in repo calls: " + e.getMessage());
				}
			}
			System.out.println("ackNo--" + ackNo + "hck" + hcAckNo);

			String caseType = reqbody.getCaseType1() != null ? reqbody.getCaseType1().toString() : "0";    
			if (caseType == null || caseType.equalsIgnoreCase("null") || caseType.trim().isEmpty()) {
				caseType = "0";
			}

			Integer regyear = reqbody.getRegYear1() != null ? reqbody.getRegYear1() : 0;

			String mainCase =   reqbody.getMainCaseNo() != null ? reqbody.getMainCaseNo().toString() : "0";  

			if (mainCase == null || mainCase.equalsIgnoreCase("null") || mainCase.trim().isEmpty()) {
				caseType = "0";
			}

			String mainCaseNo = caseType + "/" + mainCase + "/" + regyear;
			System.out.println("mainCaseNo---" + caseType + "/" + mainCase + "/" + regyear);

			if (ackNo != null && !ackNo.contentEquals("") && hcAckNo != null && !hcAckNo.contentEquals("")) {

				GPOAckDetailsEntity detailsEntity = new GPOAckDetailsEntity();

				detailsEntity.setAckNo(ackNo);
				detailsEntity.setDistId(Integer.parseInt(reqbody.getDistId()));
				detailsEntity.setAdvocateName(reqbody.getAdvocateName() != null ? reqbody.getAdvocateName() : "");
				detailsEntity.setAdvocateCCno(reqbody.getAdvocateCCno() != null ? reqbody.getAdvocateCCno() : "");
				detailsEntity.setCaseType(reqbody.getCaseType() != null ? reqbody.getCaseType() : "0");

				if (mainCaseNo.contains("0//0")) {
					detailsEntity.setMainCaseNo(null);
				} else {
					detailsEntity.setMainCaseNo(mainCaseNo);
				}
				System.out.println("if------------");
				detailsEntity.setRemarks(reqbody.getRemarks() != null ? reqbody.getRemarks() : "");
				detailsEntity.setDeleteStatus(false);
				detailsEntity.setInsertedTime(new Timestamp(new Date().getTime()));
				detailsEntity.setInsertedBy(userId);
				detailsEntity.setInsertedIp(InetAddress.getByName(entryIp()));
				detailsEntity.setAckFilePath(ackFiles);
				detailsEntity.setGpId(reqbody.getGpId());
				detailsEntity.setPetitionerName(reqbody.getPetitionerName() != null ? reqbody.getPetitionerName() : "");

				detailsEntity.setServicesId(reqbody.getServicesId());
				detailsEntity.setServicesFlag(reqbody.getServicesFlag());
				detailsEntity.setBarcodeFilePath(reqbody.getBarcodeFilePath());
				detailsEntity.setAckType(reqbody.getAckType());

				detailsEntity.setRegYear(reqbody.getRegYear1());
				detailsEntity.setRegNo(reqbody.getCaseType1());
				detailsEntity.setFilingMode(reqbody.getFilingMode());
				detailsEntity.setCaseCategory(reqbody.getCaseCategory());
				detailsEntity.setHcAckNo(hcAckNo);

				if (acktype.equals("OLD")) {
					detailsEntity.setMaincasenoUpdated(mainCaseNo);
					detailsEntity.setAcknoUpdated("true");
				}
				if (acktype.equals("NEW") && !mainCaseNo.contains("0//0")) {
					detailsEntity.setMaincasenoUpdated(mainCaseNo);
					detailsEntity.setAcknoUpdated("true");
				}

				System.out.println("-------after if");

				detailsEntity.setCrimeNo(reqbody.getCrimeNo());
				detailsEntity.setCrimeYear(reqbody.getCrimeYear());
				detailsEntity.setPoliceStationName(reqbody.getStationId());
				detailsEntity.setBailPetitionType(reqbody.getBailPetitionType());
				detailsEntity.setCourtName(reqbody.getCourtName());
				detailsEntity.setChargeSheetNo(reqbody.getChargeSheetNo());
				detailsEntity.setSebName(reqbody.getSebStationId());

				savedetailsEntity = repo.save(detailsEntity);

				if (respondentIds > 0) {
					for (int j = 0; j < respondentIds; j++) {

						System.out.println("-------before"+j);
						GPOAckDeptsEntity ackDeptsEntity = new GPOAckDeptsEntity();
						System.out.println("selection------" + reqbody.getGpOackForm().get(j).getSectionSelection());

						if ((reqbody.getGpOackForm().get(j).getDepartmentId()).equals("Department")) {
							newStatusCode = null;
						}

						if (!(reqbody.getGpOackForm().get(j).getDispalyDept()).equals("0")) {
							ackDeptsEntity.setDeptCode(reqbody.getGpOackForm().get(j).getDispalyDept());
						}
						if (reqbody.getGpOackForm().get(j).getDispalyDept().equals("0")) {
							ackDeptsEntity.setDeptCode(reqbody.getGpOackForm().get(j).getEmpDept());
						}
						if (!reqbody.getGpOackForm().get(j).getDispalyDist().equals("0")) {
							ackDeptsEntity.setDistId(reqbody.getGpOackForm().get(j).getDispalyDist());
						}
						if (reqbody.getGpOackForm().get(j).getDispalyDist().equals("0")) {
							ackDeptsEntity.setDistId(reqbody.getGpOackForm().get(j).getOtherDist());
						}
						if ((reqbody.getGpOackForm().get(j).getDepartmentId()).equals("Department")) {
							ackDeptsEntity.setAssigned(false);

						}
						if ((reqbody.getGpOackForm().get(j).getDepartmentId()).equals("District")) {
							ackDeptsEntity.setAssigned(false);

						}
						if ((reqbody.getGpOackForm().get(j).getDepartmentId()).equals("Other")) {
							ackDeptsEntity.setAssigned(true);
						}

						ackDeptsEntity.setAckNo(ackNo);
						ackDeptsEntity.setRespondentSlno(j+1);

						System.out.println("other getEmployeeId id:" + (reqbody.getGpOackForm().get(j).getEmployeeId()));

						ackDeptsEntity.setServicetpye(reqbody.getGpOackForm().get(j).getServiceType());
						ackDeptsEntity.setDeptCategory((reqbody.getGpOackForm().get(j).getDeptCategory()));
						ackDeptsEntity.setDeptDistcoll(reqbody.getGpOackForm().get(j).getDepartmentId());
						ackDeptsEntity.setAssignedTo(reqbody.getGpOackForm().get(j).getEmployeeId());

						if (newStatusCode != null && !newStatusCode.trim().isEmpty()) {
							ackDeptsEntity.setCaseStatus(Integer.parseInt(newStatusCode));
						} else {
							ackDeptsEntity.setCaseStatus(0);
						}
						// ackDeptsEntity.setDeptCode();
						ackDeptsEntity.setDesignation("");
						ackDeptsEntity.setEcourtsCaseStatus("");
						ackDeptsEntity.setSection_officer_updated("");
						ackDeptsEntity.setMloNoUpdated("");
						ackDeptsEntity.setMandalid(0);
						ackDeptsEntity.setVillageid(0);

						System.out.println("deptCode: " + ackDeptsEntity.getDeptCode());
						System.out.println("distId: " + ackDeptsEntity.getDistId());
						System.out.println("caseStatus: " + ackDeptsEntity.getCaseStatus());
						System.out.println("ackNo: " + ackDeptsEntity.getAckNo());
						System.out.println("respondentSlno: " + ackDeptsEntity.getRespondentSlno());
						System.out.println("assignedTo: " + ackDeptsEntity.getAssignedTo());

						try {
							savedeptEntity = deptsRepo.save(ackDeptsEntity);
						} catch (Exception e) {
							System.err.println("Error while saving entity: " + e.getMessage());
							e.printStackTrace();
						}

						System.out.println("section slection empty");
					}
				}

			}
			if (savedeptEntity != null ) {
				data = deptsRepo.getListData(userId, ackNo);
				System.out.println("list data-------------:" + data);
				if (data != null && !data.isEmpty() && data.size() > 0) {
					reqbody.setAckNo(ackNo);
					reqbody.setDistId(data.get(0).get("distid").toString());
					reqbody.setAdvocateName(data.get(0).get("advocatename").toString());
					reqbody.setAdvocateCCno(data.get(0).get("advocateccno").toString());
					reqbody.setCaseType(data.get(0).get("casetype").toString());
					reqbody.setMainCaseNo(data.get(0).get("maincaseno").toString());
					reqbody.setRemarks(data.get(0).get("remarks").toString());
					reqbody.setFilingMode(data.get(0).get("mode_filing").toString());
					reqbody.setHcAckNo(data.get(0).get("hc_ack_no").toString());
					reqbody.setCaseTypeName(data.get(0).get("case_full_name").toString());
					reqbody.setDept_name(data.get(0).get("dept_descs").toString());
					reqbody.setDistrict_name(data.get(0).get("district_name").toString());
					reqbody.setGeneratedDate(data.get(0).get("generated_date").toString());
				}
				String ackPath = generateAckPdf(hcAckNo, reqbody);

				System.out.println("ack path=========" + ackPath);
				String barCodeFilePath = generateAckBarCodePdf128(hcAckNo, reqbody);
				System.out.println("ack path========" + barCodeFilePath);
				//					System.out.println("barCodeFilePath path" + barCodeFilePath);
				//					System.out.println("ackPath::" + ackPath);
				//					System.out.println("barCodeFilePath::" + barCodeFilePath);
				//
				//					if (ackPath != null && !ackPath.equals("")) {
				//
				//						int b = repo.updateFilepaths(ackPath, barCodeFilePath, ackNo);

				// if (b > 0 && emailIdCount > 0) {
				System.out.println("savedeptEntity------"+savedeptEntity);
				
				System.out.println("savedeptEntity------"+savedetailsEntity);

				if (savedeptEntity != null || savedetailsEntity != null ) {
					
					return CommonQueryAPIUtils.manualResponse("01",
							"Acknowledgement details saved successfully with Ack No.:" + ackNo);

				}  else {
					return CommonQueryAPIUtils.manualResponse("02", "Failed to save Data. Kindly try again.");
				}
			} else {
				return CommonQueryAPIUtils.manualResponse("02", "Invalid Acknowledgement No. Kindly try again.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CommonQueryAPIUtils.catchResponse(e);
		}

	}

	private String generateAckPdf(String ackNo, GPOAckDetailsReqBody reqbody) {
		Document document = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String fileName = ackNo + ".pdf";
		String pdfFilePath = ackNo;

		try {
			// PDF setup
			document = new Document(PageSize.A4, 30, 30, 30, 30);
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);

			// Footer setup
			BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
			Font footerFont = new Font(baseFont, 8, Font.NORMAL);
			HeaderFooter footer = new HeaderFooter(new Phrase("Page No.:" + document.getPageNumber(), footerFont), true);
			footer.setBorder(Rectangle.NO_BORDER);
			footer.setAlignment(Element.ALIGN_RIGHT);
			document.setFooter(footer);

			document.open();

			// Style sizes
			Itext_pdf_setting pdfSetting = new Itext_pdf_setting();
			int head = 14, subhead = 12, para = 11;

			// Header
			document.add(pdfSetting.para("ANDHRA PRADESH ONLINE LEGAL CASE MONITORING SYSTEM (APOLCMS)", head, Paragraph.ALIGN_CENTER, 90, 3));
			document.add(pdfSetting.para("GOVERNMENT OF ANDHRA PRADESH", subhead, Paragraph.ALIGN_CENTER, 0, 2));
			document.add(pdfSetting.para("____________________________________________________________________________", subhead, Paragraph.ALIGN_CENTER, 0, 2));
			document.add(pdfSetting.para("", para, Paragraph.ALIGN_JUSTIFIED, 8, 10));

			// Table for details
			PdfPTable table = pdfSetting.table(2, 100);
			pdfSetting.border = 0;

			// Adding rows
			table.addCell(pdfSetting.cell("Acknowledgement No. :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(ackNo, 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Date :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getGeneratedDate(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("District :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getDistrict_name(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Department :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getDept_name(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Advocate Name :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getAdvocateName(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Advocate CC No. :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getAdvocateCCno(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Case Type :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getCaseTypeName(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Mode of Filing :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getFilingMode(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Main Case No. :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getMainCaseNo(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			table.addCell(pdfSetting.cell("Remarks :", 1, Element.ALIGN_RIGHT, para, Font.BOLD));
			table.addCell(pdfSetting.cell(reqbody.getRemarks(), 1, Element.ALIGN_LEFT, para, Font.NORMAL));

			document.add(table);

			document.add(pdfSetting.para("", para, Paragraph.ALIGN_JUSTIFIED, 8, 10));
			document.add(pdfSetting.para("____________________________________________________________________________", subhead, Paragraph.ALIGN_CENTER, 0, 2));

			// Add barcode
			PdfContentByte cb = writer.getDirectContent();
			Barcode128 barcode = new Barcode128();
			barcode.setCode(ackNo);
			Image barcodeImage = barcode.createImageWithBarcode(cb, null, null);
			barcodeImage.scalePercent(100);
			barcodeImage.setAlignment(Element.ALIGN_CENTER);
			document.add(barcodeImage);

			// Close document
			document.close();

			// Upload PDF
			Map<String, Object> uploadResult = CommonQueryAPIUtils.uploadFileToAWSS3Bucket(outputStream.toByteArray(), fileName, ackFiles);

			if (Boolean.TRUE.equals(uploadResult.get("status"))) {
				System.out.println("File uploaded successfully: " + uploadResult.get("path"));
			} else {
				System.err.println("PDF upload failed: " + uploadResult);
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Log error to system logger if available
		} finally {
			if (document != null && document.isOpen()) {
				document.close();
			}
		}

		return pdfFilePath;
	}

	private String generateAckBarCodePdf128(String ackNo, GPOAckDetailsReqBody reqbody) {
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
			Map<String, Object> isUploadedBarcodeFile = CommonQueryAPIUtils.uploadFileToAWSS3Bucket(outputStream.toByteArray(), fileName, ackFiles);
			System.out.println("isUploadedFile----------" + isUploadedBarcodeFile);
			System.out.println("BAR CODE pdfFilePath:" + pdfFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdfFilePath;
	}

}
