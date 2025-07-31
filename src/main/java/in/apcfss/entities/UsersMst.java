package in.apcfss.entities;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "public")
public class UsersMst {
	@Id
	private String userid;	
	
	private String password;
	private String ip;
	private Boolean is_delete;
	private Date last_pwd_modify_date;
	private Boolean is_change_req;
	private Integer login_type;
	private String ds_serial;
	private String user_description;
	private String user_type;
	private String password_text;
	private String created_by;
	private LocalDateTime created_on;
	private String created_ip;
	private Integer dept_id;
	private String dept_code;
	private Integer dist_id	;
	private Boolean is_first_login;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Boolean getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(Boolean is_delete) {
		this.is_delete = is_delete;
	}
	public Date getLast_pwd_modify_date() {
		return last_pwd_modify_date;
	}
	public void setLast_pwd_modify_date(Date last_pwd_modify_date) {
		this.last_pwd_modify_date = last_pwd_modify_date;
	}
	public Boolean getIs_change_req() {
		return is_change_req;
	}
	public void setIs_change_req(Boolean is_change_req) {
		this.is_change_req = is_change_req;
	}
	public Integer getLogin_type() {
		return login_type;
	}
	public void setLogin_type(Integer login_type) {
		this.login_type = login_type;
	}
	public String getDs_serial() {
		return ds_serial;
	}
	public void setDs_serial(String ds_serial) {
		this.ds_serial = ds_serial;
	}
	public String getUser_description() {
		return user_description;
	}
	public void setUser_description(String user_description) {
		this.user_description = user_description;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getPassword_text() {
		return password_text;
	}
	public void setPassword_text(String password_text) {
		this.password_text = password_text;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public LocalDateTime getCreated_on() {
		return created_on;
	}
	public void setCreated_on(LocalDateTime created_on) {
		this.created_on = created_on;
	}
	public String getCreated_ip() {
		return created_ip;
	}
	public void setCreated_ip(String created_ip) {
		this.created_ip = created_ip;
	}
	public Integer getDept_id() {
		return dept_id;
	}
	public void setDept_id(Integer dept_id) {
		this.dept_id = dept_id;
	}
	public String getDept_code() {
		return dept_code;
	}
	public void setDept_code(String dept_code) {
		this.dept_code = dept_code;
	}
	public Integer getDist_id() {
		return dist_id;
	}
	public void setDist_id(Integer dist_id) {
		this.dist_id = dist_id;
	}
	public Boolean getIs_first_login() {
		return is_first_login;
	}
	public void setIs_first_login(Boolean is_first_login) {
		this.is_first_login = is_first_login;
	}
	
	

}
