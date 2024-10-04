package com.example.ABSServer.Servlets;

import com.example.ABSServer.Utils.ServletUtils;
import com.example.ABSServer.Utils.SessionUtils;
import bank.Bank;
import users.UserManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

import static com.example.ABSServer.Utils.Constants.STATE;
import static com.example.ABSServer.Utils.Constants.USERNAME;

public class LightweightLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        Bank bank = ServletUtils.getBank(getServletContext());
        bank.getYaz();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter(USERNAME);
            String stateFromParameter = request.getParameter(STATE);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter - not standard situation. it's a conflict
                response.getWriter().println("conflict - 409");
                // stands for conflict in server state
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                stateFromParameter = stateFromParameter.trim();
                /*
                One can ask why not enclose all the synchronizations inside the userManager object ?
                Well, the atomic action we need to perform here includes both the question (isUserExists) and (potentially) the insertion
                of a new user (addUser). These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough.
                (of course there are other more sophisticated and performable means for that (atomic objects etc) but these are not in our scope)

                The synchronized is on this instance (the servlet).
                As the servlet is singleton - it is promised that all threads will be synchronized on the very same instance (crucial here)

                A better code would be to perform only as little and as necessary things we need here inside the synchronized block and avoid
                do here other not related actions (such as response setup. this is shown here in that manner just to stress this issue
                 */
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else if(userManager.isAdminLoggedIn() && stateFromParameter.equals("admin")){
                        String errorMessage = "There can only be 1 admin at a time, admin already registered.";
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }else if(!stateFromParameter.equals("admin") && !stateFromParameter.equals("customer")) {
                        String errorMessage = "Invalid state";
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }

                    else {
                        //add the new user to the users list
                        userManager.addUser(usernameFromParameter);
                        response.getWriter().println("User logged in successfully");
                        response.getWriter().println("user state:"+stateFromParameter);
                        if(Objects.equals(stateFromParameter, "customer")) {
                            ServletUtils.getBank(getServletContext()).addCustomer(usernameFromParameter);
                            System.out.println("Adding customer: "+usernameFromParameter);
                            response.getWriter().println("bank.customers current size: "+ ServletUtils.getBank(getServletContext()).getCustomers().size());
                        }
                        if(stateFromParameter.equals("admin"))
                            userManager.setAdminLoggedIn(true);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        } else {
            //user is already logged in
            response.getWriter().println("user already logged in");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

}