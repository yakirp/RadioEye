package aws;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SqlConnecion {

	public SqlConnecion connect(SqlCredentials postgresqlCredentials) ;
	
	public List<Map<String, Object>> executeQuery(String query, Object... args)
			throws SQLException;
}
