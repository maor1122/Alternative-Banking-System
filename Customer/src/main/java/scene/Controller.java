package scene;

import com.example.ABSServer.Utils.Constants;
import com.example.ABSServer.Utils.Decoder;
import Utils.fileLoader;
import com.example.ABSServer.Utils.http.HttpClientUtil;
import bank.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.ABSServer.Utils.Constants.REFRESH_RATE;
import static scene.ControllerHelper.*;

public class Controller implements Initializable {
    private final FilteredData filteredData = new FilteredData();
    private String name;
    private Bank bank=null;
    private Timer timer;

    @FXML private Button InformationButton;
    @FXML private Button ScrambleButton;
    @FXML private Button PaymentButton;
    @FXML private StackPane CustomerMenu;
    @FXML private Label Error_Label;
    @FXML private Label helloLabel;
    @FXML private TextArea LoanInformationTA;
    @FXML private StackPane stackPane;
    @FXML private Label YazLabel;
    @FXML private TableView<Loan> CustomerLoansTV;
    @FXML private TableColumn<Loan, String> CustomerLoanIDC;
    @FXML private TableColumn<Loan, String> CustomerLoanCategoryC;
    @FXML private TableColumn<Loan, Integer> CustomerLoanCapitalC;
    @FXML private TableColumn<Loan, Integer> CustomerLoanTimingC;
    @FXML private TableColumn<Loan, Integer> CustomerLoanInterestC;
    @FXML private TableColumn<Loan, String> CustomerLoanStatusC;
    @FXML private TableView<Loan> CustomerLendsTV;
    @FXML private TableColumn<Loan, String> CustomerLendIDC;
    @FXML private TableColumn<Loan, String> CustomerLendCategoryC;
    @FXML private TableColumn<Loan, Integer> CustomerLendCapitalC;
    @FXML private TableColumn<Loan, Integer> CustomerLendTimingC;
    @FXML private TableColumn<Loan, Integer> CustomerLendInterestC;
    @FXML private TableColumn<Loan, String> CustomerLendStatusC;
    @FXML private TableView<TransactionHistory> TransferTV;
    @FXML private TableColumn<TransactionHistory, Integer> TransferYazC;
    @FXML private TableColumn<TransactionHistory, Integer> TransferAmountC;
    @FXML private TableColumn<TransactionHistory, Integer> TransferBeforeC;
    @FXML private TableColumn<TransactionHistory, Integer> TransferAfterC;
    @FXML private TextField AmountTF;
    @FXML private TextField MinInterestTF;
    @FXML private TextField MinTotalYazTF;
    @FXML private TextField MaxOpenLoansTF;
    @FXML private TextField MaxOwnershipTF;
    @FXML private ListView<String> CategoriesListView;
    @FXML private Label FilterLoansLabel;
    @FXML private TableView<Loan> InvestmentLoansTableView;
    @FXML private TableColumn<Loan, String> AdminIDC1;
    @FXML private TableColumn<Loan, String> AdminLoanerC1;
    @FXML private TableColumn<Loan, String> AdminCategoryC1;
    @FXML private TableColumn<Loan, Integer> AdminTotalWorthC1;
    @FXML private TableColumn<Loan, Integer> AdminInterestC1;
    @FXML private TableColumn<Loan, String> AdminStatusC1;
    @FXML private TableView<Loan> ActiveLoansTV;
    @FXML private TableColumn<Loan, String> ActiveLoanIDC;
    @FXML private TableColumn<Loan, String> ActiveLoanCategoryC;
    @FXML private TableColumn<Loan, Integer> ActiveLoanPaymentLeftC;
    @FXML private TableColumn<Loan, Integer> ActiveLoanTimingC;
    @FXML private TableColumn<Loan, Integer> ActiveLoanInterestC;
    @FXML private TableColumn<Loan, String> ActiveLoanStatusC;
    @FXML private TableColumn<Loan, String> ActiveLoanNextPayment;
    @FXML private TextArea NotificationsTA;
    @FXML private Label CurrentBalanceLabel;
    @FXML private TextField TransactionAmountTF;
    @FXML private Label InformationErrLabel;
    @FXML private TextField PaymentSpecificTA;
    @FXML private Label PaymentErrLabel;
    @FXML private Label AddLoansErrLabel;
    @FXML private Button TakeLoanButton;
    @FXML private TextField TakeLoanIDTF;
    @FXML private TextField TakeLoanCategoryTF;
    @FXML private TextField TakeLoanCapitalTF;
    @FXML private TextField TakeLoanTotalYazTF;
    @FXML private TextField TakeLoanYazBetweenPaymentTF;
    @FXML private TextField TakeLoanInterestTF;
    @FXML private Button ChargeButton;
    @FXML private Button WithdrawButton;
    @FXML private Button PaySpecificButton;
    @FXML private Button PayAllDebtButton;
    @FXML private Button PayLoanButton;
    @FXML private Button FilterLoansButton;
    @FXML private Button InvestButton;
    @FXML private Button InvestInAllButton;
    @FXML private Button MakeLoanRequestButton;
    @FXML private Button AddLoansFromFileButton;
    @FXML private TableView<Loan> SellLoansTV;
    @FXML private TableColumn<Loan, String> SellLoansIdC;
    @FXML private TableColumn<Loan, Integer> SellLoansEndInYazC;
    @FXML private TableColumn<Loan, Integer> SellLoansOwnershipC;
    @FXML private TableColumn<Loan, Integer> SellLoansPaymentSpacingC;
    @FXML private TableColumn<Loan, Integer> SellLoansInterestC;
    @FXML private TableColumn<Loan, Integer> SellLoansPaymentLeftC;
    @FXML private TableView<LoanForSale> BuyLoansTV;
    @FXML private TableColumn<LoanForSale, String> BuyLoansIdC;
    @FXML private TableColumn<LoanForSale, Integer> BuyLoansEndInYazC;
    @FXML private TableColumn<LoanForSale, Integer> BuyLoansOwnershipC;
    @FXML private TableColumn<LoanForSale, Integer> BuyLoansPaymentSpacingC;
    @FXML private TableColumn<LoanForSale, Integer> BuyLoansInterestC;
    @FXML private TableColumn<LoanForSale, Integer> BuyLoansPaymentLeftC;
    //@FXML private Button
    //@FXML private Button



