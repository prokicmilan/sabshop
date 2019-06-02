package student;

import java.sql.Connection;
import java.sql.DriverManager;
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
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getCities() {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from City";
			
			return this.getStatementHandler().executeSelectListStatement(connection, query, null, Integer.class);
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
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getConnectedCities(int cityId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select cityId1 from Line where cityId2 = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId)));
			
			List<Integer> returnList = this.getStatementHandler().executeSelectListStatement(connection, query, parameters, Integer.class);
			
			query = "select cityId2 from Line where cityId1 = ?";
			
			returnList.addAll(this.getStatementHandler().executeSelectListStatement(connection, query, parameters, Integer.class));
			
			return returnList;
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
			
			return this.getStatementHandler().executeSelectListStatement(connection, query, parameters, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getDistanceBetweenCities(int cityId1, int cityId2) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select distance from Line where (cityId1 = ? and cityId2 = ?) or (cityId1 = ? and cityId2 = ?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(cityId1)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId2)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId2)));
			parameters.add(new ParameterPair("int", Integer.toString(cityId1)));

			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return retVal != -1 ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int getCityIdByName(String cityName) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from City where cityName = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("String", cityName));

			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			return retVal != null ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
