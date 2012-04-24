package docketplace.stocktakr.activities;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.webservice.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

import android.os.*;
import android.content.*;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class Stocktakr extends SherlockActivity implements OnClickListener {
	private Button downloadProducts;
	private Button performStocktake;
	private Button priceCheck;

	private MenuItem settingsMenu;
	
	private TransferHandler  handler;
	private DownloadProducts download;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.stocktakr);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Database.open(this);

        downloadProducts = (Button)findViewById(R.id.download_products_button);
        performStocktake = (Button)findViewById(R.id.perform_stocktake_button);
        priceCheck       = (Button)findViewById(R.id.price_check_button);
        
        handler = new TransferHandler(this, "Downloading Products", "Downloaded Products", "Error downloading products");
        download = new DownloadProducts(handler);

        downloadProducts.setOnClickListener(this);
        performStocktake.setOnClickListener(this);
        priceCheck.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        settingsMenu = menu.add("Settings").setIcon(android.R.drawable.ic_menu_preferences).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == settingsMenu) {
            startActivity(new Intent(Stocktakr.this, Settings.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();

    	Database.close();
    }

	public void onClick(View v) {
		if (v == downloadProducts) {
			Database.db.execSQL("DELETE FROM products;");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('1','AWAFK.BLU.XL','Ablaze With Azure French Knicker','27.2273','1','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10','FV1047.Slate.L','Alissa Thong','54.5000','1','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('100','15-564.BLU.M','Artistry Brief in Blue','34.5000','1','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('1000','75-538.Polka.40','Coral in Polka Dot','40.8636','2','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10000','AA4201.WHE.30DD','Faye','72.6818','0','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10001','AA4201.WHE.30E','Faye','72.6818','0','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10002','AA4201.WHE.30F','Faye','72.6818','0','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10003','AA4201.WHE.30FF','Faye','72.6818','0','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10004','AA4201.WHE.30G','Faye','72.6818','0','false','2012-04-09T18:15:00.18');");
			Database.db.execSQL("INSERT INTO products (code, barcode, description, sale_price, quantity, static, modified) VALUES('10005','AA4201.WHE.32D','Faye','72.6818','0','false','2012-04-09T18:15:00.18');");

			//showAlert("Download Products", "Latest product list downloaded from server.");
			Toast.makeText(this, "Downloaded Products", Toast.LENGTH_SHORT);
			
			//download.start();
		} else if (v == performStocktake) {
			startActivity(new Intent(Stocktakr.this, PerformStocktake.class));
		} else if (v == priceCheck) {
			startActivity(new Intent(Stocktakr.this, PriceCheck.class));
		}
	}

}
