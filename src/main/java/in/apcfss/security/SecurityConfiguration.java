package in.apcfss.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

	@Value("${server.cross-origins}")
	private String crossOrigins;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@SuppressWarnings("removal")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthEntryPointJwt unauthorizedHandler)
			throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request -> request
				.requestMatchers(AntPathRequestMatcher.antMatcher("/auth/authenticate")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/auth/refresh-token")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/rc-public/**")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/tr-public/**")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/health")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll().anyRequest().authenticated())
				.exceptionHandling(config -> config.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.headers(headers -> headers
				.contentSecurityPolicy(
						c -> c.policyDirectives("default-src 'self'").policyDirectives("frame-ancestors 'none'"))
				.httpStrictTransportSecurity(st -> st.maxAgeInSeconds(63072000).includeSubDomains(true).preload(true)));
		http.cors();
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				String[] origins = crossOrigins.split(",");
				registry.addMapping("/**").allowedOrigins(origins);
			}
		};
	}
}
