package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import operations.ArticleOperations;

public class pm160695_ArticleOperations implements ArticleOperations {

	@Override
	public int createArticle(int shopId, String articleName, int articlePrice) {
		String connectionString = ResourceManager.getConnectionString();
		try (Connection conn = DriverManager.getConnection(connectionString)) {
			PreparedStatement insertStmt = conn.prepareStatement("insert into Article (shopId, articleName, price) values (?, ?, ?)",
																 Statement.RETURN_GENERATED_KEYS);
			
			insertStmt.setInt(1, shopId);
			insertStmt.setString(2, articleName);
			insertStmt.setInt(3, articlePrice);
			
			int affectedRows = insertStmt.executeUpdate();
			if (affectedRows == 1) {
				ResultSet rs = insertStmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
				else {
					return -1;
				}
			}
			else {
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
