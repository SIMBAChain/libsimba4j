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

import com.simbachain.simba.SimbaConfig;
import com.simbachain.wallet.Wallet;

/**
 *  Config for a SimbaChain instance.
 */
public class SimbaChainConfig implements SimbaConfig {

    private String apiKey;
    private Wallet wallet;
    private String managementKey;

    public SimbaChainConfig(String apiKey, Wallet wallet, String managementKey) {
        this.apiKey = apiKey;
        this.wallet = wallet;
        this.managementKey = managementKey;
    }

    public SimbaChainConfig(String apiKey, Wallet wallet) {
        this.apiKey = apiKey;
        this.wallet = wallet;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getManagementKey() {
        return managementKey;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
