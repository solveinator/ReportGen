package application;

import java.io.IOException;
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
	public TestReport(char timeframe) {
		super("Test Report", timeframe, true);
		setPrevDates(timeframe);
		cols = new ArrayList<String>(5);
//		cols.add("FirstName");
//		cols.add("LastName");
//		cols.add("CustomerKey");
//		cols.add("ProductKey");
//		cols.add("OrderDateKey");
//		cols.add("DueDateKey");
//		cols.add("TotalProductCost");
//		cols.add("TaxAmt");
		
		cols.add("LogID");
		cols.add("ProductID");
		cols.add("DonorID");
		cols.add("LogDate");
		cols.add("DonorRef");
		cols.add("DonorName");
		cols.add("LogRef");
		cols.add("TotalFreight");
		cols.add("Class of Trade");
		cols.add("Delivered By");
		cols.add("ProductRef");
		cols.add("ProductName");
		cols.add("ReceivedDate");
		cols.add("ReceivedWeight");
		cols.add("Category");		
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

//	@Override
//	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
//		String query = "Select FirstName, LastName, FactInternetSales.CustomerKey, ProductKey, " +
//				"OrderDateKey, DueDateKey, TotalProductCost, TaxAmt " +
//				"From FactInternetSales Join DimCustomer " +
//				"On FactInternetSales.CustomerKey = DimCustomer.CustomerKey " +
//				"Where TotalProductCost >= '1800'"; //+
//			//"And uqryReceipts_ReceiptDet.LogDate >= " + getFormattedTS(YStaDate.minusYears(2)) +  
//			//"And uqryReceipts_ReceiptDet.LogDate < " + getFormattedTS(YEndDate.minusYears(2));
//
//		queries.add(query);	
//	}
	
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query = "SELECT LogID, ProductID, DonorID, LogDate, DonorRef, DonorName, "
				+ "LogRef, TotalFreight, \"Class of Trade\", \"Delivered By\", ProductRef, "
				+ "ProductName, ReceivedDate, ReceivedWeight, Category "
				+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates as A " 
				+ "WHERE (A.LogDate >= {ts '2015-07-01 00:00:00'} " 
				+ "And A.LogDate < {ts '2016-07-01 00:00:00'}) "
				+ "And NOT EXISTS "
					+ "(SELECT * "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as B "
					+ "WHERE (Category <> 'MPFS MFG' AND Category <> 'GOVERNMENT') "
					+ "AND ((DonorRef >= 'PA1703' AND DonorRef <= 'PA974') "
					+ "OR (DonorRef >= 'RFB2208' AND DonorRef <= 'RFB494') "
					+ "OR DonorRef = '446' ) "
					+ "AND (A.LogID = B.LogID AND A.ProductRef = B.ProductRef) "
					+ ") "
				+ "And NOT EXISTS "
					+ "(SELECT * "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as C "
					+ "WHERE C.LogID NOT IN "
						+ "(SELECT D.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts as D "
						+ "WHERE D.\"Donation Type\" Like '*Purchase%' ) "
					+ "AND (C.\"Class of Trade\" = '*Food Processor') "
					+ "AND (A.LogID = C.LogID AND A.ProductRef = C.ProductRef) "
					+ ") "
				+ "AND NOT EXISTS " 
					+"(SELECT * "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as E "
					+ "WHERE E.LogID NOT IN "
						+ "(SELECT F.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts as F "
						+ "WHERE F.\"Donation Type\" Like '*Purchase%' "
						+ "OR ( F.\"Donation Type\" >= '*FD- Individual Dock Delivery' "
						+ "And F.\"Donation Type\" <= '*FD-The Great Food Drive') ) "
					+ "AND (E.\"Class of Trade\" = '*Retail') "
					+ "AND (A.LogID = E.LogID AND A.ProductRef = E.ProductRef) "
					+ ") "
				+ "AND NOT EXISTS "
					+ "(SELECT * "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as G "
					+ "WHERE ((G.\"Class of Trade\" = '*Farm' OR G.\"Class of Trade\" = '*Local Garden') "
					+ "OR (G.ProductRef = '1305' OR G.ProductRef = '2500' OR "
					+ 		"(G.ProductRef >= '1406' AND G.ProductRef <= '1411'))) "
					+ "AND (A.LogID = G.LogID AND A.ProductRef = G.ProductRef) "
					+ ") "
				+ "AND NOT EXISTS "
					+ "(SELECT * "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as H "
					+ "WHERE H.LogID IN "
						+ "(SELECT I.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts as I "
						+ "WHERE I.\"Donation Type\" >= '*FD- Individual Dock Delivery' "
							+ "And I.\"Donation Type\" <= '*FD-The Great Food Drive') "
					+ "AND H.ProductRef <> '1043' "
					+ "AND H.ProductRef <> '1061' "
					+ "AND H.ProductRef <> '1064' "
					+ "AND H.ProductRef <> '1071' "
					+ "AND H.ProductRef <> '1305' "
					+ "AND H.ProductRef <> '1335' "
					+ "AND H.ProductRef <> '2500' "
					+ "AND H.ProductRef <> '1406' "
					+ "AND H.ProductRef <> '1407' "
					+ "AND H.ProductRef <> '1408' "
					+ "AND H.ProductRef <> '1409' "
					+ "AND H.ProductRef <> '1410' "
					+ "AND H.ProductRef <> '1411' "
					+ "AND (A.LogID = H.LogID AND A.ProductRef = H.ProductRef) "
					+ ") "
				+ "AND NOT EXISTS "
					+ "(SELECT * " 
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as J "
					+ "WHERE (J.Category = 'GOVERNMENT') "
					+ "AND (A.LogID = J.LogID AND A.ProductRef = J.ProductRef) "
					+ ") "
				+ "AND NOT EXISTS "
					+ "(SELECT * "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet as K "
					+ "WHERE K.LogID IN "
						+ "(SELECT L.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts as L "
						+ "WHERE L.\"Donation Type\" Like '*Purchase%') "
					+ "AND (K.Category = 'AFTER HOURS BOX' OR K.Category = 'PURCHASED' OR K.Category = 'PURCH-BLK') "
					+ "AND (A.LogID = K.LogID AND A.ProductRef = K.ProductRef) "
					+ ") ";
		queries.add(query);
	}
}
