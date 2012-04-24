package docketplace.stocktakr.activities;

import java.util.Vector;

import com.actionbarsherlock.app.*;


import docketplace.stocktakr.R;
import docketplace.stocktakr.data.DatabaseHelper;

import android.app.*;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class Stocktakr extends SherlockActivity implements OnClickListener {
    
	public static Vector<StockRecord> stock;
	
	String NAMESPACE   = "http://docketplace.com.au/";
    String METHOD_NAME = "GetItemsForStocktake";
    String URL         = "http://testdocketc.web705.discountasp.net/Itemhandler.asmx";
	
	public static SQLiteDatabase db;
	
	private Button downloadProducts;
	private Button performStocktake;
	private Button priceCheck;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.stocktakr);
        
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        
        db = helper.getWritableDatabase();
        
        stock = new Vector<StockRecord>();
        
        downloadProducts = (Button)findViewById(R.id.download_products_button);
        performStocktake = (Button)findViewById(R.id.perform_stocktake_button);
        priceCheck       = (Button)findViewById(R.id.price_check_button);
        
        downloadProducts.setOnClickListener(this);
        performStocktake.setOnClickListener(this);
        priceCheck.setOnClickListener(this);
        
        /*stock.add(new StockRecord("AWAFK.BLU.XL"));
        stock.add(new StockRecord("FV0147.Slate.L"));
        stock.add(new StockRecord("15-564.BLU.M"));
        stock.add(new StockRecord("75-538.Polka.40"));
        stock.add(new StockRecord("AA4201.WHE.30DD"));
        stock.add(new StockRecord("AA4201.WHE.30E"));*/
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	db.close();
    }

	public void onClick(View v) {
		if (v == downloadProducts) {
			db.execSQL("DELETE FROM products;");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('1','awafk.blu.xl','Ablaze With Azure French Knicker','0','27.2273','1','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10','fv1047.slate.l','Alissa Thong','0','54.5000','1','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('100','15-564.blu.m','Artistry Brief in Blue','0','34.5000','1','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('1000','75-538.polka.40','Coral in Polka Dot','0','40.8636','2','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10000','aa4201.whe.30dd','Faye','0','72.6818','0','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10001','aa4201.whe.30e','Faye','0','72.6818','0','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10002','aa4201.whe.30f','Faye','0','72.6818','0','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10003','aa4201.whe.30ff','Faye','0','72.6818','0','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10004','aa4201.whe.30g','Faye','0','72.6818','0','false','2012-04-09T18:15:00.18');");
			db.execSQL("INSERT INTO products (code, barcode, description, cost_price, sale_price, quantity, static, modified) VALUES('10005','aa4201.whe.32d','Faye','0','72.6818','0','false','2012-04-09T18:15:00.18');");
			
			showAlert("Download Products", "Latest product list downloaded from server.");
		} else if (v == performStocktake) {
			startActivity(new Intent(Stocktakr.this, PerformStocktakeNew.class));
		} else if (v == priceCheck) {
			startActivity(new Intent(Stocktakr.this, PriceCheck.class));
		}
	}
	
	private void showAlert(String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.cancel();
				}
		});
		
		alertDialog.show();
	}

}