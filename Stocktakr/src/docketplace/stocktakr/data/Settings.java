package docketplace.stocktakr.data;

public class Settings {
	public String storeID;
	public String password;
	
	public String authentication() {
		return storeID + "/" + password;
	}
}
