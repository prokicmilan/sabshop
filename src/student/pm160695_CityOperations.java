package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import operations.CityOperations;

public class pm160695_CityOperations implements CityOperations {

	private StatementHandler statementHandler;
	private String connectionString;
	
	public pm160695_CityOperations() {
		this.connectionString = ResourceManager.getConnectionString();
		this.statementHandler = StatementHandler.getInstance();
	}

	@Override
	public int createCity(String name) {
		try (Connection connection = DriverManager.getConnection(connectionString)) {
			String query = "insert into City (cityName) values (?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("String", name));
			
			PreparedStatement insertStmt = this.statementHandler.prepareInsertStatement(connection, query, parameters);
			
			return this.statementHandler.executeInsertStatementAndGetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getCities() {
		try (Connection connection = DriverManager.getConnection(connectionString)) {
			String query = "select id from City";
			PreparedStatement selectStmt = this.statementHandler.prepareSelectStatement(connection, query, null);
			
			return this.statementHandler.executeSelectStatement(selectStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int connectCities(int cityId1, int cityId2, int distance) {
		try (Connection connection = DriverManager.getConnection(connectionString)) {
			String query = "insert into Line (cityId1, cityId2, distance) values (?, ?, ?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId1)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId2)));
			parameters.add(new ParameterPair("int", Integer.toString(distance)));
			
			PreparedStatement insertStmt = this.statementHandler.prepareInsertStatement(connection, query, parameters);
			
			return this.statementHandler.executeInsertStatementAndGetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getConnectedCities(int cityId) {
		try (Connection connection = DriverManager.getConnection(connectionString)) {
			String query = "select * from Line where cityId1 = ? or cityId2 = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			
			PreparedStatement selectStmt = this.statementHandler.prepareSelectStatement(connection, query, parameters);
			
			return this.statementHandler.executeSelectStatement(selectStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getShops(int cityId) {
		try (Connection connection = DriverManager.getConnection(connectionString)) {
			String query = "select id from Shop where cityId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			
			PreparedStatement selectStmt = this.statementHandler.prepareSelectStatement(connection, query, parameters);
			
			return this.statementHandler.executeSelectStatement(selectStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
