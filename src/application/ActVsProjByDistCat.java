package application;

import java.util.ArrayList;

public class ActVsProjByDistCat extends ReportTemplate {

	private ArrayList<String> cats;

	/**
	 * Bar 1 = Dry Goal=?  
	 * Bar 2 = Fresh Goal=? 
	 * Bar 3 = Frozen Goal=? 
	 * Bar 4 = Produce Goal=? 
	 * 
	 * @param reportName
	 * @param timeframe
	 */
	public ActVsProjByDistCat(char timeframe) {
		super("Actuals vs Projected by Distribution Category", timeframe, false);
		setPrevDates(timeframe);
		
		cols = new ArrayList<String>();
		cats = new ArrayList<String>();
		
//			query = "Select FirstName, LastName, FactInternetSales.CustomerKey, ProductKey, " +
//				"OrderDateKey, DueDateKey, TotalProductCost, TaxAmt " +
//				"From FactInternetSales Join DimCustomer " +
//				"On FactInternetSales.CustomerKey = DimCustomer.CustomerKey " +
//				"Where TotalProductCost >= '1800'"; 
//			queries.add(query);
		
		/*
		 * Reports Agency --> Distribution Summary --> Distribution Summary by Storage 
		 * --> Set Date Range, Optional Selections --> Product Ref. Does Not Equal 1335 & 1305 
		 * --> Agency Ref. Not Between W207-W295
		 */
			
		cols = new ArrayList<String>();		
		cols.add("Dry");
		cols.add("Fresh");
		cols.add("Frozen");
		cols.add("Produce");
		
		cats = new ArrayList<String>(5);
		cats.add("Dry");
		cats.add("Fresh");
		cats.add("Frozen");
		cats.add("Produce");
		
//		tars = new ArrayList<String>(5);
//		tars.add(80000 + "");
//		tars.add(20000 + "");
//		tars.add(25000 + "");
//		tars.add(45000 + "");
	}

	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query;
		String commonTerms = "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) " 
				+ "AND (ProductRef <> '1335' AND ProductRef <> '1305' "
				+ "AND ProductRef <> '1043' AND ProductRef <> '1061' "
				+ "AND ProductRef <> '1064' AND ProductRef <> '1071') "
				+ "AND (PickedUp = 'TRUE') "
				+ "AND (PickupDate >= " + startTimeStamp + " AND "
				+ 		"PickupDate <= " + endTimeStamp + ")";
		
		query = "SELECT SUM(Weight) As Dry "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE Storage = 'Dry' "
				+ commonTerms;
		queries.add(query);
		
		query = "SELECT SUM(Weight) As Fresh "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE Storage = 'FRESH' "
				+ commonTerms;
		
		queries.add(query);
	
		query = "SELECT SUM(Weight) As Frozen "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE Storage = 'Frozen' "
				+ commonTerms;
		queries.add(query);
	
		query = "SELECT SUM(Weight) As Produce "
				+ "FROM NFBSData.dbo.uqryOrderDetailswoDates "
				+ "WHERE (AgencyRef > 'W295' OR AgencyRef < 'W207' ) " 
				+ "AND (ProductRef = '1305' ) " 
				+ "AND (PickedUp = 'TRUE') "
				+ "AND (PickupDate >= " + startTimeStamp + " AND "
				+ 		"PickupDate <= " + endTimeStamp + ")";
		queries.add(query);
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		ArrayList<String> barData = ReportTemplate.getBarData(results);
		
		DataOut.makeBarChartLineOverlay(super.getShortName() + " for " + time, cats, barData, 
				targs.getTargets(this.getClass().getSimpleName(), cols, days));
		
	}

}
