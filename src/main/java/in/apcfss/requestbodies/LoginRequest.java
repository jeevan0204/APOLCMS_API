package in.apcfss.requestbodies;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class LoginRequest {
	
	@Id
	private String username;
	private String password;
	private String CaptchaId;
	private String Captcha;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCaptchaId() {
		return CaptchaId;
	}
	public void setCaptchaId(String captchaId) {
		CaptchaId = captchaId;
	}
	public String getCaptcha() {
		return Captcha;
	}
	public void setCaptcha(String captcha) {
		Captcha = captcha;
	}
	
	
	
	
	
	

}
