package in.apcfss.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.common.CommonModels;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class DeleteAckCasesListServiceImpl implements DeleteAckCasesListService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	UsersRepo repo;

	@Override
	public List<Map<String, Object>> getDeleteCasesListData(Authentication authentication, HCCaseStatusAbstractReqBody abstractReqBody) {

		StringBuilder sql = new StringBuilder("SELECT ack_no, advocatename, maincaseno, petitioner_name, "
				+ "TO_CHAR(inserted_time, 'dd/mm/yyyy') AS inserted_time, inserted_by "
				+ "FROM ecourts_gpo_ack_dtls_log WHERE 1=1");

		List<Object> params = new java.util.ArrayList<>();

		if (abstractReqBody.getDofFromDate() != null && !abstractReqBody.getDofFromDate().isEmpty()) {
			sql.append(" AND inserted_time >= TO_DATE(?, 'dd-mm-yyyy')");
			params.add(abstractReqBody.getDofFromDate());
		}

		if (abstractReqBody.getDofToDate() != null && !abstractReqBody.getDofToDate().isEmpty()) {
			sql.append(" AND inserted_time <= TO_DATE(?, 'dd-mm-yyyy')");
			params.add(abstractReqBody.getDofToDate());
		}

		sql.append(" ORDER BY inserted_time::timestamp DESC");

		System.out.println("SQL: " + sql);

		return jdbcTemplate.queryForList(sql.toString(), params.toArray());
	}


	@Override
	public List<Map<String, Object>> getdataOfPendingCases(Authentication authentication ,HCCaseStatusAbstractReqBody abstractReqBody) {
		StringBuilder sql = new StringBuilder();

		sql.append("select a.cino,a.type_name_reg as case_type,a.reg_no as main_case_no,a.reg_year as year,a.dept_code,d.description as dept_name,a.dist_name, "
				+ " (case when category_service=' ' or category_service is null then 'NON-SERVICE' else category_service end) as category_service,a.pet_name as petitioner,  "
				+ "  a.pet_adv as petioner_advocate,a.res_name as respondent,a.res_adv as respondent_advocate, "
				+ " case when (p.prayer is not null and coalesce(trim(p.prayer),'')!='' and length(p.prayer) > 2) then substr(p.prayer,1,250) else '-' end as prayer, prayer as prayer_full, "
				+ " (case when a.pend_disp='P' then 'Pending' else 'Disposed' end) as pending_disposed,a.date_of_filing  FROM ecourts_case_data a ");
		sql.append("INNER JOIN dept_new d ON a.dept_code = d.dept_code ");
		sql.append("INNER JOIN nic_prayer_data p ON a.cino = p.cino ");
		sql.append("WHERE d.display = true");

		List<Object> params = new ArrayList<>();

		// Date range
		if (abstractReqBody.getDofFromDate() != null && !abstractReqBody.getDofFromDate().isEmpty()) {
			sql.append(" AND a.dt_regis >= TO_DATE(?, 'dd-mm-yyyy')");
			params.add(abstractReqBody.getDofFromDate());
		}
		if (abstractReqBody.getDofToDate() != null && !abstractReqBody.getDofToDate().isEmpty()) {
			sql.append(" AND a.dt_regis <= TO_DATE(?, 'dd-mm-yyyy')");
			params.add(abstractReqBody.getDofToDate());
		}
		System.out.println("caseType----"+abstractReqBody.getCaseTypeId());
		// Case type
		if (abstractReqBody.getCaseTypeId() != null && !abstractReqBody.getCaseTypeId().isEmpty() && !abstractReqBody.getCaseTypeId().equals("0")) {
			String[] caseTypes = abstractReqBody.getCaseTypeId().split(",");
			sql.append(" AND TRIM(a.type_name_reg) IN (");
			sql.append(String.join(",", Collections.nCopies(caseTypes.length, "?")));
			sql.append(")");
			for (String ct : caseTypes) {
				params.add(ct.trim());
			}
		}
		
		// District
		if (abstractReqBody.getDistrictId() != null && !abstractReqBody.getDistrictId().equals("0") && !abstractReqBody.getDistrictId().equals("")) {
			String distName = repo.getDistName(Integer.parseInt(abstractReqBody.getDistrictId()));
			System.out.println("distName---"+distName);
			sql.append(" AND a.dist_name = ?");
			params.add(distName.trim());
		}

		// Reg year
		if (!"ALL".equalsIgnoreCase(abstractReqBody.getRegYear()) &&
				CommonModels.checkIntObject(abstractReqBody.getRegYear()) > 0) {
			sql.append(" AND a.reg_year = ?");
			params.add(CommonModels.checkIntObject(abstractReqBody.getRegYear()));
		}
		System.out.println("getDeptIdIds----"+abstractReqBody.getDeptId());
		// Department
		if (abstractReqBody.getDeptId() != null && !abstractReqBody.getDeptId().isEmpty() && !abstractReqBody.getDeptId().equals("0")) {
			String[] deptIds = abstractReqBody.getDeptId().split(",");
			sql.append(" AND a.dept_code IN (");
			sql.append(String.join(",", Collections.nCopies(deptIds.length, "?")));
			sql.append(")");
			for (String deptId : deptIds) {
				params.add(deptId.trim());
			}
		}

		// Category service
		if (abstractReqBody.getCategoryServiceId() != null && !abstractReqBody.getCategoryServiceId().equals("0") && !abstractReqBody.getCategoryServiceId().equals("")) {
			sql.append(" AND (a.category_service = ?");
			params.add(abstractReqBody.getCategoryServiceId().trim());

			if ("NON-SERVICE".equals(abstractReqBody.getCategoryServiceId())) {
				sql.append(" OR a.category_service IS NULL OR a.category_service = ' '");
			}
			sql.append(")");
		}

		// Petitioner Name (ILike)
		if (abstractReqBody.getPetitionerName() != null && !abstractReqBody.getPetitionerName().equals("0") && !abstractReqBody.getPetitionerName().equals("")) {
			sql.append(" AND REPLACE(REPLACE(pet_name,' ',''),'.','') ILIKE ?");
			params.add("%" + abstractReqBody.getPetitionerName().replaceAll(" ", "").replaceAll("\\.", "") + "%");
		}

		// Respondent Name (ILike)
		if (abstractReqBody.getRespodentName() != null && !abstractReqBody.getRespodentName().equals("0") && !abstractReqBody.getRespodentName().equals("")) {
			sql.append(" AND REPLACE(REPLACE(res_name,' ',''),'.','') ILIKE ?");
			params.add("%" + abstractReqBody.getRespodentName().replaceAll(" ", "").replaceAll("\\.", "") + "%");
		}

		sql.append(" ORDER BY dist_name, a.reg_no");
		System.out.println("sql---"+sql);
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());

		return results;
	}
}
