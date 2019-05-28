package tests;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import operations.TransactionOperations;
import student.pm160695_TransactionOperations;

public class TransactionOperationsTest {

	private TransactionOperations transactionOperation;
	
    @Before
    public void setUp() throws Exception {
        this.transactionOperation = new pm160695_TransactionOperations();
    }
    
    @Test
    public void callFunction() {
    	BigDecimal result = this.transactionOperation.getSystemProfit();
    	System.out.println(result);
    }
	
}
