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

package com.simbachain.wallet;

import java.io.File;
import java.security.SecureRandom;

import com.simbachain.SimbaException;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;


/**
 *  File based Wallet implementation.
 *  This supports standard wallet files as well as Bip39 wallets that
 *  have an associated mnemonic.
 */
public class FileWallet extends Wallet {

    private Credentials credentials;
    private File walletDir;
    private String mnemonic = null;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Create a FileWallet. The directory is the root path to where you store keys.
     * The name parameter is used to create a subdirectory under the root. It is under
     * the name subdirectory that a single wallet file lives. That file will have the
     * name of the current UTC timestamp and the address in it.
     * NOTE: The name parameter may get mangled to ensure a valid directory name is used.
     * @param directory root directory
     * @param name sub directory for locating a single wallet file.
     */
    public FileWallet(String directory, String name) {
        super();
        walletDir = new File(directory, mangleForFile(name));
        if(!walletDir.exists() || walletDir.length() == 0) {
            boolean created = walletDir.mkdirs();
            if (!created) {
                throw new RuntimeException(String.format("Failed to create directory %s", walletDir.getAbsolutePath()));
            }    
        }
        if(walletDir.exists() && !walletDir.isDirectory()) {
            throw new RuntimeException(String.format("Wallet directory %s is not a directory", walletDir.getAbsolutePath()));
        }
    }

