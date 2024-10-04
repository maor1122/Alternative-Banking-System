package com.example.ABSServer.Utils;


public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;

    // fxml locations
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/login.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/abs";
    public final static String BANK = "/bank";
    public final static String WITHDRAW = "/withdraw";
    public final static String CHARGE = "/charge";
    public final static String ADDLOANS = "/add_loans";
    public final static String INVEST = "/invest";
    public final static String INCREASE_YAZ = "/increase_yaz";
    public final static String PAY_LOAN = "/pay_loan";
    public final static String PAY_DEBT = "/pay_debt";
    public final static String REWIND = "/rewind";
    public final static String DECREASE_YAZ = "/decrease_yaz";
    public final static String PUT_LOAN_FOR_SALE = "/loan_sale";
    public final static String BUY_LOAN_OWNERSHIP = "/buy_loan";


    public final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";

    // GSON instance

    public static final String USERNAME = "username";
    public static final String STATE = "state";
    public static final String USER_NAME_ERROR = "username_error";

    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;

}
