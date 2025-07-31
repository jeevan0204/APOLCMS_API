package in.apcfss.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.LoginRepo;
import in.apcfss.repositories.UserRepository;
import in.apcfss.requestbodies.LoginRequest;
import in.apcfss.requestbodies.RefreshTokenPayload;
import in.apcfss.response.JwtResponse1;
import in.apcfss.security.JwtUtils1;

@Service
public class AuthGenServiceImpl implements AuthGenService {

	@Autowired
	JwtUtils1 jwtUtils;

	@Value("${app.jwtExpirationMs}")
	private long jwtExpirationMs;

	@Value("${app.jwtRefreshExpirationMs}")
	private long RefreshjwtExpirationMs;

	@Value("${app.jwtTokenType}")
	private String tokenType;

	@Autowired
	Environment env;

	@Autowired
	LoginRepo loginRepo;

	@Autowired
	UserRepository userRepository;

	@Override
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
		Map<String, Object> map = new HashMap<String, Object>();
		String UserId="";
		try {
			

//			System.out.println("default password : " + env.getProperty("DEFAULT_PASSWORD") + " password :"
//					+ loginRequest.getPassword());
//			String twreisMd5Pwod = "97f828c851bb9db9c50e4420ad24b6bb";
//
//			boolean isMatch = java.security.MessageDigest.getInstance("MD5")
//					.digest(loginRequest.getPassword().getBytes()).toString().equals(twreisMd5Pwod);
			   if(loginRequest.getUsername().equals("OLCMS-ADMIN"))
			   {
				   UserId=loginRequest.getUsername();
			   }
			   else {
				   UserId=loginRequest.getUsername();
			   }
			   System.out.println("UserId------:"+UserId);
			   
				List<Map<String, Object>> userList1 = userRepository
						.getUserByUsername(UserId);
				System.out.println("userList1>>>" + userList1.get(0).get("dept_code") + "--" + userList1.size());

				if (userList1.size() > 0) {

					System.out.println("1111111111>>>>>>>>>");

					System.out.println(userList1.size());
					Map<String, Object> userMap = null;
					if (!userList1.isEmpty()) {
						userMap = userList1.get(0);
						System.out.println("hello---" + userList1.get(0).get("userid"));
					}
					System.out.println("2222");

					UserDetailsImpl userDetails = UserDetailsImpl.build(userMap,
							userList1.get(0).get("userid").toString());
					
					String deptcode = userList1.get(0).get("dept_code") != null ? 
					        userList1.get(0).get("dept_code").toString() : "0";

					String user_type = userList1.get(0).get("user_type") != null ? 
					        userList1.get(0).get("user_type").toString() : "0";

					
					System.out.println("5-----"+deptcode);
					Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
							userDetails.getAuthorities());
					System.out.println("66");
					SecurityContextHolder.getContext().setAuthentication(authentication);
					System.out.println("777");
					if (userList1.size() > 0) {
						System.out.println("888");
						String jwt = jwtUtils.generateJwtToken(authentication);
						System.out.println("TOKEN :" + jwt);
						String refreshjwt = jwtUtils.generateRefreshJwtToken(authentication);
						System.out.println("Refresh tOKEN:" + refreshjwt);

						System.out.println(""+userDetails.getResponseCode()+"-----"
						+deptcode+
								"---"+Integer.parseInt(user_type)
								+"-----"+userDetails.getResponseDesc());
						
						
						return ResponseEntity.ok(new JwtResponse1(userDetails.getResponseCode(),
								userDetails.getResponseDesc(), jwt, refreshjwt, jwtExpirationMs, RefreshjwtExpirationMs,
								deptcode,
								Integer.parseInt(user_type)));
					
					
				}else {
						map.put("responseCode", "02");
						map.put("responseDesc", "User Not Exists");
						return ResponseEntity.ok(map);
					}
				

			} else {
				map.put("responseCode", "02");
				map.put("responseDesc", "Invalid Password");
				return ResponseEntity.ok(map);
			}

		} catch (Exception e) {
			map.put("responseCode", "02");
			map.put("responseDesc", "Invalid Credentials");
			return ResponseEntity.ok(map);
		}
	}

	@Override
	public ResponseEntity<?> refreshtoken(RefreshTokenPayload reqBody) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String refreshtoken = reqBody.getRefreshToken();
			if (refreshtoken != null && jwtUtils.validateRefreshJwtToken(refreshtoken)) {
				String userId = jwtUtils.getUserNameFromRefreshJwtToken(refreshtoken);
				List<Map<String, Object>> userList = userRepository.getUserByUsername(userId);
				Map<String, Object> userMap = null;
				if (!userList.isEmpty()) {
					userMap = userList.get(0);
					System.out.println(userMap);
				}

				UserDetailsImpl userDetails = UserDetailsImpl.build(userMap, userList.get(0).get("userid").toString());
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				String jwt = jwtUtils.generateJwtToken(authentication);
				String refreshjwt = jwtUtils.generateRefreshJwtToken(authentication);
				String dept_code = userMap.get("dept_code").toString();
				Integer role = Integer.parseInt(userMap.get("user_type").toString());
				return ResponseEntity.ok(new JwtResponse1(userDetails.getResponseCode(), userDetails.getResponseDesc(),
						jwt, refreshjwt, jwtExpirationMs, RefreshjwtExpirationMs, dept_code, role));
			} else {
				map.put("ResponseCode", "03");
				map.put("ResponseDesc", "Token Denied");
				return ResponseEntity.ok().body(map);

			}

		} catch (Exception e) {
			map.put("ResponseCode", "02");
			map.put("ResponseDesc", "Invalid Credentials");
			return ResponseEntity.ok(map);
		}
	}
}
