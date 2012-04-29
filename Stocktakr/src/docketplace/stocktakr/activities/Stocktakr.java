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
			download = new DownloadProducts(handler, getBaseContext());
			
			download.start();
		} else if (v == performStocktake) {
			startActivity(new Intent(Stocktakr.this, PerformStocktake.class));
		} else if (v == priceCheck) {
			startActivity(new Intent(Stocktakr.this, PriceCheck.class));
		}
	}

}
