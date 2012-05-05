package docketplace.stocktakr.webservice;
import java.io.*;
import java.net.*;

import org.apache.http.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.*;

import android.os.Message;
import android.util.Log;

public class REST {
	//protected static final String WebServiceURL = "http://testdocketc.web705.discountasp.net/MobileItemHandler/"; 
	
	//protected static final String WebServiceURL = "http://posttestserver.com/post.php/";
	
	protected static final String WebServiceURL = "http://stocktakr.com/MobileItemHandler/";
	
	public TransferHandler handler;
	
	public String storeID, password;
	
	public REST(TransferHandler handler, String storeID, String password) {
		this.handler  = handler;
		this.storeID  = storeID;
		this.password = password;
	}
	
	private void sendMessage(int type) {
		Message message = handler.obtainMessage(type);
		
		handler.sendMessage(message);
	}
	
	public JSONObject get(String request, String params) {
		return getFullURL(WebServiceURL + request + "/" + storeID + "/" + password + "/" + params);		
	}
	
	public JSONObject get(String request) {
		return getFullURL(WebServiceURL + request + "/" + storeID + "/" + password);
	}
	
	public JSONObject getFullURL(String request) {
		Log.d("REST", "url: " + request);
		URL url;
		
		HttpURLConnection conn;
		
		InputStreamReader in;
		
		StringBuilder builder;
		
		JSONObject json = null;
		
		try {
			url = new URL(request);
			
			conn = (HttpURLConnection) url.openConnection();
	
			conn.setRequestProperty("accept", "application/json");
			
			conn.setDoOutput(false);
			
			conn.connect();
			
			int total = 0;
			
			int responseCode = conn.getResponseCode();
				
			if (responseCode != HttpURLConnection.HTTP_OK) 
			{
				System.out.println("Error");
				return null;
			}
				
			total = conn.getContentLength();
			

			in = new InputStreamReader(conn.getInputStream());
			
			builder = new StringBuilder();
			
			int count;
			
			char[] buffer = new char[1000];
				
			if (total > 0) {
				while ((count = in.read(buffer, 0, buffer.length)) > -1) 
				{
					
					builder.append(buffer, 0, count);
				}					
				json = new JSONObject(builder.toString());
			}
			
			in.close();
			
			conn.disconnect();
		} catch (MalformedURLException mue) {			
			Log.d("REST", "malformed url: " + mue.getMessage());
		} catch (IOException ioe) {			
			Log.d("REST", "connection error: " + ioe.getMessage());
		} catch (JSONException e) {			
			Log.d("REST", "malformed json: " + e.getMessage());
		}
		
		try {
			if (json != null) {
				Log.d("REST", "got JSON");
				
				String errorMessage = json.getString("errorMessage");
				
				if  ((errorMessage != null) && (!errorMessage.equals("null"))) {
					Log.d("REST", "error message: " + errorMessage);
					
					if (errorMessage.equals("NoStore")) {
						sendMessage(TransferHandler.INCORRECT_STOREID);
					} else if (errorMessage.equals("IncorrectPassword")) {
						sendMessage(TransferHandler.INCORRECT_PASSWORD);
					} else {
						Log.d("REST", "other error");
						
						sendMessage(TransferHandler.ERROR);
					}
				} else {
					Log.d("REST", "no error message");
					
					return json;
				}
			} else {
				Log.d("REST", "null JSON");
				
				sendMessage(TransferHandler.ERROR);
			}
		} catch (JSONException je) {
			Log.d("REST", "JSON exception");
			
			sendMessage(TransferHandler.ERROR);
		}
		
		return null;
	}
	
	
	public int post(String request,String params ,String  data) {
		Log.d("REST POST", "posting to: " + WebServiceURL + request + "/" + storeID + "/" + password + "/" + params);
		
		return postFullURL(WebServiceURL + request + "/" + storeID + "/" + password + "/" + params, data);		
	}
	
	public int postFullURL(String request, String data)
	{
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(request);

	    try {
	    	
	        // Add your data
	    		
	    	JSONObject json  = new JSONObject();
	    	json.put("transactions", data);
	    		    	
	    	StringEntity se = new StringEntity(json.toString(),HTTP.UTF_8);
	    	se.setContentType("application/json");
	        httppost.setEntity(se);

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        Log.d("REST POST", "Response Code: " + response.getStatusLine().getStatusCode() );
	        
	        if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
	        {
	        	sendMessage(TransferHandler.ERROR);	        
	        }
	        
	        
	        HttpEntity responseEntity = response.getEntity();
	        
	        JSONObject jsonResponse = new JSONObject(EntityUtils.toString(responseEntity));
	        
	        try {
				Log.d("REST", "got JSON");
				
				Boolean isError = jsonResponse.getBoolean("is_error");
				String errorMessage = jsonResponse.getString("errorMessage");
				
				if  (isError) {
					Log.d("REST", "error message: " + errorMessage);
					
					if (errorMessage.equals("NoStore")) {
						sendMessage(TransferHandler.INCORRECT_STOREID);
					} else if (errorMessage.equals("IncorrectPassword")) {
						sendMessage(TransferHandler.INCORRECT_PASSWORD);
					} else {
						Log.d("REST", "other error");
						
						sendMessage(TransferHandler.ERROR);
					}
				} else {
					Log.d("REST", "no error message");
					
					return response.getStatusLine().getStatusCode();
				}
			} catch (JSONException je) {
				Log.d("REST", "JSON exception");
				
				sendMessage(TransferHandler.ERROR);
			}
	        
	    } catch (Exception ex){
	    	sendMessage(TransferHandler.ERROR);
	    }    
	    	    
	    return -1;
	}
	
}
