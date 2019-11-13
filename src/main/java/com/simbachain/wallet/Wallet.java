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

package com.simbachain.wallet;

import com.simbachain.SimbaException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

/**
 * Abstract base class for Wallet implementations.
 * A Wallet is a public private key pair and is unlocked via a password.
 * It can also be created with a mnemonic - either passed in, or generated. This can be used to recreate
 * the wallet elsewhere and can be retrieved using the getMnemonic method.
 */
public abstract class Wallet {

    /**
     * For subclasses.
     */
    protected Wallet() {
    }

    /**
     * Delete the wallet.
     * @return This returns true if deletion succeeds, false if it fails.
     * @throws SimbaException if an error occurs during the attempt to delete.
     */
    public abstract boolean deleteWallet() throws SimbaException;

    /**
     * Generate a new Wallet.
     * @param passkey the password used for the Wallet.
     * @return The location of the created Wallet. What this actually represents is dependent
     * on the Wallet implementation.
     * @throws SimbaException  if an error occurs.
     */
    public abstract String generateWallet(String passkey) throws SimbaException;

    /**
     * Generate a Wallet that can be recreated elsewhere with a mnemonic.
     * Once this method completes, the getMnemonic function will return the
     * mnemonic used when generating the wallet. This is the only circumstance
     * under which the getMnemonic method will not return null.
     * @param passkey  the password to use.
     * @return return the location of the Wallet.
     * @throws SimbaException if an error occurs.
     */
    public abstract String generateMnemonicWallet(String passkey) throws SimbaException;

    /**
     * Generate a Wallet that can be recreated elsewhere with a mnemonic.
     * Once this method completes, the getMnemonic function will return non-null.
     *
     * @param passkey the password to use.
     * @param mnemonic the mnemonic to use.
     * @return return the location of the Wallet.
     * @throws SimbaException if an error occurs.
     */
    public abstract String generateMnemonicWallet(String passkey, String mnemonic) throws SimbaException;

    /**
     * Get the address of the Wallet.
     * @return The address of the Wallet as a string.
     * @throws SimbaException  if an error occurs
     */
    public String getAddress() throws SimbaException {
        Credentials credentials = getCredentials();
        if (credentials == null) {
            throw new SimbaException("No credentials available", SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        return credentials.getAddress();
    }

    /**
     * Sign a RawTransaction object.
     * @param rawTransaction a raw transaction.
     * @return The signed transaction as a hex string.
     * @throws SimbaException  if an error occurs.
     * @see org.web3j.crypto.RawTransaction
     */
    public String sign(RawTransaction rawTransaction) throws SimbaException {
        try {
            Credentials credentials = getCredentials();
            if(credentials == null) {
                throw new SimbaException("No credentials available", SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            return Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            throw new SimbaException(SimbaException.SimbaError.SIGN_FAILED, e);
        }
    }

    /**
     * Convenience method to check if the Wallet exists and if not, create it. Then try to load it.
     * @param passkey The password to possibly create and then load the Wallet.
     * @return The location of the Wallet.
     * @throws SimbaException  if an error occurs.
     */
    public String loadOrCreateWallet(String passkey) throws SimbaException {
        
        if (!walletExists()) {
            generateWallet(passkey);
        }
        return loadWallet(passkey);
    }

    /**
     * Convenience method to check if the Wallet exists and if not, create it. Then try to load it.
     *
     * @param passkey The password to possibly create and then load the Wallet.
     * @param mnemonic The mnemonic to use.               
     * @return The location of the Wallet.
     * @throws SimbaException if an error occurs.
     */
    public String loadOrCreateMnemonicWallet(String passkey, String mnemonic) throws SimbaException {

        if (!walletExists()) {
            generateMnemonicWallet(passkey, mnemonic);
        }
        return loadMnemonicWallet(mnemonic);
    }

    /**
     * Returns the private key for the wallet in hex format.
     * Note: this is without the '0x' prefix.
     *
     * @return a hex string representation of the private key.
     */
    public String getPrivateKey() {
        Credentials c = getCredentials();
        return c.getEcKeyPair()
                .getPrivateKey()
                .toString(16);
    }

    /**
     * Load the Wallet using the password.
     * @param passkey The password for the Wallet.
     * @return The location of the Wallet.
     * @throws SimbaException if an error occurs
     */
    public abstract String loadWallet(String passkey) throws SimbaException;

    /**
     * Load the Wallet using the password.
     *
     * @param mneminic The mnemonic for the Wallet.
     * @return The location of the Wallet.
     * @throws SimbaException if an error occurs
     */
    public abstract String loadMnemonicWallet(String mneminic) throws SimbaException;
    
    /**
     * If the Wallet was created with a mnemonic that was not supplied by the client,
     * this will be returned. 
     * @return The mnemonic of the wallet if create as a Bip32 Wallet.
     * @throws SimbaException if an error occurs
     */
    public abstract String getMnemonic() throws SimbaException;

    /**
     * Check if the Wallet exists and could be loaded.
     * @return Return true if the Wallet exists, false otherwise.
     * @throws SimbaException  if an error occurs.
     */
    public abstract boolean walletExists() throws SimbaException;

    /**
     * Method for subclasses to implement. Return the loaded credentials.
     * @return the Credentials from a loaded Wallet.
     * @see org.web3j.crypto.Credentials
     */
    protected abstract Credentials getCredentials();
    

}
