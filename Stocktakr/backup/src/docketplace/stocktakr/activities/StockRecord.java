package docketplace.stocktakr.activities;

public class StockRecord {
	public String barcode;
	public int    quantity;
	
	public StockRecord(String barcode) {
		this.barcode  = barcode;
		this.quantity = 1;
	}
	
	public int increment() {
		quantity++;
		
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
