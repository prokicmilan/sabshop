package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			
			PreparedStatement insertStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, parameters);
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int setCity(int shopId, String cityName) {
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
			
			PreparedStatement updateStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, parameters);
			int status = this.getStatementHandler().executeUpdateStatementAndGetId(updateStmt);
			
			return status != -1 ? 1 : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getCity(int shopId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select cityId from Shop where shopId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, parameters);
			
			List<Integer> resultList = this.getStatementHandler().executeSelectStatement(selectStmt);
			if (resultList.isEmpty() || resultList.size() > 1) {
				return -1;
			}
			return resultList.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int setDiscount(int shopId, int discountPercentage) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "update Shop set discount = ? where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(discountPercentage)));
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			
			PreparedStatement updateStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, parameters);
			
			int status = this.getStatementHandler().executeUpdateStatementAndGetId(updateStmt);
			
			return status != -1 ? 1 : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int increaseArticleCount(int articleId, int increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getArticleCount(int shopId, int articleId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select itemsAvailable from Article where shopId = ? and articleId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			parameters.add(new ParameterPair("int", Integer.toString(articleId)));
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, parameters);
			
			List<Integer> resultList = this.getStatementHandler().executeSelectStatement(selectStmt);
			if (resultList.isEmpty() || resultList.size() > 1) {
				return -1;
			}
			else {
				return resultList.get(0);
			}
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
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, parameters);
			
			return this.getStatementHandler().executeSelectStatement(selectStmt);
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
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, parameters);
			
			List<Integer> resultList = this.getStatementHandler().executeSelectStatement(selectStmt);
			if (resultList.isEmpty() || resultList.size() > 1) {
				// nije definisano postavkom, pa uzimamo da vraca -1 kao i kod svih ostalih gresaka
				return -1;
			}
			else {
				return resultList.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
