# HCAP : Developer Documentation
In this document you will find the developer documentation to set up tools related to HCAP on your system and start programing using the HCAP API.

## Configuration Instructions
This section mentions the installation procedure of tools required to compile and run HCAP API.

### Java Development Kit
1. Download the file, jdk-10.0.1_linux-x64_bin.tar.gz from the following link. (Link: http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)
2. Change the directory to the location where you want to install the JDK, then move the .tar.gz archive binary to the current directory.
3. Unpack the tarball and install the JRE using the following. 
> **% tar zxvf jre-9.minor.security.patch_linux-x64_bin.tar.gz**. 

4. The Java Development Kit files are installed in a directory called jre-9.minor.security.patch.

5. For detailed information visit https://docs.oracle.com/javase/9/install/installation-jdk-and-jre-linux-platforms.htm#JSJIG-GUID-D54A6F28-A305-4249-A5BF-835B25644C26.

### Maven
1. Check the latest tar.gz file from http://mirror.olnevhost.net/pub/apache/maven/binaries.
2. Suppose the file is apache-maven-3.2.1-bin.tar.gz, now from commandline run the following: 
>**wget http://mirror.olnevhost.net/pub/apache/maven/binaries/apache-maven-3.2.1-bin.tar.gz**

3. Now, lets proceed to the installation procedure.
4. Run the following command to extract the tar.
>**tar xvf apache-maven-3.2.1-bin.tar.gz**
5. Add the environment variables such as:
>export M2_HOME=/usr/local/apache-maven/apache-maven-3.2.1

> export M2=$M2_HOME/bin 

>export PATH=$M2:$PATH

6. Verify that maven has been successfully installed. 
>**mvn -version**


## Directory Structure

Within the HCAP project, one would find the following folders.

### KeyStores
In this the user would find two files. trustStore.jks represents the trust store while keyStore.jks represents the key store used in configuring HCAP servers and clients.

### PropertiesFiles
This folder contains the properties files for authorization server, resource server and the client.

### SecureAuthServer
This contains the following important sub folders.

#### doc
This folder contains the javadoc for authorization server code.

#### src
It contains the following sub folders.

1. ExceptionList:
It contains the data structure used for storing the exception list.

2. SecureAuthServer/SecureAuthServer: It contains the actual HCAP Authorization server code.

3. StateMachines: 
It contains some demo state machines encoded as java code.




Apart from the above folders, it SecureAuthServer contains important files such as Californium.properties and pom.xml.

_**Californium.properties**_ file is used to configure the calfornium CoAP servers. 

_**pom.xml**_ acts as the maven configuration file for the project. It contains all the dependencies of the project.

### SecureResServer
This contains the following important sub folders.

#### doc
This folder contains the javadoc for resource server code.

#### src/main/java/
It contains the following sub folders.

1. ExceptionList: It contains the data structure used for storing the exception list.

2. SecureResServer/SecureResServer: It contains the actual resource server code.

3. ExperimentResources: It contains the code for some demo resources which can be attached to the resource server.

#### src/test/java/SecureResServer/SecureResServer
This folder contains some tests for the resource server.


Apart from the above folders, it SecureResServer contains important files such as Californium.properties and pom.xml.

_**Californium.properties**_ file is used to configure the calfornium CoAP servers. 

_**pom.xml**_ acts as the maven configuration file for the project. It contains all the dependencies of the project.

### SecureClient
This contains the following important sub folders.

#### doc
This folder contains the javadoc for client code.

#### src/java/main/java/SecureClient/SecureClient
This folder contains the actual client code.

#### src/test/java/SecureClient/SecureClient
This folder contains some tests for the client.

Apart from the above folders, it SecureClient contains important files such as Californium.properties and pom.xml.

_**Californium.properties**_ file is used to configure the calfornium CoAP servers. 

_**pom.xml**_ acts as the maven configuration file for the project. It contains all the dependencies of the project.

## How to build
### Compile
Go the directory containing the code for component (authorization server, resource server or the client) you want to compile. Run the following commands.

>mvn clean

>mvn compile

### Javadoc
To generate javadoc, you can add the javadoc plugin in the _build_ section of the pom.xml file. Eg: 

```` 
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
          ...
        </configuration>
      </plugin>
    </plugins>
    ...
  </build>
  ...
</project>
````
Now run the following command

> mvn javadoc:javadoc

### Test
To run tests uning maven, go to the directory containing tests. To run all the tests inside the directory issue the following command.

> mvn test

To run a single test i.e. testOne, issue the following command.

> mvn -Dtest=testOne test

## Setting up properties
Properties files in HCAP act as configuration files for each of the HCAP components. These contains important parameters needed to set up the servers and the client machine. This section mentions the parameters used in the properties files.

### Authorization Server Properties File
The following parameters are to be set in AuthServer.properties file.

* **isCBOR**: Set this as true if CBOR is to be used as data transfer format and false to used JSON as data transfer format.

* **trustStoreLocation**: Specify the path of trust store used.

* **trustStorePassword**: Specify the password used for trust store.

* **keyStoreLocation**: Specify the path of key store used.

* **keyStorePassword**: Specify the password used for key store.

* **port**: Specify the port number here, the server would run on the port specified.

* **sharedSecret**: Specify this to add a shared secret shared between authorization server and resource server.

### Resource Server Properties File
The following parameters are to be set in ResServer.properties file.

* **authserveraddress**: Specify the address of authorization server, including the port number. For eg: 127.0.0.1:5683.

* **trustStoreLocation**: Specify the path of trust store used.

* **trustStorePassword**: Specify the password used for trust store.

* **keyStoreLocation**: Specify the path of key store used.

* **keyStorePassword**: Specify the password used for key store.

* **port**: Specify the port number here, the server would run on the port specified.

* **updateRequestCounter**: This represents the soft GC threshold in HCAP. For eg: 200 means that soft GC will occur after every 400 requests to the resource server.

* **hard GC Threshold**: This represents the hard GC threshhold in HCAP. For eg: 400 means that hard GC will occur after ever 400 requests to the resource server.

* **isCBOR**: Set this as true if CBOR is to be used as data transfer format and false to used JSON as data transfer format.

* **sharedSecret**: Specify this to add a shared secret shared between authorization server and resource server.

### Client Properties File
The following parameters are to be set in Client.properties file.

* **isCBOR**: Set this as true if CBOR is to be used as data transfer format and false to used JSON as data transfer format.

* **trustStoreLocation**: Specify the path of trust store used.

* **trustStorePassword**: Specify the password used for trust store.

* **keyStoreLocation**: Specify the path of key store used.

* **keyStorePassword**: Specify the password used for key store.

* **port**: Specify the port number here, the server would run on the port specified.

* **authserveraddress**: Specify the address of authorization server, including the port number. For eg: 127.0.0.1:5683.

* **resserveraddress**: Specify the address of resource server, including the port number. For eg: 127.0.0.1:8080.

## Javadoc Location 
Seperate javadocs are available for the authorization server, resource server and the client. These javadocs are located in the following folders.


**Authorization Server:** _'HCAPDevTeam/hcap/tree/master/SecureAuthServer/doc'_ 

**Resource Server:** _'HCAPDevTeam/hcap/tree/master/SecureResServer/doc'_

**Client:** 
_'HCAPDevTeam/hcap/tree/master/SecureClient/doc'_

For each of the above componets, _**index.html**_ acts as an entry point to the API documentation.


