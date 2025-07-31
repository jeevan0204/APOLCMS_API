package in.apcfss.entities;

import java.net.InetAddress;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ecourts_gpo_ack_dtls", schema = "apolcms")
@SequenceGenerator(name = "ecourts_gpo_ack_dtls_slno_seq_gen", sequenceName = "ecourts_gpo_ack_dtls_slno_seq", allocationSize = 1)
public class GPOAckDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ecourts_gpo_ack_dtls_slno_seq_gen")
	private Long slno;

	@Column(name = "ack_no")
	private String ackNo;

	@Column(name = "distid")
	private Integer distId;

	@Column(name = "advocatename")
	private String advocateName;

	@Column(name = "advocateccno")
	private String advocateCCno;

	@Column(name = "casetype")
	private String caseType;

	@Column(name = "maincaseno")
	private String mainCaseNo;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "delete_status")
	private Boolean deleteStatus;

	@Column(name = "inserted_time")
	private Date insertedTime;

	@Column(name = "inserted_by")
	private String insertedBy;

	@Column(name = "inserted_ip", columnDefinition = "INET")
    private InetAddress insertedIp;

	@Column(name = "ack_file_path")
	private String ackFilePath;

	@Column(name = "petitioner_name")
	private String petitionerName;

	@Column(name = "services_flag")
	private String servicesFlag;

	@Column(name = "barcode_file_path")
	private String barcodeFilePath;

	@Column(name = "ack_type")
	private String ackType;

	@Column(name = "reg_year")
	private Integer regYear;

	@Column(name = "reg_no")
	private String regNo;

	@Column(name = "mode_filing")
	private String filingMode;

	@Column(name = "case_category")
	private String caseCategory;

	@Column(name = "hc_ack_no")
	private String hcAckNo;

	@Column(name = "maincaseno_updated")
	private String maincasenoUpdated;

	@Column(name = "ackno_updated")
	private String acknoUpdated;
	
	@Column(name = "gp_id")
	private String gpId;
	
	@Column(name = "services_id")
	private String servicesId;

	
	@Column(name = "crime_no")
	private String crimeNo;
	
	@Column(name = "crime_year")
	private int crimeYear;
	
	@Column(name = "name_of_the_police_station")
	private String policeStationName;
	
	@Column(name = "bail_petition_type")
	private String bailPetitionType;
	
	@Column(name = "court_name")
	private String courtName;
	
	@Column(name = "charge_sheet_no")
	private String chargeSheetNo;
	
	@Column(name = "seb_name")
	private String sebName;
	
	
	
	
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

	public String getPoliceStationName() {
		return policeStationName;
	}

	public void setPoliceStationName(String policeStationName) {
		this.policeStationName = policeStationName;
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

	public String getSebName() {
		return sebName;
	}

	public void setSebName(String sebName) {
		this.sebName = sebName;
	}

	
	
	
	
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

	public Integer getDistId() {
		return distId;
	}

	public void setDistId(Integer distId) {
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

	public InetAddress getInsertedIp() {
		return insertedIp;
	}

	public void setInsertedIp(InetAddress insertedIp) {
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

	public Integer getRegYear() {
		return regYear;
	}

	public void setRegYear(Integer regYear) {
		this.regYear = regYear;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
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
	
	
	

}
