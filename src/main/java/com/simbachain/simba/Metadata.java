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


/**
 * Metadata about an application. This contains info about the contract
 * including methods, name and the type of network. Depending on the
 * network type, the functions to Fund, getBalance, and be able to
 * auto credit accounts may or may not be available.
 */
public interface Metadata {

    /**
     * The api name of the contract, used in the URL.
     * @return the contract API name
     */
    public String getApiName();

    /**
     * The URL to the faucet of the network if it's a test network with a faucet.
     * @return the faucet URL or null.
     */
    public String getFaucet();

    /**
     * The name of the contract. May be the same as the api name.
     * @return the name of the contract.
     */
    public String getName();

    /**
     * Get the name of the network instance.
     * @return the name of the network instance.
     */
    public String getNetwork();

    /**
     * Get the network type. 
     * @return the network type.
     */
    public String getNetworkType();

    /**
     * Is the nework Proof of Authority
     * @return true if the network is proof of authority.
     */
    public boolean isPoa();

    /**
     * Is simba in control of the faucet.
     * @return true if simba is in control of the faucet.
     */
    public boolean isSimbaFaucet();

    /**
     * The contract type. One of scaas, simbachain.com or platform
     * @return the type of contract.
     */
    public String getType();

    /**
     * Get a method object by name.
     * @param name the name of the method.
     * @return the method object with the given name.
     */
    public Method getMethod(String name);

    /**
     * The string that indicates a parameter is a file or file hash.
     * @return string representing a fie or file hash value.
     */
    public String getFileIndicator();

}
