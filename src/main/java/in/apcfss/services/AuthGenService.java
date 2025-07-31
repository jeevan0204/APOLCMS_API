package in.apcfss.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import in.apcfss.requestbodies.LoginRequest;
import in.apcfss.requestbodies.RefreshTokenPayload;


@Service
public interface AuthGenService {

	ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

	ResponseEntity<?> refreshtoken(RefreshTokenPayload reqBody);

}
