package scene;

import api.HttpStatusUpdate;
import bank.*;
import com.example.ABSServer.Utils.Constants;
import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ABSServer.Utils.Constants.REFRESH_RATE;
import static scene.ControllerHelper.*;

public class Controller implements Initializable {
    private Timer timer;
    boolean rewindMode = false;
    Bank bank=null;
    String name ="";
    private TimerTask bankRefresher;
    private boolean autoUpdate = true;
    private HttpStatusUpdate httpStatusUpdate;

    @FXML private Label helloLabel;
    @FXML private Label PathLabel;
    @FXML private TextArea LoanInformationTA;
    @FXML private Label YazLabel;
    @FXML private TableView<Loan> LoansTableView;
    @FXML private TableColumn<Loan, String> AdminIDC;
    @FXML private TableColumn<Loan, String> AdminLoanerC;
    @FXML private TableColumn<Loan, String> AdminCategoryC;
    @FXML private TableColumn<Loan, Integer> AdminTotalWorthC;
    @FXML private TableColumn<Loan, Integer> AdminInterestC;
    @FXML private TableColumn<Loan, String> AdminStatusC;
    @FXML private TableColumn<Loan, String> AdminLendersC;
    @FXML private TableView<Customer> CustomersTableView;
    @FXML private TableColumn<Customer, String> AdminNameC;
    @FXML private TableColumn<Customer, Integer> AdminCurrentBalanceC;
    @FXML private TableColumn<Customer, Integer> AdminLoanedC;
    @FXML private TableColumn<Customer, Integer> AdminLendedC;
    @FXML private Button DecreaseYAZButton;
    @FXML private Label Error_Label;
    @FXML private Button IncreaseYAZButton;
    @FXML private Button RewindModeButton;

