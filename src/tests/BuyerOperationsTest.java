package tests;

import operations.BuyerOperations;
import operations.CityOperations;
import operations.GeneralOperations;
import student.pm160695_BuyerOperations;
import student.pm160695_CityOperations;
import student.pm160695_GeneralOperations;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class BuyerOperationsTest {

    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private BuyerOperations buyerOperations;


    @Before
    public void setUp() throws Exception {
        //this.testHandler = TestHandler.getInstance();
        //Assert.assertNotNull(this.testHandler);

        //this.cityOperations = this.testHandler.getCityOperations();
    	this.cityOperations = new pm160695_CityOperations();
        Assert.assertNotNull(this.cityOperations);

        //generalOperations = testHandler.getGeneralOperations();
        this.generalOperations = new pm160695_GeneralOperations();
        Assert.assertNotNull(generalOperations);

        //buyerOperations = testHandler.getBuyerOperations();
        this.buyerOperations = new pm160695_BuyerOperations();
        Assert.assertNotNull(buyerOperations);

        generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        generalOperations.eraseAll();
    }

    @Test
    public void insertBuyer(){
        int cityId = cityOperations.createCity("Kragujevac");
        Assert.assertNotEquals(-1, cityId);

        int buyerId = buyerOperations.createBuyer("Pera", cityId);
        Assert.assertNotEquals(-1, buyerId);
    }

    @Test
    public void changeCity(){
        int cityId1 = cityOperations.createCity("Kragujevac");
        int cityId2 = cityOperations.createCity("Beograd");
        int buyerId = buyerOperations.createBuyer("Lazar", cityId1);
        buyerOperations.setCity(buyerId, cityId2);

        int cityId = buyerOperations.getCity(buyerId);
        Assert.assertEquals(cityId2, cityId);
    }

    @Test
    public void credit(){
        int cityId = cityOperations.createCity("Kragujevac");
        int buyerId = buyerOperations.createBuyer("Pera", cityId);

        BigDecimal credit1 = new BigDecimal("1000.000");

        BigDecimal creditReturned = buyerOperations.increaseCredit(buyerId, credit1);
        Assert.assertEquals(credit1, creditReturned);

        BigDecimal credit2 = new BigDecimal("500.000");
        buyerOperations.increaseCredit(buyerId, credit2);

        creditReturned = buyerOperations.getCredit(buyerId);
        Assert.assertEquals(credit1.add(credit2), creditReturned);
    }

    @Test
    public void orders(){
        int cityId = cityOperations.createCity("Kragujevac");
        int buyerId = buyerOperations.createBuyer("Pera", cityId);

        int orderId1 = buyerOperations.createOrder(buyerId);
        int orderId2 = buyerOperations.createOrder(buyerId);
        Assert.assertNotEquals(-1, orderId1);
        Assert.assertNotEquals(-1, orderId2);

        List<Integer> orders = buyerOperations.getOrders(buyerId);
        Assert.assertEquals(2, orders.size());
        Assert.assertTrue(orders.contains(orderId1) && orders.contains(orderId2));
    }

}
