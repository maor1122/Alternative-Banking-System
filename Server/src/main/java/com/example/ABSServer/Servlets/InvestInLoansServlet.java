package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import bank.Customer;
import bank.Loan;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InvestInLoansServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String strAmount = req.getParameter("amount");
        String codedLoans = req.getParameter("loans");
        String customerName = req.getParameter("customer");
        String strMaxOwnership = req.getParameter("maxOwnership");
        if(strAmount==null || codedLoans==null || strMaxOwnership==null || customerName == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getOutputStream().print("One of the fields is empty.");
        }
        else
            synchronized (this){
            try {
                Bank bank = ServletUtils.getBank(getServletContext());
                List<Loan> chosenLoans = (ArrayList<Loan>) Decoder.fromString(codedLoans);
                int amount = Integer.parseInt(strAmount);
                double maxOwnerShip = Double.parseDouble(strMaxOwnership);
                List<Loan> loans = new ArrayList<>();
                chosenLoans.forEach(loan->loans.add(bank.getLoans().stream().filter(ln->ln.equals(loan)).findFirst().get()));
                Customer customer = bank.findCustomer(customerName);
                customer.investInLoans(amount,maxOwnerShip,loans,bank.getYaz());
            }catch(Exception e){resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY); resp.getOutputStream().print(e.getMessage());}
        }
    }
}
