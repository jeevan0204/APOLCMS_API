package in.apcfss.services;

import java.util.List;
import java.util.Map;

public interface CommonMethodService {

	List<Map<String, Object>> getEmpSectionsList(String distCode, String deptCode, String tableName);

	List<Map<String, Object>> getEmpPostsList(String distCode, String deptCode, String tableName, String empSec);

	List<Map<String, Object>> getEmpsList(String distCode, String deptCode, String tableName, String empSec,
			String empPost);

	List<Map<String, Object>> getEmpPostsListWithMandal(String empDept, String distCode, String tableName,
			String empSec, String mndlCode);

	List<Map<String, Object>> getEmpPostsListWithMandalandVill(String empDept, String distCode, String tableName,
			String empSec, String mndlCode, String village);

	List<Map<String, Object>> getMandalList(String empDept, String distCode, String tableName,
			String empSec);

	List<Map<String, Object>> getVillageList(String empDept, String distCode, String tableName,
			String empSec);

	List<Map<String, Object>> getEmpsEmailsListWithMandal(String tableName, String empDept, String distCode, String empSec, String empPost,
			String mndlCode);

	List<Map<String, Object>> getEmpsEmailsListWithMandalAndVill(String tableName, String empDept, String distCode, String empSec,
			String empPost, String mndlCode, String vlgCode);


	List<Map<String, Object>> getEmpsEmailsList(String tableName, String empDept, String distCode, String empSec,
			String empPost);

	List<Map<String, Object>> getCategorywiseHodReport(String caseCategory);

	List<Map<String, Object>> getCategorywiseHodReportNew(String caseCategory);

	List<Map<String, Object>> getEmployeesList( String deptId,String designationId,String tableName,String roleId);


	List<Map<String, Object>> getEmpDetails(String empId, String designationId, String tableName);

	//boolean sendSMS(String mobileNo, String smsText, String templateId);

}
