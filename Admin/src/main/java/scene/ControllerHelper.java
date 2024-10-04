package scene;

import com.example.ABSServer.Utils.Constants;
import com.example.ABSServer.Utils.http.HttpClientUtil;
import bank.Customer;
import bank.Loan;
import bank.TransactionHistory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.Label;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ControllerHelper {
    public static ObservableList<Loan> getLoans(List<Loan> loans){
        return FXCollections.observableArrayList(loans);
    }
    public static ObservableSet<Loan> getLoans(Set<Loan> loans){return FXCollections.observableSet(loans);}
    public static ObservableList<Customer> getCustomers(List<Customer> customers){return FXCollections.observableArrayList(customers);}
    public static ObservableList<TransactionHistory> getHistory(List<TransactionHistory> history){return FXCollections.observableArrayList(history);}


    public static void sendIncreaseYazRequest(){
        HttpClientUtil.runAsync(Constants.FULL_SERVER_PATH + Constants.INCREASE_YAZ, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            }
        });
    }

    public static void sendRewindModeRequest(Label errLabel,boolean rewind){
        String finalUrl = HttpUrl
                .parse(Constants.FULL_SERVER_PATH+Constants.REWIND)
                .newBuilder()
                .addQueryParameter("rewind",String.valueOf(rewind))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    addErrorMessage(errLabel, response.body().string());
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }
        });
    }

    public static void sendDecreaseYAZRequest(Label errLabel){

        HttpClientUtil.runAsync(Constants.FULL_SERVER_PATH+Constants.DECREASE_YAZ, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    addErrorMessage(errLabel, response.body().string());
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(errLabel,"Something went wrong: "+e.getMessage());
            }
        });
    }

    public static void addErrorMessage(Label errLabel, String message){
        new Thread(()->{
            try {
                Platform.runLater(() -> errLabel.setText(message));
                Thread.sleep(3000);
                Platform.runLater(() -> errLabel.setText(""));
            }catch(InterruptedException e){Platform.runLater(() -> errLabel.setText(""));}
        }).start();
    }
}
