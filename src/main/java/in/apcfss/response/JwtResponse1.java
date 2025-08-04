package in.apcfss.response;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;

public class JwtResponse1 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String responseCode;
	private String responseDesc;
	private final String token;
//	private final String userId;
//	private String ddoCode;
//	private final Integer userGrCode;
//	private Integer deptCode;
//	private String emplFirstName;
//	private Long userDistCode;
//	private FinancialYearData financialYearData;
	private final String refreshtoken;

	@Value("${app.jwtExpirationMs}")
	private long jwtExpirationMs;

	@Value("${app.jwtRefreshExpirationMs}")
	private long jwtRefreshExpirationMs;
	private String dept_code;
	private Integer role;
	private String userDescription;

//	public JwtResponse(String responseCode, String responseDesc, String token, String userId, String ddoCode,
//			Integer userGrCode, Integer deptCode, String emplFirstName, Long userDistCode,
//			FinancialYearData financialYearData, String refreshtoken, long jwtExpirationMs,
//			long jwtRefreshExpirationMs) {
//		this.responseCode = responseCode;
//		this.responseDesc = responseDesc;
//		this.token = token;
//		this.userId = userId;
//		this.ddoCode = ddoCode;
//		this.userGrCode = userGrCode;
//		this.deptCode = deptCode;
//		this.emplFirstName = emplFirstName;
//		this.userDistCode = userDistCode;
//		this.financialYearData = financialYearData;
//		this.refreshtoken = refreshtoken;
//		this.jwtExpirationMs = jwtExpirationMs;
//		this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;
//	}

	public JwtResponse1(String responseCode, String responseDesc, String token, String refreshtoken,
			long jwtExpirationMs, long jwtRefreshExpirationMs, String dept_code, Integer role,String userDescription) {
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
		this.token = token;
		this.refreshtoken = refreshtoken;
		this.jwtExpirationMs = jwtExpirationMs;
		this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;
		this.dept_code = dept_code;
		this.role = role;
		this.userDescription=userDescription;
		//check
		
	}

	

	public String getUserDescription() {
		return userDescription;
	}



	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}



	public String getToken() {
		return token;
	}

//	public String getUserId() {
//		return userId;
//	}
//
//	public String getDdoCode() {
//		return ddoCode;
//	}
//
//	public void setDdoCode(String ddoCode) {
//		this.ddoCode = ddoCode;
//	}
//
//	public Integer getDeptCode() {
//		return deptCode;
//	}
//
//	public void setDeptCode(Integer deptCode) {
//		this.deptCode = deptCode;
//	}
//
//	public String getEmplFirstName() {
//		return emplFirstName;
//	}
//
//	public void setEmplFirstName(String emplFirstName) {
//		this.emplFirstName = emplFirstName;
//	}
//
//	public Long getUserDistCode() {
//		return userDistCode;
//	}
//
//	public void setUserDistCode(Long userDistCode) {
//		this.userDistCode = userDistCode;
//	}
//
//	public Integer getUserGrCode() {
//		return userGrCode;
//	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

//	public FinancialYearData getFinancialYearData() {
//		return financialYearData;
//	}
//
//	public void setFinancialYearData(FinancialYearData financialYearData) {
//		this.financialYearData = financialYearData;
//	}

	public long getJwtExpirationMs() {
		return jwtExpirationMs;
	}

	public void setJwtExpirationMs(long jwtExpirationMs) {
		this.jwtExpirationMs = jwtExpirationMs;
	}

	public long getJwtRefreshExpirationMs() {
		return jwtRefreshExpirationMs;
	}

	public void setJwtRefreshExpirationMs(long jwtRefreshExpirationMs) {
		this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;
	}

	public String getRefreshtoken() {
		return refreshtoken;
	}



	public String getDept_code() {
		return dept_code;
	}

	public void setDept_code(String dept_code) {
		this.dept_code = dept_code;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

}
