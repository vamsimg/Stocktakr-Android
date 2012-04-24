package docketplace.stocktakr.activities;

import java.util.Date;

public class Product {
	public String  code;
	public String  barcode;
	public String  description;
	public float   costPrice;
	public float   salePrice;
	public double  quantity;
	public boolean isStatic;
	public Date    modified;
	
	public Product(String code, String barcode, String description, float costPrice, float salePrice, double quantity, boolean isStatic, Date modified) {
		this.code        = code;
		this.barcode     = barcode;
		this.description = description;
		this.costPrice   = costPrice;
		this.salePrice   = salePrice;
		this.quantity    = quantity;
		this.isStatic    = isStatic;
		this.modified    = modified;
	}
}
