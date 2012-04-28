package docketplace.stocktakr.webservice;

import org.json.*;

import docketplace.stocktakr.data.*;

import android.os.Message;
import android.util.Log;


public class DownloadProducts extends WebServiceAction {
	public DownloadProducts(TransferHandler handler) {
		super(handler);
	}
	
	private int getProductCount() {
		JSONObject json = rest.get("ItemCount");
		
		int count = 0;
		
		try {
			if (json != null) {
				count = json.getInt("itemCount");

			} else {
				Log.d("DOWNLOAD", "null count JSON");
			}
		} catch (JSONException je) {
			Log.d("DOWNLOAD", "JSON exception: " + je.getMessage());
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
				
				json = rest.get("Items", start + "/" + count);
				
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
		settings = Database.getSettings();
		
		rest = new REST(handler, settings.storeID, settings.password);
		
		
		sendMessage(TransferHandler.START);
		
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
			
			sendMessage(TransferHandler.BEGIN, count);
			
			while (i < count) {
				if ((i + blockSize) <= count) {
					limit  = blockSize;
				} else  {
					limit = (count - i);
				}
				
				success = getProducts(i, limit);
				
				if (!success) break;
				
				i += blockSize;
				
				sendMessage(TransferHandler.TRANSFER, i);
			}
			
			if (success) {
				sendMessage(TransferHandler.COMPLETE);
			} else  {
				sendMessage(TransferHandler.ERROR);
			}
		}
	}
}
