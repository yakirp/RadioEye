package aws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresqlSqlConnecion implements SqlConnecion {

	private Connection connection;

	@Override
	public List<Map<String, Object>> executeQuery(String query, Object... args)
			throws SQLException {

		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(String.format(query, args));

		List<Map<String, Object>> results = null;
		results = map(rs);

		rs.close();
		st.close();

		return results;

	}

	 

	@Override
	public SqlConnecion connect(SqlCredentials postgresqlCredentials) {
		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			// throw new Exception("Connection failed.");

		}

		connection = null;

		try {

			connection = DriverManager.getConnection(String.format(
					"jdbc:postgresql://%s:%s/%s", postgresqlCredentials.url,
					postgresqlCredentials.port,
					postgresqlCredentials.databaseName),
					postgresqlCredentials.user, postgresqlCredentials.password);

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");

			// throw new Exception(e.getMessage());

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
			return this;
		} else {
			System.out.println("Failed to make connection!");
			// throw new Exception("Connection failed.");
		}
		return null;
	}
	
	private List<Map<String, Object>> map(ResultSet rs) throws SQLException {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			if (rs != null) {
				ResultSetMetaData meta = rs.getMetaData();
				int numColumns = meta.getColumnCount();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<String, Object>();
					for (int i = 1; i <= numColumns; ++i) {
						String name = meta.getColumnName(i);
						Object value = rs.getObject(i);
						row.put(name, value);
					}
					results.add(row);
				}
			}
		} finally {
			rs.close();
		}
		return results;
	}

}
