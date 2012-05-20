package docketplace.stocktakr.activities;

import docketplace.stocktakr.*;
import docketplace.stocktakr.activities.PurchaseOrders.PurchaseOrderHome;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.webservice.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

import android.os.*;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class Stocktakr extends SherlockActivity implements OnClickListener {
	private Button downloadProducts;
	private Button performStocktake;
	private Button purchaseOrder;
	private Button priceCheck;

	private MenuItem settingsMenu;
	
	private TransferHandler  handler;
	private DownloadProducts download;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Database.open(this);

        downloadProducts = (Button)findViewById(R.id.download_products_button);
        performStocktake = (Button)findViewById(R.id.perform_stocktake_button);
        purchaseOrder    = (Button)findViewById(R.id.purchaseorder_button);
        priceCheck       = (Button)findViewById(R.id.price_check_button);
        
        handler = new TransferHandler(this, "Downloading Products", "Downloaded Products", "Error downloading products");

        downloadProducts.setOnClickListener(this);
        performStocktake.setOnClickListener(this);
        purchaseOrder.setOnClickListener(this);
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
            startActivity(new Intent(Stocktakr.this, SettingsView.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();

    	Database.close();
    }

	public void onClick(View v) 
	{
		if (v == downloadProducts) 
		{
			Settings credentials = Database.getSettings(); 
			if(credentials.storeID.length() != 0 && credentials.password.length()!= 0)
			{
				download = new DownloadProducts(handler, getBaseContext());			
				download.start();
			}
			else
			{
				Toast.makeText(this, "Enter a StoreID and Password in Settings", Toast.LENGTH_SHORT).show();
			}
			
		}
		else if (v == performStocktake) 
		{
			startActivity(new Intent(Stocktakr.this, StocktakeHome.class));
		}
		else if (v == purchaseOrder) 
		{
			startActivity(new Intent(Stocktakr.this, PurchaseOrderHome.class));
		}
		else if (v == priceCheck) 
		{
			startActivity(new Intent(Stocktakr.this, PriceCheck.class));
		}
	}
}
