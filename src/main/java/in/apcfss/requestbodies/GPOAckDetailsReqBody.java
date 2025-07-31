package in.apcfss.requestbodies;

import java.util.Date;
import java.util.List;

import in.apcfss.entities.GPOAckDeptsEntity;
import in.apcfss.entities.GPOAckGen;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPOAckDetailsReqBody {
	
	private Long slno;
	private String ackNo;
	private String distId;
	private String advocateName;
	private String advocateCCno;
	private String caseType;
	private String mainCaseNo;
	private String remarks;
	private Boolean deleteStatus;
	private Date insertedTime;
	private String insertedBy;
	private String insertedIp;
	private String ackFilePath;
	private String petitionerName;
	private String servicesId;
	private String servicesFlag;
	private String barcodeFilePath;
	private String ackType;
	private Integer regYear1;
	private String caseType1;
	private String filingMode;
	private String caseCategory;
	private String hcAckNo;
	private String maincasenoUpdated;
	private String acknoUpdated;
	
	private String gpId;
	private String crimeNo;
	private int crimeYear;
	private String stationId;
	private String bailPetitionType;
	private String courtName;
	private String sebStationId;
	
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
	private String chargeSheetNo;
	
	//private List<GPOAckDeptsEntity> gpOackForm;
	private List<GPOAckGen> gpOackForm;
	private Integer respSize;
	
	private String caseTypeName;
	private String dept_name;
	private String district_name;
	private String generatedDate;
	public Long getSlno() {
		return slno;
	}
	public void setSlno(Long slno) {
		this.slno = slno;
	}
	public String getAckNo() {
		return ackNo;
	}
	public void setAckNo(String ackNo) {
		this.ackNo = ackNo;
	}
	public String getDistId() {
		return distId;
	}
	public void setDistId(String distId) {
		this.distId = distId;
	}
	public String getAdvocateName() {
		return advocateName;
	}
	public void setAdvocateName(String advocateName) {
		this.advocateName = advocateName;
	}
	public String getAdvocateCCno() {
		return advocateCCno;
	}
	public void setAdvocateCCno(String advocateCCno) {
		this.advocateCCno = advocateCCno;
	}
	public String getCaseType() {
		return caseType;
	}
	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}
	public String getMainCaseNo() {
		return mainCaseNo;
	}
	public void setMainCaseNo(String mainCaseNo) {
		this.mainCaseNo = mainCaseNo;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Boolean getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(Boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public Date getInsertedTime() {
		return insertedTime;
	}
	public void setInsertedTime(Date insertedTime) {
		this.insertedTime = insertedTime;
	}
	public String getInsertedBy() {
		return insertedBy;
	}
	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
	}
	public String getInsertedIp() {
		return insertedIp;
	}
	public void setInsertedIp(String insertedIp) {
		this.insertedIp = insertedIp;
	}
	public String getAckFilePath() {
		return ackFilePath;
	}
	public void setAckFilePath(String ackFilePath) {
		this.ackFilePath = ackFilePath;
	}
	public String getPetitionerName() {
		return petitionerName;
	}
	public void setPetitionerName(String petitionerName) {
		this.petitionerName = petitionerName;
	}
	public String getServicesFlag() {
		return servicesFlag;
	}
	public void setServicesFlag(String servicesFlag) {
		this.servicesFlag = servicesFlag;
	}
	public String getBarcodeFilePath() {
		return barcodeFilePath;
	}
	public void setBarcodeFilePath(String barcodeFilePath) {
		this.barcodeFilePath = barcodeFilePath;
	}
	public String getAckType() {
		return ackType;
	}
	public void setAckType(String ackType) {
		this.ackType = ackType;
	}
 
	public Integer getRegYear1() {
		return regYear1;
	}
	public void setRegYear1(Integer regYear1) {
		this.regYear1 = regYear1;
	}
	public String getCaseType1() {
		return caseType1;
	}
	public void setCaseType1(String caseType1) {
		this.caseType1 = caseType1;
	}
	public String getFilingMode() {
		return filingMode;
	}
	public void setFilingMode(String filingMode) {
		this.filingMode = filingMode;
	}
	public String getCaseCategory() {
		return caseCategory;
	}
	public void setCaseCategory(String caseCategory) {
		this.caseCategory = caseCategory;
	}
	public String getHcAckNo() {
		return hcAckNo;
	}
	public void setHcAckNo(String hcAckNo) {
		this.hcAckNo = hcAckNo;
	}
	public String getMaincasenoUpdated() {
		return maincasenoUpdated;
	}
	public void setMaincasenoUpdated(String maincasenoUpdated) {
		this.maincasenoUpdated = maincasenoUpdated;
	}
	public String getAcknoUpdated() {
		return acknoUpdated;
	}
	public void setAcknoUpdated(String acknoUpdated) {
		this.acknoUpdated = acknoUpdated;
	}
	public List<GPOAckGen> getGpOackForm() {
		return gpOackForm;
	}
	public void setGpOackForm(List<GPOAckGen> gpOackForm) {
		this.gpOackForm = gpOackForm;
	}
	public Integer getRespSize() {
		return respSize;
	}
	public void setRespSize(Integer respSize) {
		this.respSize = respSize;
	}
	public String getCaseTypeName() {
		return caseTypeName;
	}
	public void setCaseTypeName(String caseTypeName) {
		this.caseTypeName = caseTypeName;
	}
	public String getDept_name() {
		return dept_name;
	}
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	public String getDistrict_name() {
		return district_name;
	}
	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}
	public String getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(String generatedDate) {
		this.generatedDate = generatedDate;
	}
	 
	public String getGpId() {
		return gpId;
	}
	public void setGpId(String gpId) {
		this.gpId = gpId;
	}
	public String getServicesId() {
		return servicesId;
	}
	public void setServicesId(String servicesId) {
		this.servicesId = servicesId;
	}

	
}
