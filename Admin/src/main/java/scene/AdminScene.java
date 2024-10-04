package scene;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class AdminScene extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);

        primaryStage.setTitle("Alternative Banking System");

        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/adminScene.fxml")));

        Scene scene = new Scene(load, 1400, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void run(String args[]){
        launch(args);
    }
}
