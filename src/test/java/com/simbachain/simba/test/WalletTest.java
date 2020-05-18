/*
 * Copyright 2020 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.simbachain.simba.test;

import java.io.File;

import com.simbachain.SimbaException;
import com.simbachain.wallet.FileWallet;
import com.simbachain.wallet.Wallet;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class WalletTest {
    
    @AfterClass
    public static void shutdown() {
        File walletDir = new File("target/keys");
        walletDir.delete();
    }

    @Test
    public void testFileWallet() throws SimbaException {
        
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        String file = wallet.loadOrCreateWallet("password");
        assertTrue(file.contains("UTC--"));
        String addr = wallet.getAddress();
        assertTrue(addr.startsWith("0x"));

        Wallet wallet1 = new FileWallet("target/test-classes/keys", "wallet   test");
        String file1 = wallet1.loadOrCreateWallet("password");
        assertTrue(file1.contains("UTC--"));
        assertNotEquals(wallet.getAddress(), wallet1.getAddress());
        boolean deleted = wallet.deleteWallet();
        assertTrue(deleted);

        deleted = wallet1.deleteWallet();
        assertTrue(deleted);
    }

    @Test
    public void testMnemonicWallet() throws SimbaException {

        Wallet wallet = new FileWallet("target/test-classes/keys", "mnemonic");
        String file = wallet.loadOrCreateMnemonicWallet("password", "laundry pave energy busy stable near lobster teach address day gate index");
        assertTrue(file.contains("UTC--"));
        String addr = wallet.getAddress();
        assertTrue(addr.startsWith("0x"));
        String m = wallet.getMnemonic();
        assertTrue(m != null);

        Wallet wallet1 = new FileWallet("target/test-classes/keys", "mnemonic1");
        String file1 = wallet1.loadOrCreateMnemonicWallet("password", m);
        assertTrue(file1.contains("UTC--"));
        assertEquals(wallet.getAddress(), wallet1.getAddress());
        boolean deleted = wallet1.deleteWallet();
        assertTrue(deleted);

        deleted = wallet.deleteWallet();
        assertTrue(deleted);
        
    }

    @Test
    public void testNewMnemonicWallet() throws SimbaException {

        Wallet wallet = new FileWallet("target/test-classes/keys", "mnemonicnew");
        String file = wallet.generateMnemonicWallet("password");
        wallet.loadMnemonicWallet(wallet.getMnemonic());
        assertTrue(file.contains("UTC--"));
        String addr = wallet.getAddress();
        assertTrue(addr.startsWith("0x"));


        Wallet wallet1 = new FileWallet("target/test-classes/keys", "mnemonicnew1");
        String file1 = wallet1.loadOrCreateMnemonicWallet("password", wallet.getMnemonic());
        assertTrue(file1.contains("UTC--"));
        assertEquals(wallet.getAddress(), wallet1.getAddress());
        assertEquals(wallet.getPrivateKey(), wallet1.getPrivateKey());
        boolean deleted = wallet1.deleteWallet();
        assertTrue(deleted);

        deleted = wallet.deleteWallet();
        assertTrue(deleted);

    }
}
