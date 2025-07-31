package in.apcfss.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import in.apcfss.entities.GPOAckDetailsEntity;
import in.apcfss.entities.MyEntity;
import jakarta.transaction.Transactional;

@Repository
public interface ScanDocsCountRepo extends JpaRepository<MyEntity, Long>{

 
	@Query(nativeQuery = true,value = "select year,month,filecount,pages_count,available_scanned_affidavits, (filecount-available_scanned_affidavits) as not_available_scanned_affidavits "
			+ " from( select substring(hc_scan_legacy_date::text,1,4) as year,substring(hc_scan_legacy_date::text,6,2) as month ,count(*) as filecount,"
			+ "  COALESCE(SUM(NULLIF(pages_count, '(null)')::int), 0) as pages_count,"
			+ "  sum(case when is_scandocs_exists is true then 1 else 0 end) as available_scanned_affidavits,"
			+ "  sum(case when is_scandocs_exists is false then 1 else 0 end) as not_available_scanned_affidavits from ecourts_case_data"
			+ "  where scanned_document_path is not null group by substring(hc_scan_legacy_date::text,1,4),substring(hc_scan_legacy_date::text,6,2)  ) x ")
	List<Map<String, Object>> ScanDocsCount();

	
	@Query(nativeQuery = true,value = " SELECT year, month ,filecount,available_new,  available_old , available_old_pp,available_new_pp , "
			+ " available_new_p ,available_old_p , available_old_pp_p , available_new_pp_p , pages_count"
			+ "  FROM (SELECT SUBSTR(A.inserted_time::TEXT,1,4) AS YEAR,SUBSTR(A.inserted_time::TEXT,6,2) AS MONTH,count(*) as filecount, "
			+ "  sum(case when ack_type='NEW' and inserted_by NOT like '%PP%' AND file_found='Yes' then 1 else 0 end) as available_NEW,"
			+ "  sum(case when ack_type='OLD' and inserted_by NOT like '%PP%' AND file_found='Yes' then 1 else 0 end) as available_OLD,"
			+ "  sum(case when ack_type='OLD' and inserted_by like '%PP%' AND file_found='Yes' then 1 else 0 end) as available_OLD_pP,"
			+ "  sum(case when ack_type='NEW' AND inserted_by like '%PP%' AND file_found='Yes' then 1 else 0 end) as available_NEW_pP,"
			+ "  sum(case when ack_type='NEW' then pages_count::int else 0 end) as available_NEW_p,"
			+ "  sum(case when ack_type='OLD' then pages_count::int else 0 end) as available_OLD_p,"
			+ "  sum(case when ack_type='OLD' and inserted_by like '%PP%' then pages_count::int else 0 end) as available_OLD_pP_P,"
			+ "  sum(case when ack_type='NEW' AND inserted_by like '%PP%' then pages_count::int else 0 end) as available_NEW_pP_P,"
			+ "  sum(pages_count::int) as pages_count"
			+ "  FROM ecourts_gpo_ack_dtls a "
			+ "  INNER join scanned_affidavit_new_cases_count b on (a.ack_no=b.ack_no or a.hc_ack_no=b.ack_no)"
			+ "  GROUP BY SUBSTR(A.inserted_time::TEXT,1,4),SUBSTR(A.inserted_time::TEXT,6,2) order by  YEAR  desc,MONTH desc ) X ")
	List<Map<String, Object>> getScanCountForNewCases();



	@Query(nativeQuery = true,value = "select cino,hc_scan_legacy_by,hc_scan_legacy_date from ecourts_case_data "
			+ "  where scanned_document_path is not null and pages_count is null and substring(hc_scan_legacy_date::text,1,4)=:year  and substring(hc_scan_legacy_date::text,6,2)=:month ")
	List<Map<String, Object>> getNotAvailableOldScanDocsCount(@Param("year") String year,@Param("month") String month);

}
