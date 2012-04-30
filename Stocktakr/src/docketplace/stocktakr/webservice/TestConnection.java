package docketplace.stocktakr.webservice;

import org.json.*;

import android.util.Log;


public class TestConnection extends WebServiceAction {
	public TestConnection(TransferHandler handler, String storeID, String password) {
		super(handler);
		
		rest.storeID  = storeID;
		rest.password = password;
	}
	
	@Override
	public void run() {
		Log.d("TESTER", "running");
		
		Log.d("TESTER", "got settings");
		
		sendMessage(TransferHandler.START_INDETERMINATE);
		
		Log.d("TESTER", "sending message");
		
		JSONObject json = rest.get("TestConnection");
		
		Log.d("TESTER", "got response");
		
		if (json != null) {
			sendMessage(TransferHandler.LOGIN_SUCCESS);
		}
		
		Log.d("TESTER", "exiting thread");
	}
}
