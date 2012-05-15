package docketplace.stocktakr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import docketplace.stocktakr.R;
import docketplace.stocktakr.components.QuantityDialog;
import docketplace.stocktakr.components.QuantityListener;
import docketplace.stocktakr.data.Database;
import docketplace.stocktakr.data.StockRecord;


public class ScanItems extends SherlockActivity implements OnClickListener, OnEditorActionListener, QuantityListener
{	
	public LinearLayout productInfo;
	public LinearLayout productNotFound;

	public EditText barcode;

	public TextView notFoundBarcode;
	public TextView scanned;
	public TextView description;
	public TextView salePrice;


	public TextView quantityLabel;
	public Button   updateQuantity;
	public Button   quantityPlus, quantityMinus;

	
	private StockRecord currentProduct;

	private QuantityDialog quantityDialog;

	private boolean setQuantityAfterScan;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTheme(R.style.Theme_Sherlock_Light);
	
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.scan_stocktake);
	 
		productInfo     = (LinearLayout)findViewById(R.id.product_info);
    	productNotFound = (LinearLayout)findViewById(R.id.product_not_found);
    	
    	notFoundBarcode = (TextView)findViewById(R.id.stocktake_product_not_found);
        barcode         = (EditText)findViewById(R.id.stocktake_barcode);
        scanned         = (TextView)findViewById(R.id.stocktake_scanned_barcode);
        description     = (TextView)findViewById(R.id.stocktake_description_label);
        salePrice       = (TextView)findViewById(R.id.stocktake_sale_price_label);
        quantityLabel   = (TextView)findViewById(R.id.quantity_label);
        
        updateQuantity = (Button)findViewById(R.id.update_quantity);
        quantityPlus   = (Button)findViewById(R.id.quantity_plus);
        quantityMinus  = (Button)findViewById(R.id.quantity_minus);       
        
        
        productInfo.setVisibility(View.GONE);
        productNotFound.setVisibility(View.GONE);
        
        updateQuantity.setOnClickListener(this);
        quantityPlus.setOnClickListener(this);
        quantityMinus.setOnClickListener(this);
        
        setQuantityAfterScan = Database.getSettings().setQuantity;

        barcode.setOnEditorActionListener(this);
        
        quantityDialog = new QuantityDialog(this, this);

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
    
	
    public void onClick(View v) 
    {		
    	if (v == updateQuantity) {
    		quantityDialog.show(currentProduct.barcode, currentProduct.description, currentProduct.quantity);	
		} else if (v == quantityPlus) {
			changeQuantity(1);
		} else if (v == quantityMinus) {
			changeQuantity(-1);
		} 
	}

	private void searchProducts(String searchBarcode) {
		String search = searchBarcode.trim();
		
		if (!search.equals("")) {
			docketplace.stocktakr.data.Product product = Database.findProduct(search);

			if (product == null) {
				
				notFoundBarcode.setText(getString(R.string.product_not_found_message) + search);
				productInfo.setVisibility(View.GONE);
				productNotFound.setVisibility(View.VISIBLE);

				
			} else {
				description.setText(product.description);				
				scanned.setText("Barcode: " + product.barcode);
				salePrice.setText("Sale Price: " + product.price);
				
				currentProduct = recordStock(product.code, product.barcode, product.description);
				
				String displayCount = "";
				
				if(Math.ceil(currentProduct.quantity) == currentProduct.quantity )
				{
					displayCount = String.valueOf(Math.round(currentProduct.quantity));
				}
				else
				{
					displayCount = String.format("%1$,.2f", currentProduct.quantity);
				}
				
				
				updateQuantity.setText(displayCount);
				productInfo.setVisibility(View.VISIBLE);
				productNotFound.setVisibility(View.GONE);
				
				
				if (setQuantityAfterScan) 
				{
					quantityDialog.show(currentProduct.barcode, currentProduct.description, currentProduct.quantity);	
				}				
				
			}
		} else 
		{
			productInfo.setVisibility(View.GONE);
			productNotFound.setVisibility(View.GONE);			
		}
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(barcode.getWindowToken(), 0);
	}
	
	
	public boolean onEditorAction(TextView view, int action, KeyEvent event) {
		if (view == barcode) {
			if (((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
				searchProducts(barcode.getText().toString());

				barcode.setText("");			
				
				return true;
			}
		}			
		return false;
	}
	

	private StockRecord recordStock(String productCode, String productBarcode, String productDescription) 
	{	
		StockRecord foundStock  = Database.getStockRecord(productCode); 
		
		if(foundStock == null)
		{
			foundStock = new StockRecord(productCode, productBarcode, productDescription, 1, StockRecord.currentTimestamp());
			Database.addStockRecord(foundStock);			
		}
		else
		{		
			if(!setQuantityAfterScan)
			{
				foundStock.increment();
				Database.updateStockRecord(foundStock);		

				// Get instance of Vibrator from current Context
				Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);				 
				// Vibrate for 100 milliseconds
				v.vibrate(100);
			}	
		}		
		return foundStock;
	}
	
	
	private void changeQuantity(int delta) {
			
		double newCount = currentProduct.quantity + delta;		
		
		currentProduct.setQuantity(newCount);
		
		Database.updateStockRecord(currentProduct);
		
		updateQuantity.setText(String.format("%1$,.2f", newCount));			
	}
	
	public void onChangeQuantity(double newQuantity) 
	{
		currentProduct.setQuantity(newQuantity);
		Database.updateStockRecord(currentProduct);
		updateQuantity.setText(String.format("%1$,.2f", newQuantity));
	}	
	
	public void hideKeyboard()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}	
}
