package application;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeMap;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

/**
 * The class acts as a template for all the reports. It contains a master list of resourcing and
 * distribution reports, stored separately. 
 * 
 * It also contains a list of the columns and the SQL to get the necessary pertinent data.
 * 
 * Lastly, it contains several import dates which are automatically generated to streamline access. 
 * 
 * @author Solveig Osborne
 * @version 2016_08_03
 */
public abstract class ReportTemplate {
	/**
	 * Codes are:
	 * A: Annually
	 * S: Semiannually
	 * Q: Quarterly
	 * M: Monthly
	 * W: Weekly
	 * O: Other - used for a custom date range. 
	 * N: None - includes no time ranges. 
	 * 
	 * @return char The timeframe char code. The 
	 */
	protected static char A = ("A").charAt(0); 
	protected static char S = ("S").charAt(0); 
	protected static char Q = ("Q").charAt(0);
	protected static char M = ("M").charAt(0);
	protected static char W = ("W").charAt(0);
	protected static char O = ("O").charAt(0);
	protected static char N = ("N").charAt(0);
	protected static HashMap<Character,String> timeFrameMap;
	
	private static ArrayList<ReportTemplate> reports;
	protected ArrayList<String> queries;
	protected ArrayList<String> cols;
	protected LocalDateTime defaultStartDate;
	protected LocalDateTime defaultEndDate;
	protected LocalDateTime startDate;
	protected LocalDateTime endDate;
	protected String longName;
	protected String shortName;
	protected long days;
	protected Targets targs;
	private char timeframe;
	private boolean resStatus; //true for resource reports; false for distribution reports

	//This Date
	protected static LocalDateTime Today;
	
	//Start and End of year
	protected static LocalDateTime FYStaDate;
	protected static LocalDateTime FYEndDate;
	protected static LocalDateTime PFYStaDate;
	protected static LocalDateTime PFYEndDate;
	
	//Start and End of Quarter
	protected static int fiscalQuarter;
	protected static int prevFiscalQuarter;
	protected static String fqName;
	protected static long daysInQuarter;
	protected static long daysInPrevQuarter;
	
	protected static LocalDateTime QStaDate;
	protected static LocalDateTime QEndDate;
	protected static LocalDateTime PQStaDate;
	protected static LocalDateTime PQEndDate;
	
	//Start and End of Month
	protected static long daysInMonth;
	protected static LocalDateTime MStaDate;
	protected static LocalDateTime MEndDate;
	protected static LocalDateTime PMStaDate;
	protected static LocalDateTime PMEndDate;
	
	private static float FYTDExptrapScale;
	private static float QuarterExtrapScale;
	private static float MonthExtrapScale;
	
