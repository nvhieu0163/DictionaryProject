package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene home = new Scene(root, 360, 640);
        home.getStylesheets().add("/css/style.css");
        primaryStage.setScene(home);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
