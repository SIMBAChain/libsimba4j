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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *  Query parameters for querying for transactions.
 *  Strings accept 'equals' and 'contains' semantics, boolean accept 'equals'
 *  semantics and numbers accept 'greater than', 'less than', 'greater than or equal',
 *  'less than or equal' and 'equals' semantics.
 *  
 *  Usage example:
 *  <pre>
 *  Query.Params params = Query.in("name", "Java").ex("assetId", "1234").gte("number", 10);    
 *  </pre>
 *  
 */
public class Query {
    
    
    public static class Param<T> {
        String name;
        T value;

        Param(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }
        
        protected String asString(String op) {
            try {
                return name + op + "=" + URLEncoder.encode(value.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 not supported!");
            }
        }
    }
    
    public static class Gt extends Param<Number> {
        
        public Gt(String name, Number value) {
            super(name, value);
        }
        
        public String toString() {
            return asString("_gt");
        }
    }
    public static class Lt extends Param<Number> {

        public Lt(String name, Number value) {
            super(name, value);
        }

        public String toString() {
            return asString("_lt");
        }
        
    }
    public static class Gte extends Param<Number> {

        public Gte(String name, Number value) {
            super(name, value);
        }

        public String toString() {
            return asString("_gte");
        }
    }
    public static class Lte extends Param<Number> {

        public Lte(String name, Number value) {
            super(name, value);
        }

        public String toString() {
            return asString("_lte");
        }
    }
    public static class Eq extends Param<Number> {

        public Eq(String name, Number value) {
            super(name, value);
        }

        public String toString() {
            return asString("_equals");
        }
    }
    public static class Ex extends Param<String> {

        public Ex(String name, String value) {
            super(name, value);
        }

        public String toString() {
            return asString("_exact");
        }
    }
    public static class In extends Param<String> {

        public In(String name, String value) {
            super(name, value);
        }

        public String toString() {
            return asString("_contains");
        }
    }
    public static class Is extends Param<Boolean> {

        public Is(String name, Boolean value) {
            super(name, value);
        }

        public String toString() {
            return asString("_exact");
        }
    }
    
    public static class Params {
        private List<Param<?>> params = new ArrayList<>();
        
        public Params gt(String name, Number value) {
            params.add(new Gt(name, value));
            return this;
        }

        public Params lt(String name, Number value) {
            params.add(new Lt(name, value));
            return this;
        }

        public Params gte(String name, Number value) {
            params.add(new Gte(name, value));
            return this;
        }

        public Params lte(String name, Number value) {
            params.add(new Lte(name, value));
            return this;
        }

        public Params eq(String name, Number value) {
            params.add(new Eq(name, value));
            return this;
        }

        public Params ex(String name, String value) {
            params.add(new Ex(name, value));
            return this;
        }

        public Params in(String name, String value) {
            params.add(new In(name, value));
            return this;
        }

        public Params is(String name, Boolean value) {
            params.add(new Is(name, value));
            return this;
        }

        public List<Param<?>> getParams() {
            return params;
        }
        
        public String toString() {
            if(params.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder("?");
            for (int i = 0; i < params.size(); i++) {
                Param<?> param = params.get(i);
                sb.append(param.toString());
                if(i < params.size() - 1) {
                    sb.append("&");
                }
            }
            return sb.toString();
        }
    }

    public static Params empty() {
        return new Params();
    }
    
    public static Params gt(String name, Number value) {
        Params p = new Params();
        p.gt(name, value);
        return p;
    }

    public static Params lt(String name, Number value) {
        Params p = new Params();
        p.lt(name, value);
        return p;
    }

    public static Params gte(String name, Number value) {
        Params p = new Params();
        p.gte(name, value);
        return p;
    }

    public static Params lte(String name, Number value) {
        Params p = new Params();
        p.lte(name, value);
        return p;
    }

    public static Params eq(String name, Number value) {
        Params p = new Params();
        p.eq(name, value);
        return p;
    }

    public static Params ex(String name, String value) {
        Params p = new Params();
        p.ex(name, value);
        return p;
    }

    public static Params in(String name, String value) {
        Params p = new Params();
        p.in(name, value);
        return p;
    }

    public static Params is(String name, Boolean value) {
        Params p = new Params();
        p.is(name, value);
        return p;
    }
    
}
