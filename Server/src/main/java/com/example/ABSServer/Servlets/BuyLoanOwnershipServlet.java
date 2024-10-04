package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import bank.Customer;
import bank.Loan;
import bank.LoanForSale;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class BuyLoanOwnershipServlet extends HttpServlet {
    public BuyLoanOwnershipServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        synchronized (this) {
            final String loanId = req.getParameter("loan").trim();
            String sellerName = req.getParameter("seller");
            String buyerName = req.getParameter("buyer");
            if (loanId.length()==0 || sellerName == null || buyerName == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                sellerName = sellerName.trim();
                buyerName = buyerName.trim();
                Bank bank = ServletUtils.getBank(getServletContext());
                try {
                    Optional<Loan> loan = bank.getLoans().stream().filter(loan1 -> loan1.getId().equals(loanId)).findFirst();
                    if(!loan.isPresent()){
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getOutputStream().print("Loan doesn't exist!");
                        return;
                    }
                    Customer buyer = bank.findCustomer(buyerName);
                    Customer seller = bank.findCustomer(sellerName);
                    Optional<LoanForSale> loanForSale = bank.getLoansForSale().stream().filter(loanForSale1 -> loanForSale1.getSeller() == seller && loanForSale1.getLoan() == loan.get()).findFirst();
                    if(!loanForSale.isPresent()){
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getOutputStream().print("loan isn't for sale!");
                        return;
                    }
                    int amount = loanForSale.get().getOwnership();
                    if(buyer.getBalance()<amount){
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getOutputStream().print("Not enough money!");
                    }else{
                        buyer.transfer(-amount,bank.getYaz());
                        seller.transfer(amount, bank.getYaz());
                        bank.sellLoan(buyer,loanForSale.get());
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
        }
    }
}
