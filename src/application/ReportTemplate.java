package application;

import java.util.Date;

public abstract class ReportTemplate {

	private String reportName;
	private String reportSQL;
	
	private Date thisYearStartDate;
	private Date thisYearEndDate;
	private Date lastYearStartDate;
	private Date lastYearEndDate;
	private Date twoYearsAgoStartDate;
	private Date twoYearsAgoEndDate;
	
	private double FYTDExptrapScale;
	private double QuarterExtrapScale;
	private double MonthExtrapScale;
	
	
}
