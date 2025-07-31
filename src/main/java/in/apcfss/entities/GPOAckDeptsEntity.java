package in.apcfss.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ecourts_gpo_ack_depts", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class GPOAckDeptsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "ack_no")
	private String ackNo;

	@Column(name = "respondent_slno")
	private Integer respondentSlno;

	@Column(name = "dept_code")
	private String deptCode;

	@Column(name = "dist_id")
	private Integer distId;
	
	@Column(name = "assigned")
	private Boolean assigned;

	@Column(name = "case_status")
	private Integer caseStatus;

	@Column(name = "assigned_to")
	private String assignedTo;

	@Column(name = "ecourts_case_status")
	private String ecourtsCaseStatus;

	@Column(name = "section_officer_updated")
	private String section_officer_updated;

	@Column(name = "mlo_no_updated")
	private String mloNoUpdated;

	@Column(name = "designation")
	private String designation;

	@Column(name = "mandalid")
	private Integer mandalid;

	@Column(name = "villageid")
	private Integer villageid;

	@Column(name = "servicetpye")
	private String servicetpye;

	@Column(name = "dept_category")
	private String deptCategory;

	@Column(name = "dept_distcoll")
	private String deptDistcoll;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAckNo() {
		return ackNo;
	}

	public void setAckNo(String ackNo) {
		this.ackNo = ackNo;
	}

	public Integer getRespondentSlno() {
		return respondentSlno;
	}

	public void setRespondentSlno(Integer respondentSlno) {
		this.respondentSlno = respondentSlno;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public Integer getDistId() {
		return distId;
	}

	public void setDistId(Integer distId) {
		this.distId = distId;
	}

	public Boolean getAssigned() {
		return assigned;
	}

	public void setAssigned(Boolean assigned) {
		this.assigned = assigned;
	}

	public Integer getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(Integer caseStatus) {
		this.caseStatus = caseStatus;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getEcourtsCaseStatus() {
		return ecourtsCaseStatus;
	}

	public void setEcourtsCaseStatus(String ecourtsCaseStatus) {
		this.ecourtsCaseStatus = ecourtsCaseStatus;
	}

	public String getSection_officer_updated() {
		return section_officer_updated;
	}

	public void setSection_officer_updated(String section_officer_updated) {
		this.section_officer_updated = section_officer_updated;
	}

	public String getMloNoUpdated() {
		return mloNoUpdated;
	}

	public void setMloNoUpdated(String mloNoUpdated) {
		this.mloNoUpdated = mloNoUpdated;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Integer getMandalid() {
		return mandalid;
	}

	public void setMandalid(Integer mandalid) {
		this.mandalid = mandalid;
	}

	public Integer getVillageid() {
		return villageid;
	}

	public void setVillageid(Integer villageid) {
		this.villageid = villageid;
	}

	public String getServicetpye() {
		return servicetpye;
	}

	public void setServicetpye(String servicetpye) {
		this.servicetpye = servicetpye;
	}

	public String getDeptCategory() {
		return deptCategory;
	}

	public void setDeptCategory(String deptCategory) {
		this.deptCategory = deptCategory;
	}

	public String getDeptDistcoll() {
		return deptDistcoll;
	}

	public void setDeptDistcoll(String deptDistcoll) {
		this.deptDistcoll = deptDistcoll;
	}
	



	

}
