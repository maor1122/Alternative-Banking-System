package scene;

import com.example.ABSServer.Utils.Constants;
import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.http.HttpClientUtil;
import bank.Bank;
import bank.Customer;
import bank.Loan;
import bank.TransactionHistory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ControllerHelper {

    public static ObservableList<Loan> getLoans(List<Loan> loans){
        return FXCollections.observableArrayList(loans);
    }
    public static ObservableSet<Loan> getLoans(Set<Loan> loans){
        return FXCollections.observableSet(loans);
    }

    public static ObservableList<Customer> getCustomers(List<Customer> customers){
        return FXCollections.observableArrayList(customers);
    }
    public static ObservableList<TransactionHistory> getHistory(List<TransactionHistory> history){
        return FXCollections.observableArrayList(history);
    }


    public static void sendPutLoanOnSaleRequest(String name,String loanId,Label errLabel) throws Exception{
        RequestBody body = new FormBody.Builder()
                .add("seller",name)
                .add("loan",loanId)
                .build();
        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH+Constants.PUT_LOAN_FOR_SALE)
                .post(body)
                .build();
        Call call = new OkHttpClient.Builder()
                .followRedirects(false)
                .build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
                    addErrorMessage(errLabel,response.code()+response.body().string());
                }else{
                    addErrorMessage(errLabel,"Loan is now on sale");
                }
            }
        });
    }


    public static void sendBuyLoanRequest(String seller,String buyer,String loanId,Label errLabel) throws Exception{
        RequestBody body = new FormBody.Builder()
                .add("seller",seller)
                .add("buyer",buyer)
                .add("loan",loanId)
                .build();
        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH+Constants.BUY_LOAN_OWNERSHIP)
                .post(body)
                .build();
        Call call = new OkHttpClient.Builder()
                .followRedirects(false)
                .build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
                    addErrorMessage(errLabel,response.code()+response.body().string());
                }else{
                    addErrorMessage(errLabel,"Bought loan successfully!");
                }
            }
        });
    }

    public static void sendInvestInLoansRequest(ArrayList<Loan> loans,String name,int amount,double maxOwnership,Label errLabel) throws Exception{
        String codedloans = Decoder.toString(loans);
        RequestBody body = new FormBody.Builder()
                .add("customer",name)
                .add("maxOwnership",String.valueOf(maxOwnership))
                .add("amount",String.valueOf(amount))
                .add("loans",codedloans)
                .build();
        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH+Constants.INVEST)
                .post(body)
                .build();

        Call call = new OkHttpClient.Builder()
                .followRedirects(false)
                .build().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) throws ExceptionInInitializerError {
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
                    addErrorMessage(errLabel,response.body().string());
                }else{
                    addErrorMessage(errLabel,"Investment complete!");
                }
            }
        });
    }

    public static void sendWithdrawRequest(String name,int amount, Label PaymentErrLabel) throws Exception{
        String finalUrl = HttpUrl
                .parse(Constants.FULL_SERVER_PATH+Constants.WITHDRAW)
                .newBuilder()
                .addQueryParameter("name", name)
                .addQueryParameter("amount",String.valueOf(amount).toString())
                .build()
                .toString();
        //updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
                    addErrorMessage(PaymentErrLabel,response.body().string());
                }else addErrorMessage(PaymentErrLabel,"Withdraw complete!");
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(PaymentErrLabel,"Something went wrong: " + e.getMessage());
            }
        });
    }

    public static void sendFullyPayLoanRequest(String name,String loanId,Label errLabel) throws Exception{
        RequestBody body = new FormBody.Builder()
                .add("name",name)
                .add("loan",loanId)
                .build();
        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH+Constants.PAY_LOAN)
                .post(body)
                .build();
        Call call = new OkHttpClient.Builder()
                .followRedirects(false)
                .build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
                    addErrorMessage(errLabel,response.body().string());
                }else{
                    System.out.println("Payment successful");
                }
            }
        });
    }

    public static void sendLoanDebtPaymentRequest(String name,String loanId,int amount,Label errLabel) throws Exception{
        RequestBody body = new FormBody.Builder()
                .add("name",name)
                .add("loan",loanId)
                .add("amount",String.valueOf(amount))
                .build();
        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH+Constants.PAY_DEBT)
                .post(body)
                .build();
        Call call = new OkHttpClient.Builder()
                .followRedirects(false)
                .build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
                    addErrorMessage(errLabel,response.code()+response.body().string());
                }else{
                    System.out.println("Payment successful");
                }
            }
        });
    }

    public static void sendChargeRequest(String name,int amount, Label PaymentErrLabel){
        String finalUrl = HttpUrl
                .parse(Constants.FULL_SERVER_PATH+Constants.CHARGE)
                .newBuilder()
                .addQueryParameter("name",name)
                .addQueryParameter("amount",String.valueOf(amount).toString())
                .build()
                .toString();
        //updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("respose code: "+response.code()+" respose message: "+response.body().string());
                if(response.code()!=200){
                    addErrorMessage(PaymentErrLabel,response.body().string());
                }else addErrorMessage(PaymentErrLabel,"Charging complete!");
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                addErrorMessage(PaymentErrLabel,"Something went wrong: " + e.getMessage());
            }
        });
    }

    public static void addLoansFromFileRequest(Bank bank,String owner,Label errLabel) throws Exception{
        String codedBank = Decoder.toString(bank);
        RequestBody body = new FormBody.Builder()
                .add("owner",owner)
                .add("bank",codedBank)
                .build();
        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH+Constants.ADDLOANS)
                .post(body)
                .build();

        Call call = new OkHttpClient.Builder()
                .followRedirects(false)
                .build().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) throws ExceptionInInitializerError{
                addErrorMessage(errLabel,"Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    addErrorMessage(errLabel,"Added loans successfully");
                }else addErrorMessage(errLabel,response.body().string());
            }
        });
    }

    public static void addErrorMessage(Label errLabel,String message){
        new Thread(()->{
            try {
                System.out.println("Error: "+message);
                Platform.runLater(() -> errLabel.setText(message));
                Thread.sleep(3000);
                Platform.runLater(() -> errLabel.setText(""));
            }catch(InterruptedException e){Platform.runLater(() -> errLabel.setText(""));}
        }).start();
    }
}
