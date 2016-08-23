package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook.*;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcCell;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.model.CalculationChain;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;


/**
 * @author Solveig Osborne
 * @version 2016_08_03
 * 
 * This class exports to excel and provides the formatting stored in the ReportTemplate.
 */
public class DataOut {
	
	String fileName; 
	
	/**
	 * @param fileName
	 */
	public DataOut(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @param fileName
	 * @param sheetName String - The name of the worksheet where the data will go.
	 * @param arrayList ArrayList<ArrayList<String>> - The data. An inner ArrayList 
	 * contains a single column of data.
	 * @throws IOException
	 */
	public static void exportToExcel(String fileName, String sheetName, ReportTemplate report,
			ArrayList<ArrayList<String>> arrayList) throws IOException {
		//Create temporary file		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		
		//Populate File
		//First, get a baseline for how many rows and columns you need.
		int totalRows = arrayList.get(0).size();
		int totalCols = arrayList.size();
		
		XSSFRow row = sheet.createRow(0);
		//Iterate through the columns adding cells
		for(int j = 0; j < totalCols; j++){
			XSSFCell cell = row.createCell(j);
			cell.setCellValue(report.getColumns().get(j).toString());
		}
		
		//Iterate through the rows
		for(int i = 0; i < totalRows; i++) {
			row = sheet.createRow(i+1);
			//Iterate through the columns adding cells
			for(int j = 0; j < totalCols; j++){
				XSSFCell cell = row.createCell(j);
				cell.setCellValue(arrayList.get(j).get(i));
			}
		}	
		//Format file contents
		DataFormat format = workbook.createDataFormat();
		CellStyle dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));
		//cell.setCellStyle(dateStyle);
		//cell.setCellValue(new Date());
		
		//sheet.autoSizeColumn(1);
		
