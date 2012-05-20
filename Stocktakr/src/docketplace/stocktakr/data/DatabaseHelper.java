package docketplace.stocktakr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME    = "stocktakr";
	private static final int    DATABASE_VERSION = 5;
	
	
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create Settings table
		db.execSQL("CREATE TABLE settings (store_id  TEXT, password TEXT, set_quantity BOOLEAN)");
		
		// Insert default settings
		db.execSQL("INSERT INTO settings (store_id, password, set_quantity) VALUES(\'\', \'\', \'true\')");
		
		// Create Products table
		db.execSQL("CREATE TABLE products ( code TEXT, barcode TEXT, description TEXT , sale_price TEXT, quantity REAL, timestamp TEXT)");
		
		// Create StocktakeRecord table
		db.execSQL("CREATE TABLE stockRecords ( code TEXT, barcode TEXT, description TEXT , quantity REAL, timestamp TEXT)");		
		
		// Create PurchaseOrderItems table
		db.execSQL("CREATE TABLE purchaseOrderItems ( code TEXT, barcode TEXT, description TEXT , quantity REAL)");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("DROP TABLE settings;");
		db.execSQL("DROP TABLE products;");
		db.execSQL("DROP TABLE stockRecords;");
		db.execSQL("DROP TABLE purchaseOrderItems;");
		
		 // Create tables again
        onCreate(db);
	}
}
