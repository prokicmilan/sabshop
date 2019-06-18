package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import operations.GeneralOperations;
import operations.TransactionOperations;

public class pm160695_TransactionOperations extends OperationImplementation implements TransactionOperations {

	@Override
	public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select transactionAmount from [Transaction] where buyerId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));

			List<BigDecimal> transactionAmountList = this.getStatementHandler().executeSelectListStatement(connection, selectQuery, parameters, BigDecimal.class);
			
			return transactionAmountList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getShopTransactionsAmmount(int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select transactionAmount from [Transaction] where shopId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));

			List<BigDecimal> transactionAmountList = this.getStatementHandler().executeSelectListStatement(connection, selectQuery, parameters, BigDecimal.class);
			
			return transactionAmountList.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(3);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getTransationsForBuyer(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select id from [Transaction] where buyerId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			return this.getStatementHandler().executeSelectListStatement(connection, selectQuery, parameters, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getTransactionForBuyersOrder(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select id from [Transaction] where orderId = ? and buyerId is not null";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Integer.class);
			
			return retVal != -1 ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getTransactionForShopAndOrder(int orderId, int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select id from [Transaction] where orderId = ? and shopId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Integer.class);
			
			return retVal != -1 ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getTransationsForShop(int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select id from [Transaction] where shopId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			List<Integer> transactions = this.getStatementHandler().executeSelectListStatement(connection, selectQuery, parameters, Integer.class);
			return transactions.size() != 0 ? transactions : null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Calendar getTimeOfExecution(int transactionId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select timeOfExecution from [Transaction] where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(transactionId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Calendar.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select transactionAmount from [Transaction] where orderId = ? and buyerId is not null";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, BigDecimal.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select transactionAmount from [Transaction] where orderId = ? and shopId is not null";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, BigDecimal.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getTransactionAmount(int transactionId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select transactionAmount from [Transaction] id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(transactionId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, BigDecimal.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getSystemProfit() {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String totalPaidByCustomersQuery = "select sum(transactionAmount) from [Transaction] where buyerId is not null";
			String totalPaidToShopsQuery = "select sum(transactionAmount) from [Transaction] where shopId is not null";
			
			BigDecimal paidByCustomers = this.getStatementHandler().executeSelectStatement(connection, totalPaidByCustomersQuery, null, BigDecimal.class);
			BigDecimal paidToShops = this.getStatementHandler().executeSelectStatement(connection, totalPaidToShopsQuery, null, BigDecimal.class);
			
			if (paidByCustomers == null || paidToShops == null || paidByCustomers.compareTo(BigDecimal.ZERO) == 0 || paidToShops.compareTo(BigDecimal.ZERO) == 0) {
				return BigDecimal.ZERO.setScale(3);
			}
			else {
				return paidByCustomers.subtract(paidToShops).setScale(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public BigDecimal getTransactionAmountLastThirtyDaysForBuyer(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select transactionAmount from [Transaction] where buyerId = ? and timeOfExecution >= ?";

			GeneralOperations generalOperations = new pm160695_GeneralOperations();
			Calendar currentTime = generalOperations.getCurrentTime();
			LocalDate thirtyDaysBeforeCurrent = LocalDateTime.ofInstant(currentTime.toInstant(), currentTime.getTimeZone().toZoneId()).toLocalDate().minusDays(30);
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			parameters.add(new ParameterPair("LocalDate", Long.toString(thirtyDaysBeforeCurrent.toEpochDay())));

			List<BigDecimal> transactionAmountList = this.getStatementHandler().executeSelectListStatement(connection, selectQuery, parameters, BigDecimal.class);
			
			return transactionAmountList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