		//Write to permanent file
		workbook.write(new FileOutputStream(fileName));
		workbook.close();
	}
	
	/**
	 * Creates a new PieChart of the labels and values contained in the pieData map. 
	 * 
	 * @param pieData
	 * @throws Exception
	 */
	public static void makePieChart(String chartTitle, TreeMap<String,Integer> pieData) {		
		/*
		 * Code borrowed heavily from "Build a pie chart from a template pptx" code by 
		 * author Yegor Kozlov under the Apache Software Foundation License.
		 */
		        XSSFWorkbook wb = null;
		        try {		    
		            wb = new XSSFWorkbook(new FileInputStream("templates/PieChart.xlsx"));
		            XSSFSheet sheet = wb.getSheet("PieChart");
//		            int shtNum = wb.getSheetIndex(sheet);
//		            
//		            CTWorkbook ctwb = wb.getCTWorkbook();
//		            CalculationChain chain = wb.getCalculationChain();
//		            CTCalcChain ctchain = chain.getCTCalcChain();
//		            for(CTCalcCell cell : ctchain.getCList()) {
//		            	System.out.println(cell.toString());
//		            }
//		            
//		            for(int i = wb.getNumberOfSheets() - 1; i >=0; i--) {
//		            	if( i != shtNum ) {		            	
//		            	ctwb.getSheets().removeSheet(i);
//		            	}
//		            }
		            //CTCalcChain chain = wb.getCalculationChain().getCTCalcChain();
		            //chain.;
    
		            // find chart in the slide
		            XSSFDrawing drawing = null;
		            XSSFChart chart = null;
		            for(POIXMLDocumentPart part : sheet.getRelations()){
		                if(part instanceof XSSFDrawing){
		                    drawing = (XSSFDrawing) part;
		                    break;
		                }
		            }
		            for(POIXMLDocumentPart part : drawing.getRelations()){
		                if(part instanceof XSSFChart){
		                    chart = (XSSFChart) part;
		                    break;
		                }
		            }
		    
		            if(chart == null) throw new IllegalStateException("chart not found in the template");
		    
		            // embedded Excel workbook that holds the chart data
		            //POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
		            try {		        
		                CTChart ctChart = chart.getCTChart();
		                CTPlotArea plotArea = ctChart.getPlotArea();
		        
		                CTPieChart pieChart = plotArea.getPieChartArray(0);
		                //Pie Chart Series
		                CTPieSer ser = pieChart.getSerArray(0);
		        
		                // Series Text
		                CTSerTx tx = ser.getTx();
		                if(chartTitle == null) {
		                	chartTitle = "Stupid Title";
		                }
		                tx.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
		                sheet.createRow(0).createCell(1).setCellValue(chartTitle);
		                String titleRef = new CellReference(sheet.getSheetName(), 0, 1, true, true).formatAsString();
		                tx.getStrRef().setF(titleRef);
		        
		                // Category Axis Data
		                CTAxDataSource cat = ser.getCat();
		                CTStrData strData = cat.getStrRef().getStrCache();
		        
		                // Values
		                CTNumDataSource val = ser.getVal();
		                CTNumData numData = val.getNumRef().getNumCache();
		        
		                strData.setPtArray(null);  // unset old axis text
		                numData.setPtArray(null);  // unset old values
		        
		                // set model
		                int idx = 0;
		                int rownum = 1;
		                
		                for(String key : pieData.keySet()) {
		                	CTStrVal strVal = strData.addNewPt();
		    	        	strVal.setIdx(idx);
		    	        	strVal.setV(key);
		    	        	
		    	        	CTNumVal numVal = numData.addNewPt();
		    	        	numVal.setIdx(idx);
		    	        	numVal.setV(pieData.get(key) + "");
		    	        	idx++;
		    	        	
		    	        	XSSFRow row = sheet.createRow(rownum++);
		    	        	row.createCell(0).setCellValue(strVal.getV());
		    	        	row.createCell(1).setCellValue(Integer.parseInt(numVal.getV()));
		                }
		                
		                numData.getPtCount().setVal(pieData.size());
		                strData.getPtCount().setVal(pieData.size());
		        
		                String numDataRange = new CellRangeAddress(1, rownum-1, 1, 1).formatAsString(sheet.getSheetName(), true);
		                val.getNumRef().setF(numDataRange);
		                String axisDataRange = new CellRangeAddress(1, rownum-1, 0, 0).formatAsString(sheet.getSheetName(), true);
		                cat.getStrRef().setF(axisDataRange);
		        
		                // updated the embedded workbook with the data
		                OutputStream xlsOut = new FileOutputStream("Report.xlsx");
		                try {
		                    wb.write(xlsOut);
		                } 
		                finally {
		                    xlsOut.close();
		                }
		            } finally {
		        wb.close();
		            }
		        } 
		        catch(Exception e) {
		            System.out.println(e);
		        }
		    }
	
	public static void makeTwoPieCharts(String chartTitle1, TreeMap<String,Integer> pieData1,
			String chartTitle2, TreeMap<String,Integer> pieData2) {		
		/*
		 * Code borrowed heavily from "Build a pie chart from a template pptx" code by 
		 * author Yegor Kozlov under the Apache Software Foundation License.
		 */
		        XSSFWorkbook wb = null;
		        try {		    
		            wb = new XSSFWorkbook(new FileInputStream("templates/PieChart2.xlsx"));
		            XSSFSheet sheet = wb.getSheet("PieChart");
    
		            // find chart in the slide
		            XSSFDrawing drawing = null;
		            XSSFChart chart = null;
		            XSSFChart chart2 = null;
		            for(POIXMLDocumentPart part : sheet.getRelations()){
		                if(part instanceof XSSFDrawing){
		                    drawing = (XSSFDrawing) part;
		                    break;
		                }
		            }
		            
		            int counter = 0;
		            for(POIXMLDocumentPart part : drawing.getRelations()){
		                if(part instanceof XSSFChart){
		                	if(counter == 0) {
		                		chart = (XSSFChart) part;
		                		counter++;
		                		}
		                	else if (counter == 1) {
		                		chart2 = (XSSFChart) part;
		                		break;
		                	}
		                }
		            }

		            if(chart == null) throw new IllegalStateException("chart not found in the template");
		    
		            // embedded Excel workbook that holds the chart data
		            try {
		                CTChart ctChart = chart.getCTChart();
		                CTChart ctChart2 = chart2.getCTChart();
		                CTPlotArea plotArea = ctChart.getPlotArea();
		                CTPlotArea plotArea2 = ctChart2.getPlotArea();
		        
		                CTPieChart pieChart = plotArea.getPieChartArray(0);
		                CTPieChart pieChart2 = plotArea2.getPieChartArray(0);
		                //Pie Chart Series
		                CTPieSer ser = pieChart.getSerArray(0);
		                CTPieSer ser2 = pieChart2.getSerArray(0);
		        
		                // Series Text
		                CTSerTx tx = ser.getTx();
		                if(chartTitle1 == null) {
		                	chartTitle1 = "";
		                }
		                CTSerTx tx2 = ser2.getTx();
		                if(chartTitle2 == null) {
		                	chartTitle2 = "";
		                }
		                
		                tx.getStrRef().getStrCache().getPtArray(0).setV(chartTitle1);
		                tx2.getStrRef().getStrCache().getPtArray(0).setV(chartTitle2);
		                
		                sheet.createRow(0).createCell(0).setCellValue(chartTitle1);
		                sheet.getRow(0).createCell(3).setCellValue(chartTitle2);
		                
		                String titleRef = new CellReference(sheet.getSheetName(), 0, 0, true, true).formatAsString();
		                tx.getStrRef().setF(titleRef);
		        
		                String titleRef2 = new CellReference(sheet.getSheetName(), 0, 3, true, true).formatAsString();
		                tx2.getStrRef().setF(titleRef2);
		                
		                // Category Axis Data for Chart #1
		                CTAxDataSource cat = ser.getCat();
		                CTStrData strData = cat.getStrRef().getStrCache();
		        
		                // Values for Chart #1
		                CTNumDataSource val = ser.getVal();
		                CTNumData numData = val.getNumRef().getNumCache();
		        
		                strData.setPtArray(null);  // unset old axis text
		                numData.setPtArray(null);  // unset old values
		        
		                // set model 1 data
		                int idx = 0;
		                int rownum = 1;
		                
		                for(String key : pieData1.keySet()) {
		                	CTStrVal strVal = strData.addNewPt();
		    	        	strVal.setIdx(idx);
		    	        	strVal.setV(key);
		    	        	
		    	        	CTNumVal numVal = numData.addNewPt();
		    	        	numVal.setIdx(idx);
		    	        	numVal.setV(pieData1.get(key) + "");
		    	        	idx++;
		    	        	
		    	        	XSSFRow row = sheet.createRow(rownum++);
		    	        	row.createCell(0).setCellValue(strVal.getV());
		    	        	row.createCell(1).setCellValue(Integer.parseInt(numVal.getV()));
		                }
		                
		                numData.getPtCount().setVal(pieData1.size());
		                strData.getPtCount().setVal(pieData1.size());
		        
		                String numDataRange = new CellRangeAddress(1, rownum-1, 1, 1).formatAsString(sheet.getSheetName(), true);
		                val.getNumRef().setF(numDataRange);
		                String axisDataRange = new CellRangeAddress(1, rownum-1, 0, 0).formatAsString(sheet.getSheetName(), true);
		                cat.getStrRef().setF(axisDataRange);
		                
		                // Category Axis Data for Chart #1
		                cat = ser2.getCat();
		                strData = cat.getStrRef().getStrCache();
		        
		                // Values for Chart #2
		                val = ser2.getVal();
		                numData = val.getNumRef().getNumCache();
		        
		                strData.setPtArray(null);  // unset old axis text
		                numData.setPtArray(null);  // unset old values
		                
		                // set model 2 data
		                idx = 0;
		                rownum = 1;
		                
		                for(String key : pieData2.keySet()) {
		                	CTStrVal strVal = strData.addNewPt();
		    	        	strVal.setIdx(idx);
		    	        	strVal.setV(key);
		    	        	
		    	        	CTNumVal numVal = numData.addNewPt();
		    	        	numVal.setIdx(idx);
		    	        	numVal.setV(pieData2.get(key) + "");
		    	        	idx++;
		    	        	
		    	        	XSSFRow row = sheet.getRow(rownum);
		    	        	if(row == null) {
		    	        		row = sheet.createRow(rownum);
		    	        	}
		    	        	rownum++;
		    	        	row.createCell(3).setCellValue(strVal.getV());
		    	        	row.createCell(4).setCellValue(Integer.parseInt(numVal.getV()));
		                }
		                
		                numData.getPtCount().setVal(pieData2.size());
		                strData.getPtCount().setVal(pieData2.size());
		        
		                numDataRange = new CellRangeAddress(1, rownum-1, 4, 4).formatAsString(sheet.getSheetName(), true);
		                val.getNumRef().setF(numDataRange);
		                axisDataRange = new CellRangeAddress(1, rownum-1, 3, 3).formatAsString(sheet.getSheetName(), true);
		                cat.getStrRef().setF(axisDataRange);
		                
		                // updated the embedded workbook with the data
		                OutputStream xlsOut = new FileOutputStream("Report.xlsx");
		                try {
		                    wb.write(xlsOut);
		                } 
		                finally {
		                    xlsOut.close();
		                }
		            } finally {
		        wb.close();
		            }
		        } 
		        catch(Exception e) {
		            System.out.println(e);
		        }
		    }
	
	/**
	 * 
	 */
	public static void makeBarChartLineOverlay(String chartTitle, ArrayList<String> barNames, 
		ArrayList<String> barData, ArrayList<String> targets) {
		XSSFWorkbook wb = null;
        try {		    
            wb = new XSSFWorkbook(new FileInputStream("templates/BarLineOverlay.xlsx"));
            XSSFSheet sheet = wb.getSheet("BarLineOverlay");
            XSSFDrawing drawing = null;
            XSSFChart chart = null;
            for(POIXMLDocumentPart part : sheet.getRelations()){
                if(part instanceof XSSFDrawing){
                    drawing = (XSSFDrawing) part;
                    break;
                }
            }
            for(POIXMLDocumentPart part : drawing.getRelations()){
                if(part instanceof XSSFChart){
                    chart = (XSSFChart) part;
                    break;
                }
            }
    
            if(chart == null) throw new IllegalStateException("chart not found in the template");
    
            // embedded Excel workbook that holds the chart data
            //POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
            try {		        
                CTChart ctChart = chart.getCTChart();
                CTPlotArea plotArea = ctChart.getPlotArea();
        
                CTBarChart barChart = plotArea.getBarChartArray(0);
                //Pie Chart Series
                CTBarSer ser = barChart.getSerArray(0);
        
                // Series Text
                CTSerTx tx = ser.getTx();
                if(chartTitle == null) {
                	chartTitle = "";
                }
                tx.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
                sheet.createRow(0).createCell(0).setCellValue(chartTitle);
                sheet.getRow(0).createCell(1).setCellValue("Actual");
                sheet.getRow(0).createCell(1).setCellValue("Target");
                String titleRef = new CellReference(sheet.getSheetName(), 0, 0, true, true).formatAsString();
                tx.getStrRef().setF(titleRef);
        
                // Category Axis Data
                CTAxDataSource cat = ser.getCat();
                CTStrData strData = cat.getStrRef().getStrCache();
        
                // Values
                CTNumDataSource val = ser.getVal();
                CTNumData numData = val.getNumRef().getNumCache();
        
                strData.setPtArray(null);  // unset old axis text
                numData.setPtArray(null);  // unset old values
        
                // set model
                int idx = 0;
                int rownum = 1;
                String barDataPt;
                String barName;
                String target;
                
                for(int i = 0; i < barData.size(); i++) {
                	barDataPt = barData.get(i);
                	barName = barNames.get(i);
                	target = targets.get(i);
                	
                	CTStrVal strVal = strData.addNewPt();
    	        	strVal.setIdx(idx);
    	        	strVal.setV(barName);
    	        	
    	        	CTNumVal numVal = numData.addNewPt();
    	        	numVal.setIdx(idx);
    	        	numVal.setV(barDataPt);
    	        	
    	        	XSSFRow row = sheet.createRow(rownum++);
    	        	row.createCell(0).setCellValue(strVal.getV());
    	        	row.createCell(1).setCellValue(Integer.parseInt(numVal.getV()));
    	        	row.createCell(2).setCellValue(Integer.parseInt(targets.get(i)));
                }
                
                numData.getPtCount().setVal(barData.size());
                strData.getPtCount().setVal(barData.size());
        
                String numDataRange = new CellRangeAddress(1, rownum-1, 1, 1).formatAsString(sheet.getSheetName(), true);
                val.getNumRef().setF(numDataRange);
                String axisDataRange = new CellRangeAddress(1, rownum-1, 0, 0).formatAsString(sheet.getSheetName(), true);
                cat.getStrRef().setF(axisDataRange);
        
                // updated the embedded workbook with the data
                OutputStream xlsOut = new FileOutputStream("Report.xlsx");
                try {
                    wb.write(xlsOut);
                } 
                finally {
//                    xlsOut.close();
                }
            } finally {
        wb.close();
            }            
        }
        catch(Exception e) {
            System.out.println(e);
        }
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		TreeMap<String,Integer> pieData = new TreeMap<String,Integer>();
		for(int i = 0; i < 5; i++) {
        	pieData.put("Cat " + i, 5*i + 8);
        }
		
		TreeMap<String,Integer> pieData2 = new TreeMap<String,Integer>();
		for(int i = 10; i < 12; i++) {
        	pieData2.put("Dog " + i, 5*(i-9) + 8);
        }
		
		DataOut.makeTwoPieCharts("FirstPie", pieData, "SecondPie", pieData2);
//		ArrayList<ArrayList<String>> stuff = new ArrayList<ArrayList<String>>(5);
//		ArrayList<String> list;
//		for(int i = 0; i < 5; i++ ) {
//			list = new ArrayList<String>(5);
//			stuff.add(list);
//			for(int j = 0; j < 5; j++) {
//				list.add( Integer.toString(i+j) );
//			}
//		}
//		try {
//			DataOut.exportToExcel("Report.xlsx", "Sheet1", new TestReport("Test"), stuff);
//		}
//		catch(IOException e) {
//			System.out.println("IO Problem");
//		}
	}
}

