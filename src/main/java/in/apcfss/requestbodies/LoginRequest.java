package in.apcfss.requestbodies;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
public class LoginRequest {
	
	@Id
	private String username;
	private String password;
	private String captcha;
	private String captchaId;
	///auth

}
