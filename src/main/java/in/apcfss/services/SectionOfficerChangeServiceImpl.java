package in.apcfss.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import in.apcfss.entities.SectionOfficerChangeMst;
import in.apcfss.entities.UserDetailsImpl;
import in.apcfss.repositories.SectionOfficerChangeRepo;
import in.apcfss.requestbodies.SectionOfficerChangeMstPayLoad;
import in.apcfss.utils.CommonQueryAPIUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class SectionOfficerChangeServiceImpl implements SectionOfficerChangeService {

	@Autowired
	SectionOfficerChangeRepo sectionRepo;

	@Autowired
	Environment env;

	@Autowired
	HttpServletRequest request;

	@Autowired
	CommonQueryAPIUtils commonQueryAPIUtils;

	@Override
	public ResponseEntity<?> saveDetails(Authentication authentication,
			SectionOfficerChangeMstPayLoad sectionOfficerChangeMstPayLoad) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String userId = userPrincipal.getUserId();
		String deptCode = userPrincipal.getDeptCode();
		System.out.println("user id" + userId + userPrincipal.getRoleId());
		// Integer distCode = Integer.parseInt(userPrincipal.getDistId().toString());
		String UserFullName = userPrincipal.getUserDescription();
		int distId = 0;
		Map<String, Object> resp = new LinkedHashMap<String, Object>();
		String deptId = deptCode.substring(0, 5);
		String path = "";
		try {
			SectionOfficerChangeMst changeMstEntity = new SectionOfficerChangeMst();
			changeMstEntity.setUserId(userId);
			changeMstEntity.setDeptId(deptId);
			changeMstEntity.setDesignation(sectionOfficerChangeMstPayLoad.getDesignation());
			changeMstEntity.setEmployeeid(sectionOfficerChangeMstPayLoad.getEmployeeid());
			changeMstEntity.setMobileno(sectionOfficerChangeMstPayLoad.getMobileno());
			changeMstEntity.setEmailid(sectionOfficerChangeMstPayLoad.getEmailid());
			changeMstEntity.setAadharno(sectionOfficerChangeMstPayLoad.getAadharno());
			changeMstEntity.setInsertedBy(sectionOfficerChangeMstPayLoad.getInsertedBy());
			changeMstEntity.setInsertedIp(request.getRemoteAddr());
			changeMstEntity.setInsertedTime(new Timestamp(new Date().getTime()));
			changeMstEntity.setChangeReasons(sectionOfficerChangeMstPayLoad.getChangeReasons());
			changeMstEntity.setPrevEmployeeid(sectionOfficerChangeMstPayLoad.getPrevEmployeeid());
			changeMstEntity.setDistId(distId);

			String base64 = sectionOfficerChangeMstPayLoad.getChangeLetterPath();
			byte[] data1 = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(base64);

			String final_letter_path = "SectionOfficerChangeReqLetter" + "_" + "".replace('/', '_') + ".pdf";

			String apiUrl = env.getProperty("uploads") + "//changerequests";
			Map<String, Object> response = commonQueryAPIUtils.uploadFileToAWSS3Bucket(data1, final_letter_path,
					apiUrl);
			if (response.get("status").equals(true)) {
				path = (String) response.get("path");
			}

			changeMstEntity.setChangeLetterPath(sectionOfficerChangeMstPayLoad.getChangeLetterPath());

			SectionOfficerChangeMst saveEntity = sectionRepo.save(changeMstEntity);
			if (saveEntity != null) {
				// ApproveChangeRequest();
				resp.put("scode", "01");
				resp.put("sdesc", "Your Details are Submitted Successfully");
			} else {
				resp.put("scode", "02");
				resp.put("sdesc", "Error While Submitting Details");
			}

		} catch (Exception e) {
			resp.put("scode", "04");
			e.printStackTrace();
			throw new RuntimeException("Something went wrong during operation!");
		}
		return ResponseEntity.ok(resp);
	}

	
	
	
	
}
