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


@Getter@Setter
@Entity
@Table(name="olcms_sms_creation",schema = "apolcms")
@SequenceGenerator(name = "olcms_sms_creation_seq_gen", sequenceName = "olcms_sms_creation_seq", allocationSize = 1)
public class SMSCreationEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "olcms_sms_creation_seq_gen")
	private Long slno;
	
	@Column(name = "ack_no")
	private String ackNo;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "mobile_no")
	private String mobileNo ;
	
	@Column(name = "sms_text")
	private String smsText;
	
	@Column(name = "inserted_time")
	private Date insertedTime;
	
	@Column(name = "inserted_by")
	private String insertedBy;
	
	@Column(name = "inserted_ip")
	private InetAddress insertedIp;
	
	@Column(name = "is_display")
	private Boolean isDisplay;
	
	@Column(name = "sending_sms_status")
	private Boolean sendingSmsStatus;

	public SMSCreationEntity(String ackNo, String emailId, String mobileNo, String smsText,
			Date insertedTime, String insertedBy, InetAddress inetAddress, Boolean isDisplay, Boolean sendingSmsStatus) {
		super();
		this.ackNo = ackNo;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.smsText = smsText;
		this.insertedTime = insertedTime;
		this.insertedBy = insertedBy;
		this.insertedIp = inetAddress;
		this.isDisplay = isDisplay;
		this.sendingSmsStatus = sendingSmsStatus;
	}
	
	

}
