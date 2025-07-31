package in.apcfss.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class CommonQueryAPIUtils {

	private static final String STATUS = "status";
	private static final String SCODE = "scode";
	private static final String SDESC = "sdesc";

	private static final String SUCCESS_CODE = "01";
	private static final String FAILURE_CODE = "02";
	private static final String OTHER_CODE = "03";

	private static final String ISI = "Internal Server Issue";

	private static final String ISREQUIRED = " is required. ";
	private static final String ISREQUIREDINDEX = " is required at index ";
	private static final String FAILMSG = "Failed Due To: ";
	private static final String ATVALIDATION = " at Validation";

	public static Map<String, Object> apiService(String apiServiceName, List<Map<String, Object>> repomethod) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		var noDataFound = "No data found";
		var dataCount = "data_count";
		try {
			if (!repomethod.isEmpty()) {
				finalData.put(STATUS, true);
				finalData.put(SCODE, SUCCESS_CODE);
				finalData.put(SDESC, "data found");
				finalData.put(apiServiceName, repomethod);
				finalData.put(dataCount, repomethod.size());
			} else {
				finalData.put(STATUS, false);
				finalData.put(SCODE, OTHER_CODE);
				finalData.put(SDESC, noDataFound);
				finalData.put(apiServiceName, noDataFound);
				finalData.put(dataCount, 0);
			}
		} catch (Exception e) {
			catchResponse(e);
			finalData.put(STATUS, false);
			finalData.put(SCODE, FAILURE_CODE);
			finalData.put(SDESC, ISI);
			finalData.put(apiServiceName, noDataFound);
			finalData.put(dataCount, 0);
		}
		return finalData;
	}

	public static Map<String, Object> apiServiceMulti(List<String> apiServiceNameList,
			List<List<Map<String, Object>>> repomethodList) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		try {
			finalData.put(STATUS, true);
			finalData.put(SCODE, SUCCESS_CODE);
			finalData.put(SDESC, "Multiple lists found");

			var apiServiceName = "";

			for (var i = 0; i < repomethodList.size(); i++) {

				apiServiceName = apiServiceNameList.get(i);

				if (!repomethodList.get(i).isEmpty()) {
					finalData.put(apiServiceName + "_status", true);
					finalData.put(apiServiceName, repomethodList.get(i));
				} else {
					finalData.put(apiServiceName + "_status", false);
					finalData.put(apiServiceName, "No data found");
				}
			}
		} catch (Exception e) {
			finalData = new LinkedHashMap<>();
			finalData.put(STATUS, false);
			finalData.put(SCODE, FAILURE_CODE);
			finalData.put(SDESC, ISI);
		}
		return finalData;

	}

	public static Map<String, Object> apiServiceDelete(Integer deleteQueryfromRepo) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		try {
			if (deleteQueryfromRepo > 0) {
				finalData.put(STATUS, true);
				finalData.put(SCODE, SUCCESS_CODE);
				finalData.put(SDESC, "Successfully Deleted");
			} else {
				finalData.put(STATUS, false);
				finalData.put(SCODE, OTHER_CODE);
				finalData.put(SDESC, "No records found to delete");
			}
		} catch (Exception e) {
			catchResponse(e);
			finalData.put(STATUS, false);
			finalData.put(SCODE, FAILURE_CODE);
			finalData.put(SDESC, "Deletion Failed Due To: " + ISI);
		}
		return finalData;
	}

	public static ResponseEntity<Map<String, Object>> apiServiceUpdate(Integer updateQueryfromRepo) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		try {
			if (updateQueryfromRepo > 0) {
				finalData.put(STATUS, true);
				finalData.put(SCODE, SUCCESS_CODE);
				finalData.put(SDESC, "Successfully Updated");
			} else {
				finalData.put(STATUS, false);
				finalData.put(SCODE, OTHER_CODE);
				finalData.put(SDESC, "No records found to update");
			}
		} catch (Exception e) {
			catchResponse(e);
			finalData.put(STATUS, false);
			finalData.put(SCODE, FAILURE_CODE);
			finalData.put(SDESC, "Updation Failed Due To: " + ISI);
		}
		return ResponseEntity.ok().body(finalData);
	}

	public static String validationService(List<Object> values, List<String> names) {
		var sb = new StringBuilder();

		try {
			for (var i = 0; i < values.size(); i++) {
				if (Objects.isNull(values.get(i)) || "".equals(values.get(i)) || values.get(i).toString().isBlank()) {
					sb.append(names.get(i)).append(ISREQUIRED);
				}
			}
		} catch (Exception e) {
			catchResponse(e);
			sb.append(ISI);
		}
		return sb.toString();
	}

	public static String validationServiceWithIndex(List<Object> values, List<String> names, Integer index) {
		var sb = new StringBuilder();

		try {
			for (var i = 0; i < values.size(); i++) {
				if (Objects.isNull(values.get(i)) || "".equals(values.get(i)) || values.get(i).toString().isBlank()) {
					sb.append(names.get(i) + ISREQUIREDINDEX + index + ". ");
				}
			}
		} catch (Exception e) {
			catchResponse(e);
			sb.append(ISI);
		}
		return sb.toString();
	}

	public static ResponseEntity<Map<String, Object>> successResponse() {
		Map<String, Object> finalData = new LinkedHashMap<>();
		finalData.put(STATUS, true);
		finalData.put(SCODE, SUCCESS_CODE);
		finalData.put(SDESC, "Request successfully submitted.");

		return ResponseEntity.ok().body(finalData);
	}

	public static ResponseEntity<Map<String, Object>> failureResponse(String errorMessage) {
		Map<String, Object> finalData = new LinkedHashMap<>();

		finalData.put(STATUS, false);

		if (FAILURE_CODE.equals(errorMessage) || errorMessage.isBlank()) {
			finalData.put(SCODE, FAILURE_CODE);
			finalData.put(SDESC, FAILMSG + ISI);
		} else {
			finalData.put(SCODE, OTHER_CODE);
		//	finalData.put(SDESC, FAILMSG + errorMessage);
			finalData.put(SDESC,  errorMessage);
		}

		return ResponseEntity.ok().body(finalData);
	}

	public static ResponseEntity<Map<String, Object>> unAuthorisedResponse() {
		Map<String, Object> finalData = new LinkedHashMap<>();

		finalData.put(STATUS, false);
		finalData.put(SCODE, "401");
		finalData.put(SDESC, "Unauthorized request");

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(finalData);
	}

	public static ResponseEntity<Map<String, Object>> manualResponse(String code, String message) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		if (SUCCESS_CODE.equals(code)) {
			finalData.put(STATUS, true);
		} else {
			finalData.put(STATUS, false);
		}
		finalData.put(SCODE, code);
		finalData.put(SDESC, message);

		return ResponseEntity.ok().body(finalData);
	}

	public static ResponseEntity<Map<String, Object>> catchResponse(Exception e) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		finalData.put(STATUS, false);
		finalData.put(SCODE, FAILURE_CODE);
		finalData.put(SDESC, FAILMSG + ISI);
		exceptionLog(e);
		return ResponseEntity.ok().body(finalData);
	}

	public static Map<String, Object> exceptionLog(Exception ex) {
		Map<String, Object> finalData = new LinkedHashMap<>();
		List<Map<String, Object>> exList = new ArrayList<>();

		String currentPackage = CommonQueryAPIUtils.class.getPackageName().trim();
		var basePackage = currentPackage.substring(0, currentPackage.indexOf('.', currentPackage.indexOf('.') + 1));

//		finalData.put("user", );
//		finalData.put("timestamp", );
//		finalData.put("ip", );
		finalData.put("base_package", basePackage);
		finalData.put("exception", ex.getClass().getName());
		finalData.put("message", ex.getMessage());

//		System.out.println(":::Package::: " + basePackage + " :::exception::: " + ex.getClass().getName()
//				+ " :::message::: " + ex.getMessage());

		StackTraceElement[] stackTrace = ex.getStackTrace();
		for (StackTraceElement element : stackTrace) {

			if (element.getClassName().contains(basePackage)) {

				Map<String, Object> innerData = new LinkedHashMap<>();

				innerData.put("exception_class", element.getClassName());
				innerData.put("method", element.getMethodName());
				innerData.put("line", element.getLineNumber());
				exList.add(innerData);

//				System.out.println("=> :::ExClass::: " + element.getClassName() + " :::Method::: "
//						+ element.getMethodName() + " :::Line Number::: " + element.getLineNumber());
			}
		}

		finalData.put("ex_list", exList);
		return finalData;
	}

	public static String validatePojoProMax(ObjectNode objectNode) {
		var errMsg = "";

		var errMsgBuilder = new StringBuilder();
		objectNode.fieldNames().forEachRemaining(key -> {
			JsonNode value = objectNode.get(key);
			if (value == null || value.isNull() || value.isTextual() && value.textValue().isEmpty()) {
				errMsgBuilder.append(key).append(ISREQUIRED);
			}
		});
		errMsg = errMsgBuilder.toString();
		return errMsg;
	}

	public static String validatePojoWithIndex(ObjectNode objectNode, Integer i) {
		var errMsg = "";

		var errMsgBuilder = new StringBuilder();
		objectNode.fieldNames().forEachRemaining(key -> {
			JsonNode value = objectNode.get(key);
			if (value == null || value.isNull() || value.isTextual() && value.textValue().isEmpty()) {
				errMsgBuilder.append(key).append(ISREQUIREDINDEX + i + " .");
			}
		});
		errMsg = errMsgBuilder.toString();
		return errMsg;
	}

	public static Boolean isStringEmpty(String value) {
		if (value == null || "null".equals(value) || "".equals(value) || value.isEmpty() || value.isBlank()) {
			return true;
		}
		return false;
	}

	public static String validateAadhaar(String value) {
		if (value == null || "null".equals(value) || "".equals(value) || value.isEmpty() || value.isBlank()
				|| value.length() != 12 || !value.matches("\\d+")) {
			return "Invalid Aadhaar. ";
		}
		return "";
	}

	public static String validatemobile(String value) {
		if (value == null || "null".equals(value) || "".equals(value) || value.isEmpty() || value.isBlank()
				|| value.length() != 10 || !value.matches("\\d+")) {
			return "Invalid Mobile. ";
		}
		return "";
	}

	public static String validateEntity(Object entity) {
		var sb = new StringBuilder();
		try {
			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value == null || isStringEmpty(value.toString())) {
					sb.append(field.getName() + ISREQUIRED);
				}
			}

		} catch (Exception e) {
			sb.append(ISI + ATVALIDATION);
		}
		return sb.toString();
	}

	public static String validateEntity(Object entity, int index) {
		var sb = new StringBuilder();

		try {
			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value == null || isStringEmpty(value.toString())) {
					sb.append(field.getName() + ISREQUIREDINDEX + index + ". ");
				}
			}

		} catch (Exception e) {
			sb.append(ISI + ATVALIDATION);
		}
		return sb.toString();
	}

	public static String validateEntityFor(Object entity, String[] keys) {
		var sb = new StringBuilder();

		try {
			List<Field> fields = new ArrayList<>();
			for (String key : keys) {
				fields.add(entity.getClass().getDeclaredField(key));
			}
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value == null || isStringEmpty(value.toString())) {
					sb.append(field.getName() + ISREQUIRED);
				}
			}

		} catch (Exception e) {
			sb.append(ISI + ATVALIDATION);
		}
		return sb.toString();
	}

	public static String validateEntityFor(Object entity, String[] keys, int index) {
		var sb = new StringBuilder();

		try {
			List<Field> fields = new ArrayList<>();
			for (String key : keys) {
				fields.add(entity.getClass().getDeclaredField(key));
			}
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value == null || isStringEmpty(value.toString())) {
					sb.append(field.getName() + ISREQUIREDINDEX + index + ". ");
				}
			}

		} catch (Exception e) {
			sb.append(ISI + ATVALIDATION);
		}
		return sb.toString();
	}

	public static String validateEntityExcept(Object entity, String[] keys) {
		var sb = new StringBuilder();

		try {
			List<Field> fields = Arrays.asList(entity.getClass().getDeclaredFields());
			Set<String> excludedKeys = new HashSet<>(Arrays.asList(keys));

			for (Field field : fields) {
				if (excludedKeys.contains(field.getName())) {
					continue;
				}
				field.setAccessible(true);
				Object value = field.get(entity);

				if (value == null || isStringEmpty(value.toString())) {
					sb.append(field.getName() + ISREQUIRED);
				}
			}
		} catch (Exception e) {
			sb.append(ISI + ATVALIDATION);
		}
		return sb.toString();
	}

	public static String validateEntityExcept(Object entity, String[] keys, int index) {
		var sb = new StringBuilder();

		try {
			List<Field> fields = Arrays.asList(entity.getClass().getDeclaredFields());
			Set<String> excludedKeys = new HashSet<>(Arrays.asList(keys));

			for (Field field : fields) {
				if (excludedKeys.contains(field.getName())) {
					continue;
				}
				field.setAccessible(true);
				Object value = field.get(entity);

				if (value == null || isStringEmpty(value.toString())) {
					sb.append(field.getName() + ISREQUIREDINDEX + index + ". ");
				}
			}
		} catch (Exception e) {
			sb.append(ISI + ATVALIDATION);
		}
		return sb.toString();
	}

	public static Map<String, Object> uploadFileToAWSS3Bucket(byte[] fileByteArray, String fileNameWithExtension,
			String apiUrl) {

		Map<String, Object> responseMap = new LinkedHashMap<>();

		ByteArrayResource fileResource = new ByteArrayResource(fileByteArray) {
			@Override
			public String getFilename() {
				return fileNameWithExtension;
			}
		};

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> payload = new LinkedMultiValueMap<>();
		payload.add("file", fileResource);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

		var restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
//			System.out.println("File uploaded successfully! Path is " + responseEntity.getBody());
			responseMap.put(STATUS, true);
			responseMap.put("path", responseEntity.getBody());
		} else {
//			System.out.println("Failed to upload File. Response: " + responseEntity.getBody());
			responseMap.put(STATUS, false);
		}

		return responseMap;
	}

	public static BufferedImage createImageWithText(String text) {
		var width = 140;
		var height = 75;

		var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();

		// Fill the background
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);

		// Draw the text
		graphics.setColor(Color.BLACK);
		var font = new Font("Arial", Font.BOLD, 30);
		graphics.setFont(font);
		graphics.drawString(text, 10, 50);

		// Dispose the graphics object to release resources
		graphics.dispose();

		return bufferedImage;
	}

	public static String randomString(int length) {
		var rnd = new SecureRandom();
		var ab = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		var sb = new StringBuilder(length);
		for (var i = 0; i < length; i++)
			sb.append(ab.charAt(rnd.nextInt(ab.length())));
		return sb.toString();
	}

	public static String randomNumber(int length) {
		var rnd = new SecureRandom();
		var ab = "0123456789";
		var sb = new StringBuilder(length);
		for (var i = 0; i < length; i++)
			sb.append(ab.charAt(rnd.nextInt(ab.length())));
		return sb.toString();
	}
}
