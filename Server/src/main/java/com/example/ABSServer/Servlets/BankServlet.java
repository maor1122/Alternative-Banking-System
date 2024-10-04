package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



import java.io.IOException;
import java.io.PrintWriter;

public class BankServlet extends HttpServlet{
    public BankServlet() {
        super();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            Bank bank = ServletUtils.getBank(getServletContext());
            if(bank==null)
                System.out.println("error bank is null(?)!");
            String codedBank = Decoder.toString(bank);
            out.print(codedBank);
            out.flush();
        }
    }
}