    public void setHelloLabelName(String name){
        helloLabel.setText("Hello, "+name+".");
        this.name = name;
    }

    @FXML void SellLoans(ActionEvent event) {
        Loan loan = SellLoansTV.getSelectionModel().getSelectedItem();
        try {
            sendPutLoanOnSaleRequest(name, loan.getId(), AddLoansErrLabel);
        }catch(Exception e){addErrorMessage(AddLoansErrLabel,e.getMessage());}
    }

    @FXML void BuyLoans(ActionEvent event){
        LoanForSale loanForSale = BuyLoansTV.getSelectionModel().getSelectedItem();
        try{
            sendBuyLoanRequest(loanForSale.getSeller().getName(),name, loanForSale.getLoan().getId(), AddLoansErrLabel);
        }catch(Exception e){addErrorMessage(AddLoansErrLabel,e.getMessage());}
    }

    @FXML void AddLoansFromFile(ActionEvent event) {
        loadXMLFile(event);
    }

    @FXML void MakeLoanRequest(ActionEvent event) {
        String id;
        String category;
        int capital;
        int totalYaz;
        int yazBetweenPayment;
        int interest;

        try {
            id = TakeLoanIDTF.getText().trim();
            category = TakeLoanCategoryTF.getText().trim();
            capital = Integer.parseInt(TakeLoanCapitalTF.getText());
            totalYaz = Integer.parseInt(TakeLoanTotalYazTF.getText());
            yazBetweenPayment = Integer.parseInt(TakeLoanYazBetweenPaymentTF.getText());
            interest = Integer.parseInt(TakeLoanInterestTF.getText());
            if(category.length()==0 || id.length()==0) throw new Exception();
        }catch(Exception e){
            addErrorMessage(AddLoansErrLabel,"Invalid characters");
            return;
        }
        try{
            if(!(capital>0)) throw new Exception("Loan capital has to be higher then 0!");
            if(!(totalYaz>0)) throw new Exception("Total Yaz has to be higher then 0");
            if(totalYaz%yazBetweenPayment!=0) throw new Exception("Yaz timings are not valid.");
            if(interest<0) throw new Exception("interest cant be less then 0.");
            List<Loan> loans = new ArrayList<>();
            Loan loan = new Loan(id,null,category,capital,totalYaz,yazBetweenPayment,interest, bank.getYaz());
            loans.add(loan);
            List<String> categories = new ArrayList<>();
            categories.add(category);
            Bank bankLoans = new Bank();
            bankLoans.setLoans(loans);
            bankLoans.setCategories(categories);
            addLoansFromFileRequest(bankLoans,name,AddLoansErrLabel);
        }catch(Exception e) {addErrorMessage(AddLoansErrLabel,e.getMessage());}
    }

