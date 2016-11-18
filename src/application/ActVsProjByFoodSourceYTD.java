package application;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ActVsProjByFoodSourceYTD extends ReportTemplate  {

	private ArrayList<String> cats;
	private long ytdDays; 
	private ArrayList<String> tarCats;
	
	public ActVsProjByFoodSourceYTD(char timeframe) {
		// TODO Auto-generated constructor stub
		super("Actuals vs. projected by Food Source with FYTD", timeframe, true);
		setPrevDates(timeframe);
		
		queries = new ArrayList<String>();			
		
		tarCats = new ArrayList<String>();
		tarCats.add("Agencies");
		tarCats.add("FoodProcessors");
		tarCats.add("Retail");
		tarCats.add("FarmAndGarden");
		tarCats.add("FDAndPostRetail");
		
		cols = new ArrayList<String>();	
		cols.add("AgenciesTY");
		cols.add("FoodProcessorsTY");
		cols.add("RetailTY");
		cols.add("FarmAndGardenTY");
		cols.add("FDAndPostRetailTY");
		
		cols.add("AgenciesLY");
		cols.add("FoodProcessorsLY");
		cols.add("RetailLY");
		cols.add("FarmAndGardenLY");
		cols.add("FDAndPostRetailLY");
		
		cats = new ArrayList<String>(5);
		cats.add("OFB / RFB / MPFS Agencies");
		cats.add("Food Processors/Manufacturers");
		cats.add("Retail / RAD");
		cats.add("Farm / Garden");
		cats.add("Food Drives / Post Retail / Individuals");
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		ArrayList<String> barData = new ArrayList<String>();
		ArrayList<String> ytdData = new ArrayList<String>();
		for(int i = 0; i < cats.size(); i++) {
			int dataPt = (int) Float.parseFloat(results.get(i).get(0));
			barData.add(dataPt + "");
		}
		for(int i = cats.size(); i < 2*cats.size(); i++) {
			int dataPt = (int) Float.parseFloat(results.get(i).get(0));
			ytdData.add(dataPt + "");
		}
		ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
		allData.add(barData);
		allData.add(targs.getTargets(this.getClass().getSimpleName(), tarCats, days));
		allData.add(ytdData);
		allData.add(targs.getTargets(this.getClass().getSimpleName(), tarCats, ytdDays));
		ArrayList<String> headers = new ArrayList<String> ();
		headers.add("Actual");
		headers.add("Target");
		headers.add("Actual YTD");
		headers.add("Target YTD");

		DataOut.makeDoubleBarLineOverlay(super.getShortName()+" for "+ time, headers, cats,
				allData);
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query;
	
		//Start with this year. 
		String yearName = "TY";
		for(int i = 0; i < 2; i++) {
			//On the second time through, change all necessary variable so reflect FYTD.
			if(i == 1) {
				endTimeStamp = this.getFormattedTS(Today);
				ytdDays = ChronoUnit.DAYS.between(FYStaDate, Today) + 1;
				yearName = "LY";
			}
			String common = "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
					+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') "
					+ "AND (LogDate >= " + startTimeStamp + " AND "
					+ 		"LogDate <= " + endTimeStamp + ")";

			query = "SELECT SUM(ReceivedWeight) As Agencies" + yearName + " "
				+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates "
				+ "WHERE (Category <> 'MPFS MFG' AND Category <> 'Government') " 
				+ "AND (DonorRef Like 'PA%' OR  DonorRef Like 'RFB%' OR DonorRef = '446') " 
				+ common;
			queries.add(query);
		
			query = "SELECT SUM(ReceivedWeight) As FoodProcessors" + yearName + " "
				+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates "
				+ "WHERE uqryReceiptDetailwoDates.LogID NOT IN "
					+ "(SELECT uqryReceipts_Receipts.LogID "
					+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
					+ "WHERE \"Donation Type\" Like '*Purchase%' ) "
				+ "AND (\"Class of Trade\" = '*Food Processor') "
				+ common;
			queries.add(query);
	
			query = "SELECT SUM(ReceivedWeight) As Retail" + yearName + " "
					+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates "
					+ "WHERE uqryReceiptDetailwoDates.LogID NOT IN "
					+ "(SELECT uqryReceipts_Receipts.LogID "
					+ "FROM NFBSData.dbo.uqryReceipts_Receipts "
					+ "WHERE \"Receipt Type\" = 'PreReceipt' "
					+ "OR \"Donation Type\" Like '*Purchase%' "
					+ "OR ( \"Donation Type\" >= '*FD- Individual Dock Delivery' "
						+ "And \"Donation Type\" <= '*FD-The Great Food Drive') ) "
					+ "AND (\"Class of Trade\" = '*Retail') "
					+ common;
			queries.add(query);
	
			query = "SELECT SUM(ReceivedWeight) As FarmAndGarden" + yearName + " "
				+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates "
				+ "WHERE (\"Class of Trade\" = '*Farm' OR \"Class of Trade\" = '*Local Garden') "
				+ "AND (ProductRef = '1305' OR ProductRef = '2500' OR "
				+ 		"(ProductRef >= '1406' AND ProductRef <= '1411')) "
				+ "AND (LogDate >= " + startTimeStamp + " AND "
				+ 		"LogDate <= " + endTimeStamp + ")";
			queries.add(query);
		
			query = "SELECT SUM(ReceivedWeight) As FDAndPostRetail" + yearName + " "
				+ "FROM NFBSData.dbo.uqryReceiptDetailwoDates "
				+ "WHERE uqryReceiptDetailwoDates.LogID IN "
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
				+ "AND (LogDate >= " + startTimeStamp + " AND "
				+ 		"LogDate <= " + endTimeStamp + ")";
			queries.add(query);
		}
	}
}
