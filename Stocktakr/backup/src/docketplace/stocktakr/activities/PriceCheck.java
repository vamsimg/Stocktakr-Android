package docketplace.stocktakr.activities;


import com.actionbarsherlock.app.*;

import docketplace.stocktakr.R;
import docketplace.stocktakr.data.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;



public class PriceCheck extends SherlockActivity implements OnEditorActionListener {
	private EditText barcode;
	//private EditText quantity;
	private TextView description;
	private TextView costPrice;
	private TextView salePrice;
	
	//private Button scan;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.price_check);
       
        barcode     = (EditText)findViewById(R.id.price_check_barcode);
        description = (TextView)findViewById(R.id.price_check_description_label);
        costPrice   = (TextView)findViewById(R.id.price_check_cost_price_label);
        salePrice   = (TextView)findViewById(R.id.price_check_sale_price_label);
        
        //scan   = (Button)findViewById(R.id.price_check_scan);
        
        //scan.setOnClickListener(this);
        
        barcode.setOnEditorActionListener(this);
        
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
	
	private void searchProducts() {
		String search = barcode.getText().toString().trim().toLowerCase();
		
		if (!search.equals("")) {
			Cursor results = Stocktakr.db.query("products", new String[] {"description", "cost_price", "sale_price"}, "(barcode = ?)", new String[] {search}, null, null, null);
			
			if (results.getCount() == 0) {
				description.setText("");
				costPrice.setText("");
				salePrice.setText("");
				
				showAlert();
				
				
			} else {
				results.moveToFirst();
				
				description.setText("Description: " + results.getString(0));
				costPrice.setText("Cost price: " + results.getString(1));
				salePrice.setText("Sale price: " + results.getString(2));
			}
			
			results.close();
		}
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
