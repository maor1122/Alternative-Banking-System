package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class IncreaseYazServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        synchronized (this){
            Bank bank = ServletUtils.getBank(getServletContext());
            bank.setRewind(true);
            String codedBank = Decoder.toString(bank);
            bank.setRewind(false);
            ServletUtils.getRewindBank(getServletContext()).add(bank.getYaz(),codedBank);
            bank.passTime();
        }
    }
}
