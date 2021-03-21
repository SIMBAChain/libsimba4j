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

package com.simbachain.simba.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbachain.simba.JsonData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class JsonDataTest {

    @Test
    public void testMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("string", "Hello There");
        map.put("int", 12);
        map.put("float", 12.4);
        map.put("boolean", true);
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("string", "Item 1");
        map1.put("int", 1);
        map1.put("float", 1.4);
        map1.put("boolean", true);
        maps.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("string", "Item 2");
        map2.put("int", 2);
        map2.put("float", 2.4);
        map2.put("boolean", true);
        maps.add(map2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("string", "Item 3");
        map3.put("int", 2);
        map3.put("float", 2.4);
        map3.put("boolean", true);
        map.put("Boolean", new Boolean(true));
        map.put("map", map3);
        map.put("maps", maps);
        JsonData field = JsonData.with("string", "Hello Data")
            .and("int", 3)
            .and("float", 3.4)
            .and("boolean", true);
        map.put("data", field);
        int[][][] nums = {{{1, 2, 3, 4}, {5, 6, 7, 8}}, {{9, 10, 11, 12, 13}, {14, 15, 16, 17, 18}}};
        double[][][] doubles = {{{1.1, 2.1, 3.1, 4.1}, {5.1, 6.1, 7.1, 8.1}}, {{9.1, 10.1, 11.1, 12.1, 13.1}, {14.1, 15.1, 16.1, 17.1, 18.1}}};
        map.put("nums", nums);
        JsonData data = JsonData.with("map", map)
            .and("Boolean", Boolean.TRUE)
            .and("doubles", doubles);
        Map<String, Object> d = data.asMap();
        assertEquals(Boolean.TRUE, d.get("Boolean"));
        Map<String, Object> output = (Map<String, Object>) d.get("map");
        assertEquals("Hello There", output.get("string"));
        assertEquals(12, output.get("int"));
        assertEquals(12.4, output.get("float"));
        assertEquals(true, output.get("boolean"));
        assertEquals(Boolean.TRUE, output.get("Boolean"));
        List<Map<String, Object>> resultMaps = (List<Map<String, Object>>) output.get("maps");
        assertEquals(map1, resultMaps.get(0));
        assertEquals(map2, resultMaps.get(1));
        assertEquals(map3, output.get("map"));
        assertEquals(field.asMap(), output.get("data"));
        assertEquals(nums, output.get("nums"));
    }
}
