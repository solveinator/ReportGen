package application;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.util.Scanner;
import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 * 
 * This class connects the main class to the database. It maintains a master list of 
 * reports which maps the name of the report to the ReportTemplate.  
 */
public class DataIn {

	private String connectionUrl;
	private String dbNickName;	
	private String userName;
	private String password;	
	
	/**
	 * Creates a new DataIn instance which will access the connection string stored in the file
	 * named connectionName combined with the userName and password. If using Windows
	 * authentication, the userName and password can be empty strings.
	 * 
	 * @param connectionName String - The name of the text file with the first part of the 
	 * connection string.
	 * @param userName String - The SQL username of the user. If using Windows authentication, 
	 * this should be an empty String. 
	 * @param password String - The SQL password of the user. If using Windows authentication,
	 * this should be an empty String.
	 */
	public DataIn(String connectionName, String userName, String password) throws SQLException {
		dbNickName = connectionName;
		this.userName = userName;
		this.password = password;
		
		try {			
			Scanner scan = new Scanner(new File(connectionName));
			try{
				connectionUrl = scan.nextLine();
				connectionUrl = connectionUrl + "user=" + this.userName + ";password=" + this.password;
			}
			catch(NoSuchElementException e){System.out.print("No lines"); }
			catch(IllegalStateException e2){System.out.print("Illegal State");}
			getConnection();
			scan.close();
		}
		catch(FileNotFoundException e3) {System.out.println("File not found");
		}
	}
	
	/**
	 * @return Connection - A connection to the given database with the given credentials.  
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {		
		Connection conn = DriverManager.getConnection(connectionUrl);
	    return conn;
	}
	
	/**
	 * @param report ReportTemplate - An object which inherits from the ReportTemplate class
	 * because the ReportTemplate class is meant to serve as a template, and it should not be used
	 * as an input for this method. 
	 *  
	 * @return ArrayList<ArrayList<String>> 
	 * @throws SQLException
	 */
	public ArrayList<ArrayList<String>> queryDB(ReportTemplate report, Main main) throws SQLException {
		Connection con = getConnection();
	    System.out.println("Connected to " + dbNickName);
		Statement stmt = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		        stmt = con.createStatement();
		        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		        if(report.getQueryList().size() == 1) {
		        	String quer = report.getQueryList().get(0);
		        	res = stmt.executeQuery(quer);


		        	ArrayList<String> colNames = report.getColumns();
		        	for(int i = 0; i < colNames.size(); i++) {
		        		data.add(new ArrayList<String>());
		        	}
		        	while (res.next()) {
		        		for(int i = 0; i < colNames.size(); i++) {
		        			String colName = colNames.get(i);
		        			//System.out.println(res.getMetaData().getColumnLabel(i));
		        			//System.out.print(res.getString(colName) + "  ");
		        			data.get(i).add(res.getString(colName));
		        			double max = (i * 1.0)/(colNames.size() + 2);
		        			main.setProgress((i * 1.0)/(colNames.size() + 2));
		        			//data.get(i).add(res.getString(i));
		        		}
		        	}
		        } 
		        else {
		        	ArrayList<String> queries = report.getQueryList();
		        	for(int i = 0; i < queries.size(); i++) {						
						stmt = con.createStatement();
				        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				        res = stmt.executeQuery(queries.get(i));
				        
				        ArrayList<String> result = new ArrayList<String>(1);
				        ArrayList<String> colNames = report.getColumns();
				        res.next();
				        result.add(res.getString(colNames.get(i)));
				        main.setProgress(i/(queries.size()+ 2));
				        System.out.println("Made it to index " + i + ": " + result.get(0));
				        data.add(result);				        
					}
		        }
		        return data;
		     }
	
//	public static HashMap<String,String[]> getTargets() {
//		HashMap<String,String[]> tar = new HashMap<String, String[]>();
//		try {
//		    XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream("Target.xlsx"));
//		    XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
//	
//		    extractor.setFormulasNotResults(true);
//		    extractor.setIncludeSheetNames(false);
//		    String[] text = extractor.getText().split("\n");
//		    for(int i = 0; i < text.length; i++) {
//		    	String[] line = text[i].split("\\|");
//		    	if(line.length == 1) {
//		    		tar.put(line[0], null);
//		    	}
//		    	else if (line.length > 1) {
//		    		String[] temp = new String[2];
//		    		temp[0] = line[1].trim();
//		    		temp[1] = line[2].trim();
//		    		tar.put(line[0].trim(), temp);
//		    			System.out.println("[" + line[0] + "]: " + temp[0] + " " + temp[1]);
//		    	}
//		    }    
//		return tar;
//		}
//		catch(FileNotFoundException e) {
//			System.out.println(e.getMessage());
//			return null;
//		}
//		catch(IOException e) {
//			System.out.println(e.getMessage());
//			return null;
//		}
//	} 
	
	
	public static void main(String[] args) {
		try {
			DataIn testIn = new DataIn("SawbugWinConnectionUrl.txt", "" , "");
			testIn.getConnection();
		}
		catch(SQLException e) {
			System.out.print("Error: " + e.getMessage());
		}
		//DataIn.getTargets();
	}
}
