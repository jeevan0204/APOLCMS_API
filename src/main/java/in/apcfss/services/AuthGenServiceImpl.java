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

import cn.apiclub.captcha.Captcha;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.LoginRepo;
import in.apcfss.repositories.UserRepository;
import in.apcfss.requestbodies.LoginRequest;
import in.apcfss.requestbodies.RefreshTokenPayload;
import in.apcfss.response.JwtResponse1;
import in.apcfss.security.JwtUtils1;
import in.apcfss.utils.CommonQueryAPIUtils;
import in.apcfss.utils.CommonSecurityUtils;

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

	private final Map<String, Captcha> captchaStore = new HashMap<>();

	@Override
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
		Map<String, Object> map = new HashMap<String, Object>();
		String UserId = "";
		List<Map<String, Object>> userList1 = null;
		try {

			// Captcha captcha = captchaStore.get(loginRequest.getCaptchaId());
			System.out.println("captcha::::" + loginRequest.getCaptchaId() + "-------" + loginRequest.getCaptcha());

			if (CommonSecurityUtils.verifyCaptcha(loginRequest.getCaptchaId(), loginRequest.getCaptcha())) {

				UserId = loginRequest.getUsername();

				System.out.println("UserId------:" + UserId);
				userList1 = userRepository.getUserByUsername(UserId);
				if (userList1.size() < 1) {
					UserId = loginRequest.getUsername().toLowerCase();
					userList1 = userRepository.getUserByUsername(UserId);
				}
				System.out.println("userList1>>>" + userList1.get(0).get("dept_code") + "--" + userList1.size());
				if (loginRequest.getPassword().equals(userList1.get(0).get("password"))) {
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

						String deptcode = userList1.get(0).get("dept_code") != null
								? userList1.get(0).get("dept_code").toString()
								: "0";

						String user_type = userList1.get(0).get("user_type") != null
								? userList1.get(0).get("user_type").toString()
								: "0";
						String userDescription = userList1.get(0).get("user_description") != null
								? userList1.get(0).get("user_description").toString()
								: "0";

						System.out.println("5-----" + deptcode);
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

							System.out.println("" + userDetails.getResponseCode() + "-----" + deptcode + "---"
									+ Integer.parseInt(user_type) + "-----" + userDetails.getResponseDesc());

							return ResponseEntity.ok(new JwtResponse1(userDetails.getResponseCode(),
									userDetails.getResponseDesc(), jwt, refreshjwt, jwtExpirationMs,
									RefreshjwtExpirationMs, deptcode, Integer.parseInt(user_type), userDescription));

						} else {
//							map.put("responseCode", "02");
//							map.put("responseDesc", "User Not Exists");
//							return ResponseEntity.ok(map);
							
							return CommonQueryAPIUtils.failureResponse("User Not Exists");
						}

					} else {
//						map.put("responseCode", "02");
//						map.put("responseDesc", "User Id Not Exists");
//						return ResponseEntity.ok(map);
						
						return CommonQueryAPIUtils.failureResponse("UserId Not Exists");					}
				} else {
//					map.put("responseCode", "02");
//					map.put("responseDesc", "Invalid Password");
//					return ResponseEntity.ok(map);
					
					return CommonQueryAPIUtils.failureResponse("Invalid Password");
				}
			} else {
				return CommonQueryAPIUtils.failureResponse("Invalid Captcha! Try again");
			}

		} catch (Exception e) {
//			map.put("responseCode", "02");
//			map.put("responseDesc", "Invalid Credentials");
//			return ResponseEntity.ok(map);
			e.printStackTrace();
			System.out.println(e.getMessage());
			
			return CommonQueryAPIUtils.failureResponse("Invalid Credentials");
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
				String userDescription = userMap.get("user_description").toString();
				Integer role = Integer.parseInt(userMap.get("user_type").toString());
				return ResponseEntity.ok(new JwtResponse1(userDetails.getResponseCode(), userDetails.getResponseDesc(),
						jwt, refreshjwt, jwtExpirationMs, RefreshjwtExpirationMs, dept_code, role, userDescription));
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
