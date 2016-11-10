package application;

import java.util.ArrayList;
import java.io.IOException;

public class ActVsProjByFoodSource extends ReportTemplate {

	/**
	 * Bar 1 = OFB/RFB/MPFS Agencies Goal=875,000  
	 * Bar 2 = Food Processors/Manufacturers Goal=337,500 
	 * Bar 3 = Retail/RAD Goal=575,000 
	 * Bar 4 = Farm/Garden Goal=250,000 
	 * Bar 5 = Food Drives/Post Retail/Individuals Goal=87,500
	 * 
	 * @param reportName
	 * @param timeframe
	 */
	
	private ArrayList<String> cats;

	public ActVsProjByFoodSource(char timeframe) {
		super("Actuals vs. projected by Food Source", timeframe, true);
		setPrevDates(timeframe);
		
		queries = new ArrayList<String>();			
		
		cols = new ArrayList<String>();
		
		cols.add("Agencies");
		cols.add("FoodProcessors");
		cols.add("Retail");
		cols.add("FarmAndGarden");
		cols.add("FDAndPostRetail");
		
		cats = new ArrayList<String>(5);
		cats.add("OFB / RFB / MPFS Agencies");
		cats.add("Food Processors/Manufacturers");
		cats.add("Retail / RAD ");
		cats.add("Farm / Garden ");
		cats.add("Food Drives / Post Retail / Individuals");
		
//		tars = new ArrayList<String>(5);
//		tars.add(875000 + "");
//		tars.add(337500 + "");
//		tars.add(575000 + "");
//		tars.add(250000 + "");
//		tars.add(87500  + "");
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
//		try {
//		DataOut.exportToExcel("Report.xlsx", "TestSheet", this, results);
//		}
//		catch(IOException e) {
//			System.out.println(e.getMessage());
//		}
		
		ArrayList<String> barData = new ArrayList<String>();
		for(int i = 0; i < results.size(); i++) {
			int dataPt = (int) Float.parseFloat(results.get(i).get(0));
			barData.add(dataPt + "");
		}
//		barData.add(900000 + "");
//		barData.add(200000 + "");
//		barData.add(550000 + "");
//		barData.add(200000 + "");
//		barData.add(50000 + "");
		DataOut.makeBarChartLineOverlay(super.getShortName()+" for "+ time, cats, barData, 
				targs.getTargets(this.getClass().getSimpleName(), cols, days));
		
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query;
		String common = "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
				+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') "
				+ "AND (LogDate >= " + startTimeStamp + " AND "
				+ 		"LogDate <= " + endTimeStamp + ")";
		
		query = "SELECT SUM(ReceivedWeight) As Agencies "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE (Category <> 'MPFS MFG' AND Category <> 'Government') " 
				+ "AND (DonorRef Like 'PA%' OR  DonorRef Like 'RFB%' OR DonorRef = '446') " 
				+ common;
		
		queries.add(query);
		
		query = "SELECT SUM(ReceivedWeight) As FoodProcessors "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE uqryReceipts_ReceiptDet.LogID NOT IN "
					+ "(SELECT uqryReceipts_Receipts.LogID "
					+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
					+ "WHERE \"Donation Type\" Like '*Purchase%' ) "
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
	
		query = "SELECT SUM(ReceivedWeight) As FarmAndGarden "
			+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
			+ "WHERE (\"Class of Trade\" = '*Farm' OR \"Class of Trade\" = '*Local Garden') "
			+ "AND (ProductRef = '1305' OR ProductRef = '2500' OR "
			+ 		"(ProductRef >= '1406' AND ProductRef <= '1411')) "
			+ common;
		
		queries.add(query);
		
		query = "SELECT SUM(ReceivedWeight) As FDAndPostRetail "
				+ "FROM NFBSData.dbo.uqryReceipts_ReceiptDet "
				+ "WHERE uqryReceipts_ReceiptDet.LogID IN "
					+ "(SELECT uqryReceipts_Receipts.LogID "
					+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
					+ "WHERE \"Donation Type\" >= '*FD- Individual Dock Delivery' "
						+ "And \"Donation Type\" <= '*FD-The Great Food Drive') "
				+ "AND ProductRef <> '1043' "
				+ "AND ProductRef <> '1061' "
				+ "AND ProductRef <> '1064' "
				+ "AND ProductRef <> '1071' "
				+ "AND ProductRef <> '1305' "
				+ "AND ProductRef <> '1335' "
				+ "AND ProductRef <> '2500' "
				+ "AND ProductRef <> '1406' "
				+ "AND ProductRef <> '1407' "
				+ "AND ProductRef <> '1408' "
				+ "AND ProductRef <> '1409' "
				+ "AND ProductRef <> '1410' "
				+ "AND ProductRef <> '1411' "
				+ common;
		queries.add(query);
	}

}
