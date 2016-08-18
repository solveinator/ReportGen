package application;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 * 
 * This class contains all the information needed to create the FreshAlliance specific report. 
 *
 */
public class FreshAlliance extends ReportTemplate {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private ArrayList<String> columns;
	private String query;
	
	/**
	 * @param name String - The name of the report
	 * @param start LocalDateTime - The LocalDateTime which will be used as the beginning of the 
	 * query timeframe. 
	 * @param end LocalDateTime - The LocalDateTime which will be used as the end of the 
	 * query timeframe. 
	 */
	public FreshAlliance(String name, LocalDateTime start, LocalDateTime end) {
		super(name, true, ReportTemplate.M);
		//query = "SELECT name FROM sysobjects WHERE xtype = 'V'";
		query = "SELECT LogDate, DonorRef, DonorName, ProductRef FROM NFBSData.dbo.uqryReceipts_ReceiptDet"; 
		//query = "SELECT * FROM NFBSData.information_schema.tables WHERE TABLE_TYPE='BASE TABLE'";
		//query = "SELECT * FROM NFBSData.dbo.uqryReceipts_ReceiptDet";
		//query = "SELECT uqryReceipts_ReceiptDet.LogDate, uqryReceipts_ReceiptDet.DonorRef, " +
		//		"uqryReceipts_ReceiptDet.DonorName, uqryReceipts_ReceiptDet.ProductRef " + 
		//		"FROM NFBSData.dbo.uqryReceipts_ReceiptDet uqryReceipts_ReceiptDet " +
		//		"WHERE uqryReceipts_ReceiptDet.LogDate >= " + getFormattedTS(start) +  
		//		"And uqryReceipts_ReceiptDet.LogDate < " + getFormattedTS(end);
		columns = new ArrayList<String>(4);
		//columns.add("name");
		columns.add("LogDate");
		columns.add("DonorRef");
		columns.add("DonorName");
		columns.add("ProductRef");
		
	}
	
	@Override
	public void makeReport (ArrayList<ArrayList<String>> results) {
		try {
			DataOut.exportToExcel("Report.xlsx", "Sheet1", this, results);
		}
		catch(IOException e) {System.out.println("IO Problem\n" + e.getMessage());}
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
}
