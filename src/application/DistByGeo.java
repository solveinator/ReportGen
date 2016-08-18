package application;

import java.util.ArrayList;
import java.util.TreeMap;

//import org.docx4j.dml.chart.CTPieChart;

public class DistByGeo extends ReportTemplate {

	public DistByGeo(String reportName, char timeframe) {
		super(reportName, false, timeframe);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<String> getColumns() {
		ArrayList<String> list = new ArrayList<String>(1);
		list.add("TotalProductCost");
		return list;
	}

	@Override
	public void setTargets(ArrayList<String> targets) {
		// TODO Auto-generated method stub

	}

	@Override
	protected ArrayList<String> getTargets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSQL() {
		return "Select TotalProductCost From FactInternetSales";
	}

	@Override
	public void format(String excelFileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results) {
		TreeMap<String,Integer> pieData = new TreeMap<String,Integer>();
		for(int i = 0; i < 5; i++) {
        	pieData.put("Cat " + i, 5*i + 8);
        }
		DataOut.makePieChart("FirstPie", pieData);
	} 
	

	public static void main(String args[]) {
		//report.makeReport(null);
		TreeMap<String,Integer> pieData = new TreeMap<String,Integer>();
		for(int i = 0; i < 5; i++) {
        	pieData.put("Cat " + i, 5*i + 8);
        }
		DataOut.makePieChart("MyPie", pieData);
	}
}
