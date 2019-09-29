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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Funds object
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class Funds {

    @JsonProperty
    private boolean poa;
    @JsonProperty
    private String txnId;
    @JsonProperty ("faucet_url")
    private String faucetUrl;

    public Funds(boolean poa, String txnId, String faucetUrl) {
        this.poa = poa;
        this.txnId = txnId;
        this.faucetUrl = faucetUrl;
    }

    public Funds() {
    }

    
    public boolean isPoa() {
        return poa;
    }

    public void setPoa(boolean poa) {
        this.poa = poa;
    }

    
    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    
    public String getFaucetUrl() {
        return faucetUrl;
    }

    public void setFaucetUrl(String faucetUrl) {
        this.faucetUrl = faucetUrl;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Funds{");
        sb.append("poa=")
          .append(poa);
        sb.append(", txnId='")
          .append(txnId)
          .append('\'');
        sb.append(", faucetUrl='")
          .append(faucetUrl)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
