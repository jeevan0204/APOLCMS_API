package in.apcfss.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.UsersRepo;

@Service
public class HCCaseDocsUploadStatusAbstractReportNewServiceImpl implements HCCaseDocsUploadStatusAbstractReportNewService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getSecdeptwiseData(UserDetailsImpl userPrincipal,
	        String dofFromDate, String dofToDate, String caseTypeId, String districtId, String deptId,
	        String petitionerName, String serviceType1, String advocateName) {

	    StringBuilder sqlCondition = new StringBuilder();
	    StringBuilder condition = new StringBuilder();
	    String sql="";

	    String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
	    String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
	    String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";

	    try {
	        // Date Filters
	        if (dofFromDate != null && !dofFromDate.trim().isEmpty()) {
	            sqlCondition.append(" AND b.inserted_time >= TO_DATE('").append(dofFromDate).append("', 'dd-mm-yyyy') ");
	        }

	        if (dofToDate != null && !dofToDate.trim().isEmpty()) {
	            sqlCondition.append(" AND b.inserted_time <= TO_DATE('").append(dofToDate).append("', 'dd-mm-yyyy') ");
	        }

	        // Case Type
	        if (caseTypeId != null && !caseTypeId.trim().isEmpty() && !"0".equals(caseTypeId)) {
	            sqlCondition.append(" AND TRIM(b.casetype) = '").append(caseTypeId.trim()).append("' ");
	        }

	        // District
	        if (districtId != null && !districtId.trim().isEmpty() && !"0".equals(districtId)) {
	            sqlCondition.append(" AND b.distid = '").append(districtId.trim()).append("' ");
	        }

	        // Department
	        if (deptId != null && !deptId.trim().isEmpty() && !"0".equals(deptId)) {
	            sqlCondition.append(" AND a.dept_code = '").append(deptId.trim()).append("' ");
	        }

	        // Petitioner Name
	        if (petitionerName != null && !petitionerName.trim().isEmpty() && !"0".equals(petitionerName)) {
	            sqlCondition.append(" AND REPLACE(REPLACE(petitioner_name, ' ', ''), '.', '') ILIKE '%")
	                        .append(petitionerName.trim()).append("%' ");
	        }

	        // Service Type
	        if (serviceType1 != null && !serviceType1.trim().isEmpty() && !"0".equals(serviceType1)) {
	            sqlCondition.append(" AND b.services_flag = '").append(serviceType1.trim()).append("' ");
	        }

	        // Advocate Name
	        if (advocateName != null && !advocateName.trim().isEmpty() && !"0".equals(advocateName)) {
	            sqlCondition.append(" AND REPLACE(REPLACE(advocatename, ' ', ''), '.', '') ILIKE '%")
	                        .append(advocateName.trim()).append("%' ");
	        }

	        // Role-specific logic
	        if ("3".equals(roleId)) {
	            sqlCondition.append(" AND (dn.reporting_dept_code = '").append(deptCode)
	                        .append("' OR dn.dept_code = '").append(deptCode).append("') ");
	        }

	        if ("6".equals(roleId)) {
	            condition.append(" LEFT JOIN ecourts_mst_gp_dept_map egm ON (egm.dept_code = dn.dept_code) ");
	            sqlCondition.append(" AND egm.gp_id = '").append(userId).append("' ");

	            if ("public-prosecutor@ap.gov.in".equalsIgnoreCase(userId)) {
	                sqlCondition.append(" AND inserted_by LIKE 'PP%' AND COALESCE(a.ecourts_case_status, '') != 'Closed' ");
	            }

	            if ("addl-pubprosecutor@ap.gov.in".equalsIgnoreCase(userId)) {
	                sqlCondition.append(" AND inserted_by LIKE 'PP%' AND COALESCE(a.ecourts_case_status, '') != 'Closed' ");
	            }
	        }

	        // Main SQL Query
	        sql = """
	            SELECT 
	                a1.reporting_dept_code AS dept_code,
	                UPPER(TRIM(dn1.description)) AS description,
	                SUM(total_resp1) AS cases_respondent_one,
	                SUM(total_resp_other) AS cases_respondent_other,
	                SUM(totalcases) AS total,
	                SUM(closed_cases) AS closed_cases,
	                SUM(pwrcounter_uploaded) AS pwrcounter_uploaded,
	                SUM(pwrcounter_not_uploaded) AS pwrcounter_not_uploaded,
	                SUM(pwrcounter_approved_by_gp) AS pwrcounter_approved_by_gp,
	                SUM(pwrcounter_rejected_by_gp) AS pwrcounter_rejected_by_gp,
	                SUM(counter_uploaded) AS counter_uploaded,
	                SUM(counter_not_uploaded) AS counter_not_uploaded,
	                SUM(counter_approved_gp) AS counter_approved_gp,
	                SUM(counter_rejected_gp) AS counter_rejected_gp
	            FROM (
	                SELECT 
	                    CASE WHEN reporting_dept_code = 'CAB01' THEN a.dept_code ELSE reporting_dept_code END AS reporting_dept_code,
	                    a.dept_code,
	                    UPPER(TRIM(dn.description)) AS description,
	                    SUM(CASE WHEN a.respondent_slno = 1 THEN 1 ELSE 0 END) AS total_resp1,
	                    SUM(CASE WHEN a.respondent_slno > 1 THEN 1 ELSE 0 END) AS total_resp_other,
	                    COUNT(*) AS totalcases,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Closed' THEN 1 ELSE 0 END) AS closed_cases,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND pwr_uploaded_copy IS NOT NULL AND LENGTH(pwr_uploaded_copy) > 10 THEN 1 ELSE 0 END) AS pwrcounter_uploaded,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND pwr_uploaded_copy IS NULL THEN 1 ELSE 0 END) AS pwrcounter_not_uploaded,
	                    SUM(CASE WHEN ecod.pwr_approved_gp = 'Yes' THEN 1 ELSE 0 END) AS pwrcounter_approved_by_gp,
	                    SUM(CASE WHEN ecod.pwr_approved_gp = 'No' THEN 1 ELSE 0 END) AS pwrcounter_rejected_by_gp,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND counter_filed_document IS NOT NULL AND LENGTH(counter_filed_document) > 10 THEN 1 ELSE 0 END) AS counter_uploaded,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND counter_filed_document IS NULL THEN 1 ELSE 0 END) AS counter_not_uploaded,
	                    SUM(CASE WHEN counter_approved_gp = 'Yes' THEN 1 ELSE 0 END) AS counter_approved_gp,
	                    SUM(CASE WHEN counter_approved_gp = 'No' THEN 1 ELSE 0 END) AS counter_rejected_gp
	                FROM 
	                    ecourts_gpo_ack_depts a
	                LEFT JOIN ecourts_olcms_case_details ecod ON (a.ack_no = ecod.cino AND a.respondent_slno = ecod.respondent_slno)
	                LEFT JOIN ecourts_gpo_ack_dtls b USING (ack_no)
	                INNER JOIN dept_new dn ON (a.dept_code = dn.dept_code)
	                """ + condition + """
	                WHERE b.ack_type = 'NEW' 
	                """ + sqlCondition + """
	                GROUP BY dn.reporting_dept_code, a.dept_code, dn.description, pwr_gp_approved_date, counter_approved_date
	            ) a1
	            INNER JOIN dept_new dn1 ON (a1.reporting_dept_code = dn1.dept_code)
	            GROUP BY a1.reporting_dept_code, dn1.description
	            ORDER BY 1
	        """;

	        System.out.println("Generated Sec SQL: " + sql);

	    } catch (Exception e) {
	        e.printStackTrace();  
	    }

	    return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getDeptWiseData(UserDetailsImpl userPrincipal,
	                                                 String deptCode, String deptName ) {
	    String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
	    String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
	    int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

	    StringBuilder sqlCondition = new StringBuilder();
	    StringBuilder sqlCondition1 = new StringBuilder();
	    StringBuilder joinClause = new StringBuilder();
	    String sql="",deptId="";

	    try {
	        // Determine deptId and deptName based on role
	        if ("5".equals(roleId) || "9".equals(roleId) || "10".equals(roleId)) {
	            deptId = userPrincipal.getDeptCode();
	            deptName = jdbcTemplate.queryForObject(
	                "SELECT UPPER(description) FROM dept_new WHERE dept_code = ?", String.class, deptId);
	        } else {
	            deptId = deptCode;
	            deptName = repo.getDeptName(deptId);
	        }

	        // Base SQL
	        sql = """
	            SELECT 
	                a1.reporting_dept_code AS dept_code,
	                UPPER(TRIM(dn1.description)) AS description,
	                SUM(total_resp1) AS cases_respondent_one,
	                SUM(total_resp_other) AS cases_respondent_other,
	                SUM(totalcases) AS total,
	                SUM(closed_cases) AS closed_cases,
	                SUM(pwrcounter_uploaded) AS pwrcounter_uploaded,
	                SUM(pwrcounter_not_uploaded) AS pwrcounter_not_uploaded,
	                SUM(pwrcounter_approved_by_gp) AS pwrcounter_approved_by_gp,
	                SUM(pwrcounter_rejected_by_gp) AS pwrcounter_rejected_by_gp,
	                SUM(counter_uploaded) AS counter_uploaded,
	                SUM(counter_not_uploaded) AS counter_not_uploaded,
	                SUM(counter_approved_gp) AS counter_approved_gp,
	                SUM(counter_rejected_gp) AS counter_rejected_gp
	            FROM (
	                SELECT 
	                    CASE WHEN reporting_dept_code = 'CAB01' THEN a.dept_code ELSE reporting_dept_code END AS reporting_dept_code,
	                    a.dept_code,
	                    UPPER(TRIM(dn.description)) AS description,
	                    SUM(CASE WHEN a.respondent_slno = 1 THEN 1 ELSE 0 END) AS total_resp1,
	                    SUM(CASE WHEN a.respondent_slno > 1 THEN 1 ELSE 0 END) AS total_resp_other,
	                    COUNT(*) AS totalcases,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Closed' THEN 1 ELSE 0 END) AS closed_cases,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND pwr_uploaded_copy IS NOT NULL AND LENGTH(pwr_uploaded_copy) > 10 THEN 1 ELSE 0 END) AS pwrcounter_uploaded,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND pwr_uploaded_copy IS NULL THEN 1 ELSE 0 END) AS pwrcounter_not_uploaded,
	                    SUM(CASE WHEN ecod.pwr_approved_gp = 'Yes' THEN 1 ELSE 0 END) AS pwrcounter_approved_by_gp,
	                    SUM(CASE WHEN ecod.pwr_approved_gp = 'No' THEN 1 ELSE 0 END) AS pwrcounter_rejected_by_gp,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND counter_filed_document IS NOT NULL AND LENGTH(counter_filed_document) > 10 THEN 1 ELSE 0 END) AS counter_uploaded,
	                    SUM(CASE WHEN a.ecourts_case_status = 'Pending' AND counter_filed_document IS NULL THEN 1 ELSE 0 END) AS counter_not_uploaded,
	                    SUM(CASE WHEN counter_approved_gp = 'Yes' THEN 1 ELSE 0 END) AS counter_approved_gp,
	                    SUM(CASE WHEN counter_approved_gp = 'No' THEN 1 ELSE 0 END) AS counter_rejected_gp
	                FROM 
	                    ecourts_gpo_ack_depts a
	                LEFT JOIN ecourts_olcms_case_details ecod ON (a.ack_no = ecod.cino AND a.respondent_slno = ecod.respondent_slno)
	                LEFT JOIN ecourts_gpo_ack_dtls b USING (ack_no)
	                INNER JOIN dept_new dn ON (a.dept_code = dn.dept_code)
	                """ + joinClause + """
	                WHERE b.ack_type = 'NEW'
	                """ + sqlCondition1 + sqlCondition + """
	                AND (a.dept_code = '""" + deptId + "' OR reporting_dept_code = '" + deptId + "')";

	        // Add district restriction for district-level roles
	        if ("2".equals(roleId) || "10".equals(roleId)) {
	            sql += " AND a.dist_id = '" + distId + "'";
	        }

	        sql += """
	                GROUP BY dn.reporting_dept_code, a.dept_code, dn.description, pwr_gp_approved_date, counter_approved_date
	            ) a1
	            INNER JOIN dept_new dn1 ON a1.reporting_dept_code = dn1.dept_code
	            GROUP BY a1.reporting_dept_code, dn1.description
	            ORDER BY 1
	        """;

	        System.out.println("SQL: " + sql);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return jdbcTemplate.queryForList(sql);
	}


	@Override
	public List<Map<String, Object>> getHCCaseDocsUploadCasesList(UserDetailsImpl userPrincipal, String deptCode, String deptName, String caseStatus, String respondentType) {
		String sql ="";
	    StringBuilder sqlCondition = new StringBuilder();
	    StringBuilder condition = new StringBuilder();

	    String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId().toString() : "";
	    String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";
	    int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;

	    try {
	        if (caseStatus != null && !caseStatus.isEmpty()) {
	            switch (caseStatus) {
	                case "TOTAL":
	                    break;
	                case "CLOSED":
	                    sqlCondition.append(" AND COALESCE(ad.ecourts_case_status,'') = 'Closed' ");
	                    break;
	                case "PWRUPLOADED":
	                    sqlCondition.append(" AND pwr_uploaded_copy IS NOT NULL AND LENGTH(pwr_uploaded_copy) > 10 ");
	                    break;
	                case "PWRNOTUPLOADED":
	                    sqlCondition.append(" AND ad.ecourts_case_status = 'Pending' AND pwr_uploaded_copy IS NULL ");
	                    break;
	                case "PWRAPPROVEDBYGP":
	                    sqlCondition.append(" AND pwr_approved_gp = 'Yes' ");
	                    break;
	                case "PWRREJEDBYGP":
	                    sqlCondition.append(" AND pwr_approved_gp = 'No' ");
	                    break;
	                case "COUNTERUPLOADED":
	                    sqlCondition.append(" AND counter_filed_document IS NOT NULL AND LENGTH(counter_filed_document) > 10 ");
	                    break;
	                case "COUNTERNOTUPLOADED":
	                    sqlCondition.append(" AND ad.ecourts_case_status = 'Pending' AND counter_filed_document IS NULL ");
	                    break;
	                case "GPCOUNTERAPPROVED":
	                    sqlCondition.append(" AND counter_approved_gp = 'Yes' ");
	                    break;
	                case "GPCOUNTERREJECTED":
	                    sqlCondition.append(" AND counter_approved_gp = 'No' ");
	                    break;
	                case "ALL":
	                    sqlCondition.append(" AND (dmt.dept_code = '").append(deptCode).append("' OR dmt.reporting_dept_code = '").append(deptCode).append("') ");
	                    break;
	                case "HOD":
	                    sqlCondition.append(" AND dmt.dept_code = '").append(deptCode).append("' ");
	                    break;
	            }
	        }

	        // Respondent type conditions
	        if ("1".equals(respondentType)) {
	            sqlCondition.append(" AND ad.respondent_slno = 1 ");
	        } else if ("2".equals(respondentType)) {
	            sqlCondition.append(" AND ad.respondent_slno > 1 ");
	        } else if ("ALL".equals(respondentType)) {
	            sqlCondition.append(" AND (dmt.dept_code = '").append(deptCode).append("' OR dmt.reporting_dept_code = '").append(deptCode).append("') ");
	        } else if ("HOD".equals(respondentType)) {
	            sqlCondition.append(" AND dmt.dept_code = '").append(deptCode).append("' ");
	        }

	        // GP Role handling
	        if ("6".equals(roleId)) {
	            condition.append(" INNER JOIN ecourts_mst_gp_dept_map egm ON (egm.dept_code = ad.dept_code) ");

	            if ("public-prosecutor@ap.gov.in".equals(userId) || "addl-pubprosecutor@ap.gov.in".equals(userId)) {
	                sqlCondition.append(" AND egm.gp_id = '").append(userId).append("' ")
	                            .append(" AND inserted_by LIKE 'PP%' ")
	                            .append(" AND COALESCE(ad.ecourts_case_status,'') != 'Closed' ");
	            }
	        }

	        // Section Officer / Dept user by district
	        if ("2".equals(roleId) || "10".equals(roleId)) {
	            sqlCondition.append(" AND ad.dist_id = '").append(distId).append("' ");
	        }

	        // Final SQL query
	          sql = "SELECT DISTINCT a.slno, a.ack_no, file_found, distid, mode_filing, advocatename, advocateccno, casetype, maincaseno, "
	                + "ecod.remarks, inserted_by, inserted_ip, UPPER(TRIM(district_name)) AS district_name, "
	                + "UPPER(TRIM(case_full_name)) AS case_full_name, a.ack_file_path, "
	                + "CASE WHEN services_id = '0' THEN NULL ELSE services_id END AS services_id, services_flag, "
	                + "TO_CHAR(a.inserted_time, 'dd-mm-yyyy') AS generated_date, getack_dept_desc(a.ack_no) AS dept_descs, "
	                + "a.inserted_time, COALESCE(a.hc_ack_no, '-') AS hc_ack_no "
	                + "FROM ecourts_gpo_ack_depts ad "
	                + "INNER JOIN ecourts_gpo_ack_dtls a ON (ad.ack_no = a.ack_no) "
	                + "LEFT JOIN scanned_affidavit_new_cases_count sc ON (sc.ack_no = a.ack_no OR sc.ack_no = a.hc_ack_no) "
	                + "LEFT JOIN ecourts_olcms_case_details ecod ON (ad.ack_no = ecod.cino AND ad.respondent_slno = ecod.respondent_slno) "
	                + "INNER JOIN district_mst dm ON (a.distid = dm.district_id) "
	                + "INNER JOIN dept_new dmt ON (ad.dept_code = dmt.dept_code) "
	                + condition.toString()
	                + "INNER JOIN case_type_master cm ON (a.casetype = cm.sno::TEXT OR a.casetype = cm.case_short_name) "
	                + "WHERE a.delete_status IS FALSE AND ack_type = 'NEW' "
	                + sqlCondition.toString()
	                + " ORDER BY a.inserted_time DESC";

	        System.out.println("SQL: " + sql);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return jdbcTemplate.queryForList(sql);
	}

}
