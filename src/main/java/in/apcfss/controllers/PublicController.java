package in.apcfss.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Public Services - etc...", description = "Public Controller")
public class PublicController {

	@GetMapping("/rc-public/test")
	String helloWorld() {
		return "Hello World";
	}
	
	
	@GetMapping("/hello")
	String hello() {
		return "Hello AUTH";
	}
}