    @FXML void FilterLoans(ActionEvent event){
        try{
            Customer customer = bank.getCustomers().stream().filter(obj -> Objects.equals(obj.getName(), name)).findFirst().get();
            int minInterest,minTotalYaz,amount,maxOwnerShip,maxOpenLoans;
            if(AmountTF.getText()=="") throw new Exception("Please insert amount!");
            if(customer.getBalance()<Integer.parseInt(AmountTF.getText())) throw new Exception("Not enough money!");
            if((amount=Integer.parseInt(AmountTF.getText()))<=0) throw new Exception("Minimum amount has to be more then 0!");
            if(MinInterestTF.getText().isEmpty()) minInterest=0;
            else if((minInterest=Integer.parseInt(MinInterestTF.getText()))<=0) throw new Exception("Minimum interest has to be more then 0!");
            if(MaxOpenLoansTF.getText().isEmpty()) maxOpenLoans=0;
            else if((maxOpenLoans=Integer.parseInt(MaxOpenLoansTF.getText()))<0) throw new Exception("Max loaner open loans has to be more then or equal to 0!");
            if(MinTotalYazTF.getText().isEmpty()) minTotalYaz=0;
            else if((minTotalYaz=Integer.parseInt(MinTotalYazTF.getText()))<=0) throw new Exception("Minimum total yaz has to be more then 0!");
            if(MaxOwnershipTF.getText().isEmpty()) maxOwnerShip=100;
            else if((maxOwnerShip = Integer.parseInt(MaxOwnershipTF.getText()))<1 || Integer.parseInt(MaxOwnershipTF.getText())>100) throw new Exception("Maximum Loan ownership percentage has to be between 100 and 1");
            FilterLoansLabel.setText("");
            List<String> categories = CategoriesListView.getSelectionModel().getSelectedItems();
            if(categories.isEmpty())
                categories=new ArrayList<>(new HashSet<String>(bank.getCategories()));
            List<Loan> loans = bank.filterLoans(categories, minInterest, minTotalYaz,maxOpenLoans, customer);
            filteredData.setAmount(amount);
            filteredData.setMaxOwnerShip(((double)maxOwnerShip)/100);
            filteredData.setMinInterest(minInterest);
            filteredData.setMinTotalYaz(minTotalYaz);
            filteredData.setMaxOpenLoans(maxOpenLoans);
            InvestmentLoansTableView.setItems(getLoans(loans));
        }
        catch(NumberFormatException e){
            FilterLoansLabel.setText("Please enter integers only!");
        }
        catch(Exception e){
            FilterLoansLabel.setText(e.getMessage());
        }
    }

    @FXML void SwitchToInformation(ActionEvent event){
        InvestmentLoansTableView.getItems().clear();
        if(InformationButton.getPadding().getTop()!=10){
            CustomerMenu.getChildren().get(1).setDisable(true);
            CustomerMenu.getChildren().get(1).setVisible(false);
            CustomerMenu.getChildren().get(2).setDisable(true);
            CustomerMenu.getChildren().get(2).setVisible(false);
            CustomerMenu.getChildren().get(3).setDisable(true);
            CustomerMenu.getChildren().get(3).setVisible(false);

            ScrambleButton.setPadding(new Insets(0,15,0,15));
            PaymentButton.setPadding(new Insets(0,15,0,15));
            InformationButton.setPadding(new Insets(10,15,0,15));
            TakeLoanButton.setPadding(new Insets(0,15,0,15));

            CustomerMenu.getChildren().get(0).setDisable(false);
            CustomerMenu.getChildren().get(0).setVisible(true);
        }
    }

