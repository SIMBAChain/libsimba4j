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

package com.simbachain.simba.com;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simbachain.simba.Method;
import com.simbachain.simba.Parameter;

/**
 *  Represents a method in the application metadata.
 */
public class AppMethod implements Method {
    
    private Map<String, AppParameter> parameters = new HashMap<>();

    public Map<String, AppParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, AppParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    @JsonIgnore
    public Map<String, Parameter> getParameterMap() {
        return new HashMap<>(parameters);
    }

    @Override
    @JsonIgnore
    public Parameter getParameter(String name) {
        return getParameters().get(name);
    }

    @Override
    public Set<String> getParameterNames() {
        return getParameters().keySet();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Method{");
        sb.append("parameters=")
          .append(parameters);
        sb.append('}');
        return sb.toString();
    }

}
