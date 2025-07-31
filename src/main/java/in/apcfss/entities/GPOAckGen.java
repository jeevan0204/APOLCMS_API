package in.apcfss.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPOAckGen {
	
	private String sectionSelection;
	private String empDept;
	private Integer otherDist;
	private String empSection;
	private String empPost;
	private String departmentId;
	private String serviceType;
	private Integer village;
	private Integer mandal;
	private String employeeId;
	private Integer dispalyDist;
	private String dispalyDept;
	private Integer slno;
	private String deptCategory;
	public String getSectionSelection() {
		return sectionSelection;
	}
	public void setSectionSelection(String sectionSelection) {
		this.sectionSelection = sectionSelection;
	}
	public String getEmpDept() {
		return empDept;
	}
	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}
	public Integer getOtherDist() {
		return otherDist;
	}
	public void setOtherDist(Integer otherDist) {
		this.otherDist = otherDist;
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
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Integer getVillage() {
		return village;
	}
	public void setVillage(Integer village) {
		this.village = village;
	}
	public Integer getMandal() {
		return mandal;
	}
	public void setMandal(Integer mandal) {
		this.mandal = mandal;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getDispalyDist() {
		return dispalyDist;
	}
	public void setDispalyDist(Integer dispalyDist) {
		this.dispalyDist = dispalyDist;
	}
	public String getDispalyDept() {
		return dispalyDept;
	}
	public void setDispalyDept(String dispalyDept) {
		this.dispalyDept = dispalyDept;
	}
	public Integer getSlno() {
		return slno;
	}
	public void setSlno(Integer slno) {
		this.slno = slno;
	}
	public String getDeptCategory() {
		return deptCategory;
	}
	public void setDeptCategory(String deptCategory) {
		this.deptCategory = deptCategory;
	}

	
}
