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
import in.apcfss.repositories.ScanDocsCountRepo;
import in.apcfss.repositories.UsersRepo;
import in.apcfss.requestbodies.HCCaseStatusAbstractReqBody;

@Service
public class ScanDocsCountReportServiceImpl implements ScanDocsCountReportService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ScanDocsCountRepo repo;

	@Override
	public List<Map<String, Object>> ScanDocsCountReport(Authentication authentication ) {
		List<Map<String, Object>> data=repo.ScanDocsCount();
		return data;
	}

	@Override
	public List<Map<String, Object>> getScanCountForNewCases(Authentication authentication ) {
		List<Map<String, Object>> data=repo.getScanCountForNewCases();
		return data;
	}

	@Override
	public List<Map<String, Object>> getNotAvailableOldScanDocsCount(Authentication authentication, String year, String month) {
		List<Map<String, Object>> data=repo.getNotAvailableOldScanDocsCount(year,month);
		return data;
	}

}
