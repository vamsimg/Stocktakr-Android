package docketplace.stocktakr.activities.PurchaseOrders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import com.actionbarsherlock.view.MenuItem;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.Stocktakr;
import docketplace.stocktakr.data.Database;

public class PurchaseOrderHome extends SherlockActivity implements OnClickListener {
	
	private Button scanItem;
	private Button itemList;
	private Button submitOrder;
	
	private TextView products;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.purchaseorder_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
       
        scanItem = (Button)findViewById(R.id.scanitem_button);
        itemList = (Button)findViewById(R.id.itemslist_button);
        submitOrder       = (Button)findViewById(R.id.submitpurchaseorder_button);
        products = (TextView)findViewById(R.id.productcount_textView);
       
        
        scanItem.setOnClickListener(this);
        itemList.setOnClickListener(this);
        submitOrder.setOnClickListener(this);
        
        products.setText("Product Count: " + Database.getProductCount());
        
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Stocktakr.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }    

	public void onClick(View v) 
	{
		if (v == scanItem) 
		{
			startActivity(new Intent(PurchaseOrderHome.this, PurchaseOrderScanItem.class));
		}
		else if (v == itemList) 
		{
			startActivity(new Intent(PurchaseOrderHome.this, PurchaseOrderList.class));
		}		
		else if (v == submitOrder) 
		{
			startActivity(new Intent(PurchaseOrderHome.this, PurchaseOrderSubmit.class));
		}
	}
}

