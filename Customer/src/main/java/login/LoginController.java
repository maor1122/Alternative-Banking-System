package login;

import com.example.ABSServer.Utils.Constants;
import com.example.ABSServer.Utils.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import scene.Controller;

import java.io.Closeable;
import java.io.IOException;

public class LoginController implements Closeable {
    private Scene scene;
    private Stage stage;
    private Parent root;

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    //private AppMainController AppMainController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("state","customer")
                .build()
                .toString();
        //updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    System.out.println(response.body().string());
                    Platform.runLater(() -> {
                        try {
                            switchToCustomerScene(event, userName);
                            System.out.println("new name :" + userName);
                        }catch(Exception e){e.printStackTrace();}
                    });
                }
            }
        });
    }

    public void switchToCustomerScene(ActionEvent event,String userName) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/customerScene.fxml"));
        root = loader.load();
        Controller controller= loader.getController();
        controller.setHelloLabelName(userName);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Customer DashTable");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void close() throws IOException {
        //chatRoomComponentController.close();
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }
/*
    private void updateHttpStatusLine(String data) {
        AppMainController.updateHttpLine(data);
    }
*/
    //public void setMainController(AppMainController AppMainController) {
    //    this.AppMainController = AppMainController;
    //}
}
