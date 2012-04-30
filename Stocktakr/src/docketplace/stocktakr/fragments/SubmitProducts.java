package docketplace.stocktakr.fragments;

import docketplace.stocktakr.*;
import docketplace.stocktakr.activities.PerformStocktake;
import docketplace.stocktakr.components.SubmissionListener;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.webservice.*;

import com.actionbarsherlock.app.SherlockFragment;

import android.database.Cursor;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;


public class SubmitProducts extends SherlockFragment implements OnClickListener, SubmissionListener {
	private static SubmitProducts instance;
	
	private EditText name;
	private Button   submit;
	private TextView productCounter;
	
	private SubmitStockRecords submitStock;
	private TransferHandler    handler;
	
	private Button   submitAll;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	instance = this;
    	
    	View layout = inflater.inflate(R.layout.submit_stocktake, container, false);
    	
    	name   = (EditText)layout.findViewById(R.id.person_name);
        submit = (Button)layout.findViewById(R.id.submit_button);
        
        productCounter = (TextView)layout.findViewById(R.id.product_counter);
        
        if (Database.stock.size() > 0) {
        	productCounter.setText(instance.getString(R.string.products) + Database.stock.size());
        } else {
        	productCounter.setText("");
        }
        
        submit.setOnClickListener(this);
        
        submitAll = (Button)layout.findViewById(R.id.submit_all_button);
        
        submitAll.setOnClickListener(this);
        
        handler = new TransferHandler(this.getActivity(), "Submitting stock records", "Records submitted", "Submission Error");
        
    	return layout;
    }

	public void onClick(View v) {
		String personName;
		
		if (v == submit) {
			personName = name.getText().toString().trim();
			
			if (personName.length() == 0) {
				Toast.makeText(this.getActivity(), "Please enter you name", Toast.LENGTH_SHORT).show();
			} else {
				Log.d("SUBMIT", "starting");
				
				submitStock = new SubmitStockRecords(handler, name.getText().toString(), this, getActivity().getBaseContext());
				
				submitStock.start();
				
				Log.d("SUBMIT", "started");
			
			}
		} else if (v == submitAll) {
			Database.stock.clear();
			
			Log.d("SUBMIT ALL", "loading all products");
			
			Cursor results = Database.db.query("products", new String[] {"code", "barcode", "description", "sale_price"}, null, null, null, null, null);

			if (results.getCount() > 0) {
				results.moveToFirst();
				
				Log.d("SUBMIT ALL", "loading: " + results.getCount());
				
				do {
					Database.stock.add(new StockRecord(results.getString(0), results.getString(1), results.getString(2)));
				} while (results.moveToNext());
				
				Log.d("SUBMIT ALL", "starting");
				
				submitStock = new SubmitStockRecords(handler, "submit all", this, getActivity().getBaseContext());
				
				submitStock.start();
				
				Log.d("SUBMIT ALL", "started");
			} else {
				Log.d("SUBMIT ALL", "no products downloaded");
			}
			
			results.close();
		}
	}
	
	public void submitComplete() {
		PerformStocktake.close();
	}
	
	public static void updateStockCount() {
		if (instance != null) {
			if (Database.stock.size()> 0) {
				instance.productCounter.setText(instance.getString(R.string.products) + Database.stock.size());
			} else {
				instance.productCounter.setText("");
			}
		}
	}
}
