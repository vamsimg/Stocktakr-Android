package docketplace.stocktakr.webservice;

import org.json.*;

public class JSONResponse {
	public static final int SUCCESS = 0;
	public static final int ERROR   = 1;
	
	public JSONObject json;
	
	public int status;
	
	public JSONResponse(JSONObject json, int status) {
		this.json   = json;
		this.status = status;
	}
	
	public String toString() {
		return json.toString();
	}
	
	public String getString(String name) throws JSONException {
		return json.getString(name);
	}
	
	public JSONObject getJSONObject(String name) throws JSONException {
		return json.getJSONObject(name);
	}
	
	public JSONArray getJSONArray(String name) throws JSONException {
		return json.getJSONArray(name);
	}
}
