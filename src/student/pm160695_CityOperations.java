package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import operations.CityOperations;

public class pm160695_CityOperations extends OperationImplementation implements CityOperations {

	@Override
	public int createCity(String name) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "insert into City (cityName) values (?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("String", name));
			
			PreparedStatement insertStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, parameters);
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getCities() {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from City";
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, null);
			
			return this.getStatementHandler().executeSelectStatement(selectStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int connectCities(int cityId1, int cityId2, int distance) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "insert into Line (cityId1, cityId2, distance) values (?, ?, ?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId1)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId2)));
			parameters.add(new ParameterPair("int", Integer.toString(distance)));
			
			PreparedStatement insertStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, parameters);
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getConnectedCities(int cityId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select * from Line where cityId1 = ? or cityId2 = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, parameters);
			
			return this.getStatementHandler().executeSelectStatement(selectStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getShops(int cityId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from Shop where cityId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareSelectStatement(connection, query, parameters);
			
			return this.getStatementHandler().executeSelectStatement(selectStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected int getCityIdByName(String cityName) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from City where cityName = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("String", cityName));
			
			PreparedStatement selectStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, parameters);

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
}
