package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import operations.BuyerOperations;

public class pm160695_BuyerOperations extends OperationImplementation implements BuyerOperations {

	@Override
	public int createBuyer(String name, int cityId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "insert into Buyer (cityId, name) values (?, ?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			parameters.add(new ParameterPair("String", name));
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int setCity(int buyerId, int cityId) {
		int retVal = -1;
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "update Buyer set cityId = ? where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
			
			return retVal != -1 ? 1 : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			retVal = -1;
			return retVal;
		}
	}

	@Override
	public int getCity(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select cityId from Buyer where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return retVal != null ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String updateQuery = "update Buyer set balance = balance + ? where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("BigDecimal", credit.toString()));
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateQuery, parameters);
			
			parameters.clear();
			String selectQuery = "select balance from Buyer where id = ?";
			
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, BigDecimal.class);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int createOrder(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "insert into [Order] (buyerId) values (?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getOrders(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from [Order] where buyerId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			return this.getStatementHandler().executeSelectListStatement(connection, query, parameters, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getCredit(int buyerId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select balance from Buyer where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(buyerId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, query, parameters, BigDecimal.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
