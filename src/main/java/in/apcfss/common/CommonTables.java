package in.apcfss.common;

public class CommonTables {
	
	public static String getTableName(String distId) {

		String tableName = "nic_data";

		if (distId != null && !distId.equals("") && Integer.parseInt(distId) > 0) {
			String sql = "select tablename from district_mst where district_id=" + distId;
			System.out.println("dist::Id" + distId + "-tableName::" + tableName);

		}
		System.out.println("get table name" + tableName);

		return tableName;
	}

}
