package in.apcfss.requestbodies;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter@Setter
public class SMSCreationReqBody {
	
	private Long slno;
	private String ackNo;
	private String emailId;
	private String mobileNo ;
	private String smsId;
	private String smsText;
	private Date insertedTime;
	private String insertedBy;
	private String insertedIp;
	
	private Boolean isDisplay;
	private Boolean sendingSmsStatus;
	
	private String projectCode;
	private String templateId;
	
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
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getSmsText() {
		return smsText;
	}
	public void setSmsText(String smsText) {
		this.smsText = smsText;
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
	public Boolean getIsDisplay() {
		return isDisplay;
	}
	public void setIsDisplay(Boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
	public Boolean getSendingSmsStatus() {
		return sendingSmsStatus;
	}
	public void setSendingSmsStatus(Boolean sendingSmsStatus) {
		this.sendingSmsStatus = sendingSmsStatus;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getSmsId() {
		return smsId;
	}
	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}
	
	 

	
	
	
	
}
