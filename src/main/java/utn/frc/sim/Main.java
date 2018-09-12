package utn.frc.sim;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/views/main-menu.fxml"));
        primaryStage.setTitle("UTN - FRC - SIM - Trabajo practico 3.");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(e -> {Platform.exit(); System.exit(0);});
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
