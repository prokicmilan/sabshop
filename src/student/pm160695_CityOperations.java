package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import operations.CityOperations;

public class pm160695_CityOperations implements CityOperations {
	
	private String connectionString;
	
	public pm160695_CityOperations() {
		this.connectionString = ResourceManager.getConnectionString();
	}

	@Override
	public int createCity(String name) {
		try (Connection conn = DriverManager.getConnection(connectionString)) {
			String query = "insert into City (cityName) values (?)";
			PreparedStatement insertStmt = this.prepareInsertStatement(conn, query);
			
			insertStmt.setString(1, name);
			
			return this.executeAndgetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getCities() {
		try (Connection conn = DriverManager.getConnection(connectionString)) {
			String query = "select id from City";
			PreparedStatement selectStmt = conn.prepareStatement("select id from City");
			List<Integer> citiesList = new LinkedList<>();
			
			ResultSet rs = selectStmt.executeQuery();
			
			while (rs.next()) {
				citiesList.add(rs.getInt("id"));
			}
			
			return citiesList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int connectCities(int cityId1, int cityId2, int distance) {
		try (Connection conn = DriverManager.getConnection(connectionString)) {
			String query = "insert into Line (cityId1, cityId2, distance) values (?, ?, ?)";
			
			PreparedStatement insertStmt = this.prepareInsertStatement(conn, query);
			
			insertStmt.setInt(1, cityId1);
			insertStmt.setInt(2, cityId2);
			insertStmt.setInt(3, distance);
			
			return this.executeAndgetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getConnectedCities(int cityId) {
		try (Connection conn = DriverManager.getConnection(connectionString)) {
			String query = "select * from Line where cityId1 = ? or cityId2 = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			List<Integer> connectedCitiesList = new LinkedList<>();
			
			stmt.setInt(1, cityId);
			stmt.setInt(2, cityId);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				connectedCitiesList.add(rs.getInt("id"));
			}
			
			return connectedCitiesList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getShops(int cityId) {
		try (Connection conn = DriverManager.getConnection(connectionString)) {
			String query = "select id from Shop where cityId = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			List<Integer> shopList = new LinkedList<>();
			
			stmt.setInt(1, cityId);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				shopList.add(rs.getInt("id"));
			}
			
			return shopList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private PreparedStatement prepareInsertStatement(Connection connection, String query) throws SQLException {
		return connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}
	
	private int executeAndgetId(PreparedStatement insertStmt) throws SQLException {
		int affectedRows = insertStmt.executeUpdate();
		
		if (affectedRows == 1) {
			ResultSet rs = insertStmt.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1);
			}
		}
		return -1;
	}

}
