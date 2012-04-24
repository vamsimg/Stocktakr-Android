package docketplace.stocktakr.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

public class Database {
    public static ArrayList<StockRecord> stock;

    public static SQLiteDatabase db;

    //public static StockAdapter stockAdapter;

    private static DatabaseHelper helper;

    public static void open(Context context) {
        helper = new DatabaseHelper(context);

        db = helper.getWritableDatabase();

        stock = new ArrayList<StockRecord>();

        //StockAdapter stockAdapter = new StockAdapter(context, );
    }

    public static void close() {
        db.close();
    }
    
    public static Settings getSettings() {
    	Cursor results = db.query("settings", new String[] {"store_id", "password"}, null, null, null, null, null);
    	
    	Settings settings = new Settings();
    	
    	if (results.getCount() > 0) {
    		results.moveToFirst();
    		
    		settings.storeID  = results.getString(0);
    		settings.password = results.getString(1);
    	}
    	
    	return settings;
    }
    
    public Product findProduct(String barcode) {
		Product product = null;
		
		String search = barcode.trim();
		
		if (!search.equals("")) {
			Cursor results = Database.db.query("products", new String[] {"barcode", "description", "sale_price"}, "(barcode = ?)", new String[] {search}, null, null, null);

			if (results.getCount() > 0) {
				product = new Product();
				
				results.moveToFirst();

				product.barcode     = results.getString(0);
				product.description = results.getString(1);
				product.price       = results.getString(2);
			}

			results.close();
		}
		
		return product;
    }
}
