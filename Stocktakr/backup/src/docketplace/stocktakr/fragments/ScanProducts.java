package docketplace.stocktakr.fragments;

import java.util.Vector;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.BarcodeChangeWatcher;
import docketplace.stocktakr.activities.StockRecord;
import docketplace.stocktakr.activities.Stocktakr;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class ScanProducts extends SherlockFragment implements OnClickListener, OnEditorActionListener, TextWatcher {
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
        super.onCreate(savedInstanceState);

        stock = Stocktakr.stock;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.scan_stocktake, container, false);
        
        barcode       = (EditText)layout.findViewById(R.id.stocktake_barcode);
        quantityLabel = (TextView)layout.findViewById(R.id.quantity_label);
        quantity      = (EditText)layout.findViewById(R.id.stocktake_quantity);
        description   = (TextView)layout.findViewById(R.id.stocktake_description_label);
        costPrice     = (TextView)layout.findViewById(R.id.stocktake_cost_price_label);
        salePrice     = (TextView)layout.findViewById(R.id.stocktake_sale_price_label);
        
        updateQuantity = (Button)layout.findViewById(R.id.update_quantity);

        
        updateQuantity.setOnClickListener(this);
        
        barcode.addTextChangedListener(new BarcodeChangeWatcher(this));
        
        barcode.setOnEditorActionListener(this);
        
        quantity.addTextChangedListener(this);
        
        return layout;
    }
    
    private void showAlert() {
    	
		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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
}

/*public class ScanProducts extends Fragment implements OnClickListener, OnEditorActionListener, TextWatcher {
	private Context context;
	
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
        super.onCreate(savedInstanceState);
       
        context = getActivity().getApplicationContext();
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.perform_stocktake, container, false);
        
        barcode       = (EditText)container.findViewById(R.id.stocktake_barcode);
        quantityLabel = (TextView)container.findViewById(R.id.quantity_label);
        quantity      = (EditText)container.findViewById(R.id.stocktake_quantity);
        description   = (TextView)container.findViewById(R.id.stocktake_description_label);
        costPrice     = (TextView)container.findViewById(R.id.stocktake_cost_price_label);
        salePrice     = (TextView)container.findViewById(R.id.stocktake_sale_price_label);
        
        updateQuantity = (Button)container.findViewById(R.id.update_quantity);

        
        updateQuantity.setOnClickListener(this);
        
        barcode.addTextChangedListener(new BarcodeChangeWatcher(this));
        
        barcode.setOnEditorActionListener(this);
        
        quantity.addTextChangedListener(this);
        
        stock = Stocktakr.stock;
        
        return layout;
    }

    private void showAlert() {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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
}
*/