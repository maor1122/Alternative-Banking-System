package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.ServletUtils;
import bank.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class ChargeServlet extends HttpServlet {
    public ChargeServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nameFromParameter = req.getParameter("name");
        int amount = Integer.parseInt(req.getParameter("amount"));
        synchronized (this) {
            Optional<Customer> customer = ServletUtils.getBank(getServletContext()).getCustomers().stream().filter(cust -> cust.equals(nameFromParameter)).findFirst();
            if (!customer.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getOutputStream().print("The customer doesn't exist.");
            } else {
                customer.get().transfer(amount, ServletUtils.getBank(getServletContext()).getYaz());
                resp.getWriter().println("New balance: "+customer.get().getBalance());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }
}