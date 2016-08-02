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

public class DataOut {
	
	String fileName; 
	
	/*
	 * This contructor takes 
	 */
	public DataOut(String fileName, String sheetName, ArrayList<ArrayList<Object>> contents) throws IOException {
		//Create temporary file
		this.fileName = fileName;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		
		//Populate File
		ArrayList<Object> list;
		for(int i = 0; i < contents.size(); i++ ) {
			list = contents.get(i);
			for(int j = 0; j < list.size(); j++) {				
				XSSFRow row = sheet.createRow(j);
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(list.get(j).toString());
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
	
	public static void main(String[] args) throws IOException {
		ArrayList<ArrayList<Object>> stuff = new ArrayList<ArrayList<Object>>(5);
		ArrayList<Object> list;
		for(int i = 0; i < 5; i++ ) {
			list = new ArrayList<Object>(5);
			stuff.add(list);
			for(int j = 0; j < 5; j++) {
				list.add(new Integer(i+j));
			}
		}
		try {
			DataOut out = new DataOut("Report.xlsx", "Sheet1", stuff);
		}
		catch(IOException e) {
			System.out.println("IO Problem");
		}
	}
}

