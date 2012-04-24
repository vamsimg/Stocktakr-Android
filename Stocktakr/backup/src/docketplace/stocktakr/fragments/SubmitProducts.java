package docketplace.stocktakr.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.StockRecord;
import docketplace.stocktakr.activities.Stocktakr;


public class SubmitProducts extends SherlockFragment implements OnClickListener {
	private Button submit;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.submit_stocktake, container, false);
    	
        submit = (Button)layout.findViewById(R.id.submit_button);
        
        submit.setOnClickListener(this);
        
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
		if (v == submit) {
			Stocktakr.stock.clear();
			
			showAlert("Submit Stocktake", "Stocktake records have been submitted to server.");
		}
	}
}
