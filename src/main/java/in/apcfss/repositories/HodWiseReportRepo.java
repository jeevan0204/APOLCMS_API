package in.apcfss.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.apcfss.entities.MyEntity;

public interface HodWiseReportRepo extends JpaRepository<MyEntity, Long>{

	@Query(value = "select b.dept_code,c.description,count(b.cino) as total from ecourts_case_data b "
			+ "inner join dept_new c on (c.dept_code=b.dept_code) WHERE b.dept_code!='0' group by b.dept_code,description" ,nativeQuery = true)
	List<Map<String, Object>> getHodwiseData();

	
	@Query(value="select b.dept_code,c.description,count(a.ack_no) as total from ecourts_gpo_ack_dtls a "
			+ "inner join ecourts_gpo_ack_depts b on (a.ack_no=b.ack_no) inner join dept_new c on (c.dept_code=b.dept_code)"
			+ " where ack_type='NEW' and b.dept_code!='0' and respondent_slno='1' group by b.dept_code,description",nativeQuery = true)
	List<Map<String, Object>> getHodwiseNewData();
}
