package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class StatementHandler {

	private static StatementHandler instance;
	
	public static StatementHandler getInstance() {
		if (instance == null) {
			instance = new StatementHandler();
		}
		return instance;
	}
	
	public <T> T executeSelectStatement(Connection connection, String query, List<ParameterPair> parameters, Class<T> typeClass) throws SQLException {
		List<T> resultList = this.executeSelectListStatement(connection, query, parameters, typeClass);
		if (resultList.isEmpty() || resultList.size() > 1) {
			return null;
		}
		else {
			return resultList.get(0);
		}
	}
	
	public <T> List<T> executeSelectListStatement(Connection connection, String query, List<ParameterPair> parameters, Class<T> typeClass) throws SQLException {
		PreparedStatement selectStatement = this.prepareSelectStatement(connection, query, parameters);
		List<T> resultList = new LinkedList<>();
		
		this.populateResultList(selectStatement, resultList, typeClass);
		
		return resultList;
	}
	
	public int executeUpdateStatementAndGetId(Connection connection, String query, List<ParameterPair> parameters) throws SQLException {
		List<Integer> resultList = this.executeUpdateStatementAndGetIdList(connection, query, parameters);
		if (resultList.isEmpty() || resultList.size() > 1) {
			return -1;
		}
		else {
			return resultList.get(0).intValue();
		}
	}
	
	public List<Integer> executeUpdateStatementAndGetIdList(Connection connection, String query, List<ParameterPair> parameters) throws SQLException {
		PreparedStatement updateStatement = this.prepareUpdateStatement(connection, query, parameters);
		List<Integer> generatedKeysList = new LinkedList<>();
		
		updateStatement.executeUpdate();
		try (ResultSet rs = updateStatement.getGeneratedKeys()) {
			while (rs.next()) {
				generatedKeysList.add(rs.getInt(1));
			}
		}
		return generatedKeysList;
	}
	
	private PreparedStatement prepareSelectStatement(Connection connection, String query, List<ParameterPair> parameters) throws SQLException {
		PreparedStatement selectStatment = connection.prepareStatement(query);
		this.setParameters(selectStatment, parameters);
		
		return selectStatment;
	}
	
	private PreparedStatement prepareUpdateStatement(Connection connection, String query, List<ParameterPair> parameters) throws SQLException {
		PreparedStatement updateStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		this.setParameters(updateStatement, parameters);
		return updateStatement;
	}
	
	private void setParameters(PreparedStatement statement, List<ParameterPair> parameters) throws SQLException {
		if (parameters == null || parameters.isEmpty()) return;
		
		int paramIndex = 1;
		
		for (ParameterPair argumentPair : parameters) {
			String type = argumentPair.getType();
			switch (type) {
				case "int":
					int intVal = Integer.parseInt(argumentPair.getValue());
					statement.setInt(paramIndex++, intVal);
					break;
					
				case "float":
					float floatVal = Float.parseFloat(argumentPair.getValue());
					statement.setFloat(paramIndex++, floatVal);
					break;
	
				case "String":
					String stringVal = argumentPair.getValue();
					statement.setString(paramIndex++, stringVal);
					break;
					
				case "BigDecimal":
					BigDecimal bigDecimalVal = new BigDecimal(argumentPair.getValue());
					statement.setBigDecimal(paramIndex++, bigDecimalVal);
					break;
					
				default:
					System.err.println("Unknown type: " + type);
					break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> void populateResultList(PreparedStatement statement, List<T> resultList, Class<T> typeClass) throws SQLException {
		String className = typeClass.getName().substring(typeClass.getName().lastIndexOf(".") + 1, typeClass.getName().length());
		try (ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				switch (className) {
					case "Integer":
						resultList.add((T) Integer.valueOf(rs.getInt(1)));
						break;
					case "BigDecimal":
						resultList.add((T) rs.getBigDecimal(1));
						break;
					case "Calendar":
						Timestamp ts = rs.getTimestamp(1);
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(ts.getTime());
						resultList.add((T) calendar);
						break;
					default:
						System.err.println("Unsupported type class: " + className);
						break;
					}
			}
		}
	}
	
	private StatementHandler() {
	}
	
}
