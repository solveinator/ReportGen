package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;

import org.apache.poi.hssf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;

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
	public static void exportToExcel(String fileName, String sheetName, ArrayList<ArrayList<String>> arrayList) throws IOException {
		//Create temporary file		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		
		//Populate File
		//First, get a baseline for how many rows and columns you need.
		int totalRows = arrayList.get(0).size();
		int totalCols = arrayList.size();
		
		//Iterate through the rows
		for(int i = 0; i < totalRows; i++) {
			XSSFRow row = sheet.createRow(i);
			//Iterate through the columns adding cells
			for(int j = 0; j < totalCols; j++){
				XSSFCell cell = row.createCell(j);
				cell.setCellValue(arrayList.get(j).get(i).toString());
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
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ArrayList<ArrayList<String>> stuff = new ArrayList<ArrayList<String>>(5);
		ArrayList<String> list;
		for(int i = 0; i < 5; i++ ) {
			list = new ArrayList<String>(5);
			stuff.add(list);
			for(int j = 0; j < 5; j++) {
				list.add( Integer.toString(i+j) );
			}
		}
		try {
			DataOut.exportToExcel("Report.xlsx", "Sheet1", stuff);
		}
		catch(IOException e) {
			System.out.println("IO Problem");
		}
	}
}

