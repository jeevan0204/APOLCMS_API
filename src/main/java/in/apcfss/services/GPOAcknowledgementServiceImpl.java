package in.apcfss.services;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
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

import in.apcfss.common.CommonModels;
import in.apcfss.common.Itext_pdf_setting;
import in.apcfss.entities.GPOAckDeptsEntity;
import in.apcfss.entities.GPOAckDetailsEntity;
import in.apcfss.entities.SMSCreationEntity;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.model.ApplicationVariables;
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
public class GPOAcknowledgementServiceImpl implements GPOAcknowledgementService {

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

	@Override
	public ResponseEntity<Map<String, Object>> saveGPOAckForm(Authentication authentication,GPOAckDetailsReqBody reqbody,
			HttpServletRequest request) {

		int respondentIds = 0, distId = 0;
		int isDone = 0;
		int emailIdCount = 0;
		String employeeId = "", hcAckNo = null, ackNo = null, deptId = "", newStatusCode = "0", activityDesc = "",
				tableName = "nic_data";
		//String empDept = "", sectionSelection = "", otherDist = "", empSection = "", empPost = "";

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
			String newRoleId = "";

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

						if ((reqbody.getGpOackForm().get(j).getSectionSelection()).equals("Dist-Section")) { // Dist. - Section  Officer
							newStatusCode = "10";
							activityDesc = "CASE ASSSIGNED TO Section Officer (District)";
						}
						if ((reqbody.getGpOackForm().get(j).getSectionSelection()).equals("Sec-Section")) { // Sect.  Dept. -  Section  Officer
							newStatusCode = "5";
							activityDesc = "CASE ASSSIGNED TO Section Officer (Sect. Dept.)";
						}
						if ((reqbody.getGpOackForm().get(j).getSectionSelection()).equals("Hod-Section")) { // HOD -  Section  Officer.
							newStatusCode = "9";
							activityDesc = "CASE ASSSIGNED TO Section Officer (HOD)";
						}
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
							ackDeptsEntity.setCaseStatus(null);
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

						System.out.println(
								"--------------" + reqbody.getGpOackForm().get(j).getSectionSelection().isEmpty());
						if (reqbody.getGpOackForm().get(j).getSectionSelection() != null
								&& !reqbody.getGpOackForm().get(j).getSectionSelection().isEmpty()) {
							if ((reqbody.getGpOackForm().get(j).getSectionSelection()).equals("Dist-Section")
									|| (reqbody.getGpOackForm().get(j).getSectionSelection()).equals("Sec-Section")
									|| ((reqbody.getGpOackForm().get(j).getSectionSelection())).equals("Hod-Section")) {

								int DistIdForTable = reqbody.getGpOackForm().get(j).getOtherDist();
								System.out.println("DistIdForTable--------" + DistIdForTable);
								if (DistIdForTable > 0) {
									tableName = repo.getTableName(DistIdForTable);
								}
								System.out.println("tablename------" + tableName);
								System.out.println("email id====" + reqbody.getGpOackForm().get(j).getEmployeeId());
								String remarks = "DIRECT_CASE_ASSIGNMENT";
								System.out.println("insertEcourtsAckAssignmentDtls    "+tableName);
								isDone = deptsRepo.insertEcourtsAckAssignmentDtls(ackNo,
										reqbody.getGpOackForm().get(j).getEmpDept(),
										reqbody.getGpOackForm().get(j).getEmpSection(),
										reqbody.getGpOackForm().get(j).getEmpPost(),
										reqbody.getGpOackForm().get(j).getMandal(),
										reqbody.getGpOackForm().get(j).getVillage(),
										reqbody.getGpOackForm().get(j).getOtherDist(),
										reqbody.getGpOackForm().get(j).getEmployeeId(),
										new Timestamp(new Date().getTime()), InetAddress.getByName(entryIp()), userId);
								System.out.println("isdone----" + isDone);
								isDone += deptsRepo.insertEcourtsCaseActivities(ackNo, activityDesc, userId,
										InetAddress.getByName(entryIp()),
										reqbody.getGpOackForm().get(j).getEmployeeId(), remarks,
										reqbody.getGpOackForm().get(j).getOtherDist());

								System.out.println("isdone----" + isDone);

								emailIdCount = deptsRepo.isExistsEmp(reqbody.getGpOackForm().get(j).getEmployeeId());
								System.out.println("count ==" + emailIdCount);

								if (emailIdCount > 0) {

									CommonQueryAPIUtils.manualResponse("01",
											"Case successfully Assigned to Selected Employee.");
									System.out.println("Login Id Already Available in Users Table");

									// IF NEW FILE ENTRY IN APOLCMS SMS SENT EMPLOYEE

									String mobileNo = service.getMobileNo(tableName,
											reqbody.getGpOackForm().get(j).getEmployeeId());
									List<Map<String, Object>> data_req = deptsRepo.getFileName(ackNo);
									System.out.println("mobileNo-sms-"+mobileNo);
									String smsText = " Daily Cases Report: A New '"
											+ data_req.get(0).get("case_full_name").toString()
											+ "' has been registered in " + " the Department: '"
											+ data_req.get(0).get("dept_code").toString() + "' - '"
											+ data_req.get(0).get("description").toString() + "' in APOLCMS. "
											+ " Please visit the APOLCMS site for more details. -OLCMS, GOVTAP";

									System.out.println("case_full_name--------"
											+ data_req.get(0).get("case_full_name").toString());
									System.out
									.println("dept_code--------" + data_req.get(0).get("dept_code").toString());
									System.out.println(
											"description--------" + data_req.get(0).get("description").toString());

									SMSCreationEntity creationEntity = new SMSCreationEntity(ackNo,
											reqbody.getGpOackForm().get(j).getEmployeeId(), mobileNo, smsText,
											new Timestamp(new Date().getTime()), userId,
											InetAddress.getByName(entryIp()), false, false);
									smsCreationRepo.save(creationEntity);

									// ==================================SMS END=======================

								} else {

									if (reqbody.getGpOackForm().get(j).getSectionSelection().equals("Dist-Section")) { // Dist.
										// -//
										// Section
										// // Officer
										newRoleId = "12";
									} else if (reqbody.getGpOackForm().get(j).getSectionSelection()
											.equals("Sec-Section")) { // Sect.Dept.-Section
										// Officer
										newRoleId = "8";
									} else { // HOD - Section Officer
										newRoleId = "11";
									}
									// NEW SECTION OFFICER CREATION
									isDone += insertSectionOfficerDetails(userId, InetAddress.getByName(entryIp()),
											reqbody.getGpOackForm().get(j).getOtherDist(), tableName,
											reqbody.getGpOackForm().get(j).getEmpDept(),
											reqbody.getGpOackForm().get(j).getEmployeeId(),
											reqbody.getGpOackForm().get(j).getEmpSection(),
											reqbody.getGpOackForm().get(j).getEmpPost());

									isDone += insertUsers(userId, new Timestamp(new Date().getTime()),
											InetAddress.getByName(entryIp()), newRoleId,
											reqbody.getGpOackForm().get(j).getOtherDist(), tableName,
											reqbody.getGpOackForm().get(j).getEmpDept(),
											reqbody.getGpOackForm().get(j).getEmployeeId(),
											reqbody.getGpOackForm().get(j).getEmpSection(),
											reqbody.getGpOackForm().get(j).getEmpPost());

									String mobileNo = getMobileno(tableName,
											reqbody.getGpOackForm().get(j).getEmpDept(),
											reqbody.getGpOackForm().get(j).getEmployeeId(),
											reqbody.getGpOackForm().get(j).getEmpSection(),
											reqbody.getGpOackForm().get(j).getEmpPost());


									if (mobileNo != null && !mobileNo.isEmpty()) {
										mobileNo=mobileNo;
										System.out.println("Mobile No: " + mobileNo);
									} else {
										mobileNo="";
										System.out.println("No mobile number found.");
									}



									isDone += usersRepo.insertUserRoles(reqbody.getGpOackForm().get(j).getEmployeeId(),
											Integer.parseInt(newRoleId));

									String smsText = "Your User Id is " + reqbody.getGpOackForm().get(j).getEmployeeId()
											+ j
											+ " and Password is olcms@123 to Login to https://apolcms.ap.gov.in/ Portal. Please do not share with anyone. \r\n-APOLCMS";

									SMSCreationEntity creationEntity = new SMSCreationEntity(ackNo,
											reqbody.getGpOackForm().get(j).getEmployeeId(), mobileNo, smsText,
											new Timestamp(new Date().getTime()), userId,
											InetAddress.getByName(entryIp()), false, false);
									smsCreationRepo.save(creationEntity);

								}

							}
						} else {
							System.out.println("section slection empty");
						}
					}

				}
				if (savedeptEntity != null || isDone > 0) {
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


					if (savedeptEntity != null || savedetailsEntity != null || emailIdCount > 0) {
						System.out.println("hlooo");
						return CommonQueryAPIUtils.manualResponse("01",
								"Acknowledgement details saved successfully with Ack No.:" + ackNo
								+ "  (Case successfully Assigned to Selected Employee)");

					} else if (emailIdCount > 0 && isDone >= 3) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Acknowledgement details saved successfully with Ack No.:" + ackNo
								+ "(Cases successfully Assigned to Selected Employee & User Login created successfully. Login details sent to Registered Mobile No)");
						// CommonQueryAPIUtils.successResponse();
					} else if (emailIdCount > 0 && isDone < 1) {
						return CommonQueryAPIUtils.manualResponse("01",
								"Acknowledgement details saved successfully with Ack No.:" + ackNo);

					} else {
						return CommonQueryAPIUtils.manualResponse("02", "Failed to save Data. Kindly try again.");
					}
				} else {
					return CommonQueryAPIUtils.manualResponse("02", "Invalid Acknowledgement No. Kindly try again.");
				}

			} else
				return CommonQueryAPIUtils.manualResponse("02",
						"Error in saving Acknowledgement details. Kindly try again with valid data.");
			// request.setAttribute("saveAction", "INSERT");
			// } else {
			// return CommonQueryAPIUtils.manualResponse("02", "Invalid Acknowledgement No.
			// Kindly try again.");
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return CommonQueryAPIUtils.catchResponse(e);
		}

	}

	private String getMobileno(String tableName, String empDept, String employeeId, String empSection, String empPost) {
		String sql = "select distinct mobile1 from " + tableName + " b where b.email='" + employeeId + "' "
				+ "and trim(b.employee_identity)='" + empSection + "' and trim(b.post_name_en)='" + empPost
				+ "' and mobile1 is not null";

		System.out.println("sql--" + sql);

		try {
			return jdbcTemplate.queryForObject(sql, String.class);
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {

			System.out.println("No mobile number found for: " + employeeId);
			return null; // or return ""; depending on how you want to handle it
		}
	}

	private int insertUsers(String userId, Timestamp insertedTime, InetAddress insertedIp, String newRoleId,
			Integer otherDist, String tableName, String empDept, String employeeId, String empSection, String empPost) {
		String ipAddress = insertedIp.getHostAddress();

		String sql = "insert into users (userid, password,password_text, user_description, created_by, created_on, created_ip, dept_id, dept_code, user_type, dist_id) "
				+ "select distinct b.email, md5('olcms@123'),'olcms@123'," + " b.fullname_en,'" + userId + "','"
				+ insertedTime + "'::TIMESTAMP,'" + ipAddress + "'::INET,d.dept_id,d.dept_code,'" + newRoleId + "',"
				+ otherDist + " from " + tableName + " b " + " inner join dept_new d on (d.dept_code='" + empDept
				+ "') where b.email='" + employeeId + "' " + "and trim(b.employee_identity)='" + empSection
				+ "' and trim(b.post_name_en)='" + empPost + "'";
		return jdbcTemplate.update(sql);
	}

	private int insertSectionOfficerDetails(String userId, InetAddress byName, Integer otherDist, String tableName,
			String empDept, String employeeId, String empSection, String empPost) {

		String ipAddress = byName.getHostAddress();

		String sql = "insert into section_officer_details (emailid, dept_id,designation,employeeid,mobileno,aadharno,inserted_by,inserted_ip, dist_id) "
				+ " select distinct b.email,d.sdeptcode||d.deptcode,b.designation_id,b.employee_id,b.mobile1," + "uid,'"
				+ userId + "','" + ipAddress + "'::INET," + otherDist + " from " + tableName + " b "
				+ " inner join dept_new d on (d.dept_code='" + empDept + "') " + "where b.email='" + employeeId
				+ "' and trim(b.employee_identity)='" + empSection + "' and trim(b.post_name_en)='" + empPost + "' ";
		return jdbcTemplate.update(sql);
	}

	private String generateAckPdf(String ackNo, GPOAckDetailsReqBody reqbody) {
		Document document = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//String fileName = ackNo + ".pdf";
		//String pdfFilePath = ackNo;
		
		String fileName = ackNo + ".pdf";
		String pdfFilePath = ApplicationVariables.ackPath + fileName;
		//String FilePath1 = context.getRealPath("/") + pdfFilePath;
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
			/*
			 * Map<String, Object> uploadResult =
			 * CommonQueryAPIUtils.uploadFileToAWSS3Bucket(outputStream.toByteArray(),
			 * fileName, ackFiles);
			 * 
			 * if (Boolean.TRUE.equals(uploadResult.get("status"))) {
			 * System.out.println("File uploaded successfully: " +
			 * uploadResult.get("path")); } else { System.err.println("PDF upload failed: "
			 * + uploadResult); }
			 */

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
			
			pdfFilePath = ApplicationVariables.ackPath + fileName;
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

	@Override
	public String getMobileNo(String tableName, String employeeId) {
		 
		String sql = "select distinct mobile1 from " + tableName + " where email='" + employeeId + "'";
		System.out.println("sql---" + sql);
		return jdbcTemplate.queryForObject(sql, String.class);
	}

	@Override
	public List<Map<String, Object>> getAcknowledementsList(UserDetailsImpl userPrincipal) {

		List<Map<String, Object>> response = null;
		String sql = null,userId="" ;
		userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String ackType = "NEW";
		response = repo.getAcknowledementsList(userId,ackType);
		return response;
	}

	@Override
	public List<Map<String, Object>> gpoAcknowledementsListAll(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,String ackDate,String ackType) {

		String sql = null;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String UserId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		try {

			String sql_add = "";
			if (!UserId.equals("HC-DEO-ADMIN")) {

				sql_add += " a.inserted_by='" + UserId + "' and";
			} else {
				// sql_add+="and hc_scan_legacy_by='"+UserId+"'";
			}

			System.out.println("ackType--"+ackType);


			sql = "select slno , a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip,"
					+ " upper(trim(dm.district_name)) as district_name,  upper(trim(case_full_name)) as  case_full_name, a.ack_file_path,"
					+ " case when services_id='0' then null else services_id end as services_id,services_flag,"
					+ " STRING_AGG(distinct gd.dept_code,',') as dept_codes,"
					+ " STRING_AGG(distinct gd.description||'-'||gd.servicetpye||case when coalesce(gd.dept_category,'0')!='0' then '-'||gd.dept_category else '' end ,', ') as dept_descs,"
					+ "STRING_AGG(distinct upper(trim(ead2.district_name))||'-'||ead2.emp_section||'-'|| ead2.emp_post||'-'|| ead2.emp_user_id, ',') AS other_selection_types,"
					+ " a.barcode_file_path, to_char(inserted_time,'dd-mm-yyyy') as generated_date,"
					+ " mode_filing, case_category, coalesce(a.hc_ack_no,'-') as hc_ack_no"
					+ " from ecourts_gpo_ack_dtls a" + " left join district_mst dm on (a.distid=dm.district_id)"
					+ " left join case_type_master cm on (a.casetype=cm.sno::text or a.casetype=cm.case_short_name)"
					+ " left join (select ack_no,respondent_slno,a1.dept_code,"
					+ " case when dm1.description is not null then dm1.description when dm2.district_id is not null then 'District Collector, '||dm2.district_name end as description ,servicetpye , coalesce(dept_category,'0') as dept_category"
					+ " from ecourts_gpo_ack_depts a1 left join dept_new dm1 on (a1.dept_code=dm1.dept_code)"
					+ " left join district_mst dm2 on (a1.dist_id=dm2.district_id)"
					+ " order by respondent_slno) gd on (a.ack_no=gd.ack_no) left join"
					+ " (select ackno,emp_section,eaad.dept_code,emp_post,emp_user_id, dm3.district_name,"
					+ " case when dn2.description is not null then dn2.description when dm3.district_id is not null then 'District Collector, '||dm3.district_name end as description"
					+ " from ecourts_ack_assignment_dtls eaad "
					+ " left join dept_new dn2  on (eaad.dept_code=dn2.dept_code) "
					+ " left join district_mst dm3 on (eaad.dist_id=dm3.district_id)) ead2 on (a.ack_no=ead2.ackno)"
					+ " where " + sql_add + "  a.delete_status is false ";

			if (!CommonModels.checkStringObject(ackType).equals("") && !CommonModels.checkStringObject(ackType).equals("0"))
				sql += " and ack_type='" + ackType + "'";

			sql += " and to_char(inserted_time::date,'dd-mm-yyyy')='" + ackDate + "' and a.inserted_by like 'HC%'"
					+ "group by slno , a.ack_no , distid , advocatename ,advocateccno , casetype , maincaseno , remarks ,  inserted_by , inserted_ip,dm.district_name,"
					+ "case_full_name,a.ack_file_path, services_id, services_flag, inserted_time, a.barcode_file_path, reg_year, reg_no, ack_type,a.mode_filing,a.case_category "
					+ "order by inserted_time desc";

			System.out.println("SQL:" + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getExistingCaseDetails(Authentication authentication,HCCaseStatusAbstractReqBody abstractReqBody,String ackDate,String ackType) {

		String sql = null;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String UserId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
		String sql_add = "";
		if (!UserId.equals("HC-DEO-ADMIN")) {

			sql_add += "and hc_scan_legacy_by='" + UserId + "'";
		} else {
			sql_add+="";
		}

		sql = "select cino,scanned_document_path,date_of_filing,type_name_fil,reg_no,reg_year,fil_year,date_next_list,dist_name,"
				+ " hc_scan_legacy_by from ecourts_case_data where hc_scan_legacy_date='"
				+ ackDate + "' " + sql_add + "  and hc_scan_legacy_by not like '%PP%'";
		System.out.println("SQL:" + sql);
		return jdbcTemplate.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getDisplayAckEditFormList(Authentication authentication,String ackNo) {

		List<Map<String, Object>> response = null;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId=userPrincipal.getUserId();

		System.out.println("userId===>"+userId+"ackNo===========>"+ackNo);
		response = repo.getDisplayAckEditFormList(userId,ackNo);
		return response;
	}

	@Override
	public List<Map<String, Object>> getData2(String ackNo) {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> response = null;
		response = repo.getData2(ackNo);
		map.put("ackId", ackNo);
		return response;
	}
	@Override
	public String getUpdateAckDetails(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		String message = "";
		String ackNo = null; 
		//int respondentIds = 0;
		int count = 0;
		int k1 = 0;
		try {
			ackNo = abstractReqBody.getAckNo() != null ? abstractReqBody.getAckNo().toString() : "";
			System.out.println("ackNo==up===>" + abstractReqBody.getAckNo());

			if (ackNo != null && !ackNo.contentEquals("")) {

				String caseType = abstractReqBody.getCaseType1() != null ? abstractReqBody.getCaseType1().toString() : "0";   //abstractReqBody.getCaseType1().toString();
				String regyear = abstractReqBody.getRegYear1() != null ? abstractReqBody.getRegYear1().toString() : "0";
				String mainCase =   abstractReqBody.getMainCaseNo() != null ? abstractReqBody.getMainCaseNo().toString() : "0"; //abstractReqBody.getMainCaseNo().toString();


				String distId =abstractReqBody.getDistId() != null ? abstractReqBody.getDistId().toString() : "0";
				int distIdInt=0;
				if (distId != null && !distId.isEmpty()) {
					distIdInt = Integer.parseInt(distId);
				}

				String mainCaseNo = caseType + "/" + mainCase + "/" + regyear;
				String ackno_updated=null;

				if (!mainCaseNo.contains("0//0")) {
					mainCaseNo = caseType + "/" + mainCase + "/" + regyear;
					ackno_updated="true";
				}else {
					mainCaseNo = null;
					ackno_updated= null;
				}

				System.out.println("mainCaseNo===>" + mainCaseNo);

				count = repo.updateEcourts_gpo_ack_dtls(distIdInt,abstractReqBody.getPetitionerName(),abstractReqBody.getAdvocateCCno(),abstractReqBody.getAdvocateName(),
						abstractReqBody.getCaseCategory(),abstractReqBody.getCaseType(),abstractReqBody.getFilingMode(),abstractReqBody.getMainCaseNo(),abstractReqBody.getMainCaseNo(),
						ackno_updated,abstractReqBody.getRemarks(),abstractReqBody.getCrimeNo() ,abstractReqBody.getCrimeYear() ,abstractReqBody.getStationId() ,
						abstractReqBody.getBailPetitionType() ,abstractReqBody.getCourtName() ,abstractReqBody.getChargeSheetNo() ,abstractReqBody.getSebStationId(),
						ackNo);
				System.out.println("count sql========>" + count);
				List<AckRespondentDTO>	gpOackForm=abstractReqBody.getGpOackForm();
				System.out.println("respondentIds sql========>" + gpOackForm);
				int distInt=0;
				if (gpOackForm.size() > 0) {
					System.out.println("ack no new =>" + ackNo);

					int c  = repo.ecourts_gpo_ack_depts_bk(request.getRemoteAddr(),ackNo);

					System.out.println("c sql========>" + c);

					int d = 0;
					if (c > 0) {

						d  = repo.deleteEcourts_gpo_ack_depts(ackNo);
					}
					System.out.println("d sql========>" + d);

					if (d >= 0) {

						System.out.println("respondentList========>" +gpOackForm.size());

						for (int i = 0; i < gpOackForm.size(); i++) {
							AckRespondentDTO dto = gpOackForm.get(i);
							int respondentId = i+1;

							Integer newStatusCode = 0;

							String sectionSelection ="";

							boolean assigned = false;

							sectionSelection=dto.getSectionSelection();


							if ("Dist-Section".equals(sectionSelection)) {  //Dist-Section

								newStatusCode = 10;
							}

							if ("Sec-Section".equals(sectionSelection)) {  // Sect.  Dept. -  Section  Officer

								newStatusCode = 5;
							}

							if ("Hod-Section".equals(sectionSelection)) {  // HOD -  Section  Officer.

								newStatusCode = 9;
							}

							if ("Department".equals(dto.getDepartmentId())) {

								newStatusCode = 0;
							}

							if ("Department".equals(dto.getDepartmentId())) {

								assigned = false;
							}

							if ("District".equals(dto.getDepartmentId())) {

								assigned = false;
							}

							if ("Other".equals(dto.getDepartmentId())) {

								assigned = true;
							}

							System.out.println("newStatusCode---"+newStatusCode);

							System.out.println("sectionSelection---"+sectionSelection);

							System.out.println("assigned---"+assigned);

							if (dto.getDispalyDist() != null && !dto.getDispalyDist().isEmpty()) {
								distInt = Integer.parseInt(dto.getDispalyDist());
							}
							System.out.println("respondentId--"+respondentId);
							k1 = repo.ecourts_gpo_ack_depts_Loop(
									ackNo,
									dto.getDispalyDept(),
									respondentId,
									assigned,
									newStatusCode,
									dto.getEmployeeId(),
									dto.getServiceType(),
									dto.getDeptCategory(),
									dto.getDepartmentId(),
									distInt
									);
						}
					}
				}
				System.out.println("k1===>"+count+"=========" + k1);
				if (count > 0 && k1 > 0) {

					message="Ack No.:" + ackNo + " details updated successfully.";
				} else {

					message="Error in saving Acknowledgement details. Kindly try again with valid data.";
				}
			} else {
				message="Invalid Acknowledgement No. Kindly try again.";
			}
		} catch (Exception e) {
			message="Exception Occurred while saving Acknowledgement details. Kindly try again with valid data.";
			e.printStackTrace();
		}
		return message;

	}
	@Override
	public Integer getDeleteAckDetails(Authentication authentication,  
			String ackNo) {
		int a = 0;
		//String ackNo = abstractReqBody.getAckNo()!= null ? abstractReqBody.getAckNo().toString() : "";

		System.out.println("DeleteAckDetails ackNo--->"+ackNo);

		try {

			a = repo.DeleteBAck_ecourts_gpo_ack_depts_log(ackNo);

			a += repo.DeleteBAck_ecourts_gpo_ack_dtls_log(ackNo);

			a += repo.Delete_ecourts_gpo_ack_depts(ackNo);

			a += repo.Delete_ecourts_gpo_ack_dtls(ackNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;

	}
}
