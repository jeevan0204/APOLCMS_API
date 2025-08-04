package in.apcfss.services;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service; 
import in.apcfss.entities.UserDetailsImpl; 

@Service
public class DistrictWiseAssigmentCasesAbstractServiceImpl implements DistrictWiseAssigmentCasesAbstractService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getDistrictWiseAssigmentCasesList(Authentication authentication, String sectionCode) {
	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

	    String roleId = String.valueOf(userPrincipal.getRoleId());
	    String deptCode = String.valueOf(userPrincipal.getDeptCode());

	    List<Map<String, Object>> result = new ArrayList<>();

	    if (!"9".equals(roleId)) {
	        return result; // Return empty list for non-HOD roles
	    }

	    String sql = "";
	    Map<String, Object> params = new HashMap<>();

	    try {
	        if ("N".equalsIgnoreCase(sectionCode)) {
	            sql = """
	                    SELECT 
	                        COALESCE(district_name, :deptCode || ' / HOD') AS district_name, COALESCE(dist_id, '0') AS dist_id, total, uploaded, (total - uploaded) AS not_uploaded  
	                    FROM (
	                        SELECT  b.district_name,  c.dist_id, COUNT(a.ack_no) AS total, SUM(CASE  WHEN c.ecourts_case_status IN ('Pending','Closed','Private') THEN 1  ELSE 0  END) AS uploaded
	                        FROM ecourts_gpo_ack_dtls a
	                        INNER JOIN ecourts_gpo_ack_depts c ON a.ack_no = c.ack_no
	                        LEFT JOIN district_mst b ON c.dist_id = b.district_id
	                        LEFT JOIN ecourts_olcms_case_details ecod ON a.ack_no = ecod.cino
	                        WHERE c.dept_code = :deptCode
	                        GROUP BY c.dist_id, b.district_name
	                    ) x
	                    ORDER BY district_name
	                """;
	        } else {
	            sql = """
	                    SELECT 
	                        COALESCE(district_name, :deptCode || ' / HOD') AS district_name,
	                        COALESCE(dist_id, '0') AS dist_id,total, uploaded, (total - uploaded) AS not_uploaded
	                    FROM (
	                        SELECT  b.district_name, a.dist_id, COUNT(*) AS total, SUM(CASE   WHEN a.ecourts_case_status IN ('Pending','Closed','Private') THEN 1  ELSE 0  END) AS uploaded
	                        FROM ecourts_case_data a
	                        LEFT JOIN district_mst b ON a.dist_id = b.district_id
	                        LEFT JOIN ecourts_olcms_case_details ecod ON a.cino = ecod.cino
	                        WHERE a.dept_code = :deptCode
	                        GROUP BY a.dist_id, b.district_name
	                    ) x
	                    ORDER BY district_name
	                """;
	        }

	        params.put("deptCode", deptCode);
	        result = jdbcTemplate.queryForList(sql, params);

	    } catch (Exception e) {
	        e.printStackTrace(); // Or use a logger
	    }

	    return result;
	}
	
	@Override
	public List<Map<String, Object>> getDistrictWiseAssigmentCasesDetails(
	        Authentication authentication, 
	        String sectionCode, 
	        String actionType, 
	        String email, 
	        String deptName, 
	        String uploadValue, 
	        String distId) {

	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	    String userId = String.valueOf(userPrincipal.getUserId());
	    int distCode = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

	    String sql = "";
	    String sqlCondition = "";
	    Map<String, Object> params = new HashMap<>();

	    try {
	        if ("U".equalsIgnoreCase(uploadValue)) {
	            sqlCondition = " AND a.ecourts_case_status IN ('Pending','Closed','Private')";
	        } else if ("NU".equalsIgnoreCase(uploadValue)) {
	            sqlCondition = " AND a.ecourts_case_status IS NULL";
	        }

	        params.put("userId", userId);
	        params.put("distCode", distCode);

	        if ("N".equalsIgnoreCase(sectionCode)) {
	            sql = """
	                SELECT  a.ack_no,  servicetpye,  advocatename,  advocateccno,  casetype,  maincaseno,   petitioner_name,  egd.inserted_time  FROM ecourts_gpo_ack_depts a
	                INNER JOIN ecourts_gpo_ack_dtls egd ON a.ack_no = egd.ack_no
	                LEFT JOIN ecourts_olcms_case_details ecod ON a.ack_no = ecod.cino
	                WHERE a.dept_code = :userId 
	                  AND a.dist_id = :distCode
	                  """ + sqlCondition;
	        } else {
	            sql = """
	                SELECT  a.*,  COALESCE(TRIM(a.scanned_document_path), '-') AS scanned_document_path1 
	                FROM ecourts_case_data a
	                INNER JOIN dept_new d ON a.dept_code = d.dept_code
	                LEFT JOIN ecourts_olcms_case_details ecod ON a.cino = ecod.cino
	                WHERE (d.dept_code = :userId OR reporting_dept_code = :userId)
	                  AND a.dist_id = :distCode
	                  """ + sqlCondition;
	        }

	        // Optional: Use a logger here
	        System.out.println("Executing SQL: " + sql);

	        
	    } catch (Exception e) {
	        e.printStackTrace(); // Replace with logger.error in real code
	    }
	    return jdbcTemplate.queryForList(sql, params);
	}


}
