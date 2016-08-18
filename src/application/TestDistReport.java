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
	
	private ArrayList<String> columns;
	private String query;
	/**
	 * @param name
	 */
	public TestDistReport(String name) {
		super(name, false, ReportTemplate.M);
		query = "Select ProductKey, OrderDateKey, DueDateKey from FactInternetSales Where ProductKey = '310'"; //+
				//"And uqryReceipts_ReceiptDet.LogDate >= " + getFormattedTS(YStaDate.minusYears(2)) +  
				//"And uqryReceipts_ReceiptDet.LogDate < " + getFormattedTS(YEndDate.minusYears(2));
		columns = new ArrayList<String>(4);
		columns.add("ProductKey");
		columns.add("OrderDateKey");
		columns.add("DueDateKey");
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
