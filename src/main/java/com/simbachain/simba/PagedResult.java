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

import java.util.List;

/**
 *  This class encapsulates paged results. It contains a total count of results
 *  as well as a next and previous URL. 
 *  
 *  When a paged result is returned, the getNext() and getPrevious() methods can be called
 *  to retrieve the appropriate page URL and a Simba instance's next() or previous()
 *  methods can be called using theis class as a parameter.
 *  If there is no appropriate page null is returned.
 *  
 */
public class PagedResult<R> {
    
    private String method = null;
    private int count;
    private String next;
    private String previous;
    private List<? extends R> results;
    

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<? extends R> getResults() {
        return results;
    }

    public void setResults(List<? extends R> results) {
        this.results = results;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PagedResult{");
        sb.append("count=")
          .append(count);
        sb.append(", next='")
          .append(next)
          .append('\'');
        sb.append(", previous='")
          .append(previous)
          .append('\'');
        sb.append(", results=")
          .append(results);
        sb.append('}');
        return sb.toString();
    }
}
