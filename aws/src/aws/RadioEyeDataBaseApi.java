package aws;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface RadioEyeDataBaseApi {

	public List<Map<String, Object>> getAllPublisherImages(String publisherId) throws SQLException;

}
