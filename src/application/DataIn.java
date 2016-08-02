package application;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import java.util.Scanner;
import java.util.ArrayList;

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
	 * Creates a new DataIn instance which will access the data via data access
	 * credentials stored in the file named connectionName
	 * 
	 * @param connectionName
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
	
	public ArrayList<ArrayList<Object>> viewTable(String queryName) throws SQLException {
		Connection con = getConnection();
		Statement stmt = null;
		ResultSet res = null;
		
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
        data.add(new ArrayList<Object>());
		//String query = queries.get(queryName);
		Query qu = new Query("OrderDateKey", "FactInternetSales", "ProductKey = '310'");
		     try {
		        stmt = con.createStatement();
		        res = stmt.executeQuery(qu.getQuery());

		        while (res.next()) {
					String odk = res.getString("OrderDateKey");
					data.get(0).add(odk);
					//String fis = res.getString("FactInternetSales");
		        }
		    } catch (SQLException e ) {
		        System.out.println(e);
		        data = null;
		        
		    } finally {
		        if (stmt != null) { stmt.close(); }
		        return data;
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
		try {
			DataIn testIn = new DataIn("SawbugWinConnectionUrl.txt", "" , "");
			testIn.getConnection();
		}
		catch(SQLException e) {
			System.out.print("Error: " + e.getMessage());
		}
	}
}
