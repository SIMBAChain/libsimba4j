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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.simbachain.SimbaException;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.com.SigningConfirmation;
import com.simbachain.simba.com.SimbaChain;

/**
 *  A Simba class that controls how transactions are submitted to simbachain
 *  in order to reduce the possibility of out-of-order nonces.
 *  
 *  Methods are placed into a queue and executed in order. Each execution runs in a separate
 *  single execution thread and waits for a transaction to reach a specified state
 *  before the next transaction is executed. On completion or failure, the AsyncCallHandler
 *  is invoked from the execution thread.
 */
public class AsyncSimbaChain extends SimbaChain {

    private ExecutorService executor;
    private BlockingQueue<Runnable> queue;
    private long poll;
    private int totalWait;
    private Transaction.State state;

    /**
     * Create an async simba instance.
     * @param endpoint the host endpoint.
     * @param contract the contract or app name.
     * @param config an AsyncSimbaChainConfig object.
     */
    public AsyncSimbaChain(String endpoint, String contract, AsyncSimbaChainConfig config) {
        this(endpoint, contract, config, new SigningConfirmation() {
        });
    }

    /**
     *
     * Create an async simba instance.
     * @param endpoint the host endpoint.
     * @param contract the contract or app name.
     * @param config an AsyncSimbaChainConfig object.
     * @param signingConfirmation a signing confirmation instance.
     */
    public AsyncSimbaChain(String endpoint,
        String contract,
        AsyncSimbaChainConfig config,
        SigningConfirmation signingConfirmation) {
        super(endpoint, contract, config, signingConfirmation);
        this.poll = config.getPollInterval();
        this.totalWait = config.getTotalWaitSeconds();
        this.state = config.getState();
        this.queue = new ArrayBlockingQueue<>(config.getQueueSize());
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (!executor.isShutdown()) {
                    executor.shutdown();
                }
            }
        });
    }

    /**
     * Call a contract method and receive the transaction or an error in the call handler.
     * 
     * @param handler an AsyncCallHandler instance.
     * @param method  the contract method to call.
     * @param parameters The method parameters.
     * @param files zero or more UploadFile objects if the method support file uploads.
     */
    public void asyncCallMethod(final AsyncCallHandler handler,
        final String method,
        final JsonData parameters,
        final UploadFile... files) {

        try {
            executor.submit(() -> {
                try {
                    CallResponse response = callMethod(method, parameters, files);
                    String txnId = response.getRequestIdentitier();
                    Transaction txn = null;
                    long now = System.currentTimeMillis();
                    long end = now + (totalWait * 1000);
                    while (now < end) {
                        txn = getTransaction(txnId);
                        if (txn != null && txn.getState() == state) {
                            break;
                        }
                        Thread.sleep(poll);
                        now = System.currentTimeMillis();
                    }
                    if(txn != null) {
                        handler.handleResponse(txn);
                    }
                } catch (Exception e) {
                    if (e instanceof SimbaException) {
                        handler.handleTransactionError((SimbaException) e);
                    } else {
                        handler.handleTransactionError(new SimbaException("Error in async call method",
                            SimbaException.SimbaError.EXECUTION_ERROR));
                    }
                }
            });
        } catch (Throwable e) {
           handler.handleExecutionError(e);
        }
    }
    
    public int getQueueSize() {
        return queue.size();
    }
    
    public void shutdown() {
        if(!executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
