package application;

import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.File;

public class Targets {

	String file;
	Scanner scan;
	HashMap<String, HashMap<String, Integer>> targets;
	FileWriter out;
	
	public Targets(String file) throws FileNotFoundException {
		this.file = file;
		scan = new Scanner(new File(file));
		targets = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		while(scan.hasNext()) {
			String line = scan.nextLine();
			String[] words = line.split(":");
			if(words.length == 1 && !(words[0].equals(""))) {
				map = new HashMap<String, Integer>();
				targets.put(words[0], map);
			}
			else if(words.length == 2){
					map.put(words[0], Integer.parseInt(words[1]));
			}
		}
		scan.close();
	}
	
	public HashMap<String, Integer> getWeeklyTargets(String reportName) {	
		if(targets.containsKey(reportName)) {
			return targets.get(reportName);
		}
		else {
			return null;
		}
	}
	
	public ArrayList<String> getTargets(String reportName, ArrayList<String> cols, long numDays) {
		ArrayList<String> targetList = new ArrayList<String>();
		HashMap<String, Integer> map = null;
		if(targets.containsKey(reportName)) {
			map = targets.get(reportName);
		}
		for(int i = 0; i < cols.size(); i++) {
			if(map != null & map.containsKey(cols.get(i))){
				long tar = map.get(cols.get(i)) * numDays / 7;
				targetList.add(tar + "");
			}
			else {
				targetList.add("0");
			}
		}
		return targetList;
	}
	
	public boolean setWeeklyTargets(String reportName, HashMap<String, Integer> newTargets) {
		try {
			targets.replace(reportName, newTargets);
			out = new FileWriter(new File(file));
			for(String report : targets.keySet()) {
				HashMap<String, Integer> map = targets.get(report);
				out.write(report + "\n");
				for(String col : map.keySet()){
					out.write(col + ":" + map.get(col) + "\n");
				}
				out.write("\n");
			}
			out.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	} 
	
	public static void main(String args[]) {
		try {
			Targets tars = new Targets("txtFiles/Targets.txt");
			HashMap<String, Integer> map = tars.getWeeklyTargets("ActVsProjByFoodSource");
			for(String cat : map.keySet()) {
				System.out.println("The target for " + cat + " is " + map.get(cat));
			}
			System.out.println("------------------");
			map.replace("Retail", 10000000);
			System.out.println(tars.setWeeklyTargets("ActVsProjByFoodSource",map));
			map = tars.getWeeklyTargets("ActVsProjByFoodSource");
			for(String cat : map.keySet()) {
				System.out.println("The target for " + cat + " is " + map.get(cat));
			}
			map.replace("Retail", 575000);
			tars.setWeeklyTargets("ActVsProjByFoodSource",map);
			map = tars.getWeeklyTargets("ActVsProjByFoodSource");
			for(String cat : map.keySet()) {
				System.out.println("The target for " + cat + " is " + map.get(cat));
			}
			
			map = tars.getWeeklyTargets("ActVsProjByDistCat");
			for(String cat : map.keySet()) {
				System.out.println("The target for " + cat + " is " + map.get(cat));
			}
			System.out.println(tars.getClass().getSimpleName());
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
