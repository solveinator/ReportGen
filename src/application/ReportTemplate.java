package application;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

	private static HashMap <String,ReportTemplate> resourcing;
	private static HashMap <String,ReportTemplate> distribution;
	
	private String reportName;
	private String reportSQL;
	private ArrayList<String> columns;

	//This Date
	public static LocalDateTime Today;
	
	//Start and End of year
	public static LocalDateTime YStaDate;
	public static LocalDateTime YEndDate;
	
	//Start and End of Quarter
	public static int fiscalQaurter;
	public static long daysInQuarter;
	public static LocalDateTime QStaDate;
	public static LocalDateTime QEndDate;
	
	//Start and End of Month
	public static long daysInMonth;
	public static LocalDateTime MStaDate;
	public static LocalDateTime MEndDate;
	
	private static float FYTDExptrapScale;
	private static float QuarterExtrapScale;
	private static float MonthExtrapScale;
	
	/**
	 * This constructor creates a new instance of ReportTemplate with the given name. 
	 * 
	 * @param reportName String - The name of the report.
	 */
	public ReportTemplate(String reportName) {
		this.reportName = reportName;
	}
	
	/**
	 * Sets the list of columns in the query.
	 * 
	 * @param columns ArrayList<String> - The list of columns in the query.
	 */
	protected void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}
	
	/**
	 * Returns the list of columns in the query.
	 * 
	 * @return ArrayList<String> - The list of columns in the query.
	 */
	protected ArrayList<String> getColumns() {
		return columns;
	}
	
	/**
	 * Sets the query string.
	 * 
	 * @param query String - The query string.
	 */
	protected void setSQL(String query) {
		reportSQL = query;
	} 
	
	/**
	 * Returns the query string.
	 * 
	 * @return String - The query string.
	 */
	protected String getSQL() {
		return reportSQL;
	}
	
	/**
	 * Formats a LocalDateTime as a "timestamp" ready to be included in an SQL query. 
	 * 
	 * @param date LocalDateTime - The date to be formatted.
	 * @return String - The timestamp. 
	 */
	protected String getFormattedTS(LocalDateTime date) {
		return "{ts '" + date.toString().replace("T"," ") + ":00'} ";
	}
	
	/**
	 * Cleans and processes data (including any necessary computations)
	 */
	public abstract ArrayList<ArrayList<String>> cleanData( ArrayList<ArrayList<String>> list );
	
	/**
	 * Provides formatting to excel file, including headers and images etc.
	 * @param excelFileName
	 */
	public abstract void format(String excelFileName);
	
	/**
	 * Returns the map of resource reports.
	 * @return HashMap<String,ReportTemplate> The map of resource reports.
	 */
	public static HashMap<String,ReportTemplate> getResDic () {
		return resourcing;
	}
	
	/**
	 * Returns the map of distribution reports.
	 * @return HashMap<String,ReportTemplate> The map of distribution reports.
	 */
	public static HashMap<String,ReportTemplate> getDistDic () {
		return distribution;
	}
	

	public static void initReports() {
		Today = LocalDateTime.now();		
		//LYtoday = Today.minusYears(1);
		
		//Start and End of year
		YStaDate = LocalDateTime.of(Today.getYear(), 1, 1, 0, 0);
		YEndDate = LocalDateTime.of(Today.getYear(), 12, 31, 23, 23);
		long daysInYear = ChronoUnit.DAYS.between(YStaDate, YEndDate) + 1;
		FYTDExptrapScale = ( (float) daysInYear )/( (float) Today.getDayOfYear() );
		
		//Start and End of Quarter
		int calQ = ( (Today.getMonthValue() - 1) / 3) + 1;
		fiscalQaurter = ( (calQ + 1) % 4 ) + 1;

		QStaDate = LocalDateTime.of(Today.getYear(),(calQ -1)*3 + 1,1,0,0);
		QEndDate = QStaDate.plusMonths(3).minusDays(1).withHour(23).withMinute(23);
		daysInQuarter = ChronoUnit.DAYS.between(QStaDate, QEndDate) + 1; 
		QuarterExtrapScale = ( (float) daysInQuarter )/( (float) ChronoUnit.DAYS.between(QStaDate, Today) + 1 );
		
		//Start and End of Quarter
		MStaDate = LocalDateTime.of(Today.getYear(), Today.getMonthValue(), 1, 0, 0);
		MEndDate = MStaDate.plusMonths(1).minusDays(1).withHour(23).withMinute(23);
		daysInMonth = MEndDate.getDayOfMonth();
		MonthExtrapScale = ( (float) daysInMonth )/( (float) Today.getDayOfMonth() );
		initReportLists();
	}
	
	private static void initReportLists() {
		resourcing = new HashMap<String,ReportTemplate>(); 
		resourcing.put("TestReport", 
				new TestReport("Monthly: Product Received By Source") );
		resourcing.put("Monthly: Product Received By Source", 
				new FreshAlliance("Monthly: Product Received By Source", MStaDate, MEndDate) );
		resourcing.put("Weekly: Retail Performance Report By Store", null);
		resourcing.put("Monthly: Current month compared to last FY", null);
		resourcing.put("Monthly: Current FYTD compared to last FY", null);
		resourcing.put("Monthly: Product Received By Source", null);
		resourcing.put("Quarterly: Actuals vs. projected by Food Source", null);
		resourcing.put("Quarterly: FYTD Total Sourced", null);
		resourcing.put("Quarterly: Received vs. Distributed Rolling 12 month", null);
		resourcing.put("Semiannually: Actuals vs. projected by Food Source", null);
		resourcing.put("Semiannually: FYTD Total Sourced", null);
		resourcing.put("Annually: Actuals vs. projected by Food Source", null);
		resourcing.put("Annually: FYTD Total Sourced", null);
		
		distribution = new HashMap<String,ReportTemplate>();
		distribution.put("Weekly: Inventory Trend Report By Storage Type", null);
		distribution.put("Weekly: Distribution vs. Targets", null);
		distribution.put("Monthly: Current month compared to last FY", null);
		distribution.put("Monthly: Current FYTD compared to last FY", null);
		distribution.put("Monthly: Allocated vs Distributed by Food Source", null);
		distribution.put("Quarterly: Actuals vs Projected, by Distribution Category", null);
		distribution.put("Quarterly: Waste ratio for specific products", null);
		distribution.put("Quarterly: Total Distribution by Geographical Area", null);
		distribution.put("Semiannually: Actuals vs Projected, by Distribution Category", null);
		distribution.put("Semiannually: Waste ratio for specific products", null);
		distribution.put("Semiannually: Total Distribution by Geographical Area", null);
		distribution.put("Annually: Actuals vs Projected, by Distribution Category", null);
		distribution.put("Annually: Waste ratio for specific products", null);
		distribution.put("Annually: Total Distribution by Geographical Area", null);
	}
	
//	public static void main(String[] args) {
//		ReportTemplate rt = new ReportTemplate("Temp");
//		
//	}
}
