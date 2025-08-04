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
public class CaseAssignmentStatusReportServiceImpl implements CaseAssignmentStatusReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> getCaseAssignmentStatus(Authentication authentication,
			HCCaseStatusAbstractReqBody abstractReqBody) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String deptCode = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode().toString() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId().toString() : "";

		String sql = "";
		List<Map<String, Object>> result = new ArrayList<>();

		try {
			if ("4".equals(roleId)) {
				sql = """
						SELECT 
						d.sdeptcode || '01' AS deptshortname,
						UPPER(d.description) AS description,
						COUNT(a.*) AS total,
						SUM(CASE WHEN SUBSTR(a.dept_code, 4, 2) != '01' THEN 1 ELSE 0 END) AS assigned_to_hod,
						SUM(CASE WHEN SUBSTR(a.dept_code, 4, 2) = '01' AND assigned = TRUE THEN 1 ELSE 0 END) AS assigned_to_sect_sec,
						SUM(CASE WHEN SUBSTR(a.dept_code, 4, 2) != '01' AND assigned = TRUE THEN 1 ELSE 0 END) AS assigned_to_hod_sec
						FROM ecourts_case_data a
						RIGHT JOIN (
						SELECT * FROM dept WHERE sdeptcode || deptcode = ?
						) d ON SUBSTR(a.dept_code, 1, 3) = d.sdeptcode
						GROUP BY SUBSTR(a.dept_code, 1, 3), d.sdeptcode, d.description
						ORDER BY 1
						""";
				result = jdbcTemplate.queryForList(sql, deptCode);

			} else if ("7".equals(roleId)) {
				sql = """
						SELECT 
						d.sdeptcode || '01' AS deptshortname,
						UPPER(d.description) AS description,
						COUNT(a.*) AS total,
						SUM(CASE WHEN SUBSTR(a.dept_code, 4, 2) != '01' THEN 1 ELSE 0 END) AS assigned_to_hod,
						SUM(CASE WHEN SUBSTR(a.dept_code, 4, 2) = '01' AND assigned = TRUE THEN 1 ELSE 0 END) AS assigned_to_sect_sec,
						SUM(CASE WHEN SUBSTR(a.dept_code, 4, 2) != '01' AND assigned = TRUE THEN 1 ELSE 0 END) AS assigned_to_hod_sec
						FROM ecourts_case_data a
						RIGHT JOIN (
						SELECT * FROM dept WHERE deptcode = '01'
						) d ON SUBSTR(a.dept_code, 1, 3) = d.sdeptcode
						GROUP BY SUBSTR(a.dept_code, 1, 3), d.sdeptcode, d.description
						ORDER BY 1
						""";
				result = jdbcTemplate.queryForList(sql);
			}

			System.out.println("Executed SQL: " + sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}


	@Override
	public List<Map<String, Object>> getCaseAssignmentStatusList(Authentication authentication, String actionType, String deptId, String deptName) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		int distId = userPrincipal.getDistId() != null ? userPrincipal.getDistId() : 0;
		String userDeptId = userPrincipal.getDeptCode() != null ? userPrincipal.getDeptCode() : "";
		String roleId = userPrincipal.getRoleId() != null ? userPrincipal.getRoleId() : "";
		String userId = userPrincipal.getUserId() != null ? userPrincipal.getUserId() : "";

		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();

		try {
			if (actionType != null && !actionType.isEmpty() && deptId != null && !deptId.isEmpty()) {

				String sqlCondition = "";
				switch (actionType) {
				case "assigned2HOD":
					sqlCondition = " AND SUBSTR(a.dept_code, 4, 2) != '01' ";
					break;
				case "assigned2SectSec":
					sqlCondition = " AND SUBSTR(a.dept_code, 4, 2) = '01' AND assigned = true ";
					break;
				case "assigned2HodSec":
					sqlCondition = " AND SUBSTR(a.dept_code, 4, 2) != '01' AND assigned = true ";
					break;
				default:
					break;
				}

				sql.append("""
						SELECT a.*, b.orderpaths
						FROM ecourts_case_data a
						LEFT JOIN (
						SELECT cino,
						STRING_AGG(
						'<a href="./' || order_document_path || '" target="_new" class="btn btn-sm btn-info">' ||
						'<i class="glyphicon glyphicon-save"></i><span>' || order_details || '</span></a><br/>',
						'- '
						) AS orderpaths
						FROM (
						SELECT cino, order_document_path, order_date, order_details || ' Dt.' || TO_CHAR(order_date, 'dd-mm-yyyy') AS order_details
						FROM ecourts_case_interimorder WHERE order_document_path IS NOT NULL AND POSITION('RECORD_NOT_FOUND' IN order_document_path) = 0
						AND POSITION('INVALID_TOKEN' IN order_document_path) = 0 UNION ALL
						SELECT cino, order_document_path, order_date, order_details || ' Dt.' || TO_CHAR(order_date, 'dd-mm-yyyy') AS order_details
						FROM ecourts_case_finalorder WHERE order_document_path IS NOT NULL
						AND POSITION('RECORD_NOT_FOUND' IN order_document_path) = 0
						AND POSITION('INVALID_TOKEN' IN order_document_path) = 0
						) all_orders
						GROUP BY cino
						) b ON a.cino = b.cino
						WHERE SUBSTR(a.dept_code, 1, 3) = ?
						""");

				sql.append(sqlCondition);
				params.add(deptId.substring(0, 3));
				System.out.println("sql==="+sql);
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}

		return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	}

}
