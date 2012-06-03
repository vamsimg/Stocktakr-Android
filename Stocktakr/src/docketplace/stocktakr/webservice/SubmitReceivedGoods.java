package docketplace.stocktakr.webservice;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import docketplace.stocktakr.components.SubmissionListener;
import docketplace.stocktakr.data.Database;
import docketplace.stocktakr.data.PurchaseOrderItem;
import docketplace.stocktakr.data.ReceivedGoodsItem;

public class SubmitReceivedGoods extends WebServiceAction {
	private String personName;
	private SubmissionListener listener;
	
	public SubmitReceivedGoods(TransferHandler handler, String personName, Context now, SubmissionListener listener ) 
	{	
		super(handler);
		this.personName = personName;	
		this.listener = listener;
	}	
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	
	public static String currentTimestamp() 
	{
		 return formatter.format(new Date());
	}
	
	private JSONObject buildItem(ReceivedGoodsItem  item) throws JSONException {
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
		
		for (ReceivedGoodsItem item : Database.getAllReceivedGoodsItems()) 
		{
			json.put(buildItem(item));
		}

		return json;
	}
	
	@Override
	public void run() {
		
		Log.d("SUBMIT RG", "beginning");
			
		sendMessage(TransferHandler.START);
				
		try 
		{		
			Log.d("SUBMIT RG", "posting");
			String deviceDetails = URLEncoder.encode((Build.BRAND + "$" + Build.MODEL  + "$" + Build.VERSION.SDK_INT).replaceAll(" ",""));
			String encodedPerson = URLEncoder.encode(personName.replace(' ', '_'));
			String orderDate = URLEncoder.encode(currentTimestamp());
			
			int responseCode = rest.post("ReceivedGoodsItems", deviceDetails + "/" + encodedPerson + "/" + orderDate , buildItemList().toString());
			
			Log.d("SUBMIT RG", "posted");
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				Log.d("SUBMIT RG", "success");
				Database.deleteAllReceivedGoodsItems();				
				sendMessage(TransferHandler.COMPLETE);
				listener.submitComplete();
			}
			
		} catch (JSONException e) {
			Log.d("SUBMIT RG", "Exception: " + e.getMessage());
			
			
			sendMessage(TransferHandler.ERROR);
		}
		
		Log.d("SUBMIT RG", "exiting thread");
	}
}
