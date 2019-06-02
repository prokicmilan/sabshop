package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import operations.GeneralOperations;
import operations.OrderOperations;
import operations.ShopOperations;

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
				
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateQuery, parameters);
				
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
					
					retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateAiOQuery, parameters);
				}
				else {
					// proizvod ne postoji u narudzbini, dodajemo novi 
					String insertAiOQuery = "insert into ArticleInOrder (orderId, articleId, amount) values (?, ?, ?)";
					
					parameters.add(new ParameterPair("int", Integer.toString(orderId)));
					parameters.add(new ParameterPair("int", Integer.toString(articleId)));
					parameters.add(new ParameterPair("int", Integer.toString(count)));
					
					retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, insertAiOQuery, parameters);
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
				
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateQuery, parameters);
				
				parameters.clear();
				String deleteQuery = "delete from ArticleInOrder where orderId = ? and articleId = ";
				
				parameters.add(new ParameterPair("int", Integer.toString(orderId)));
				parameters.add(new ParameterPair("int", Integer.toString(articleId)));
				
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, deleteQuery, parameters);
				
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
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			try {
				connection.setAutoCommit(false);
				
				GeneralOperations generalOperations = new pm160695_GeneralOperations();
				Calendar currentTime = generalOperations.getCurrentTime();
				
				String updateOrderQuery = "update [Order] set state = ?, sentTime = ? where id = ?";
				
				List<ParameterPair> orderUpdateParams = new LinkedList<>();
				orderUpdateParams.add(new ParameterPair("String", "sent"));
				orderUpdateParams.add(new ParameterPair("Calendar", Long.toString(currentTime.getTimeInMillis())));
				orderUpdateParams.add(new ParameterPair("int", Integer.toString(orderId)));
				
				String updateBuyerQuery = "update Buyer set balance = balance - ? where id = ?";
				
				int buyerId = this.getBuyer(orderId);
				BigDecimal finalPrice = this.calculateFinalPrice(orderId);
				BigDecimal discount = this.calculateDiscountSum(orderId);

				List<ParameterPair> buyerUpdateParams = new LinkedList<>();
				buyerUpdateParams.add(new ParameterPair("BigDecimal", finalPrice.toString()));
				buyerUpdateParams.add(new ParameterPair("int", Integer.toString(buyerId)));
				
				String insertIntoTransactionQuery = "insert into [Transaction] (orderId, buyerId, timeOfExecution, transactionAmount, discountAmount, additionalDiscount) "
												  + "values (?, ?, ?, ?, ?, ?)";
				
				boolean hasExtraDiscount = this.hasExtraDiscount(buyerId);
				
				List<ParameterPair> transactionInsertParams = new LinkedList<>();
				
				transactionInsertParams.add(new ParameterPair("int", Integer.toString(orderId)));
				transactionInsertParams.add(new ParameterPair("int", Integer.toString(buyerId)));
				transactionInsertParams.add(new ParameterPair("Calendar", Long.toString(currentTime.getTimeInMillis())));
				transactionInsertParams.add(new ParameterPair("BigDecimal", finalPrice.toString()));
				transactionInsertParams.add(new ParameterPair("BigDecimal", discount.toString()));
				transactionInsertParams.add(new ParameterPair("boolean", Boolean.toString(hasExtraDiscount)));
				
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateOrderQuery, orderUpdateParams);
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateBuyerQuery, buyerUpdateParams);
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, insertIntoTransactionQuery, transactionInsertParams);
				
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public BigDecimal getFinalPrice(int orderId) {
		String orderState = this.getState(orderId);
		if (!"sent".equalsIgnoreCase(orderState) && !"completed".equalsIgnoreCase(orderState)) {
			// porudzbina nije u statusu "sent" ili "completed"
			return BigDecimal.valueOf(-1);
		}
		return this.calculateFinalPrice(orderId);
	}

	@Override
	public BigDecimal getDiscountSum(int orderId) {
		String orderState = this.getState(orderId);
		if (!"sent".equalsIgnoreCase(orderState) && !"completed".equalsIgnoreCase(orderState)) {
			// porudzbina nije u statusu "sent" ili "completed"
			return BigDecimal.valueOf(-1);
		}
		
		return this.calculateDiscountSum(orderId);
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
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select sentTime from [Order] where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));

			return this.getStatementHandler().executeSelectStatement(connection, query, parameters, Calendar.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Calendar getRecievedTime(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select recievedTime from [Order] where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));

			return this.getStatementHandler().executeSelectStatement(connection, query, parameters, Calendar.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
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
	
	private boolean hasExtraDiscount(int buyerId) {
		pm160695_TransactionOperations transactionOperations = new pm160695_TransactionOperations();
		
		return transactionOperations.getTransactionAmountLastThirtyDaysForBuyer(buyerId).compareTo(BigDecimal.valueOf(10000)) >= 0;
	}

	private BigDecimal calculateFinalPrice(int orderId) {
		pm160695_ArticleOperations articleOperations = new pm160695_ArticleOperations();
		
		List<Integer> articlesInOrder = this.getItems(orderId);
		long totalPrice = 0;
		
		for (Integer articleId : articlesInOrder) {
			int articlePrice = articleOperations.getArticlePrice(articleId);
			int amount = this.getArticleAmountInOrder(articleId, orderId);
			totalPrice += articlePrice * amount;
		}
		BigDecimal totalDiscount = this.calculateDiscountSum(orderId);
		
		return BigDecimal.valueOf(totalPrice).subtract(totalDiscount).setScale(3);
	}
	
	private BigDecimal calculateDiscountSum(int orderId) {
		ShopOperations shopOperations = new pm160695_ShopOperations();
		pm160695_ArticleOperations articleOperations = new pm160695_ArticleOperations();
		
		List<Integer> articlesInOrder = this.getItems(orderId);
		BigDecimal totalDiscount = BigDecimal.ZERO;
		
		for (Integer articleId : articlesInOrder) {
			int articlePrice = articleOperations.getArticlePrice(articleId);
			if (articlePrice == -1) {
				totalDiscount = BigDecimal.valueOf(-1);
				break;
			}
			
			int shopId = articleOperations.getShop(articleId);
			if (shopId == -1) {
				totalDiscount = BigDecimal.valueOf(-1);
				break;
			}
			
			int shopDiscount = shopOperations.getDiscount(shopId);
			if (shopDiscount == -1) {
				totalDiscount = BigDecimal.valueOf(-1);
				break;
			}
			int amount = this.getArticleAmountInOrder(articleId, orderId);
			
			BigDecimal discountValue = BigDecimal.valueOf(articlePrice * amount * (shopDiscount / 100.0));
			BigDecimal newPrice = BigDecimal.valueOf(articlePrice).subtract(discountValue);
			
			int buyerId = this.getBuyer(orderId);
			if (buyerId == -1) {
				totalDiscount = BigDecimal.valueOf(-1);
				break;
			}
			if (this.hasExtraDiscount(buyerId)) { 
				discountValue = discountValue.add(newPrice.multiply(BigDecimal.valueOf(0.02)));
			}
			totalDiscount = totalDiscount.add(discountValue);
		}
		
		return totalDiscount.setScale(3);
	}
	
	private int getArticleAmountInOrder(int articleId, int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectQuery = "select amount from ArticleInOrder where articleId = ? and orderId = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(articleId)));
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			Integer amount = this.getStatementHandler().executeSelectStatement(connection, selectQuery, parameters, Integer.class);
			
			return amount.intValue();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
}
