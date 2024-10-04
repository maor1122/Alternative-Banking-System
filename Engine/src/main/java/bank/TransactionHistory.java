package bank;

import java.io.Serializable;

public class TransactionHistory implements Serializable {
    int time;
    int moneyBefore;
    int moneyAfter;
    int transferAmount;

    public TransactionHistory(double balance,double amount,int yaz){
        this.time= yaz;
        this.moneyBefore = (int)balance;
        this.moneyAfter = (int)(balance+amount);
        this.transferAmount=(int)amount;
    }

    public double getMoneyAfter() {
        return moneyAfter;
    }

    public double getMoneyBefore() {
        return moneyBefore;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public int getTime() {
        return time;
    }
}
