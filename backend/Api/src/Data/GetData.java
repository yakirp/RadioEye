package Data;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubSyncedObject;


//http://localhost:8080/RadioEyeApi/rest/get/table/users.yakir
//Sets the path to base URL + /get
@Path("/get")
public class GetData {
    
	@GET
	@Path("/{objectId}/{path}")
	public Response getMsg(@PathParam("objectId") String objectId,
			@PathParam("path") String path) throws org.json.JSONException {
  
		 

		Pubnub pubnub = new Pubnub("pub-69159aa7-3bcf-4d09-ae25-3269f14acb6a",
				"sub-4d81bf51-1eb6-11e1-82b2-3d61f7276a67");
		pubnub.setCacheBusting(false);
		pubnub.setOrigin("pubsub-beta");
		
		final PubnubSyncedObject myData = pubnub.createSyncObject(objectId,
				path);

		try {
			myData.initSync(new Callback() {

				// Called when the initialization process connects the ObjectID
				// to PubNub
				@Override
				public void connectCallback(String channel, Object message) {
					System.out.println("Object Initialized : " + message);
					 
						try {
							System.out.println(myData.toString(2));
						} catch (org.json.JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 
				}

				// Called every time ObjectID is changed, starting with the
				// initialization process
				// that retrieves current state of the object
				@Override
				public void successCallback(String channel, Object message) {
					System.out.println(message);
					try {
						System.out.println(myData.toString(1));
					} catch (org.json.JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Called whenever any error occurs
				@Override
				public void errorCallback(String channel, PubnubError error) {
					System.out.println(System.currentTimeMillis() / 1000
							+ " : " + error);
				}

			});
		} catch (PubnubException e) {
			e.printStackTrace();
		}

	 
		return Response.status(200).entity("").build();

	}
}
