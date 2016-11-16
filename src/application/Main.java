package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
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
	Stage proStage;
	RadioButton prevBt;
	RadioButton custBt;
	private TextField sDate;
	private TextField eDate;
	private double progress;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			auth = false;
			res = true;
			initProgress();
			
			ReportTemplate.initReports();
			//in = new DataIn("PrimariusConnectionURL.txt", "", "");
			in = new DataIn("txtFiles/SawbugWinConnectionUrl.txt", "", ""); //SHORTCUT ONLY FOR DEVELOPMENT --MUST BE REMOVED.
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
			catList.getSelectionModel().selectFirst();			
			catList.setMaxWidth(150);
			
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
					resReportNames.add(report.getLongName());
				}
				else {
					distReportNames.add(report.getLongName());
				}
			}
			resourcing = FXCollections.observableArrayList(resReportNames);
			distribution = FXCollections.observableArrayList (distReportNames);			
			repList.setItems(resourcing);
			repList.getSelectionModel().selectFirst();
			repList.setMinWidth(330);
			
			repList.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if(repList.getSelectionModel().getSelectedItem() != null) {
						ReportTemplate repTemp = getReportByName(repList.getSelectionModel().getSelectedItem());
						System.out.println(repTemp.getClass().getSimpleName().substring(0, 4));
						if(repTemp.getClass().getSimpleName().substring(0, 4).equals("FYTD")) {
							prevBt.setText("Fiscal Year to Date");
							prevBt.setUserData("Fiscal Year to Date");
						}
						else{
							String time = repTemp.getTimeFrame();						
							prevBt.setText("Previous " + time);
							prevBt.setUserData("Previous " + time);
						}
					}
				}
			});
			
			ScrollPane s1 = new ScrollPane();
			s1.setPrefSize(60, 120);
			s1.setContent(repList);
			s1.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
			
			final ToggleGroup btGroup = new ToggleGroup();
			VBox btBox = new VBox();
						
		    prevBt = new RadioButton("Previous Month");
		    prevBt.setUserData("Previous Month");
		    prevBt.setToggleGroup(btGroup);
		    btBox.getChildren().add(prevBt);
		    prevBt.setSelected(true);
		    custBt = new RadioButton("Custom Time Frame");
		    custBt.setUserData("Custom Time Frame");
		    custBt.setToggleGroup(btGroup);
		    btBox.getChildren().add(custBt);
		    Label dateDirections = new Label(" Please enter a date\n range in the format\n mm/dd/yyyy");
		    btBox.getChildren().add(dateDirections);
		    sDate = new TextField();
		    btBox.getChildren().add(sDate);
		    eDate = new TextField();
		    btBox.getChildren().add(eDate);
		    Label dateClar = new Label(" (Report includes orders\n from both the start\n and end dates.)");
		    btBox.getChildren().add(dateClar);
		    
		    choices.getChildren().add(btBox);			
			
			//Buttons
			Button go = new Button("Run Report");
			go.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					String selection = repList.getSelectionModel().getSelectedItem();
					if(selection != null) {
						ReportTemplate temp = getReportByName(selection);
						String time = getUserTimeFrame(temp, btGroup.getSelectedToggle().getUserData().toString());						
						if(time != null) {
							runReport(temp, time);
						}
						else {
							//Print out error message saying you screwed up the dates.
						}
					}
				}
			});
			
//			Button details = new Button("View Details");
//			details.setOnAction(new EventHandler<ActionEvent>() {
//				@Override
//				public void handle(ActionEvent event) {
//					editReport(repList);
//					
//				}
//			});
			root.getChildren().add(go);
