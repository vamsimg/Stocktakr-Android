package docketplace.stocktakr.webservice;


import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.*;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import docketplace.stocktakr.data.*;


public class SubmitPurchaseOrder extends WebServiceAction {
	private String personName;
	
	public SubmitPurchaseOrder(TransferHandler handler, String personName, Context now) 
	{	
		super(handler);
		this.personName = personName;		
	}	
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String currentTimestamp() 
	{
		 return formatter.format(new Date());
	}
	
	private JSONObject buildItem(PurchaseOrderItem item) throws JSONException {
		JSONObject json = new JSONObject();		
		
		json.put("product_code", item.code);
		json.put("product_barcode", item.barcode);
		json.put("description", item.description);
		json.put("quantity", item.quantity);		
		
		return json;
	}
	
	private JSONArray buildItemList() throws JSONException 
	{
		JSONArray json = new JSONArray();
		
		for (PurchaseOrderItem item : Database.getAllPurchaseOrderItems()) 
		{
			json.put(buildItem(item));
		}

		return json;
	}
	
	@Override
	public void run() {
		
		Log.d("SUBMIT PO", "beginning");
			
		sendMessage(TransferHandler.START);
				
		try 
		{		
			Log.d("SUBMIT PO", "posting");
			String deviceDetails = URLEncoder.encode((Build.BRAND + "$" + Build.MODEL  + "$" + Build.VERSION.SDK_INT).replaceAll(" ",""));
			String encodedPerson = URLEncoder.encode(personName);
			String orderDate = URLEncoder.encode(currentTimestamp());
			
			int responseCode = rest.post("PurchaseOrderItems", deviceDetails + "/" + encodedPerson + "/" + orderDate , buildItemList().toString());
			
			Log.d("SUBMIT PO", "posted");
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				Log.d("SUBMIT PO", "success");
				Database.deleteAllPurchaseOrderItems();				
				sendMessage(TransferHandler.COMPLETE);
			}
			
		} catch (JSONException e) {
			Log.d("SUBMIT PO", "Exception: " + e.getMessage());
			
			
			sendMessage(TransferHandler.ERROR);
		}
		
		Log.d("SUBMIT PO", "exiting thread");
	}
}
