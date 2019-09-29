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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application metadata that describes various aspects of a contract such as methods
 * and the nature of the blockchain it si deployed on.
 * 
 * This is retrieved in the init() method of a Simba instance.
 */
public class AppMetadata {

    @JsonProperty ("api_name")
    private String apiName;
    @JsonProperty
    private String faucet;
    @JsonProperty
    private String name;
    @JsonProperty
    private String network;
    @JsonProperty ("network_type")
    private String networkType;
    @JsonProperty
    private boolean poa;
    @JsonProperty("simba_faucet")
    private boolean simbaFaucet = false;
    @JsonProperty
    private String type;

    @JsonProperty
    private Map<String, Method> methods = new HashMap<>();

    public AppMetadata() {
    }

    
    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getFaucet() {
        return faucet;
    }

    public void setFaucet(String faucet) {
        this.faucet = faucet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    
    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public boolean getPoa() {
        return poa;
    }

    public void setPoa(boolean poa) {
        this.poa = poa;
    }

    public boolean isSimbaFaucet() {
        return simbaFaucet;
    }

    public void setSimbaFaucet(boolean simbaFaucet) {
        this.simbaFaucet = simbaFaucet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, Method> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppMetadata{");
        sb.append("apiName='")
          .append(apiName)
          .append('\'');
        sb.append(", faucet='")
          .append(faucet)
          .append('\'');
        sb.append(", name='")
          .append(name)
          .append('\'');
        sb.append(", network='")
          .append(network)
          .append('\'');
        sb.append(", networkType='")
          .append(networkType)
          .append('\'');
        sb.append(", poa=")
          .append(poa);
        sb.append(", simbaFaucet=")
          .append(simbaFaucet);
        sb.append(", type='")
          .append(type)
          .append('\'');
        sb.append(", methods=")
          .append(methods);
        sb.append('}');
        return sb.toString();
    }
}
