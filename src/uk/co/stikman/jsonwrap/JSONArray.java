package uk.co.stikman.jsonwrap;

public interface JSONArray {
	int length();
	
	JSONObject getJSONObject(int idx);
	JSONArray getJSONArray(int idx);
	int getInt(int idx);
	String getString(int idx);
	float getFloat(int idx);
}
