package docketplace.stocktakr.activities;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.MenuItem;

import android.os.*;

import android.content.*;

import android.view.KeyEvent;
import android.view.View;

import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;


public class PriceCheck extends SherlockActivity implements  OnEditorActionListener {
	public LinearLayout productInfo;
	public LinearLayout productNotFound;
	
	public TextView notFoundBarcode;
	
	private EditText barcode;

	private TextView scanned;
	private TextView description;
	private TextView salePrice;


	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.price_check);

        productInfo     = (LinearLayout)findViewById(R.id.price_check_product_info);
    	productNotFound = (LinearLayout)findViewById(R.id.price_check_product_not_found_section);
    	
    	notFoundBarcode = (TextView)findViewById(R.id.price_check_product_not_found);
        barcode     = (EditText)findViewById(R.id.price_check_barcode);
        scanned     = (TextView)findViewById(R.id.price_check_scanned_barcode);
        description = (TextView)findViewById(R.id.price_check_description_label);
        salePrice   = (TextView)findViewById(R.id.price_check_sale_price_label);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        	
        barcode.setOnEditorActionListener(this);
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

    private void searchProducts(String searchBarcode) {
		String search = searchBarcode.trim();
		
		if (!search.equals("")) {
			docketplace.stocktakr.data.Product product = Database.findProduct(search);
			
			if (product == null) {
				productInfo.setVisibility(View.GONE);
				productNotFound.setVisibility(View.VISIBLE);
				
				notFoundBarcode.setText(getString(R.string.product_not_found_message) + search);
			} else {
				description.setText(product.description);				
				scanned.setText(getString(R.string.barcode_label) + product.barcode);
				salePrice.setText(getString(R.string.price_label) + product.price);

				productInfo.setVisibility(View.VISIBLE);
				productNotFound.setVisibility(View.GONE);
				
				// Get instance of Vibrator from current Context
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				 
				// Vibrate for 100 milliseconds
				v.vibrate(100);

			}
		} else {
			productInfo.setVisibility(View.GONE);
			productNotFound.setVisibility(View.GONE);
		}
	}

    
	public boolean onEditorAction(TextView view, int action, KeyEvent event) 
	{
		
		if (view == barcode) {
			if ((action == EditorInfo.IME_ACTION_SEARCH) || ((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
				searchProducts(barcode.getText().toString());

				barcode.setText("");
				
				//Stocktakr.hideKeyboard();
				
				barcode.requestFocus();
				
				return true;
			}
		}

		return false;
	}
}
 