package in.apcfss.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;



/*@RAMASIVA */


@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${app.jwtRefreshSecret}")
	private String jwtRefreshSecret;

	@Value("${app.jwtRefreshExpirationMs}")
	private int jwtRefreshExpirationMs;

	@Value("${app.jwtCookieName}")
	private String jwtCookie;
	
	@Value("${server.base-url}")
	private String baseUrl;

	public String generateJwtToken(Map<String, Object> claims) {

		var username = claims.get("username").toString();
		var now = new Date();
		var expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder().setSubject(username).setIssuer(baseUrl).setIssuedAt(now).setExpiration(expiryDate)
				.addClaims(claims).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
	
	public Claims getClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

	// ****************************************************//

	public String generateJwtRefreshToken(String username) {
		var now = new Date();
		var expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);
		Map<String, Object> customData = new HashMap<>();
		customData.put("token_type", "refresh");

		return Jwts.builder().setSubject(username).setIssuer(baseUrl).setIssuedAt(now).setExpiration(expiryDate).addClaims(customData)
				.signWith(SignatureAlgorithm.HS512, jwtRefreshSecret).compact();
	}

	public String getUserNameFromJwtRefreshToken(String token) {
		return Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtRefreshToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(authToken);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Claims getClaimsFromRefreshToken(String token) {
		return Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(token).getBody();
    }

	// ****************************************************//

	public String getJwtFromCookies(HttpServletRequest request) {
		var cookie = WebUtils.getCookie(request, jwtCookie);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	public ResponseCookie generateJwtCookie(String username) {
		String jwt = generateTokenFromUsername(username);
		return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24L * 60 * 60).httpOnly(true).build();
	}

	public ResponseCookie getCleanJwtCookie() {
		return ResponseCookie.from(jwtCookie, null).path("/api").build();
	}

	public String generateTokenFromUsername(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

}