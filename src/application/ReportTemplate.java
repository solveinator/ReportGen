package application;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.ArrayList;
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
	
	private static ArrayList<ReportTemplate> reports;
	
	private String reportName;
	private char timeframe;
	private boolean resStatus; //true for resource reports; false for distribution reports

	//This Date
	protected static LocalDateTime Today;
	
	//Start and End of year
	protected static LocalDateTime YStaDate;
	protected static LocalDateTime YEndDate;
	
	//Start and End of Quarter
	protected static int fiscalQaurter;
	protected static long daysInQuarter;
	protected static LocalDateTime QStaDate;
	protected static LocalDateTime QEndDate;
	
	//Start and End of Month
	protected static long daysInMonth;
	protected static LocalDateTime MStaDate;
	protected static LocalDateTime MEndDate;
	
	private static float FYTDExptrapScale;
	private static float QuarterExtrapScale;
	private static float MonthExtrapScale;
	
	/**
	 * This constructor creates a new instance of ReportTemplate with the given name. 
	 * 
	 * @param reportName String - The name of the report.
	 */
	public ReportTemplate(String reportName, boolean resource, char timeframe) {
		this.reportName = reportName;
		this.resStatus = resource;
		this.timeframe = timeframe;
	}
	
	/**
	 * Returns the list of columns in the query.
	 * 
	 * @return ArrayList<String> - The list of columns in the query.
	 */
	public abstract ArrayList<String> getColumns();
	
	/**
	 * Sets the targets for this reprot
	 */
	public abstract void setTargets(ArrayList<String> targets);
	
	/**
	 * Returns the targets for this report
	 */
	protected abstract ArrayList<String> getTargets();
	
	/**
	 * Returns the query string.
	 * 
	 * @return String - The query string.
	 */
	public abstract ArrayList<String> getQueryList();
	
	/**
	 * Formats a LocalDateTime as a "timestamp" ready to be included in an SQL query. 
	 * 
	 * @param date LocalDateTime - The date to be formatted.
	 * @return String - The timestamp. 
	 */
	protected String getFormattedTS(LocalDateTime date) {
		return "{ts '" + date.toString().replace("T"," ") + ":00'} ";
	}
	
	public ArrayList<String> getCleanRules() {
		try {
			ArrayList<String> rules = new ArrayList<String>();
			InputStream inp = new FileInputStream("Cleaning.xlsx");
	
		    Workbook wb = WorkbookFactory.create(inp);
		    Sheet sheet = wb.getSheetAt(1);
		    Row row = sheet.getRow(2);
		    Cell cell = row.getCell(3);
		    if (cell == null)
		        cell = row.createCell(3);
		    cell.setCellType(Cell.CELL_TYPE_STRING);
		    cell.setCellValue("a test");
	
		    // Write the output to a file
		    FileOutputStream fileOut = new FileOutputStream("Cleaning.xlsx");
		    wb.write(fileOut);
		    fileOut.close();
		    return rules;
			}
		catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
			return null;
		}
		catch(IOException e2) {
			System.out.println(e2.getMessage());
			return null;
		}
		catch(InvalidFormatException e3) {
			System.out.println(e3.getMessage());
			return null;
		}
	}
	
	/**
	 * 
	 */
	public abstract void makeReport (ArrayList<ArrayList<String>> results);
	
	/**
	 * Cleans and processes data (including any necessary computations). Each inheriting class
	 * should override this method if cleaning is required. If no specific cleaning rules are 
	 * included, the list will be returned unchanged.
	 */
	public ArrayList<ArrayList<String>> cleanData( ArrayList<ArrayList<String>> list ) {
		return list;
	}
	
	/**
	 * Provides formatting to excel file, including headers and images etc.
	 * @param excelFileName
	 */
	public abstract void format(String excelFileName);
	
	public String getName() {
		return reportName;
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
	public char getTimeframe() {
		return timeframe;
	}
	
	public boolean getResStatus() {
		return resStatus;
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
		reports = new ArrayList<ReportTemplate>();
		reports.add(new TestReport("TestReport"));
		//reports.add(new TestReport("Weekly: Retail Performance Report By Store"));
		//reports.add(new TestReport("Monthly: Current month compared to last FY"));
		//reports.add(new TestReport("Monthly: Current FYTD compared to last FY"));
		reports.add(new FreshAlliance("Monthly: Product Received By Source", MStaDate, MEndDate));
		reports.add(new ActVsProjByFoodSource("Quarterly: Actuals vs. projected by Food Source", ReportTemplate.Q));
		reports.add(new FYTDTotalSourced("Quarterly: FYTD Total Sourced", ReportTemplate.Q));
		reports.add(new RecVsDistRolling12Mos("Quarterly: Received vs. Distributed Rolling 12 month",ReportTemplate.Q));
		reports.add(new ActVsProjByFoodSource("Semiannually: Actuals vs. projected by Food Source", ReportTemplate.S));
		reports.add(new FYTDTotalSourced("Semiannually: FYTD Total Sourced", ReportTemplate.S));
		reports.add(new ActVsProjByFoodSource("Annually: Actuals vs. projected by Food Source", ReportTemplate.A));
		reports.add(new FYTDTotalSourced("Annually: FYTD Total Sourced", ReportTemplate.A));
		
		//reports.add(new TestDistReport("Weekly: Inventory Trend Report By Storage Type"));
		//reports.add(new TestDistReport("Weekly: Distribution vs. Targets"));
		//reports.add(new TestDistReport("Monthly: Current month compared to last FY"));
		//reports.add(new TestDistReport("Monthly: Current FYTD compared to last FY"));
		//reports.add(new TestDistReport("Monthly: Allocated vs Distributed by Food Source"));
		reports.add(new ActVsProjByDistCat("Quarterly: Actuals vs Projected, by Distribution Category", ReportTemplate.Q));
		reports.add(new WasteRatio("Quarterly: Waste ratio for specific products", ReportTemplate.Q));
		reports.add(new DistByGeo("Quarterly: Total Distribution by Geographical Area", ReportTemplate.Q));
		reports.add(new ActVsProjByDistCat("Semiannually: Actuals vs Projected by Distribution Category", ReportTemplate.S));
		reports.add(new WasteRatio("Semiannually: Waste ratio for specific products", ReportTemplate.S));
		reports.add(new DistByGeo("Semiannually: Total Distribution by Geographical Area", ReportTemplate.S));
		reports.add(new ActVsProjByDistCat("Annually: Actuals vs Projected by Distribution Category", ReportTemplate.A));
		reports.add(new WasteRatio("Annually: Waste ratio for specific products", ReportTemplate.A));
		reports.add(new DistByGeo("Annually: Total Distribution by Geographical Area", ReportTemplate.A));
	}
	
//	public static void main(String[] args) {
//		ReportTemplate rt = new ReportTemplate("Temp");
//		
//	}
}
