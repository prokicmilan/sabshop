package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import operations.ArticleOperations;

public class pm160695_ArticleOperations implements ArticleOperations {

	private StatementHandler statementHandler;
	private String connectionString;
	
	public pm160695_ArticleOperations() {
		this.connectionString = ResourceManager.getConnectionString();
		this.statementHandler = StatementHandler.getInstance();
	}
	
	@Override
	public int createArticle(int shopId, String articleName, int articlePrice) {
		try (Connection connection = DriverManager.getConnection(connectionString)) {
			String query = "insert into Article (shopId, articleName, price) values (?, ?, ?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			parameters.add(new ParameterPair("String", articleName));
			parameters.add(new ParameterPair("int", Integer.toString(articlePrice)));
			
			PreparedStatement insertStmt = this.statementHandler.prepareInsertStatement(connection, query, parameters);

			return this.statementHandler.executeInsertStatementAndGetId(insertStmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