    @FXML void SwitchToScramble(ActionEvent event){
        if(ScrambleButton.getPadding().getTop()!=10){
            CustomerMenu.getChildren().get(0).setDisable(true);
            CustomerMenu.getChildren().get(0).setVisible(false);
            CustomerMenu.getChildren().get(1).setDisable(true);
            CustomerMenu.getChildren().get(1).setVisible(false);
            CustomerMenu.getChildren().get(3).setDisable(true);
            CustomerMenu.getChildren().get(3).setVisible(false);

            InformationButton.setPadding(new Insets(0,15,0,15));
            PaymentButton.setPadding(new Insets(0,15,0,15));
            ScrambleButton.setPadding(new Insets(10,15,0,15));
            TakeLoanButton.setPadding(new Insets(0,15,0,15));

            CustomerMenu.getChildren().get(2).setDisable(false);
            CustomerMenu.getChildren().get(2).setVisible(true);

        }
    }

    @FXML void SwitchToPayment(ActionEvent event){
        InvestmentLoansTableView.getItems().clear();
        if(PaymentButton.getPadding().getTop()!=10){
            CustomerMenu.getChildren().get(0).setDisable(true);
            CustomerMenu.getChildren().get(0).setVisible(false);
            CustomerMenu.getChildren().get(2).setDisable(true);
            CustomerMenu.getChildren().get(2).setVisible(false);
            CustomerMenu.getChildren().get(3).setDisable(true);
            CustomerMenu.getChildren().get(3).setVisible(false);

            ScrambleButton.setPadding(new Insets(0,15,0,15));
            InformationButton.setPadding(new Insets(0,15,0,15));
            PaymentButton.setPadding(new Insets(10,15,0,15));
            TakeLoanButton.setPadding(new Insets(0,15,0,15));

            CustomerMenu.getChildren().get(1).setDisable(false);
            CustomerMenu.getChildren().get(1).setVisible(true);
        }
    }

    @FXML void SwitchToTakeLoan(ActionEvent event){
        if(TakeLoanButton.getPadding().getTop()!=10){
            CustomerMenu.getChildren().get(0).setDisable(true);
            CustomerMenu.getChildren().get(0).setVisible(false);
            CustomerMenu.getChildren().get(1).setDisable(true);
            CustomerMenu.getChildren().get(1).setVisible(false);
            CustomerMenu.getChildren().get(2).setDisable(true);
            CustomerMenu.getChildren().get(2).setVisible(false);

            ScrambleButton.setPadding(new Insets(0,15,0,15));
            InformationButton.setPadding(new Insets(0,15,0,15));
            PaymentButton.setPadding(new Insets(0,15,0,15));
            TakeLoanButton.setPadding(new Insets(10,15,0,15));

            CustomerMenu.getChildren().get(3).setDisable(false);
            CustomerMenu.getChildren().get(3).setVisible(true);
        }
    }

    void refreshPaymentScreen(){
        refreshActiveLoanTable();
    }

    void refreshInformationScreen(){
        refreshCategoriesLV();
        refreshInformationTables();
        Optional<Customer> currCustomer = bank.getCustomers().stream().filter(cus->cus.equals(name)).findFirst();
        if(currCustomer.isPresent())
            CurrentBalanceLabel.setText("Current Balance: "+currCustomer.get().getBalance());
        else{
            System.out.println("curr customer is not present");
        }
    }

    @FXML void PaymentAllDebtButton(ActionEvent event) {
        if(ActiveLoansTV.getSelectionModel().getSelectedItems().isEmpty()) return;
        try{
            Loan loan = ActiveLoansTV.getSelectionModel().getSelectedItem();
            Customer customer = bank.getCustomers().stream().filter(cus->cus.getName().equals(name)).findFirst().get();
            if(customer.getBalance()<loan.getDebts().stream().mapToInt(Debt::getAmount).sum()) throw new Exception("Not enough money!");
            if(loan.getDebts().stream().mapToInt(Debt::getAmount).sum()<=0)throw new Exception("No pending debts in selected loan.");
            sendLoanDebtPaymentRequest(name,loan.getId(),(int)customer.getBalance(),Error_Label);
            getBank();
            //customer.fullyPayLoanDebt(bank.getYaz(),loan);
            //PaymentErrLabel.setText("Payment Successful!");
            //refreshPaymentScreen();
        }catch(Exception e){Error_Label.setText(e.getMessage());}
    }

    @FXML void PaymentAllLoanButton(ActionEvent event) {
        if(ActiveLoansTV.getSelectionModel().getSelectedItems().isEmpty()) return;
        try{
            Loan loan = ActiveLoansTV.getSelectionModel().getSelectedItem();
            sendFullyPayLoanRequest(name,loan.getId(),Error_Label);
            getBank();
        }catch(Exception e){Error_Label.setText(e.getMessage());}
    }

