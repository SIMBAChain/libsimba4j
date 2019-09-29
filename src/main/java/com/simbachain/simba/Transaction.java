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

import java.util.Date;
import java.util.Map;

/**
 *  Interface to a transaction on the blockchain.
 *  Transactions can be in different states and depending
 *  onthe state, and the service being used (SimbaChain or SCaaS)
 *  the transaction may have certain fields not available.
 *  
 *  Scaas has a list/get model which means when querying for
 *  transactions, the inputs will not be available. Instead,
 *  these can be retrieved via getTransaction(txnId).
 *  
 *  Additionally for Scaas, if the transaction is not in a COMPLETE state, then the
 *  inputs will also not be available.
 *  
 *  SimbaChain has a slightly different query model and returns
 *  a full set of data for every query, even if it is not
 *  in a COMPLETED state. This means the inputs are always available.
 *  
 *  Possible states are:
 *  
 *  INITIALIZED, which means the transaction has been
 *  sent to the blockchain, but has not been confirmed yet.
 *  
 *  FAILED, which means the transaction either never made it to the blockchain, or has
 *  failed subsequently.
 *  
 *  COMPLETED, which means the transaction made it to the blockchain and has been confirmed.
 */
public interface Transaction {

    /**
     * Possible states a transaction can be in.
     */
    public static enum State {
        INITIALIZED, FAILED, COMPLETED
    }

    /**
     * get the time the transaction was logged as received.
     * @return the created date
     */
    public Date getCreated();

    /**
     * Get the unique transaction hash.
     * @return the transaction hash.
     */
    public String getTxnHash();

    /**
     * Get the block number in the blockchain
     * where the transaction is recorded.
     * @return the block number.
     */
    public String getBlock();

    /**
     * Get the address of the sender and signer of the transaction.
     * @return the sender address.
     */
    public String getSender();

    /**
     * get the unique request ID.
     * @return the request ID.
     */
    public String getRequestId();

    /**
     * Get the app name (aka contract name) this transaction is related to.
     * @return the app name.
     */
    public String getApp();

    /**
     * Ge the method name of the contract this transaction was created by.
     * @return the method name.
     */
    public String getMethod();

    /**
     * Get the current state of the transaction.
     * @return the current state.
     */
    public State getState();

    /**
     * Get the actual inputs that weere used to create this transaction. 
     * @return a map of inputs mapped to the parameter name.
     */
    public Map<String, Object> getInputs();
                          
    /**
     * Get the parameters defined by the metadata that relate to the method
     * that was invoked to create this transaction.
     * @return the parameter metadata for this transaction.
     */
    public Map<String, Parameter> getMethodParameters();

}
