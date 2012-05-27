package docketplace.stocktakr.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.json.*;

import docketplace.stocktakr.data.*;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;


public class DownloadProducts extends WebServiceAction {
	
	private Context currentContext;
	final int BUFFER = 2048;
	
	public DownloadProducts(TransferHandler handler, Context now) {	
		super(handler);
		currentContext = now;	
	}
	
	private int getProductCount() {
		JSONObject json = rest.get("ItemCount");
		
		int count = 0;
		
		try {
			if (json != null) {
				count = json.getInt("itemCount");

			} else {
				Log.d("DOWNLOAD", "null count JSON");
			}
		} catch (JSONException je) {
			Log.d("DOWNLOAD", "JSON exception: " + je.getMessage());
		}
		
		return count;
	}
	
	
	private boolean getProducts(int start, int pageSize) {
		JSONObject json;
		
		boolean success = false;
		
		try {
			json = null;
			
			if (pageSize > 0) {
				//Log.d("DOWNLOAD", "Begin product download");
				String deviceDetails = URLEncoder.encode((Build.BRAND + "$" + Build.MODEL  + "$" + Build.VERSION.SDK_INT).replaceAll(" ",""));
				json = rest.get("ZippedItems", deviceDetails + "/" + start + "/" + pageSize);
				
				Log.d("DOWNLOAD", "End product download");
			} else {
				Log.d("DOWNLOAD", "No products");
			}
			
			if (json != null) {
				Log.d("DOWNLOAD", "not-null product JSON");
				
				String data = json.getString("zippedText");
				
				String unzippedItems = unzipData(data);
				
				JSONArray items = new JSONArray(unzippedItems);
				
				int itemCount = json.getInt("itemCount");
				
				success = Database.insertProductList(itemCount, start, items, handler);
								
				Log.d("DOWNLOAD", "stored products: "  + itemCount);				
				
			} else {
				Log.d("DOWNLOAD", "null product JSON");
			}
		} catch (JSONException je) {
			Log.d("DOWNLOAD", "JSON exception: " + je.getMessage());
		}
		
		return success;
	}

	private String unzipData(String data)  {
		String unzippedItems = "";
		
		try {
			byte[] compressedData = Base64.decode(data, Base64.DEFAULT);
			
			FileOutputStream fos = currentContext.openFileOutput("temp.zip", Context.MODE_PRIVATE);
			fos.write(compressedData);
			fos.close();
			

			 BufferedOutputStream dest = null;
			 BufferedInputStream is = null;
			 ZipEntry entry;
			 ZipFile zipfile = new ZipFile(currentContext.getFileStreamPath("temp.zip"));
			 Enumeration<? extends ZipEntry> e = zipfile.entries();
			 while(e.hasMoreElements()) {
			    entry = (ZipEntry) e.nextElement();
			    
			    is = new BufferedInputStream (zipfile.getInputStream(entry));
			    int zipCount;
			    byte zipData[] = new byte[BUFFER];
			    FileOutputStream newfos = currentContext.openFileOutput("data",Context.MODE_PRIVATE);
			    dest = new  BufferedOutputStream(newfos, BUFFER);
			    while ((zipCount = is.read(zipData, 0, BUFFER)) != -1) {
			       dest.write(zipData, 0, zipCount);
			    }
			    dest.flush();
			    dest.close();
			    is.close();
			 }
			
			
			
			FileInputStream fstream = new FileInputStream(currentContext.getFilesDir() + "/data"); 
								
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fstream));
			String aDataRow = "";
			
			while ((aDataRow = myReader.readLine()) != null) {
				unzippedItems += aDataRow;
			}								
			
			//Cleanup 
			File deleteFile = new File(currentContext.getFilesDir() + "/data");
			deleteFile.delete();
			
			deleteFile =  new File(currentContext.getFilesDir() + "/temp.zip");
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
		
		return unzippedItems;
	}
	
	@Override
	public void run() {
		settings = Database.getSettings();
		
		rest = new REST(handler, settings.storeID, settings.password);
		
		
		sendMessage(TransferHandler.START);
		
		int count = getProductCount();
		
		int i = 0;
		
		int blockSize = 5000;
		
		int limit;
		
		boolean success = true;
		
		if (count > 0) {
			Log.d("DOWNLOAD", "Product count: " + count);
						
			
			Database.db.execSQL("DELETE FROM products;");
			
			sendMessage(TransferHandler.BEGIN, count);
			
			while (i < count) {
				if ((i + blockSize) <= count) {
					limit  = blockSize;
				} else  {
					limit = (count - i);
				}
				
				success = getProducts(i, limit);
				
				if (!success) break;
				
				i += blockSize;
			}
			
			if (success) {
				sendMessage(TransferHandler.COMPLETE);
			} else  {
				sendMessage(TransferHandler.ERROR);
			}			
		}
		else
		{
			sendMessage(TransferHandler.NO_PRODUCTS);		
		}
	}
}
