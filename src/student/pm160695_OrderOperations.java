package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import operations.BuyerOperations;
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
		GeneralOperations generalOperations = new pm160695_GeneralOperations();
		pm160695_ArticleOperations articleOperations = new pm160695_ArticleOperations();
		ShopOperations shopOperations = new pm160695_ShopOperations();
		BuyerOperations buyerOperations = new pm160695_BuyerOperations();
		
		Calendar currentTime = generalOperations.getCurrentTime();
		int buyerId = this.getBuyer(orderId);
		
		// dohvatamo listu svih proizvoda u porudzbini
		List<Integer> articlesInOrder = this.getItems(orderId);
		Set<Integer> shopsInOrder = new HashSet<>();
		
		// za svaki proizvod u porudzbini dohvatamo prodavnicu kojoj pripada i stavljamo njen id u set
		for (Integer articleId : articlesInOrder) {
			shopsInOrder.add(articleOperations.getShop(articleId));
		}
		Set<Integer> cities = new HashSet<>();
		// za svaku prodavnicu iz seta dohvatamo grad kom pripada i stavljamo njegov id u set
		for (Integer shopId : shopsInOrder) {
			cities.add(shopOperations.getCity(shopId));
		}
		// dohvatamo grad kom pripada kupac
		int buyerCityId = buyerOperations.getCity(buyerId);
		int shortestCityId = -1;
		int shortestCityDistance = Integer.MAX_VALUE;
		int longestCityDistance = -1;
		List<ShortestPathNode> shortestPath = null;
		List<ShortestPathNode> actualPath = null;
		for (Integer cityId : cities) {
			// za sve gradove prodavnica iz porudzbine odredjujemo najkraci put do grada kupca i trazimo minimalan i maksimalan medju njima
			// minimalan put odredjuje "centralnu" prodavnicu iz koje ce porudzbina krenuti
			// maksimalan put odredjuje ukupno vreme potrebno da porudzbina stigne do kupca
			List<ShortestPathNode> sp = ShortestPathUtil.determineShortestPath(buyerCityId, cityId);
			ShortestPathNode node = sp.get(0);
			if (shortestCityDistance > node.getCost()) {
				shortestCityId = node.getCityId();
				shortestCityDistance = node.getCost();
				shortestPath = sp;
			}
			if (longestCityDistance < node.getCost()) {
				longestCityDistance = node.getCost();
				actualPath = sp;
			}
		}
		
		// prolazimo kroz actualPath i sumiramo udaljenosti dok ne dodjemo do pocetka shortestPath-a
		int distance = 0;
		ShortestPathNode centralNode = shortestPath.get(0);
		for (ShortestPathNode node : actualPath) {
			if (node.getCityId() == centralNode.getCityId()) break;
			distance += node.getDistance();
		}
		
		ShortestPathNode node = shortestPath.get(0);
		node.setDistance(node.getDistance() + distance);
		
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			try {
				connection.setAutoCommit(false);
				
				
				String updateOrderQuery = "update [Order] set state = ?, sentTime = ?, cityId = ? where id = ?";
				
				List<ParameterPair> orderUpdateParams = new LinkedList<>();
				orderUpdateParams.add(new ParameterPair("String", "sent"));
				orderUpdateParams.add(new ParameterPair("Calendar", Long.toString(currentTime.getTimeInMillis())));
				orderUpdateParams.add(new ParameterPair("int", Integer.toString(shortestCityId)));
				orderUpdateParams.add(new ParameterPair("int", Integer.toString(orderId)));
				
				String updateBuyerQuery = "update Buyer set balance = balance - ? where id = ?";
				
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
				
				String insertIntoOrderPathQuery = "insert into OrderPath (orderId, cityId, time, nextCityId) values (?, ?, ?, ?)";
				
				
				Integer retVal;
				
				for (ShortestPathNode spn : shortestPath) {
					List<ParameterPair> orderPathInsertParams = new LinkedList<>();
					orderPathInsertParams.add(new ParameterPair("int", Integer.toString(orderId)));
					orderPathInsertParams.add(new ParameterPair("int", Integer.toString(spn.getCityId())));
					orderPathInsertParams.add(new ParameterPair("int", Integer.toString(spn.getDistance())));
					orderPathInsertParams.add(new ParameterPair("int", Integer.toString(spn.getNextCityId())));
					retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, insertIntoOrderPathQuery, orderPathInsertParams);
					if (retVal == -1) {
						connection.rollback();
						return -1;
					}
				}
				
				// radimo update porudzbine
				retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateOrderQuery, orderUpdateParams);
				if (retVal == -1) {
					connection.rollback();
					return -1;
				}
				// radimo update novca kod kupca
				retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateBuyerQuery, buyerUpdateParams);
				if (retVal == -1) {
					connection.rollback();
					return -1;
				}
				// radimo insert u transakciju
				retVal = this.getStatementHandler().executeUpdateStatementAndGetId(connection, insertIntoTransactionQuery, transactionInsertParams);
				if (retVal == -1) {
					connection.rollback();
					return -1;
				}
				
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
			return 1;
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
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select cityId from [Order] where id = ?";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			
			Integer cityId = this.getStatementHandler().executeSelectStatement(connection, query, parameters, Integer.class);
			
			return cityId != null ? cityId.intValue() : -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int updateTimeForOrders() {
		List<Integer> orders = this.getUndeliveredOrders();
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String selectCityIdQuery = "select cityId from [Order] where id = ?";
			
			List<Integer> cities = new LinkedList<>();
			for (Integer orderId : orders) {
				// za svaku porudzbinu dohvatamo grad u kom se trenutno nalazi i dodajemo ga u listu gradova
				List<ParameterPair> parameters = new LinkedList<>();
				parameters.add(new ParameterPair("int", Integer.toString(orderId)));
				
				Integer cityId = this.getStatementHandler().executeSelectStatement(connection, selectCityIdQuery, parameters, Integer.class);
				cities.add(cityId);
			}
			
			String selectTimeQuery = "select time from OrderPath where orderId = ? and cityId = ?";

			Iterator<Integer> orderIterator = orders.iterator();
			Iterator<Integer> citiesIterator = cities.iterator();
			
			while (orderIterator.hasNext() && citiesIterator.hasNext()) {
				// idemo kroz liste gradova i porudzbina
				Integer orderId = orderIterator.next();
				Integer cityId = citiesIterator.next();
				List<ParameterPair> parameters = new LinkedList<>();
				parameters.add(new ParameterPair("int", Integer.toString(orderId)));
				parameters.add(new ParameterPair("int", Integer.toString(cityId)));

				// dohvatamo trenutno preostalo vreme
				Integer time = this.getStatementHandler().executeSelectStatement(connection, selectTimeQuery, parameters, Integer.class);
				if (time > 0) {
					time--;
				}
				
				// upisujemo novo vreme u OrderPath
				String updateOrderPathQuery = "update OrderPath set time = ? where orderId = ? and cityId = ?";
				
				List<ParameterPair> updateOrderPathParams = new LinkedList<>();
				updateOrderPathParams.add(new ParameterPair("int", time.toString()));
				updateOrderPathParams.add(new ParameterPair("int", Integer.toString(orderId)));
				updateOrderPathParams.add(new ParameterPair("int", Integer.toString(cityId)));
				
				this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateOrderPathQuery, updateOrderPathParams);
				
				if (time == 0) {
					// ako je vreme dostiglo 0, dohvatamo sledeci grad koji cemo upisati u Order
					String selectNextCityIdQuery = "select nextCityId from OrderPath where orderId = ? and cityId = ?";
					
					List<ParameterPair> selectNextCityIdParameters = new LinkedList<>();
					selectNextCityIdParameters.add(new ParameterPair("int", Integer.toString(orderId)));
					selectNextCityIdParameters.add(new ParameterPair("int", Integer.toString(cityId)));
					
					Integer nextCityId = this.getStatementHandler().executeSelectStatement(connection, selectNextCityIdQuery, selectNextCityIdParameters, Integer.class);
					if (nextCityId == -1) {
						// ako je nextCityId -1, upisujemo recieved time u Order kao current - 1 dan
						GeneralOperations generalOperations = new pm160695_GeneralOperations();
						Calendar currentTime = generalOperations.getCurrentTime();
						LocalDate recievedTime = LocalDateTime.ofInstant(currentTime.toInstant(), currentTime.getTimeZone().toZoneId()).toLocalDate().minusDays(1);
						
						String updateOrderQuery = "update [Order] set recievedTime = ? where id = ?";
						
						List<ParameterPair> updateOrderParams = new LinkedList<>();
						updateOrderParams.add(new ParameterPair("LocalDate", Long.toString(recievedTime.toEpochDay())));
						updateOrderParams.add(new ParameterPair("int", Integer.toString(orderId)));
						
						this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateOrderQuery, updateOrderParams);
					}
					else {
						// u suprotnom, upisujemo novi cityId u Order
						String updateOrderQuery = "update [Order] set cityId = ? where id = ?";
						
						List<ParameterPair> updateOrderParams = new LinkedList<>();
						updateOrderParams.add(new ParameterPair("int", Integer.toString(nextCityId)));
						updateOrderParams.add(new ParameterPair("int", Integer.toString(orderId)));
						
						this.getStatementHandler().executeUpdateStatementAndGetId(connection, updateOrderQuery, updateOrderParams);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}
	
	private boolean hasExtraDiscount(int buyerId) {
		pm160695_TransactionOperations transactionOperations = new pm160695_TransactionOperations();
		
		return transactionOperations.getTransactionAmountLastThirtyDaysForBuyer(buyerId).compareTo(BigDecimal.valueOf(10000)) >= 0;
	}
	
	private BigDecimal calculateFinalPrice(int orderId) {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String callQuery = "{CALL SP_FINAL_PRICE (?, ?) }";
			
			List<ParameterPair> parameters = new LinkedList<>();
			parameters.add(new ParameterPair("int", Integer.toString(orderId)));
			parameters.add(new ParameterPair("BigDecimal", BigDecimal.ZERO.toString()));
			
			BigDecimal finalPrice = this.getStatementHandler().executeCallableStatement(connection, callQuery, parameters, BigDecimal.class);
			
			return finalPrice.setScale(3);
		} catch (SQLException e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
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
	
	private List<Integer> getUndeliveredOrders() {
		try (Connection connection = DriverManager.getConnection(this.getConnectionString())) {
			String query = "select id from [Order] where recievedTime is null";
			
			return this.getStatementHandler().executeSelectListStatement(connection, query, null, Integer.class);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
