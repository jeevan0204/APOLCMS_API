package in.apcfss.requestbodies;

import java.sql.Timestamp;



public class SectionOfficerChangeMstPayLoad {
	
	private Integer slno;
	private String userId;
	private String deptId;
	private String designation;
	private String employeeid;
	private String mobileno;
	private String emailid;
	private String aadharno;
	private String changeReasons;
	private String changeLetterPath;
	private Boolean changeReqApproved;
	private String insertedBy;
	private String insertedIp;
	private Timestamp insertedTime;
	private String prevEmployeeid;
	private Integer distId;
	public Integer getSlno() {
		return slno;
	}
	public void setSlno(Integer slno) {
		this.slno = slno;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getEmployeeid() {
		return employeeid;
	}
	public void setEmployeeid(String employeeid) {
		this.employeeid = employeeid;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getEmailid() {
		return emailid;
	}
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}
	public String getAadharno() {
		return aadharno;
	}
	public void setAadharno(String aadharno) {
		this.aadharno = aadharno;
	}
	public String getChangeReasons() {
		return changeReasons;
	}
	public void setChangeReasons(String changeReasons) {
		this.changeReasons = changeReasons;
	}
	public String getChangeLetterPath() {
		return changeLetterPath;
	}
	public void setChangeLetterPath(String changeLetterPath) {
		this.changeLetterPath = changeLetterPath;
	}
	public Boolean getChangeReqApproved() {
		return changeReqApproved;
	}
	public void setChangeReqApproved(Boolean changeReqApproved) {
		this.changeReqApproved = changeReqApproved;
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
	public Timestamp getInsertedTime() {
		return insertedTime;
	}
	public void setInsertedTime(Timestamp insertedTime) {
		this.insertedTime = insertedTime;
	}
	public String getPrevEmployeeid() {
		return prevEmployeeid;
	}
	public void setPrevEmployeeid(String prevEmployeeid) {
		this.prevEmployeeid = prevEmployeeid;
	}
	

	
	public Integer getDistId() {
		return distId;
	}
	public void setDistId(Integer distId) {
		this.distId = distId;
	}
	@Override
	public String toString() {
		return "SectionOfficerChangeMstPayLoad [slno=" + slno + ", userId=" + userId + ", deptId=" + deptId
				+ ", designation=" + designation + ", employeeid=" + employeeid + ", mobileno=" + mobileno
				+ ", emailid=" + emailid + ", aadharno=" + aadharno + ", changeReasons=" + changeReasons
				+ ", changeLetterPath=" + changeLetterPath + ", changeReqApproved=" + changeReqApproved
				+ ", insertedBy=" + insertedBy + ", insertedIp=" + insertedIp + ", insertedTime=" + insertedTime
				+ ", prevEmployeeid=" + prevEmployeeid + ", distId=" + distId + "]";
	}
	

	

}
