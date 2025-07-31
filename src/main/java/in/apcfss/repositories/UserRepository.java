package in.apcfss.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.UsersMst;
import jakarta.transaction.Transactional;


@Repository
public interface UserRepository extends JpaRepository<UsersMst, String>{
	
	
	@Query(nativeQuery = true, value = "SELECT userid,user_type,user_description, password, password_text, dept_code, dist_id FROM users WHERE userid = :userId")
	List<Map<String, Object>> getUserByUsername(@Param("userId") String userId);


	@Query(nativeQuery = true, value = "SELECT userid,user_type,user_description, password, password_text, dept_code, dist_id FROM users WHERE userid =:username and password =:password")
	List<Map<String, Object>> getUserByUsernameandpassword(@Param("userId") String userId, @Param("password") String password);

	
	
	
}
