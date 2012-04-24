package docketplace.stocktakr.data;

public class StockRecord {
	public String barcode;
	public String description;
	public int    quantity;

	public StockRecord(String barcode, String description) {
		this.barcode     = barcode;
		this.description = description;
		this.quantity    = 1;
	}

	public int increment() {
		quantity++;

		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
