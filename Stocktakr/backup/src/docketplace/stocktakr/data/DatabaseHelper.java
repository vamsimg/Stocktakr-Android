package docketplace.stocktakr.data;

import docketplace.stocktakr.R;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME    = "stocktakr.db";
	private static final int    DATABASE_VERSION = 2;
	
	private Resources resources;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.resources = context.getResources();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create Settings table
		db.execSQL(resources.getString(R.string.create_settings));
		
		// Insert default settings
		db.execSQL(resources.getString(R.string.default_settings));
		
		// Create Products table
		db.execSQL(resources.getString(R.string.create_products));
		
		// Create Stocktake table
		db.execSQL(resources.getString(R.string.create_stocktake));
		
		// Create Stocktake-Products mapping table
		db.execSQL(resources.getString(R.string.create_stocktake_products));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE products;");
		

		// Create Products table
		db.execSQL(resources.getString(R.string.create_products));				
	}

}
