import operations.*;
import student.pm160695_ArticleOperations;
import student.pm160695_BuyerOperations;
import student.pm160695_CityOperations;
import student.pm160695_GeneralOperations;
import student.pm160695_OrderOperations;
import student.pm160695_ShopOperations;
import student.pm160695_TransactionOperations;

import org.junit.Test;
import tests.TestHandler;
import tests.TestRunner;

import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new pm160695_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new pm160695_BuyerOperations();
        CityOperations cityOperations = new pm160695_CityOperations();
        GeneralOperations generalOperations = new pm160695_GeneralOperations();
        OrderOperations orderOperations = new pm160695_OrderOperations();
        ShopOperations shopOperations = new pm160695_ShopOperations();
        TransactionOperations transactionOperations = new pm160695_TransactionOperations();
//
//        Calendar c = Calendar.getInstance();
//        c.clear();
//        c.set(2010, Calendar.JANUARY, 01);
//
//
//        Calendar c2 = Calendar.getInstance();
//        c2.clear();
//        c2.set(2010, Calendar.JANUARY, 01);
//
//        if(c.equals(c2)) System.out.println("jednako");
//        else System.out.println("nije jednako");

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