    @FXML void PaymentSpecificButton(ActionEvent event) {
        if(ActiveLoansTV.getSelectionModel().getSelectedItems().isEmpty()) {System.out.println("nothing is selected"); return;}
        try{
            Loan loan = ActiveLoansTV.getSelectionModel().getSelectedItem();
            String loanId = loan.getId();
            int amount = Math.min(Integer.parseInt(PaymentSpecificTA.getText()),loan.getDebts().stream().mapToInt(Debt::getAmount).sum());
            if(amount <= 0) throw new NumberFormatException("Payment is not higher then 0 or the loan has no pending payments.");
            sendLoanDebtPaymentRequest(name,loanId,amount,Error_Label);
            getBank();
        }catch(NumberFormatException e){Error_Label.setText(e.getMessage());}
        catch(Exception e){addErrorMessage(Error_Label,"Something went wrong: "+e.getMessage());}
    }

    @FXML void Charge(ActionEvent event) {
        try {
            int amount = Integer.parseInt(TransactionAmountTF.getText());
            if(amount<=0) throw new Exception("Charge amount has to be more then 0!");
            sendChargeRequest(name,amount,InformationErrLabel);
            getBank();
        }catch(Exception e){addErrorMessage(InformationErrLabel,e.getMessage());}
    }

    @FXML void Withdraw(ActionEvent event){
        try{
            int amount = Integer.parseInt(TransactionAmountTF.getText());
            Customer customer = bank.getCustomers().stream().filter(cus -> cus.getName().equals(name)).findFirst().get();
            if(customer.getBalance()>=amount) {
                scene.ControllerHelper.sendWithdrawRequest(name, amount, InformationErrLabel);
                getBank();
            }
            else throw new Exception("Not enough money!");
        }catch(IOException ignored){}
        catch(Exception e){addErrorMessage(InformationErrLabel,e.getMessage());}
    }

