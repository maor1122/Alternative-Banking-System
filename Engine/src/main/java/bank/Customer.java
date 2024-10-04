package bank;

import Utils.StringJoiner;
import Utils.loanSorterByYazAndPayment;
import java.io.Serializable;
import java.util.*;

public class Customer implements Serializable {
    private String name;
    private double balance;
    private List<TransactionHistory> history;
    private List<Loan> loans;
    private Map<Loan,Integer> shareHolder;
    private StringJoiner notifications;

    public String getNotifications() {
        return notifications.toString();
    }

    public void addNotifications(String str){
        notifications.add(str);
    }

    public void addLoans(Loan loan){
        this.loans.add(loan);
    }

    public void addLoans(Collection<Loan> loans){ loans.forEach(this::addLoans);}

    public void investInLoans(final int startingAmount,double maxOwnerShipPercentage,List<Loan> loans,int yaz){
        int size = loans.size(),investment;
        int amount = startingAmount;
        // we sort loans by the amount of money each loan can take.
        if(maxOwnerShipPercentage!=0)
            loans.sort((Loan l1,Loan l2) -> Double.compare(Math.min(l1.getCapital()*maxOwnerShipPercentage,l1.getCapital()-l1.getCurrBalance()),Math.min(l2.getCapital()*maxOwnerShipPercentage,l2.getCapital()-l2.getCurrBalance())));
        else
            loans.sort((Loan l1,Loan l2) -> Integer.compare(l1.getCapital()-l1.getCurrBalance(),l2.getCapital()-l2.getCurrBalance()));
        while(!loans.isEmpty()){
            if(loans.get(0).getCapital()-loans.get(0).getCurrBalance()<=Double.min(amount/loans.size(),loans.get(0).getCapital()*maxOwnerShipPercentage)){
                investment = loans.get(0).getCapital()-loans.get(0).getCurrBalance();
                amount -=investment;
                shareHolder.put(loans.get(0),investment);
                transfer(-investment,yaz);
                if(!loans.get(0).getShareHolders().containsKey(this))
                    loans.get(0).getShareHolders().put(this,investment);
                else
                    loans.get(0).getShareHolders().put(this,investment+loans.get(0).getShareHolders().get(this));
                loans.get(0).setStatus("active");
                loans.get(0).setYazStarted(yaz);
                loans.get(0).setNextYazPayment(yaz+loans.get(0).getYazBetweenPayments());
                loans.get(0).getOwner().transfer(loans.get(0).getCapital(),yaz);
                loans.get(0).setCurrBalance(0);
            }
            else{
                investment = (int)Math.round(Double.min((double)amount/loans.size(),loans.get(0).getCapital()*maxOwnerShipPercentage));
                amount-=investment;
                transfer(-investment, yaz);
                loans.get(0).setStatus("pending");
                loans.get(0).setCurrBalance(loans.get(0).getCurrBalance() + investment);
                if (!loans.get(0).getShareHolders().containsKey(this)) {
                    shareHolder.put(loans.get(0), investment);
                    loans.get(0).getShareHolders().put(this, investment);
                }
                else{
                    shareHolder.put(loans.get(0), investment + shareHolder.get(loans.get(0)));
                    loans.get(0).getShareHolders().put(this, investment+loans.get(0).getShareHolders().get(this));
                }
            }
            loans.remove(0);
        }
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public Map<Loan,Integer> getShareHolder() {
        return shareHolder;
    }

    public void fullyPayLoanDebt(int yaz,Loan loan) throws Exception{
        int amount = loan.getDebts().stream().mapToInt(Debt::getAmount).sum();
        if(this.getBalance()<amount) throw new Exception("Not enough money!");
        payLoan(yaz,amount,loan);
    }

    public void fullyPayLoan(int yaz,Loan loan) throws Exception{
        fullyPayLoanDebt(yaz,loan);
        int amount = loan.getTotalPayment()-loan.getCurrBalance();
        transfer(-amount,yaz);
        loan.getPayments().add(new TransactionHistory(loan.getCurrBalance(),amount,yaz));
        loan.setCurrBalance(loan.getTotalPayment()-loan.getCurrBalance()+loan.getCurrBalance());
        loan.finish(yaz);
    }

    public void payLoan(int yaz,int amount,Loan loan) throws Exception {
        if(amount>loan.getDebts().stream().mapToInt(Debt::getAmount).sum()){
            throw new Exception("amount");
        }
        else{
            int totalDebt = loan.getDebts().stream().mapToInt(Debt::getAmount).sum();
            if(amount==totalDebt){
                transfer(-totalDebt,yaz);
                loan.getPayments().add(new TransactionHistory(loan.getCurrBalance(),amount,yaz));
                loan.setCurrBalance(totalDebt+loan.getCurrBalance());
                amount-=totalDebt;
                loan.removeAllDept();
                if(loan.getYazStarted()+loan.getTotalTime()>yaz)
                    loan.setStatus("active");
                else
                    loan.finish(yaz);
            }
            else{
                transfer(-amount,yaz);
                loan.getPayments().add(new TransactionHistory(loan.getCurrBalance(),amount,yaz));
                for(Debt debt: loan.getDebts()){
                    if(amount==0) break;
                    if(debt.getAmount()<=amount){
                        amount-=debt.getAmount();
                        loan.getDebts().remove(debt);
                    }
                    else{
                        debt.setAmount(debt.getAmount()-amount);
                        break;
                    }
                }
            }
        }
    }

    public void updateLoans(int yaz) {
        this.getLoans().sort(new loanSorterByYazAndPayment());
        for (Loan loan : this.getLoans()) {
            int amount = Math.round(loan.getTotalPayment() / loan.getTotalTime() * loan.getYazBetweenPayments());
            if ((loan.getStatus().equals("active") ||loan.getStatus().equals("risk"))&&loan.getTotalTime()+loan.getYazStarted()>=yaz) {
                if (loan.getNextYazPayment() == yaz) {
                    this.addNotifications("Time: "+yaz+", new loan payment awaits, payment: "+amount+", loan ID: "+loan.getId()+'.');
                    loan.addDebt(yaz, amount);
                }
            }
                /*if(loan.getStatus().equals("risk")){
                    for(int debt:loan.getDebts()){
                        if(this.balance>=amount){
                            //transfer(-amount,yaz);
                            //loan.payLoan(yaz);
                            //loan.removeDebt(yaz);
                        }
                    }
                    if(loan.getNextYazPayment()==yaz){
                        if (this.balance >= amount) {
                            transfer(-amount,yaz);
                            loan.payLoan(yaz);
                        }
                        else{
                            loan.addDebt(yaz);
                        }
                    }
                if(loan.getDebts().size()==0)
                    loan.setStatus("active");
                    if(loan.getYazStarted()+loan.getTotalTime()<=yaz)
                        loan.setStatus("finished");
                }
                else if(loan.getStatus().equals("active") && loan.getNextYazPayment()==yaz) {
                    if (this.balance >= amount) {
                        transfer(-amount,yaz);
                        loan.payLoan(yaz);
                        if(loan.getYazStarted()+loan.getTotalTime()<=yaz)
                            loan.finish(yaz);
                        }
                        else{
                            loan.setStatus("risk");
                            loan.addDebt(yaz);
                        }
                    }
                if(loan.getNextYazPayment()==yaz)
                    loan.setNextYazPayment(yaz+loan.getYazBetweenPayments());
                }*/
        }
    }

    public void transfer(int amount,int yaz) {
        if (amount != 0) {
            this.history.add(new TransactionHistory(this.balance, amount, yaz));
            this.balance += amount;
        }
    }

    public List<TransactionHistory> getHistory() {
        return history;
    }

    public Customer(String name, int balance){
        this.name=name;
        this.balance=balance;
        this.history=new ArrayList<>();
        this.loans=new ArrayList<>();
        this.shareHolder=new HashMap<>();
        this.notifications = new StringJoiner("\n");
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Customer o){
        if(o instanceof Customer)
            return o.getName().equals(this.getName());
        return false;
    }

    public boolean equals(String s){
        return this.getName().equals(s);
    }
}
