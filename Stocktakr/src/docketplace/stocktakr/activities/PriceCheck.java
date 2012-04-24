package docketplace.stocktakr.activities;

import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.MenuItem;

import android.os.*;
import android.app.*;
import android.content.*;
import android.database.*;
import android.view.KeyEvent;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.TextView.*;


public class PriceCheck extends SherlockActivity implements OnEditorActionListener {
	private EditText barcode;

	private TextView scanned;
	private TextView description;
	private TextView salePrice;


	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.price_check);

        barcode     = (EditText)findViewById(R.id.price_check_barcode);
        scanned     = (TextView)findViewById(R.id.price_check_scanned_barcode);
        description = (TextView)findViewById(R.id.price_check_description_label);
        salePrice   = (TextView)findViewById(R.id.price_check_sale_price_label);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barcode.setOnEditorActionListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Stocktakr.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


	private void showAlert() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage("Could not find a product matching this code");

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di6alog, int which) {
					alertDialog.cancel();
				}
		});

		alertDialog.show();
	}

	private void searchProducts() {
		String search = barcode.getText().toString().trim().toLowerCase();

		String productBarcode;
		String productDescription;
		String productSalePrice;
		
		if (!search.equals("")) {
			Cursor results = Database.db.query("products", new String[] {"barcode", "description", "sale_price"}, "(barcode = ?)", new String[] {search}, null, null, null);
			
			if (results.getCount() == 0) {
				scanned.setText("");
				description.setText("");
				salePrice.setText("");

				showAlert();


			} else {
				results.moveToFirst();
				
				productBarcode     = results.getString(0);
				productDescription = results.getString(1);
				productSalePrice   = results.getString(2);
				
				scanned.setText("Barcode: " + productBarcode);
				description.setText(productDescription);
				salePrice.setText("Sale price: " + productSalePrice);
			}

			results.close();
		}
	}


	public boolean onEditorAction(TextView view, int action, KeyEvent event) {
		if (view == barcode) {
			if ((action == EditorInfo.IME_ACTION_SEARCH) || ((event != null) && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
				searchProducts();

				return true;
			}
			
			barcode.setText("");
		}

		return false;
	}
}
 