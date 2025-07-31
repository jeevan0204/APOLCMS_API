package in.apcfss.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.GPOAcknowledgementDetailsRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class AcknowledgementsReportServiceImpl implements AcknowledgementsReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	GPOAcknowledgementDetailsRepo GPOAckRepo;

	@Override
	public List<Map<String, Object>> getACKDATA(Authentication authentication ) {
	 
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		String UserId=userPrincipal.getUserId();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		sql.append("SELECT TO_CHAR(inserted_time::date, 'dd-mm-yyyy') AS ack_date, ");
		sql.append("COUNT(*) AS total, ");
		sql.append("SUM(CASE WHEN ack_type = 'NEW' THEN 1 ELSE 0 END) AS new_acks, ");
		sql.append("SUM(CASE WHEN ack_type = 'OLD' THEN 1 ELSE 0 END) AS existing_acks ");
		sql.append("FROM ecourts_gpo_ack_dtls ");
		sql.append("WHERE inserted_by LIKE 'HC%' ");

		if (!UserId.equals("HC-DEO-ADMIN")) {
		    sql.append("AND inserted_by = ? ");
		    params.add(UserId);
		}

		sql.append("GROUP BY inserted_time::date ");
		sql.append("ORDER BY inserted_time::date DESC");

		System.out.println("SQL Query: " + sql);
		List<Map<String, Object>> data = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		return data;
	}

	@Override
	public List<Map<String, Object>> getACKDATAExistingCase(Authentication authentication ) {
		StringBuilder sql = new StringBuilder();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		List<Map<String, Object>> result=null;
		String UserId=userPrincipal.getUserId();
		System.out.println("User ID: " + userPrincipal.getUserId());
		 
		try {
		    
		    List<Object> params = new ArrayList<>();

		    sql.append("SELECT hc_scan_legacy_date AS ack_date, COUNT(*) AS existing_caseNumber ");
		    sql.append("FROM ecourts_case_data ");
		    sql.append("WHERE hc_scan_legacy_date IS NOT NULL ");
		    sql.append("AND hc_scan_legacy_by NOT LIKE '%PP%' ");

		    if (!UserId.equals("HC-DEO-ADMIN")) {
		        sql.append("AND hc_scan_legacy_by = ? ");
		        params.add(UserId);
		    }

		    sql.append("GROUP BY hc_scan_legacy_date ");
		    sql.append("ORDER BY hc_scan_legacy_date DESC");

		    System.out.println("SQL list cases>:" + sql);

		     result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

		    // Process 'result' as needed
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getUSERWISEACKDATA(Authentication authentication,String ackDate ) {
		List<Map<String, Object>> result=null;
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		StringBuilder sql = new StringBuilder();
		String UserId=userPrincipal.getUserId();
		System.out.println("User ID: " + userPrincipal.getUserId());
		 
		try {
		    
		    List<Object> params = new ArrayList<>();

		    sql.append("SELECT inserted_by, ");
		    sql.append("COUNT(*) AS total, ");
		    sql.append("SUM(CASE WHEN ack_type = 'NEW' THEN 1 ELSE 0 END) AS new_acks, ");
		    sql.append("SUM(CASE WHEN ack_type = 'OLD' THEN 1 ELSE 0 END) AS existing_acks ");
		    sql.append("FROM ecourts_gpo_ack_dtls ");
		    sql.append("WHERE inserted_time::date = TO_DATE(?, 'DD-MM-YYYY') ");
		    params.add(ackDate);

		    if (!UserId.equals("HC-DEO-ADMIN")) {
		        sql.append("AND inserted_by = ? ");
		        params.add(UserId);
		    }

		    sql.append("GROUP BY inserted_by");

		    System.out.println("SQL list cases>:" + sql);

		     result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

		    // Process 'result' as needed

		} catch (Exception e) {
		    e.printStackTrace();
		}
		return result;
	}

}
