package docketplace.stocktakr.fragments;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.components.*;

import com.actionbarsherlock.app.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class RecordedProducts extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener, QuantityListener, DialogInterface.OnClickListener {
	private static RecordedProducts instance;
	
	private ListView stockList;
	
	private StockAdapter listAdapter;
	
	private QuantityDialog quantityDialog;
	
	private StockRecord currentProduct; 
	
	private int selectedProduct;
	
	private AlertDialog removeDialog; 
	
	private Activity activity;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.recorded_stocktake, container, false);
    	
    	activity = getActivity();
    	
        stockList = (ListView)layout.findViewById(R.id.stock_records);

        listAdapter = new StockAdapter(activity, Database.stock);
        
        stockList.setAdapter(listAdapter);

        stockList.setOnItemClickListener(this);
        stockList.setOnItemLongClickListener(this);
        
        quantityDialog = new QuantityDialog(activity, this);
        
        removeDialog = new AlertDialog.Builder(activity)
        				   .setPositiveButton(R.string.remove, this)
        				   .setNegativeButton(R.string.cancel, this)
        				   .setMessage(R.string.remove_confirmation)
        				   .setCancelable(true)
        				   .create();

        instance = this;
        
    	return layout;
    }
    
	private void setQuantity() {
		quantityDialog.show(currentProduct.barcode, currentProduct.description, currentProduct.quantity);
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		currentProduct = listAdapter.getItem(position);
		
		setQuantity();
	}
	
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		selectedProduct = position;
		
		removeDialog.setTitle("Remove " + Database.stock.get(position).barcode);

		removeDialog.show();
		
		return true;
	}
	
	public void onClick(DialogInterface dialog, int button) {
		if (button == DialogInterface.BUTTON_POSITIVE) {
			removeDialog.dismiss();
			
			Database.stock.remove(selectedProduct);
			
			refreshList();
			
			SubmitProducts.updateStockCount();
		}
	}

	public void onChangeQuantity(float newQuantity) {
		currentProduct.setQuantity(newQuantity);
		
		RecordedProducts.refreshList();
	}
	
	public static void refreshList() {
    	if (instance != null) {
    		if (instance.listAdapter != null) {
    			instance.listAdapter.notifyDataSetChanged();
    		}
    		
    		if (instance.stockList != null) {
    			instance.stockList.refreshDrawableState();
    		}
    	}
    }
}
