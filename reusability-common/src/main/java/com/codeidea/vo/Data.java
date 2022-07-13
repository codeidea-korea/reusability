package com.codeidea.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

@SuppressWarnings("rawtypes")
public class Data extends HashMap<Object, Object> {

	static final long serialVersionUID = 1L;

	public Data() {
		super();
	}

	public Data(Map map) {
		super();
		this.addAll(map);
	}

	@SuppressWarnings("unchecked")
	public void addAll(Map map) {

		Iterator i$ = map.entrySet().iterator();

		do {
			if (!i$.hasNext()) {
				break;
			}

			java.util.Map.Entry entry = (java.util.Map.Entry) i$.next();
			Object value = entry.getValue();

			if (value != null) {
				Object toadd;
				if (value instanceof String[]) {
					String values[] = (String[]) (String[]) value;
					if (values.length > 1) {
						toadd = new ArrayList(Arrays.asList(values));
					} else {
						toadd = values[0];
					}
				} else {
					toadd = value;
				}

				this.put(((String) entry.getKey()), toadd);
			}
		} while (true);
	}

	@Override
	public Object get(Object key) {

		if (super.containsKey(key)) {
			return super.get(key);
		}

		try {
			String usKey = convertCamelCaseToUnderscores(key);

			if (super.containsKey(usKey)) {
				return super.get(usKey);
			} else if (super.containsKey(usKey.toLowerCase())) {
				return super.get(usKey.toLowerCase());
			}
		} catch (Exception e) {}

		return null;
	}

	public static Data toData(JSONObject jsonObj) throws Exception {

		Data data = new Data();

		Iterator<?> keys = jsonObj.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = jsonObj.get(key);

			if (value instanceof Map) {
				data.put(key, toMap((JSONObject) value));
			} else {
				data.put(key, value);
			}
		}

		jsonObjValueNullCheck(data);
		return data;
	}

	public static Data toData(String jsonStr) throws Exception {

		return toData(new JSONObject(jsonStr));
	}

	public void putJsonString(String jsonStr) {

		try {
			Map<String, Object> map = toMap(jsonStr);

			for (String key : map.keySet()) {
				this.put(key, map.get(key));
			}
		} catch (Exception e) {}
	}

	public Data getData(String key) {

		Object obj = this.get(key);

		try {
			if (obj instanceof JSONObject) {
				return toData((JSONObject) obj);
			} else {
				return (Data) obj;
			}
		} catch (Exception e) {}

		return null;
	}

	public String getString(Object key) {

		Object obj = this.get(key);

		try {
			return obj.toString();
		} catch (Exception e) {}

		return "";
	}

	public float getFloat(Object key) {

		try {
			return Float.valueOf(this.getString(key)).floatValue();
		} catch (Exception e) {}

		return 0;
	}

	public int getInt(Object key) {

		Object obj = this.get(key);

		try {
			if (obj instanceof java.lang.Number) {
				return ((Number) obj).intValue();
			}

			return Integer.parseInt(obj.toString());
		} catch (Exception e) {}

		return 0;
	}

	public long getLong(Object key) {

		try {
			return Long.valueOf(this.getString(key), 10).longValue();
		} catch (Exception e) {}

		return 0;
	}

	public double getDouble(Object key) {

		try {
			return Double.valueOf(this.getString(key)).doubleValue();
		} catch (Exception e) {}

		return 0;
	}

	private static Map<String, Object> toMap(JSONObject jsonObj) throws Exception {

		Map<String, Object> map = new HashMap<>();

		Iterator<?> keys = jsonObj.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = jsonObj.get(key);

			if (value instanceof Map) {
				map.put(key, toMap((JSONObject) value));
			} else {
				map.put(key, value);
			}
		}

		jsonObjValueNullCheck(map);
		return map;
	}

	private static Map<String, Object> toMap(String jsonStr) throws Exception {

		return toMap(new JSONObject(jsonStr));
	}

	private static void jsonObjValueNullCheck(Map<?, Object> map) {

		try {
			for (Entry<?, Object> e : map.entrySet()) {
				if (e.getValue() == JSONObject.NULL) {
					e.setValue("");
				}
			}
		} catch (Exception e) {}
	}

	private String convertCamelCaseToUnderscores(Object key) {

		if (ObjectUtils.isEmpty(key)) {
			return null;
		}

		return key.toString().replaceAll("([a-z0-9])([A-Z]+)", "$1_$2").toUpperCase();
	}
}
