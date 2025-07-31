package in.apcfss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
@OpenAPIDefinition(info = @Info(
		title = "APOLCMS (profile - ${spring.profiles.active})",description = "LEGAL CASES API SERVICES - GOVERNMENT OF ANDHRA PRADESH"
		, license = @License(name = "Designed & Developed By APCFSS",url = "https://apcfss.in"))
        , servers = {
    	        @Server(url = "http://localhost:${server.port}${server.servlet.context-path}", description = "Local Server"),
    	        @Server(url = "${server.base-url}", description = "Env Server")
    	    }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SpringBootApplication
public class ApolcmsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApolcmsApiApplication.class, args);
	}

}
