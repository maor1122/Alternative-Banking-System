package Utils;

import bank.Loan;

import java.util.Comparator;

public class loanSorterByYazAndPayment implements Comparator<Loan> {
    public int compare(Loan loan1,Loan loan2) {
        int res = loan1.getYazStarted()-loan2.getYazStarted();
        if(res!=0)
            return res;
        else
            return loan1.getCapital()/loan1.getYazBetweenPayments() - loan2.getCapital()/loan2.getYazBetweenPayments();
    }
}
