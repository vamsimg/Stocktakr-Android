package docketplace.stocktakr.activities;


import docketplace.stocktakr.*;
import docketplace.stocktakr.data.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

import android.os.*;
import android.widget.*;
import android.content.*;
import android.database.*;


public class Settings extends SherlockActivity {
	private EditText storeID;
	private EditText password;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        loadSettings();
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
    	storeID  = (EditText)findViewById(R.id.store_id);
        password = (EditText)findViewById(R.id.password);
        
        Cursor results = Database.db.query("settings", new String[] {"store_id", "password"}, null, null, null, null, null);
        
        if (results.getCount() > 0) {
        	results.moveToFirst();
        	
        	storeID.setText(results.getString(0));
        	password.setText(results.getString(1));
        }
        
        results.close();
    }
    
    private void saveSettings() {
    	ContentValues values = new ContentValues();
    	
    	values.put("store_id", storeID.getText().toString());
    	values.put("password", password.getText().toString());
    	
    	Database.db.update("settings", values, null, null);
    }
}
