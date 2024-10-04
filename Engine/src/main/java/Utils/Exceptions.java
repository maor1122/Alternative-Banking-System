package Utils;

import bank.Customer;
import bank.Loan;

public class Exceptions {
    public static class CategoryAppearsMoreThenOnce extends Exception{
        public CategoryAppearsMoreThenOnce(String category){
            super(category);
        }
    }
    public static class CustomerAppearsMoreThenOnce extends Exception{
        public CustomerAppearsMoreThenOnce(Customer customer){
            super(customer.getName());
        }
    }
    public static class YazIncorrectTiming extends Exception{
        public YazIncorrectTiming(String id){
            super(id);
        }
    }
    public static class OwnerNotFound extends  Exception{
        public OwnerNotFound(String name){
            super(name);
        }
    }
    public static class CategoryNotFound extends  Exception{
        public CategoryNotFound(String name){
            super(name);
        }
    }
    public static class NoFileLoaded extends  Exception{
        public NoFileLoaded(String name){super(name);}
    }
}
