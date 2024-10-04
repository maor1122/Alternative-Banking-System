package Utils;

import bank.*;
import loadDir.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class fileLoader {
    private final static String schemaPath="loadDir";
    public static Bank load(String path) throws Exception{
        return convertObject(loadXmlFile(path));
    }

    public static AbsDescriptor loadXmlFile(String path) throws FileNotFoundException,JAXBException{
        InputStream inputStream = new FileInputStream(new File(path));
        AbsDescriptor descriptors = deserializeFrom(inputStream);
        return descriptors;
    }
    private static AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(schemaPath);
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }
    private static Bank convertObject(AbsDescriptor xml) throws Exception{
        Bank bank = new Bank();
        //bank.setCustomers(convertCustomers(xml.getAbsCustomers().getAbsCustomer()));
        bank.setCategories(convertCategories(xml.getAbsCategories().getAbsCategory()));
        bank.setLoans(convertLoans(xml.getAbsLoans().getAbsLoan(),bank.getCategories()));
        return bank;
    }
    /*
    private static List<Customer> convertCustomers(List<AbsCustomer> absCustomers) throws Exception{
        List<Customer> customers = new ArrayList<>();
        for(int i=0;i<absCustomers.size();i++){
            Customer newCustomer=new Customer(absCustomers.get(i).getName(),absCustomers.get(i).getAbsBalance());
            if(i!=0)
                if(customers.contains(newCustomer)) {
                    throw new Exceptions.CustomerAppearsMoreThenOnce(newCustomer);
                }
            customers.add(newCustomer);
        }
        return customers;
    }*/
    private static List<String> convertCategories(List<String> absCategories) throws Exception{
        List<String> categories = new ArrayList<>();
        for(int i=0;i<absCategories.size();i++){
            if(categories.contains(absCategories.get(i).trim()))
                throw new Exception("category "+absCategories.get(i).trim() + "apears more then once.");
            categories.add(absCategories.get(i).trim());
        }
        return categories;
    }
    private static List<Loan> convertLoans(List<AbsLoan> absLoans,List<String> categories) throws Exception{
        List<Loan> loans = new ArrayList<>();
        Customer owner=null;
        String category=null;
        for(int i=0;i<absLoans.size();i++) {
            AbsLoan currLoan = absLoans.get(i);
            if (currLoan.getAbsTotalYazTime() %currLoan.getAbsPaysEveryYaz()!= 0)
                throw new Exception("Loan "+currLoan.getId()+" Yaz timing is not valid.");
            //for (Customer customer : customers) {
                //if (customer.getName().equals(currLoan.getAbsOwner()))
                //    owner = customer;
            //}
            for (String category_ : categories) {
                if (category_.equals(currLoan.getAbsCategory()))
                    category = category_;
            }
            //if (owner == null)
            //    throw new Exceptions.OwnerNotFound(currLoan.getAbsOwner());
            if (category == null)
                throw new Exception("Category \""+currLoan.getAbsCategory()+"\" is not found.");
            else {
                Loan loan = (new Loan(currLoan.getId(), owner, currLoan.getAbsCategory(), currLoan.getAbsCapital(),
                    currLoan.getAbsTotalYazTime(), currLoan.getAbsPaysEveryYaz(), currLoan.getAbsIntristPerPayment(), 1));
                //owner.addLoans(loan);
                loans.add(loan);
            }
        }
        return loans;
    }
}
