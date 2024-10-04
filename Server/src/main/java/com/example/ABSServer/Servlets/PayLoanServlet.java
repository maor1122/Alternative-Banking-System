package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import bank.Customer;
import bank.Debt;
import bank.Loan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class PayLoanServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String loanId = req.getParameter("loan");
        synchronized (this) {
            try {
                Bank bank = ServletUtils.getBank(getServletContext());
                Optional<Loan> loan = bank.getLoans().stream().filter(ln -> ln.getId().trim().equals(loanId.trim())).findFirst();
                if(!loan.isPresent()) {
                    resp.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
                    resp.getOutputStream().print("Loan doesn't exist!");
                    return;
                }
                Customer customer = bank.findCustomer(name);
                int debtAmount = loan.get().getDebts().stream().mapToInt(Debt::getAmount).sum();
                int futurePaymentAmount = loan.get().getTotalPayment()-loan.get().getCurrBalance()+debtAmount;
                if (customer.getBalance() >= futurePaymentAmount) {
                    customer.fullyPayLoan(bank.getYaz(), loan.get());
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getOutputStream().print("Not enough money!");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getOutputStream().print("something has changed, please try again.");
            }
        }
    }
}
