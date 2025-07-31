package in.apcfss.repositories;

import java.net.InetAddress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.SectionOfficerDetailsEntity;
import jakarta.transaction.Transactional;


@Repository
public interface SectionOfficerDetailsRepo extends JpaRepository<SectionOfficerDetailsEntity, Long>{
	

	@Modifying
	@Transactional
    @Query(nativeQuery = true,value = "insert into section_officer_details (emailid, dept_id,designation,employeeid,mobileno,aadharno,inserted_by,inserted_ip, dist_id)"
    		+ " select distinct b.email,d.sdeptcode||d.deptcode,b.designation_id,b.employee_id,b.mobile1,uid,:userId,:entryIp,:distId from :tableName b "
    		+ " inner join dept_new d on (d.dept_code=:empDept) where b.email=:empid and trim(b.employee_identity)=:empsection and trim(b.post_name_en)=:empPost")
	int insertSectionOfficerDetails(@Param("userId")String userId,@Param("entryIp") InetAddress entryIp,
			@Param("distId") Integer distId,@Param("tableName") String tableName,@Param("empDept") String empDept,
			@Param("empid")String empid,@Param("empsection") String empsection,@Param("empPost") String empPost);


}
