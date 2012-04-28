package docketplace.stocktakr.webservice;

import org.json.*;

import docketplace.stocktakr.components.*;
import android.util.Log;


public class DownloadProductInfo extends WebServiceAction {
	private String barcode;
	
	private ProductInfoListener listener;
	
	public DownloadProductInfo(TransferHandler handler, String barcode, ProductInfoListener listener) {
		super(handler);
		
		this.barcode = barcode;
		
		this.listener = listener;
	}
	
	@Override
	public void run() {
		Log.d("PRODUCT INFO", "running");
		
		Log.d("PRODUCT INFO", "got settings");
		
		sendMessage(TransferHandler.START_INDETERMINATE);
		
		Log.d("PRODUCT INFO", "sending message");
		
		JSONObject json = rest.get("ItemInfo/" + settings.authentication() + "/" + barcode);
		
		Log.d("PRODUCT INFO", "got response");
		
		if (json != null) {
			try {
				json.getJSONObject("localItem");
				
				listener.productDownloaded(barcode);
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}
		
		Log.d("PRODUCT INFO", "exiting thread");
	}
}
