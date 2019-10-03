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

import com.simbachain.SimbaException;
import com.simbachain.simba.Transaction;

/**
 * Handler for asynchronous execution.
 * These methods are called from a separate thread when a transaction
 * reaches a specified state or an error occurs.
 * 
 * handleTransactionError is typically an error thrown by the Simba server,
 * emanating from the blockchain itself, or the server.
 * 
 * handleExecutionError is typically a runtime exception triggered
 * by the thread executor itself.
 */
public interface AsyncCallHandler {

    /**
     * Handle a successful transaction.
     * @param transaction the transaction object.
     */
    void handleResponse(Transaction transaction);

    /**
     * Handle a transaction error.
     * @param exception a SimbaException
     */
    void handleTransactionError(SimbaException exception);

    /**
     * Handle an execution error.
     * @param throwable a throwable, most likely a runtime exception.
     */
    void handleExecutionError(Throwable throwable);

}
