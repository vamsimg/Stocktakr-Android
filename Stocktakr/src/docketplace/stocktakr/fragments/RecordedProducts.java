package docketplace.stocktakr.fragments;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.components.*;

import com.actionbarsherlock.app.*;

import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class RecordedProducts extends SherlockFragment implements OnItemClickListener, QuantityListener {
	private static RecordedProducts instance;
	
	private ListView stockList;
	
	private StockAdapter listAdapter;
	
	private QuantityDialog quantityDialog;
	
	private StockRecord currentProduct; 

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.recorded_stocktake, container, false);

        stockList = (ListView)layout.findViewById(R.id.stock_records);

        listAdapter = new StockAdapter(getActivity(), Database.stock);
        
        stockList.setAdapter(listAdapter);

        stockList.setOnItemClickListener(this);
        
        quantityDialog = new QuantityDialog(getActivity(), this);
        
    	return layout;
    }
    
	private void setQuantity() {
		quantityDialog.show(currentProduct.barcode, currentProduct.quantity);
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		currentProduct = listAdapter.getItem(position);
		
		setQuantity();
	}

	public void onChangeQuantity(int newQuantity) {
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
