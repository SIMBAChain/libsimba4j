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

import com.simbachain.SimbaException;
import com.simbachain.simba.com.SimbaChain;
import com.simbachain.simba.com.SimbaChainConfig;

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
     * @param endpoint The contract endpoint, e.g. https://api.simbachain.com/v1/ 
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

}