    void loadXMLFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files","*.xml"));
        File f=fc.showOpenDialog(null);
        if(f!=null) {
            try {
                Bank bank=fileLoader.load(f.getAbsolutePath());
                addLoansFromFileRequest(bank,name,AddLoansErrLabel);
                getBank();
            } catch (Exception e) {addErrorMessage(AddLoansErrLabel,"Loading loans has failed: "+e.getMessage());}
        }
    }

    public void initialize(URL url, ResourceBundle rb){
        getBank();
        startBankRefresher();
        initTableViews();
        CategoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void initTableViews(){
        initAdminTableView();
        initCustomersTableView();
        initBuyAndSellTableViews();
        InvestmentLoansTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void initBuyAndSellTableViews(){
        SellLoansIdC.setCellValueFactory(new PropertyValueFactory<Loan,String>("id"));
        SellLoansEndInYazC.setCellValueFactory(loan -> new SimpleIntegerProperty(loan.getValue().getYazStarted()+loan.getValue().getTotalTime()).asObject());
        SellLoansPaymentSpacingC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("yazBetweenPayments"));
        SellLoansInterestC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("interest"));
        SellLoansPaymentLeftC.setCellValueFactory(loan -> new SimpleIntegerProperty(Math.round(loan.getValue().getCapital()-loan.getValue().getCurrBalance())).asObject());


        BuyLoansIdC.setCellValueFactory(loanForSale -> new SimpleStringProperty(loanForSale.getValue().getLoan().getId()));
        BuyLoansEndInYazC.setCellValueFactory(loanForSale-> new SimpleIntegerProperty(loanForSale.getValue().getLoan().getYazStarted()+loanForSale.getValue().getLoan().getTotalTime()).asObject());
        BuyLoansOwnershipC.setCellValueFactory(loanForSale -> new SimpleIntegerProperty(loanForSale.getValue().getOwnership()).asObject());
        BuyLoansPaymentSpacingC.setCellValueFactory(loanForSale -> new SimpleIntegerProperty(loanForSale.getValue().getLoan().getYazBetweenPayments()).asObject());
        BuyLoansInterestC.setCellValueFactory(loanForSale -> new SimpleIntegerProperty(loanForSale.getValue().getLoan().getInterest()).asObject());
        BuyLoansPaymentLeftC.setCellValueFactory(loanForSale-> new SimpleIntegerProperty(Math.round(loanForSale.getValue().getLoan().getCapital()-loanForSale.getValue().getLoan().getCurrBalance())).asObject());
    }


    public void initCustomersTableView(){
        //Transfer tableView
        TransferYazC.setCellValueFactory(new PropertyValueFactory<TransactionHistory,Integer>("time"));
        TransferAmountC.setCellValueFactory(new PropertyValueFactory<TransactionHistory,Integer>("transferAmount"));
        TransferBeforeC.setCellValueFactory(new PropertyValueFactory<TransactionHistory,Integer>("moneyBefore"));
        TransferAfterC.setCellValueFactory(new PropertyValueFactory<TransactionHistory,Integer>("moneyAfter"));
        //Customer loans tableView
        CustomerLoanIDC.setCellValueFactory(new PropertyValueFactory<Loan,String>("id"));
        CustomerLoanCategoryC.setCellValueFactory(new PropertyValueFactory<Loan,String>("category"));
        CustomerLoanInterestC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("interest"));
        CustomerLoanStatusC.setCellValueFactory(new PropertyValueFactory<Loan,String>("status"));
        CustomerLoanCapitalC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("capital"));
        CustomerLoanTimingC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("yazBetweenPayments"));
        //Customer lends tableView
        CustomerLendIDC.setCellValueFactory(new PropertyValueFactory<Loan,String>("id"));
        CustomerLendCategoryC.setCellValueFactory(new PropertyValueFactory<Loan,String>("category"));
        CustomerLendInterestC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("interest"));
        CustomerLendStatusC.setCellValueFactory(new PropertyValueFactory<Loan,String>("status"));
        CustomerLendCapitalC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("capital"));
        CustomerLendTimingC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("yazBetweenPayments"));
        //Active loans tableView
        ActiveLoanIDC.setCellValueFactory(new PropertyValueFactory<Loan,String>("id"));
        ActiveLoanCategoryC.setCellValueFactory(new PropertyValueFactory<Loan,String>("category"));
        ActiveLoanInterestC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("interest"));
        ActiveLoanStatusC.setCellValueFactory(new PropertyValueFactory<Loan,String>("status"));
        ActiveLoanPaymentLeftC.setCellValueFactory(obj -> new SimpleIntegerProperty(obj.getValue().getTotalPayment()-obj.getValue().getCurrBalance()).asObject());
        ActiveLoanNextPayment.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getNextYazPayment()-bank.getYaz()==0? "TODAY":String.valueOf(obj.getValue().getNextYazPayment()-bank.getYaz())));
        ActiveLoanTimingC.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("yazBetweenPayments"));
    }

    public void refreshActiveLoanTable(){
        int loanIndex = ActiveLoansTV.getSelectionModel().getSelectedIndex();
        //Loan loan = ActiveLoansTV.getSelectionModel().getSelectedItem();
        ActiveLoansTV.getItems().clear();
        NotificationsTA.clear();

        Customer customer = bank.getCustomers().stream().filter(obj->obj.getName().equals(name)).findFirst().get();
        NotificationsTA.setText(customer.getNotifications());
        ActiveLoansTV.setItems(getLoans(customer.getLoans().stream().filter(obj -> obj.getStatus().equals("active")||obj.getStatus().equals("risk")).collect(Collectors.toList())));
        try {
            ActiveLoansTV.getSelectionModel().select(loanIndex);
            //ActiveLoansTV.getSelectionModel().select(loan);
        }catch(Exception ignored){}

        int sellLoansIndex = SellLoansTV.getSelectionModel().getSelectedIndex();
        int buyLoansIndex = BuyLoansTV.getSelectionModel().getSelectedIndex();
        try {
            SellLoansTV.setItems(FXCollections.observableList(bank.findCustomer(name).getShareHolder().keySet().stream().filter(loan -> bank.getLoansForSale().stream().noneMatch(loanForSale -> loanForSale.equals(new LoanForSale(customer,loan)))).collect(Collectors.toList())));
            SellLoansTV.sort();
            BuyLoansTV.setItems(FXCollections.observableList(bank.getLoansForSale().stream().filter(loanForSale -> !loanForSale.getSeller().getName().equals(name) && loanForSale.getLoan().getOwner().getName()!=name).collect(Collectors.toList())));
        SellLoansTV.getSelectionModel().select(sellLoansIndex);
        BuyLoansTV.getSelectionModel().select(buyLoansIndex);

        }catch(Exception ignored){}
    }

    public void refreshInformationTables() {
        Customer customer = bank.getCustomers().stream().filter(obj -> obj.getName().equals(name)).findFirst().get();
        Loan loan = CustomerLoansTV.getSelectionModel().getSelectedItem();
        int LoanedLoans = CustomerLoansTV.getSelectionModel().getSelectedIndex();
        Loan lend = CustomerLendsTV.getSelectionModel().getSelectedItem();
        int LendedLoans=CustomerLendsTV.getSelectionModel().getSelectedIndex();
        TransactionHistory transfer = TransferTV.getSelectionModel().getSelectedItem();
        int transferIndex = TransferTV.getSelectionModel().getSelectedIndex();
        CustomerLoansTV.getItems().clear();
        CustomerLendsTV.getItems().clear();
        TransferTV.getItems().clear();
        List<Loan> loans = new ArrayList<>(customer.getShareHolder().keySet());
        CustomerLendsTV.setItems(getLoans(loans));
        CustomerLendsTV.sort();
        CustomerLoansTV.setItems(getLoans(customer.getLoans()));
        CustomerLoansTV.sort();
        TransferTV.setItems(getHistory(customer.getHistory()));
        try {
            CustomerLoansTV.getSelectionModel().select(LoanedLoans);
            CustomerLendsTV.getSelectionModel().select(LendedLoans);
            TransferTV.getSelectionModel().select(transferIndex);
        }catch(Exception ignored){}
    }

    public void refreshCategoriesLV(){
        Set<String> categories = new HashSet<>(bank.getCategories());
        if(CategoriesListView.getSelectionModel().getSelectedItems().size()<categories.size())
            CategoriesListView.setItems(FXCollections.observableArrayList(categories));
    }

    public void initAdminTableView(){
        AdminIDC1.setCellValueFactory(new PropertyValueFactory<Loan,String>("id"));
        AdminCategoryC1.setCellValueFactory(new PropertyValueFactory<Loan,String>("category"));
        AdminInterestC1.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("interest"));
        AdminStatusC1.setCellValueFactory(new PropertyValueFactory<Loan,String>("status"));
        AdminLoanerC1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner().getName()));
        AdminTotalWorthC1.setCellValueFactory(new PropertyValueFactory<Loan,Integer>("capital"));
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

    @FXML void Invest(ActionEvent event){
        try{
            ArrayList<Loan> loans = new ArrayList<>(InvestmentLoansTableView.getSelectionModel().getSelectedItems());
            sendInvestInLoansRequest(loans,name,filteredData.getAmount(),filteredData.getMaxOwnerShip(),FilterLoansLabel);
            InvestmentLoansTableView.getItems().clear();
            getBank();
        }catch(Exception ignored){}
    }

    @FXML void InvestInAll(ActionEvent event) {
        try {
            ArrayList<Loan> loans = new ArrayList<>(InvestmentLoansTableView.getItems());
            sendInvestInLoansRequest(loans,name,filteredData.getAmount(),filteredData.getMaxOwnerShip(),FilterLoansLabel);
            InvestmentLoansTableView.getItems().clear();
        }catch(Exception ignored){}
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
                                disableButtons(bank.isRewind());
                                refreshPaymentScreen();
                                refreshInformationScreen();
                                refreshActiveLoanTable();
                            }
                            else
                                System.out.println("reponseBody is empty.");
                        }catch(Exception e){e.printStackTrace();}
                    });
                }
            }
        });
    }

    public void disableButtons(boolean disable){
        TakeLoanButton.setDisable(disable);
        ChargeButton.setDisable(disable);
        WithdrawButton.setDisable(disable);
        PaySpecificButton.setDisable(disable);
        PayAllDebtButton.setDisable(disable);
        PayLoanButton.setDisable(disable);
        FilterLoansButton.setDisable(disable);
        InvestButton.setDisable(disable);
        InvestInAllButton.setDisable(disable);
        MakeLoanRequestButton.setDisable(disable);
        AddLoansFromFileButton.setDisable(disable);
    }

    public void startBankRefresher() {
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {getBank();}},REFRESH_RATE,REFRESH_RATE);
    }
}
