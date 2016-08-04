package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.io.IOException;
import javafx.scene.control.ScrollPane;

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
	
	private DataIn in;
	private DataOut out;
	private boolean auth;
	private VBox root;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			auth = false;
			
			ReportTemplate.initReports();
			
			root = new VBox();
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();		
						
			in = new DataIn("SawbugWinConnectionUrl.txt", "", ""); //SHORTCUT ONLY FOR DEVELOPMENT --MUST BE REMOVED.
			//primaryStage.getAuth(); //Creates and then closes another stage.
			getReport(); //Populates the current stage
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
	public void getReport() {
		//Once authenticated, show report selection screen.
		
		Label intro = new Label("Welcome to Solveinator's Reports. Please select the report"
				+ " you would like to view.");
		root.getChildren().add(intro);
		
		HBox choices = new HBox();
		root.getChildren().add(choices);
		
		ListView<String> catList = new ListView<String>();
		//catList.addEventHandler();
		choices.getChildren().add(catList);
		ObservableList<String> category = FXCollections.observableArrayList (
			    "Resourcing", "Distribution");
		catList.setItems(category);
		
		ListView<String> repList = new ListView<String>();
		choices.getChildren().add(repList);
		
		ObservableList<String> resouring = 
				FXCollections.observableArrayList(ReportTemplate.getResDic().keySet());
		
		ObservableList<String> distribution = 
				FXCollections.observableArrayList (ReportTemplate.getDistDic().keySet());
		
		repList.setItems(resouring);
		//repList.setItems(distribution);
			
		ScrollPane s1 = new ScrollPane();
		s1.setPrefSize(60, 120);
		s1.setContent(repList);
		s1.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);			
		
		//Buttons
		Button btn = new Button("Generate Report");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					String selection = repList.getSelectionModel().getSelectedItem();
					System.out.println(selection);
					ReportTemplate temp = ReportTemplate.getResDic().get(selection);
					DataOut.exportToExcel("Report.xlsx", "Sheet1", in.viewTable(temp));
					btn.setText("Good Job!"); }
				catch(SQLException e) {System.out.println("Error: SQLError");}
				catch(IOException e) {System.out.println("IO Problem");
				}
			}
		});
		root.getChildren().add(btn);
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
