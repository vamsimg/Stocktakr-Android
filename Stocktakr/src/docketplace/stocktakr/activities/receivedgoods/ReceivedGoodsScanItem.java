package docketplace.stocktakr.activities.receivedgoods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.receivedgoods.ReceivedGoodsHome;
import docketplace.stocktakr.components.QuantityDialog;
import docketplace.stocktakr.components.QuantityListener;
import docketplace.stocktakr.data.Database;
import docketplace.stocktakr.data.ReceivedGoodsItem;

public class ReceivedGoodsScanItem extends SherlockActivity implements OnClickListener, OnEditorActionListener, QuantityListener
{	
	public LinearLayout productInfo;
	public TextView productNotFound;

	public EditText barcode;

	public TextView notFoundBarcode;
	public TextView scanned;
	public TextView description;
	public TextView salePrice;


	public TextView quantityLabel;
	public Button   updateQuantity;
		
	private ReceivedGoodsItem currentItem;

	private QuantityDialog quantityDialog;

	private boolean setQuantityAfterScan;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTheme(R.style.Theme_Sherlock_Light);
	
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.transactionorder_scan);
	 
		productInfo     = (LinearLayout)findViewById(R.id.product_info);
    	productNotFound = (TextView)findViewById(R.id.product_not_found);
    	
    	notFoundBarcode = (TextView)findViewById(R.id.product_not_found);
        barcode         = (EditText)findViewById(R.id.barcode);
        scanned         = (TextView)findViewById(R.id.scanned_barcode);
        description     = (TextView)findViewById(R.id.description_label);
        salePrice       = (TextView)findViewById(R.id.sale_price_label);
        quantityLabel   = (TextView)findViewById(R.id.quantity_label);
        
        updateQuantity = (Button)findViewById(R.id.update_quantity);
       
        productInfo.setVisibility(View.GONE);
        productNotFound.setVisibility(View.GONE);
        
        updateQuantity.setOnClickListener(this);
        
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
                Intent intent = new Intent(this, ReceivedGoodsHome.class);
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
    		quantityDialog.show(currentItem.barcode, currentItem.description, currentItem.quantity);	
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
				salePrice.setText("Sale Price: " + "$" + product.price);
				
				currentItem = recordStock(product.code, product.barcode, product.description);
				
				String displayCount = "";
				
				if(Math.ceil(currentItem.quantity) == currentItem.quantity )
				{
					displayCount = String.valueOf(Math.round(currentItem.quantity));
				}
				else
				{
					displayCount = String.format("%1$,.2f", currentItem.quantity);
				}
				
				
				updateQuantity.setText(displayCount);
				productInfo.setVisibility(View.VISIBLE);
				productNotFound.setVisibility(View.GONE);				
				
				if (setQuantityAfterScan) 
				{
					quantityDialog.show(currentItem.barcode, currentItem.description, currentItem.quantity);	
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
	

	private ReceivedGoodsItem recordStock(String productCode, String productBarcode, String productDescription) 
	{	
		ReceivedGoodsItem foundStock  = Database.getReceivedGoodsItem(productCode); 
		
		if(foundStock == null)
		{
			foundStock = new ReceivedGoodsItem(productCode, productBarcode, productDescription, 1);
			Database.addReceivedGoodsItem(foundStock);			
		}
		
		return foundStock;
	}
	
	
	public void onChangeQuantity(double newQuantity) 
	{
		currentItem.quantity = newQuantity;
		Database.updateReceivedGoodsItem(currentItem);
		updateQuantity.setText(String.format("%1$,.2f", newQuantity));
	}	
	
	public void hideKeyboard()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}	
}
