package application;

import java.util.ArrayList;
import java.util.TreeMap;
import java.time.LocalDateTime;

public class RecVsDistRolling12Mos extends ReportTemplate {
	
	private ArrayList<String> cats;
	private ArrayList<LocalDateTime> staDates;
	private ArrayList<LocalDateTime> endDates;
	private TreeMap<String,String> oldData;

	public RecVsDistRolling12Mos(char timeframe) {
		super("Received vs. Distributed Rolling 12 Month", timeframe, true);
		setPrevDates(timeframe);
		
		cols = new ArrayList<String>();
		cats = new ArrayList<String>();
		staDates = new ArrayList<LocalDateTime>();
		endDates = new ArrayList<LocalDateTime>();
		
		//Get info for quarter just ended minus 5 years. 
		LocalDateTime starDate = PQStaDate.minusYears(5);
		int fiscalYear = 0;
		String fy1; 
		String fy2;

		if(fiscalQuarter == 2) {
			fiscalYear = Today.getYear() - 4;
		}
		else if(fiscalQuarter == 3 || fiscalQuarter == 4 || fiscalQuarter == 1) {
			fiscalYear = Today.getYear() - 5;
		}
		else { 
			System.out.println("ERROR: " + fiscalQuarter + " is not a real quarter number. Fix it.");
		}
		int quar = prevFiscalQuarter;
		
		for(int i = 0; i < 17; i++) {
			fy1 = fiscalYear + ""; 
			fy2 = (fiscalYear + 1) + "";
			String fQuar = "FY" + fy1.substring(2) + "-" + fy2.substring(2) + "Q" + quar;
			cats.add(fQuar);
			cols.add("R" + fy1);
			cols.add("D" + fy1);
			//Not a typo --> supposed to be here twice;
			staDates.add(starDate);
			endDates.add(starDate.plusMonths(3).withHour(23).withMinute(59));
			//Increment 
			quar = (quar % 4) + 1;
			if(fiscalQuarter == 1) {
				fiscalYear++;
			}
			starDate = starDate.plusMonths(3);
		}
		makeQueries(null,null);
		//Get most recently ending Quarter number and fiscal year. 
		//Get last year years before that. You should have 17 quarters total. 
		
		//Load in old data		
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		//Separate the Rec and Dist data in the results
		ArrayList<String> rec = new ArrayList<String>();
		ArrayList<String> dec = new ArrayList<String>();
		
		for(int i=0; i < cats.size(); i++) {
			rec.add(results.get(i*2).get(0));
			dec.add(results.get(i*2 + 1).get(0));
		}
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		String query; 
		for(int i = 0; i < cats.size(); i++) {
			query = "SELECT SUM(ReceivedWeight) As " + cols.get(2*i) + 
					" FROM NFBSData.dbo.uqryReceiptDetailwoDates " + 
					"WHERE (ProductRef <> '1335' " +
					"AND ProductRef <> '1043' AND ProductRef <> '1062' " +
					"AND ProductRef <> '1064' AND ProductRef <> '1071') " +
					"AND (LogDate >= " + getFormattedTS(staDates.get(i)) + 
					" AND LogDate <= " + getFormattedTS(endDates.get(i)) + ")";
							
			queries.add(query);
			System.out.println(query);
			query = "SELECT SUM(Weight) As " + cols.get(2*i + 1) + 
					" FROM NFBSData.dbo.uqryOrderDetailswoDates " +
					"WHERE (ProductRef <> '1335' " +
					"AND ProductRef <> '1043' AND ProductRef <> '1062' " +
					"AND ProductRef <> '1064' AND ProductRef <> '1071') " +
					"AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) " +
					"AND (PickupDate >= " + getFormattedTS(staDates.get(i)) + 
					" AND PickupDate <= " + getFormattedTS(endDates.get(i)) + ")";
			queries.add(query);	
			System.out.println(query);
		}
		
	}

}
