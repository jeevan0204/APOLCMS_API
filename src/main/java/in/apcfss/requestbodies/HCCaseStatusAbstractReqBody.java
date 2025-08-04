package in.apcfss.requestbodies;

import java.util.List;

import in.apcfss.controllers.ServiceAssignment;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HCCaseStatusAbstractReqBody {

	private String regYear;
	private String dofFromDate;
	private String dofToDate;
	private String petitionerName;
	private String respodentName;
	private String categoryServiceId;
	private String caseTypeId;
	private String caseTypeIdIds;
	
	private String deptId;
	private String deptIdIds;
	private String res_adv_Id;
	
	private String purpose;
	private String distId;
	private String judgeName;
	private String selectedCaseIds;
	private String caseDept;
	private String distDept;
	private int caseDist;
	private int caseDist1;
	private String caseRemarks;
	private String officerType;
	private String mloSubjectId;
	private String empDept;
	private String empSection;
	private String empPost;
	private String employeeId;
	private String actionType;
	private String districtId;
	private String cino;
	private String fileCino;
	private String SHOWPOPUP;
	private int resident_id;
	
	private String pwCounterFlag;
	private String petitionDocumentOld;
	private String counterFileCopyOld;
	private String judgementOrderOld;
	private String actionTakenOrderOld;
	private String counterFiled;
	private String remarks;
	private String ecourtsCaseStatus;
	private String parawiseRemarksSubmitted;
	private String parawiseRemarksCopyOld;
	private String parawiseRemarksDt;
	private String dtPRReceiptToGP;
	private String pwr_gp_approved;
	private String dtPRApprovedToGP;
	private String appealFiled;
	private String appealFileCopyOld;
	private String appealFiledDt;
	private String actionToPerform;
	private String counter_pw_flag;
	private String dismissedFileCopyOld;
	private String dismissedFileCopy;
	private String petitionDocument;
	private String actionTakenOrder;
	private String judgementOrder;
	private String appealFileCopy;
	private String implementedDt;
	//private List<String> petitionDocument;
	//private String counterFileCopy;
	private List<String> counterFileCopy;
	//private String counterFileCopy2;
	//private String counterFileCopy3;
	
	private String counterFiledDt;
	private String counterFiledDocument;
	
	private List<String> parawiseRemarksCopy;

 
	private String relatedGp;
	
	private String gpCode;
	
	private String district_name;
	
	private String remarks2;
	
	private String counter_approved_gp;
	
	private String serno;
	private String pwr_uploaded_copy;
	private String counter_filed_document;
	private String daily_status;
	private String changeLetter;
	private String sendBack_dept_code;
	
	private String employeeName;
	private String employeeCode;
	private String mobileNo;
	private String emailId;
	private String aadharNo;
	private String designationId;
	private String userType;
	private String type;
	private String policeStation;
	private String firNo;
	private String resName;
	private String petName;
	private String regYear2;
	private String regYear3;
	private String resAdv;
	private String petAdv;
	private String regYear4;
	private String filNo;
	private String filYear;
	 
	public String getPoliceStation() {
		return policeStation;
	}
	public void setPoliceStation(String policeStation) {
		this.policeStation = policeStation;
	}
	public String getFirNo() {
		return firNo;
	}
	public void setFirNo(String firNo) {
		this.firNo = firNo;
	}
	public String getResName() {
		return resName;
	}
	public void setResName(String resName) {
		this.resName = resName;
	}
	public String getPetName() {
		return petName;
	}
	public void setPetName(String petName) {
		this.petName = petName;
	}
	public String getRegYear3() {
		return regYear3;
	}
	public void setRegYear3(String regYear3) {
		this.regYear3 = regYear3;
	}
	public String getResAdv() {
		return resAdv;
	}
	public void setResAdv(String resAdv) {
		this.resAdv = resAdv;
	}
	public String getPetAdv() {
		return petAdv;
	}
	public void setPetAdv(String petAdv) {
		this.petAdv = petAdv;
	}
	public String getRegYear4() {
		return regYear4;
	}
	public void setRegYear4(String regYear4) {
		this.regYear4 = regYear4;
	}
	public String getFilNo() {
		return filNo;
	}
	public void setFilNo(String filNo) {
		this.filNo = filNo;
	}
	public String getFilYear() {
		return filYear;
	}
	public void setFilYear(String filYear) {
		this.filYear = filYear;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getAadharNo() {
		return aadharNo;
	}
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}


	//============================NEW CASES===============================//
	private String ackNo;
	private String advocateCCno;
	private String advocateName;
	private String caseCategory;
	private String caseType;
	private String filingMode;
	//private String regYear;
	private String regNo;
	private String caseType1;
	private String regYear1;
	private String mainCaseNo;
	private String ackType;
	
	private List<AckRespondentDTO> gpOackForm;
	 
	private List<ServiceAssignment> assignments;
 
	private String deptCategory;

	private String caseFullName;
	private String advocateCode;
	
	
	
	private String crimeNo;
	
	private int crimeYear;
	
	private String stationId;
	private String sebStationId;
	
	private String bailPetitionType;
	
	private String courtName;
	
	private String chargeSheetNo;
	
	private String fromDate;
	private String toDate;
	
	private String advcteName;
	private String oldNewType;
	private String ackNoo;
	
	private String instructions;
	private String slno1;
	private String slno2;
	private String slno3a;
	private String slno3b;
	private String slno3c;
	private String slno3d;
	private String slno3e;
	private String slno4;
	private String slno5;
	private String slno6;
	private String slno7;
	private String slno8;
	private String slno8a;
	private String slno8b;
	private String slno9;
	private String slno10;
	private String slno11;
	private String slno12;
	private String slno13;
	private String slno14;
	

	private String case_type;
	private int case_year;
	private int case_number;
	private String mloId;
	private String subjectDesc;
	private String deptName;
	private String flag;
	
	public String getSlno1() {
		return slno1;
	}
	public String getCase_type() {
		return case_type;
	}
	public void setCase_type(String case_type) {
		this.case_type = case_type;
	}
	public int getCase_year() {
		return case_year;
	}
	public void setCase_year(int case_year) {
		this.case_year = case_year;
	}
	public int getCase_number() {
		return case_number;
	}
	public void setCase_number(int case_number) {
		this.case_number = case_number;
	}
	public void setSlno1(String slno1) {
		this.slno1 = slno1;
	}
	public String getSlno2() {
		return slno2;
	}
	public void setSlno2(String slno2) {
		this.slno2 = slno2;
	}
	public String getSlno3a() {
		return slno3a;
	}
	public void setSlno3a(String slno3a) {
		this.slno3a = slno3a;
	}
	public String getSlno3b() {
		return slno3b;
	}
	public void setSlno3b(String slno3b) {
		this.slno3b = slno3b;
	}
	public String getSlno3c() {
		return slno3c;
	}
	public void setSlno3c(String slno3c) {
		this.slno3c = slno3c;
	}
	public String getSlno3d() {
		return slno3d;
	}
	public void setSlno3d(String slno3d) {
		this.slno3d = slno3d;
	}
	public String getSlno3e() {
		return slno3e;
	}
	public void setSlno3e(String slno3e) {
		this.slno3e = slno3e;
	}
	public String getSlno5() {
		return slno5;
	}
	public void setSlno5(String slno5) {
		this.slno5 = slno5;
	}
	public String getSlno6() {
		return slno6;
	}
	public void setSlno6(String slno6) {
		this.slno6 = slno6;
	}
	public String getSlno7() {
		return slno7;
	}
	public void setSlno7(String slno7) {
		this.slno7 = slno7;
	}
	public String getSlno8() {
		return slno8;
	}
	public void setSlno8(String slno8) {
		this.slno8 = slno8;
	}
	public String getSlno8a() {
		return slno8a;
	}
	public void setSlno8a(String slno8a) {
		this.slno8a = slno8a;
	}
	public String getSlno8b() {
		return slno8b;
	}
	public void setSlno8b(String slno8b) {
		this.slno8b = slno8b;
	}
	public String getSlno9() {
		return slno9;
	}
	public void setSlno9(String slno9) {
		this.slno9 = slno9;
	}
	public String getSlno10() {
		return slno10;
	}
	public void setSlno10(String slno10) {
		this.slno10 = slno10;
	}
	public String getSlno11() {
		return slno11;
	}
	public void setSlno11(String slno11) {
		this.slno11 = slno11;
	}
	public String getSlno12() {
		return slno12;
	}
	public void setSlno12(String slno12) {
		this.slno12 = slno12;
	}
	public String getSlno13() {
		return slno13;
	}
	public void setSlno13(String slno13) {
		this.slno13 = slno13;
	}
	public String getSlno14() {
		return slno14;
	}
	public void setSlno14(String slno14) {
		this.slno14 = slno14;
	}
	public String getRegYear() {
		return regYear;
	}
	public void setRegYear(String regYear) {
		this.regYear = regYear;
	}


	public String getPetitionerName() {
		return petitionerName;
	}
	public String getAdvocateCCno() {
		return advocateCCno;
	}
	public void setAdvocateCCno(String advocateCCno) {
		this.advocateCCno = advocateCCno;
	}
	public String getAdvocateName() {
		return advocateName;
	}
	public void setAdvocateName(String advocateName) {
		this.advocateName = advocateName;
	}
	public String getCaseCategory() {
		return caseCategory;
	}
	public void setCaseCategory(String caseCategory) {
		this.caseCategory = caseCategory;
	}
	public String getCaseType() {
		return caseType;
	}
	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}
	public String getFilingMode() {
		return filingMode;
	}
	public void setFilingMode(String filingMode) {
		this.filingMode = filingMode;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getCaseType1() {
		return caseType1;
	}
	public void setCaseType1(String caseType1) {
		this.caseType1 = caseType1;
	}
	public String getRegYear1() {
		return regYear1;
	}
	public void setRegYear1(String regYear1) {
		this.regYear1 = regYear1;
	}
	public String getMainCaseNo() {
		return mainCaseNo;
	}
	public void setMainCaseNo(String mainCaseNo) {
		this.mainCaseNo = mainCaseNo;
	}
	public void setPetitionerName(String petitionerName) {
		this.petitionerName = petitionerName;
	}
	public String getRespodentName() {
		return respodentName;
	}
	public void setRespodentName(String respodentName) {
		this.respodentName = respodentName;
	}
	public String getCategoryServiceId() {
		return categoryServiceId;
	}
	public void setCategoryServiceId(String categoryServiceId) {
		this.categoryServiceId = categoryServiceId;
	}
	public String getCaseTypeId() {
		return caseTypeId;
	}
	public void setCaseTypeId(String caseTypeId) {
		this.caseTypeId = caseTypeId;
	}
	
	
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDofFromDate() {
		return dofFromDate;
	}
	public void setDofFromDate(String dofFromDate) {
		this.dofFromDate = dofFromDate;
	}
	public String getDofToDate() {
		return dofToDate;
	}
	public void setDofToDate(String dofToDate) {
		this.dofToDate = dofToDate;
	}
	public String getDistId() {
		return distId;
	}
	public void setDistId(String distId) {
		this.distId = distId;
	}
	public String getJudgeName() {
		return judgeName;
	}
	public void setJudgeName(String judgeName) {
		this.judgeName = judgeName;
	}
	 
	public String getCaseDept() {
		return caseDept;
	}
	public void setCaseDept(String caseDept) {
		this.caseDept = caseDept;
	}
	public String getMloSubjectId() {
		return mloSubjectId;
	}
	public void setMloSubjectId(String mloSubjectId) {
		this.mloSubjectId = mloSubjectId;
	}
	public String getEmpDept() {
		return empDept;
	}
	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}

	
	

	public String getSelectedCaseIds() {
		return selectedCaseIds;
	}
	public void setSelectedCaseIds(String selectedCaseIds) {
		this.selectedCaseIds = selectedCaseIds;
	}
	public String getEmpSection() {
		return empSection;
	}
	public void setEmpSection(String empSection) {
		this.empSection = empSection;
	}
	public String getEmpPost() {
		return empPost;
	}
	public void setEmpPost(String empPost) {
		this.empPost = empPost;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getCaseRemarks() {
		return caseRemarks;
	}
	public void setCaseRemarks(String caseRemarks) {
		this.caseRemarks = caseRemarks;
	}
	public int getCaseDist() {
		return caseDist;
	}
	public void setCaseDist(int caseDist) {
		this.caseDist = caseDist;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getOfficerType() {
		return officerType;
	}
	public void setOfficerType(String officerType) {
		this.officerType = officerType;
	}
	public String getDistDept() {
		return distDept;
	}
	public void setDistDept(String distDept) {
		this.distDept = distDept;
	}
	public int getCaseDist1() {
		return caseDist1;
	}
	public void setCaseDist1(int caseDist1) {
		this.caseDist1 = caseDist1;
	}

	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	 
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	 
	public String getFileCino() {
		return fileCino;
	}
	public void setFileCino(String fileCino) {
		this.fileCino = fileCino;
	}
	public String getCino() {
		return cino;
	}
	public void setCino(Object object) {
		this.cino = (String) object;
	}
	public String getSHOWPOPUP() {
		return SHOWPOPUP;
	}
	public void setSHOWPOPUP(String sHOWPOPUP) {
		SHOWPOPUP = sHOWPOPUP;
	}
	public String getPetitionDocumentOld() {
		return petitionDocumentOld;
	}
	public void setPetitionDocumentOld(String petitionDocumentOld) {
		this.petitionDocumentOld = petitionDocumentOld;
	}
	public String getCounterFileCopyOld() {
		return counterFileCopyOld;
	}
	public void setCounterFileCopyOld(String counterFileCopyOld) {
		this.counterFileCopyOld = counterFileCopyOld;
	}
	public String getJudgementOrderOld() {
		return judgementOrderOld;
	}
	public void setJudgementOrderOld(String judgementOrderOld) {
		this.judgementOrderOld = judgementOrderOld;
	}
	public String getActionTakenOrderOld() {
		return actionTakenOrderOld;
	}
	public void setActionTakenOrderOld(String actionTakenOrderOld) {
		this.actionTakenOrderOld = actionTakenOrderOld;
	}
	public String getCounterFiled() {
		return counterFiled;
	}
	public void setCounterFiled(String counterFiled) {
		this.counterFiled = counterFiled;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getEcourtsCaseStatus() {
		return ecourtsCaseStatus;
	}
	public void setEcourtsCaseStatus(String ecourtsCaseStatus) {
		this.ecourtsCaseStatus = ecourtsCaseStatus;
	}
	public String getParawiseRemarksSubmitted() {
		return parawiseRemarksSubmitted;
	}
	public void setParawiseRemarksSubmitted(String parawiseRemarksSubmitted) {
		this.parawiseRemarksSubmitted = parawiseRemarksSubmitted;
	}
	public String getParawiseRemarksCopyOld() {
		return parawiseRemarksCopyOld;
	}
	public void setParawiseRemarksCopyOld(String parawiseRemarksCopyOld) {
		this.parawiseRemarksCopyOld = parawiseRemarksCopyOld;
	}
	public String getParawiseRemarksDt() {
		return parawiseRemarksDt;
	}
	public void setParawiseRemarksDt(String parawiseRemarksDt) {
		this.parawiseRemarksDt = parawiseRemarksDt;
	}
	public String getDtPRReceiptToGP() {
		return dtPRReceiptToGP;
	}
	public void setDtPRReceiptToGP(String dtPRReceiptToGP) {
		this.dtPRReceiptToGP = dtPRReceiptToGP;
	}
	public String getPwr_gp_approved() {
		return pwr_gp_approved;
	}
	public void setPwr_gp_approved(String pwr_gp_approved) {
		this.pwr_gp_approved = pwr_gp_approved;
	}
	public String getDtPRApprovedToGP() {
		return dtPRApprovedToGP;
	}
	public void setDtPRApprovedToGP(String dtPRApprovedToGP) {
		this.dtPRApprovedToGP = dtPRApprovedToGP;
	}
	public String getAppealFiled() {
		return appealFiled;
	}
	public void setAppealFiled(String appealFiled) {
		this.appealFiled = appealFiled;
	}
	public String getAppealFileCopyOld() {
		return appealFileCopyOld;
	}
	public void setAppealFileCopyOld(String appealFileCopyOld) {
		this.appealFileCopyOld = appealFileCopyOld;
	}
	public String getAppealFiledDt() {
		return appealFiledDt;
	}
	public void setAppealFiledDt(String appealFiledDt) {
		this.appealFiledDt = appealFiledDt;
	}
	
	
	
	 
	public String getActionToPerform() {
		return actionToPerform;
	}
	public void setActionToPerform(String actionToPerform) {
		this.actionToPerform = actionToPerform;
	}
	public String getPwCounterFlag() {
		return pwCounterFlag;
	}
	public void setPwCounterFlag(String pwCounterFlag) {
		this.pwCounterFlag = pwCounterFlag;
	}
	public String getCounter_pw_flag() {
		return counter_pw_flag;
	}
	public void setCounter_pw_flag(String counter_pw_flag) {
		this.counter_pw_flag = counter_pw_flag;
	}
	 
	 

	public String getCounterFiledDt() {
		return counterFiledDt;
	}

	public void setCounterFiledDt(String counterFiledDt) {
		this.counterFiledDt = counterFiledDt;
	}

	public String getCounterFiledDocument() {
		return counterFiledDocument;
	}

	public void setCounterFiledDocument(String counterFiledDocument) {
		this.counterFiledDocument = counterFiledDocument;
	}

	 

	public String getRelatedGp() {
		return relatedGp;
	}

	public void setRelatedGp(String relatedGp) {
		this.relatedGp = relatedGp;
	}

	public String getPetitionDocument() {
		return petitionDocument;
	}

	public void setPetitionDocument(Object object) {
		this.petitionDocument = (String) object;
	}

	public String getActionTakenOrder() {
		return actionTakenOrder;
	}

	public void setActionTakenOrder(Object object) {
		this.actionTakenOrder = (String) object;
	}

	public String getJudgementOrder() {
		return judgementOrder;
	}

	public void setJudgementOrder(Object object) {
		this.judgementOrder = (String) object;
	}

	public String getAppealFileCopy() {
		return appealFileCopy;
	}

	public void setAppealFileCopy(Object object) {
		this.appealFileCopy = (String) object;
	}
	public List<String> getCounterFileCopy() {
		return counterFileCopy;
	}
	public void setCounterFileCopy(List<String> counterFileCopy) {
		this.counterFileCopy = counterFileCopy;
	}
	public List<String> getParawiseRemarksCopy() {
		return parawiseRemarksCopy;
	}
	public void setParawiseRemarksCopy(List<String> parawiseRemarksCopy) {
		this.parawiseRemarksCopy = parawiseRemarksCopy;
	}
	public String getGpCode() {
		return gpCode;
	}
	public void setGpCode(String gpCode) {
		this.gpCode = gpCode;
	}
	public String getDistrict_name() {
		return district_name;
	}
	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}
	public String getRemarks2() {
		return remarks2;
	}
	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}
	
	
	public String getCounter_approved_gp() {
		return counter_approved_gp;
	}
	public void setCounter_approved_gp(String counter_approved_gp) {
		this.counter_approved_gp = counter_approved_gp;
	}
	public String getSerno() {
		return serno;
	}
	public void setSerno(String serno) {
		this.serno = serno;
	}
	public String getPwr_uploaded_copy() {
		return pwr_uploaded_copy;
	}
	public void setPwr_uploaded_copy(String pwr_uploaded_copy) {
		this.pwr_uploaded_copy = pwr_uploaded_copy;
	}
	public String getCounter_filed_document() {
		return counter_filed_document;
	}
	public void setCounter_filed_document(String counter_filed_document) {
		this.counter_filed_document = counter_filed_document;
	}
	public String getDaily_status() {
		return daily_status;
	}
	public void setDaily_status(String daily_status) {
		this.daily_status = daily_status;
	}
	public String getChangeLetter() {
		return changeLetter;
	}
	public void setChangeLetter(String changeLetter) {
		this.changeLetter = changeLetter;
	}
	public String getAckNo() {
		return ackNo;
	}
	public void setAckNo(String ackNo) {
		this.ackNo = ackNo;
	}
	 
	public List<ServiceAssignment> getAssignments() {
		return assignments;
	}
	public void setAssignments(List<ServiceAssignment> assignments) {
		this.assignments = assignments;
	}
	public String getAckType() {
		return ackType;
	}
	public void setAckType(String ackType) {
		this.ackType = ackType;
	}
	 
	public List<AckRespondentDTO> getGpOackForm() {
		return gpOackForm;
	}
	public void setGpOackForm(List<AckRespondentDTO> gpOackForm) {
		this.gpOackForm = gpOackForm;
	}
	 
	public String getDeptCategory() {
		return deptCategory;
	}
	public void setDeptCategory(String deptCategory) {
		this.deptCategory = deptCategory;
	}
	 
	
	private List<AckRespondentDTO> respondents;

	// Getter & Setter
	public List<AckRespondentDTO> getRespondents() {
	    return respondents;
	}
	public void setRespondents(List<AckRespondentDTO> respondents) {
	    this.respondents = respondents;
	}
	 
	public String getCaseFullName() {
		return caseFullName;
	}
	public void setCaseFullName(String caseFullName) {
		this.caseFullName = caseFullName;
	}

	public String getAdvocateCode() {
		return advocateCode;
	}
	public void setAdvocateCode(String advocateCode) {
		this.advocateCode = advocateCode;
	}
	
	
	

	public String getCrimeNo() {
		return crimeNo;
	}
	public void setCrimeNo(String crimeNo) {
		this.crimeNo = crimeNo;
	}
	public int getCrimeYear() {
		return crimeYear;
	}
	public void setCrimeYear(int crimeYear) {
		this.crimeYear = crimeYear;
	}
	 
	public String getBailPetitionType() {
		return bailPetitionType;
	}
	public void setBailPetitionType(String bailPetitionType) {
		this.bailPetitionType = bailPetitionType;
	}
	public String getCourtName() {
		return courtName;
	}
	public void setCourtName(String courtName) {
		this.courtName = courtName;
	}
	public String getChargeSheetNo() {
		return chargeSheetNo;
	}
	public void setChargeSheetNo(String chargeSheetNo) {
		this.chargeSheetNo = chargeSheetNo;
	}
	 



	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}




	public String getSebStationId() {
		return sebStationId;
	}
	public void setSebStationId(String sebStationId) {
		this.sebStationId = sebStationId;
	}




	public String getCaseTypeIdIds() {
		return caseTypeIdIds;
	}
	public void setCaseTypeIdIds(String caseTypeIdIds) {
		this.caseTypeIdIds = caseTypeIdIds;
	}




	public String getDeptIdIds() {
		return deptIdIds;
	}
	public void setDeptIdIds(String deptIdIds) {
		this.deptIdIds = deptIdIds;
	}




	public String getRes_adv_Id() {
		return res_adv_Id;
	}
	public void setRes_adv_Id(String res_adv_Id) {
		this.res_adv_Id = res_adv_Id;
	}




	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}




	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}




	public String getAdvcteName() {
		return advcteName;
	}
	public void setAdvcteName(String advcteName) {
		this.advcteName = advcteName;
	}




	public String getSendBack_dept_code() {
		return sendBack_dept_code;
	}
	public void setSendBack_dept_code(String sendBack_dept_code) {
		this.sendBack_dept_code = sendBack_dept_code;
	}




	 



	public int getResident_id() {
		return resident_id;
	}
	public void setResident_id(int resident_id) {
		this.resident_id = resident_id;
	}



	public String getOldNewType() {
		return oldNewType;
	}
	public void setOldNewType(String oldNewType) {
		this.oldNewType = oldNewType;
	}

	public String getAckNoo() {
		return ackNoo;
	}
	public void setAckNoo(String ackNoo) {
		this.ackNoo = ackNoo;
	}

	public String getSlno4() {
		return slno4;
	}
	public void setSlno4(String slno4) {
		this.slno4 = slno4;
	}


	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}


	public String getDismissedFileCopyOld() {
		return dismissedFileCopyOld;
	}
	public void setDismissedFileCopyOld(String dismissedFileCopyOld) {
		this.dismissedFileCopyOld = dismissedFileCopyOld;
	}


	public String getImplementedDt() {
		return implementedDt;
	}
	public void setImplementedDt(String implementedDt) {
		this.implementedDt = implementedDt;
	}


	public String getDismissedFileCopy() {
		return dismissedFileCopy;
	}
	public void setDismissedFileCopy(String dismissedFileCopy) {
		this.dismissedFileCopy = dismissedFileCopy;
	}


	public String getDesignationId() {
		return designationId;
	}
	public void setDesignationId(String designationId) {
		this.designationId = designationId;
	}


	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	public String getRegYear2() {
		return regYear2;
	}
	public void setRegYear2(String regYear2) {
		this.regYear2 = regYear2;
	}


	public String getMloId() {
		return mloId;
	}
	public void setMloId(String mloId) {
		this.mloId = mloId;
	}


	public String getSubjectDesc() {
		return subjectDesc;
	}
	public void setSubjectDesc(String subjectDesc) {
		this.subjectDesc = subjectDesc;
	}


	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}


	public static class AckRespondentDTO {

	    private String departmentId;
	    private String serviceType;
	    private String deptCategory;
	    private String dispalyDept;
	    private String dispalyDist;
	    private String employeeId;
	    private String sectionSelection;
	    // Getters and Setters
	   

	    public String getServiceType() {
	        return serviceType;
	    }
	    public String getDepartmentId() {
			return departmentId;
		}
		public void setDepartmentId(String departmentId) {
			this.departmentId = departmentId;
		}
		public void setServiceType(String serviceType) {
	        this.serviceType = serviceType;
	    }

	    public String getDeptCategory() {
	        return deptCategory;
	    }
	    public void setDeptCategory(String deptCategory) {
	        this.deptCategory = deptCategory;
	    }

	     
		public String getDispalyDept() {
			return dispalyDept;
		}
		public void setDispalyDept(String dispalyDept) {
			this.dispalyDept = dispalyDept;
		}
		public String getDispalyDist() {
			return dispalyDist;
		}
		public void setDispalyDist(String dispalyDist) {
			this.dispalyDist = dispalyDist;
		}
		public String getEmployeeId() {
			return employeeId;
		}
		public void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
		}
		public String getSectionSelection() {
			return sectionSelection;
		}
		public void setSectionSelection(String sectionSelection) {
			this.sectionSelection = sectionSelection;
		}

	    
	}


	 
	
	
	
}
