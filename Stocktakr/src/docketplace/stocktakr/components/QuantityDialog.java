package docketplace.stocktakr.components;

import docketplace.stocktakr.R;
import docketplace.stocktakr.activities.PerformStocktake;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class QuantityDialog implements DialogInterface.OnClickListener {
	private AlertDialog      quantityDialog;
	private QuantityListener listener;
	
	private EditText quantityInput; 

	private Activity activity;
	private Context  context;
	
	private InputMethodManager input;
	
	public QuantityDialog(Activity activity, QuantityListener listener) {
		this.activity = activity;
		this.listener = listener;
		
		context = PerformStocktake.instance; //activity.getApplicationContext();
		
        quantityInput = new EditText(context);
        
        quantityInput.setSingleLine(true);
        quantityInput.setSelectAllOnFocus(true);
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        
        quantityDialog = new AlertDialog.Builder(context)
							.setTitle(R.string.change_quantity)
							.setView(quantityInput)
							.setPositiveButton(R.string.change, this)
							.setNegativeButton(R.string.cancel, this)
							.create();
        
        input = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	public void show(String code, String description, float currentQuantity) {
		quantityInput.setText(String.valueOf(currentQuantity));
		
		quantityInput.selectAll();
		
		quantityDialog.setTitle(context.getString(R.string.quantity_for) + code);
		quantityDialog.setMessage(description);
		
		PerformStocktake.instance.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		quantityDialog.show();
	}

	public void onClick(DialogInterface dialog, int button) {
		if (dialog == quantityDialog) {
			if (PerformStocktake.instance.getCurrentFocus() != null) {
				input.hideSoftInputFromWindow(PerformStocktake.instance.getCurrentFocus().getWindowToken(), 0);
			}
			
			if (button == DialogInterface.BUTTON_POSITIVE) {
				float quantity;
				
				try {
					quantity = Float.parseFloat(quantityInput.getText().toString());
					
					if (listener != null) {
						listener.onChangeQuantity(quantity);
					}
				} catch (NumberFormatException nfe) {
					// not sure what to do here just yet
				}
			} else if (button == DialogInterface.BUTTON_NEGATIVE) {
				quantityDialog.dismiss();
			}
		}
	}
}
