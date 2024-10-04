package scene;

public class FilteredData {
    private int amount;
    private int minInterest;
    private int minTotalYaz;
    private double maxOwnerShip;
    private int maxOpenLoans;

    public int getMaxOpenLoans() {
        return maxOpenLoans;
    }

    public void setMaxOpenLoans(int maxOpenLoans) {
        this.maxOpenLoans = maxOpenLoans;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMinInterest() {
        return minInterest;
    }

    public void setMinInterest(int minInterest) {
        this.minInterest = minInterest;
    }

    public int getMinTotalYaz() {
        return minTotalYaz;
    }

    public void setMinTotalYaz(int minTotalYaz) {
        this.minTotalYaz = minTotalYaz;
    }

    public double getMaxOwnerShip() {
        return maxOwnerShip;
    }

    public void setMaxOwnerShip(double maxOwnerShip) {
        this.maxOwnerShip = maxOwnerShip;
    }
}
