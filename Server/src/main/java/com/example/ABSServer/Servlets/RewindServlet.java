package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.ServletUtils;
import bank.Bank;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.security.auth.callback.TextInputCallback;
import java.io.IOException;

public class RewindServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean rewindMode = Boolean.parseBoolean(req.getParameter("rewind"));

        if(rewindMode) {
            Bank bank = ServletUtils.getBank(getServletContext());
            bank.setRewind(rewindMode);
            ServletUtils.getRewindBank(getServletContext()).add(bank.getYaz(),Decoder.toString(bank));
        }
        else {
            try {
                Bank bank = (Bank) Decoder.fromString(ServletUtils.getRewindBank(getServletContext()).getLast());
                bank.setRewind(false);
                ServletUtils.setBank(bank,getServletContext());
            }catch(Exception e){
                System.out.println("Ending rewind mode has failed!!! "+e.getMessage());
                System.exit(1);
            }
        }
    }
}
