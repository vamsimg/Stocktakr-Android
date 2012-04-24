package docketplace.stocktakr.fragments;

import com.actionbarsherlock.app.SherlockFragment;

import docketplace.stocktakr.R;

import docketplace.stocktakr.activities.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RecordedProducts extends SherlockFragment {
	private ListView stockList;
	
	private String[] stockItems;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        stockItems = new String[Stocktakr.stock.size()];
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.recorded_stocktake, container, false);
    	
        stockList = (ListView)layout.findViewById(R.id.stock_records);
        
        stockList.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.stock_item, stockItems));
        
        for (int i = 0; i < Stocktakr.stock.size(); i++) {
        	StockRecord record = Stocktakr.stock.elementAt(i);
        	
        	stockItems[i] = record.barcode + " (" + record.quantity + ")";
        }
        
    	return layout;
    }
}
