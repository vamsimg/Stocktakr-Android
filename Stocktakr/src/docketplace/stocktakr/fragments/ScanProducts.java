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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stock = Database.stock;
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

		String productBarcode;
		String productDescription;
		String productSalePrice;
		
		if (!search.equals("")) {
			Cursor results = Database.db.query("products", new String[] {"barcode", "description", "sale_price"}, "(barcode = ?)", new String[] {search}, null, null, null);

			if (results.getCount() == 0) {
				productInfo.setVisibility(View.GONE);
				productNotFound.setVisibility(View.VISIBLE);
				
				notFoundBarcode.setText(getString(R.string.product_not_found_message) + search);
			} else {
				results.moveToFirst();

				productBarcode     = results.getString(0);
				productDescription = results.getString(1);
				productSalePrice   = results.getString(2);
				
				description.setText(productDescription);
				
				scanned.setText("Barcode: " + productBarcode);
				salePrice.setText("Price: " + productSalePrice);
				
				int count = recordStock(search, productBarcode, productDescription);

				updateQuantity.setText(String.valueOf(count));

				productInfo.setVisibility(View.VISIBLE);
				productNotFound.setVisibility(View.GONE);
			}

			results.close();
		} else {
			productInfo.setVisibility(View.GONE);
			productNotFound.setVisibility(View.GONE);
		}
	}
	
	private void setQuantity() {
		quantityDialog.show(currentProduct.barcode, currentProduct.quantity);
	}
	
	private void changeQuantity(int delta) {
		int count = currentProduct.quantity;
		
		int newCount = count + delta;
		
		if (newCount >= 0) {
			currentProduct.setQuantity(newCount);
			
			updateQuantity.setText(" " + String.valueOf(newCount) + " ");
			
			RecordedProducts.refreshList();
		}
	}

	private int recordStock(String code, String original, String description) {
		boolean updated = false;

		int count = 1;

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
			currentProduct = new StockRecord(original, description);
			stock.add(0, new StockRecord(original, description));
		}

		PerformStocktake.updateStockCount(stock.size());
		
		RecordedProducts.refreshList();

		return count;
	}

	public boolean onEditorAction(TextView view, int action, KeyEvent event) {
		if (view == barcode) {
			if ((action == EditorInfo.IME_ACTION_SEARCH) || ((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
				searchProducts(barcode.getText().toString());

				barcode.setText("");
				
				PerformStocktake.hideKeyboard();
				
				return true;
			}
		}

		return false;
	}
	
	public void onChangeQuantity(int newQuantity) {
		currentProduct.setQuantity(newQuantity);
		
		updateQuantity.setText(" " + String.valueOf(newQuantity) + " ");
		
		RecordedProducts.refreshList();
	}

	public void productDownloaded(String productBarcode) {
		Log.d("SCAN", "downloaded product: " + productBarcode);
		
		searchProducts(productBarcode);
	}
}
