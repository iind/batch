package com.fieldaware.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TestRestClient {

	private RestClient rc;
	
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetMethod() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException{
		rc = new RestClient("https://api.fieldaware.net/job","Token aa5f0cc2f69e4917bf9fa0e1e3d9c205");
		JsonElement root =rc.getMethod("/?start=2015-02-06T16%300&end=2015-02-28T16%300&state=completed");
		String json = prettyGson.toJson(root);
		Long count = root.getAsJsonObject().get("count").getAsLong();
		for(int i=0;i<count;i++){
			System.out.println(root.getAsJsonObject().get("items").getAsJsonArray().get(i).getAsJsonObject().get(("jobId")));
		}
		System.out.println(json);
	}
	
	@Test
	public void testGetJobDetails() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException{
		rc = new RestClient("https://api.fieldaware.net/job","Token aa5f0cc2f69e4917bf9fa0e1e3d9c205");
		JsonElement root =rc.getMethod("/J99");
		JsonArray tasks = root.getAsJsonObject().get("tasks").getAsJsonArray(); 
		int tasksCount = tasks.size();
		for(int t=0; t<tasksCount; t++){
			Double totalPrice = 0d;
			JsonArray items = tasks.get(t).getAsJsonObject().get("items").getAsJsonArray();
			int itemCount = items.size();
			for(int i=0;i<itemCount; i++){
				System.out.println( items.get(i).getAsJsonObject().get("item").getAsJsonObject().get("name"));
				JsonObject item =items.get(i).getAsJsonObject().get("item").getAsJsonObject();
				items.get(i).getAsJsonObject().add("unitPrice", prettyGson.fromJson("6.00", JsonElement.class));
				totalPrice = totalPrice + (items.get(i).getAsJsonObject().get("quantity").getAsDouble()
						* items.get(i).getAsJsonObject().get("unitPrice").getAsDouble());
			}
			tasks.get(t).getAsJsonObject().add("unitPrice",prettyGson.fromJson(totalPrice.toString(),JsonElement.class));
		}
		
		String json = prettyGson.toJson(root);
		System.out.println(json);
		
		json = prettyGson.toJson(rc.postMethod("/J99", root.toString()));
		System.out.println(json);
	}
	
	@Test
	public void testGetCustomerName() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException{
		rc = new RestClient("https://api.fieldaware.net/job","Token aa5f0cc2f69e4917bf9fa0e1e3d9c205");
		JsonElement root =rc.getMethod("/J99");
		JsonObject cust = root.getAsJsonObject().get("customer").getAsJsonObject(); 
		System.out.println(cust.get("name"));
		
	}
	
	
}
