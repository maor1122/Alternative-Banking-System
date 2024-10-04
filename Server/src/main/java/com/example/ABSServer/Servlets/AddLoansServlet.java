package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import bank.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AddLoansServlet extends HttpServlet {
    public AddLoansServlet() {
        super(); // No-argument constructor
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ownerName = req.getParameter("owner");
        String codedBank = req.getParameter("bank");
        synchronized (this) {
            if (ownerName == null || codedBank == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getOutputStream().print("One of the fields was empty.");
            } else {
                try {
                    Bank bankLoans = (Bank) Decoder.fromString(codedBank);
                    Bank bank = ServletUtils.getBank(getServletContext());
                    List<String> commonList1 = bankLoans.getLoans().stream().map(loan -> loan.getId().trim())
                            .collect(Collectors.toList());
                    List<String> commonList2 =bank.getLoans().stream().map(loan -> loan.getId().trim()).collect(Collectors.toList());
                            commonList1.retainAll(commonList2);
                    if(!commonList1.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_CONFLICT);
                        resp.getOutputStream().print("Loan id has to be unique!");
                        return;
                    }
                    Customer owner = ServletUtils.getBank(getServletContext()).getCustomers().stream().filter(cus -> cus.equals(ownerName)).findFirst().get();
                    bankLoans.getLoans().forEach(loan -> loan.setOwner(owner));
                    owner.addLoans(bankLoans.getLoans());
                    ServletUtils.getBank(getServletContext()).getLoans().addAll(bankLoans.getLoans());
                    ServletUtils.getBank(getServletContext()).getCategories().addAll(bankLoans.getCategories());
                    resp.setStatus(HttpServletResponse.SC_OK);
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
                    resp.getOutputStream().print(e.getMessage());
                }
            }
        }
    }
}
