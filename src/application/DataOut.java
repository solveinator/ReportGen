package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileNotFoundException;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook.*;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcCell;
//import org.openxmlformats.schemas.drawingml.x2006.chart.CTTextBody;
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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
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
	
	private static String fileName = "Report.xlsx"; 
	
	/**
	 * @param fileName
	 */
	public DataOut(String fileName) {
	}
	
	public static void setOutputFile(String filePath) {
		fileName = filePath;
	}
	/**
	 * @param fileName
	 * @param sheetName String - The name of the worksheet where the data will go.
	 * @param arrayList ArrayList<ArrayList<String>> - The data. An inner ArrayList 
	 * contains a single column of data.
	 * @throws IOException
	 */
	public static void exportToExcel(String sheetName, ReportTemplate report,
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
		                OutputStream xlsOut = new FileOutputStream(fileName);
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
		        catch(FileNotFoundException e) {
		            System.out.println(e);
		        }
		        catch(IOException e) {
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
		                OutputStream xlsOut = new FileOutputStream(fileName);
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
		        catch(FileNotFoundException e) {
		            System.out.println(e);
		        }
		        catch(IOException e) {
		            System.out.println(e);
		        }
		    }
	
	/**
	 * 
	 */
	public static void makeDoubleBarLineOverlay(String title, ArrayList<String> headers, ArrayList<String> barNames, 
		ArrayList<ArrayList<String>> barData) {
		XSSFWorkbook wb = null;
		ArrayList<XSSFChart> charts;
        try {		    
            wb = new XSSFWorkbook(new FileInputStream("templates/DoubleBarLineOverlay.xlsx"));
            XSSFSheet sheet = wb.getSheet("BarLineOverlay");
            XSSFDrawing drawing = null;
            XSSFChart chart = null;
            for(POIXMLDocumentPart part : sheet.getRelations()){
                if(part instanceof XSSFDrawing){
                    drawing = (XSSFDrawing) part;
                    break;
                }
            }
            int counter = 0;
            for(POIXMLDocumentPart part : drawing.getRelations()){
                if(part instanceof XSSFChart){
                	if(counter < 1) {
                		chart = (XSSFChart) part; 
                		if(chart == null) throw new IllegalStateException("chart not found in the template");
                		counter++;
                		}
                }
            }
       
            // embedded Excel workbook that holds the chart data
            //POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
            try {		        
                CTChart ctChart = chart.getCTChart();
                ctChart.getTitle().getTx().getRich();
                ctChart.getTitle().getTx().getRich().getPArray(0).getRArray(0).setT(title);
                //.setT(chartTitle);
                CTPlotArea plotArea = ctChart.getPlotArea();
               
                CTBarChart barChart = plotArea.getBarChartArray(0);
                CTLineChart lineChart = plotArea.getLineChartArray(0);
                //Pie Chart Series
                ArrayList<CTAxDataSource> axData = new ArrayList<CTAxDataSource>(4);
                ArrayList<CTNumDataSource> numData = new ArrayList<CTNumDataSource>(4);
                for(int i = 0; i < barChart.sizeOfSerArray(); i++) {
                	CTBarSer barSer = barChart.getSerArray(i);
                	if(barSer.getCat() == null) {
                		barSer.addNewCat();
                	}
                	axData.add(barSer.getCat());
                	if(barSer.getVal() == null) {
                		barSer.addNewVal();
                	}
                	numData.add(barSer.getVal());
                	barSer.getTx().getStrRef().getStrCache().getPtArray(0).setV(title);

                	if(i < lineChart.sizeOfSerArray()) {
                		CTLineSer lineSer = lineChart.getSerArray(i);
                		lineSer.getTx().getStrRef().getStrCache().getPtArray(0).setV(title);
                		if(lineSer.getCat() == null) {
                			lineSer.addNewCat().addNewStrRef().addNewStrCache().addNewPt();
                		}
                		axData.add(lineSer.getCat());
                		numData.add(lineSer.getVal());
                		lineSer.getCat().getStrRef().getStrCache().setPtArray(null);
                	}
                	else {
                		axData.add(null);
                		numData.add(null);
                	}
                }
                // unset old values
                for(CTAxDataSource cat : axData) {
                	if(cat == null) {
                		System.out.println("I hate my life");
                	}
                	cat.getStrRef().getStrCache().setPtArray(null); 
                }               
                for(CTNumDataSource cat : numData) {
                	cat.getNumRef().getNumCache().setPtArray(null); 
                }
                
                sheet.createRow(0).createCell(0).setCellValue(title);
                String titleRef = new CellReference(sheet.getSheetName(), 0, 0, true, true).formatAsString();
                for(int i = 0; i < headers.size(); i++) 
                {               
                sheet.getRow(0).createCell(i + 1).setCellValue(headers.get(i));
                }
        
                // set model
                int idx = 0;
                int rownum = 1;
                String barDataPt;
                String barName;
                String target;
                
                for(int i = 0; i < barNames.size(); i++) {
                	barName = barNames.get(i);
                	XSSFRow row = sheet.createRow(rownum++);
                	row.createCell(0).setCellValue(barName);
                	for(CTAxDataSource cat : axData) {
                		if(i < axData.size()) {
                		CTStrVal strVal = axData.get(i).getStrRef().getStrCache().addNewPt();
        	        	strVal.setIdx(idx);
        	        	strVal.setV(barName);  
                		}
        	        	
        	        for(int j = 0; j < barData.size(); j++)	{
                        barDataPt = barData.get(j).get(i);
                        CTNumVal numVal = numData.get(j).getNumRef().getNumCache().addNewPt();
            	        numVal.setIdx(idx);
            	        numVal.setV(barDataPt);
            	                  	        
            	        row.createCell(j + 1).setCellValue(Integer.parseInt(barDataPt));
            	        numData.get(j).getNumRef().getNumCache().getPtCount().setVal(i);
            	        	//ser.getIdx().setVal(2); //Changes color
                        }
                    } 
                }
  
                for(CTAxDataSource cat : axData) {
                	String axisDataRange = new CellRangeAddress(1, rownum-1, 0, 0).formatAsString(sheet.getSheetName(), true);
                	cat.getStrRef().setF(axisDataRange);
                }
                
                for(int j = 0; j < barData.size(); j++)	{
                	String numDataRange = new CellRangeAddress(1, rownum-1, 1 + j, 1 + j).formatAsString(sheet.getSheetName(), true);
                	numData.get(j).getNumRef().setF(numDataRange);
                }
                // updated the embedded workbook with the data
                OutputStream xlsOut = new FileOutputStream(fileName);
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
        catch(FileNotFoundException e) {
            System.out.println(e);
        }
        catch(IOException e) {
            System.out.println(e);
        }
	}
	
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
	                ctChart.getTitle().getTx().getRich();
	                ctChart.getTitle().getTx().getRich().getPArray(0).getRArray(0).setT(chartTitle);
	                //.setT(chartTitle);
	                CTPlotArea plotArea = ctChart.getPlotArea();
	                
	                CTBarChart barChart = plotArea.getBarChartArray(0);
	                CTLineChart lineChart = plotArea.getLineChartArray(0);
	                //Pie Chart Series
	                CTBarSer barSer = barChart.getSerArray(0);
	                CTLineSer lineSer = lineChart.getSerArray(0);
	        
	                // Series Text
	                CTSerTx tx = barSer.getTx();
	                if(chartTitle == null) {
	                	chartTitle = "";
	                }
	                tx.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
	                sheet.createRow(0).createCell(0).setCellValue(chartTitle);
	                sheet.getRow(0).createCell(1).setCellValue("Actual");
	                sheet.getRow(0).createCell(2).setCellValue("Target");
	                String titleRef = new CellReference(sheet.getSheetName(), 0, 0, true, true).formatAsString();
	                //tx.getStrRef().setF(titleRef);
	        
	                // Category Axis Data
	                CTAxDataSource barCat = barSer.getCat();
	                CTAxDataSource lineCat = lineSer.getCat();

	                CTStrData barStrData = barCat.getStrRef().getStrCache();                
	                CTStrData lineStrData = lineCat.getStrRef().getStrCache();
	        
	                // Values
	                CTNumDataSource barVal = barSer.getVal();
	                CTNumDataSource lineVal = lineSer.getVal();
	                
	                CTNumData barNumData = barVal.getNumRef().getNumCache();
	                CTNumData lineNumData = lineVal.getNumRef().getNumCache();
	        
	                barStrData.setPtArray(null);  // unset old axis text
	                lineStrData.setPtArray(null);  // unset old axis text
	                barNumData.setPtArray(null);  // unset old values
	                lineNumData.setPtArray(null);  // unset old values
	        
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
	                	
	                	CTStrVal strVal = barStrData.addNewPt();
	    	        	strVal.setIdx(idx);
	    	        	strVal.setV(barName);
	    	        	
	    	        	strVal = lineStrData.addNewPt();
	    	        	strVal.setIdx(idx);
	    	        	strVal.setV(barName);
	    	        	
	    	        	CTNumVal numVal = barNumData.addNewPt();
	    	        	numVal.setIdx(idx);
	    	        	numVal.setV(barDataPt);
	    	        	
	    	        	numVal = lineNumData.addNewPt();
	    	        	numVal.setIdx(idx);
	    	        	numVal.setV(target);
	    	        	
	    	        	XSSFRow row = sheet.createRow(rownum++);
	    	        	row.createCell(0).setCellValue(strVal.getV());
	    	        	row.createCell(1).setCellValue(Integer.parseInt(barData.get(i)));
	    	        	row.createCell(2).setCellValue(Integer.parseInt(targets.get(i)));
	    	        	
	    	        	//ser.getIdx().setVal(2); //Changes color
	                }
	                barNumData.getPtCount().setVal(barData.size());
	                barStrData.getPtCount().setVal(barData.size());
	                lineNumData.getPtCount().setVal(barData.size());
	                lineStrData.getPtCount().setVal(barData.size());
	        
	                String axisDataRange = new CellRangeAddress(1, rownum-1, 0, 0).formatAsString(sheet.getSheetName(), true);
	                barCat.getStrRef().setF(axisDataRange);
	                
	                String numDataRange = new CellRangeAddress(1, rownum-1, 1, 1).formatAsString(sheet.getSheetName(), true);
	                barVal.getNumRef().setF(numDataRange);
	                
	                numDataRange = new CellRangeAddress(1, rownum-1, 2, 2).formatAsString(sheet.getSheetName(), true);               
	                lineVal.getNumRef().setF(numDataRange);             
	                
	                ctChart.getTitle().getTx().getRich().getPArray(0).getRArray(0).setT(chartTitle);        
	        
	                // updated the embedded workbook with the data
	                OutputStream xlsOut = new FileOutputStream(fileName);
	                try {
	                    wb.write(xlsOut);
	                } 
	                finally {
//	                    xlsOut.close();
	                }
	            } finally {
	        wb.close();
	            }            
	        }
	        catch(FileNotFoundException e) {
	            System.out.println(e);
	        }
	        catch(IOException e) {
	            System.out.println(e);
	        }
		}
	
	/**
	 * Creates bar graph with two side-by-side bars. 
	 * 
	 * @param chartTitle - The chart title. 
	 * @param barNames - A name for each set of two bars. This should be exactly half the size of barData
	 * @param barData - The data for the chart. Data will be processed in twos: the first and second 
	 * entries will make up the first side-by-side bars, the third and fourth entries will make the 
	 * second side-by-side pair, etc. 
	 * @param col1Name The name of the first column
	 * @param col2Name The name of the second column
	 */
	
	public static void makeSingleDoubleBar(String chartTitle, 
		ArrayList<String> barNames, 
		ArrayList<String> barData, 
		String col1Name, String col2Name) {
		XSSFWorkbook wb = null;
        try {		    
            wb = new XSSFWorkbook(new FileInputStream("templates/SingleDoubleBarGraph.xlsx"));
            XSSFSheet sheet = wb.getSheet("SimpleBarGraph");
            XSSFDrawing drawing = null;
            XSSFChart chart = null;
            for(POIXMLDocumentPart part : sheet.getRelations()){
                if(part instanceof XSSFDrawing){
                    drawing = (XSSFDrawing) part;
                    break;
                }
            }
            int counter = 0;
            for(POIXMLDocumentPart part : drawing.getRelations()){
                if(part instanceof XSSFChart){
                	if(counter < 1) {
                		chart = (XSSFChart) part;
                		counter++;
                		}
                }
            }
    
            if(chart == null) throw new IllegalStateException("chart not found in the template");
    
            // embedded Excel workbook that holds the chart data
            //POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
            try {
            	sheet.createRow(0).createCell(0).setCellValue(chartTitle);
            	sheet.createRow(1).createCell(1).setCellValue(col1Name);
            	sheet.getRow(1).createCell(2).setCellValue(col2Name);
            	int rowNum = 2;
            	for(int i = 0; i < barNames.size(); i++) {
            		XSSFRow row = sheet.createRow(rowNum);
		        	row.createCell(0).setCellValue(barNames.get(i));
		    	    row.createCell(1).setCellValue(Integer.parseInt(barData.get(i*2)));
		    	    row.createCell(2).setCellValue(Integer.parseInt(barData.get(i*2 +1)));
		    	    rowNum++;
            	}
            	//Cleaning out old numbers that might be near the top
            	for(int i = barNames.size() + 2; i < 25; i++){
            		sheet.createRow(i);
            	}
                CTChart ctChart = chart.getCTChart();
                ctChart.getTitle().getTx().getRich().getPArray(0).getRArray(0).setT(chartTitle);
                //.setT(chartTitle);
                CTPlotArea plotArea = ctChart.getPlotArea();                
                CTBarChart barChart = plotArea.getBarChartArray(0);
                
                //Pie Chart Series
                CTBarSer barSer1 = barChart.getSerArray(0);
                CTBarSer barSer2 = barChart.getSerArray(1);
        
                // Series Text
                CTSerTx tx1 = barSer1.getTx();
                CTSerTx tx2 = barSer2.getTx();
                tx1.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
                tx2.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
                
                String legend = new CellReference(sheet.getSheetName(), 1, 1, true, true).formatAsString();
                tx1.getStrRef().setF(legend);
                legend = new CellReference(sheet.getSheetName(), 1, 2, true, true).formatAsString();
                tx2.getStrRef().setF(legend);
        
                // Category Axis Data
                CTStrData barStrData1 = barSer1.getCat().getStrRef().getStrCache();
                CTStrData barStrData2 = barSer2.getCat().getStrRef().getStrCache();                
        
                // Values
                CTNumDataSource barVal1 = barSer1.getVal();
                CTNumDataSource barVal2 = barSer2.getVal();
                
                CTNumData barNumData1 = barVal1.getNumRef().getNumCache();
                CTNumData barNumData2 = barVal2.getNumRef().getNumCache();
        
                barStrData1.setPtArray(null);  // unset old axis text
                barStrData2.setPtArray(null);
                barNumData1.setPtArray(null);  // unset old values
                barNumData2.setPtArray(null);
        
                // set model
                int idx = 0;
                String barDataPt1;
                String barDataPt2;
                String barName;
                
                  	        	       	
    	        	//ser.getIdx().setVal(2); //Changes color
                barNumData1.getPtCount().setVal(barNames.size());
                barNumData2.getPtCount().setVal(barNames.size());
                barStrData1.getPtCount().setVal(barNames.size());
                barStrData2.getPtCount().setVal(barNames.size());
        
                String axisDataRange = new CellRangeAddress(2,  barNames.size() + 1, 0, 0).formatAsString(sheet.getSheetName(), true);
                barSer1.getCat().getStrRef().setF(axisDataRange);
                barSer2.getCat().getStrRef().setF(axisDataRange);
                
                String numDataRange = new CellRangeAddress(2, barNames.size() + 1, 1, 1).formatAsString(sheet.getSheetName(), true);
                System.out.println(numDataRange);
                barVal1.getNumRef().setF(numDataRange);
                
                numDataRange = new CellRangeAddress(2, barNames.size() + 1, 2, 2).formatAsString(sheet.getSheetName(), true);               
                System.out.println(numDataRange);
                barVal2.getNumRef().setF(numDataRange);             
             
                // updated the embedded workbook with the data
                OutputStream xlsOut = new FileOutputStream(fileName);
                wb.write(xlsOut);
            	xlsOut.close();
            	}
            catch(Exception e) {
            	e.printStackTrace();
            }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void makeTripleDoubleBar(String chartTitle, ArrayList<String> barNames, 
		ArrayList<String> barData, ArrayList<String> archCats, ArrayList<Integer> archCatsIdx) {
		System.out.println(archCats);
		System.out.println(archCatsIdx);
		System.out.println(barData);
		ArrayList<XSSFChart> chartList = new ArrayList<XSSFChart>(3);
		XSSFWorkbook wb = null;
        try {		    
        	wb = new XSSFWorkbook(new FileInputStream("templates/TripleBarGraph.xlsx"));
            XSSFSheet sheet = wb.getSheet("TripleBarGraph");
            XSSFDrawing drawing = null;
            XSSFChart chart = null;
            for(POIXMLDocumentPart part : sheet.getRelations()){
                if(part instanceof XSSFDrawing){
                    drawing = (XSSFDrawing) part;
                    break;
                }
            }
            int counter = 0;
            for(POIXMLDocumentPart part : drawing.getRelations()){
                if(part instanceof XSSFChart){
                	if(counter < 4) {
                		chart = (XSSFChart) part;
                		chartList.add(chart);
                		counter++;
                		}
                }
            }
    
            if(chart == null) throw new IllegalStateException("chart not found in the template");
    
            // embedded Excel workbook that holds the chart data
            //POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
            try {
            	counter = 0;
            	int catNum = 0;
            	sheet.createRow(0).createCell(0).setCellValue(chartTitle);
            	
        		sheet.getRow(counter).createCell(0).setCellValue(archCats.get(catNum));
        		catNum++;
                sheet.getRow(counter).createCell(1).setCellValue("Last Year");
                sheet.getRow(counter).createCell(2).setCellValue("This Year");
                	
            	int excelIdxSta;
            	int excelIdxStop = 0;
            	int catIdxSta;
            	int catIdxStop = 0;
	            for(int i = 0; i < 3; i++) {
	            	if(i == 0) {
	            		excelIdxSta = 2;
	            		excelIdxStop = 17;
	            		catIdxSta = 0;
	            		catIdxStop = 15;
	            	}
	            	else if(i == 1) {
	            		excelIdxSta = 19;
	            		excelIdxStop = 23;
	            		catIdxSta = 16;
	            		catIdxStop = 20;
	            	}
	            	else {
	            		excelIdxSta = 25;
	            		excelIdxStop = 29;
	            		catIdxSta = 21;
	            		catIdxStop = 25;
	            	}
	            	XSSFRow row = sheet.createRow(excelIdxSta - 1);
	            	row.createCell(0).setCellValue(archCats.get(i));
	           		
	            	int excelIdx = excelIdxSta;
            	for(int j = catIdxSta; j <= catIdxStop; j++) {
            		row = sheet.createRow(excelIdx);
	        		row.createCell(0).setCellValue(barNames.get(j));
	    	        row.createCell(1).setCellValue(Integer.parseInt(barData.get(j*2 + 1)));
	    	        row.createCell(2).setCellValue(Integer.parseInt(barData.get(j*2)));
	    	        excelIdx++;
	        		}
            	
	            	chart = chartList.get(i);
	            	chartTitle = archCats.get(i);
	            	
	                CTChart ctChart = chart.getCTChart();
	                ctChart.getTitle().getTx().getRich().getPArray(0).getRArray(0).setT(chartTitle);
	                //.setT(chartTitle);
	                CTPlotArea plotArea = ctChart.getPlotArea();                
	                CTBarChart barChart = plotArea.getBarChartArray(0);
	                
	                //Pie Chart Series
	                CTBarSer barSer1 = barChart.getSerArray(0);
	                CTBarSer barSer2 = barChart.getSerArray(1);
	        
	                // Series Text
	                CTSerTx tx1 = barSer1.getTx();
	                CTSerTx tx2 = barSer2.getTx();
	                tx1.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
	                tx2.getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
	                
	                String legend = new CellReference(sheet.getSheetName(), 1, 1, true, true).formatAsString();
	                tx1.getStrRef().setF(legend);
	                legend = new CellReference(sheet.getSheetName(), 1, 2, true, true).formatAsString();
	                tx2.getStrRef().setF(legend);
	        
	                // Category Axis Data
	                CTStrData barStrData1 = barSer1.getCat().getStrRef().getStrCache();
	                CTStrData barStrData2 = barSer2.getCat().getStrRef().getStrCache();                
	        
	                // Values
	                CTNumDataSource barVal1 = barSer1.getVal();
	                CTNumDataSource barVal2 = barSer2.getVal();
	                
	                CTNumData barNumData1 = barVal1.getNumRef().getNumCache();
	                CTNumData barNumData2 = barVal2.getNumRef().getNumCache();
	        
	                barStrData1.setPtArray(null);  // unset old axis text
	                barStrData2.setPtArray(null);
	                barNumData1.setPtArray(null);  // unset old values
	                barNumData2.setPtArray(null);
	        
	                // set model
	                int idx = 0;
	                String barDataPt1;
	                String barDataPt2;
	                String barName;
	                
	                  	        	       	
	    	        	//ser.getIdx().setVal(2); //Changes color
	                int numPts = excelIdxStop - excelIdxSta + 1;
	                barNumData1.getPtCount().setVal(numPts);
	                barNumData2.getPtCount().setVal(numPts);
	                barStrData1.getPtCount().setVal(numPts);
	                barStrData2.getPtCount().setVal(numPts);
	        
	                String axisDataRange = new CellRangeAddress(excelIdxSta,  excelIdxStop, 0, 0).formatAsString(sheet.getSheetName(), true);
	                barSer1.getCat().getStrRef().setF(axisDataRange);
	                barSer2.getCat().getStrRef().setF(axisDataRange);
	                
	                String numDataRange = new CellRangeAddress(excelIdxSta, excelIdxStop, 1, 1).formatAsString(sheet.getSheetName(), true);
	                System.out.println(numDataRange);
	                barVal1.getNumRef().setF(numDataRange);
	                
	                numDataRange = new CellRangeAddress(excelIdxSta, excelIdxStop, 2, 2).formatAsString(sheet.getSheetName(), true);               
	                System.out.println(numDataRange);
	                barVal2.getNumRef().setF(numDataRange);
            	}
                // updated the embedded workbook with the data
                OutputStream xlsOut = new FileOutputStream(fileName);
                wb.write(xlsOut);
            	xlsOut.close();
            	}
            catch(Exception e) {
            	e.printStackTrace();
            }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param colNum
	 * @param value
	 * @return
	 */
	public static boolean writeToCell(XSSFSheet sheet, int rowNum, int colNum, String value) {
		if(sheet == null || rowNum < 0 || colNum < 0 || value == null) {
			return false;
		}
		if(sheet.getRow(rowNum) == null) {
			sheet.createRow(rowNum).createCell(colNum).setCellValue(value);
		}
		else if(sheet.getRow(rowNum).getCell(colNum) == null) {
			sheet.getRow(rowNum).createCell(colNum).setCellValue(value);
			}
		else {
			sheet.getRow(rowNum).getCell(colNum).setCellValue(value);
		}
		return true;
	}
	
//	public static void make12MonthsRolling (String chartTitle, ArrayList<String> rowNames, 
//			ArrayList<String> colNames, 
//			ArrayList<String> qRecData, 
//			ArrayList<String> qDistData,
//			ArrayList<String> aRecData,
//			ArrayList<String> aDistData){
//		XSSFWorkbook wb = null;
//		ArrayList<XSSFChart> charts;
//        try {		    
//            wb = new XSSFWorkbook(new FileInputStream("templates/LineGraph.xlsx"));
//            XSSFSheet sheet = wb.getSheet("LineGraph");
//            XSSFDrawing drawing = null;
//            XSSFChart chart = null;
//            for(POIXMLDocumentPart part : sheet.getRelations()){
//                if(part instanceof XSSFDrawing){
//                    drawing = (XSSFDrawing) part;
//                    break;
//                }
//            }
//            int counter = 0;
//            for(POIXMLDocumentPart part : drawing.getRelations()){
//                if(part instanceof XSSFChart){
//                	if(counter < 1) {
//                		chart = (XSSFChart) part; 
//                		if(chart == null) throw new IllegalStateException("chart not found in the template");
//                		counter++;
//                		}
//                }
//            }
//       
//            // embedded Excel workbook that holds the chart data
//            //POIXMLDocumentPart xlsPart = chart.getRelations().get(0);
//            try {		        
//                CTChart ctChart = chart.getCTChart();
//                ctChart.getTitle().getTx().getRich().getPArray(0).getRArray(0).setT(chartTitle);
//                CTPlotArea plotArea = ctChart.getPlotArea();
//               
//                CTLineChart lineChart = plotArea.getLineChartArray(0);
//               
//                //Pie Chart Series
//                ArrayList<CTAxDataSource> axData = new ArrayList<CTAxDataSource>(2);
//                ArrayList<CTNumDataSource> numData = new ArrayList<CTNumDataSource>(2);
//                for(int i = 0; i < 2; i++) {
//                	CTLineSer lineSer = lineChart.getSerArray(i);
//                	if(lineSer.getCat() == null) {
//                		lineSer.addNewCat();
//                	}
//                	axData.add(lineSer.getCat());
//                	if(lineSer.getVal() == null) {
//                		lineSer.addNewVal();
//                	}
//                	numData.add(lineSer.getVal());
//                	lineSer.getTx().getStrRef().getStrCache().getPtArray(0).setV(chartTitle);
//
//                }
//                // unset old values
//                for(CTAxDataSource cat : axData) {
//                	if(cat == null) {
//                		System.out.println("I hate my life");
//                	}
//                	cat.getStrRef().getStrCache().setPtArray(null); 
//                }               
//                for(CTNumDataSource cat : numData) {
//                	cat.getNumRef().getNumCache().setPtArray(null); 
//                }
//                
//                sheet.createRow(0).createCell(0).setCellValue(chartTitle);
//                String titleRef = new CellReference(sheet.getSheetName(), 0, 0, true, true).formatAsString();
//                sheet.createRow(1);
//                for(int i = 0; i < colNames.size(); i++) {
//                	sheet.getRow(1).createCell(i+1).setCellValue(colNames.get(i));
//                }
//        
//                // set model
//                int idx = 0;
//                int rownum = 1;
//                String barDataPt;
//                String barName;
//                String target;
//                
//                for(int i = 0; i < qRecData.size(); i++) {
//                	barName = rowNames.get(i);
//                	XSSFRow row = sheet.createRow(i+2);
//                	row.createCell(0).setCellValue(barName);
//                	for(CTAxDataSource cat : axData) {
//                		if(i < axData.size()) {
//                		CTStrVal strVal = axData.get(i).getStrRef().getStrCache().addNewPt();
//        	        	strVal.setIdx(idx);
//        	        	strVal.setV(barName);  
//                		}
//        	        	
//        	        for(int j = 0; j <  colNames.size(); j++)	{
//                        barDataPt = barData.get(j).get(i);
//                        CTNumVal numVal = numData.get(j).getNumRef().getNumCache().addNewPt();
//            	        numVal.setIdx(idx);
//            	        numVal.setV(barDataPt);
//            	                  	        
//            	        row.createCell(j + 1).setCellValue(Integer.parseInt(barDataPt));
//            	        numData.get(j).getNumRef().getNumCache().getPtCount().setVal(i);
//            	        	//ser.getIdx().setVal(2); //Changes color
//                        }
//                    } 
//                }
//  
//                for(CTAxDataSource cat : axData) {
//                	String axisDataRange = new CellRangeAddress(1, rownum-1, 0, 0).formatAsString(sheet.getSheetName(), true);
//                	cat.getStrRef().setF(axisDataRange);
//                }
//                
//                for(int j = 0; j < barData.size(); j++)	{
//                	String numDataRange = new CellRangeAddress(1, rownum-1, 1 + j, 1 + j).formatAsString(sheet.getSheetName(), true);
//                	numData.get(j).getNumRef().setF(numDataRange);
//                }
//                // updated the embedded workbook with the data
//                OutputStream xlsOut = new FileOutputStream(fileName);
//                try {
//                    wb.write(xlsOut);
//                } 
//                finally {
////                    xlsOut.close();
//                }
//            } finally {
//        wb.close();
//            }            
//        }
//        catch(FileNotFoundException e) {
//            System.out.println(e);
//        }
//        catch(IOException e) {
//            System.out.println(e);
//        }
//	}
	
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
		
		//DataOut.makeTwoPieCharts("FirstPie", pieData, "SecondPie", pieData2);
		
//		ArrayList<ArrayList<String>> stuff = new ArrayList<ArrayList<String>>(5);
//		ArrayList<String> list;
//		for(int i = 0; i < 5; i++ ) {
//			list = new ArrayList<String>(5);
//			stuff.add(list);
//			for(int j = 0; j < 5; j++) {
//				list.add( Integer.toString(i+j) );
//			}
//		}
		ArrayList<String> barNam = new ArrayList<String>(8);
		ArrayList<String> barDat = new ArrayList<String>(8);
		ArrayList<String> target = new ArrayList<String>(8);
		ArrayList<String> cats = new ArrayList<String>();
		ArrayList<Integer> index = new ArrayList<Integer>();
		for(int i = 0; i < 27; i++ ) {
			barNam.add("A" + i);
			barDat.add(i + "");
			barDat.add(i + "0");
			target.add((i + 1) + "");
		}
		ArrayList<String> barDat2 = new ArrayList<String>(8);
		ArrayList<String> targets2 = new ArrayList<String>(8);
		for(int i = 0; i < 27; i++ ) {
			barDat2.add(i + "");
			targets2.add((i + 1) + "");
		}
		cats.add("Dry");
		cats.add("Wet");
		cats.add("Green");
		cats.add("Orange");
		index.add(2);
		index.add(4);
		index.add(8);
		index.add(10);
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		data.add(barDat);
		data.add(target);
		data.add(barDat2);
		data.add(targets2);
				
		//DataOut.makeBarChartLineOverlay("Stupid Title XXX", barNam, barDat, target);
		//DataOut.makeSingleDoubleBar("Cool Title", barNam, barDat);
		//DataOut.makeTripleDoubleBar("Cool Title", barNam, barDat, cats, index);
		DataOut.makeDoubleBarLineOverlay("Stupid Title", barNam, cats, data);
	}
	
	
}

