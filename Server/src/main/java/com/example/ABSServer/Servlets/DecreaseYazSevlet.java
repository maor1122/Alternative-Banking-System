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
import java.util.LinkedList;

public class DecreaseYazSevlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LinkedList<String> stringLinkedList =ServletUtils.getRewindBank(getServletContext());
        int newYaz = ServletUtils.getBank(getServletContext()).getYaz()-1;
        if(newYaz<0) {
            resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            resp.getOutputStream().print("Yaz is less then or equal to 0!");
        }else{
            try{
                int currBankYaz = ((Bank) Decoder.fromString(stringLinkedList.getLast())).getYaz();
                String bankString = stringLinkedList.get(newYaz);
                Bank bank = (Bank)Decoder.fromString(bankString);
                ServletUtils.setBank(bank,getServletContext());
            }catch(Exception e){resp.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED); resp.getOutputStream().print(e.getMessage());}
        }
    }
}
