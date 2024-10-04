package bank;

import java.io.Serializable;
import java.util.Comparator;

public class LoanForSale implements Serializable {
    private Customer seller;
    private Loan loan;
    private int Ownership;

    public LoanForSale(Customer seller, Loan loan) {
        this.seller = seller;
        this.loan = loan;
    }

    public boolean equals(LoanForSale other){
        return this.loan.getId().equals(other.getLoan().getId()) && this.seller.getName().equals(other.getSeller().getName());
    }

    public int getOwnershipWorth(){
        int percentageOwnership=loan.getCapital()/loan.getShareHolders().get(seller);
        return percentageOwnership*Math.round(loan.getCapital()-loan.getCurrBalance());
    }


    public Customer getSeller() {
        return seller;
    }

    public void setSeller(Customer seller) {
        this.seller = seller;
    }

    public int getOwnership() {
        return Ownership;
    }

    public void setOwnership(int ownership) {
        Ownership = ownership;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
