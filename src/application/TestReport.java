package application;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 *
 */
public class TestReport extends ReportTemplate {
	
	private ArrayList<String> columns;
	private ArrayList<String> queries;
	/**
	 * @param name
	 */
	public TestReport(String name) {
		super(name, true, ReportTemplate.M);
		queries = new ArrayList<String>();
		String query = "Select FirstName, LastName, FactInternetSales.CustomerKey, ProductKey, " +
				"OrderDateKey, DueDateKey, TotalProductCost, TaxAmt " +
				"From FactInternetSales Join DimCustomer " +
				"On FactInternetSales.CustomerKey = DimCustomer.CustomerKey " +
				"Where TotalProductCost >= '1800'"; //+
		queries.add(query);
				//"And uqryReceipts_ReceiptDet.LogDate >= " + getFormattedTS(YStaDate.minusYears(2)) +  
				//"And uqryReceipts_ReceiptDet.LogDate < " + getFormattedTS(YEndDate.minusYears(2));
		columns = new ArrayList<String>(5);
		columns.add("FirstName");
		columns.add("LastName");
		columns.add("CustomerKey");
		columns.add("ProductKey");
		columns.add("OrderDateKey");
		columns.add("DueDateKey");
		columns.add("TotalProductCost");
		columns.add("TaxAmt");
	}

	@Override
	public ArrayList<ArrayList<String>> cleanData(ArrayList<ArrayList<String>> list) {
		// TODO Auto-generated method stub
		return list;
	}

	@Override
	public void format(String excelFileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> getColumns() {
		// TODO Auto-generated method stub
		return columns;
	}

	@Override
	public void setTargets(ArrayList<String> targets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<String> getTargets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getQueryList() {
		return queries;
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results) {
		try {
			DataOut.exportToExcel("Report.xlsx", "TestData", this, results);
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
}
