package application;

import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_11
 * 
 * This is a copy of Test Report only it is technically a distribution report.
 *
 */
public class TestDistReport extends ReportTemplate {

	private ArrayList tars;
	/**
	 * @param name
	 */
	public TestDistReport(char timeframe) {
		super("Test Distribution Report", timeframe, false);
		setPrevDates(timeframe);
		
		cols = new ArrayList<String>(3);
		cols.add("ProductKey");
		cols.add("TotalProductCost");
		cols.add("TaxAmt");
		
		tars = new ArrayList<String>(3);
		tars.add("5");
		tars.add("40");
		tars.add("300");
	}
	
	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		DataOut.makeBarChartLineOverlay(super.getShortName() + " for " + time, 
				cols, ReportTemplate.getBarData(results), tars);
		
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query;
		query = "Select Top 1 ProductKey from FactInternetSales";
		queries.add(query);
		
		query = "Select Top 1 TotalProductCost from FactInternetSales";
		queries.add(query);
		
		query = "Select Top 1 TaxAmt from FactInternetSales";
		queries.add(query);
			
	}
}
