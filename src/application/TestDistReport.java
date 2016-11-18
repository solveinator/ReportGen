package application;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_11
 * 
 * This is a copy of Test Report only it is technically a distribution report.
 *
 */
public class TestDistReport extends ReportTemplate {

	//private ArrayList<String> tars;
	private ArrayList<String> singleQueries;
	
	/**
	 * @param name
	 */
	public TestDistReport(char timeframe) {
		super("Test Distribution Report", timeframe, false);
		setPrevDates(timeframe);
		ReportTemplate temp = new ProdRecBySrc(timeframe);
		singleQueries = temp.getQueryList();
		for(String query : singleQueries) {
			query.replaceAll("SUM(ReceivedWeight)", "*");
		}
//		cols = new ArrayList<String>(3);
//		cols.add("ProductKey");
//		cols.add("TotalProductCost");
//		cols.add("TaxAmt");
//		
//		tars = new ArrayList<String>(3);
//		tars.add("5");
//		tars.add("40");
//		tars.add("300");
		
		
	}
	
	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		try {
			DataOut.exportToExcel("TestData", this, results);
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}	
		
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		queries = new ArrayList<String>();
		String query; 
		query = "SELECT A.LogID, A.ProductID, A.DonorID, A.LogDate, A.DonorRef, A.DonorName, "
		+ "A.LogRef, A.TotalFreight, A.\"Class of Trade\", A.\"Delivered By\", A.ProductRef, "
		+ "A.ProductName, A.ReceivedDate, A.ReceivedWeight, A.Category "
		+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates as A " 
		+ "WHERE (A.LogDate >= {ts '2015-07-01 00:00:00'} " 
				+ "And A.LogDate < {ts '2016-07-01 00:00:00'}) ";	
		//Set up first combination with "AND"
		query = query + "AND (A.LogID IN (" + singleQueries.get(0) + ") " 
				+ "AND A.LogID IN (" + singleQueries.get(1) + ")) ";
		
		for(int i = 1; i < singleQueries.size(); i++ ) {
			for(int j = i + 1; j < singleQueries.size(); j++ ) {
				//Complete the rest of the combinations with an "OR"
				query = query + "OR (A.LogID IN (" + singleQueries.get(i) + ") " 
						+ "AND A.LogID IN (" + singleQueries.get(j) + ")) ";
			}
		}
		
		System.out.println(query);
		queries.add(query);
//		String query;
//		query = "Select Top 1 ProductKey from FactInternetSales";
//		queries.add(query);
//		
//		query = "Select Top 1 TotalProductCost from FactInternetSales";
//		queries.add(query);
//		
//		query = "Select Top 1 TaxAmt from FactInternetSales";
//		queries.add(query);
			
	}
}
