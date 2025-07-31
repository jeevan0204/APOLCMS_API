package in.apcfss.entities;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.apcfss.repositories.UserRepository;

public class UserDetailsImpl implements UserDetails {

	@Autowired
	UserRepository userRepository;

	private static final long serialVersionUID = 1L;

	private String userId;
	private String deptCode;
	private Integer distId;
	private String userDescription;
	private Integer deptId;
	private String roleId;
	private String responseCode;
	private String responseDesc;

	public UserDetailsImpl(String userId, String deptCode, Integer distId, String userDescription, Integer deptId,
			String roleId, String responseCode, String responseDesc) {
		super();
		this.userId = userId;
		this.deptCode = deptCode;
		this.distId = distId;
		this.userDescription = userDescription;
		this.deptId = deptId;
		this.roleId = roleId;
		this.responseCode = responseCode;
		this.responseDesc = responseDesc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public Integer getDistId() {
		return distId;
	}

	public void setDistId(Integer distId) {
		this.distId = distId;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public static UserDetailsImpl build(Map<String, Object> map,String userid1) {
        System.out.println("userid1---"+userid1);
		String userId = userid1;
		String userDescription = (String) map.get("user_description");
		String DeptCode = (String) map.get("dept_code");
		Integer DistId = (Integer) map.get("dist_id");
		Integer DeptId = (Integer) map.get("dept_id");
		String RoleId = (String) map.get("user_type");

		String responseCode = "01";
		String responseDesc = "Login Successful";
		
		return new UserDetailsImpl(userId, DeptCode, DistId, userDescription, DeptId, RoleId, responseCode,
				responseDesc);
	}
}