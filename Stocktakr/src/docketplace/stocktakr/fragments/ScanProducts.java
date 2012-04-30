package docketplace.stocktakr.fragments;

import docketplace.stocktakr.*;
import docketplace.stocktakr.components.*;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.activities.*;

import com.actionbarsherlock.app.*;

import android.os.*;
import android.database.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;

import java.util.*;

public class ScanProducts extends SherlockFragment implements OnClickListener, OnEditorActionListener, QuantityListener, ProductInfoListener {
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
	public Button   downloadProductInfo;

	public ArrayList<StockRecord> stock;
	
	private StockRecord currentProduct;
		
	private QuantityDialog quantityDialog;
	
	private boolean setQuantityAfterScan;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stock = Database.stock;
        
        Log.d("SCAN", "Set quantity after scan: " + Database.getSettings().setQuantity);
        
        setQuantityAfterScan = Database.getSettings().setQuantity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.scan_stocktake, container, false);

    	productInfo     = (LinearLayout)layout.findViewById(R.id.product_info);
    	productNotFound = (LinearLayout)layout.findViewById(R.id.product_not_found);
    	
    	notFoundBarcode = (TextView)layout.findViewById(R.id.stocktake_product_not_found);
        barcode         = (EditText)layout.findViewById(R.id.stocktake_barcode);
        scanned         = (TextView)layout.findViewById(R.id.stocktake_scanned_barcode);
        description     = (TextView)layout.findViewById(R.id.stocktake_description_label);
        salePrice       = (TextView)layout.findViewById(R.id.stocktake_sale_price_label);
        quantityLabel   = (TextView)layout.findViewById(R.id.quantity_label);
        
        updateQuantity = (Button)layout.findViewById(R.id.update_quantity);
        quantityPlus   = (Button)layout.findViewById(R.id.quantity_plus);
        quantityMinus  = (Button)layout.findViewById(R.id.quantity_minus);
        
        downloadProductInfo = (Button)layout.findViewById(R.id.download_product_info);
        
        productInfo.setVisibility(View.GONE);
        productNotFound.setVisibility(View.GONE);
        
        updateQuantity.setOnClickListener(this);
        quantityPlus.setOnClickListener(this);
        quantityMinus.setOnClickListener(this);
        
        downloadProductInfo.setOnClickListener(this);

        barcode.setOnEditorActionListener(this);
        
        quantityDialog = new QuantityDialog(getActivity(), this);

        return layout;
    }

	public void onClick(View v) {

		if (v == updateQuantity) {
			setQuantity();
		} else if (v == quantityPlus) {
			changeQuantity(1);
		} else if (v == quantityMinus) {
			changeQuantity(-1);
		} else if (v == downloadProductInfo) {
			
		}
	}

	private void searchProducts(String searchBarcode) {
		String search = searchBarcode.trim();
		
		if (!search.equals("")) {
			Product product = Database.findProduct(search);

			if (product == null) {
				productInfo.setVisibility(View.GONE);
				productNotFound.setVisibility(View.VISIBLE);
				
				notFoundBarcode.setText(getString(R.string.product_not_found_message) + search);
			} else {
				description.setText(product.description);				
				scanned.setText(getString(R.string.barcode_label) + product.barcode);
				salePrice.setText(getString(R.string.price_label) + product.price);
				
				float count = recordStock(search, product.code, product.barcode, product.description);

				updateQuantity.setText(String.format("%1$,.2f", count));

				productInfo.setVisibility(View.VISIBLE);
				productNotFound.setVisibility(View.GONE);
				
				if (setQuantityAfterScan) {
					quantityDialog.show(currentProduct.barcode, currentProduct.description, currentProduct.quantity);
				}
			}
		} else {
			productInfo.setVisibility(View.GONE);
			productNotFound.setVisibility(View.GONE);
		}
	}
	
	private void setQuantity() {
		quantityDialog.show(currentProduct.barcode, currentProduct.description, currentProduct.quantity);
	}
	
	private void changeQuantity(int delta) {
		float count = currentProduct.quantity;
		
		float newCount = count + delta;
		
		if (newCount >= 0) {
			currentProduct.setQuantity(newCount);
			
			updateQuantity.setText(String.format("%1$,.2f", newCount));
			
			RecordedProducts.refreshList();
		}
	}

	private float recordStock(String code, String productCode, String productBarcode, String productDescription) {
		boolean updated = false;

		float count = 1;
		
		for (int r = 0; r < stock.size(); r++) {
			if (stock.get(r).barcode.equalsIgnoreCase(code)) {
				updated = true;

				StockRecord record = stock.remove(r);

				count = record.increment();

				stock.add(0, record);
				
				currentProduct = record;
			}
		}

		if (!updated) {
			currentProduct = new StockRecord(productCode, productBarcode, productDescription);
			
			stock.add(0, currentProduct);
		}

		//PerformStocktake.updateStockCount(stock.size());
		SubmitProducts.updateStockCount();
		
		RecordedProducts.refreshList();

		return count;
	}

	public boolean onEditorAction(TextView view, int action, KeyEvent event) {
		if (view == barcode) {
			if ((action == EditorInfo.IME_ACTION_SEARCH) || ((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
				searchProducts(barcode.getText().toString());

				barcode.setText("");
				
				PerformStocktake.hideKeyboard();
				
				//barcode.requestFocus();
				
				return true;
			}
		}

		return false;
	}
	
	public void onChangeQuantity(float newQuantity) {
		currentProduct.setQuantity(newQuantity);
		
		updateQuantity.setText(String.format("%1$,.2f", newQuantity));
		
		RecordedProducts.refreshList();
	}

	public void productDownloaded(String productBarcode) {
		Log.d("SCAN", "downloaded product: " + productBarcode);
		
		searchProducts(productBarcode);
		
		barcode.setText("");
		
		PerformStocktake.hideKeyboard();
		
		barcode.requestFocus();
	}
}
