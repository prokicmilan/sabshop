package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import operations.ShopOperations;

public class pm160695_ShopOperations extends OperationImplementation implements ShopOperations {

	@Override
	public int createShop(String name, String cityName) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			pm160695_CityOperations cityOperations = new pm160695_CityOperations();
			int cityId = cityOperations.getCityIdByName(cityName);
			
			if (cityId == -1) {
				return -1;
			}
			String query = "insert into Shop (cityId, shopName) values (?, ?)";
			
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
	public int setCity(int shopId, String cityName) {
		int retVal;
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			pm160695_CityOperations cityOperations = new pm160695_CityOperations();
			int cityId = cityOperations.getCityIdByName(cityName);
			
			if (cityId == -1) {
				return -1;
			}
			
			String query = "update Shop set cityId = ? where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
			
			return retVal != -1 ? 1 : -1;
		} catch (SQLException e) {
			retVal = -1;
			e.printStackTrace();
			return retVal;
		}
	}

	@Override
	public int getCity(int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select cityId from Shop where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return retVal != null ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int setDiscount(int shopId, int discountPercentage) {
		int retVal;
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "update Shop set discount = ? where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(discountPercentage)));
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
			
			return retVal != -1 ? 1 : -1;
		} catch (SQLException e) {
			retVal = -1;
			e.printStackTrace();
			return retVal;
		}
	}

	@Override
	public int increaseArticleCount(int articleId, int increment) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String updateQuery = "update Article set itemsAvailable = itemsAvailable + ? where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(increment)));
			parameters.add(new ParameterPair("int", Integer.toString(articleId)));
			
			this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateQuery, parameters);
			
			parameters.clear();
			String selectQuery = "select itemsAvailable from Article where id = ?";
			
			parameters.add(new ParameterPair("int", Integer.toString(articleId)));
			
			Integer itemsAfterIncrease = this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Integer.class);
			
			return itemsAfterIncrease != null ? itemsAfterIncrease.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getArticleCount(int shopId, int articleId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select itemsAvailable from Article where shopId = ? and id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			parameters.add(new ParameterPair("int", Integer.toString(articleId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return retVal != null ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getArticles(int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from Article where shopId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			return this.getStatementHandler().executeSelectListStatement(connection, query, parameters, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getDiscount(int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select discount from Shop where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return retVal != null ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
