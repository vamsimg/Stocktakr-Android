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
import docketplace.stocktakr.data.Database;


public class StocktakeHome extends SherlockActivity implements OnClickListener {
	
	private Button scanItem;
	private Button recordsList;
	private Button submitRecords;
	
	private TextView products;
	private TextView records;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.stocktake_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
       
        scanItem = (Button)findViewById(R.id.scanitem_button);
        recordsList = (Button)findViewById(R.id.records_button);
        submitRecords       = (Button)findViewById(R.id.submitrecords_button);
        products = (TextView)findViewById(R.id.productcount_textView);
        records = (TextView)findViewById(R.id.recordcount_textView);
        
        scanItem.setOnClickListener(this);
        recordsList.setOnClickListener(this);
        submitRecords.setOnClickListener(this);
        
        products.setText("Product Count: " + Database.getProductCount());
        records.setText("Total Records: " + Database.getStockRecordCount());
        
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
			startActivity(new Intent(StocktakeHome.this, ScanItems.class));
		}
		else if (v == recordsList) 
		{
			startActivity(new Intent(StocktakeHome.this, RecordsList.class));
		}		
		else if (v == submitRecords) 
		{
			startActivity(new Intent(StocktakeHome.this, SubmitRecords.class));
		}
	}


}
