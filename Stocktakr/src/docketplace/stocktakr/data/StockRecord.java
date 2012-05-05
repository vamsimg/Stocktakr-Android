package docketplace.stocktakr.data;

import java.text.*;
import java.util.*;

public class StockRecord {
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public static String currentTimestamp() {
		 return formatter.format(new Date());
	}
	
	public String code;
	public String barcode;
	public String description;
	public double  quantity;
	public String timestamp;

	public StockRecord(String code, String barcode, String description, double quantity, String timestamp) {
		this.code        = code;
		this.barcode     = barcode;
		this.description = description;
		this.quantity    = quantity;
		this.timestamp   = timestamp;
		
	}

	public double increment() {
		quantity++;
		
		this.timestamp = currentTimestamp();

		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
		
		this.timestamp = currentTimestamp();
	}
}
