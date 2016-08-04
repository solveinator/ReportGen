package application;

import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 *
 */
public class TestReport extends ReportTemplate {
	
	/**
	 * @param name
	 */
	public TestReport(String name) {
		super(name);
		String query = "Select ProductKey, OrderDateKey, DueDateKey from FactInternetSales Where ProductKey = '310'"; //+
				//"And uqryReceipts_ReceiptDet.LogDate >= " + getFormattedTS(YStaDate.minusYears(2)) +  
				//"And uqryReceipts_ReceiptDet.LogDate < " + getFormattedTS(YEndDate.minusYears(2));
		ArrayList<String> columns = new ArrayList<String>(4);
		columns.add("ProductKey");
		columns.add("OrderDateKey");
		columns.add("DueDateKey");
		setColumns(columns);
		setSQL(query);
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
}
