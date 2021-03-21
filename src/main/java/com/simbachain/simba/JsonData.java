/*
 * Copyright (c) 2020 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.simbachain.simba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple utility for building JSON objects to convert to a map.
 */
public class JsonData {
    
    private Map<String, Object> map = new HashMap<>();

    private JsonData() {
    }

    private JsonData(Map<String, Object> map) {
        this.map.putAll(map);
    }

    public static JsonData jsonData() {
        return new JsonData();
    }
    
    public static JsonData with(String key, String value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Number value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, int value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, float value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, long value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, double value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, short value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, boolean value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, JsonData value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, String[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Number[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, int[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, float[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, double[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, long[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, short[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, boolean[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, JsonData[] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Number[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, int[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, float[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, double[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, long[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, short[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, boolean[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, String[][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Number[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, int[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, float[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, double[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, long[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, short[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, boolean[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, String[][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Number[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, int[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, float[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, double[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, long[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, short[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, boolean[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, String[][][][] value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, List<?> value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Map<String, Object> value) {
        return new JsonData().and(key, value);
    }

    public JsonData and(String key, String value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, Number value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, int value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, float value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, double value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, long value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, short value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, boolean value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, String[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, Number[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, int[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, float[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, double[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, long[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, short[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, boolean[] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, String[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, Number[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, int[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, float[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, double[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, long[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, short[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, boolean[][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, String[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, Number[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, int[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, float[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, double[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, long[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, short[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, boolean[][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, String[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, Number[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, int[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, float[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, double[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, long[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, short[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, boolean[][][][] value) {
        map.put(key, value);
        return this;
    }

    public JsonData and(String key, JsonData[] value) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JsonData jsonData : value) {
            list.add(jsonData.asMap());
        }
        map.put(key, list);
        return this;
    }

    public JsonData and(String key, List<?> value) {
        map.put(key, getList(value));
        return this;
    }

    public JsonData and(String key, Map<String, Object> value) {
        map.put(key, getMap(value));
        return this;
    }

    public JsonData and(String key, JsonData value) {
        map.put(key, value);
        return this;
    }
    
    public Map<String, Object> asMap() {
        Map<String, Object> ret = new HashMap<>();
        for (String s : map.keySet()) {
            Object o = map.get(s);
            if(o instanceof JsonData) {
                ret.put(s, ((JsonData) o).asMap());
            } else {
                ret.put(s, o);
            }
        }
        return ret;
    }
    
    public JsonData copy() {
        return new JsonData(this.map);
    }
    
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonData{");
        sb.append(map);
        sb.append('}');
        return sb.toString();
    }

    @SuppressWarnings ("unchecked")
    private List<Object> getList(List<?> value) {
        List<Object> inserts = new ArrayList<>();
        for (Object o : value) {
            if (o instanceof JsonData) {
                inserts.add((((JsonData) o).asMap()));
            } else if (o instanceof List) {
                inserts.add(getList((List<?>) o));
            } else if (o instanceof Map) {
                inserts.add(getMap((Map<String, Object>) o));
            } else {
                inserts.add(o);
            }
        }
        return inserts;
    }

    @SuppressWarnings ("unchecked")
    private Map<String, Object> getMap(Map<String, Object> value) {
        Map<String, Object> inserts = new HashMap<>();
        for (String s : value.keySet()) {
            Object o = value.get(s);
            if (o instanceof JsonData) {
                inserts.put(s, (((JsonData) o).asMap()));
            } else if (o instanceof List) {
                inserts.put(s, getList((List<?>) o));
            } else if (o instanceof Map) {
                inserts.put(s, getMap((Map<String, Object>) o));
            } else {
                if(isSupported(o.getClass(), o)) {
                    inserts.put(s, o);
                }
            }
        }
        return inserts;
    }
    
    private boolean isSupported(Class<?> cls, Object value) {
        if(cls.equals(int.class)
            || cls.equals(long.class)
            || cls.equals(float.class)
            || cls.equals(double.class)
            || cls.equals(short.class)
            || cls.equals(char.class)
            || cls.equals(boolean.class))  {
            return true;
        }
        if (cls.isArray()) {
            Class<?> type = cls.getComponentType();
            while(type.isArray()) {
                type = type.getComponentType();
            }
            return isSupported(type, null);
        }
        if (Map.class.isAssignableFrom(cls)) {
            return true;
        }
        if (List.class.isAssignableFrom(cls)) {
            if (value != null) {
                if (!((List<?>) value).isEmpty()) {
                    Object v = ((List<?>) value).get(0);
                    return isSupported(v.getClass(), v);
                }
            }
            return false;
        }
        if (JsonData.class.isAssignableFrom(cls)) {
            return true;
        }
        if (Number.class.isAssignableFrom(cls)) {
            return true;
        }
        if (String.class.isAssignableFrom(cls)) {
            return true;
        }
        return Boolean.class.isAssignableFrom(cls);
    }
    
}
