package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.io.IOException;
import javafx.scene.control.ScrollPane;
import javafx.fxml.FXMLLoader;

/**
 * @author Solveig Osborne
 * @version 2016_08_03
 *
 * This class is the main class for the Solveinator Report Generation application. It contains 
 * the GUI and interacts with the other classes as necessary. 
 * 
 * This application contains a main pane with report options to choose from. 
 * 
 * It also contains an optional authentication pane. If using SQL authentication, the 
 * authentication pane takes the user name and password info. If using Windows 
 * authentication, the authentication pane is unnecessary and can be commented out. 
 */

public class Main extends Application {
	
	public static boolean testing = false;
	private DataIn in;
	private DataOut out;
	private boolean auth;
	private VBox root;
	private boolean res;
	ObservableList<String> resourcing;
	ObservableList<String> distribution;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			auth = false;
			res = true;
			
			ReportTemplate.initReports();
			//in = new DataIn("PrimariusConnectionURL.txt", "", "");
			in = new DataIn("SawbugWinConnectionUrl.txt", "", ""); //SHORTCUT ONLY FOR DEVELOPMENT --MUST BE REMOVED.
			//primaryStage.getAuth(); //Creates and then closes another stage.
			//Once authenticated, show report selection screen.
			//GridPane root2 = FXMLLoader.load(getClass().getResource("fxml_root.fxml"));
			
			root = new VBox();
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();		
			
			Label intro = new Label("Welcome to Solveinator's Reports. Please select the report"
					+ " you would like to view.");
			root.getChildren().add(intro);
			
			HBox choices = new HBox();
			root.getChildren().add(choices);
			
			ListView<String> catList = new ListView<String>();
			choices.getChildren().add(catList);
			ObservableList<String> category = FXCollections.observableArrayList (
				    "Resourcing", "Distribution");
			catList.setItems(category);
			
			ListView<String> repList = new ListView<String>();
			choices.getChildren().add(repList);
			
			catList.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					toggleRepList(repList, catList.getSelectionModel().getSelectedItem());
				}
			});
			
			ArrayList<String> distReportNames = new ArrayList<String>();
			ArrayList<String> resReportNames = new ArrayList<String>();
			for(ReportTemplate report : ReportTemplate.getReportList()) {
				if(report.getResStatus()) {
					resReportNames.add(report.getName());
				}
				else {
					distReportNames.add(report.getName());
				}
			}
			resourcing = FXCollections.observableArrayList(resReportNames);
			distribution = FXCollections.observableArrayList (distReportNames);			
			repList.setItems(resourcing);
			
			ScrollPane s1 = new ScrollPane();
			s1.setPrefSize(60, 120);
			s1.setContent(repList);
			s1.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);			
			
			//Buttons
			Button go = new Button("Run Report");
			go.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					runReport(repList);
					
				}
			});
			
			Button details = new Button("View Details");
			details.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					editReport(repList);
					
				}
			});
			root.getChildren().add(go);
			root.getChildren().add(details);
			
			//Report List 
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Allows user to select the report they want generated. The selected report
	 * is sent to Report.xlsx
	 */
	public void editTargets() {
		
	} 
	
	private void runReport(ListView<String> repList) {
		if(repList.getSelectionModel().getSelectedItem() != null) {
			String reportName = repList.getSelectionModel().getSelectedItem().trim();
			try {
				System.out.println(reportName);
				ReportTemplate temp = null;
				for(ReportTemplate report : ReportTemplate.getReportList()) {
					if(report.getName().equals(reportName)) {
						temp = report;
					}
				}
				if(temp == null) {
					System.out.println("REPORT NOT FOUND");
				}

				//ReportTemplate temp = ReportTemplate.getResDic().get(selection);
				ArrayList<ArrayList<String>> results = in.queryDB(temp);
				if(results != null && results.size() > 0 && results.get(0).size() > 0) {
					//temp.getCleanRules();
					temp.cleanData(results);
					temp.makeReport(results);
					//btn.setText("Done"); 
				}
			}
			catch(SQLException e) {System.out.println("Error: SQLError\n" + e.getMessage());}
		}
	}
	
	private void editReport(ListView<String> repList) {
		
	}
	
	private void toggleRepList(ListView<String> repList, String sel) {
		if(res && sel.equals("Distribution")) {
			res = false;
			repList.setItems(distribution);
		}
		else if((!res) && sel.equals("Resourcing")) {
			res = true;
			repList.setItems(resourcing);
		}
	}
	
	/**
	 * Allows user to provide user name and password to connect to the database. 
	 */
	public void getAuth() {
		/* First, get userName and password from user. Compare those 
		 * against the target database. If successful, the DataIn "in" 
		 * object will be created to access that database. If 
		 * unsuccessful, repeat again. 
		 */
		Stage authStage = new Stage();
		TilePane authRoot = new TilePane();
		authStage.initModality(Modality.APPLICATION_MODAL);
		authStage.setOnCloseRequest(e -> {
			if(!(auth)) {System.exit(0); }
		});
		
		//Scene
		Scene authScene = new Scene(authRoot,300,200);
		//scene.getStylesheets().add(getClass().getResource("auth.css").toExternalForm());			
		authStage.setScene(authScene);
		authStage.show();
		
		//Text Input Fields
		Label userLabel = new Label("User Name");
		TextField userText = new TextField();
		Label passLabel = new Label("Password");
		TextField passText = new TextField();
		Label warningLabel = new Label("");
		
		authRoot.getChildren().add(userLabel);
		authRoot.getChildren().add(userText);
		authRoot.getChildren().add(passLabel);
		authRoot.getChildren().add(passText);
		authRoot.getChildren().add(new Label(""));
		authRoot.getChildren().add(warningLabel);
		
		//Buttons
		Button can = new Button("Cancel");
		can.setOnAction(e -> System.exit(0));
		Button sub = new Button("Submit");
		sub.setOnAction(e -> {
				try {
					in = new DataIn("PrimariusConnectionURL.txt", userText.getText().trim(), passText.getText().trim());
					//in = new DataIn("SawbugConnectionUrl.txt", userText.getText().trim(), passText.getText().trim());
					//in = new DataIn("SawbugWinConnectionUrl.txt","",""); 
					auth = true;
					authStage.close();
				}
				catch(SQLException e2) {
					auth = false;
					System.out.println(e2.getMessage());
					warningLabel.setText("Invalid credentials");					
					//Do nothing --eventually have error box. 
				}});
		
		authRoot.getChildren().add(can);
		authRoot.getChildren().add(sub);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
