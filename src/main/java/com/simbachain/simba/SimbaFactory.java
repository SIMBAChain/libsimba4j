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

import com.simbachain.SimbaException;
import com.simbachain.simba.com.SimbaChain;
import com.simbachain.simba.com.SimbaChainConfig;
import com.simbachain.simba.com.async.AsyncSimbaChain;
import com.simbachain.simba.com.async.AsyncSimbaChainConfig;

/**
 * Factory for creating Simba instances.
 * Not required, but does some validation and calls init() on the instance.
 */
public class SimbaFactory {

    private SimbaFactory() {
    }

    private static SimbaFactory factory = new SimbaFactory();

    public static SimbaFactory factory() {
        return factory;
    }

    /**
     * Create a SIMBA Chain client for simbachain.com
     * @param endpoint The contract host, e.g. https://api.simbachain.com 
     * @param contract The contract or app name.
     * @param config Configuration used for the client.
     * @return an instance of SimbaChain.
     * @throws SimbaException if something goes wrong.
     */
    public SimbaChain createSimbaChain(String endpoint, String contract, SimbaChainConfig config)
        throws SimbaException {

        if (config.getApiKey() == null) {
            throw new SimbaException("No API key specified",
                SimbaException.SimbaError.INVALID_CREDENTIALS);
        }
        if (config.getWallet() == null) {
            throw new SimbaException("No wallet specified",
                SimbaException.SimbaError.INVALID_CREDENTIALS);
        }
        SimbaChain simbaChain = new SimbaChain(endpoint, contract, config);
        simbaChain.init();
        return simbaChain;

    }

    /**
     * Create an asynch SIMBA Chain client for simbachain.com
     *
     * @param endpoint The contract host, e.g. https://api.simbachain.com
     * @param contract The contract or app name.
     * @param config   Configuration used for the client.
     * @return an instance of AsyncSimbaChain.
     * @throws SimbaException if something goes wrong.
     */
    public AsyncSimbaChain createAsynchSimbaChain(String endpoint, String contract, AsyncSimbaChainConfig config)
        throws SimbaException {

        if (config.getApiKey() == null) {
            throw new SimbaException("No API key specified",
                SimbaException.SimbaError.INVALID_CREDENTIALS);
        }
        if (config.getWallet() == null) {
            throw new SimbaException("No wallet specified",
                SimbaException.SimbaError.INVALID_CREDENTIALS);
        }
        AsyncSimbaChain simbaChain = new AsyncSimbaChain(endpoint, contract, config);
        simbaChain.init();
        return simbaChain;

    }

}
