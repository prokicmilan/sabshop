package student;

public abstract class OperationImplementation {

	private StatementHandler statementHandler;
	private String connectionString;
	
	public OperationImplementation() {
		this.statementHandler = StatementHandler.getInstance();
		this.connectionString = ResourceManager.getConnectionString();
	}

	public StatementHandler getStatementHandler() {
		return statementHandler;
	}

	public String getConnectionString() {
		return connectionString;
	}
}
