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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbachain.SimbaException;
import org.apache.http.Header;
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
 *  Base class for generic HTTP related stuff.
 */
public abstract class SimbaClient {

    private String endpoint;
    protected ObjectMapper mapper = new ObjectMapper();
    protected CloseableHttpClient client;
    protected Logger log = LoggerFactory.getLogger(getClass().getName());

    public SimbaClient(String endpoint) {
        if (!endpoint.endsWith("/")) {
            endpoint += "/";
        }
        this.endpoint = endpoint;
    }

    /**
     * Get the endpoint associated with this client.
     *
     * @return the endpoint.
     */
    public String getEndpoint() {
        return endpoint;
    }

    protected void validateParameters(Metadata metadata, String method, JsonData parameters, boolean files)
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

        if (metadata == null) {
            throw new SimbaException("No metadata. You may need to call init() first.",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }
        Method m = metadata.getMethod(method);
        if (m == null) {
            throw new SimbaException(String.format("No method named %s", method),
                SimbaException.SimbaError.MESSAGE_ERROR);
        }
        Set<String> keys = parameters.keys();
        for (String key : keys) {
            if (key.equals(metadata.getFileIndicator())) {
                throw new SimbaException(String.format(
                    "Files parameters%s for method %s should not be used. Please upload files as attachments",
                    key, method), SimbaException.SimbaError.MESSAGE_ERROR);
            }
            if (m.getParameterMap()
                 .get(key) == null) {
                throw new SimbaException(
                    String.format("Unknown parameter %s for method %s", key, method),
                    SimbaException.SimbaError.MESSAGE_ERROR);
            }
        }
        if (m.getParameterMap()
             .get(metadata.getFileIndicator()) == null && files) {
            throw new SimbaException(
                String.format("Method %s does not support file uploads.", method),
                SimbaException.SimbaError.MESSAGE_ERROR);
        }
    }

    protected void validateQueryParameters(Metadata metadata, String method, Query.Params parameters)
        throws SimbaException {
        if (log.isDebugEnabled()) {
            log.debug("ENTER: Simba.validateQueryParameters: "
                + "method = ["
                + method
                + "], parameters = ["
                + parameters
                + "]");
        }
        if (metadata == null) {
            throw new SimbaException("No metadata. You may need to call init() first.",
                SimbaException.SimbaError.METADATA_NOT_AVAILABLE);
        }
        Method m = metadata.getMethod(method);
        if (m == null) {
            throw new SimbaException(String.format("No method named %s", method),
                SimbaException.SimbaError.MESSAGE_ERROR);
        }
        for (Query.Param<?> parameter : parameters.getParams()) {
            String name = parameter.getName();
            if (name.contains(".")) {
                name = name.substring(name.indexOf(".") + 1);
                if (name.contains(".")) {
                    name = name.substring(0, name.indexOf(".") + 1);
                }
            }
            if (m.getParameterMap()
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

    protected IOException createException(String mime, int status, String reason, String body) {
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
                StringBuilder sb = new StringBuilder();
                Map<?, ?> map = mapper.readValue(body, Map.class);
                Object err = map.get("error");
                if (err != null) {
                    if (err instanceof String) {
                        sb.append(err)
                          .append(' ');
                    }
                    Object detail = map.get("detail");
                    if (detail != null) {
                        if (detail instanceof String) {
                            sb.append(detail);
                        }
                    }
                    return new HttpResponseException(status, sb.toString());
                }
                err = map.get("errors");
                if (err instanceof Collection) {
                    Errors errs = mapper.readValue(body, Errors.class);
                    List<Error> ers = errs.getErrors();
                    if (ers != null && ers.size() > 0) {
                        Error error = ers.get(0);
                        String title = error.getTitle();
                        if (title != null) {
                            sb.append(title)
                              .append(": ");
                        }
                        String detail = error.getDetail();
                        if (detail != null) {
                            sb.append(detail);
                        }
                        String errCode = error.getCode();
                        if (errCode != null) {
                            SimbaException.SimbaError errorEnum = mapErrorCode(errCode);
                            SimbaException ex = new SimbaException(err.toString(), errorEnum);
                            ex.setProperties(error.getMeta());
                            ex.setHttpStatus(status);
                            return ex;
                        }
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
        if (code == null
            || code.trim()
                   .length() == 0) {
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
            System.out.println("Simba.jsonResponseHandler JSON: " + responseString);
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
     * @param tf  the type reference
     * @param <C> the type reference type.
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
     * Create a response handler for JSON that tries to deserialize to the given class.
     *
     * @param tf  the type reference
     * @param <C> the type reference type.
     * @return ResponseHandler that returns an instance of the requested class
     */
    protected <C> ResponseHandler<HeaderedResponse<C>> jsonHeaderResponseHandler(final TypeReference<C> tf) {
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
                Header[] headers = response.getAllHeaders();
                return new HeaderedResponse<C>(headers, mapper.readValue(responseString, tf));
            } else {
                throw createException(mime, status, reason, responseString);
            }
        };
    }

    /**
     * Create a response handler that writes to a stream. The return value is the number of btes
     * written to the output stream.
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
                Object d = data.get(key);
                if (d instanceof List || d.getClass()
                                          .isArray() || d instanceof Map) {
                    try {
                        d = this.mapper.writeValueAsString(d);
                    } catch (JsonProcessingException e) {
                        throw new SimbaException("Error converting array to JSON",
                            SimbaException.SimbaError.MESSAGE_ERROR, e);
                    }
                }
                builder.addTextBody(key, d.toString());

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

    public static class HeaderedResponse<C> {

        private Header[] headers;
        private C response;

        public HeaderedResponse(Header[] headers, C response) {
            this.headers = headers;
            this.response = response;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public C getResponse() {
            return response;
        }

        public String getHeaderValue(String name) {
            if (this.headers != null) {
                for (Header header : this.headers) {
                    if (header.getName()
                              .equalsIgnoreCase(name)) {
                        return header.getValue();
                    }
                }
            }
            return null;
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
}
