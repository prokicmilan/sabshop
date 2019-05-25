package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import operations.GeneralOperations;

public class pm160695_GeneralOperations extends OperationImplementation implements GeneralOperations {

	@Override
	public void setInitialTime(Calendar time) {
		// TODO Auto-generated method stub

	}

	@Override
	public Calendar time(int days) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calendar getCurrentTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eraseAll() {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			List<String> queries = new LinkedList<>();
			queries.addAll(Arrays.asList(new String[] {
					"delete from Article",
					"delete from ArticleInOrder",
					"delete from [Order]",
					"delete from Buyer",
					"delete from Shop",
					"delete from Line",
					"delete from City"
			}));
			for (String query : queries) {
				PreparedStatement deleteStmt = this.getStatementHandler().prepareUpdateStatement(connection, query, null);
				this.getStatementHandler().executeUpdateStatementAndGetIdList(deleteStmt);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
