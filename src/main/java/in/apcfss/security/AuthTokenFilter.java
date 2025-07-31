package in.apcfss.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	UserRepository userRepository;

	@Value("${app.jwtTokenType}")
	private String tokenType;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String userId = jwtUtils.getUserNameFromJwtToken(jwt);
				List<Map<String, Object>> userList = userRepository.getUserByUsername(userId);
				Map<String, Object> userMap=null;
				if (!userList.isEmpty()) {
					  userMap = userList.get(0);
										
					UserDetails userDetails = UserDetailsImpl.build(userMap,userList.get(0).get("userid").toString());
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		System.out.println("#### headerAuth: " + headerAuth);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(tokenType + " ")) {
			return headerAuth.substring(7, headerAuth.length());
		}
		return null;
	}

	
}