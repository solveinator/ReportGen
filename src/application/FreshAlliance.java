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
	
	/**
	 * @param name String - The name of the report
	 * @param start LocalDateTime - The LocalDateTime which will be used as the beginning of the 
	 * query timeframe. 
	 * @param end LocalDateTime - The LocalDateTime which will be used as the end of the 
	 * query timeframe. 
	 */
	public FreshAlliance(LocalDateTime start, LocalDateTime end) {
		super("FreshAlliance", ReportTemplate.M, false);
		//setPrevDates(timeframe);
		//query = "SELECT name FROM sysobjects WHERE xtype = 'V'";
		
		cols = new ArrayList<String>(4);
		//columns.add("name");
		cols.add("LogDate");
		cols.add("DonorRef");
		cols.add("DonorName");
		cols.add("ProductRef");
		
	}
	
	@Override
	public void makeReport (ArrayList<ArrayList<String>> results, String time) {
		try {
			DataOut.exportToExcel("Sheet1", this, results);
		}
		catch(IOException e) {System.out.println("IO Problem\n" + e.getMessage());}
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query = "SELECT LogDate, DonorRef, DonorName, ProductRef "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet"; 
		//query = "SELECT * FROM NFBSData.information_schema.tables WHERE TABLE_TYPE='BASE TABLE'";
		//query = "SELECT * FROM NFBSData.dbo.uqryReceipts_ReceiptDet";
		//query = "SELECT uqryReceipts_ReceiptDet.LogDate, uqryReceipts_ReceiptDet.DonorRef, " +
		//		"uqryReceipts_ReceiptDet.DonorName, uqryReceipts_ReceiptDet.ProductRef " + 
		//		"FROM NFBSData.dbo.uqryReceipts_ReceiptDet uqryReceipts_ReceiptDet " +
		//		"WHERE uqryReceipts_ReceiptDet.LogDate >= " + getFormattedTS(start) +  
		//		"And uqryReceipts_ReceiptDet.LogDate < " + getFormattedTS(end);
		queries.add(query);
		
	}
}
