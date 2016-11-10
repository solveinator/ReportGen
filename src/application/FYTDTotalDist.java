package application;

import java.util.ArrayList;

public class FYTDTotalDist extends ReportTemplate {

	
	public FYTDTotalDist(char timeframe) {
		super("FYTD Total Distributed", timeframe, false);
		setCurDates(timeframe);
		shortName = "Fiscal Year-to-Date Total Distributed";
		
		queries = new ArrayList<String>();
		cols = new ArrayList<String>();
		cols.add("Dry");
		cols.add("Fresh");
		cols.add("Frozen");
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		DataOut.makePieChart(shortName + " for " + time, ReportTemplate.getPieData(cols, results));
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query;
		String common = "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) " 
				+ "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
				+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') " //Non-food items
				+ "AND (PickedUp = 'TRUE') "
				+ "AND (PickupDate >= " + startTimeStamp + " AND "
				+ 		"PickupDate <= " + endTimeStamp + ")";
		
		query = "SELECT SUM(Weight) As Dry "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE Storage LIKE 'Dry' "
				+ common;
		queries.add(query);
		System.out.println(query);
		
		query = "SELECT SUM(Weight) As Fresh "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE Storage LIKE 'FRESH' " 
				+ common;
		queries.add(query);
		System.out.println(query);
	
		query = "SELECT SUM(Weight) As Frozen "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE Storage LIKE 'Frozen' "
				+ common;
		queries.add(query);
		System.out.println(query);
		
	}

}
