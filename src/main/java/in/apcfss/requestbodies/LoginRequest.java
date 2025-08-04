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
	
	

}
