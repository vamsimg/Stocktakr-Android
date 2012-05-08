package docketplace.stocktakr.activities;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import docketplace.stocktakr.R;
import docketplace.stocktakr.components.QuantityDialog;
import docketplace.stocktakr.components.QuantityListener;
import docketplace.stocktakr.data.Database;
import docketplace.stocktakr.data.StockRecord;

public class RecordsList  extends SherlockActivity implements OnItemClickListener, OnItemLongClickListener, QuantityListener, DialogInterface.OnClickListener {
	
	private ListView stockList;
	
	private QuantityDialog quantityDialog;
	
	private StockRecord selectedRecord;	
	
	private AlertDialog removeDialog; 
	
	private List<StockRecord> stockRecords;
	
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		setTheme(R.style.Theme_Sherlock_Light);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recorded_stocktake);
		
		stockRecords = Database.getAllStockRecords();	        	
		
		stockList = (ListView)findViewById(R.id.stock_records);
		
		stockList.setAdapter(new StockAdapter(this, stockRecords));
		
		stockList.setOnItemClickListener(this);
		stockList.setOnItemLongClickListener(this);
		
		quantityDialog = new QuantityDialog(this, this);
		
		removeDialog = new AlertDialog.Builder(this)
						   .setPositiveButton(R.string.remove, this)
						   .setNegativeButton(R.string.cancel, this)
						   .setMessage(R.string.remove_confirmation)
						   .setCancelable(true)
						   .create();
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, StocktakeHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
	private void setQuantity() {
		quantityDialog.show(selectedRecord.barcode, selectedRecord.description, selectedRecord.quantity);
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		selectedRecord = stockRecords.get(position);			
		
		setQuantity();
	}
	
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		selectedRecord = 	stockRecords.get(position);			
		removeDialog.setTitle("Remove " + selectedRecord.barcode);

		removeDialog.show();
		
		return true;
	}
	
	public void onClick(DialogInterface dialog, int button) {
		if (button == DialogInterface.BUTTON_POSITIVE) {
			removeDialog.dismiss();			
			Database.deleteRecord(selectedRecord);
			refreshList();
			hideKeyboard();
		}
	}

	public void onChangeQuantity(double newQuantity) {
		selectedRecord.setQuantity(newQuantity);
		Database.updateStockRecord(selectedRecord);	
		refreshList();
		hideKeyboard();
	}
	
	private void hideKeyboard()
	{		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	private void refreshList()
	{
		stockRecords = Database.getAllStockRecords();	        	
		stockList.setAdapter(new StockAdapter(this, stockRecords));
	}
	
	
	private class StockAdapter extends ArrayAdapter<StockRecord> 
	{
	    public List<StockRecord> stockRecords;

	    public StockAdapter(Context context, List<StockRecord> records) 
	    {
	        super(context, R.layout.stock_record, records);	        
	        this.stockRecords = records;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {
	        View v = convertView;

	        
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.stock_record, null);
	        }

	        StockRecord record = stockRecords.get(position);

	        if (record != null)
	        {
	            TextView barcode     = (TextView)v.findViewById(R.id.stock_barcode);
	            TextView description = (TextView)v.findViewById(R.id.stock_description);
	            TextView quantity    = (TextView)v.findViewById(R.id.stock_quantity);

	            if (barcode != null) 
	            {
	                barcode.setText(record.barcode);
	            }

	            if (description != null) 
	            {
	                description.setText(record.description);
	            }

	            if (quantity != null) 
	            {
	            	
	            	double quant = record.quantity ;
	            	String displayCount = "";
					
					if(Math.ceil(quant) == quant )
					{
						displayCount = String.valueOf(Math.round(quant));
					}
					else
					{
						displayCount = String.format("%1$,.2f", quant);
					}
	                quantity.setText(displayCount);
	            }
	        }
	        return v;
	    }
	}
	
}
