package Data;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubSyncedObject;

//http://localhost:8080/RadioEyeApi/rest/set/table/users.yakir/name/yakir
//Sets the path to base URL + /set
@Path("/set")
public class SetData {

	private PubnubSyncedObject myData;

	@GET
	@Path("/{objectId}/{path}/{key}/{value}")
	public Response getMsg(@PathParam("objectId") String objectId,
			@PathParam("path") final String path, @PathParam("key") String key,
			@PathParam("value") String value) throws org.json.JSONException,
			PubnubException {

		Pubnub pubnub = new Pubnub("pub-69159aa7-3bcf-4d09-ae25-3269f14acb6a",
				"sub-4d81bf51-1eb6-11e1-82b2-3d61f7276a67");
		pubnub.setCacheBusting(false);
		pubnub.setOrigin("pubsub-beta");

		System.out.println(objectId);
		System.out.println(path);
		System.out.println(key);
		System.out.println(value);

		myData = pubnub.createSyncObject(objectId, path);

		final JSONObject j = new JSONObject();
		j.put(key, value);

		myData.initSync(new Callback() {

			@Override
			public void connectCallback(String arg0, Object arg1) {
				System.out.println("-------------------");
				set(path, myData, j);
				super.connectCallback(arg0, arg1);
			}
		});

		return Response.status(200).entity("").build();

	}

	private void set(String path, final PubnubSyncedObject myData, JSONObject j) {
		myData.set(path, j, new Callback() {

			// Called when this operation succeeds
			@Override
			public void successCallback(String channel, Object message) {
				System.out.println("set(): SUCCESS");

				System.out.println(message.toString());
				try {
					System.out.println(myData.toString(1));
				} catch (org.json.JSONException e) {

					e.printStackTrace();
				}

			}

			// Called if an error occurs
			@Override
			public void errorCallback(String channel, PubnubError error) {

				System.out.println(System.currentTimeMillis() / 1000 + " : "
						+ error);
			}

		});
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey";
	}

	// This method is called if XML is request
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	}

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}

}