package in.apcfss.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import in.apcfss.requestbodies.LoginRequest;
import in.apcfss.requestbodies.RefreshTokenPayload;
import in.apcfss.services.AuthGenService;
import in.apcfss.utils.CommonSecurityUtils;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Authentication Services - Access, Refresh", description = "Authentication Controller")
@SecurityRequirement(name = "bearerAuth")
public class AuthGenController {
	
	@Autowired
	AuthGenService authService;
	
	@Value("${app.jwtSecret}")
	private String jwtSecret;
	
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	@PostMapping("/auth/authenticate")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
	    System.err.println(loginRequest);
		return authService.authenticateUser(loginRequest);
	}
	
	@PostMapping("/auth/refresh-token")
	public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenPayload reqBody) {
		return authService.refreshtoken(reqBody);
	}
	
	
	@PostMapping("/tr-public/generate-captcha")
	ResponseEntity<?> generateCaptcha() {
		System.out.println("capthaaaa");
		return CommonSecurityUtils.generateCaptcha();
	}

}
