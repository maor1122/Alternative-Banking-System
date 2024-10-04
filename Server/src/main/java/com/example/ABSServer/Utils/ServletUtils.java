package com.example.ABSServer.Utils;

import bank.Bank;
import users.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedList;

import static com.example.ABSServer.Utils.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String BANK_ATTRIBUTE_NAME = "bankManager";
	private static final String REWIND_BANK_ATTRIBUTE_NAME = "rewindBank";

	private static final Object userManagerLock = new Object();
	private static final Object bankLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext){
		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null)
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			if(servletContext.getAttribute(BANK_ATTRIBUTE_NAME) ==null)
				servletContext.setAttribute(BANK_ATTRIBUTE_NAME,new Bank());
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static Bank getBank(ServletContext servletContext){
		synchronized (bankLock) {
			if (servletContext.getAttribute(BANK_ATTRIBUTE_NAME) == null) {
				System.out.println("Bank requested but doesn't exists!");
				Bank bank= new Bank();
				servletContext.setAttribute(BANK_ATTRIBUTE_NAME,bank);
				return bank;
			}
			else
				return (Bank)servletContext.getAttribute(BANK_ATTRIBUTE_NAME);
		}
	}

	public static LinkedList<String> getRewindBank(ServletContext servletContext){
		if(servletContext.getAttribute(REWIND_BANK_ATTRIBUTE_NAME) == null){
			LinkedList<String> llBank = new LinkedList<>();
			servletContext.setAttribute(REWIND_BANK_ATTRIBUTE_NAME,llBank);
		}
		return (LinkedList<String>) servletContext.getAttribute(REWIND_BANK_ATTRIBUTE_NAME);
	}

	public static void setBank(Bank bank,ServletContext servletContext){
		servletContext.setAttribute(BANK_ATTRIBUTE_NAME,bank);
	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}
}
