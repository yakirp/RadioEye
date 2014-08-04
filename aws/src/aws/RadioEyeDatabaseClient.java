package aws;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RadioEyeDatabaseClient implements RadioEyeDataBaseApi {

	private static SqlCredentials RADIO_EYE_AWS_POSTGRESQL_CREDENTIALS = new SqlCredentials(
			"radioeye.cnual8ofd0ui.us-east-1.rds.amazonaws.com", "5432",
			"radioEye", "root", "rootroot");

	public static SqlConnecion AWS_POSTGRESQL_RDS_CONNECTION = new PostgresqlSqlConnecion()
			.connect(RADIO_EYE_AWS_POSTGRESQL_CREDENTIALS);

	private SqlConnecion conn;

	public static RadioEyeDatabaseClient setConnetcion(
			SqlConnecion conn) {
		return new RadioEyeDatabaseClient(conn);
	}
	
	@Override
	public List<Map<String, Object>> getAllPublisherImages(String publisherId)
			throws SQLException {
		return getConn().executeQuery(
				"SELECT * FROM images WHERE data->>'facebookId' ='%s'",
				publisherId);

	}
	

	private RadioEyeDatabaseClient(SqlConnecion conn) {
		super();
		setConn(conn);
	}

	 

	private SqlConnecion getConn() {
		return conn;
	}

	private void setConn(SqlConnecion conn) {
		this.conn = conn;
	}

}
