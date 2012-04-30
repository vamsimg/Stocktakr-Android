package docketplace.stocktakr.data;

public class AppSettings {
	public String storeID;
	public String password;
	public boolean setQuantity;
	
	public String authentication() {
		return storeID + "/" + password;
	}
}
