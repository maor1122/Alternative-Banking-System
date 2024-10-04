package main;


import com.example.ABSServer.Utils.http.HttpClientUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login.LoginController;

import java.io.IOException;
import java.net.URL;

import static com.example.ABSServer.Utils.Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

public class Client extends Application {
    LoginController loginController;
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(400);
        primaryStage.setTitle("Customer Login");

        URL loginPage = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            loginController = fxmlLoader.getController();

            Scene scene = new Scene(root, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
        loginController.close();
    }

    public static void main(String [] args){
        launch(args);
    }
}
