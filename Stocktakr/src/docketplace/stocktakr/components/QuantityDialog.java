package docketplace.stocktakr.components;

import docketplace.stocktakr.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

public class QuantityDialog implements DialogInterface.OnClickListener {
	private AlertDialog      quantityDialog;
	private QuantityListener listener;	
	private EditText quantityInput; 
	private Context  context;
	
	
	public QuantityDialog(Activity activity, QuantityListener listener) {
	
		this.listener = listener;
		
		context = activity;
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
        
    
	}
	
	public void show(String code, String description, double quantity) {
		quantityInput.setText(String.valueOf(quantity));
		
		quantityInput.selectAll();
		
		quantityDialog.setTitle(context.getString(R.string.quantity_for) + code);
		quantityDialog.setMessage(description);
		
		quantityDialog.show();
		quantityDialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	public void onClick(DialogInterface dialog, int button) {
		if (dialog == quantityDialog) {
			if (button == DialogInterface.BUTTON_POSITIVE) 
			{
				double quantity;
				
				try 
				{
					quantity = Double.parseDouble(quantityInput.getText().toString());
					
					if (listener != null) 
					{
						listener.onChangeQuantity(quantity);
						listener.hideKeyboard();
					}
				} 
				catch (NumberFormatException nfe) 
				{
					// not sure what to do here just yet
				}
			} else if (button == DialogInterface.BUTTON_NEGATIVE) {
				listener.hideKeyboard();
				quantityDialog.dismiss();				
			}
		}
	}
}
