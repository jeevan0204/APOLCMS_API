package in.apcfss.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.AssignedCasesToSectionRepo;
import in.apcfss.repositories.DistrictWiseFinalOrdersImplementationRegRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class InstructionsreplyCountReportServiceImpl implements InstructionsreplyCountReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getReplyCountData(Authentication authentication,
	                                                  HCCaseStatusAbstractReqBody abstractReqBody) {
	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

	    int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
	    String deptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
	    String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
	    String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";

	    StringBuilder condition = new StringBuilder();
	    StringBuilder sqlCondition = new StringBuilder();
	    // Build parameter list
        List<Object> params = new ArrayList<>();
	    String deptName = "";
	    String sql="";
	    try {
	        // GP role
	        if ("6".equals(roleId)) {
	            condition.append(" INNER JOIN ecourts_mst_gp_dept_map egm ON egm.dept_code = d.dept_code ");
	            sqlCondition.append(" AND egm.gp_id = ? ");
	            params.add(userId);
	        }

	        // Officer-level roles
	        if (Arrays.asList("3", "4", "5", "9", "10").contains(roleId)) {
	            deptName = jdbcTemplate.queryForObject(
	                    "SELECT UPPER(description) FROM dept_new WHERE dept_code = ?", String.class, deptId);
	            sqlCondition.append(" AND (d.reporting_dept_code = ? OR a.dept_code = ?) ");
	            
	            params.add(deptId);
	            params.add(deptId);
	        }

	        // District-level roles
	        if ("2".equals(roleId) || "10".equals(roleId)) {
	            sqlCondition.append(" AND a.dist_code = ? ");
	            abstractReqBody.setDistrictId(String.valueOf(distId));
	            params.add(distId);
	        }

	          sql = """
	                SELECT DISTINCT
	                    x.reporting_dept_code AS dept_code,
	                    d1.description AS dept_name,
	                    'S' AS flag,
	                    SUM(dept_count) AS dept_count,
	                    SUM(instructions_count) AS instructions_count,
	                    SUM(reply_instructions_count) AS reply_instructions_count
	                FROM (
	                    SELECT a.dept_code,
	                        CASE WHEN reporting_dept_code = 'CAB01' THEN d.dept_code ELSE reporting_dept_code END AS reporting_dept_code,
	                        SUM(CASE WHEN a.dept_code IS NOT NULL THEN 1 ELSE 0 END) AS dept_count,
	                        SUM(CASE WHEN a.instructions IS NOT NULL AND a.instructions != '' THEN 1 ELSE 0 END) AS instructions_count,
	                        SUM(CASE WHEN a.reply_instructions IS NOT NULL AND a.reply_instructions != '' THEN 1 ELSE 0 END) AS reply_instructions_count
	                    FROM ecourts_dept_instructions a
	                    INNER JOIN dept_new d ON a.dept_code = d.dept_code
	                    """ + condition + """
	                    WHERE 1=1
	                    """ + sqlCondition + """
	                    GROUP BY a.dept_code, d.dept_code, reporting_dept_code
	                ) x
	                INNER JOIN dept_new d1 ON x.reporting_dept_code = d1.dept_code
	                GROUP BY x.reporting_dept_code, d1.description
	                ORDER BY x.reporting_dept_code
	                """;

	        System.out.println("SQL: ==> " + sql);
 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return jdbcTemplate.queryForList(sql, params.toArray());
	}
	@Override
	public List<Map<String, Object>> getReplyCountSecDeptdata(Authentication authentication,
	                                                          HCCaseStatusAbstractReqBody abstractReqBody) {
	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

	    int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
	    String userDeptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode() : "";
	    String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId() : "";
	    String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId() : "";

	    StringBuilder condition = new StringBuilder();
	    StringBuilder sqlCondition = new StringBuilder();
	    String deptId = "";
	    String deptName = "";
	    List<Object> params = new ArrayList<>();
	    String sql ="";
	    try {
	        // GP role
	        if ("6".equals(roleId)) {
	            condition.append(" INNER JOIN ecourts_mst_gp_dept_map egm ON egm.dept_code = d.dept_code ");
	            sqlCondition.append(" AND egm.gp_id = ? ");
	            params.add(userId);
	        }

	        // District level
	        if ("2".equals(roleId) || "10".equals(roleId)) {
	            sqlCondition.append(" AND a.dist_code = ? ");
	            params.add(distId);
	            abstractReqBody.setDistrictId(String.valueOf(distId));
	        }

	        // Officer role or others
	        if (Arrays.asList("5", "9", "10").contains(roleId)) {
	            deptId = userDeptId;
	            deptName = jdbcTemplate.queryForObject(
	                "SELECT UPPER(description) FROM dept_new WHERE dept_code = ?",
	                String.class,
	                deptId
	            );
	        } else {
	            deptId =  (abstractReqBody.getDeptId()).trim();
	            deptName =  (CommonModels.checkStringObject(abstractReqBody.getDeptName())).trim();
	        }

	        if (!deptId.isEmpty()) {
	            sqlCondition.append(" AND (d.reporting_dept_code = ? OR a.dept_code = ?) ");
	            params.add(deptId);
	            params.add(deptId);
	        }  

	          sql = """
	                SELECT a.dept_code, d.description AS dept_name, 'N' AS flag,
	                       SUM(CASE WHEN a.dept_code IS NOT NULL THEN 1 ELSE 0 END) AS dept_count,
	                       SUM(CASE WHEN a.instructions IS NOT NULL AND a.instructions != '' THEN 1 ELSE 0 END) AS instructions_count,
	                       SUM(CASE WHEN a.reply_instructions IS NOT NULL AND a.reply_instructions != '' THEN 1 ELSE 0 END) AS reply_instructions_count
	                FROM ecourts_dept_instructions a
	                INNER JOIN dept_new d ON a.dept_code = d.dept_code
	                """ + condition + """
	                WHERE 1=1
	                """ + sqlCondition + """
	                GROUP BY a.dept_code, d.description
	                ORDER BY a.dept_code
	                """;
	        System.out.println("SQL:==>" + sql);  // Consider replacing with a logger
	        
	    } catch (Exception e) {
	        e.printStackTrace(); 
	    }
	    return jdbcTemplate.queryForList(sql, params.toArray() );
	}

	@Override
	public List<Map<String, Object>> getReplyCountReportViewData(Authentication authentication,
	                                                             HCCaseStatusAbstractReqBody abstractReqBody) {
	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	    
	    int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
	    String deptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode() : "";
	    String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId() : "";
	    String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId() : "";

	    StringBuilder condition = new StringBuilder(" WHERE a.dept_code IS NOT NULL ");
	    StringBuilder joinClause = new StringBuilder();
	    List<Object> params = new ArrayList<>();
	    String sql = "";
	    try {
	        // GP Role
	        if ("6".equals(roleId)) {
	            joinClause.append(" INNER JOIN ecourts_mst_gp_dept_map egm ON egm.dept_code = d.dept_code ");
	            condition.append(" AND egm.gp_id = ? ");
	            params.add(userId);
	        }

	        // Officer Roles
	        if (Arrays.asList("3", "4", "5", "9", "10").contains(roleId)) {
	            deptId = jdbcTemplate.queryForObject(
	                "SELECT UPPER(description) FROM dept_new WHERE dept_code = ?", String.class, deptId);
	        }

	        // District-level roles
	        if (Arrays.asList("2", "10").contains(roleId)) {
	            condition.append(" AND a.dist_code = ? ");
	            params.add(distId);
	            abstractReqBody.setDistrictId(String.valueOf(distId));
	        }

	        // Department ID and filter flag
	        String inputDeptId =  (CommonModels.checkStringObject(abstractReqBody.getDeptId()));
	        String pwFlag =  (CommonModels.checkStringObject(abstractReqBody.getPwCounterFlag()));
	        String flag =  (abstractReqBody.getFlag());

	        if (!inputDeptId.isEmpty()) {
	            if ("S".equals(flag)) {
	                condition.append(" AND (d.reporting_dept_code = ? OR a.dept_code = ?) ");
	                params.add(inputDeptId);
	                params.add(inputDeptId);
	            } else {
	                condition.append(" AND a.dept_code = ? ");
	                params.add(inputDeptId);
	            }
	        }

	        // Flag-based conditions
	        switch (pwFlag) {
	            case "InstCount":
	                condition.append(" AND a.instructions IS NOT NULL AND a.instructions != '' ");
	                break;
	            case "ReplyInstCount":
	                condition.append(" AND a.reply_instructions IS NOT NULL AND a.reply_instructions != '' ");
	                break;
	            // No condition needed for DeptCount
	        }

	          sql = """
	            SELECT a.dept_code,
	                   d.description AS dept_name,
	                   a.instructions,
	                   a.reply_instructions,
	                   mobile_no,
	                   cino
	              FROM ecourts_dept_instructions a
	              INNER JOIN dept_new d ON a.dept_code = d.dept_code
	              """ + joinClause + condition + """
	              ORDER BY dept_code, cino
	        """;

	        System.out.println("SQL: " + sql);

	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    }
	    return jdbcTemplate.queryForList(sql, params.toArray());
	}

}
