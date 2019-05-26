package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import operations.ArticleOperations;

public class pm160695_ArticleOperations extends OperationImplementation implements ArticleOperations {
	
	@Override
	public int createArticle(int shopId, String articleName, int articlePrice) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "insert into Article (shopId, articleName, price) values (?, ?, ?)";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(shopId)));
			parameters.add(new ParameterPair("String", articleName));
			parameters.add(new ParameterPair("int", Integer.toString(articlePrice)));
			
			return this.getStatementHandler().executeUpdateStatementAndGetId(connection, query, parameters);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
