package application;

import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeMap;

//import org.docx4j.dml.chart.CTPieChart;

public class DistByGeo extends ReportTemplate {

	//private ArrayList<String> queries;
	//private ArrayList<String> cols;
	//private String startTimeStamp;
	//private String endTimeStamp;
	
	/**
	 * Geographical Counties:
	 * Marion county & Polk County. 
	 * Geographical Regions:
	 * Salem, Keizer, Canyon, Dallas, Grand Ronde, Woodburn, Other(all other distribution areas).
	 * @param reportName
	 * @param timeframe
	 */
	public DistByGeo(char timeframe) {
		super("Total Distribution by Geographical Area", timeframe, false);
		setPrevDates(timeframe);
		String query = "Select TotalProductCost From FactInternetSales";
		//queries.add(query);
		
		cols.add("Marion");
		cols.add("Polk");
		cols.add("Other");
		cols.add("Salem");
		cols.add("Keizer");
		cols.add("Dallas");
		cols.add("Woodburn");
		cols.add("GrandRonde");
		cols.add("Canyon");		
		cols.add("Other"); 	
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		TreeMap<String,Integer> countyData = new TreeMap<String, Integer>();
		TreeMap<String,Integer> cityData = new TreeMap<String, Integer>();
		for(int i = 0; i < 3; i++) {
			if(results.get(i) != null && results.get(i).get(0) != null) {
				countyData.put(cols.get(i), (int) Float.parseFloat(results.get(i).get(0)));
			}
			else {
				countyData.put(cols.get(i), 0);
			}
		}
		for(int i = 3; i < results.size(); i++) {
			if(results.get(i) != null && results.get(i).get(0) != null) {
				cityData.put(cols.get(i), (int) Float.parseFloat(results.get(i).get(0)));
			}
			else {
				cityData.put(cols.get(i), 0);
			}
		} 
		DataOut.makeTwoPieCharts("Distribution By County for " + time, countyData, 
				"Distribution By Region for " + time, cityData);
	} 
	

	public static void main(String args[]) {
		//report.makeReport(null);
		TreeMap<String,Integer> pieData = new TreeMap<String,Integer>();
		for(int i = 0; i < 5; i++) {
        	pieData.put("Cat " + i, 5*i + 8);
        }
		DataOut.makePieChart("MyPie", pieData);
	}

	//Agency Ref. Not Between W207-W295
	
	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		queries = new ArrayList<String>();
		String query;
		String common = "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) " 
				+ "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
				+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') "
				+ "AND (PickedUp = 'TRUE') "
				+ "AND (PickupDate >= " + startTimeStamp + " AND "
				+ 		"PickupDate <= " + endTimeStamp + ")"; 
		
		query = "SELECT SUM(Weight) As Marion "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE \"County Code\" = 'Marion' " 
				+ common;
		
		queries.add(query);
		
		query = "SELECT SUM(Weight) As Polk "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE \"County Code\" = 'Polk' " 
				+ common;
		
		queries.add(query);
		
		query = "SELECT SUM(Weight) As Other "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE \"County Code\" <> 'Polk' "
				+ "AND \"County Code\" <> 'Marion' "
				+ common;
		
		queries.add(query);
		
		for(int i = 3; i < 7; i++) {
			query = "SELECT SUM(Weight) As " + cols.get(i) + " " 
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE City Like '" + cols.get(i) + "%' "
				+ common;
			
			queries.add(query);
		}
		
		query = "SELECT SUM(Weight) As GrandRonde "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE City Like 'Grand Ronde%' "
				+ common;
		
		queries.add(query);
		
		query = "SELECT SUM(Weight) As Canyon "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE (\"County Code\" = 'Polk' OR \"County Code\" = 'Marion') "
				+ "AND (City NOT LIKE 'Salem%' AND City NOT LIKE 'Keizer%' AND City NOT LIKE 'Dallas%' "
				+ "AND City NOT LIKE 'Woodburn%' AND City NOT LIKE 'Grand Ronde%') "
				+ common;
		
		queries.add(query);
		
		query = "SELECT SUM(Weight) As Other "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE \"County Code\" <> 'Polk' "
				+ "AND \"County Code\" <> 'Marion' "
				+ "AND (City NOT LIKE 'Salem%' AND City NOT LIKE 'Keizer%' AND City NOT LIKE 'Dallas%' "
				+ "AND City NOT LIKE 'Woodburn%' AND City NOT LIKE 'Grand Ronde%') "
				+ common;
		
		queries.add(query);		
		
	}
}
