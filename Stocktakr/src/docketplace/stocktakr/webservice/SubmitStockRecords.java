package docketplace.stocktakr.webservice;

import java.net.HttpURLConnection;

import org.json.*;

import android.util.Log;

import docketplace.stocktakr.data.*;


public class SubmitStockRecords extends WebServiceAction {
	private String personName;
	
	public SubmitStockRecords(TransferHandler handler, String personName) {	
		super(handler);
		
		this.personName = personName;
	}
	
	private JSONObject buildStockRecord(StockRecord record) throws JSONException {
		JSONObject json = new JSONObject();		
		
		json.put("product_code", record.code);
		json.put("product_barcode", record.barcode);
		json.put("description", record.description);
		json.put("quantity", record.quantity);
		json.put("person", personName);
		json.put("stocktake_datetime", record.timestamp);
		
		return json;
	}
	
	private JSONArray buildStockList() throws JSONException {
		JSONArray json = new JSONArray();
		
		for (StockRecord record : Database.stock) {
			json.put(buildStockRecord(record));
		}

		return json;
	}
	
	@Override
	public void run() {
		JSONObject json = new JSONObject();
		
		
		Log.d("SUBMIT", "beginning");
		
		sendMessage(TransferHandler.START);
		

		try {
			json.put("transactions", buildStockList());
			
			Log.d("SUBMIT", "JSON: " + json.toString(2));
			
			Log.d("SUBMIT", "posting");
			
			int responseCode = rest.post("StocktakeTransactions", json);
			
			Log.d("SUBMIT", "posted");
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				Log.d("SUBMIT", "success");
				
				sendMessage(TransferHandler.COMPLETE);
				
				Database.stock.clear();
			}
		} catch (JSONException e) {
			Log.d("SUBMIT", "Exception: " + e.getMessage());
			
			
			sendMessage(TransferHandler.ERROR);
		}
		
		Log.d("SUBMIT", "exiting thread");
	}
}
