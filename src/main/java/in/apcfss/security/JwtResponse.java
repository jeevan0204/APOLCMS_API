package in.apcfss.security;

public class JwtResponse {

	private final Boolean status;
	private final String scode;
	private final String sdesc;
	private String user_type;
	private String token_type;
	private String access_token;
	private Integer access_token_expiration;
	
	private String refresh_token;
	private Integer refresh_token_expiration;
	
	public JwtResponse(String user_type,String token_type,String access_token,Integer access_token_expiration,String refresh_token,Integer refresh_token_expiration) {
		this.status = true;
		this.scode = "01";
		this.sdesc = "Login success";
		this.user_type = user_type;
		this.token_type = token_type;
		this.access_token = access_token;
		this.access_token_expiration = access_token_expiration;
		this.refresh_token = refresh_token;
		this.refresh_token_expiration = refresh_token_expiration;
		
	}





	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Integer getAccess_token_expiration() {
		return access_token_expiration;
	}

	public void setAccess_token_expiration(Integer access_token_expiration) {
		this.access_token_expiration = access_token_expiration;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public Integer getRefresh_token_expiration() {
		return refresh_token_expiration;
	}

	public void setRefresh_token_expiration(Integer refresh_token_expiration) {
		this.refresh_token_expiration = refresh_token_expiration;
	}

	public String getScode() {
		return scode;
	}

	public String getSdesc() {
		return sdesc;
	}

	public Boolean getStatus() {
		return status;
	}
	
	

}
