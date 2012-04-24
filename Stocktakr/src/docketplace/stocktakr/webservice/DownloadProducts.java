package docketplace.stocktakr.webservice;

import org.json.*;

import docketplace.stocktakr.data.*;

import android.os.Message;
import android.util.Log;


public class DownloadProducts extends Thread {
	private TransferHandler handler;
	
	private REST rest;
	
	private Settings settings;
	
	public DownloadProducts(TransferHandler handler) {
		this.handler = handler;
	}
	
	private void sendMessage(TransferHandler handler, int type) {
		Message message = handler.obtainMessage(type);
		
		handler.sendMessage(message);
	}
	
	private void sendMessage(TransferHandler handler, int type, int arg) {
		Message message = handler.obtainMessage(type, arg, 0);
		
		handler.sendMessage(message);
	}
	
	private int getProductCount() {
		//Log.d("DOWNLOAD", "getting JSON...");
		
		JSONObject json = rest.get("http://testdocketc.web705.discountasp.net/MobileItemHandler/ItemCount/" + settings.authentication());
		
		//Log.d("DOWNLOAD", "got JSON");
		
		int count = 0;
		
		try {
			if (json != null) {
				//Log.d("DOWNLOAD", "not-null count JSON");
				
				count = json.getInt("itemCount");
				
				//Log.d("DOWNLOAD", "Total: " + count);
			} else {
				//Log.d("DOWNLOAD", "null count JSON");
			}
		} catch (JSONException je) {
			//Log.d("DOWNLOAD", "JSON exception: " + je.getMessage());
		}
		
		return count;
	}
	
	private boolean getProducts(int start, int count) {
		JSONObject json;
		
		boolean success = false;
		
		try {
			json = null;
			
			if (count > 0) {
				//Log.d("DOWNLOAD", "Begin product download");
				
				json = rest.get("http://testdocketc.web705.discountasp.net/MobileItemHandler/Items/" + settings.authentication() + "/" + start + "/" + count);
				
				//Log.d("DOWNLOAD", "End product download");
			} else {
				//Log.d("DOWNLOAD", "No products");
			}
			
			if (json != null) {
				//Log.d("DOWNLOAD", "not-null product JSON");
				
				JSONArray items = json.getJSONArray("localItems");
				
				JSONObject item;
				
				for (int i = 0; i < count; i++) {
					item = items.getJSONObject(i);
					
					String code = item.getString("product_code");
					String barcode = item.getString("product_barcode");
					String description = item.getString("description");
					String salePrice = item.getString("sale_price");
					String quantity = item.getString("quantity");
					boolean isStatic = item.getBoolean("is_static");
					String modified = item.getString("modified_datetime");
					
					////Log.d("DOWNLOAD", "Product " + i + ": " + code + ", " + barcode + ", " + description + ", " + quantity + ", " + salePrice + ", " + isStatic + ", " + modified);
					
					Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('" + code + "','" + barcode + "','" + description + "','" + salePrice + "','" + quantity + "','" + isStatic + "','" + modified + "');");
				}
				
				//Log.d("DOWNLOAD", "stored products: "  + count);
				
				success = true;
			} else {
				//Log.d("DOWNLOAD", "null product JSON");
			}
		} catch (JSONException je) {
			//Log.d("DOWNLOAD", "JSON exception: " + je.getMessage());
		}
		
		return success;
	}
	
	@Override
	public void run() {
		rest = new REST();
		
		settings = Database.getSettings();
		
		
		sendMessage(handler, TransferHandler.START);
		
		int count = getProductCount();
		
		int i = 0;
		
		int blockSize = 250;
		
		int limit;
		
		boolean success = true;
		
		if (count > 0) {
			Log.d("DOWNLOAD", "Product count: " + count);
			
			/*if (count > 1000) {
				count = 1000;
			}*/
			
			Database.db.execSQL("DELETE FROM products;");
			
			sendMessage(handler, TransferHandler.BEGIN, count);
			
			while (i < count) {
				if ((i + blockSize) <= count) {
					limit  = blockSize;
				} else  {
					limit = (count - i);
				}
				
				success = getProducts(i, limit);
				
				if (!success) break;
				
				i += blockSize;
				
				sendMessage(handler, TransferHandler.TRANSFER, i);
			}
			
			if (success) {
				sendMessage(handler, TransferHandler.COMPLETE);
			} else  {
				sendMessage(handler, TransferHandler.ERROR);
			}
		}
	}
}
