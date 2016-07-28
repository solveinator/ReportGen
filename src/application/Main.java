package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import java.sql.SQLException;


public class Main extends Application {
	
	private DataIn in;
	private DataOut out;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			in = new DataIn("SawbugWinConnectionUrl.txt");
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			Button btn = new Button("Press Me");
			btn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
						getReport("basic");
						btn.setText("Good Job!"); }
				});
			root.setCenter(btn);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getReport(String queryName) {
		try{
			in.viewTable(in.getConnection(), "Ijustmadethisup", queryName);
		}
		catch(SQLException e) {System.out.println("Error:" + e.getMessage());}
		//while (rs.next()) {
//        String coffeeName = rs.getString("COF_NAME");
//        int supplierID = rs.getInt("SUP_ID");
//        float price = rs.getFloat("PRICE");
//        int sales = rs.getInt("SALES");
//        int total = rs.getInt("TOTAL");
//        System.out.println(coffeeName + "\t" + supplierID +
//                           "\t" + price + "\t" + sales +
//                           "\t" + total);
    //}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
