package docketplace.stocktakr.webservice;

import android.os.Message;
import docketplace.stocktakr.data.AppSettings;
import docketplace.stocktakr.data.Database;

public class WebServiceAction extends Thread {
	protected TransferHandler handler;
	
	protected REST rest;
	
	protected AppSettings settings;
	
	public WebServiceAction(TransferHandler handler) {
		this.handler = handler;
		
		settings = Database.getSettings();
		
		rest = new REST(handler, settings.storeID, settings.password);
	}
	
	protected void sendMessage(int type) {
		Message message = handler.obtainMessage(type);
		
		handler.sendMessage(message);
	}
	
	protected void sendMessage(int type, int arg) {
		Message message = handler.obtainMessage(type, arg, 0);
		
		handler.sendMessage(message);
	}
}
