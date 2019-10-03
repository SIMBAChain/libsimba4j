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

package com.simbachain.simba.com.async;

import com.simbachain.simba.Transaction;
import com.simbachain.simba.com.SimbaChainConfig;
import com.simbachain.wallet.Wallet;

/**
 *  A config that supports in-order execution and waiting for transactions.
 *  pollInterval sets the polling interval to the server in milliseconds.
 *  The default is 1000.
 *  
 *  totalWaitSeconds is the time in seconds that polling should take place for.
 *  If the tranasction has not reached the desired state by then, it is returned anyway.
 *  Default is 10 seconds.
 *  
 *  queueSize is the size of the blocking queue. Default is 100. If the queue fills up
 *  the wait for a free slot will be experienced in the calling thread.
 *  
 *  state is the desired state to check for before returning the transaction.
 *  Typically State.SUBMITTED ensures the transaction is safely going to be confirmed
 *  on the blockchain.
 */
public class AsyncSimbaChainConfig extends SimbaChainConfig {
    
    private long pollInterval = 1000;
    private int totalWaitSeconds = 10;
    private int queueSize = 100;
    private Transaction.State state = Transaction.State.SUBMITTED;

    public AsyncSimbaChainConfig(String apiKey, Wallet wallet, String managementKey) {
        super(apiKey, wallet, managementKey);
    }

    public AsyncSimbaChainConfig(String apiKey, Wallet wallet) {
        super(apiKey, wallet);
    }

    public AsyncSimbaChainConfig(String apiKey,
        Wallet wallet,
        String managementKey,
        long pollInterval,
        int totalWaitSeconds,
        int queueSize,
        Transaction.State state) {
        super(apiKey, wallet, managementKey);
        this.pollInterval = pollInterval;
        this.totalWaitSeconds = totalWaitSeconds;
        this.queueSize = queueSize;
        this.state = state;
    }

    public AsyncSimbaChainConfig(String apiKey,
        Wallet wallet,
        long pollInterval,
        int totalWaitSeconds,
        int queueSize,
        Transaction.State state) {
        super(apiKey, wallet);
        this.pollInterval = pollInterval;
        this.totalWaitSeconds = totalWaitSeconds;
        this.queueSize = queueSize;
        this.state = state;
    }

    public long getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(long pollInterval) {
        this.pollInterval = pollInterval;
    }

    public int getTotalWaitSeconds() {
        return totalWaitSeconds;
    }

    public void setTotalWaitSeconds(int totalWaitSeconds) {
        this.totalWaitSeconds = totalWaitSeconds;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public Transaction.State getState() {
        return state;
    }

    public void setState(Transaction.State state) {
        this.state = state;
    }
}
