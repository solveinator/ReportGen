package application;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 * 
 * This class connects the main class to the database. It maintains a master list of 
 * reports which maps the name of the report to the ReportTemplate.  
 */
public class DataIn {

	private HashMap<String,String> queries;
	private String connectionUrl;
	private String dbNickName;
	private String dbms; 
	private String serverName;
	private String portNumber;
	private String dbName;
	
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
	 * @param report ReportTemplate - A class which inherits the ReportTemplate class. Note: 
	 * The ReportTemplate class is meant to serve as a template, and it should not be used
	 * as an input for this method. 
	 *  
	 * @return ArrayList<ArrayList<String>> 
	 * @throws SQLException
	 */
	public ArrayList<ArrayList<String>> viewTable(ReportTemplate report) throws SQLException {
		Connection con = getConnection();
	    System.out.println("Connected to " + dbNickName);
		Statement stmt = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		     try {
		        stmt = con.createStatement();
		        res = stmt.executeQuery(report.getSQL());
		        
		        ArrayList<String> colNames = report.getColumns();
		        for(int i = 0; i < colNames.size(); i++) {
		        	data.add(new ArrayList<String>());
		        }
		        while (res.next()) {
		        	for(int i = 0; i < colNames.size(); i++) {
		        		String colName = colNames.get(i);
		        		data.get(i).add(res.getString(colName));
		        	}
		        }
		        return data;
		        } 
		     catch (SQLException e ) {
		        System.out.println(e);
		        return null;
		        } 
		     finally {
		        if (stmt != null) { stmt.close(); }
		    }
		}
	
	public static void main(String[] args) {
		try {
			DataIn testIn = new DataIn("SawbugWinConnectionUrl.txt", "" , "");
			testIn.getConnection();
		}
		catch(SQLException e) {
			System.out.print("Error: " + e.getMessage());
		}
	}
}
