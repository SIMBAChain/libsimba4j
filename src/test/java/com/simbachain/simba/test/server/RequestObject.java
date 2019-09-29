/*
 * Copyright 2019 SIMBA Chain Inc.
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

package com.simbachain.simba.test.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class RequestObject {
    
    @JsonProperty(required = true)
    private String path;
    @JsonProperty (required = true)
    private String method;
    @JsonProperty
    private Map<String, String> headers = new HashMap<>();
    @JsonProperty
    private Map<String, List<String>> parameters = new HashMap<>();
    @JsonProperty
    private Map<String, Object> body = new HashMap<>();
    @JsonProperty (required = true)
    private ResponseObject response;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public ResponseObject getResponse() {
        return response;
    }

    public void setResponse(ResponseObject response) {
        this.response = response;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RequestObject{");
        sb.append("path='")
          .append(path)
          .append('\'');
        sb.append(", method='")
          .append(method)
          .append('\'');
        sb.append(", headers=")
          .append(headers);
        sb.append(", parameters=")
          .append(parameters);
        sb.append(", body=")
          .append(body);
        sb.append(", response=")
          .append(response);
        sb.append('}');
        return sb.toString();
    }
}
