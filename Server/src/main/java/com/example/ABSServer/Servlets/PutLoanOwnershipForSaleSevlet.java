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

public class PutLoanOwnershipForSaleSevlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        synchronized (this){
            final String loanId = req.getParameter("loan").trim();
            String sellerName = req.getParameter("seller").trim();

            Bank bank = ServletUtils.getBank(getServletContext());
            try {
                Customer customer = bank.findCustomer(sellerName);
                Optional<Loan> loan = bank.getLoans().stream().filter(loan1 -> loan1.getId().equals(loanId)).findFirst();
                if(!loan.isPresent()){
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getOutputStream().print("Loan doesn't exist!");
                    return;
                }
                bank.addLoanForSale(customer,loan.get());
                resp.setStatus(HttpServletResponse.SC_OK);
            }catch(Exception e ){resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);}
        }
    }
}
