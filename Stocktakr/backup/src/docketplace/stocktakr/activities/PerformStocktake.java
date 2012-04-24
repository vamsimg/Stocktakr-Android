package docketplace.stocktakr.activities;

import java.util.Vector;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar.Tab;

import docketplace.stocktakr.R;
import docketplace.stocktakr.data.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;



public class PerformStocktake extends SherlockActivity implements OnClickListener, OnEditorActionListener, TextWatcher, ActionBar.TabListener {
	public EditText barcode;
	public EditText quantity;
	
	//private EditText quantity;
	public TextView description;
	public TextView costPrice;
	public TextView salePrice;
	public TextView quantityLabel;
	
	//public Button scan;
	
	public Button updateQuantity;
	
	public Vector<StockRecord> stock;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.perform_stocktake);
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        ActionBar.Tab scanTab     = getSupportActionBar().newTab();
        ActionBar.Tab recordedTab = getSupportActionBar().newTab();
        ActionBar.Tab submitTab   = getSupportActionBar().newTab();
        
        scanTab.setText("Scan");
        recordedTab.setText("Recorded");
        submitTab.setText("Submit");
        
        scanTab.setTabListener(this);
        recordedTab.setTabListener(this);
        submitTab.setTabListener(this);
        
        getSupportActionBar().addTab(scanTab);
        getSupportActionBar().addTab(recordedTab);
        getSupportActionBar().addTab(submitTab);
       
        barcode       = (EditText)findViewById(R.id.stocktake_barcode);
        quantityLabel = (TextView)findViewById(R.id.quantity_label);
        quantity      = (EditText)findViewById(R.id.stocktake_quantity);
        description   = (TextView)findViewById(R.id.stocktake_description_label);
        costPrice     = (TextView)findViewById(R.id.stocktake_cost_price_label);
        salePrice     = (TextView)findViewById(R.id.stocktake_sale_price_label);
        
        updateQuantity = (Button)findViewById(R.id.update_quantity);
        
        //scan   = (Button)findViewById(R.id.stocktake_scan);
        
        //scan.setOnClickListener(this);
        
        updateQuantity.setOnClickListener(this);
        
        //barcode.addTextChangedListener(new BarcodeChangeWatcher(this));
        
        barcode.setOnEditorActionListener(this);
        
        quantity.addTextChangedListener(this);
        
        stock = Stocktakr.stock;
        
        //barcode.setKeyListener(this);
    }

	
	private void showAlert() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage("Could not find a product matching this code");

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.cancel();
				}
		});
		
		alertDialog.show();
	}

	public void onClick(View v) {
		//if (v == scan) {
		//	searchProducts();
		//} else
		if (v == updateQuantity) {
			setQuantity(barcode.getText().toString(), quantity.getText().toString());
		}
	}
	
	private void searchProducts() {
		String search = barcode.getText().toString().trim().toLowerCase();
		
		if (!search.equals("")) {
			Cursor results = Stocktakr.db.query("products", new String[] {"description", "cost_price", "sale_price"}, "(barcode = ?)", new String[] {search}, null, null, null);
			
			if (results.getCount() == 0) {
				description.setText("");
				costPrice.setText("");
				salePrice.setText("");
				quantityLabel.setText("");
				quantity.setVisibility(View.INVISIBLE);
				updateQuantity.setVisibility(View.INVISIBLE);
				
				showAlert();
				
				
			} else {
				results.moveToFirst();
				
				description.setText("Description: " + results.getString(0));
				costPrice.setText("Cost price: " + results.getString(1));
				salePrice.setText("Sale price: " + results.getString(2));
				quantityLabel.setText("Quantity: ");
				quantity.setVisibility(View.VISIBLE);
				
				int count = recordStock(search);
				
				quantity.setText(String.valueOf(count));
				
				updateQuantity.setVisibility(View.INVISIBLE);
			}
			
			results.close();
		}
	}
	
	private void setQuantity(String code, String quantityText) {
		boolean updated = false;
		
		int count;
		
		try {
			count = Integer.parseInt(quantityText);
		} catch (Exception ex) {
			return;
		}
		
		
		for (int r = 0; r < stock.size(); r++) {
			if (stock.elementAt(r).barcode.equals(code)) {
				stock.elementAt(r).setQuantity(count);
				
				updated = true;
			}
		}
		
		if (updated) {
			updateQuantity.setVisibility(View.INVISIBLE);
		}
	}
	
	private int recordStock(String code) {
		boolean updated = false;
		
		int count = 1;
		
		for (int r = 0; r < stock.size(); r++) {
			if (stock.elementAt(r).barcode.equals(code)) {
				updated = true;
				
				StockRecord record = stock.remove(r);
				
				count = record.increment();
				
				stock.add(0, record);
			}
		}
		
		if (!updated) {
			stock.add(0, new StockRecord(code));
		}
		
		return count;
	}

	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		updateQuantity.setVisibility(View.VISIBLE);
	}


	public boolean onEditorAction(TextView view, int action, KeyEvent event) {
		if (view == barcode) { 
			if ((action == EditorInfo.IME_ACTION_SEARCH) || ((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
				
				
				searchProducts();
				//Toast.makeText(PerformStocktake.this, "Enter pressed", Toast.LENGTH_SHORT).show();
	
				
				return true;
			}
		}
		
		return false;
	}


	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}


	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}


	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
