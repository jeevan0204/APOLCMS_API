package in.apcfss.services;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.common.CommonModels;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.EcourtsCaseSearchRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class EcourtsCaseSearchServiceImpl implements EcourtsCaseSearchService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	 
	
	@Autowired
	EcourtsCaseSearchRepo SearchRepo; 
	
	
	
	
	@Override
	public List<Map<String, Object>> getCaseTypesListShrtNEW(Authentication authentication) { 
		List<Map<String, Object>> data=null;
		data=SearchRepo.getCaseTypesListShrtNEW();
		return data;
	}
	
	@Override
	public List<Map<String, Object>> getYearbyCasetypes(Authentication authentication, String caseType){
		
		List<Map<String, Object>> data=null;
		data=SearchRepo.getYearbyCasetypes(caseType);
		return data;
	}
	
	@Override
	public List<Map<String, Object>> getNumberbyYear(Authentication authentication, String caseType, int year) {
 
		List<Map<String, Object>> data=null;
		data=SearchRepo.getNumberbyYear(caseType,year);
		return data;
	}
	private boolean isValid(Object input) {
	    return input != null && !input.toString().trim().isEmpty() && !"0".equals(input.toString().trim());
	}
	
	@Override
	public List<Map<String, Object>> getSearchCasesList(Authentication authentication,
	        HCCaseStatusAbstractReqBody abstractReqBody, String SelectCaseType) {

	    String roleId = "", distId = "", deptCode = "", userid = "", msg = "failed";
	    String advocateName = null, petitionerName = null, ackNoo = null, cino = null;
	    String sql = null;

	    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	    deptCode = CommonModels.checkStringObject(userPrincipal.getDeptCode());

	    try {
	        String caseType = SelectCaseType;
	        String Type = abstractReqBody.getType();
	        StringBuilder sqlBuilder = new StringBuilder();

	        if ("New".equals(caseType)) {
	            abstractReqBody.setOldNewType("New");

	            ackNoo = String.valueOf(abstractReqBody.getAckNoo());
	            advocateName = String.valueOf(abstractReqBody.getAdvocateName()).toUpperCase();
	            petitionerName = String.valueOf(abstractReqBody.getPetitionerName()).toUpperCase();

	            StringBuilder sqlConditionN = new StringBuilder();

	            if (isValid(ackNoo)) {
	                sqlConditionN.append(" AND (a.ack_no='").append(ackNoo)
	                        .append("' OR a.hc_ack_no='").append(ackNoo).append("')");
	            }

	            if (isValid(advocateName)) {
	                sqlConditionN.append(" AND upper(advocatename)='").append(advocateName).append("'");
	            }

	            if (isValid(petitionerName)) {
	                sqlConditionN.append(" AND upper(petitioner_name)='").append(petitionerName).append("'");
	            }

	            sqlBuilder.append("SELECT a.slno, ad.respondent_slno, a.ack_no, distid, advocatename, petitioner_name, ")
	                    .append("advocateccno, casetype, maincaseno, a.remarks, inserted_by, inserted_ip, ")
	                    .append("UPPER(TRIM(district_name)) AS district_name, UPPER(TRIM(case_full_name)) AS case_full_name, ")
	                    .append("a.ack_file_path, CASE WHEN services_id='0' THEN NULL ELSE services_id END AS services_id, ")
	                    .append("services_flag, TO_CHAR(a.inserted_time,'dd-mm-yyyy') AS generated_date, ")
	                    .append("COALESCE(NULLIF(servicetpye,'null'), 'NON-SERVICES') AS servicetype, ")
	                    .append("getack_dept_desc(a.ack_no::text) AS dept_descs, COALESCE(a.hc_ack_no,'-') AS hc_ack_no ")
	                    .append("FROM ecourts_gpo_ack_depts ad ")
	                    .append("INNER JOIN ecourts_gpo_ack_dtls a ON ad.ack_no=a.ack_no ")
	                    .append("LEFT JOIN district_mst dm ON ad.dist_id=dm.district_id ")
	                    .append("LEFT JOIN dept_new dmt ON ad.dept_code=dmt.dept_code ")
	                    .append("INNER JOIN case_type_master cm ON (a.casetype=cm.sno::text OR a.casetype=cm.case_short_name) ")
	                    .append("WHERE a.delete_status IS FALSE AND ack_type='NEW' ")
	                    .append(sqlConditionN)
	                    .append(" ORDER BY a.inserted_time DESC");

	        } else {
	            abstractReqBody.setOldNewType("Legacy");

	            StringBuilder sqlCondition = new StringBuilder();

	            switch (Type) {
	                case "maincase":
	                    cino = abstractReqBody.getCaseType1() + "/" + abstractReqBody.getMainCaseNo() + "/" + abstractReqBody.getRegYear1();

	                    if (isValid(abstractReqBody.getCaseType1())) {
	                        sqlCondition.append(" AND type_name_reg='").append(abstractReqBody.getCaseType1().trim()).append("'");
	                    }
	                    if (isValid(abstractReqBody.getMainCaseNo())) {
	                        sqlCondition.append(" AND a.reg_no='").append(abstractReqBody.getMainCaseNo().trim()).append("'");
	                    }
	                    if (isValid(abstractReqBody.getRegYear1())) {
	                        sqlCondition.append(" AND a.reg_year='").append(abstractReqBody.getRegYear1().trim()).append("'");
	                    }
	                    break;

	                case "fir":
	                    if (isValid(abstractReqBody.getPoliceStation())) {
	                        sqlCondition.append(" AND police_station ILIKE '%").append(abstractReqBody.getPoliceStation().trim()).append("%'");
	                    }
	                    if (isValid(abstractReqBody.getFirNo())) {
	                        sqlCondition.append(" AND fir_no='").append(abstractReqBody.getFirNo().trim()).append("'");
	                    }
	                    if (isValid(abstractReqBody.getRegYear2())) {
	                        sqlCondition.append(" AND fir_year='").append(abstractReqBody.getRegYear2().trim()).append("'");
	                    }
	                    break;

	                case "party":
	                    if (isValid(abstractReqBody.getRespodentName())) {
	                        sqlCondition.append(" AND res_name ILIKE '%").append(abstractReqBody.getRespodentName().trim()).append("%'");
	                    }
	                    if (isValid(abstractReqBody.getPetitionerName())) {
	                        sqlCondition.append(" AND pet_name ILIKE '%").append(abstractReqBody.getPetitionerName().trim()).append("%'");
	                    }
	                    if (isValid(abstractReqBody.getRegYear3())) {
	                        sqlCondition.append(" AND reg_year='").append(abstractReqBody.getRegYear3().trim()).append("'");
	                    }
	                    break;

	                case "adv":
	                    if (isValid(abstractReqBody.getResAdv())) {
	                        sqlCondition.append(" AND res_adv ILIKE '%").append(abstractReqBody.getResAdv().trim()).append("%'");
	                    }
	                    if (isValid(abstractReqBody.getPetAdv())) {
	                        sqlCondition.append(" AND pet_adv ILIKE '%").append(abstractReqBody.getPetAdv().trim()).append("%'");
	                    }
	                    if (isValid(abstractReqBody.getRegYear4())) {
	                        sqlCondition.append(" AND reg_year='").append(abstractReqBody.getRegYear4().trim()).append("'");
	                    }
	                    break;

	                case "file":
	                    if (isValid(abstractReqBody.getFilNo())) {
	                        sqlCondition.append(" AND fil_no='").append(abstractReqBody.getFilNo().trim()).append("'");
	                    }
	                    if (isValid(abstractReqBody.getFilYear())) {
	                        sqlCondition.append(" AND fil_year='").append(abstractReqBody.getFilYear().trim()).append("'");
	                    }
	                    break;
	            }
	            
	            System.out.println("caseType--"+caseType+Type);

	            sqlBuilder.append("SELECT DISTINCT cino, a.date_of_filing, a.dt_regis, a.type_name_reg, a.reg_no, a.reg_year, ")
	                    .append("a.pet_name, a.coram, a.res_name, ")
	                    .append("CASE WHEN pend_disp='P' THEN 'Pending' ELSE 'Disposed' END AS status, ")
	                    .append("CASE WHEN (category_service='NON-SERVICE' OR a.category_service IS NULL OR a.category_service=' ') ")
	                    .append("THEN 'NON-SERVICE' ELSE 'SERVICE' END AS servicetype ")
	                    .append("FROM ecourts_case_data a ")
	                    .append("INNER JOIN dept_new d ON a.dept_code = d.dept_code ")
	                    .append("WHERE d.display = TRUE ")
	                    .append(sqlCondition);
	        }

	        sql = sqlBuilder.toString();
	        System.out.println("Final SQL:\n" + sql);
	        return jdbcTemplate.queryForList(sql);

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to fetch case data: " + e.getMessage(), e);
	    }
	}
	
}