	/**
	 * This constructor creates a new instance of ReportTemplate with the given name. 
	 * 
	 * @param reportName String - The name of the report.
	 */
	public ReportTemplate(String repName, char timeframe, boolean resource) {
		try {
			targs = new Targets("txtFiles/Targets.txt");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		if(timeframe == S) {
			this.longName = "Semiannually: " + repName;
		}
		else if (timeframe == A) {
			this.longName = "Annually: " + repName;
		}
		else {
			this.longName = timeFrameMap.get(timeframe) + "ly: " + repName;
		}
		this.shortName = repName;
		this.resStatus = resource;
		this.timeframe = timeframe;
		queries = new ArrayList<String>();
		cols = new ArrayList<String>();
	}
	
	/**
	 * Returns the list of columns in the query.
	 * 
	 * @return ArrayList<String> - The list of columns in the query.
	 */
	public ArrayList<String> getColumns() {
		return cols;
	}
	
	/**
	 * Returns the query string.
	 * 
	 * @return String - The query string.
	 */
	public ArrayList<String> getQueryList() {
		makeQueries();
		return queries;
	}
	
	
	/**
	 * Formats a LocalDateTime as a "timestamp" ready to be included in an SQL query. 
	 * 
	 * @param date LocalDateTime - The date to be formatted.
	 * @return String - The timestamp. 
	 */
	protected String getFormattedTS(LocalDateTime date) {
//		String timestamp = "{ts '" + date.toString().replace("T"," ") + ":00'} ";
//		System.out.println(timestamp.substring(5,9));
//		System.out.println(timestamp.substring(9));
		return "{ts '" + date.toString().replace("T"," ") + ":00'} ";
	}
	
	/**
	 * 
	 */
	public abstract void makeReport (ArrayList<ArrayList<String>> results, String time);
	
	public void makeQueries() {
		queries = new ArrayList<String>();
		String startTimeStamp = getFormattedTS(startDate);
		String endTimeStamp = getFormattedTS(endDate);
		makeQueries(startTimeStamp, endTimeStamp);
	}
	
	protected abstract void makeQueries(String startTimeStamp, String endTimeStamp);
	
	public void resetDates() {
		startDate = defaultStartDate;
		endDate = defaultEndDate;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String getLongName() {
		return longName;
	}
	
	/**
	 * Returns the timeframe char code. Codes are:
	 * A: Annually
	 * S: Semiannually
	 * Q: Quarterly
	 * M: Monthly
	 * W: Weekly
	 * O: Other - used for a custom date range. 
	 * N: None - includes no time ranges. 
	 * 
	 * @return char The timeframe char code. The 
	 */
	
	/**
	 * Returns the start date.
	 * @return startDate
	 */
	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	/**
	 * Manually sets the start date to the given year, month, and day. 
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public boolean setStartDate(int year, int month, int day) {
		startDate = LocalDateTime.of(year, month, day, 0, 0, 0);
		return true;
	}
	
	/**
	 * Sets the start date and end date of the current time period of the specified length. 
	 * Note: this is only for "-to-date" types of reports.
	 * 
	 * @param timeframe
	 */
	protected void setCurDates(char timeframe) {
		if( timeframe == W ) {
			long days = (long) Today.getDayOfWeek().getValue();
			startDate = Today.minusDays(days).withHour(0).withMinute(0);
			endDate = Today.plusDays(6-days).withHour(23).withMinute(59);
			
		}
		else if( timeframe == M) {
			startDate = MStaDate;
			endDate = MEndDate;
		}
		else if( timeframe == Q ) {
			startDate = QStaDate;
			endDate = QEndDate;
		}
		else if( timeframe == S ) {
			startDate = PQStaDate;
			endDate = QEndDate;
		}
		else if( timeframe == A ) {
			startDate = FYStaDate;
			endDate = FYEndDate;
		}
		else {System.out.println("You have selected an impossible selection");}
		defaultStartDate = startDate;
		defaultEndDate = endDate;
		days =  ChronoUnit.DAYS.between(startDate, endDate) + 1;
	}
	
	/**
	 * Sets the start date and end date to reflect the most recently completed time period of 
	 * the specified length.
	 * 
	 * @param timeframe
	 */
	protected void setPrevDates(char timeframe) {
		if( timeframe == W ) {
			long days = (long) Today.getDayOfWeek().getValue();
			startDate = Today.minusDays(days + 7).withHour(0).withMinute(0);
			endDate = Today.minusDays(days + 1).withHour(23).withMinute(59);
		}
		else if( timeframe == M ) {
			startDate = PMStaDate;
			endDate = PMEndDate;
		}
		else if( timeframe == Q ) {
			startDate = PQStaDate;
			endDate = PQEndDate;
		}
		else if( timeframe == S ) {
			startDate = PQStaDate.minusMonths(3);
			endDate = PQEndDate;
		}
		else if( timeframe == A ) {
			startDate = PFYStaDate;
			endDate = PFYEndDate;
		}
		else {System.out.println("You have selected an impossible selection");}
		//System.out.println(startDate + " " + endDate);
		defaultStartDate = startDate;
		defaultEndDate = endDate;
		days =  ChronoUnit.DAYS.between(startDate, endDate) + 1;
	}
	
	/**
	 * Returns the end date.
	 * @return endDate
	 */
	public LocalDateTime getEndDate() {
		return endDate;
	}
	
	/**
	 * Manually sets the end date to the given year, month, and day. 
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public boolean setEndDate(int year, int month, int day) {
		LocalDateTime tempEnd = LocalDateTime.of(year, month, day, 23, 59);
		if(tempEnd.isAfter(startDate)) {
			endDate = tempEnd;
			days =  ChronoUnit.DAYS.between(startDate, endDate) + 1;
			System.out.println(days);
			return true;
		}
		else return false;
	}
	
	public String getTimeFrame() {
		return timeFrameMap.get(timeframe);
	}
	
	public boolean getResStatus() {
		return resStatus;
	}

	/*
	 * All reports return true by default. Certain reports should not allow a custom time frame (such 
	 * as FYTD reports). These reports should override this method and return false.
	 */
	public boolean allowsCustomTimeFrame() {
		return true;
	}
	
	/**
	 * Returns the list of all the reports. 
	 * To differentiate between resource and distribution reports, call the getResStatus method. 
	 * 
	 * @return HashMap<String,ReportTemplate> The map of resource reports.
	 */
	public static ArrayList<ReportTemplate> getReportList () {
		return reports;
	}
	
	public static ArrayList<String> getBarData(ArrayList<ArrayList<String>> inputData) {
		ArrayList<String> barData = new ArrayList<String>();
		for(int i = 0; i < inputData.size(); i++) {
			if(inputData.get(i).get(0) != null) {
				int dataPt = (int) Float.parseFloat(inputData.get(i).get(0));
				barData.add(dataPt + "");
			}
			else { 
				barData.add("0");
				System.out.println("Possible error on index " + i);
			}
		}
		return barData;
	}
	
	public static TreeMap<String, Integer> getPieData(ArrayList<String> colNames, ArrayList<ArrayList<String>> inputData) {
		TreeMap<String,Integer> pieData = new TreeMap<String,Integer>();
		for(int i = 0; i < inputData.size(); i++) {
			int dataPt = (int) Float.parseFloat(inputData.get(i).get(0));
			pieData.put(colNames.get(i), dataPt);
		}
		return pieData;	
	}
	
	public static void initReports() {
		Today = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), 
				LocalDateTime.now().getDayOfMonth(), 0, 0);		
		//LYtoday = Today.minusYears(1);
		
		//Start and End of year
		if(Today.getMonthValue() < 7) {
			FYStaDate = LocalDateTime.of(Today.getYear() - 1, 7, 1, 0, 0);
			FYEndDate = LocalDateTime.of(Today.getYear(), 6, 30, 23, 59);
		}
		else {
			FYStaDate = LocalDateTime.of(Today.getYear(), 7, 1, 0, 0);
			FYEndDate = LocalDateTime.of(Today.getYear() + 1, 6, 30, 23, 59);
		}
		PFYStaDate = FYStaDate.minusYears(1);
		PFYEndDate = FYEndDate.minusYears(1);
		long daysInYear = ChronoUnit.DAYS.between(FYStaDate, FYEndDate) + 1;
		FYTDExptrapScale = ( (float) daysInYear )/( (float) Today.getDayOfYear() );
				
		//Quarter name and number
		int calQ = ( (Today.getMonthValue() - 1) / 3) + 1;
		fiscalQuarter = ( (calQ + 1) % 4 ) + 1;
		
		int fiscalYear;
		if(fiscalQuarter == 1 || fiscalQuarter == 2) {
			fiscalYear = Today.getYear();
		}
		else {
			fiscalYear = Today.getYear() - 1;
		}
		String fy1 = fiscalYear + ""; 
		String fy2 = (fiscalYear + 1) + "";
		fqName = "FY" + fy1.substring(2) + "-" + fy2.substring(2) + "Q" + fiscalQuarter;
		
		//Start and End of Quarter
		QStaDate = LocalDateTime.of(Today.getYear(),(calQ -1)*3 + 1,1,0,0,0);
		QEndDate = QStaDate.plusMonths(3).minusDays(1).withHour(23).withMinute(59);
		PQStaDate = QStaDate.minusMonths(3);
		PQEndDate = QStaDate.minusDays(1).withHour(23).withMinute(59);
		daysInQuarter = ChronoUnit.DAYS.between(QStaDate, QEndDate) + 1; 
		QuarterExtrapScale = ( (float) daysInQuarter )/( (float) ChronoUnit.DAYS.between(QStaDate, Today) + 1 );
		
		//Start and End of Quarter
		MStaDate = LocalDateTime.of(Today.getYear(), Today.getMonthValue(), 1, 0, 0, 0);
		MEndDate = MStaDate.plusMonths(1).minusDays(1).withHour(23).withMinute(59);
		PMStaDate = MStaDate.minusMonths(1);
		PMEndDate = MStaDate.minusDays(1).withHour(23).withMinute(59);
		daysInMonth = MEndDate.getDayOfMonth();
		MonthExtrapScale = ( (float) daysInMonth )/( (float) Today.getDayOfMonth() );
		initReportLists();
	}
	
