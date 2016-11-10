package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class DistByProdType extends ReportTemplate {

	private ArrayList<String> cats;
	private ArrayList<String[]> convs;
	
	public DistByProdType(char timeframe) {
		super("Distribution By Product Type", timeframe, false);
		setPrevDates(timeframe);
		cols = new ArrayList<String>();
		cats = new ArrayList<String>();
		convs = new ArrayList<String[]>();
		
		try {
			Scanner scan = new Scanner(new File("txtFiles/FYTDDistCategories.txt"));
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] pieces = line.split(",");
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
				cols.add(catName);
				convs.add(Arrays.copyOfRange(pieces, 1, pieces.length)); 
				makeQueries();
			}
			scan.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
//		cats.add("Green Vegetables");
//		cats.add("Non-Green Vegetables");
//		cats.add("Tomato Products");
//		cats.add("Fruits");
//		cats.add("Shelf Stable Beans");
//		cats.add("Shelf Stable Protein");
//		cats.add("Shelf Stable Dairy");
//		cats.add("Soups, Stews, Chili");
//		cats.add("Peanut Butter");
//		cats.add("Meat");
//		cats.add("Rice");
//		cats.add("Cereal/Oatmeal");
//		cats.add("Flour/Masa");
//		cats.add("Pasta");
//		cats.add("Produce");
//		cats.add("Dairy");
//		cats.add("Cooler");
//		cats.add("Bakery");
//		cats.add("Core");
//		cats.add("Non-Core");
//		cats.add("Agency Purchased");		
		
	}

	@Override
	public void makeReport(ArrayList<ArrayList<String>> results, String time) {
		DataOut.makePieChart("Distribution By Product Type for " + time,
				ReportTemplate.getPieData(cats, results));
	}

	@Override
	protected void makeQueries(String startTimeStamp, String endTimeStamp) {
		queries = new ArrayList<String>();
		String query;
		String common = "AND (AgencyRef > 'W295' OR AgencyRef < 'W207' ) "
				+ "AND (ProductRef <> '1335' AND ProductRef <> '1043'  "
				+ "AND ProductRef <> '1061' AND ProductRef <> '1064' AND ProductRef <> '1071') "
				+ "And (PickedUp = 'TRUE') "
				+ "AND (PickupDate >= " + startTimeStamp + " AND "
						+ "PickupDate <= " + endTimeStamp + ")";
		
		for(int i = 0; i < convs.size(); i++) {
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
			query += ") " + common;
			//Finish with the dates
					
			queries.add(query);
		}	
	}

}
