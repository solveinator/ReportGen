package application;

import java.util.ArrayList;
import java.util.Collections;

public class RecVsDistLast3Years extends ReportTemplate {
	
private ArrayList<String> cats;

	public RecVsDistLast3Years(char timeframe) {
		super("3 Year Received vs. Distributed", timeframe, true);
		setPrevDates(timeframe);
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		DataOut.makeSingleDoubleBar("Received v. Distributed", cats, getBarData(results),
				"Received", "Distributed");

	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		cats = new ArrayList<String>();
		//Dates only - no years
		String staDate = startTimeStamp.substring(11,15).replace("-", "/");
		String endDate = endTimeStamp.substring(11,15).replace("-", "/");
		
		//Years only - no month/day
		int staYear = Integer.parseInt(startTimeStamp.substring(5,9)) - 2;
		int endYear = Integer.parseInt(endTimeStamp.substring(5,9)) - 2;
		String colName;
		String query;
		
		for(int i = 0; i < 3; i++) {
			//Received	
			String startTime = "{ts '" + staYear + startTimeStamp.substring(9);
			String endTime = "{ts '" + endYear + endTimeStamp.substring(9);
			
			cats.add((staYear + "").substring(2,4) + "-" + (endYear + "").substring(2, 4));
			colName = "R" + (staYear + "").substring(2,4) + (endYear + "").substring(2, 4);
			cols.add(colName);
			query = "SELECT SUM(ReceivedWeight) AS " + colName 
					+ " FROM NFBSData.dbo.uqryReceiptDetailwoDates " 
					+ "WHERE LogDate >= " + startTime + " AND LogDate <= " + endTime
					+ "AND (ProductRef <> '1335' AND ProductRef <> '1043' "
					+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') ";
			queries.add(query);
			
			//Distributed
			colName = "D" + (staYear + "").substring(2,4) + (endYear + "").substring(2, 4);
			cols.add(colName);
			query = "SELECT SUM(Weight) AS " + colName + " FROM NFBSData.dbo.uqryOrderDetailswoDates " 
					+ "WHERE (PickupDate >= " + startTime + " AND PickupDate <= " + endTime + ") " 
					+ "AND PickedUp = 'TRUE' AND ProductRef <> '1335'"
					+ "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' )"
					+ "AND (ProductRef <> '1335' AND ProductRef <> '1043' "
					+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') ";	
			queries.add(query);
			
			staYear = staYear + 1;
			endYear = endYear + 1; 
			
		}
		//Collections.reverse(cols);
		System.out.println(cols);
		System.out.println(cats);
		System.out.println(queries);
	}
	
	public static void main(String[] args) {
		ReportTemplate.initReports();
		ReportTemplate temp = new RecVsDistLast3Years(ReportTemplate.A);
		temp.makeQueries();
	}
}
