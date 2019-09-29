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

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbachain.SimbaException;
import com.simbachain.simba.Balance;
import com.simbachain.simba.Funds;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.Manifest;
import com.simbachain.simba.Method;
import com.simbachain.simba.MethodResponse;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Simba;
import com.simbachain.simba.Transaction;
import com.simbachain.wallet.Wallet;
import org.web3j.crypto.RawTransaction;
import org.web3j.utils.Numeric;

/**
 * Client for simbachain.com
 */
public class SimbaChain extends Simba<SimbaChainConfig> {

    private Wallet wallet;
    private SigningConfirmation signingConfirmation;
    private Map<String, String> apiHeaders = new HashMap<>();
    private Map<String, String> managementHeaders = new HashMap<>();

    /**
     * Create a SimbaChain instance
     *
     * @param endpoint the endpoint of a contract, e.g. https://api.simbachain.com/v1/
     * @param contract the contract or app name.
     * @param config   the credentials required to access the endpoint.
     */
    public SimbaChain(String endpoint, String contract, SimbaChainConfig config) {
        this(endpoint, contract, config, new SigningConfirmation() {
        });
    }

    /**
     * Create a SimbaChain instance
     *
     * @param endpoint            the endpoint of a contract, e.g. https://api.simbachain.com/v1/
     * @param contract            the contract or app name.
     * @param config              the credentials required to access the endpoint.
     * @param signingConfirmation an optional SigningConfirmation instance
     *                            that is queried before a transaction is signed.
     */
    public SimbaChain(String endpoint,
        String contract,
        SimbaChainConfig config,
        SigningConfirmation signingConfirmation) {
        super(endpoint, contract, config);
        this.wallet = config.getWallet();
        this.apiHeaders.put("APIKEY", this.getCredentials()
                                          .getApiKey());
        if (this.getCredentials()
                .getManagementKey() != null) {
            this.managementHeaders.put("APIKEY", this.getCredentials()
                                                     .getManagementKey());
        }
        this.signingConfirmation = signingConfirmation;
    }

