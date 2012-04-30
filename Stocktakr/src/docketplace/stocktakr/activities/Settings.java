package docketplace.stocktakr.activities;


import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;
import docketplace.stocktakr.webservice.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.content.*;
import android.database.*;


public class Settings extends SherlockActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
	private EditText storeID;
	private EditText password;
	private ToggleButton   setQuantity;
	
	private Button testConnection;
	
	private TestConnection  tester;
	private TransferHandler handler;
	
	private boolean setQuantityChecked;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        
        storeID     = (EditText)findViewById(R.id.store_id);
        password    = (EditText)findViewById(R.id.password);
        setQuantity = (ToggleButton)findViewById(R.id.set_quantity);
        
        handler = new TransferHandler(this, "Testing connection", "Login Success", "Login Error");
        
        testConnection = (Button)findViewById(R.id.test_connection);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        loadSettings();
        
        setQuantity.setOnCheckedChangeListener(this);
        
        testConnection.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	saveSettings();
            	
                Intent intent = new Intent(this, Stocktakr.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed() {
    	saveSettings();
    	
    	finish();
    }
    
    private void loadSettings() {
        AppSettings settings = Database.getSettings();
        
        if (settings != null) {
        	storeID.setText(settings.storeID);
        	password.setText(settings.password);
        	setQuantity.setChecked(settings.setQuantity);
        	
        	setQuantityChecked = settings.setQuantity;
        }
    }
    
    private void saveSettings() {
    	Database.saveSettings(storeID.getText().toString(), password.getText().toString(), setQuantityChecked);
    }

	public void onClick(View v) {
		if (v == testConnection) {
			Log.d("TESTER", "starting");
			
			String store_ID = storeID.getText().toString();
			
			String pwd = password.getText().toString();
			
			if(store_ID.length() != 0 && pwd.length()!= 0)
			{
				tester = new TestConnection(handler, store_ID,pwd);				
				tester.start();
				Log.d("TESTER", "started");
			}
			else
			{
				Toast.makeText(this, "Enter a StoreID and Password", Toast.LENGTH_SHORT).show();
			}
			

		}
	}

	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		if (button == setQuantity) {
			setQuantityChecked = isChecked;
		}
	}
}
