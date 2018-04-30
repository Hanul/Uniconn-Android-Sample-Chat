package io.uppercase.unicorn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

public class JSONUtil {

    /**
     * extend array.
     *
     * @param originArray
     * @param extendArray
     * @throws JSONException
     */
    public static void extendArray(JSONArray originArray, JSONArray extendArray) throws JSONException {

        for (int i = 0; i < extendArray.length(); i += 1) {
            Object value = extendArray.get(i);

            // when value is Date type
            if (value instanceof Date) {
                originArray.put(new Date(((Date) value).getTime()));
            }

            // when value is data
            else if (value instanceof JSONObject) {
                originArray.put(copyData((JSONObject) value));
            }

            // when value is array
            else if (value instanceof JSONArray) {
                originArray.put(copyArray((JSONArray) value));
            }

            else {
                originArray.put(value);
            }
        }
    }

    /**
     * extend data.
     *
     * @param originData
     * @param extendData
     * @throws JSONException
     */
    public static void extendData(JSONObject originData, JSONObject extendData) throws JSONException {

        Iterator<String> iterator = extendData.keys();

        while (iterator.hasNext()) {

            String name = iterator.next();
            Object value = extendData.get(name);

            // when value is Date type
            if (value instanceof Date) {
                originData.put(name, new Date(((Date) value).getTime()));
            }

            // when value is data
            else if (value instanceof JSONObject) {
                originData.put(name, copyData((JSONObject) value));
            }

            // when value is array
            else if (value instanceof JSONArray) {
                originData.put(name, copyArray((JSONArray) value));
            }

            else {
                originData.put(name, value);
            }
        }
    }

    /**
     * copy array.
     *
     * @param jsonArray
     * @return copiedArray
     * @throws JSONException
     */
    public static JSONArray copyArray(JSONArray jsonArray) throws JSONException {
        JSONArray copy = new JSONArray();
        extendArray(copy, jsonArray);
        return copy;
    }

    /**
     * copy data.
     *
     * @param json
     * @return copiedData
     * @throws JSONException
     */
    public static JSONObject copyData(JSONObject json) throws JSONException {
        JSONObject copy = new JSONObject();
        extendData(copy, json);
        return copy;
    }

    /**
     * pack data with Date type.
     *
     * @param json
     * @return packedData
     * @throws JSONException
     */
    public static JSONObject packData(JSONObject json) throws JSONException {

        JSONObject result = null;

        // result
        result = copyData(json);

        // date attribute names
        JSONArray dateAttrNames = new JSONArray();

        Iterator<String> iterator = result.keys();

        while (iterator.hasNext()) {

            String name = iterator.next();
            Object value = result.get(name);

            // when value is Date type
            if (value instanceof Date) {

                // change to timestamp integer.
                result.put(name, ((Date) value).getTime());
                dateAttrNames.put(name);
            }

            // when value is data
            else if (value instanceof JSONObject) {
                result.put(name, packData((JSONObject) value));
            }

            // when value is array
            else if (value instanceof JSONArray) {

                for (int i = 0; i < ((JSONArray) value).length(); i += 1) {
                    Object v = ((JSONArray) value).get(i);

                    if (v instanceof JSONObject) {
                        ((JSONArray) value).put(i, packData((JSONObject) v));
                    }
                }
            }
        }

        result.put("__D", dateAttrNames);

        return result;
    }

    /**
     * unpack data with Date type.
     *
     * @param json
     * @return unpackedData
     * @throws JSONException
     */
    public static JSONObject unpackData(JSONObject json) throws JSONException {

        JSONObject result = null;

        // result
        result = copyData(json);

        // when date attribute names exists
        if (!result.isNull("__D")) {

            // change timestamp integer to Date type.
            for (int i = 0; i < ((JSONArray) result.get("__D")).length(); i += 1) {
                String dateAttrName = (String) ((JSONArray) result.get("__D")).get(i);
                result.put(dateAttrName, new Date((Long) result.get(dateAttrName)));
            }
            result.remove("__D");
        }

        Iterator<String> iterator = result.keys();

        while (iterator.hasNext()) {

            String name = iterator.next();
            Object value = result.get(name);

            // when value is data
            if (value instanceof JSONObject) {
                result.put(name, unpackData((JSONObject) value));
            }

            // when value is array
            else if (value instanceof JSONArray) {

                for (int i = 0; i < ((JSONArray) value).length(); i += 1) {
                    Object v = ((JSONArray) value).get(i);

                    if (v instanceof JSONObject) {
                        ((JSONArray) value).put(i, unpackData((JSONObject) v));
                    }
                }
            }
        }

        return result;
    }
}
