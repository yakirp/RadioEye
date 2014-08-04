package aws;

import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws Exception {

		RadioEyeDatabaseClient gg = RadioEyeDatabaseClient
				.setConnetcion(RadioEyeDatabaseClient.AWS_POSTGRESQL_RDS_CONNECTION);

		List<Map<String, Object>> rs = gg.getAllPublisherImages("695432614");

		 
		
		
		for (Map<String, Object> map : rs) {

			System.err.println(map.get("data"));

		}

	}

}
