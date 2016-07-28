package application;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.util.Scanner;

public class DataIn {

	HashMap<String,String> queries;
	String connectionUrl;
	String dbNickName;
	String dbms; 
	String serverName;
	String portNumber;
	String dbName;
	String userName;
	String password;	
	
	/**
	 * Creates a new DataIn instance which will access the data via data access
	 * credentials stored in the file named connectionName
	 * 
	 * @param connectionName
	 */
	public DataIn(String connectionName) {
		dbNickName = connectionName;
		try {			
			Scanner scan = new Scanner(new File(connectionName));
			try{
				connectionUrl = scan.nextLine();
			}
			catch(NoSuchElementException e){System.out.print("No lines"); }
			catch(IllegalStateException e2){System.out.print("Illegal State");}
			System.out.println(connectionUrl);
			scan.close();
			
			queries = new HashMap<String, String>();
			Scanner scan2 = new Scanner(new File("SQLQueries.txt"));
			try{
			while(scan2.hasNext()) {
				String query = scan2.nextLine();
				String[] queryCombo = query.split("\\|");
				queries.put(queryCombo[0], queryCombo[1]);
				}}
			catch(NoSuchElementException e){System.out.print("No lines"); }
			catch(IllegalStateException e2){System.out.print("Illegal State");}
			scan2.close();
		}
		catch(FileNotFoundException e3) {System.out.println("File not found");
		}
		
		}
	
	public Connection getConnection() throws SQLException {		
	    Connection conn = DriverManager.getConnection(connectionUrl);
	   
	    System.out.println("Connected to " + dbNickName);
	    return conn;
	}
	
	public ResultSet viewTable(Connection con, String dbName, String queryName)
		throws SQLException {

		Statement stmt = null;
		String query = queries.get(queryName);
		System.out.println(query);
		ResultSet res = null;
		     try {
		        stmt = con.createStatement();
		        res = stmt.executeQuery(query);
		        while (res.next()) {
					String odk = res.getString("OrderDateKey");
					System.out.println(odk);}
		        
		    } catch (SQLException e ) {
		        System.out.println(e);
		        res = null;
		        
		    } finally {
		        if (stmt != null) { stmt.close(); }
		        return res;
		    }
		}
	
        //int supplierID = rs.getInt("SUP_ID");
        //float price = rs.getFloat("PRICE");
        //int sales = rs.getInt("SALES");
        //int total = rs.getInt("TOTAL");
        //System.out.println(coffeeName + "\t" + supplierID +
        //                   "\t" + price + "\t" + sales +
        //                   "\t" + total);
	
	public void cleanData(){}
	
	public static void main(String[] args) {
		DataIn testIn = new DataIn("SawbugWinConnectionUrl.txt");
		try {
			testIn.getConnection();
		}
		catch(SQLException e) {
			System.out.print("Error: " + e.getMessage());
		}
		testIn = new DataIn("SawbugConnectionUrl.txt");
		try {
			testIn.getConnection();
		}
		catch(SQLException e) {
			System.out.print("Error: " + e.getMessage());
		}
	}
}