    public void setHelloLabelName(String name){
        helloLabel.setText("Hello, "+name+".");
        this.name=name;
    }

    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate) {
        this.httpStatusUpdate = httpStatusUpdate;
    }

    @FXML void IncreaseYaz(ActionEvent event) {
        sendIncreaseYazRequest();
        getBank();
    }

    @FXML void Rewind(ActionEvent event){
        if(rewindMode) {
            this.rewindMode = false;
            RewindModeButton.setText("Rewind Mode: off");
        }

        else {
            this.rewindMode = true;
            RewindModeButton.setText("Rewind Mode: on");
        }
        IncreaseYAZButton.setDisable(rewindMode);
        DecreaseYAZButton.setDisable(!rewindMode);
        sendRewindModeRequest(Error_Label,rewindMode);
        getBank();
    }

    @FXML void DecreaseYAZ(ActionEvent event){
        getBank();
        if(bank.getYaz()>0) {
            sendDecreaseYAZRequest(Error_Label);
            getBank();
        }
        else
            addErrorMessage(Error_Label,"yaz has to be bigger then 0!");
    }

    private void getBank(){
        String url = Constants.FULL_SERVER_PATH+Constants.BANK;
        HttpClientUtil.runAsync(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    System.out.println("Something went wrong: " + responseBody);
                } else {
                    Platform.runLater(() -> {
                        //System.out.println("Bank request resulted in response code 200");
                        try {
                            String responseBody = response.body().string();
                            if(!responseBody.equals("")) {
                                bank = (Bank) Decoder.fromString(responseBody);
                                YazLabel.setText("Current Yaz: "+bank.getYaz());
                                //bank.getCustomers().stream().map(Customer::getName).forEach(System.out::println);
                                refreshAdminTables();
                            }
                            else
                                System.out.println("reponseBody is empty.");
                        }catch(Exception e){e.printStackTrace();}
                    });
                }
            }
        });
    }

    public void initialize(URL url, ResourceBundle rb){
        initAdminTableView();
        startBankRefresher();
        getBank();
        DecreaseYAZButton.setDisable(true);
    }

    public void refreshAdminTables(){
        int loanIndex = LoansTableView.getSelectionModel().getSelectedIndex();
        int customerIndex = CustomersTableView.getSelectionModel().getSelectedIndex();
        //Loan loan = LoansTableView.getSelectionModel().getSelectedItem();
        //Customer customer = CustomersTableView.getSelectionModel().getSelectedItem();
        LoansTableView.getItems().clear();
        CustomersTableView.getItems().clear();

        LoansTableView.setItems(getLoans(bank.getLoans()));
        CustomersTableView.setItems(getCustomers(bank.getCustomers()));
        try{
            //LoansTableView.getSelectionModel().select(loan);
            LoansTableView.getSelectionModel().select(loanIndex);
            CustomersTableView.getSelectionModel().select(customerIndex);
            //CustomersTableView.getSelectionModel().select(customer);
        }catch(Exception ignored){}
    }


    public void initAdminTableView(){
        //Admin loans tableView
        AdminIDC.setCellValueFactory(new PropertyValueFactory<Loan,String>("id"));
        AdminCategoryC.setCellValueFactory(new PropertyValueFactory<Loan,String>("category"));
        AdminInterestC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("interest"));
        AdminStatusC.setCellValueFactory(new PropertyValueFactory<Loan,String>("status"));
        AdminLoanerC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner().getName()));
        AdminLendersC.setCellValueFactory(cellData -> {
                StringJoiner str = new StringJoiner(", ");
                cellData.getValue().getShareHolders().forEach((customer, integer) -> str.add(customer.getName()+": "+integer));
                return new SimpleStringProperty(str.toString());
        });
        AdminTotalWorthC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("capital"));
        // Admin customers tableView
        AdminNameC.setCellValueFactory(new PropertyValueFactory<Customer,String>("name"));
        AdminCurrentBalanceC.setCellValueFactory(new PropertyValueFactory<Customer,Integer>("balance"));
        AdminLoanedC.setCellValueFactory(cellData -> {
            SimpleIntegerProperty temp = new SimpleIntegerProperty(
                    cellData.getValue().getLoans().stream().mapToInt(Loan::getCapital).sum());
            return temp.asObject();
        });
        AdminLendedC.setCellValueFactory(cellData -> {
            SimpleIntegerProperty temp = new SimpleIntegerProperty(
                    cellData.getValue().getShareHolder().values().stream().mapToInt(Integer::intValue).sum());
            return temp.asObject();
        });
        LoansTableView.setOnMouseClicked((MouseEvent event) -> {
            try {
                TableView t = (TableView) event.getSource();
                Loan loan = (Loan) t.getSelectionModel().getSelectedItem();
                printLoanInformation(loan);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        });
    }

    public void printLoanInformation(Loan loan){
        StringJoiner str = new StringJoiner("");
        if(loan.getStatus().equals("pending")){
            str.add("in total gathered: "+loan.getCurrBalance()+", money needed until active: "+(loan.getCapital()-loan.getCurrBalance()));
        }
        else if(loan.getStatus().equals("active")) {
            str.add("Started: " + loan.getYazStarted() + "\nNext payment at time: " + loan.getNextYazPayment() + "\nTotal payment left: " + (loan.getTotalPayment() - loan.getCurrBalance()) +
                    "\n\nPayments:\n");
            str.add(getPayment(loan));
        }
        else if(loan.getStatus().equals("risk")){
            str.add(getPayment(loan));
        }
        else if(loan.getStatus().equals("finished")){
            str.add("Started: "+loan.getYazStarted()+"\nFinished: "+loan.getYazFinished());
        }
        LoanInformationTA.setText(str.toString());
    }

    String getPayment(Loan loan){
        int amount = Math.round(loan.getTotalPayment()/loan.getTotalTime()*loan.getYazBetweenPayments());
        int j=1;
        String str = "";
        str=str.concat("\n");
        for(TransactionHistory payment:loan.getPayments()) {
            str = str.concat("  " + (j++) + ". time: " + payment.getTime() + ", capital: " + payment.getTransferAmount()/(1+(double)loan.getInterest()/100) + ", interest: " + (payment.getTransferAmount()-payment.getTransferAmount()/(1+(double)loan.getInterest()/100)) + ", in total: " + (payment.getTransferAmount())+"\n");
        }
        for(Debt missedPayment:loan.getDebts())
            str = str.concat("MISSED!   " + (j++) + ". time: " + missedPayment.getYaz() + ", Payment left: " + missedPayment.getAmount());
        if(loan.getDebts().size()!=0){
            str = str.concat("\n Owed "+loan.getDebts().size()+" debts for a total of "+ loan.getDebts().stream().mapToInt(Debt::getAmount).sum());
        }

        str = str.concat("\nSo far payed in total; capital: "+Math.round((double)loan.getCurrBalance()/(1+(double)loan.getInterest()/100))+ ", interest: "+Math.round(loan.getCurrBalance()-(double)loan.getCurrBalance()/(1+(double)loan.getInterest()/100))+", total: "+loan.getCurrBalance()+"\n");
        str = str.concat("Total Payment left: "+Math.round(loan.getCapital()-loan.getCurrBalance())+
                ", interest left to pay: "+Math.round((double)loan.getInterest()/100*loan.getCapital()-(double)loan.getInterest()/100*loan.getCurrBalance())+".\n");
        return str;
    }
    public void startBankRefresher() {
    /*
        bankRefresher = new BankRefresher(
                autoUpdate,
                httpStatusUpdate::updateHttpLine,
                this::updateBank);

        timer = new Timer();

        timer.schedule(bankRefresher, REFRESH_RATE, REFRESH_RATE);
    */
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {getBank();}},REFRESH_RATE,REFRESH_RATE);
    }

    private void updateBank(Bank bank){
        Platform.runLater(()->{
            if(bank!=null) {
                System.out.println(bank.getCustomers().size());
                bank.getCustomers().forEach(System.out::println);
            }
            else
                System.out.println("bank is null");
        });
    }
}
