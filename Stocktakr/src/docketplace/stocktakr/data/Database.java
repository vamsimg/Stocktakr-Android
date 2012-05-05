package docketplace.stocktakr.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import docketplace.stocktakr.R;
import docketplace.stocktakr.webservice.TransferHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Message;
import android.util.Log;

public class Database {  
    	
    public static SQLiteDatabase db;

    private static DatabaseHelper helper;
    
    private static Context context;

    public static void open(Context context) {
    	Database.context = context;
    	
        helper = new DatabaseHelper(context);

        db = helper.getWritableDatabase();     
    }

    public static void close() {
        db.close();
    }
    
    //Settings CRUD
    
    public static Settings getSettings() {
    	Cursor results = db.query("settings", new String[] {"store_id", "password", "set_quantity"}, null, null, null, null, null);
    	
    	Settings settings = new Settings();
    	
    	if (results.getCount() > 0) {
    		results.moveToFirst();
    		
    		Log.d("LOAD SETTINGS", "Set quantity after scan: " + results.getString(2));
    		
    		settings.storeID     = results.getString(0);
    		settings.password    = results.getString(1);
    		settings.setQuantity = (results.getInt(2) != 0);
    	}
    	results.close();
    	
    	return settings;
    }
    
    public static void saveSettings(String storeID, String password, boolean setQuantity) {
    	ContentValues values = new ContentValues();
    	
    	values.put("store_id", storeID);
    	values.put("password", password);
    	values.put("set_quantity", setQuantity);
    	
    	Database.db.update("settings", values, null, null);
    }
    
    
    //Products CRUD
    
    public static Product findProduct(String barcode) 
    {
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
  
    
    private static void sendMessage(TransferHandler handler, int type, int arg) 
    {
		Message message = handler.obtainMessage(type, arg, 0);
		
		handler.sendMessage(message);
	}
	
    
    public static boolean insertProductList(int itemCount , int start, JSONArray items, TransferHandler handler)
    {    	
    	boolean success = false;
    	Database.db.beginTransaction();
		try 
		{
			SQLiteStatement insert = Database.db.compileStatement("INSERT INTO products (code, barcode, description, sale_price) VALUES(?,?,?,?)");
			
			JSONObject item;
			
			for (int i = 0; i < itemCount; i++) 
			{
				
				item = items.getJSONObject(i);
				
				String code = item.getString("product_code");
				String barcode = item.getString("product_barcode");
				String description = item.getString("description");
				String salePrice = item.getString("sale_price");
				
				insert.bindString(1, code);
				insert.bindString(2, barcode);
				insert.bindString(3, description);
				insert.bindString(4, salePrice);				
												
				insert.execute();
				insert.clearBindings();
				
				sendMessage(handler, TransferHandler.TRANSFER, i+start);
			}
			
			Database.db.setTransactionSuccessful();
			success = true;
		}
		
		catch (Exception e) 
		{
		    String errMsg = (e.getMessage() == null) ? "bulkInsert failed" : e.getMessage();
		    Log.e("bulkInsert:", errMsg);
		    
		}
		finally 
		{
			Database.db.endTransaction();
		}  
		return success;
    }    

    
    public static void deleteAllProducts() 
	{		
		db.delete("products", null, null);		
	}
    	
    
    //Stock Records CRUD
    
    public static void addStockRecord(StockRecord record) 
    {
        ContentValues values = new ContentValues();
        values.put("code", record.code);
        values.put("barcode", record.barcode); 
        values.put("description", record.description);
        values.put("quantity", record.quantity);
        values.put("timestamp", record.timestamp);
        
        // Inserting Row
        db.insert("stockRecords", null, values);       
    }
    

    public static StockRecord getStockRecord(String code) 
    {        
        StockRecord record = null;
        
        Cursor cursor = db.query("stockRecords", new String[] { "code", "barcode", "description", "quantity", "timestamp"}, "code= ?", new String[] {code} , null, null, null);
        
        if(cursor.moveToFirst())
        {
        	record = new StockRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getString(4));
        }
        cursor.close();
         
        return record;
    }
    

    public static List<StockRecord> getAllStockRecords() 
    {
        List<StockRecord> recordList = new ArrayList<StockRecord>();
        // Select All Query
        String selectQuery = "SELECT  * FROM stockRecords";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) 
        {
            do 
            {
            	 StockRecord record = new StockRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getString(4));
                // Adding contact to list
            	 recordList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return recordList;
    }
        

	public static int getStockRecordCount() 
	{
         String countQuery = "SELECT  * FROM stockRecords";    
         Cursor cursor = db.rawQuery(countQuery, null);
         int count  = cursor.getCount(); 
         cursor.close();
         // return count
         return count; 
    }
	
	public static int updateStockRecord(StockRecord record) 
	{	    
	    ContentValues values = new ContentValues();
	    values.put("code", record.code);
        values.put("barcode", record.barcode); 
        values.put("description", record.description);
        values.put("quantity", record.quantity);
        values.put("timestamp", record.timestamp);	    
        
        int rowsAffected = db.update("stockRecords", values, "code = ?", new String[] {record.code});
	    return rowsAffected ;
	}
	
	public static void deleteRecord(StockRecord record) 
	{		
		db.delete("stockRecords", "code = ?", new String[] {record.code});	
	}
	
	public static void deleteAllRecords() 
	{		
		db.delete("stockRecords", null, null);		
	}

}
