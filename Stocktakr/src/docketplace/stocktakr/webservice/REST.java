package docketplace.stocktakr.webservice;
import java.io.*;
import java.net.*;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
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
	protected static final String WebServiceURL = "http://testdocketc.web705.discountasp.net/MobileItemHandler/"; 
	
	//protected static final String WebServiceURL = "http://posttestserver.com/post.php/";
	
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
	
	private void sendMessage(int type, int arg) {
		Message message = handler.obtainMessage(type, arg, 0);
		
		handler.sendMessage(message);
	}
	
	public JSONObject get(String request, String params) {
		return getFullURL(WebServiceURL + request + "/" + storeID + "/" + password + "/" + params);		
	}
	
	public JSONObject get(String request) {
		return getFullURL(WebServiceURL + request + "/" + storeID + "/" + password);
	}
	
	public JSONObject getFullURL(String request) {
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
				
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("Error");

				//sendMessage(handler, TransferHandler.ERROR);
				//tracker.error();
				
				return null;
			}
				
			total = conn.getContentLength();
			
			//sendMessage(handler, TransferHandler.BEGIN, total);
			//tracker.begin(total);
			
			in = new InputStreamReader(conn.getInputStream());
			
			builder = new StringBuilder();
			
			int count;
			int progress = 0;
			
			char[] buffer = new char[1000];
				
			if (total > 0) {
				while ((count = in.read(buffer, 0, buffer.length)) > -1) {
					progress += count;
					
					//sendMessage(handler, TransferHandler.TRANSFER, progress);
					//tracker.track(progress);
					
					builder.append(buffer, 0, count);
				}
				
				//sendMessage(handler, TransferHandler.COMPLETE);
				//tracker.complete();
								
				json = new JSONObject(builder.toString());
			}
			
			in.close();
			
			conn.disconnect();
		} catch (MalformedURLException mue) {
			//tracker.error();
			//sendMessage(handler, TransferHandler.ERROR);
			
			Log.d("REST", "malformed url: " + mue.getMessage());
		} catch (IOException ioe) {
			//tracker.error();
			//sendMessage(handler, TransferHandler.ERROR);
			
			Log.d("REST", "connection error: " + ioe.getMessage());
		} catch (JSONException e) {
			//tracker.error();
			//sendMessage(handler, TransferHandler.ERROR);
			
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
	
	//WebServiceURL + request + "/" + storeID + "/" + password
	public int post(String request, String  data) {
		Log.d("REST POST", "posting to: " + WebServiceURL + request + "/" + storeID + "/" + password);
		
		return postFullURL(WebServiceURL + request + "/" + storeID + "/" + password, data);		
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
	
	
	
	/*public int postFullURL(String request, JSONObject json) {
		URL url;
		
		HttpURLConnection conn;
		
		OutputStream out;
		
		Log.d("REST POST", "getting bytes");
		
		byte[] data = "Hello World".getBytes();
		//byte[] data = json.toString().getBytes();
	
		Log.d("REST POST", "got bytes");
		
		try {
			url = new URL(request);
			
			conn = (HttpURLConnection) url.openConnection();
	
			conn.setRequestProperty("accept", "application/json");
			
			conn.setRequestProperty("content-length", String.valueOf(data.length));
			
			conn.setDoOutput(true);
			
			out = conn.getOutputStream();
			
			conn.connect();
			
			Log.d("REST POST", "begin: "  + data.length);
			
			sendMessage(TransferHandler.BEGIN, data.length);
			
			int count;
				
			int blockSize = 1000;
			
			for (int i = 0; i < data.length; i += blockSize) {
				Log.d("REST POST", "writing: " + i);
				
				if ((i + blockSize) <= data.length) {
					count = blockSize;
				} else {
					count = data.length - i;
				}
				
				out.write(data, i, count);
				
				out.flush();
				
				Log.d("REST POST", "flushed: " + i);
				
				sendMessage(TransferHandler.TRANSFER, i);
			}
			
			out.close();
			
			Log.d("REST POST", "closed otput stream");
			
			int responseCode = conn.getResponseCode();
			
			Log.d("REST POST", "Response Code: " + responseCode);
				
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("Error");

				sendMessage(TransferHandler.ERROR);
			}
			
			conn.disconnect();
			
			return responseCode;
		} catch (MalformedURLException mue) {
			//tracker.error();
			sendMessage(TransferHandler.ERROR);
			
			Log.d("REST POST", "malformed url");
		} catch (IOException ioe) {
			//tracker.error();
			sendMessage(TransferHandler.ERROR);
			
			Log.d("REST POST", "connection error");
		}
		
		return -1;
	}
	*/
}
