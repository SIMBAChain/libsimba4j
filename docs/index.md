#  LibSimba4J Documentation

This page provides usage exeamples of the Java SIMBA Chain client.

JavaDocs are available <a href="./api-doc/index.html" target="_blank">here</a>


## Getting Started

<!---

To use LibSimba4J include it as a dependency. For Maven builds add the following dependency
to your pom file:
 
```
<dependency>
    <groupId>com.simbachain</groupId>
    <artifactId>libsimba4j</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

or for Gradle:

```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.simbachain:libsimba4j:0.1.0-alpha'
}
```

-->

LibSimba4J builds with maven. You will need to have maven 3.* installed. Once you have maven,
cd into the top level directory and type:

```shell
mvn install
```


## Creating a Simba Instance

Currently there is one Simba implementation available that talks to simbachain.com.
To create an instance, you need a Wallet, an API Key and a deployed contract.

The FileWallet implementation provides means to create a Wallet that has a private key and
Ethereum address. You can create or load a pre-existing wallet file like this:

```
Wallet wallet = new FileWallet("./keys", "mywallet");
wallet.loadOrCreateWallet("mypassword");
```

If you have a wallet that you have used before elsewhere, you can re-instantiate it using the
mnemonic for the wallet:

```
Wallet wallet = new FileWallet("./keys", "mywallet");
wallet.loadOrCreateMnemonicWallet("mypassword", "afford gift small spoil target describe arrange frequent foam cross stick mistake");
```

If you want to create a wallet with a new mnemonic that you can save somewhere safe, you can run:

```
Wallet wallet = new FileWallet("./keys", "mywallet");
wallet.loadOrCreateMnemonicWallet("mypassword");
String m = wallet.getMnemonic();
System.out.println(m);
```

The FileWallet creates directories if they do not exist. The first argument to the constructor
is a directory that can contain multiple wallets. THe second argument is a subdirectory
for a named wallet that can only contain a single wallet file. Therefore, you will encounter
errors if you try to create multiple wallets using the same wallet name.


Once you have a Wallet, you need an API Key. You can get this at simbachain.com for a deployed app from
the Api Key menu item on the simbachain.com navigation bar.

Finally, you need your API endpoint and contract or app name. The API endpoint will look like this
for simbachain.com:

`https://api.simbachain.com`

and the contract or name is whatever your public api name is or contract name in the ase of SCaaS.

where `myapp` is the api name of your app on simbachain.com.

Next, construct a SimbaChainConfig object using the api key and the wallet, and provide the endpoint
to SimbaFactory factory:

```
try {
   SimbaChain simba = SimbaFactory.factory()
       .createSimbaChain("http://localhost:8080/v1/", "simbachain",
             new SimbaChainConfig("04d1729f7144873851a745d2ae85639f55c8e3de5aea626a2bcd0055c01ba6fc",
                                       wallet));
} catch(SimbaException se) {
    se.printStackTrace();
}

```

Using the factory is not required, but it does some validation and calls the `init()` on the Simba
instance before returning it. The `init()` method retrieves the application metadata from
simbachain.com and ensures the client is up and running correctly.
 
## Posting A Transaction

Each method on a contract has parameters defined by you. These are added as key value pairs
to the body of the POST request. Additionally a method may allow file uploads, in which case
these are also optionally added. The `callMethod` function is used to perform this. Below is
an example of posting to a method with three parameters (`assetId`, `number` and `name`) as
well as uploading files which will be stored off chain.

```
try {
    JsonData data1 = JsonData.with("assetId", "1234")
                                 .and("number", 5)
                                 .and("name", "Foo");
    Simba.UploadFile uploadFile = new Simba.UploadFile("file_0", "text/plain", new File("text.txt"));
    MethodResponse response = simba.callMethod("myMethod", data1, uploadFile);
    System.out.println(response.getRequestIdentitier());
} catch(SimbaException se) {
    se.printStackTrace();
}
```

The response will contain the UID of the request. This UID can be used to query for the transaction.

This can be used to determine the status of the transaction:

```
try {
    TransactionStatus status = simba.checkTransactionStatus(response.getRequestIdentitier());
    System.out.println(status.getStatus());
} catch(SimbaException se) {
    se.printStackTrace();
}
```


