/*
 * Copyright 2020 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.simbachain.simba.test;

import java.net.URLDecoder;

import com.simbachain.simba.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class QueryTest {

    @Test
    public void testQueryString() throws Exception {
        String q = Query.gt("key1", 1)
                        .eq("key2", 2)
                        .ex("key3", "val3")
                        .lt("key4", 4)
                        .lte("key5", 5)
                        .gte("key5", 5)
                        .in("key6", "val6")
                        .is("key7", true)
                        .toString();

        assertEquals(q,
            "?key1_gt=1&key2_equals=2&key3_exact=val3&key4_lt=4&key5_lte=5&key5_gte=5&key6_contains=val6&key7_exact=true");

        assertEquals(q.substring(1), URLDecoder.decode(q.substring(1), "UTF-8"));

        String q1 = Query.in("sentBy", "Java Hello!! $()")
                         .ex("assetId", "1234")
                         .in("chatRoom", "foo bar £££& poo 34")
                         .toString();
        assertEquals(q1,
            "?sentBy_contains=Java+Hello%21%21+%24%28%29&assetId_exact=1234&chatRoom_contains=foo+bar+%C2%A3%C2%A3%C2%A3%26+poo+34");

        assertEquals(
            "sentBy_contains=Java Hello!! $()&assetId_exact=1234&chatRoom_contains=foo bar £££& poo 34",
            URLDecoder.decode(q1.substring(1), "UTF-8"));
    }

}
