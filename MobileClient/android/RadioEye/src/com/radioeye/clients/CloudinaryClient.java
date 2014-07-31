package com.radioeye.clients;

import java.util.HashMap;
import java.util.Map;

import com.cloudinary.Cloudinary;



public class CloudinaryClient {
   
	private static final String AI_SECRET = "RzZU6gdbyVMBQjvDLF701Q7hWFQ";
	private static final String API_KEY = "528447269881241";
	private static final String CLOUD_NAME = "dhwxfvlrn";
	private static CloudinaryClient instance;
	private Cloudinary cloudinaryClient;
  
	public static CloudinaryClient getInstance() {
		if (instance == null) {
			return new CloudinaryClient();
		}
		return instance;
	} 

	private CloudinaryClient() {
		super();
		Map<String, String> config = new HashMap<String, String>();
		config.put("cloud_name", CLOUD_NAME);
		config.put("api_key", API_KEY);
		config.put("api_secret", AI_SECRET);
		setCloudinaryClient(new Cloudinary(config));
	}

	public String generateWebPUrl(String imageId) {
		return getCloudinaryClient().url().generate(imageId + ".webp");
	}

	public Cloudinary getCloudinaryClient() {
		return cloudinaryClient;
	}

	public void setCloudinaryClient(Cloudinary cloudinaryClient) {
		this.cloudinaryClient = cloudinaryClient;
	}

}
