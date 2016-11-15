package application;

import java.util.ArrayList;

public class ProdRecBySrc extends ReportTemplate {

	private ArrayList<String> cats;

	/**
	 * Bar 1 = OFB/RFB/PA
	 * Bar 2 = Producers/Manufacturers
	 * Bar 3 = Retail Partners
	 * Bar 4 = Fooddrives/Post Retail
	 * Bar 5 = Farms/Gardens
	 * Bar 6 = USDA
	 * Bar 7 = Purchasing
	 */
	public ProdRecBySrc(char timeframe) {
		super("Product Received By Source", timeframe, true);
		setPrevDates(timeframe);
		cols = new ArrayList<String>();
		cats = new ArrayList<String>();

		cols.add("Agencies");
		cols.add("FoodProducers");
		cols.add("Retail");
		cols.add("FDAndPostRetail");
		cols.add("FarmAndGarden");
		cols.add("USDA");
		cols.add("Purchased");
				
		cats.add("OFB / RFB / PA");
		cats.add("Producers / Manufacturers");
		cats.add("Retail Partners");
		cats.add("Fooddrives / Post Retail");
		cats.add("Farms / Gardens");
		cats.add("USDA");
		cats.add("Purchased");
	}
	
	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		DataOut.makePieChart(super.getShortName() + " for " + time,
				ReportTemplate.getPieData(cats, results));
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query;
		String common = "AND (ProductRef <> '1043' AND ProductRef <> '1061' "
				+  		"AND ProductRef <> '1064' AND ProductRef <> '1071' "
				+ 		"AND ProductRef <> '1335') "
				+ "AND (LogDate >= " + startTimeStamp + " AND "
				+ 		"LogDate <= " + endTimeStamp + ")";
		/*Donor  --> Donor --> Total Pounds Received By Category --> 
		 * Set Date Range, Optional Selections.  --> 
		 * Product Category Does not Equal Government or MPFS MFG. -->
		 * Donor: Donor Ref is Between PA1703 to PA974, betweenRFB 2208 to RFB494 and equals 446. --> 
		 * Product Ref does not equal 1043, 1061, 1064, 1071, 1335                        
		 */
		query = "SELECT SUM(ReceivedWeight) As Agencies "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE (Category <> 'MPFS MFG' AND Category <> 'GOVERNMENT') "
				+ "AND (((DonorRef >= 'PA1703' AND DonorRef <= 'PA974') "
					+ "OR (DonorRef >= 'RFB2208' AND DonorRef <= 'RFB494') "
					+ "OR DonorRef = '446' ) " 
				+ "OR (uqryReceipts_ReceiptDet.LogID IN "
						+ "(SELECT uqryReceipts_Receipts.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
						+ "WHERE \"Donation Type\" Like '*Purchase%'))) "
				+ common;
		queries.add(query);
		
		query = "SELECT SUM(ReceivedWeight) As FoodProducers "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE uqryReceipts_ReceiptDet.LogID NOT IN "
					+ "(SELECT uqryReceipts_Receipts.LogID "
					+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
					+ "WHERE \"Donation Type\" Like '*Purchase%' "
				+ "AND (\"Class of Trade\" = '*Food Processor') "
				+ common;
		queries.add(query);
		
		query = "SELECT SUM(ReceivedWeight) As Retail "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE uqryReceipts_ReceiptDet.LogID NOT IN "
					+ "(SELECT uqryReceipts_Receipts.LogID "
					+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
					+ "WHERE \"Donation Type\" Like '*Purchase%' "
					+ "OR ( \"Donation Type\" >= '*FD- Individual Dock Delivery' "
						+ "And \"Donation Type\" <= '*FD-The Great Food Drive') ) "
				+ "AND (\"Class of Trade\" = '*Retail') "
				+ common;
			queries.add(query);
		
			query = "SELECT SUM(ReceivedWeight) As FDAndPostRetail "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
					+ "WHERE uqryReceipts_ReceiptDet.LogID IN "
						+ "(SELECT uqryReceipts_Receipts.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
						+ "WHERE \"Donation Type\" >= '*FD- Individual Dock Delivery' "
							+ "And \"Donation Type\" <= '*FD-The Great Food Drive') "
					+ "AND ProductRef <> '1305' "
					+ "AND ProductRef <> '2500' "
					+ "AND ProductRef <> '1406' "
					+ "AND ProductRef <> '1407' "
					+ "AND ProductRef <> '1408' "
					+ "AND ProductRef <> '1409' "
					+ "AND ProductRef <> '1410' "
					+ "AND ProductRef <> '1411' "
					+ common;
			queries.add(query);
			
			query = "SELECT SUM(ReceivedWeight) As FarmAndGarden "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE (\"Class of Trade\" = '*Farm' OR \"Class of Trade\" = '*Local Garden') "
					+ "AND (ProductRef = '1305' "
					+ "OR ProductRef = '2500' "
					+ "OR ProductRef = '1406' "
					+ "OR ProductRef = '1407' "
					+ "OR ProductRef = '1408' "
					+ "OR ProductRef = '1409' "
					+ "OR ProductRef = '1410' "
					+ "OR ProductRef = '1411' ) "
				+ common;
			queries.add(query);
		
			query = "SELECT SUM(ReceivedWeight) As USDA " 
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
					+ "WHERE (Category = 'GOVERNMENT') "
					+ common;
			queries.add(query);
					
			query = "SELECT SUM(ReceivedWeight) As Purchased "
					+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
					+ "WHERE uqryReceipts_ReceiptDet.LogID NOT IN "
						+ "(SELECT uqryReceipts_Receipts.LogID "
						+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
						+ "WHERE \"Donation Type\" Like '*Purchase%') "
					+ "AND (Category = 'AFTER HOURS BOX' OR Category = 'PURCHASED' OR Category = 'PURCH-BLK') "
					+ common;
			queries.add(query);
	} 
}
