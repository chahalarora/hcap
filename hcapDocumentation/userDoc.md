# HCAP: User Documentation
## Setting up Servers
This section describes how an HCAP user would be able to set up authorization and resource servers and start their working with HCAP.

It is assumed that the user already has downloaded the code for authorization server and the resource server. If not, use https://github.com/HCAPDevTeam/hcap to download code.

### Authorization Server
To set up the authorization server, the authorization server code accepts few inputs. The inputs are to be provided to the code before starting the server. The inputs are:

#### Properties file location
The authorization server reads parameters from the authorization server's properties file. For more information regarding properties file parameters refer developer documentation.

After setting the parameteres in properties file, the location of the file goes as an input to the constructor of HCAPAuthorizationServer class.

#### Client's SecurityAutomaton
The authorization server needs to know the security automaton for each client in order to provide capabilities. The user needs to write a class to represent a state machine. This class implements the SecurityAutomaton inerface found in the authorization server code.

A sample client Security Automaton is provided with the code. You can find this in StateMachines/InitStateMachine.java.

Once you have written the class to represent a security automaton. An instantiation of this class would go as one of the inputs to **addClientStateMachine** method of the authorization API.

**addClientStateMachine** accepts three parameters.
    
* Client name as represented in the client certificate.
* An instantiation of client security automaton.
* The size of fragment. Eg: if the size is 0, the client capability would contain permissions about only the current state. If the size is 1, the client can traverse one extra state without the need to visit the authorization server. If the size is -1, the authorization server encodes the full security automaton inside the capability, the client does not need to visit the authorization server at all.


#### Information about resource servers

##### Shared secret of resource server
The authorization server also needs to know some information about the resource servers it serves. Each resource server shares a shared secret with the authorization server so that both of these can process tickets signed by each other. Thus, the user needs to let the authorization server know about the resource servers shared secret. For the same it uses **addResourceServer** method which accepts two parameters. The first parameter is the **ip address + portnumber** which is the address of the resource server. The second parameter is the shared secret for that resource server. A usage example of this can be found in **App.java** source file.

##### State mapping to resource server
A security automaton can contain a number of states. In a multiple resource server scenario. A single resource server can be representative of a single state or multiple states. The authorization server should know identity of the resource server which is representative of the current client state. This is because the authorization server would use the representative resource servers shared secret to sign the capability issued to the client.

**addToStateRSMap** can be used to do the same. It accepts two parameters. The first one is the state from the security automaton. The second one is **ip address + portnumber** which is the address of the resource server. A usage example of this can be found in **App.java** source file.



#### Starting the Authorization Server
Once all inputs are given to the authorization server object, **startHCAPServer** method can be invoked on the object to start the server. This method does not accept any parameters but returns a server object which can be used to shut down the server. **stopHCAPServer** can be invoked on the returned object to shut down the server.


### Resource Server
Resource server needs some user inputs to start. These inputs are to be provided before calling the start method on the server object.
The inputs are listed below.

#### Properties file location
The resource server reads parameters from the resource server's properties file. For more information regarding properties file parameters refer developer documentation.

After setting the parameteres in properties file, the location of the file goes as an input to the constructor of HCAPResourceServer class.

#### Resources to be hosted at the server
The resource server next needs to know the resources which it should be hosting. These resources go as a parameter to addResourcesToServer method defined by HCAPResourceServer class. This method accepts an array of objects of type CoapResource. The user needs to create this array before passing it to addResourcesToServer method. An example to construct resources has been provided in ExperimentResources/ConstructResourcesTestMachine.java.

Once an array is created, it goes as an input to *addResourcesToServer* method. An example can be found in lines 22 - 24 of file SecureResServer.SecureResServer/App.java.

#### A map which maps method-resource pair to permission
Permissions in HCAP are represented using a CoAP method and a resource ID. As HCAP follows a RESTful architectural style, each resource is presented using a unique URI. CoAP metods are invoked on the URI to get access to a resource. The resouce server can fetch the CoAP method and the resource ID from the request to see the permisson which is requested. But for enabling such a functionality, it needs to know the mapping between (CoAPMethod + ResourceID) and permission. This mapping is to be provided by the user configuring HCAPResourceServer. 

The user can write a method which returns a map mapping CoAP Method-ResourceID pair to permission. An example to show how this can be done is given in *createMap* method (Lines 54 - 66) of file SecureResServer.SecureResServer/App.java.

Once user has createed a map, this map goes as a second parameter to the contructor of HCAPResourceServer.

#### Starting the resource server
Once all inputs are given to the resource server object, **startHCAPServer** method can be used to start the server. This method does not accept any parameters but returns a server object which can be used to shut down the server. **stopHCAPServer** can be invoked on the returned object to shut down the server.


## Setting Up the Client
The client machine is sends requests to both the servers. It gets capabilties from the authorization server. And using those capabilities, it tries to access resources hosted by the resource servers.

The ClientBuilder class in HCAP client API provides functionality to perform the above mentioned operations.

### Creating an instance of ClientBuilder
The constructor of **ClientBuilder** class accepts the location of properties file for client. Pass it as an argument and create an instance of the class to be used for later operations.

> For more information regarding the contents client properties file, refer developer documentation.

### Getting a new capability
The first step of a client is to get a new capability from the authorization server. For this, **ClientBuilder** provides method called **getCapability**. This method does not expect any parameters and returns a capability corresponding to the current client state after contacting the authorization server.

### Requesting acccess to resources at a resource server
For this, **ClientBuilder** provides method called **requestAccess**. It accepts three parameters, the first is the capability to be used, the second is the payload to be delivered to the resource and the third is the name of the resource it self. After execution of this method, it returns a capability or a state update request. If a capability is returned, the client can again use **requestAccess** to access the next resource at the resource server. If a state update is returned, then see below for further information.

### Requesting a new capability after state update request is returned
If a state update request is retuned by the **requestAccess** method. The client can then use **sendUpdateRequestAndGetCapability** to get a new usable capability. This method accepts one parameter, which is the state update request returned by the resource server. As a result of execution of this method, it return a new capability which can be used with the resource server.

### Getting back a lost capability
If a client looses its capability, it can use **reIssueCapabilityFromAuthServer** method provided by **ClientBuilder** to get a new capability. This method accepts the client's sessionID as a parameter. It returns the capability or a state update request. If a state update request is returned, the client needs to contact the authorization server to get a new capability. See previous step for this.