## Querying

To query for multiple transactions, you can use the `Query.Param` class that creates filters
on the inputs of the transactions. Queries can be by contract method, or across all
 methods. Query filters are dependent on the input type:
  
* String: exact match `ex()` and contains `in()`
* Number: equals `eq()`, greater than `gt()`, less than `lt()`, greater than or equal `gte()`, less than or equal `lte()`
* Boolea: equals `is()`

These queries are concatenated into a query string, e.g.:

`Query.Params params = Query.in("name", "Java").ex("assetId", "1234").gte("number", 10);`

The names of fields being filtered are validated against the contract method if the query
is for a given contract method.

 Once you have your query parameters you can query for transactions:
  
```
try {
    PagedResult<Transaction> response = simba.getTransactions(params);
    List<Transaction> transactions = response.getResults();
    for (Transaction transaction : transactions) {
        System.out.println(transaction);
    }
} catch(SimbaException se) {
    se.printStackTrace();
}  
```

or query for a method:

```
try {
    PagedResult<Transaction> response = simba.getTransactions("mymethod", params);
} catch(SimbaException se) {
    se.printStackTrace();
}  
```

A PagedResult contains a `count` as well as `getNext()` and `getPrevious()` URLs that can be used to retrieve
the next or previous page. A Simba instance is used to call these URLs via
you can call `simba.next(result)` and `simba.previous(result)` to get further pages. These
will return `null` if there are not pages.

```
PagedResult<Transaction> response = simba.getTransactions("method1", params);
while(response != null) {
    System.out.print(response.getResults());
    response = simba.next(response);
}
```


Off chain data is stored in `bundles`. A bundle is a group of files that were uploaded
together, along with metadata about each of those files.


To Query for off chain data, you can use three methods. Below pulls the metadata
for a given bundle and returns it as a Manifest object. At a minimum, a Manifest contains a list
of ManifestFiles which have a name, mime type and size in bytes.

**NOTE: THis API is still evolving until the bundle mechanism is unified across the simbachain.com
and Enterprise versions of SIMBA**.

```
try {
    Manifest metadata = simba.getBundleMetadataForTransaction(response.getRequestIdentitier());
    List<ManifestFile> files = meta.getFiles();
    for (ManifestFile file : files) {
        System.out.println(file.toString());
    }
} catch(SimbaException se) {
    se.printStackTrace();
} 
```


The following two methods retrieve a file from a bundle by name, or the whole bundle as a compressed
file. They both take an output stream to write to. When the bundle or file is retrieved,
it is streamed directly to the provided output stream and the output stream is closed.


```
try {
    OutputStream myOutputStream = new FileOutputStream(...);
    long = simba.getBundleFileForTransaction(response.getRequestIdentitier(),
                    "myfile", myOutputStream);
    System.out.print(long);
} catch(SimbaException se) {
    se.printStackTrace();
}

try {
    OutputStream myOutputStream = new FileOutputStream(...);
    long = simba.getBundleForTransaction(response.getRequestIdentitier(), myOutputStream);
    System.out.print(long);
} catch(SimbaException se) {
    se.printStackTrace();
}
```


Use the variants that allow specifying wether or not to close the output stream and set that to false
if you want to keep the output stream open:

```
try {
    OutputStream myOutputStream = new FileOutputStream(...);
    long = simba.getBundleFileForTransaction(response.getRequestIdentitier(),
                    "myfile", myOutputStream, false);
    System.out.print(long);
} catch(SimbaException se) {
    se.printStackTrace();
}

try {
    OutputStream myOutputStream = new FileOutputStream(...);
    long = simba.getBundleForTransaction(response.getRequestIdentitier(), myOutputStream, false);
    System.out.print(long);
} catch(SimbaException se) {
    se.printStackTrace();
}
```


## Exception Handling

Requests - both POST and GET will produce a `SimbaException` of the HTTP response code is
not in the two hundreds. In this case, the `SimbaException` will have a `SimbaError` enum
value of `HTTP_ERROR` and contain the status code of the HTTP response. Additionally, the
error message aims to provide details of the error returned from the server. 


## Logging

LibSimba4J uses the slf4j logging API library and is not tied to a specific logging implementation.
All logging is done at a debug level.






 
 

