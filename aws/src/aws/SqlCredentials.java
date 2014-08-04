package aws;

public class SqlCredentials {

	String url;
	String port;
	String databaseName;
	String user;
	String password;

	public SqlCredentials(String url, String port, String databaseName,
			String user, String password) {
		super();
		this.url = url;
		this.port = port;
		this.databaseName = databaseName;
		this.user = user;
		this.password = password;
	};

}
