/*
 * Copyright 2019 SIMBA Chain Inc.
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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.simbachain.SimbaException;
import com.simbachain.simba.Balance;
import com.simbachain.simba.CallResponse;
import com.simbachain.simba.Funds;
import com.simbachain.simba.JsonData;
import com.simbachain.simba.PagedResult;
import com.simbachain.simba.Query;
import com.simbachain.simba.Simba;
import com.simbachain.simba.SimbaFactory;
import com.simbachain.simba.Transaction;
import com.simbachain.simba.com.SimbaChain;
import com.simbachain.simba.com.SimbaChainConfig;
import com.simbachain.simba.test.server.TestServer;
import com.simbachain.wallet.FileWallet;
import com.simbachain.wallet.Wallet;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */

public class SimbaChainTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private static MockServerClient msc;

    @BeforeClass
    public static void setup() throws Exception {
        msc = TestServer.mockServer("./test.properties");
    }

    @AfterClass
    public static void shutdown() throws IOException {
        msc.close();
    }

    @Test
    public void testGetTransaction() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        Transaction txn = simba.getTransaction("1234567890");
        assertEquals(txn.getState(), Transaction.State.INITIALIZED);  // no receipt in payload
    }

    @Test
    public void testMetadata() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        assertEquals(simba.getMetadata()
                          .getApiName(), "simbachain");
        Set<String> methods = new HashSet<>();
        methods.add("method1");
        methods.add("method2");
        methods.add("method3");
        assertEquals(simba.getMetadata()
                          .getMethods()
                          .keySet(), methods);
    }

    @Test
    public void testCall() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));

        JsonData data = JsonData.with("assetId", "1234")
                                .and("createdBy", "Andrew")
                                .and("name", "Foo");
        CallResponse response = simba.callMethod("method1", data);
        assertNotNull(response.getRequestIdentitier());
    }

    @Test
    public void testCallWithNonceError() throws SimbaException {
        CallResponse response = null;
        SimbaException expected = null;
        try {
            Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
            wallet.loadOrCreateWallet("password");

            SimbaChain simba = SimbaFactory.factory()
                                           .createSimbaChain("http://localhost:8080/", "simbachain",
                                               new SimbaChainConfig(
                                                   "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                                   wallet));

            JsonData data = JsonData.with("assetId", "1234");
            response = simba.callMethod("method3", data);
        } catch (SimbaException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertEquals(expected.getHttpStatus(), 409);
        assertEquals(expected.getType(), SimbaException.SimbaError.TRANSACTION_ERROR);
    }

    @Test
    public void testCallWithFile() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        
        JsonData data1 = JsonData.with("assetId", "1234")
                                 .and("number", 5)
                                 .and("name", "Foo");
        Simba.UploadFile uploadFile = new Simba.UploadFile("file_0", "text/plain",
            new File("src/test/resources/text.txt"));
        CallResponse response = simba.callMethod("method2", data1, uploadFile);
        assertNotNull(response.getRequestIdentitier());
    }

    @Test
    public void testNotFound() {
        SimbaException ex = null;
        try {
            Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
            wallet.loadOrCreateWallet("password");

            SimbaChain simba = SimbaFactory.factory()
                                           .createSimbaChain("http://localhost:8080/",
                                               "simbachainy",
                                               new SimbaChainConfig(
                                                   "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                                   wallet));
        } catch (SimbaException e) {
            ex = e;
        }
        assertNotNull(ex);
        assertEquals(ex.getHttpStatus(), 404);
    }

    @Test
    public void testPoaBalance() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        Balance balance = simba.getBalance();
        assertTrue(balance.getCurrency()
                          .equals(""));
        assertTrue(balance.getAmount().equals("0"));
    }

    @Test
    public void testNoPoaBalance() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain1",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        Balance balance = simba.getBalance();
        assertTrue(balance.getCurrency()
                          .equals("ethers"));
        assertTrue(balance.getAmount().equals("10"));
        assertFalse(balance.isPoa());
    }

    @Test
    public void testPoaFunds() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        Funds funds = simba.addFunds();
        assertTrue(funds.getFaucetUrl() == null);
        assertTrue(funds.getTxnId() == null);
        assertTrue(funds.isPoa());
    }

    @Test
    public void testNoPoaFunds() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain1",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));
        Funds funds = simba.addFunds();
        assertTrue(funds.getFaucetUrl().equals("https://faucet.com"));
        assertTrue(funds.getTxnId().equals("1234567890"));
        assertFalse(funds.isPoa());
    }

    @Test
    public void testQuery() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));

        PagedResult<Transaction> response = simba.getTransactions();
        assertEquals(response.getResults()
                             .size(), 2);
        response = simba.next(response);
        assertEquals(response.getResults()
                             .size(), 2);
        response = simba.previous(response);
        assertEquals(response.getResults()
                             .size(), 2);
    }

    @Test
    public void testMethodQuery() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/", "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));

        Query.Params params = Query.in("name", "Java")
                                   .ex("assetId", "1234")
                                   .in("createdBy", "Dave");

        PagedResult<Transaction> response = simba.getTransactions("method1", params);
        assertEquals(response.getResults()
                             .size(), 2);
        response = simba.next(response);
        assertEquals(response.getResults()
                             .size(), 2);
        response = simba.previous(response);
        assertEquals(response.getResults()
                             .size(), 2);
    }

    @Test
    public void testMethodQueryValidation() throws SimbaException {
        Wallet wallet = new FileWallet("target/test-classes/keys", "wallet test");
        wallet.loadOrCreateWallet("password");

        SimbaChain simba = SimbaFactory.factory()
                                       .createSimbaChain("http://localhost:8080/",
                                           "simbachain",
                                           new SimbaChainConfig(
                                               "04d1729f7144873851a745d2a000039f55c8e3de5aea626a2bcd0055c01ba6fc",
                                               wallet));

        Query.Params params = Query.in("name", "Java")
                                   .ex("assetId", "1234")
                                   .gte("number", 10);
        SimbaException ex = null;

        try {
            PagedResult<Transaction> response = simba.getTransactions("method1", params);
            assertEquals(response.getResults()
                                 .size(), 2);
        } catch (SimbaException e) {
            ex = e;
        }
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("unknown parameter"));

    }

}