//			root.getChildren().add(details);
			
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
	
	private ReportTemplate getReportByName(String reportName) {
		ReportTemplate temp = null;
		for(ReportTemplate report : ReportTemplate.getReportList()) {
			if(report.getLongName().equals(reportName)) {
				temp = report;
			}
		}
		if(temp == null) {
			System.out.println("REPORT NOT FOUND");
		}
		return temp;
	}
	
	private void runReport(ReportTemplate temp, String time) {
			try {
				System.out.println(temp.getLongName() + " for " + temp.getStartDate() + " to " 
						+ temp.getEndDate());
				//ReportTemplate temp = ReportTemplate.getResDic().get(selection);
				showProgress();
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
				fileChooser.setInitialFileName("Report.xlsx");
				File fileName = fileChooser.showSaveDialog(proStage);
				if(fileName != null) {
					String path = fileName.getAbsolutePath();
					if(path.substring(path.length()-5).equals(".xlsx")){
						out.setOutputFile(path);
					}
					else {
						out.setOutputFile(path + ".xlsx");
					}
					ArrayList<ArrayList<String>> results = in.queryDB(temp, this);
					if(results != null && results.size() > 0 && results.get(0).size() > 0) {
						temp.makeReport(results, time);
					}
				}
				closeProgress();
			}
			catch(Exception e) {
				closeProgress();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Error: " + e.getClass().getSimpleName());
				alert.setContentText("The program has encountered the following error:\n" + 
						e.getClass() + "\n\n" + e.getMessage());
				alert.show();
				e.printStackTrace();
			}
		}
	
	private void toggleRepList(ListView<String> repList, String sel) {
		if(sel == null) {
			//Do nothing
		}
		else if(res && sel.equals("Distribution")) {
			res = false;
			repList.setItems(distribution);
			repList.getSelectionModel().selectFirst();
		}
		else if((!res) && sel.equals("Resourcing")) {
			res = true;
			repList.setItems(resourcing);
			repList.getSelectionModel().selectFirst();
		}
	}
	
	/**
	 * Returns false if the dates entered are not valid.
	 * Updates dates on report and returns true if the dates entered are valid. 
	 */
	private String getUserTimeFrame(ReportTemplate repTemp, String radioBtSelection) {
		System.out.println(radioBtSelection);
		if(radioBtSelection.equals(prevBt.getText())) {
			return prevBt.getText();
		}
		else {
			String staDate = sDate.getText();
			String endDate = eDate.getText();
			if(staDate == null || endDate == null || staDate == "" || endDate == "") {
				return null;
			}
			String[] staPieces = staDate.split("/");
			String[] endPieces = endDate.split("/");
			if(staPieces.length != 3 || endPieces.length != 3){
				return null;
					}
			try {
				int sYear = Integer.parseInt(staPieces[2]);
				int sMo = Integer.parseInt(staPieces[0]);
				int sDay = Integer.parseInt(staPieces[1]);
				int eYear = Integer.parseInt(endPieces[2]);
				int eMo = Integer.parseInt(endPieces[0]);
				int eDay = Integer.parseInt(endPieces[1]);
				if(repTemp.setStartDate(sYear,sMo,sDay) && repTemp.setEndDate(eYear,eMo,eDay)) {
					return sMo +"/"+ sDay +"/"+ sYear +" to "+ eMo +"/"+ eDay +"/"+ eYear;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}			
			return null;
		}
		//else continue with the default dates
	}
	
	public void setProgress(double pro) {
		progress = pro;
	}
	
	private void showProgress() {
		proStage.show();
	}
	
	private void initProgress() {		
		//pi = new ProgressBar(0.5F);
		Label proLabel = new Label("Processing");
		proLabel.setText("Processing");
		proStage = new Stage();
		TilePane proRoot = new TilePane();
		Scene proScene = new Scene(proRoot);	
		proStage.initModality(Modality.APPLICATION_MODAL);
		proStage.setScene(proScene);
		proRoot.getChildren().add(proLabel);
		//proRoot.getChildren().add(pi);		
	}
	
	private void closeProgress() {
		proStage.close();
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
					in = new DataIn("PrimariusConnectionURL.txt",userText.getText().trim(), passText.getText().trim());
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
	
	public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(200);
                    //updateMessage("2000 milliseconds");
                    updateProgress(progress, 1);
                }
                return true;
            }
        };       
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
