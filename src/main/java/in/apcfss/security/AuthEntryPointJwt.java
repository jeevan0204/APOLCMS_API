package in.apcfss.security;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		var jsonErrorResponse = new ObjectMapper().writeValueAsString(CommonQueryAPIUtils.unAuthorisedResponse().getBody());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(jsonErrorResponse.getBytes());
		outputStream.flush();
	}

}