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


public class Settings extends SherlockActivity implements OnClickListener {
	private EditText storeID;
	private EditText password;
	
	private Button testConnection;
	
	private TestConnection  tester;
	private TransferHandler handler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        
        handler = new TransferHandler(this, "Testing connection", "Login Success", "Login Error");
        
        testConnection = (Button)findViewById(R.id.test_connection);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        loadSettings();
        
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
    	storeID  = (EditText)findViewById(R.id.store_id);
        password = (EditText)findViewById(R.id.password);
        
        AppSettings settings = Database.getSettings();
        
        if (settings != null) {
        	storeID.setText(settings.storeID);
        	password.setText(settings.password);
        }
    }
    
    private void saveSettings() {
    	Database.saveSettings(storeID.getText().toString(), password.getText().toString());
    }

	public void onClick(View v) {
		if (v == testConnection) {
			Log.d("TESTER", "starting");
			
			tester = new TestConnection(handler, storeID.getText().toString(), password.getText().toString());
			
			tester.start();
			
			Log.d("TESTER", "started");
		}
	}
}
