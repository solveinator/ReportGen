package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class DistFYvsLastFY extends ReportTemplate {

	private ArrayList<String> cats;
	private ArrayList<String[]> convs;
	private ArrayList<String> archCats;
	private ArrayList<Integer> archCatsIdx;
	private String staDate;
	private String endDate;
	
	public DistFYvsLastFY(char timeframe) {
		super("This FY Compared to Last FY by Storage Type", timeframe, false);
		setPrevDates(timeframe);
		cols = new ArrayList<String>();
		cats = new ArrayList<String>();
		convs = new ArrayList<String[]>();
		archCats = new ArrayList<String>();
		archCatsIdx = new ArrayList<Integer>();
		String archCat = "";
		
		try {
			Scanner scan = new Scanner(new File("txtFiles/FYvsLFYDistCategories.txt"));
			int counter = 1;
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] pieces = line.split(",");
				if(pieces.length == 1) {
					archCat = pieces[0].trim();
					archCats.add(archCat);
					archCatsIdx.add(counter);
					line = scan.nextLine();
					pieces = line.split(",");
				}
				String catName = pieces[0];
				cats.add(catName);
				
				//Remove Any Spaces or Slashes from the category name so it will query correctly
				String[] splitCatName = catName.split(" ");
				if(splitCatName.length > 1) {
					catName = "";
					for(int i = 0; i < splitCatName.length; i++) {
						catName += splitCatName[i]; 
					}
				}
				splitCatName = catName.split("/");
				if(splitCatName.length > 1) {
					catName = "";
					for(int i = 0; i < splitCatName.length; i++) {
						catName += splitCatName[i]; 
					}
				}
				splitCatName = catName.split("-");
				if(splitCatName.length > 1) {
					catName = "";
					for(int i = 0; i < splitCatName.length; i++) {
						catName += splitCatName[i]; 
					}
				}
				cols.add(archCat + catName + "TY");
				cols.add(archCat + catName + "LY");
				convs.add(Arrays.copyOfRange(pieces, 1, pieces.length));
				convs.add(Arrays.copyOfRange(pieces, 1, pieces.length));
				makeQueries();
				counter++;
				}
			scan.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		DataOut.makeTripleDoubleBar("Distribution this year vs. last for " + staDate + " to " + endDate,
			cats, ReportTemplate.getBarData(results), archCats, archCatsIdx);
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		staDate = startTimeStamp.substring(11,15).replace("-", "/");
		endDate = endTimeStamp.substring(11,15).replace("-", "/");
		queries = new ArrayList<String>();
		String query;
		for(int i = 0; i < convs.size(); i++) {
			String startTime = startTimeStamp;
			String endTime = endTimeStamp;
			String colName = cols.get(i);
			//Start with Select and From Statements 
			query = "SELECT SUM(Weight) As " + colName + " FROM NFBSData.dbo.uqryOrderDetailswoDates "
					+ "WHERE (";
			
			//Add the product ref numbers that fit this category
			String[] proRefs = convs.get(i);
			for(int j = 0; j < proRefs.length; j++) {
				if(j != 0) { 
					query += "OR ";
				}
				query += "ProductRef = '" + proRefs[j].trim() + "' ";
			}
			query = query.trim();
			query += ") ";
			//Finish with the dates
			query += "AND PickedUp = 'TRUE' "
					+ "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) "
					+ "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
					+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') "
					+ "AND (PickupDate >= " + startTime + " AND "
					+ "PickupDate <= " + endTime + ")";			
			queries.add(query);
			//System.out.println(query);
					
			int staYear = Integer.parseInt(startTimeStamp.substring(5,9)) - 1;
			int endYear = Integer.parseInt(endTimeStamp.substring(5,9)) - 1;
			startTime = "{ts '" + staYear + startTimeStamp.substring(9);
			endTime = "{ts '" + endYear + endTimeStamp.substring(9);
	
			//Repeat the process for last FY
			i++;
			colName = cols.get(i);
			
			
			query = "SELECT SUM(Weight) As " + colName + " FROM NFBSData.dbo.uqryOrderDetailswoDates "
					+ "WHERE (";
			
			//Add the product ref numbers that fit this category
			for(int j = 0; j < proRefs.length; j++) {
				if(j != 0) { 
					query += "OR ";
				}
				query += "ProductRef = '" + proRefs[j].trim() + "' ";
			}
			query = query.trim();
			query += ") ";
			//Finish with the dates
			query += "AND PickedUp = 'TRUE' "
					+ "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) "
					+ "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
					+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') "
					+ "AND (PickupDate >= " + startTime + " AND "
					+ "PickupDate <= " + endTime + ")";			
			queries.add(query);
			//System.out.println(query);
		}	
	}

}
