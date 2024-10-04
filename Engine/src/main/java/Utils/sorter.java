package Utils;

import bank.Loan;

import java.util.Comparator;

public class sorter implements Comparator<Loan> {
    public int compare(Loan loan1,Loan loan2){
        double res = loan1.getCapital()-loan1.getCurrBalance() -loan2.getCapital()+loan2.getCurrBalance();
        if(res>=0)
            return (int)res;
        else
            return (int)res-1;
    }
}
