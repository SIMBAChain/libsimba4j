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

import com.simbachain.simba.SimbaConfig;
import com.simbachain.wallet.Wallet;

/**
 *  Config for a SimbaChain instance.
 */
public class SimbaChainConfig implements SimbaConfig {

    private String apiKey;
    private Wallet wallet;
    private String managementKey;
    private int retrySignAttempts;
    private long retryTransactionSleep;

    /**
     * Create a SimbaChain config.
     * 'retrySignAttempts' and 'retryTransactionSleep' are used to control how many efforts
     * are made to overcome out of synch nonce errors. For a single transaction, signing
     * will be tried 'retrySignAttempts' times, and the payload will be attempted with a new
     * transaction 'retrySignAttempts' times. So total attempts are maximum 'retrySignAttempts' *
     * 'retrySignAttempts'.
     * 
     * 'retryTransactionSleep' determines how many milliseconds to sleep between attempting
     * to submit the a new transaction with the same payload.
     * 
     * All retries and sleeps takes place inside the 'callMethod' method.
     * 
     * @param apiKey the api key for the contract
     * @param wallet the wallet to use
     * @param managementKey the management key to use. Not currently required.
     * @param retrySignAttempts in the case of nonce out of synch errors, try to sign this
     *                          many times based on the suggested nonce returned from
     *                          simbachain.
     * @param retryTransactionSleep in the case of nonce out of synch errors, sleep for
     *                              this many milliseconds before trying with a nw transaction.
     */
    public SimbaChainConfig(String apiKey,
        Wallet wallet,
        String managementKey,
        int retrySignAttempts,
        long retryTransactionSleep) {
        this.apiKey = apiKey;
        this.wallet = wallet;
        this.managementKey = managementKey;
        this.retrySignAttempts = retrySignAttempts;
        this.retryTransactionSleep = retryTransactionSleep;
    }

    public SimbaChainConfig(String apiKey, Wallet wallet, String managementKey) {
        this(apiKey, wallet, managementKey, 3, 2*1000);
    }

    public SimbaChainConfig(String apiKey, Wallet wallet) {
        this(apiKey, wallet, null, 3, 2 * 1000);
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

    public int getRetrySignAttempts() {
        return retrySignAttempts;
    }

    public long getRetryTransactionSleep() {
        return retryTransactionSleep;
    }
}
