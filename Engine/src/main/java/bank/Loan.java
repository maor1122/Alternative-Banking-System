package bank;

import java.io.Serializable;
import java.util.*;

public class Loan implements Serializable {
    private String id;
    private Customer owner;
    private Map<Customer,Integer> shareHolders;
    private String category;
    private int capital;
    private int totalTime;
    private int yazBetweenPayments;
    private int interest;
    private String status;
    private int yazStarted;
    private int totalPayment;
    private int currBalance;
    private int nextYazPayment;
    private PriorityQueue<Debt> debts;
    private List<TransactionHistory> payments;
    private int yazFinished;

    public List<TransactionHistory> getPayments() {
        return payments;
    }

    public int getYazFinished() {
        return yazFinished;
    }

    public PriorityQueue<Debt> getDebts() {
        return debts;
    }

    public int getNextYazPayment() {
        return nextYazPayment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<Customer, Integer> getShareHolders() {
        return shareHolders;
    }

    public int getYazStarted() {
        return yazStarted;
    }

    public Loan(String id, Customer owner, String category, int capital, int totalTime, int yazBetweenPayments, int interest, int yazStarted){
        this.id= id;
        this.owner=owner;
        this.category= category;
        this.capital=capital;
        this.totalTime=totalTime;
        this.yazBetweenPayments=yazBetweenPayments;
        this.interest=interest;
        this.yazStarted=yazStarted;
        this.status="new";
        this.totalPayment = (int)((double)capital * ((double)interest/100) +(double)capital);
        this.shareHolders=new HashMap<>();
        this.currBalance= 0;
        this.nextYazPayment=0;
        this.debts = new PriorityQueue<>();
        this.payments = new ArrayList<>();
        this.yazFinished = -1;
    }

    public void finish(int yaz){
        setStatus("finished");
        setYazFinished(yaz);
        int money = capital;
        for(Customer investor:shareHolders.keySet()){
            investor.transfer(Math.round(shareHolders.get(investor)/money*totalPayment),yaz);
        }
    }

    public void setYazFinished(int yazFinished) {
        this.yazFinished = yazFinished;
    }

    public void payLoan(int yaz,int amount){
        payments.add(new TransactionHistory(currBalance,amount,yaz));
        currBalance=amount+currBalance;
        for(Debt debt:debts){
            if(amount>=debt.getAmount()){
                amount-=debt.getAmount();
                debts.remove(debt);
            }
            else{
                debt.setAmount(debt.getAmount()-amount);
            }
        }
        if(debts.isEmpty())
            this.setStatus("active");
        if(this.getStatus()=="active"){
            if(getYazStarted()+getTotalTime()<=yaz){
                finish(yaz);
            }
        }
    }

    public void addDebt(int yaz,int amount){
        debts.add(new Debt(amount,yaz));
    }
    public void removeDebt(){
        debts.remove();
    }
    public void removeAllDept(){debts.clear();}

    public void setYazStarted(int yazStarted) {
        this.yazStarted = yazStarted;
    }

    public void setNextYazPayment(int nextYazPayment) {
        this.nextYazPayment = nextYazPayment;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public String getStatus() {
        return status;
    }

    public void setCurrBalance(int currBalance) {
        this.currBalance = currBalance;
    }

    public int getCurrBalance() {
        return currBalance;
    }

    public void update(int yaz) {
        if(this.getNextYazPayment()==yaz) {
            this.setNextYazPayment(this.getNextYazPayment() + this.yazBetweenPayments);
            if(!this.debts.isEmpty()) {
                this.getOwner().addNotifications("Time: " + yaz + ", Missed payment! loan ID: " + this.getId() + '.');
                this.setStatus("risk");
            }
        }
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    public int getInterest() {
        return interest;
    }

    public boolean equals(Loan other){
        return this.getId().equals(other.getId());
    }

    public void setInterest(int interest) {
        this.interest =interest;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getYazBetweenPayments() {
        return yazBetweenPayments;
    }

    public void setYazBetweenPayments(int yazBetweenPayments) {
        this.yazBetweenPayments = yazBetweenPayments;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
