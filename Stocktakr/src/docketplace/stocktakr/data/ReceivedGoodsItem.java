package docketplace.stocktakr.data;

public class ReceivedGoodsItem {

	public String code;
	public String barcode;
	public String description;
	public double  quantity;
	public String timestamp;

	public ReceivedGoodsItem(String code, String barcode, String description, double quantity) {
		this.code        = code;
		this.barcode     = barcode;
		this.description = description;
		this.quantity    = quantity;		
	}
}