    /**
     * Delete the wallet.
     *
     * @return This returns true if deletion succeeds, false if it fails.
     * @throws SimbaException if an error occurs during the attempt to delete.
     */
    @Override
    public synchronized boolean deleteWallet() throws SimbaException {
        
        if (!walletDir.exists() || !walletDir.isDirectory()) {
            throw new SimbaException(
                String.format("Unknown directory: %s", walletDir.getAbsolutePath()), SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        try {
            return deleteWalletDir(walletDir);
        } catch (Exception e) {
            throw new SimbaException("Cannot delete wallet", SimbaException.SimbaError.DELETE_FAILED);
        }
    }

    /**
     * Generate a new Wallet.
     *
     * @param passkey the password used for the Wallet.
     * @return The location of the created Wallet. What this actually represents is dependent
     * on the Wallet implementation.
     * @throws SimbaException if an error occurs.
     */
    @Override
    public synchronized String generateWallet(String passkey)
        throws SimbaException {
        if(walletExists()) {
            throw new SimbaException("Error generating wallet. A wallet already exists.", SimbaException.SimbaError.WALLET_EXISTS);
        }
        try {
            String filename = WalletUtils.generateNewWalletFile(passkey, walletDir);
            return new File(walletDir, filename).getAbsolutePath();
        } catch (Exception e) {
            throw new SimbaException("Error generating wallet", SimbaException.SimbaError.WALLET_GENERATE_FAILED, e);
        }
    }

    /**
     * Generate a Wallet that can be recreated elsewhere with a mnemonic.
     * Once this method completes, the getMnemonic function will return the
     * mnemonic used when generating the wallet. This is the only circumstance
     * under which the getMnemonic method will not return null.
     *
     * @param passkey the password to use.
     * @return return the location of the Wallet.
     * @throws SimbaException if an error occurs.
     */
    @Override
    public String generateMnemonicWallet(String passkey) throws SimbaException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        return generateMnemonicWallet(passkey, mnemonic);
    }

    /**
     * Generate a Wallet that can be recreated elsewhere with a mnemonic.
     * Once this method completes, the getMnemonic function will return null
     * as the client already knows it.
     *
     * @param passkey  the password to use.
     * @param mnemonic the mnemonic to use.
     * @return return the location of the Wallet.
     * @throws SimbaException if an error occurs.
     */
    @Override
    public synchronized String generateMnemonicWallet(String passkey, String mnemonic)
        throws SimbaException {
        if (walletExists()) {
            throw new SimbaException("Error generating wallet. A wallet already exists at " + walletDir,
                SimbaException.SimbaError.WALLET_EXISTS);
        }
        try {
            Bip39Wallet bw = WalletUtils.generateBip39WalletFromMnemonic(passkey, mnemonic,
                walletDir);
            this.mnemonic = mnemonic;
            return new File(walletDir, bw.getFilename()).getAbsolutePath();
        } catch (Exception e) {
            throw new SimbaException("Error generating wallet", SimbaException.SimbaError.WALLET_GENERATE_FAILED, e);
        }

    }

    /**
     * Load the Wallet using the password.
     *
     * @param passkey The password for the Wallet.
     * @return The location of the Wallet.
     * @throws SimbaException if an error occurs
     */
    @Override
    public synchronized String loadWallet(String passkey)
        throws SimbaException {
        try {
            File wallet = getWalletFile(walletDir);
            if (wallet == null) {
                throw new SimbaException(
                    String.format("Cannot find wallet at %s", walletDir.getAbsolutePath()), SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            this.credentials = WalletUtils.loadCredentials(passkey, wallet);
            return wallet.getAbsolutePath();
            
        } catch (Exception e) {
            throw new SimbaException(SimbaException.SimbaError.LOAD_FAILED, e);
        }
    }

    /**
     * Load the Wallet using the password.
     *
     * @param mnemonic The mnemonic for the Wallet.
     * @return The location of the Wallet.
     * @throws SimbaException if an error occurs
     */
    @Override
    public synchronized String loadMnemonicWallet(String mnemonic) throws SimbaException {
        try {
            File wallet = getWalletFile(walletDir);
            if (wallet == null) {
                throw new SimbaException(
                    String.format("Cannot find wallet at %s", walletDir.getAbsolutePath()),
                    SimbaException.SimbaError.WALLET_NOT_FOUND);
            }
            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,
                                    0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(
                MnemonicUtils.generateSeed(mnemonic, null));
            Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair,
                derivationPath);
            this.credentials = Credentials.create(derivedKeyPair);
            this.mnemonic = mnemonic;
            return wallet.getAbsolutePath();

        } catch (Exception e) {
            throw new SimbaException(SimbaException.SimbaError.LOAD_FAILED, e);
        }
    }

    /**
     * If the Wallet was created with a mnemonic that was not supplied by the client,
     * this will be returned.
     *
     * @return The mnemonic of the wallet if create as a Bip32 Wallet.
     * @throws SimbaException if an error occurs
     */
    @Override
    public String getMnemonic() throws SimbaException {
        return mnemonic;
    }

    /**
     * Check if the Wallet exists and could be loaded.
     * This checks if a wallet file exists.
     *
     * @return Return true if the Wallet exists, false otherwise.
     * @throws SimbaException if an error occurs.
     */
    @Override
    public synchronized boolean walletExists() throws SimbaException {
        
        File[] files = walletDir.listFiles((File p, String filename) -> {
            return filename.startsWith("UTC--");
        });
        if (files == null) {
            throw new SimbaException(String.format("Could not find wallet at %s", walletDir.getAbsolutePath()),
                SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        return files.length == 1;
    }

    /**
     * Method for subclasses to implement. Return the loaded credentials.
     *
     * @return the Credentials from a loaded Wallet.
     * @see org.web3j.crypto.Credentials
     */
    @Override
    protected synchronized Credentials getCredentials() {
        return this.credentials;
    }

    private File getWalletFile(File dir) throws SimbaException {
        File[] files = dir.listFiles((File p, String name) -> {
            return name.startsWith("UTC--");
        });
        if (files == null) {
            throw new SimbaException(String.format("Could not find wallet at %s", dir.getAbsolutePath()),
                SimbaException.SimbaError.WALLET_NOT_FOUND);
        }
        if(files.length != 1) {
            throw new SimbaException(String.format("Multiple wallets at %s", dir.getAbsolutePath()),
                SimbaException.SimbaError.MULTIPLE_WALLETS);
        }
        return files[0];
    }

    private String mangleForFile(String name) throws RuntimeException {
        if (name == null) {
            throw new RuntimeException("Null name");
        }
        name = name.trim()
                   .toLowerCase()
                   .replace(' ', '_')
                   .replaceAll("(?u)[^-\\w.]", "");
        if (name.length() == 0) {
            throw new RuntimeException("Empty name");
        }
        return name;
    }
    
    private boolean deleteWalletDir(File f) {
        try {
            if (f.isDirectory()) {
                for (File c : f.listFiles())
                    deleteWalletDir(c);
            }
            return f.delete();
        } catch (Exception e) {
            return false;
        }
    }

}


