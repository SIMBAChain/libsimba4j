/*
 * Copyright (c) 2019 SIMBA Chain Inc.
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

import java.util.HashMap;
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
    
    public static JsonData with(String key, String value) {
        return new JsonData().and(key, value);
    }

    public static JsonData with(String key, Number value) {
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

    public static JsonData with(String key, boolean[] value) {
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

    public JsonData and(String key, boolean[] value) {
        map.put(key, value);
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
}
