package docketplace.stocktakr.webservice;
import java.io.*;
import java.net.*;

import org.json.*;

import android.os.Message;
import android.util.Log;

public class REST {
	protected static final String WebServiceURL = "http://testdocketc.web705.discountasp.net/MobileItemHandler/"; 
	
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
	
	public void post(String request, JSONObject json) {
		URL url;
		
		HttpURLConnection conn;
		
		OutputStream out;
		
		byte[] data = json.toString().getBytes();
		
		try {
			url = new URL(request);
			
			conn = (HttpURLConnection) url.openConnection();
	
			conn.setRequestProperty("accept", "application/json");
			
			conn.setRequestProperty("content-length", String.valueOf(data.length));
			
			conn.setDoOutput(true);
			
			out = conn.getOutputStream();
			
			conn.connect();
			
			//tracker.begin(data.length);
			
			int count;
				
			int blockSize = 1000;
			
			for (int i = 0; i < data.length; i += blockSize) {
				if ((i + blockSize) <= data.length) {
					count = blockSize;
				} else {
					count = data.length - i;
				}
				
				out.write(data, i, count);
				
				out.flush();
				
				//tracker.track(i);
			}
			
			out.close();
			
			int responseCode = conn.getResponseCode();
				
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("Error");

				//tracker.error();
				
				return;
			} else {
				//tracker.complete();
			}
			
			conn.disconnect();
		} catch (MalformedURLException mue) {
			//tracker.error();
			
			System.err.println("malformed url");
		} catch (IOException ioe) {
			//tracker.error();
			
			System.err.println("connection error");
		}
	}
}
