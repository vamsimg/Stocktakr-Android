package docketplace.stocktakr.fragments;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.*;
import android.app.*;
import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;


public class SubmitProducts extends SherlockFragment implements OnClickListener {
	private EditText name;
	private Button   submit;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View layout = inflater.inflate(R.layout.submit_stocktake, container, false);
    	
    	name   = (EditText)layout.findViewById(R.id.person_name);
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
			Database.stock.clear();
			
			showAlert("Submit Stocktake", "Stocktake records have been submitted to server.");
		}
	}
}
