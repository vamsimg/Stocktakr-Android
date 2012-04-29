package docketplace.stocktakr.fragments;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.webservice.*;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.*;
import android.app.*;
import android.content.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;


public class SubmitProducts extends SherlockFragment implements OnClickListener {
	private static SubmitProducts instance;
	
	private EditText name;
	private Button   submit;
	private TextView productCounter;
	
	private SubmitStockRecords submitStock;
	private TransferHandler    handler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        instance = this;
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.submit_stocktake, container, false);
    	
    	name   = (EditText)layout.findViewById(R.id.person_name);
        submit = (Button)layout.findViewById(R.id.submit_button);
        
        productCounter = (TextView)layout.findViewById(R.id.product_counter);
        
        if (Database.stock.size() > 0) {
        	productCounter.setText(instance.getString(R.string.products) + Database.stock.size());
        }
        
        submit.setOnClickListener(this);
        
        handler = new TransferHandler(this.getActivity(), "Submitting stock records", "Records submitted", "Submission Error");
        
    	return layout;
    }
    
    private void showAlert(String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.cancel();
					getActivity().finish();
				}
		});
		
		alertDialog.show();
	}

	public void onClick(View v) {
		String personName;
		
		if (v == submit) {
			personName = name.getText().toString().trim();
			
			if (personName.length() == 0) {
				Toast.makeText(this.getActivity(), "Please enter you name", Toast.LENGTH_SHORT).show();
			} else {
				Log.d("SUBMIT", "starting");
				
				submitStock = new SubmitStockRecords(handler, name.getText().toString());
				
				submitStock.start();
				
				Log.d("SUBMIT", "started");
			
			}
		}
	}
	
	public static void updateStockCount() {
		if (instance != null) {
			if (Database.stock.size()> 0) {
				instance.productCounter.setText(instance.getString(R.string.products) + Database.stock.size());
			} else {
				instance.productCounter.setText("");
			}
		}
	}
}
