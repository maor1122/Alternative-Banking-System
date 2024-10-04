package bank;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Bank implements Serializable {
    private int yaz=0;
    private List<Customer> customers;
    private List<String> categories;
    private List<Loan> loans;
    private List<LoanForSale> loansForSale;
    private boolean rewind;

    public List<LoanForSale> getLoansForSale() {
        return loansForSale;
    }

    public List<Loan> getLoans() {
        return loans;
    }
    public List<Loan> filterLoans(List<String> categories,double minInterest,int minTotalTime,int maxOpenLoans,Customer customer){
        List<Loan> filteredLoans = loans.stream().filter(element -> element.getTotalTime()>=minTotalTime&&(element.getStatus().equals("new")||element.getStatus().equals("pending"))
        &&element.getInterest()>=minInterest&&categories.contains(element.getCategory())&&!element.getOwner().equals(customer.getName())
        &&(maxOpenLoans==0 ||element.getOwner().getLoans().stream().filter(elem -> elem.getStatus()!="finished").toArray().length<=maxOpenLoans)
        ).collect(Collectors.toList());
        return filteredLoans;
    }

    public Bank(){
        this.yaz=0;
        this.customers = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.rewind = false;
        this.loansForSale = new ArrayList<>();
    }

    public void addLoanForSale(Customer customer,Loan loan){
        if(loansForSale.stream().noneMatch(ths -> ths.getLoan().getId().equals(loan.getId()) && ths.getSeller().equals(customer)))
            this.loansForSale.add(new LoanForSale(customer,loan));
    }

    public void sellLoan(Customer buyer,LoanForSale loanForSale){
        loanForSale.getLoan().getShareHolders().put(buyer,loanForSale.getLoan().getShareHolders().get(loanForSale.getSeller()));
        loanForSale.getLoan().getShareHolders().remove(loanForSale.getSeller());
        if(buyer.getShareHolder().containsKey(loanForSale.getLoan()))
            buyer.getShareHolder().put(loanForSale.getLoan(),buyer.getShareHolder().get(loanForSale.getLoan())+loanForSale.getSeller().getShareHolder().get(loanForSale.getLoan()));
        else
            buyer.getShareHolder().put(loanForSale.getLoan(),loanForSale.getSeller().getShareHolder().get(loanForSale.getLoan()));
        loanForSale.getSeller().getShareHolder().remove(loanForSale.getLoan());
        loanForSale.getSeller().addNotifications("yaz: "+this.getYaz()+". Sold Loan Ownership! loan id: "+loanForSale.getLoan().getId());
        this.loansForSale.remove(loanForSale);
    }

    public boolean isRewind() {
        return rewind;
    }

    public void setRewind(boolean rewind) {
        this.rewind = rewind;
    }

    public Customer findCustomer(String name) throws Exception{
        return this.getCustomers().stream().filter(customer->customer.equals(name)).findFirst().get();
    }

    int[] a= {2,4,5};

    public synchronized void addCustomer(String name){this.customers.add(new Customer(name,0));}

    public synchronized void addCustomer(Customer customer){this.customers.add(customer);}


    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    public synchronized int getYaz() {
        return yaz;
    }

    public void setYaz(int yaz) {
        this.yaz = yaz;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public synchronized  List<Customer> getCustomers() {
        return customers;
    }

    public synchronized List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    public void passTime(){
        loans.stream().filter(obj -> obj.getStatus()=="active"||obj.getStatus()=="risk").collect(Collectors.toList()).forEach(obj -> obj.update(yaz));
        this.yaz++;
        for(Customer customer:customers){
            customer.updateLoans(this.yaz);
        }
        this.loansForSale.removeIf(loanForSale -> !loanForSale.getLoan().getStatus().equals("active"));
    }

    public List<Loan> getLoansReady(){
        return loans.stream().filter(obj-> obj.getStatus().equals("active")).filter(obj-> obj.getNextYazPayment()==yaz).collect(Collectors.toList());
    }
}
