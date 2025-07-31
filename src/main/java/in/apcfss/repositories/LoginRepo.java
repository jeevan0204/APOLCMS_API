package in.apcfss.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.apcfss.requestbodies.LoginRequest;

@Repository
public interface LoginRepo extends JpaRepository<LoginRequest, String> {

	
	@Query(nativeQuery = true, value = "SELECT * FROM users WHERE userid =:userId")
	List<Map<String, Object>> getUserByUsername(@Param("userId") String userId);

	@Query(nativeQuery = true, value = "SELECT * FROM users WHERE userid =:username and password =:password")
	List<Map<String, Object>> getUserByUsernameandpassword(String username, String password);

}
