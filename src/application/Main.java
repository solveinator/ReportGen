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
import javafx.scene.control.ChoiceBox;
import java.sql.SQLException;
import java.io.IOException;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.Group;

public class Main extends Application {
	
	private DataIn in;
	private DataOut out;
	private boolean auth; 
	
	@Override
	public void start(Stage primaryStage) {
		try {
			auth = false;
			/* First, get userName and password from user. Compare those 
			 * against the target database. If successful, the DataIn "in" 
			 * object will be created to access that database. If 
			 * unsuccessful, repeat again. 
			 */
			
			//Once authenticated, show report selection screen.
			VBox root = new VBox();
			
			//Scene
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());			
			
			primaryStage.setScene(scene);
			primaryStage.show();			
			
			getAuth();
			
			//Report List 
			Label intro = new Label("Welcome to Solveinator's Reports. Please select the report"
					+ " you would like to view.");
			root.getChildren().add(intro);
			
			//ChoiceBox cb = new ChoiceBox();
			//cb.getItems().addAll("item1", "item2", "item3");
			
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
			
			ObservableList<String> resouring = FXCollections.observableArrayList (
			    "Weekly: Retail Performance Report By Store",  
			    "Monthly: Current month compared to last FY", 
			    "Monthly: Current FYTD compared to last FY",
			    "Monthly: Product Received By Source",
			    "Quarterly: Actuals vs. projected by Food Source",
			    "Quarterly: FYTD Total Sourced",
			    "Quarterly: Received vs. Distributed Rolling 12 month",
			    "Semiannually: Actuals vs. projected by Food Source",
			    "Semiannually: FYTD Total Sourced",
			    "Annually: Actuals vs. projected by Food Source",
			    "Annually: FYTD Total Sourced");
			
			ObservableList<String> distribution = FXCollections.observableArrayList (
				"Weekly: Inventory Trend Report By Storage Type", 
				"Weekly: Distribution vs. Targets",
				"Monthly: Current month compared to last FY",
				"Monthly: Current FYTD compared to last FY",
				"Monthly: Allocated vs Distributed by Food Source",
				"Quarterly: Actuals vs Projected, by Distribution Category",
				"Quarterly: Waste ratio for specific products",
				"Quarterly: Total Distribution by Geographical Area",
				"Semiannually: Actuals vs Projected, by Distribution Category",
				"Semiannually: Waste ratio for specific products",
				"Semiannually: Total Distribution by Geographical Area",
				"Annually: Actuals vs Projected, by Distribution Category",
				"Annually: Waste ratio for specific products",
				"Annually: Total Distribution by Geographical Area");
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
						new DataOut("Report.xlsx", "Sheet1", in.viewTable("basic"));
						btn.setText("Good Job!"); }
					catch(SQLException e) {System.out.println("Error: SQLError");}
					catch(IOException e) {System.out.println("IO Problem");
					}
				}
			});
			root.getChildren().add(btn);
			//root.setBottom(btn);
			
			//List
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getAuth() {
		Stage authStage = new Stage();
		TilePane authRoot = new TilePane();
		authStage.initModality(Modality.APPLICATION_MODAL);
		authStage.setOnCloseRequest(e -> {
			if(!(auth)) {System.exit(0); }
		});
	
		//authStage.
		
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
					//in = new DataIn("SawbugConnectionUrl.txt", userText.getText().trim(), passText.getText().trim());
					//SHORTCUT ONLY FOR DEVELOPMENT --MUST BE REMOVED.
					in = new DataIn("SawbugWinConnectionUrl.txt","",""); 
					auth = true;
					authStage.close();
				}
				catch(SQLException e2) {
					auth = false;
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
