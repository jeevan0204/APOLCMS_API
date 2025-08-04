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
<<<<<<< HEAD
	private String captcha;
	private String captchaId;
	///auth
=======
	
	
>>>>>>> ae60c89a8c9931d88917cbfb86d14ae284dccaf0

}
