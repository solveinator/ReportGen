package application;

import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 *
 */
public class TestReport extends ReportTemplate {
	
	private ArrayList<String> columns;
	private String query;
	/**
	 * @param name
	 */
	public TestReport(String name) {
		super(name, true, ReportTemplate.M);
		query = "Select FirstName, LastName, FactInternetSales.CustomerKey, ProductKey, " +
				"OrderDateKey, DueDateKey, TotalProductCost, TaxAmt " +
				"From FactInternetSales Join DimCustomer " +
				"On FactInternetSales.CustomerKey = DimCustomer.CustomerKey " +
				"Where TotalProductCost >= '1800'"; //+
		
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
	public String getSQL() {
		// TODO Auto-generated method stub
		return query;
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results) {
		// TODO Auto-generated method stub
		
	}
}
