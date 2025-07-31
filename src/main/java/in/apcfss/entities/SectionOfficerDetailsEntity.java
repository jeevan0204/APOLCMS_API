package in.apcfss.entities;

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
@Table(name="section_officer_details",schema = "apolcms")
@SequenceGenerator(name = "section_officer_details_slno_seq_gen", sequenceName = "section_officer_details_slno_seq", allocationSize = 1)
public class SectionOfficerDetailsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_officer_details_slno_seq_gen")
	private Long slno;
	
	@Column(name = "dept_id")
	private String deptId;
	
	@Column(name = "designation")
	private String designation; 
	
	@Column(name = "employeeid")
	private String employeeid;
	
	@Column(name = "mobileno")
	private String mobileno;
	
	@Column(name = "emailid")
	private String emailid;
	
	@Column(name = "aadharno")
	private String aadharno;
	
	@Column(name = "inserted_by")
	private String insertedBy;
	
	@Column(name = "inserted_ip")
	private String insertedIp;
	
	@Column(name = "inserted_time")
	private Date insertedTime;
	
	@Column(name = "employee_name")
	private String employeeName;
	
	@Column(name = "dist_id")
	private Integer distId;

	public SectionOfficerDetailsEntity(String deptId, String designation, String employeeid, String mobileno,
			String emailid, String aadharno, String insertedBy, String insertedIp, Date insertedTime,
			String employeeName, Integer distId) {
		super();
		this.deptId = deptId;
		this.designation = designation;
		this.employeeid = employeeid;
		this.mobileno = mobileno;
		this.emailid = emailid;
		this.aadharno = aadharno;
		this.insertedBy = insertedBy;
		this.insertedIp = insertedIp;
		this.insertedTime = insertedTime;
		this.employeeName = employeeName;
		this.distId = distId;
	}
	
	
	

}
