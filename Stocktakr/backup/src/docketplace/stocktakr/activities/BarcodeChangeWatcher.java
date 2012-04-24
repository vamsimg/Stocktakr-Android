package docketplace.stocktakr.activities;

import docketplace.stocktakr.fragments.ScanProducts;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class BarcodeChangeWatcher implements TextWatcher {
	ScanProducts stocktake;
	
	public BarcodeChangeWatcher(ScanProducts stocktake) {
		this.stocktake = stocktake;
	}

	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		/*stocktake.description.setText("");
		stocktake.costPrice.setText("");
		stocktake.salePrice.setText("");
		stocktake.quantityLabel.setText("");
		stocktake.quantity.setVisibility(View.INVISIBLE);
		stocktake.updateQuantity.setVisibility(View.INVISIBLE);
		
		stocktake.updateQuantity.setVisibility(View.INVISIBLE);*/
	}
}
