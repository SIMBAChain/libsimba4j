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

package com.simbachain.simba;

import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.simbachain.SimbaException;


/**
 * Main entry point to interacting with Simba.
 * The client has methods for invoking contract methods, checking transaction status and
 * checking account balance and retrieving funds where applicable.
 */
public abstract class Simba<C extends SimbaConfig> extends SimbaClient {

    private String vPath = "v1/";
    private String contract;
    protected Metadata metadata;
    private C config;

    /**
     * Constructor overrriden by subclasses.
     *
     * @param endpoint the URL of a particular contract API, e.g. https://api.simbachain.com/
     * @param contract the name of the contract or the appname, e.g. mycontract
     * @param config   used by subclasses.
     */
    public Simba(String endpoint, String contract, C config) {
        super(endpoint);
        this.contract = contract;
        this.client = createClient();
        this.config = config;
    }



    /**
     * Get the contract or app name
     *
     * @return the contract or app name.
     */
    public String getContract() {
        return contract;
    }

    /**
     * Get the app metadata for the instance. This is retrieved in the init() method.
     *
     * @return application metadata.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    public C getConfig() {
        return config;
    }

    /**
     * Get the api version path element.
     *
     * @return the api version path element.
     */
    public String getvPath() {
        return vPath;
    }

    /**
     * Get the api doc path element.
     *
     * @return the api doc path element.
     */
    public String getApiPath() {
        return String.format("%s%s%s/?format=openapi", getEndpoint(), getvPath(), getContract());
    }

    /**
     * Set the api version path element. Default is 'v1/'
     *
     * @param vPath a new version path element.
     */
    public void setvPath(String vPath) {
        if (!vPath.endsWith("/")) {
            vPath += "/";
        }
        this.vPath = vPath;
    }

    /**
     * Initialize the client and retrieve the application metadata.
     * If created via the factory then this method will already have been called.
     *
     * @throws SimbaException if an error occurs
     */
    public void init() throws SimbaException {
        this.metadata = loadMetadata();
        if (log.isDebugEnabled()) {
            log.debug(this.metadata.toString());
        }
    }
    
    protected abstract Metadata loadMetadata() throws SimbaException;

    /**
     * get a Transaction given a transaction hash
     *
     * @param txnId a transaction ID
     * @return a Transaction object
     * @throws SimbaException if an error occurs
     */
    public abstract Transaction getTransaction(String txnId) throws SimbaException;

    /**
     * Invoke a particular method of a smart contract via the SIMBA HTTP API.
     *
     * @param method     The method name
     * @param parameters the parameters
     * @param files      optional list of UploadFile objects
     * @return a CallResponse object containing the unique ID of the call.
     * @throws SimbaException if an error occurs
     */
    public abstract CallResponse callMethod(String method, JsonData parameters, UploadFile... files)
        throws SimbaException;

    /**
     * Invoke a particular method of a smart contract via the SIMBA HTTP API.
     *
     * @param method     The method name
     * @param parameters The parameters
     * @param headers    Client provided headers. Auth headers should not be included.
     * @param files      optional list of UploadFile objects
     * @return a CallResponse object containing the unique ID of the call.
     * @throws SimbaException if an error occurs
     */
    public abstract CallResponse callMethod(String method, JsonData parameters,
        Map<String, String> headers, UploadFile... files)
        throws SimbaException;

    /**
     * Get the metadata JSON file for a bundle as a string.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @return a String that is the JSON manifest file.
     * @throws SimbaException if an error occurs
     */
    public abstract Manifest getBundleMetadataForTransaction(String transactionIdOrHash)
        throws SimbaException;

    /**
     * Get the bundle file itself for a given transaction.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param outputStream        An output stream to write the bundle file to.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public abstract long getBundleForTransaction(String transactionIdOrHash,
        OutputStream outputStream) throws SimbaException;

    /**
     * Get a file from the bundle file for a given transaction with the given index.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param fileName            the file name
     * @param outputStream        An output stream to write the bundle file to.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public abstract long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream) throws SimbaException;

    /**
     * Get the bundle file itself for a given transaction.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param outputStream        An output stream to write the bundle file to.
     * @param close               Whether or not to close the output stream on completion.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public abstract long getBundleForTransaction(String transactionIdOrHash,
        OutputStream outputStream,
        boolean close) throws SimbaException;

    /**
     * Get a file from the bundle file for a given transaction with the given index.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param fileName            the file name
     * @param outputStream        An output stream to write the bundle file to.
     * @param close               Whether or not to close the output stream on completion.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    public abstract long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream,
        boolean close) throws SimbaException;

    /**
     * Query for a transactions and get back a paged result.
     *
     * @return A PagedResult containing zero or more results.
     * @throws SimbaException if an error occurs
     */
    public abstract PagedResult<Transaction> getTransactions() throws SimbaException;

