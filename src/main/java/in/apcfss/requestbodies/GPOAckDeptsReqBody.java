package in.apcfss.requestbodies;

import java.util.List;

import in.apcfss.entities.GPOAckDeptsEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPOAckDeptsReqBody {

	private String ackNo;
	private Integer respondentSlno;
	private String deptCode;
	private String distId;
	private Boolean assigned;
	private Integer caseStatus;
	private String assignedTo;
	private String ecourtsCaseStatus;
	private String section_officer_updated;
	private String mloNoUpdated;
	private String designation;
	private Integer mandalid;
	private Integer villageid;
	private String servicetpye;
	private String deptCategory;
	private String deptistcoll;
	private String sectionSelection;
	
	private List<GPOAckDeptsEntity> gpOackForm;
}
