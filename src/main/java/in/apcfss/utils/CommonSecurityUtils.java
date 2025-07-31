package in.apcfss.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonSecurityUtils {

	private static Map<String, String> globalCaptchaStore = new LinkedHashMap<>();

	public static ResponseEntity<Map<String, Object>> generateCaptcha() {
		Map<String, Object> resMap = new LinkedHashMap<>();
		try {
			var captchaId = UUID.randomUUID().toString();
			String captchaCode = CommonQueryAPIUtils.randomNumber(6);
			BufferedImage image = CommonQueryAPIUtils.createImageWithText(captchaCode);

			var baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);

			byte[] bytes = baos.toByteArray();
			String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

			globalCaptchaStore.put(captchaId, captchaCode);
			resMap.put("status", true);
			resMap.put("captchaId", captchaId);
			resMap.put("captcha", base64Image);

		} catch (Exception e) {
			resMap.put("status", false);
		}
		return ResponseEntity.ok(resMap);
	}

	public static Boolean verifyCaptcha(String captchaId, String inputCaptcha) {

		String storeCaptcha = globalCaptchaStore.getOrDefault(captchaId, "");
		globalCaptchaStore.remove(captchaId);
		return inputCaptcha.equals(storeCaptcha);

	}

	private static Map<String, Map<String, LocalDateTime>> globalOtpStore = new LinkedHashMap<>();

	public static ResponseEntity<Map<String, Object>> generateOtp(BigDecimal mobile, String otpType,
			JdbcTemplate jdbcTemplate) {

		var mobileString = mobile.toString();

		if (mobileString.length() == 10) {

			var now = LocalDateTime.now();

			Map<String, LocalDateTime> getOtpMap = globalOtpStore.getOrDefault(mobileString, null);

			// if null then new request allowed, if not null and now is after validTime then
			// new request allowed - added by rm
			if (getOtpMap == null || now.isAfter(getOtpMap.values().iterator().next())) {

				String otp = CommonQueryAPIUtils.randomNumber(6);

				String message = "Your OTP for " + otpType + " is " + otp + " -GOVTAP";

				sendJnbOtp(mobileString, "1407166237464854505", message, jdbcTemplate);

				LocalDateTime validTime = LocalDateTime.now().plusSeconds(60);
				Map<String, LocalDateTime> otpMap = new LinkedHashMap<>();
				otpMap.put(otp, validTime);

				globalOtpStore.put(mobileString, otpMap);

//				"mobileString " + mobileString + "otp " + otp + "validTime " + validTime;

				return CommonQueryAPIUtils.manualResponse("01", "OTP sent");
			}

			return CommonQueryAPIUtils.manualResponse("02",
					"You can request for another OTP only after 1 minute from the previous request!");
		}

		return CommonQueryAPIUtils.failureResponse("Invalid mobile number!");
	}

	public static Boolean verifyOtp(BigDecimal mobile, String otp) {
		Map<String, LocalDateTime> getOtpMap = globalOtpStore.getOrDefault(mobile.toString(), null);
		if (getOtpMap != null && getOtpMap.containsKey(otp)) {
			var now = LocalDateTime.now();
			LocalDateTime validTime = getOtpMap.get(otp).plusSeconds(120);
			if (validTime.isAfter(now)) {
				globalOtpStore.remove(mobile.toString());
				return true;
			} else {
				globalOtpStore.remove(mobile.toString());
			}
		}
		return false;
	}

	public static boolean isStrongPassword(String password) {

		int pwdLength = password.length();

		// Check if password length is between 8 and 16 characters
		if (pwdLength < 8 || pwdLength > 16) {
			return false;
		}

		// Initialize flags to check for different character classes
		var hasUppercase = false;
		var hasLowercase = false;
		var hasDigit = false;
		var hasSpecialChar = false;

		// Iterate through each character to validate the conditions
		for (var i = 0; i < pwdLength; i++) {
			var c = password.charAt(i);

			if (Character.isUpperCase(c)) {
				hasUppercase = true;
			} else if (Character.isLowerCase(c)) {
				hasLowercase = true;
			} else if (Character.isDigit(c)) {
				hasDigit = true;
			} else if ("@#$%^&+=".indexOf(c) >= 0) {
				hasSpecialChar = true;
			}

			// Early exit if all conditions are met
			if (hasUppercase && hasLowercase && hasDigit && hasSpecialChar) {
				return true;
			}
		}

		// Return true only if all criteria are met
		return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
	}

	static String sendJnbOtp(String mobileNo, String templateId, String message, JdbcTemplate jdbcTemplate) {

		var response = "";
		final var pwd = "password";
		String url = "https://www.smsstriker.com/API/sms.php?username=jnbotp&" + pwd + "=Jnb_871&from=JNBOTP&to="
				+ mobileNo + "&msg=" + message + "&type=1&template_id=" + templateId;
		try {
			var restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
			if (responseEntity.getBody() != null) {
				response = responseEntity.getBody();
			}

			var sql = "INSERT INTO candidate_otp_log(mobile, time_stamp, message, response) VALUES (?, now(), ?, ?)";
			jdbcTemplate.update(sql, mobileNo, message, response);

		} catch (Exception e) {
			CommonQueryAPIUtils.catchResponse(e);
		}

		return response;
	}
}
