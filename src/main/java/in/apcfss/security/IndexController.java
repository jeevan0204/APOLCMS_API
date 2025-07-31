package in.apcfss.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "IndexController")
public class IndexController implements ErrorController {

	private static final String PATH = "/error";

	@GetMapping(value = PATH)
	public Map<String, Object> errorGet(HttpServletRequest request){
		return error(request);
	}
	
	@PostMapping(value = PATH)
	public Map<String, Object> errorPost(HttpServletRequest request){
		return error(request);
	}
	
	@PutMapping(value = PATH)
	public Map<String, Object> errorPut(HttpServletRequest request){
		return error(request);
	}
	
	@DeleteMapping(value = PATH)
	public Map<String, Object> errorDelete(HttpServletRequest request){
		return error(request);
	}
	
	public Map<String, Object> error(HttpServletRequest request) {
		Map<String, Object> finalData = new LinkedHashMap<>();

		Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
		String message = request.getAttribute("jakarta.servlet.error.message") + "";

		if (message.isBlank()) {
			message = mapMessage(statusCode);
		}

		finalData.put("status", false);
		finalData.put("scode", statusCode + "");
		finalData.put("sdesc", message);

		return finalData;
	}

	public static String mapMessage(Integer statusCode) {
		String message;
		switch (statusCode) {
		case 400:
			message = "Bad Request: The request could not be understood or was missing required parameters.";
			break;
		case 401:
			message = "Unauthorized: Authentication is required and has failed or has not been provided.";
			break;
		case 403:
			message = "Forbidden: You don't have permission to access the requested resource.";
			break;
		case 404:
			message = "Not Found: The requested resource could not be found.";
			break;
		case 405:
			message = "Method Not Allowed: The request method is not supported for the requested resource.";
			break;
		default:
			message = "Unknown Status Code: " + statusCode;
			break;
		}
		return message;
	}
}