    /**
     * get a Transaction given a transaction hash
     *
     * @param txnId a transaction ID
     * @return a Transaction object
     * @throws SimbaException if an error occurs
     */
    @Override
    public Transaction getTransaction(String txnId) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getTransaction: " + "txnId = [" + txnId + "]");
        }
        FullTransaction txn = this.get(
            String.format("%s%s%s/transaction/%s", getEndpoint(), getvPath(), getContract(), txnId),
            jsonResponseHandler(FullTransaction.class));
        String method = txn.getMethod();
        Method m = getMetadata().getMethods()
                                .get(method);
        if (m != null) {
            txn.setMethodParameters(m.getParameters());
        }
        return txn;
    }

    /**
     * Invoke a particular method of a smart contract via the SIMBA HTTP API.
     *
     * @param method     The method name
     * @param parameters the parameters
     * @param files      files to upload with the request
     * @return a MethodResponse object containing the unique ID of the call.
     * @throws SimbaException if an error occurs
     */
    @Override
    public MethodResponse callMethod(String method, JsonData parameters, UploadFile... files)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            Object f = files.length == 0 ? "" : Arrays.asList(files);
            log.debug("ENTER: SimbaChain.callMethod: "
                + "method = ["
                + method
                + "], parameters = ["
                + parameters
                + "], files = ["
                + f
                + "]");
        }

        if (this.wallet == null) {
            throw new SimbaException("No Wallet found", SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        validateParameters(method, parameters, files.length > 0);
        parameters.and("from", this.wallet.getAddress());
        String txnId = null;

        String endpoint = String.format("%s%s%s/%s/", getEndpoint(), getvPath(), getContract(),
            method);
        SigningTransaction response = this.post(endpoint, parameters,
            jsonResponseHandler(SigningTransaction.class), files);
        if (!getSigningConfirmation().confirm(response)) {
            throw new SimbaException(response.toString(), SimbaException.SimbaError.SIGN_REJECTED);
        }
        txnId = response.getId();
        Payload payload = response.getPayload();
        Raw raw = payload.getRaw();

        String nonce = raw.getNonce();
        String gasPrice = raw.getGasPrice();
        String gasLimit = raw.getGasLimit();
        BigInteger value = raw.getValue() == null ? BigInteger.ZERO
            : Numeric.toBigInt(raw.getValue());
        String to = raw.getTo();
        String data = raw.getData();

        RawTransaction rt = RawTransaction.createTransaction(Numeric.toBigInt(nonce),
            Numeric.toBigInt(gasPrice), new BigInteger(gasLimit), to, value, data);
        String signed = this.wallet.sign(rt);

        String signedResponse = this.post(
            String.format("%s%s%s/transaction/%s/", getEndpoint(), getvPath(), getContract(),
                txnId), JsonData.with("payload", signed), stringResponseHandler());

        MethodResponse mr = new MethodResponse(txnId);
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaChain.callMethod: returning " + mr);
        }
        return mr;
    }

    /**
     * Get the metadata JSON file for a bundle as a string.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @return a String that is the JSON manifest file.
     * @throws SimbaException if an error occurs
     */
    @Override
    public Manifest getBundleMetadataForTransaction(String transactionIdOrHash)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getBundleMetadataForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "]");
        }
        ManifestWrapper wrapper = this.get(
            String.format("%s%s%s/transaction/%s/bundle/?no_files=true", getEndpoint(), getvPath(),
                getContract(), transactionIdOrHash), jsonResponseHandler(ManifestWrapper.class));
        Manifest m = new Manifest();
        m.setFiles(wrapper.getManifest());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaChain.getBundleMetadataForTransaction: returning " + m);
        }
        return m;
    }

    /**
     * Get the bundle file itself for a given transaction.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param outputStream        An output stream to write the bundle file to.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    @Override
    public long getBundleForTransaction(String transactionIdOrHash, OutputStream outputStream)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getBundleForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "], outputStream = ["
                + outputStream
                + "]");
        }
        return this.getBundleForTransaction(transactionIdOrHash, outputStream, true);
    }

    /**
     * Get a file from the bundle file for a given transaction with the given name.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param fileName            the file name
     * @param outputStream        An output stream to write the bundle file to.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    @Override
    public long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getBundleFileForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "], fileName = ["
                + fileName
                + "], outputStream = ["
                + outputStream
                + "]");
        }
        return this.getBundleFileForTransaction(transactionIdOrHash, fileName, outputStream, true);
    }

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
    @Override
    public long getBundleForTransaction(String transactionIdOrHash,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getBundleForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "], outputStream = ["
                + outputStream
                + "], close = ["
                + close
                + "]");
        }

        return this.get(
            String.format("%s%s%s/transaction/%s/bundle_raw/", getEndpoint(), getvPath(),
                getContract(), transactionIdOrHash), streamResponseHandler(outputStream, close));
    }

    /**
     * Get a file from the bundle file for a given transaction with the given name.
     * The output stream is closed once complete.
     *
     * @param transactionIdOrHash The transaction ID or hash
     * @param fileName            the file name
     * @param outputStream        An output stream to write the bundle file to.
     * @param close               Whether or not to close the output stream on completion.
     * @return the number of bytes written to the output stream
     * @throws SimbaException if an error occurs
     */
    @Override
    public long getBundleFileForTransaction(String transactionIdOrHash,
        String fileName,
        OutputStream outputStream,
        boolean close) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getBundleFileForTransaction: "
                + "transactionIdOrHash = ["
                + transactionIdOrHash
                + "], fileName = ["
                + fileName
                + "], outputStream = ["
                + outputStream
                + "], close = ["
                + close
                + "]");
        }

        return this.get(
            String.format("%s%s%s/transaction/%s/fileByName/%s", getEndpoint(), getvPath(),
                getContract(), transactionIdOrHash, fileName),
            streamResponseHandler(outputStream, close));
    }

    /**
     * Query for a transactions and get back a paged result.
     *
     * @return A PagedResult containing zero or more results.
     * @throws SimbaException if an error occurs
     */
    public PagedResult<Transaction> getTransactions() throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getTransactions: " + "");
        }

        String endpoint = String.format("%s%s%s/transaction/", getEndpoint(), getvPath(),
            getContract());
        Page page = this.get(endpoint, jsonResponseHandler(Page.class));
        PagedResult<Transaction> result = new PagedResult<>();
        result.setCount(page.getCount());
        result.setNext(page.getNext());
        result.setPrevious(page.getPrevious());
        List<FullTransaction> txs = page.getResults();
        for (FullTransaction tx : txs) {
            tx.setApp(getContract());
        }
        result.setResults(txs);
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaChain.getTransactions: returning " + result);
        }
        return result;
    }

    /**
     * Query for a transactions on a particular method and get back a paged result.
     *
     * @param params the query parameters.
     * @return A PagedResult containing zero or more results.
     * @throws SimbaException if an error occurs
     */
    public PagedResult<Transaction> getTransactions(String method, Query.Params params)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getTransactions: "
                + "method = ["
                + method
                + "], params = ["
                + params
                + "]");
        }
        validateQueryParameters(method, params);
        String endpoint = String.format("%s%s%s/%s/%s", getEndpoint(), getvPath(), getContract(),
            method, params.toString());
        Page page = this.get(endpoint, jsonResponseHandler(Page.class));
        PagedResult<Transaction> result = new PagedResult<>();
        result.setCount(page.getCount());
        result.setNext(page.getNext());
        result.setPrevious(page.getPrevious());
        Method m = getMetadata().getMethods()
                                .get(method);
        List<FullTransaction> txs = page.getResults();
        for (FullTransaction tx : txs) {
            tx.setApp(getContract());
            tx.setMethodParameters(m.getParameters());
        }
        result.setResults(txs);
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaChain.getTransactions: returning " + result);
        }
        return result;
    }

    /**
     * Get the next paged result is available.
     *
     * @param results the current paged result.
     * @return a new paged result from calling getNext() or null if there is no next URL.
     * @throws SimbaException if an error occurs
     */
    @Override
    public PagedResult<Transaction> next(PagedResult results) throws SimbaException {
        if (results.getNext() == null) {
            return null;
        }
        Page page = this.get(results.getNext(), jsonResponseHandler(Page.class));
        PagedResult<Transaction> result = new PagedResult<>();
        result.setCount(page.getCount());
        result.setNext(page.getNext());
        result.setPrevious(page.getPrevious());
        List<FullTransaction> txs = page.getResults();
        for (FullTransaction tx : txs) {
            tx.setApp(getContract());
            if (tx.getMethod() != null) {
                Method m = getMetadata().getMethods()
                                        .get(tx.getMethod());
                tx.setMethodParameters(m.getParameters());
            }
        }
        result.setResults(txs);
        return result;
    }

    /**
     * Get the previous paged result is available.
     *
     * @param results the current paged result.
     * @return a new paged result from calling getPrevious() or null if there is no previous URL.
     * @throws SimbaException if an error occurs
     */
    @Override
    public PagedResult<Transaction> previous(PagedResult results) throws SimbaException {
        if (results.getPrevious() == null) {
            return null;
        }
        Page page = this.get(results.getPrevious(), jsonResponseHandler(Page.class));
        PagedResult<Transaction> result = new PagedResult<>();
        result.setCount(page.getCount());
        result.setNext(page.getNext());
        result.setPrevious(page.getPrevious());
        List<FullTransaction> txs = page.getResults();
        for (FullTransaction tx : txs) {
            tx.setApp(getContract());
            if (tx.getMethod() != null) {
                Method m = getMetadata().getMethods()
                                        .get(tx.getMethod());
                tx.setMethodParameters(m.getParameters());
            }
        }
        result.setResults(txs);
        return result;
    }

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
    public Funds addFunds() throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.addFunds: " + "");
        }

        if (this.getMetadata() == null) {
            throw new SimbaException("App Metadata not yet retrieved",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }

        if (this.wallet == null) {
            throw new SimbaException("No Wallet found", SimbaException.SimbaError.WALLET_NOT_FOUND);
        }

        String address = this.wallet.getAddress();

        if (this.getMetadata()
                .getPoa()) {
            Funds f = new Funds(true, null, null);
            if (log.isDebugEnabled()) {
                log.debug("EXIT: SimbaChain.addFunds: returning " + f);
            }
            return f;
        }

        if (!this.getMetadata()
                 .isSimbaFaucet()) {
            Funds f = new Funds(false, null, this.getMetadata()
                                                 .getFaucet());
            if (log.isDebugEnabled()) {
                log.debug("EXIT: SimbaChain.addFunds: returning " + f);
            }
            return f;
        }
        JsonData data = JsonData.with("account", address)
                                .and("value", "1")
                                .and("currency", "ether");

        Funds f = this.post(
            String.format("%s%s%s/balance/%s", getEndpoint(), getvPath(), getContract(), address),
            data, jsonResponseHandler(Funds.class));
        f.setPoa(false);
        f.setFaucetUrl(this.getMetadata()
                           .getFaucet());
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaChain.addFunds: returning " + f);
        }
        return f;
    }

    /**
     * Get the current balance of the account associated with the Wallet.
     *
     * @return a Balance object.
     * @throws SimbaException if an error occurs.
     */
    public Balance getBalance() throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: SimbaChain.getBalance: " + "");
        }

        if (this.getMetadata() == null) {
            throw new SimbaException("App Metadata not yet retrieved",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }

        if (this.wallet == null) {
            throw new SimbaException("No Wallet found", SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        if (this.getMetadata()
                .getPoa()) {
            Balance b = new Balance(true, "", -1L);
            if (log.isDebugEnabled()) {
                log.debug("EXIT: SimbaChain.getBalance: returning " + b);
            }
            return b;
        }
        String address = this.wallet.getAddress();

        Balance b = this.get(
            String.format("%s%s%s/balance/%s", getEndpoint(), getvPath(), getContract(), address),
            jsonResponseHandler(Balance.class));
        b.setPoa(false);
        if (log.isDebugEnabled()) {
            log.debug("EXIT: SimbaChain.getBalance: returning " + b);
        }
        return b;
    }

    /**
     * Get the HTTP headers used by the client.
     *
     * @return the headers as a map.
     * @throws SimbaException if the headers cannot be set.
     */
    @Override
    protected Map<String, String> getApiHeaders() throws SimbaException {
        return this.apiHeaders;
    }

    /**
     * Get the signing confirmation implementation.
     *
     * @return the signing confirmation
     */
    public SigningConfirmation getSigningConfirmation() {
        return signingConfirmation;
    }

}

    
