package docketplace.stocktakr.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.json.*;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import docketplace.stocktakr.components.SubmissionListener;
import docketplace.stocktakr.data.*;


public class SubmitStockRecords extends WebServiceAction {
	private String personName;
	private Context currentContext;
	private SubmissionListener listener;
	
	public SubmitStockRecords(TransferHandler handler, String personName, SubmissionListener listener, Context now) {	
		super(handler);
		currentContext = now;	
		this.personName = personName;
		this.listener = listener;
	}
	
	private JSONObject buildStockRecord(StockRecord record) throws JSONException {
		JSONObject json = new JSONObject();		
		
		json.put("product_code", record.code);
		json.put("product_barcode", record.barcode);
		json.put("description", record.description);
		json.put("quantity", record.quantity);		
		json.put("stocktake_datetime", record.timestamp);
		
		return json;
	}
	
	private JSONArray buildStockList() throws JSONException {
		JSONArray json = new JSONArray();
		
		for (StockRecord record : Database.stock) {
			json.put(buildStockRecord(record));
		}

		return json;
	}
	
	
	private String zipData(String stockList)  {
		
		String base64encList = null;
		
		try{
			
			 File f = new File(currentContext.getFilesDir() + "/temp.zip");
			 ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
			 
			 ZipEntry e = new ZipEntry("data");
			  out.putNextEntry(e);
			
			 byte[] data = stockList.getBytes();
			 out.write(data, 0, data.length);
			 out.closeEntry();			
			 out.close();
			 
			//Cleanup 		
			
			File deleteFile =  new File(currentContext.getFilesDir() + "/temp.zip");
			
			base64encList = Base64.encodeToString(getFileBytes(deleteFile) , Base64.DEFAULT);
			
			deleteFile.delete();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return base64encList;
	}
	
	private static byte[] getFileBytes(File file) throws IOException {
	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    try {
	        byte[] buffer = new byte[4096];
	        ous = new ByteArrayOutputStream();
	        ios = new FileInputStream(file);
	        int read = 0;
	        while ((read = ios.read(buffer)) != -1)
	            ous.write(buffer, 0, read);
	    } finally {
	        try {
	            if (ous != null)
	                ous.close();
	        } catch (IOException e) {
	            // swallow, since not that important
	        }
	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {
	            // swallow, since not that important
	        }
	    }
	    return ous.toByteArray();
	}
	
	
	@Override
	public void run() {
		
		Log.d("SUBMIT", "beginning");
			
		sendMessage(TransferHandler.START);
				
		try {
		
			String zippedTransactions = zipData(buildStockList().toString());			
			
			
			Log.d("SUBMIT", "posting");
			String deviceDetails = URLEncoder.encode((Build.BRAND + "$" + Build.MODEL  + "$" + Build.VERSION.SDK_INT).replaceAll(" ",""));
			
			int responseCode = rest.post("ZippedStocktakeTransactions", deviceDetails + "/" + personName, zippedTransactions);
			
			Log.d("SUBMIT", "posted");
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				Log.d("SUBMIT", "success");
				
				Database.stock.clear();
				
				if (listener != null) {
					listener.submitComplete();
				}
				
				sendMessage(TransferHandler.COMPLETE);
			}
		} catch (JSONException e) {
			Log.d("SUBMIT", "Exception: " + e.getMessage());
			
			
			sendMessage(TransferHandler.ERROR);
		}
		
		Log.d("SUBMIT", "exiting thread");
	}
}
