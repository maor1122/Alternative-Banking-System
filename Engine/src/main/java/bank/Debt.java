package bank;

import java.io.Serializable;

public class Debt implements Comparable<Debt>, Serializable {
    private int amount;
    private final int yaz;

    public Debt(int amount,int yaz){
        this.yaz = yaz;
        this.amount = amount;
    }
    public int getYaz() {
        return yaz;
    }
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Debt other){
        return Integer.compare(this.getYaz(),other.getYaz());
    }
}