	private static void initReportLists() {
		timeFrameMap = new HashMap<Character, String>();
		timeFrameMap.put(A, "Year");
		timeFrameMap.put(S, "Half-Year");
		timeFrameMap.put(Q, "Quarter");
		timeFrameMap.put(M, "Month");
		timeFrameMap.put(W, "Week");
		//timeFrameMap.put(O, "Other");
		//timeFrameMap.put(N, "None");
		
		reports = new ArrayList<ReportTemplate>();
		//reports.add(new TestReport(ReportTemplate.W));
		//reports.add(new TestReport("Weekly: Retail Performance Report By Store"));
		//reports.add(new TestReport("Monthly: Current month compared to last FY"));
		//reports.add(new TestReport("Monthly: Current FYTD compared to last FY"));
		
		reports.add(new ProdRecBySrc(ReportTemplate.M));
		reports.add(new ActVsProjByFoodSource(ReportTemplate.Q));
		reports.add(new ActVsProjByFoodSourceYTD(ReportTemplate.Q));
		reports.add(new ProdRecBySrc(ReportTemplate.Q));
		//reports.add(new RecVsDistRolling12Mos(ReportTemplate.Q));
		reports.add(new ActVsProjByFoodSource(ReportTemplate.S));
		reports.add(new ActVsProjByFoodSourceYTD(ReportTemplate.S));
		reports.add(new ActVsProjByFoodSource(ReportTemplate.A));
		reports.add(new ActVsProjByFoodSourceYTD(ReportTemplate.A));
		reports.add(new TestReport(ReportTemplate.A));
		
		//reports.add(new TestDistReport("Weekly: Inventory Trend Report By Storage Type"));
		//reports.add(new TestDistReport(ReportTemplate.W));
		//reports.add(new TestDistReport("Monthly: Current month compared to last FY"));
		//reports.add(new TestDistReport("Monthly: Current FYTD compared to last FY"));
		//reports.add(new TestDistReport("Monthly: Allocated vs Distributed by Food Source"));
		reports.add(new TestDistReport(ReportTemplate.A));
		reports.add(new DistByProdType(ReportTemplate.M));
		reports.add(new ActVsProjByDistCat(ReportTemplate.Q));
		reports.add(new FYTDTotalDist(ReportTemplate.Q));
		//reports.add(new WasteRatio(ReportTemplate.Q));
		reports.add(new DistByGeo(ReportTemplate.Q));
		reports.add(new DistFYvsLastFY(ReportTemplate.Q));
		reports.add(new DistByProdType(ReportTemplate.Q));
		reports.add(new ActVsProjByDistCat(ReportTemplate.S));
		reports.add(new FYTDTotalDist(ReportTemplate.S));
		//reports.add(new WasteRatio(ReportTemplate.S));
		reports.add(new DistByGeo(ReportTemplate.S));
		reports.add(new DistFYvsLastFY(ReportTemplate.S));
		reports.add(new DistByProdType(ReportTemplate.S));
		reports.add(new ActVsProjByDistCat(ReportTemplate.A));
		reports.add(new FYTDTotalDist(ReportTemplate.A));
		//reports.add(new WasteRatio(ReportTemplate.A));
		reports.add(new DistByGeo(ReportTemplate.A));
		reports.add(new DistFYvsLastFY(ReportTemplate.A));
		reports.add(new DistByProdType(ReportTemplate.A));
		reports.add(new RecVsDistLast3Years(ReportTemplate.A));
	}
	
//	public static void main(String[] args) {
//		ReportTemplate rt = new ReportTemplate("Temp");
//		
//	}
}
