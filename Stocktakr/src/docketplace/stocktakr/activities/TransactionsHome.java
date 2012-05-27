package docketplace.stocktakr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import com.actionbarsherlock.view.MenuItem;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.PurchaseOrders.PurchaseOrderHome;
import docketplace.stocktakr.activities.receivedgoods.ReceivedGoodsHome;
import docketplace.stocktakr.data.Database;


public class TransactionsHome extends SherlockActivity implements OnClickListener {
	
	private Button purchaseOrder;
	private Button receivedGoods;
	
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.transactions_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
       
        purchaseOrder = (Button)findViewById(R.id.purchaseorder_button);
        receivedGoods = (Button)findViewById(R.id.receivedgoods_button);
                
        purchaseOrder.setOnClickListener(this);
        receivedGoods.setOnClickListener(this);
        
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
		if (v == purchaseOrder) 
		{
			startActivity(new Intent(this, PurchaseOrderHome.class));
		}
		else if (v == receivedGoods) 
		{
			startActivity(new Intent(this, ReceivedGoodsHome.class));
		}
	}
}