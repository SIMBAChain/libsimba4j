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

package com.simbachain.simba.com;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simbachain.simba.Parameter;
import com.simbachain.simba.Transaction;

/**
 * Simbachain.com Transaction object.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class FullTransaction implements Transaction {
    @JsonProperty
    private String id;
    @JsonProperty
    private String method;
    @JsonProperty
    private Payload payload;
    @JsonProperty
    private Map<String, Object> receipt;
    @JsonProperty("transaction_hash")
    private String transactionHash;
    @JsonProperty
    private String error;
    @JsonProperty("error_details")
    private String errorDetails;
    @JsonProperty
    private String status;
    @JsonProperty
    private String timestamp;
    
    @JsonIgnore
    private String app;
    
    private Map<String, Parameter> methodParameters = new HashMap<>();

    @Override
    public Date getCreated() {
        String ts = getTimestamp();
        if(ts != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").parse(ts);
            } catch (ParseException e) {
                
            }
        }
        return null;
    }

    @Override
    public String getTxnHash() {
        return getTransactionHash();
    }

    @Override
    public String getBlock() {
        if(getReceipt() != null) {
            return getReceipt().getOrDefault("blockNumber", "").toString();
        }
        return null;
    }

    @Override
    public String getSender() {
        Payload p = getPayload();
        if(p != null) {
            Raw raw = p.getRaw();
            if(raw != null) {
                return raw.getFrom();
            }
        }
        return null;
    }

    @Override
    public String getRequestId() {
        return getId();
    }

    @Override
    public String getApp() {
        return app;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public State getState() {
        String status = getStatus();
        if (status != null && status.equals("DEPLOYED")) {
            return State.COMPLETED;
        }
        if (getError() != null) {
            return State.FAILED;
        } else if (getReceipt() == null) {
            return State.INITIALIZED;
        } else {
            return State.COMPLETED;
        }
    }

    @Override
    public Map<String, Object> getInputs() {
        Payload p = getPayload();
        if (p != null) {
            return p.getInputs();
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Parameter> getMethodParameters() {
        return methodParameters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Map<String, Object> getReceipt() {
        return receipt;
    }

    public void setReceipt(Map<String, Object> receipt) {
        this.receipt = receipt;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setMethodParameters(Map<String, Parameter> methodParameters) {
        this.methodParameters = methodParameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FullTransaction{");
        sb.append("id='")
          .append(id)
          .append('\'');
        sb.append(", method='")
          .append(method)
          .append('\'');
        sb.append(", payload=")
          .append(payload);
        sb.append(", receipt=")
          .append(receipt);
        sb.append(", transactionHash='")
          .append(transactionHash)
          .append('\'');
        sb.append(", error='")
          .append(error)
          .append('\'');
        sb.append(", errorDetails='")
          .append(errorDetails)
          .append('\'');
        sb.append(", status='")
          .append(status)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }

}
