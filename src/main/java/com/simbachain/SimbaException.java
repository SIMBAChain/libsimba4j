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

package com.simbachain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  General Exception class for Simba Errors.
 *  Each Exception has a SimbaError value to help
 *  identify the process that caused the exception.
 *  
 *  For HTTP_ERROR types the exception will also include the HTTP status code.
 */
public class SimbaException extends IOException {

    private SimbaError type;
    private Map<String, Object> properties = new HashMap<>();

    /**
     * Flavours of Wallet Exception
     */
    public enum SimbaError {
        
        INVALID_CREDENTIALS,
        METADATA_NOT_AVAILABLE,
        WALLET_NOT_FOUND, SIGN_REJECTED,
        SIGN_FAILED,
        WALLET_GENERATE_FAILED,
        DELETE_FAILED,
        LOAD_FAILED,
        MULTIPLE_WALLETS,
        WALLET_EXISTS,
        MESSAGE_ERROR,
        HTTP_ERROR,
        AUTHENTICATION_ERROR,
        FILE_ERROR,
        TRANSACTION_ERROR,
        EXECUTION_ERROR,
        PROCESSING_ERROR
    }

    public SimbaException(String message, SimbaError type) {
        super(message);
        this.type = type;
    }

    public SimbaException(String message, SimbaError type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public SimbaException(SimbaError type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    /**
     * The SimbaError enum
     * @return the SimbaError associated with this exception.
     */
    public SimbaError getType() {
        return type;
    }

    /**
     * The HTTP status code. May be null.
     * @return The HTTP status code. 
     */
    public int getHttpStatus() {
        Object o = properties.get("HTTP_STATUS");
        if(o != null) {
            try {
                return Integer.parseInt(o.toString());
            } catch (NumberFormatException e) {
                
            }
        }
        return -1;
    }

    public void setHttpStatus(int httpStatus) {
        properties.put("HTTP_STATUS", httpStatus);
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public void setProperties(Map<String, Object> props) {
        properties.putAll(props);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
