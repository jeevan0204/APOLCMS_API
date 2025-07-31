package in.apcfss.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import in.apcfss.requestbodies.SectionOfficerChangeMstPayLoad;

public interface SectionOfficerChangeService {


	ResponseEntity<?> saveDetails(Authentication authentication, SectionOfficerChangeMstPayLoad sectionOfficerChangeMstPayLoad);

}
