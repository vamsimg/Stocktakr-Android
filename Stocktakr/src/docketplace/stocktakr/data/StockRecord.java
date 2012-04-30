package docketplace.stocktakr.data;

import java.text.*;
import java.util.*;

public class StockRecord {
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private static String currentTimestamp() {
		 return formatter.format(new Date());
	}
	
	public String code;
	public String barcode;
	public String description;
	public float  quantity;
	public String timestamp;

	public StockRecord(String code, String barcode, String description) {
		this.code        = code;
		this.barcode     = barcode;
		this.description = description;
		this.quantity    = 1;
		
		this.timestamp = currentTimestamp();
	}

	public float increment() {
		quantity++;
		
		this.timestamp = currentTimestamp();

		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
		
		this.timestamp = currentTimestamp();
	}
}
