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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbachain.SimbaException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point to interacting with Simba.
 * The client has methods for invoking contract methods, checking transaction status and
 * checking account balance and retrieving funds where applicable.
 */
public abstract class Simba<C extends SimbaConfig> {

    private String vPath = "v1/";
    private String endpoint;
    private String contract;
    protected ObjectMapper mapper = new ObjectMapper();
    protected CloseableHttpClient client;
    protected AppMetadata metadata = new AppMetadata();
    private C config;
    protected Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * Constructor overrriden by subclasses.
     *
     * @param endpoint    the URL of a particular contract API, e.g. https://api.simbachain.com/
     * @param contract    the name of the contract or the appname, e.g. mycontract
     * @param config used by subclasses.
     */
    public Simba(String endpoint, String contract, C config) {
        if (!endpoint.endsWith("/")) {
            endpoint += "/";
        }
        this.endpoint = endpoint;
        this.contract = contract;
        this.client = createClient();
        this.config = config;
    }

    /**
     * Get the endpoint associated with this client.
     *
     * @return the endpoint.
     */
    public String getEndpoint() {
        return endpoint;
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
    public AppMetadata getMetadata() {
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
        Api result = this.get(
            String.format("%s%s%s/?format=openapi", getEndpoint(), getvPath(), getContract()),
            jsonResponseHandler(Api.class));
        ApiInfo info = result.getInfo();
        this.metadata = info.getAppMetadata();
        if (log.isDebugEnabled()) {
            try {
                log.debug(mapper.writeValueAsString(this.metadata));
            } catch (JsonProcessingException e) {
                log.error("Error logging metadata", e);
            }
        }
    }

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
     * Wait for a transaction to reach INITIALIZED stage.
     *
     * @param txnId        The transaction or requiest ID.
     * @param interval     Interval to poll the server.
     * @param totalSeconds Total time to wait.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionInitialized(String txnId,
        long interval,
        int totalSeconds) throws SimbaException {
        return submit(txnId, interval, totalSeconds, Transaction.State.INITIALIZED);
    }

    /**
     * Wait for a transaction to reach INITIALIZED stage. Polling interval is set to 1 second
     * and total wait time is set to 10 seconds.
     *
     * @param txnId The transaction or requiest ID.
     * @return a Future object that returns a Transaction.
     * @throws SimbaException if something goes wrong.
     */
    public Future<Transaction> waitForTransactionInitialized(String txnId) throws SimbaException {
        return submit(txnId, 1000, 10, Transaction.State.INITIALIZED);
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
                if (txn != null && txn.getState() == state) {
                    return txn;
                }
                Thread.sleep(poll);
                now = System.currentTimeMillis();
            }
            return txn;
        }
    }

    /**
     * Utility class used for file uploads.
     */
    public static class UploadFile {

        private String name;
        private String mimeType;
        private InputStream file;

        /**
         * Create an upload file from a File object.
         *
         * @param name     the file name.
         * @param mimeType the mime type.
         * @param file     the File object to read from.
         * @throws SimbaException if the file cannot be found.
         */
        public UploadFile(String name, String mimeType, File file) throws SimbaException {
            this.name = name;
            this.mimeType = mimeType;
            try {
                this.file = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new SimbaException("Could not find file " + file.getAbsolutePath(),
                    SimbaException.SimbaError.FILE_ERROR, e);
            }
        }

        /**
         * Create an upload file from a File object with a default mime type of
         * application/octet-stream
         *
         * @param name the file name.
         * @param file the File object to read from.
         * @throws SimbaException if the file cannot be found.
         */
        public UploadFile(String name, File file) throws SimbaException {
            this(name, "application/octet-stream", file);
        }

        /**
         * Create an upload file from an input stream.
         *
         * @param name     the file name.
         * @param mimeType the mime type.
         * @param file     the input stream object to read from.
         */
        public UploadFile(String name, String mimeType, InputStream file) {
            this.name = name;
            this.mimeType = mimeType;
            this.file = file;
        }

        /**
         * Create an upload file from an input stream with a default mime type of
         * application/octet-stream
         *
         * @param name the file name.
         * @param file the input stream object to read from.
         */
        public UploadFile(String name, InputStream file) {
            this(name, "application/octet-stream", file);
        }

        /**
         * Create an upload file from a byte array.
         *
         * @param name     the file name.
         * @param mimeType the mime type.
         * @param file     the byte array to read from.
         */
        public UploadFile(String name, String mimeType, byte[] file) {
            this.name = name;
            this.mimeType = mimeType;
            this.file = new ByteArrayInputStream(file);
        }

        /**
         * Create an upload file from a byte array with a default mime type of
         * application/octet-stream
         *
         * @param name the file name.
         * @param file the byte array to read from.
         */
        public UploadFile(String name, byte[] file) {
            this(name, "application/octet-stream", file);
        }

        /**
         * Create an upload file from a File path.
         *
         * @param name     the file name.
         * @param mimeType the mime type.
         * @param file     the file path to read from.
         * @throws SimbaException if the file cannot be found.
         */
        public UploadFile(String name, String mimeType, String file) throws SimbaException {
            this(name, mimeType, new File(file));
        }

        /**
         * Create an upload file from a File path with a default mime type of
         * application/octet-stream
         *
         * @param name the file name.
         * @param file the file path to read from.
         * @throws SimbaException if the file cannot be found.
         */
        public UploadFile(String name, String file) throws SimbaException {
            this(name, "application/octet-stream", file);
        }

        public String getName() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }

        public InputStream getFile() {
            return file;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("UploadFile{");
            sb.append("name='")
              .append(name)
              .append('\'');
            sb.append(", mimeType='")
              .append(mimeType)
              .append('\'');
            if (file != null) {
                sb.append(", file=")
                  .append(file);
                sb.append('}');
            }
            return sb.toString();
        }
    }

    //****************************** END OF PUBLIC INTERFACE ******************************//

    protected void validateParameters(String method, JsonData parameters, boolean files)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.validateParameters: "
                + "method = ["
                + method
                + "], parameters = ["
                + parameters
                + "], files = ["
                + files
                + "]");
        }

        if (getMetadata() == null) {
            throw new SimbaException("No metadata. You may need to call init() first.",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }
        Method m = getMetadata().getMethods()
                                .get(method);
        if (m == null) {
            throw new SimbaException(String.format("No method named %s", method),
                SimbaException.SimbaError.MESSAGE_ERROR);
        }
        Set<String> keys = parameters.keys();
        for (String key : keys) {
            if (key.equals("_files")) {
                throw new SimbaException(String.format(
                    "Files parameters%s for method %s should not be used. Please upload files as attachments",
                    key, method), SimbaException.SimbaError.MESSAGE_ERROR);
            }
            if (m.getParameters()
                 .get(key) == null) {
                throw new SimbaException(
                    String.format("Unknown parameter %s for method %s", key, method),
                    SimbaException.SimbaError.MESSAGE_ERROR);
            }
        }
        if (m.getParameters()
             .get("_files") == null && files) {
            throw new SimbaException(
                String.format("Method %s does not support file uploads.", method),
                SimbaException.SimbaError.MESSAGE_ERROR);
        }
    }

    protected void validateQueryParameters(String method, Query.Params parameters)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.validateQueryParameters: "
                + "method = ["
                + method
                + "], parameters = ["
                + parameters
                + "]");
        }
        if (getMetadata() == null) {
            throw new SimbaException("No metadata. You may need to call init() first.",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }
        Method m = getMetadata().getMethods()
                                .get(method);
        if (m == null) {
            throw new SimbaException(String.format("No method named %s", method),
                SimbaException.SimbaError.MESSAGE_ERROR);
        }
        for (Query.Param<?> parameter : parameters.getParams()) {
            String name = parameter.getName();
            if (m.getParameters()
                 .get(name) == null) {
                throw new SimbaException(
                    String.format("Unknown parameter %s for method %s", name, method),
                    SimbaException.SimbaError.MESSAGE_ERROR);
            }
        }
    }

    /**
     * Get the HTTP headers used by the client.
     *
     * @return the headers as a map.
     * @throws SimbaException if the headers cannot be set.
     */
    protected abstract Map<String, String> getApiHeaders() throws SimbaException;

    /**
     * Create an HTTP client.
     *
     * @return a CloseableHttpClient
     * @see org.apache.http.impl.client.CloseableHttpClient
     */
    protected CloseableHttpClient createClient() {
        return HttpClients.createDefault();
    }

    private IOException createException(String mime, int status, String reason, String body) {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.createException: "
                + "mime = ["
                + mime
                + "], status = ["
                + status
                + "], reason = ["
                + reason
                + "], body = ["
                + body
                + "]");
        }

        if (mime.equals("text/html")) {
            return new HttpResponseException(status, reason);
        } else if (mime.contains("text")) {
            return new HttpResponseException(status, body);
        } else if (mime.equals("application/json") || mime.equals("application/vnd.api+json")) {
            try {
                Map<?, ?> map = mapper.readValue(body, Map.class);
                Object err = map.get("error");
                if (err != null) {
                    String errCode = null;
                    Object code = map.get("error_code");
                    if (code != null) {
                        errCode = code.toString();
                    }
                    if(errCode != null) {
                        SimbaException.SimbaError errorEnum = mapErrorCode(errCode);
                        SimbaException ex = new SimbaException(err.toString(), errorEnum);
                        Object extras = map.get("extra_detail");
                        if(extras != null && extras instanceof Map) {
                            Map<String, Object> mp = (Map<String, Object>) extras;
                            ex.setProperties(mp);
                        }
                        ex.setHttpStatus(status);
                        return ex;
                    }
                    return new HttpResponseException(status, err.toString());
                }
                err = map.get("detail");
                if (err != null) {
                    return new HttpResponseException(status, err.toString());
                }
                err = map.get("errors");
                if (err != null && err instanceof Collection) {
                    StringBuilder sb = new StringBuilder();
                    Collection<?> c = (Collection) err;
                    for (Object o : c) {
                        if (o instanceof String) {
                            sb.append((String) o);
                        } else if (o instanceof Map) {
                            Map<?, ?> error = (Map) o;
                            Object title = error.get("title");
                            if (title != null && title instanceof String) {
                                sb.append(title)
                                  .append(": ");
                            }
                            Object detail = error.get("detail");
                            if (detail != null && detail instanceof String) {
                                sb.append(detail);
                            }
                        }
                        sb.append("\n");
                    }
                    return new HttpResponseException(status, sb.toString());
                }

            } catch (Exception e) {
                return new HttpResponseException(status, body);
            }
        }
        return new HttpResponseException(status, body);
    }
    
    private SimbaException.SimbaError mapErrorCode(String code) {
        if(code == null || code.trim().length() == 0) {
            return SimbaException.SimbaError.HTTP_ERROR;
        }
        switch (code) {
            case "15001":
                return SimbaException.SimbaError.TRANSACTION_ERROR;
            default:
                return SimbaException.SimbaError.HTTP_ERROR;
        }
    }

    /**
     * Create a string response handler
     *
     * @return ResponseHandler that returns a string.
     */
    protected ResponseHandler<String> stringResponseHandler() {

        return response -> {
            int status = response.getStatusLine()
                                 .getStatusCode();
            String reason = response.getStatusLine()
                                    .getReasonPhrase();
            String responseString = "";
            String mime = "text/plain";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseString = EntityUtils.toString(entity);

                ContentType contentType = ContentType.getOrDefault(entity);
                mime = contentType.getMimeType();
            }
            if (status >= 200 && status < 300) {
                return responseString;
            } else {
                throw createException(mime, status, reason, responseString);
            }
        };
    }

    /**
     * Create a response handler for JSON that tries to deserialize to the given class.
     *
     * @param cls the class
     * @param <C> the target class of the JSON parser.
     * @return ResponseHandler that returns an instance of the requested class
     */
    protected <C> ResponseHandler<C> jsonResponseHandler(final Class<C> cls) {
        return response -> {
            int status = response.getStatusLine()
                                 .getStatusCode();
            String reason = response.getStatusLine()
                                    .getReasonPhrase();
            String mime = "text/plain";
            String responseString = "";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ContentType contentType = ContentType.getOrDefault(entity);
                mime = contentType.getMimeType();
                responseString = EntityUtils.toString(entity);
            }
            if (status >= 200 && status < 300) {
                return mapper.readValue(responseString, cls);
            } else {
                throw createException(mime, status, reason, responseString);
            }
        };
    }

    /**
     * Create a response handler for JSON that tries to deserialize to the given class.
     *
     * @param tf the type reference
     * @return ResponseHandler that returns an instance of the requested class
     */
    protected <C> ResponseHandler<C> jsonResponseHandler(final TypeReference<C> tf) {
        return response -> {
            int status = response.getStatusLine()
                                 .getStatusCode();
            String reason = response.getStatusLine()
                                    .getReasonPhrase();
            String mime = "text/plain";
            String responseString = "";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ContentType contentType = ContentType.getOrDefault(entity);
                mime = contentType.getMimeType();
                responseString = EntityUtils.toString(entity);
            }
            if (status >= 200 && status < 300) {
                return mapper.readValue(responseString, tf);
            } else {
                throw createException(mime, status, reason, responseString);
            }
        };
    }

    /**
     * Create a response handler that writes to a stream.
     * The return value is the number of btes written to the output stream.
     *
     * @param outputStream an output stream to write the response to.
     * @param close        whether or not to close the output stream on completion.
     * @return ResponseHandler that returns the number of bytes written.
     */
    protected ResponseHandler<Long> streamResponseHandler(final OutputStream outputStream,
        final boolean close) {
        return response -> {
            int status = response.getStatusLine()
                                 .getStatusCode();
            HttpEntity entity = response.getEntity();
            String errorResponse = "Error receiving stream.";
            if (status >= 200 && status < 300) {
                if (entity == null) {
                    return 0L;
                }
                InputStream inStream = entity.getContent();
                if (inStream == null) {
                    return 0L;
                } else {
                    try {
                        long total = 0;
                        byte[] tmp = new byte[4096];
                        int c;
                        while ((c = inStream.read(tmp)) != -1) {
                            outputStream.write(tmp, 0, c);
                            total += c;
                        }
                        return total;
                    } finally {
                        if (close) {
                            outputStream.close();
                        }
                        inStream.close();
                    }
                }
            } else {
                if (entity != null) {
                    errorResponse = EntityUtils.toString(entity);
                    String reason = response.getStatusLine()
                                            .getReasonPhrase();
                    ContentType contentType = ContentType.getOrDefault(entity);
                    String mime = contentType.getMimeType();
                    throw createException(mime, status, reason, errorResponse);
                }
                throw new HttpResponseException(status, errorResponse);
            }
        };
    }

    /**
     * Create an HTTP entity.
     *
     * @param data  the data
     * @param files files if any
     * @return HttpEntity
     * @throws SimbaException if an error occurs
     */
    protected HttpEntity createEntity(Map<String, Object> data, UploadFile... files)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            Object f = files == null ? "" : Arrays.asList(files);
            log.debug("ENTER: Simba.createEntity: " + "data = [" + data + "], files = [" + f + "]");
        }

        if (files != null && files.length > 0) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (String key : data.keySet()) {
                builder.addTextBody(key, data.get(key)
                                             .toString());
            }
            for (int i = 0; i < files.length; i++) {
                UploadFile file = files[i];
                builder.addBinaryBody(String.format("file_%s", i), file.getFile(),
                    ContentType.create(file.getMimeType()), file.getName());
            }
            return builder.build();
        } else {
            try {
                String json = mapper.writeValueAsString(data);
                return new StringEntity(json, ContentType.APPLICATION_JSON);
            } catch (JsonProcessingException e) {
                throw new SimbaException("Error parsing JSON",
                    SimbaException.SimbaError.MESSAGE_ERROR, e);
            }
        }
    }

    protected <R> R post(String endpoint, Map<String, Object> data, ResponseHandler<R> handler)
        throws SimbaException {
        return post(endpoint, data, handler, new UploadFile[0]);
    }

    protected <R> R post(String endpoint,
        Map<String, Object> data,
        ResponseHandler<R> handler,
        UploadFile... files) throws SimbaException {
        if (log.isDebugEnabled()) {
            Object f = files == null ? "" : Arrays.asList(files);
            log.debug("ENTER: Simba.post: "
                + "endpoint = ["
                + endpoint
                + "], data = ["
                + data
                + "], handler = ["
                + handler
                + "], files = ["
                + f
                + "]");
        }

        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.setEntity(createEntity(data, files));
        Map<String, String> headers = getApiHeaders();
        if (headers != null) {
            for (String s : headers.keySet()) {
                httpPost.setHeader(s, headers.get(s));
            }
        }
        try {
            return this.client.execute(httpPost, handler);
        } catch (Exception e) {
            throw getException("POST", e);
        }
    }

    protected <R> R post(String endpoint, JsonData data, ResponseHandler<R> handler)
        throws SimbaException {
        return post(endpoint, data, handler, new UploadFile[0]);
    }

    protected <R> R post(String endpoint,
        JsonData data,
        ResponseHandler<R> handler,
        UploadFile... files) throws SimbaException {
        return post(endpoint, data.asMap(), handler, files);
    }

    protected <R> R get(String endpoint, ResponseHandler<R> handler) throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.get: "
                + "endpoint = ["
                + endpoint
                + "], handler = ["
                + handler
                + "]");
        }
        HttpGet httpGet = new HttpGet(endpoint);
        Map<String, String> headers = getApiHeaders();
        if (headers != null) {
            for (String s : headers.keySet()) {
                httpGet.setHeader(s, headers.get(s));
            }
        }
        httpGet.setHeader("pragma", "no-cache");
        httpGet.setHeader("cache-control", "no-cache");
        try {
            return this.client.execute(httpGet, handler);
        } catch (Exception e) {
            throw getException("GET", e);
        }
    }

    private SimbaException getException(String method, Exception e) {
        if (e instanceof SimbaException) {
            return (SimbaException) e;
        }
        SimbaException ex = new SimbaException("Error in HTTP " + method + ": " + e.getMessage(),
            SimbaException.SimbaError.HTTP_ERROR, e);
        if (e instanceof HttpResponseException) {
            ex.setHttpStatus(((HttpResponseException) e).getStatusCode());
        }
        return ex;
    }
    
    private Future<Transaction> submit(String txnId, long interval, int totalSeconds, Transaction.State state) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Transaction> txn = executor.submit(
            new TransactionCallable(txnId, interval, totalSeconds, state));
        executor.shutdown();
        return txn;
        
    }
}
