package com.vaultid.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description of Helper
 * 
 * @author Paulo Filipe Macedo dos Santos <paulo.filipe@live.com>
 */
public class Helper {

    public static String getControllerName(String path) {
        return Helper.toCamelCase(path.replace("-", " ").replace("/", "") + " controller");
    }

    public static String toCamelCase(String strData) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(strData, " ");
        while (st.hasMoreTokens()) {
            String strWord = st.nextToken();
            sb.append(Character.toUpperCase(strWord.charAt(0)));
            sb.append(strWord.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
