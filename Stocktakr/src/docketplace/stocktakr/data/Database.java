package docketplace.stocktakr.data;

import java.util.ArrayList;

import docketplace.stocktakr.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
    public static ArrayList<StockRecord> stock;

    public static SQLiteDatabase db;

    private static DatabaseHelper helper;
    
    private static Context context;

    public static void open(Context context) {
    	Database.context = context;
    	
        helper = new DatabaseHelper(context);

        db = helper.getWritableDatabase();

        stock = new ArrayList<StockRecord>();
    }

    public static void close() {
        db.close();
    }
    
    public static AppSettings getSettings() {
    	Cursor results = db.query("settings", new String[] {"store_id", "password", "set_quantity"}, null, null, null, null, null);
    	
    	AppSettings settings = new AppSettings();
    	
    	if (results.getCount() > 0) {
    		results.moveToFirst();
    		
    		Log.d("LOAD SETTINGS", "Set quantity after scan: " + results.getString(2));
    		
    		settings.storeID     = results.getString(0);
    		settings.password    = results.getString(1);
    		settings.setQuantity = (results.getInt(2) != 0);
    	}
    	
    	return settings;
    }
    
    public static void saveSettings(String storeID, String password, boolean setQuantity) {
    	ContentValues values = new ContentValues();
    	
    	values.put("store_id", storeID);
    	values.put("password", password);
    	values.put("set_quantity", setQuantity);
    	
    	Database.db.update("settings", values, null, null);
    }
    
    public static Product findProduct(String barcode) {
		Product product = null;
		
		String search = barcode.trim();
		
		if (!search.equals("")) {
			Cursor results = Database.db.query("products", new String[] {"code", "barcode", "description", "sale_price"}, "(barcode = ?)", new String[] {search}, null, null, null);

			if (results.getCount() > 0) {
				product = new Product();
				
				results.moveToFirst();

				product.code        = results.getString(0);
				product.barcode     = results.getString(1);
				product.description = results.getString(2);

				try {
					Float price = Float.parseFloat(results.getString(3));
					
					product.price = String.format("%1$,.2f", price);
				} catch (NumberFormatException nfe) {
					product.price = context.getString(R.string.price_error);
				}
			}

			results.close();
		}
		
		return product;
    }
}
