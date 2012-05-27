package docketplace.stocktakr.activities.receivedgoods;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.receivedgoods.ReceivedGoodsHome;
import docketplace.stocktakr.components.QuantityDialog;
import docketplace.stocktakr.components.QuantityListener;
import docketplace.stocktakr.data.Database;
import docketplace.stocktakr.data.ReceivedGoodsItem;

public class ReceivedGoodsList  extends SherlockActivity implements OnItemClickListener, OnItemLongClickListener, QuantityListener, DialogInterface.OnClickListener {
	
	private ListView itemListView;
	
	private QuantityDialog quantityDialog;
	
	private ReceivedGoodsItem selectedItem;	
	
	private AlertDialog removeDialog; 
	
	private List<ReceivedGoodsItem> itemList;
	
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		setTheme(R.style.Theme_Sherlock_Light);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.item_list);
		
		itemList = Database.getAllReceivedGoodsItems();	        	
		
		itemListView = (ListView)findViewById(R.id.items_listview);
		
		itemListView.setAdapter(new ItemArrayAdaptor(this, itemList));
		
		itemListView.setOnItemClickListener(this);
		itemListView.setOnItemLongClickListener(this);
		
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
                Intent intent = new Intent(this, ReceivedGoodsHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
	private void setQuantity() {
		quantityDialog.show(selectedItem.barcode, selectedItem.description, selectedItem.quantity);
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		selectedItem = itemList.get(position);			
		
		setQuantity();
	}
	
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		selectedItem = 	itemList.get(position);			
		removeDialog.setTitle("Remove " + selectedItem.barcode);

		removeDialog.show();
		
		return true;
	}
	
	public void onClick(DialogInterface dialog, int button) {
		if (button == DialogInterface.BUTTON_POSITIVE) {
			removeDialog.dismiss();			
			Database.deleteReceivedGoodsItem(selectedItem);
			refreshList();
			hideKeyboard();
		}
	}

	public void onChangeQuantity(double newQuantity) {
		selectedItem.quantity = newQuantity;
		Database.updateReceivedGoodsItem(selectedItem);	
		refreshList();
		hideKeyboard();
	}
	
	public void hideKeyboard()
	{		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}	
	
	private void refreshList()
	{
		itemList = Database.getAllReceivedGoodsItems();	        	
		itemListView.setAdapter(new ItemArrayAdaptor(this, itemList));
	}
	
	
	private class ItemArrayAdaptor extends ArrayAdapter<ReceivedGoodsItem> 
	{
	    public List<ReceivedGoodsItem> orderItems;

	    public ItemArrayAdaptor(Context context, List<ReceivedGoodsItem> records) 
	    {
	        super(context, R.layout.product_record, records);	        
	        this.orderItems = records;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {
	        View v = convertView;

	        
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.product_record, null);
	        }

	        ReceivedGoodsItem record = orderItems.get(position);

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
