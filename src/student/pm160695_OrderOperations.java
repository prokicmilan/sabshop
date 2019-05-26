package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import operations.OrderOperations;

public class pm160695_OrderOperations extends OperationImplementation implements OrderOperations {

	@Override
	public int addArticle(int orderId, int articleId, int count) {
		int retVal = -1;
		
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			try {
				// postavljamo autocommit na false jer imamo update jedne tabele i upis u drugu
				connection.setAutoCommit(false);
	
				
				String updateQuery = "update Article set itemsAvailable = itemsAvailable - ? where id = ?";
				
				List<ParameterPair> parameters = new LinkedList<>();
				parameters.add(new ParameterPair("int", Integer.toString(count)));
				parameters.add(new ParameterPair("int", Integer.toString(articleId)));
				
				PreparedStatement updateStmt = this.getStatementHandler().prepareUpdateStatement(connection, updateQuery, parameters);
				
				this.getStatementHandler().executeUpdateStatementAndGetId(updateStmt);
				
				parameters.clear();
				// proveravamo da li proizvod vec postoji u porudzbini
				String selectQuery = "select id from ArticleInOrder where orderId = ? and articleId = ?";
				parameters.add(new ParameterPair("int", Integer.toString(orderId)));
				parameters.add(new ParameterPair("int", Integer.toString(articleId)));
				
				Integer articleInOrderId = this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Integer.class);
				parameters.clear();
				if (articleInOrderId != null) {
					// proizvod vec postoji u narudzbini, samo radimo update kolicine
					String updateAiOQuery = "update ArticleInOrder set amount = amount + ? where id = ?";
					
					parameters.add(new ParameterPair("int", Integer.toString(count)));
					parameters.add(new ParameterPair("int", Integer.toString(articleInOrderId)));
					
					PreparedStatement updateAiOStmt = this.getStatementHandler().prepareUpdateStatement(connection, updateAiOQuery, parameters);
					
					retVal = this.getStatementHandler().executeUpdateStatementAndGetId(updateAiOStmt);
				}
				else {
					// proizvod ne postoji u narudzbini, dodajemo novi 
					String insertAiOQuery = "insert into ArticleInOrder (orderId, articleId, amount) values (?, ?, ?)";
					
					parameters.add(new ParameterPair("int", Integer.toString(orderId)));
					parameters.add(new ParameterPair("int", Integer.toString(articleId)));
					parameters.add(new ParameterPair("int", Integer.toString(count)));
					
					PreparedStatement insertAiOStmt = this.getStatementHandler().prepareUpdateStatement(connection, insertAiOQuery, parameters);
					
					retVal = this.getStatementHandler().executeUpdateStatementAndGetId(insertAiOStmt);
				}
				
				// commit-ujemo transakciju
				connection.commit();
				
				return retVal;
			} catch (SQLException e) {
				// doslo je do greske, radimo rollback transakcije i bacamo exception dalje
				connection.rollback();
				retVal = -1;
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			retVal = -1;
			e.printStackTrace();
			return retVal;
		}
	}

	@Override
	public int removeArticle(int orderId, int articleId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			try {
				// postavljamo autocommit na false jer imamo update i delete
				connection.setAutoCommit(false);
				
				// dohvatamo trenutni broj artikala u narudzbini
				String selectQuery = "select amount from ArticleInOrder where orderId = ? and articleId = ?";
				
				List<ParameterPair> parameters = new LinkedList<>();
				parameters.add(new ParameterPair("int", Integer.toString(orderId)));
				parameters.add(new ParameterPair("int", Integer.toString(articleId)));

				Integer articlesInOrder = this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Integer.class);
				
				parameters.clear();
				String updateQuery = "update Article set itemsAvailable = itemsAvailable + ? where id = ?";
				
				parameters.add(new ParameterPair("int", Integer.toString(articlesInOrder)));
				parameters.add(new ParameterPair("int", Integer.toString(articleId)));
				
				PreparedStatement updateStmt = this.getStatementHandler().prepareUpdateStatement(connection, updateQuery, parameters);

				this.getStatementHandler().executeUpdateStatementAndGetId(updateStmt);
				
				parameters.clear();
				String deleteQuery = "delete from ArticleInOrder where orderId = ? and articleId = ";
				
				parameters.add(new ParameterPair("int", Integer.toString(orderId)));
				parameters.add(new ParameterPair("int", Integer.toString(articleId)));
				
				PreparedStatement deleteStmt = this.getStatementHandler().prepareUpdateStatement(connection, deleteQuery, parameters);
				
				this.getStatementHandler().executeUpdateStatementAndGetId(updateStmt);
				
				connection.commit();
				return 1;
			} catch (SQLException e) {
				// doslo je do greske, radimo rollback transakcije i bacamo exception dalje
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getItems(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select articleId from ArticleInOrder where orderId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			return this.getStatementHandler().executeSelectListStatement(connection, query, parameters, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int completeOrder(int orderId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getFinalPrice(int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getDiscountSum(int orderId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getState(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select [state] from [Order] where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			return this.getStatementHandler().executeSelectStatement(connection, query, parameters, String.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Calendar getSentTime(int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calendar getRecievedTime(int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBuyer(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select buyerId from [Order] where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			Integer retVal = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return retVal != null ? retVal.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getLocation(int orderId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