    /**
     * Query for a transactions on a particular method and get back a paged result.
     *
     * @param method the contract method to get transactions for.
     * @param params the query parameters.
     * @return A PagedResult containing zero or more results.
     * @throws SimbaException if an error occurs
     */
    public abstract PagedResult<Transaction> getTransactions(String method, Query.Params params)
        throws SimbaException;

    /**
     * Get the next paged result if available.
     *
     * @param result the current paged result.
     * @return a new paged result from calling getNext() or null if there is no next URL.
     * @throws SimbaException if an error occurs
     */
    public abstract PagedResult<Transaction> next(PagedResult<Transaction> result)
        throws SimbaException;

    /**
     * Get the previous paged result if available.
     *
     * @param result the current paged result.
     * @return a new paged result from calling getPrevious() or null if there is no previous URL.
     * @throws SimbaException if an error occurs
     */
    public abstract PagedResult<Transaction> previous(PagedResult<Transaction> result)
        throws SimbaException;

    /**
     * Add funds to the attached account.
     * Please check the output of this method. It is of the form
     * <p>
     * txnId: transaction ID if auto funding was successful.
     * faucetUrl: external faucet URL to manually retrieve funds.
     * poa: Whether or not the blockchain is Proof of Activity, in which case funds are not needed.
     * </p>
     * encapsulated in a Funds object.
     * <p>
     * If successful, txnId will be populated.
     * If the network is PoA, then poa will be true, and txnId will be null
     * If the faucet for the network is external (e.g. Rinkeby, Ropsten, etc), then txnId will be
     * null,
     * and faucet_url will be populated with a URL. You should present this URL to your users to
     * direct them
     * to request funds there.
     * </p>
     *
     * @return Funds object containing details of the attempt to directly add funds to an account.
     * @throws SimbaException if an error occurs.
     */
    public abstract Funds addFunds() throws SimbaException;

    /**
     * Get the current balance of the account associated with the Wallet.
     *
     * @return a Balance object.
     * @throws SimbaException if an error occurs.
     */
    public abstract Balance getBalance() throws SimbaException;

    /**
     * Wait for a transaction to reach COMPLETED stage.
     *
     * @param txnId        The transaction or requiest ID.
     * @param interval     Interval to poll the server.
     * @param totalSeconds Total time to wait.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionCompletion(String txnId,
        long interval,
        int totalSeconds) throws SimbaException {
        return submit(txnId, interval, totalSeconds, Transaction.State.COMPLETED);
    }

    /**
     * Wait for a transaction to reach COMPLETED stage. Polling interval is set to 1 second
     * and total wait time is set to 10 seconds.
     *
     * @param txnId The transaction or requiest ID.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionCompletion(String txnId) throws SimbaException {
        return submit(txnId, 1000, 10, Transaction.State.COMPLETED);
    }

    /**
     * Wait for a transaction to reach SUBMITTED stage.
     *
     * @param txnId        The transaction or requiest ID.
     * @param interval     Interval to poll the server.
     * @param totalSeconds Total time to wait.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionSubmitted(String txnId,
        long interval,
        int totalSeconds) throws SimbaException {
        return submit(txnId, interval, totalSeconds, Transaction.State.SUBMITTED);
    }

    /**
     * Wait for a transaction to reach SUBMITTED stage. Polling interval is set to 1 second
     * and total wait time is set to 10 seconds.
     *
     * @param txnId The transaction or requiest ID.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionSubmitted(String txnId) throws SimbaException {
        return submit(txnId, 1000, 10, Transaction.State.SUBMITTED);
    }

    private class TransactionCallable implements Callable<Transaction> {

        private String txnId;
        private long poll;
        private int totalSeconds;
        private Transaction.State state;

        private TransactionCallable(String txnId,
            long poll,
            int totalSeconds,
            Transaction.State state) {
            this.txnId = txnId;
            this.poll = poll;
            this.totalSeconds = totalSeconds;
            this.state = state;
        }

        @Override
        public Transaction call() throws Exception {
            Transaction txn = null;
            long now = System.currentTimeMillis();
            long end = now + (totalSeconds * 1000);
            while (now < end) {
                txn = getTransaction(txnId);
                if (txn != null && (txn.getState() == state
                    || txn.getState() == Transaction.State.COMPLETED)) {
                    return txn;
                }
                Thread.sleep(poll);
                now = System.currentTimeMillis();
            }
            return txn;
        }
    }

    //****************************** END OF PUBLIC INTERFACE ******************************//


    private Future<Transaction> submit(String txnId,
        long interval,
        int totalSeconds,
        Transaction.State state) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Transaction> txn = executor.submit(
            new TransactionCallable(txnId, interval, totalSeconds, state));
        executor.shutdown();
        return txn;

    }
}
