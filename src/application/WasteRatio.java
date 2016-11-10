package application;

import java.util.ArrayList;

public class WasteRatio extends ReportTemplate {

	public WasteRatio(char timeframe) {
		super("Waste Ratio for Specific Products", timeframe, false);
		setPrevDates(timeframe);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		// TODO Auto-generated method stub
		
	}

}
