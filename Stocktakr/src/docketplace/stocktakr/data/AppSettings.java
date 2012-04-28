package docketplace.stocktakr.data;

public class AppSettings {
	public String storeID;
	public String password;
	
	public String authentication() {
		return storeID + "/" + password;
	}
}
