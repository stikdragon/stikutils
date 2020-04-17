package uk.co.stikman.jsonwrap;

public interface JSONObject {
	JSONObject getJSONObject(String idx);

	JSONArray getJSONArray(String idx);

	int getInt(String idx);

	String getString(String idx);

	boolean has(String idx);

	/**
	 * returns <code>null</code> if missing
	 * 
	 * @param idx
	 * @return
	 */
	JSONArray optJSONArray(String idx);

	/**
	 * returns <code>def</code> if missing
	 * 
	 * @param idx
	 * @param def
	 * @return
	 */
	String optString(String idx, String def);

	float getFloat(String idx);

}
