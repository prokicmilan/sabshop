package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public PreparedStatement prepareSelectStatement(Connection connection, String query, List<ParameterPair> parameters) throws SQLException {
		PreparedStatement selectStatment = connection.prepareStatement(query);
		this.setParameters(selectStatment, parameters);
		
		return selectStatment;
	}
	
	public PreparedStatement prepareInsertStatement(Connection connection, String query, List<ParameterPair> parameters) throws SQLException {
		PreparedStatement insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		this.setParameters(insertStatement, parameters);
		return insertStatement;
	}
	
	public List<Integer> executeSelectStatement(PreparedStatement selectStmt) throws SQLException {
		List<Integer> resultList = new LinkedList<>();
		
		try (ResultSet rs = selectStmt.executeQuery()) {
			while (rs.next()) {
				resultList.add(rs.getInt("id"));
			}
		}
		return resultList;
	}
	
	public int executeInsertStatementAndGetId(PreparedStatement insertStmt) throws SQLException {
		int affectedRows = insertStmt.executeUpdate();
		
		if (affectedRows == 1) {
			try (ResultSet rs = insertStmt.getGeneratedKeys()) {
			
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		}
		return -1;
	}
	
	public List<Integer> executeInsertStatementAndGetIdList(PreparedStatement insertStatement) throws SQLException {
		List<Integer> generatedKeysList = new LinkedList<>();
		
		insertStatement.executeUpdate();
		try (ResultSet rs = insertStatement.getGeneratedKeys()) {
			while (rs.next()) {
				generatedKeysList.add(rs.getInt(1));
			}
		}
		return generatedKeysList;
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
				
			default:
				System.err.println("Unknown type: " + type);
				break;
			}
		}
	}
	
	private StatementHandler() {
		
	}
	
}
