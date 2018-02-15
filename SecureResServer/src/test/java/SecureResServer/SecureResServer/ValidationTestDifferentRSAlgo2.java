package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.network.Exchange.Origin;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;
import ExperimentResources.ConstructResourcesEx1;
import ExperimentResources.ConstructResourcesEx2;
import ExperimentResources.ConstructResourcesTestMachine;

public class ValidationTestDifferentRSAlgo2 
{
	String userID = "testUser";
	HashMap<String, Object> payloadMap = new HashMap<String, Object>();
	
	byte[] payload;
	
	@Before
	public void InitTest()
	{
		HCAPResourceServer.isCBOR = true;
		startResourceServer();
		createDemoSession();
		
		
		HashMap<String, Object> capability = new HashMap<String, Object>();
		capability.put("vId", "127.0.0.1:8080");
		capability.put("certType", true);
		capability.put("sessID", 1);
		
		ArrayList<Long> stPerm = new ArrayList<Long>();
		stPerm.add((long) 1);
		capability.put("stPerm", stPerm);
		
		HashMap<Long, String> transPerm = new HashMap<Long, String>();
		transPerm.put((long) 2, null);
		transPerm.put((long) 3, null);
		capability.put("transPerm", transPerm);
		
		capability.put("tCreated", 2111111111);
		
		HashMap<String, Object> defMap = new HashMap<String, Object>(); 
		capability.put("nameDefs", defMap);
		
		String toHashString = HashFunctionality.getString(capability);
		capability.put("hash", HashFunctionality.SHA256hash(toHashString.concat(HCAPResourceServer.serverSharedKey).concat(userID)));
		
		payloadMap.put("ticket", capability);
		payloadMap.put("userID", userID);
		
		
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter cCon = new cborConverter();
			payload = cCon.convertToCBOR(payloadMap);
		}
		else
		{
			jsonConverter jCon = new jsonConverter();
			payload = jCon.convertToJSON(payloadMap);
		}
	}
	
	
	@Test
	public void testAlgo2()
	{
		
		HCAPResourceServer.authServerAddress = "coaps://" + "127.0.0.1:8080";
		//setting up the resource server client to send request
		HCAPResourceServer.resToAuth = new CoapClient(HCAPResourceServer.authServerAddress); 				//address of Authorization Server
		buildDTLSConnectorForUpdate();
		HCAPResourceServer.resToAuth.setEndpoint(new CoapEndpoint(HCAPResourceServer.updateConnector, NetworkConfig.getStandard()));
		//timeout set to 100 seconds
		HCAPResourceServer.resToAuth.setTimeout(1000000);
		
		
		String vID = "coaps://" + "127.0.0.1:8080";
		Request request = new Request(Code.POST);
	    request.setURI(vID + "/hcap/validateResource");
	    request.setPayload(payload);
		request.getOptions().setSize1(payload.length);
		
		CoapResponse response = HCAPResourceServer.resToAuth.advanced(request);
		
		byte[] responsePayload = response.getPayload();
		
		HashMap<String, Object> responseMap;
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter cCon = new cborConverter();
			responseMap = cCon.convertFromCBOR(responsePayload);
		}
		else
		{
			jsonConverter jCon = new jsonConverter();
			//responseMap = jCon.convertFromJSON(responsePayload);
			responseMap = jCon.convertFromJSON(responsePayload);
		}
		
		assertTrue(responseMap.containsKey("exceptionList"));
		
		HashMap<String, Object> ex = (HashMap<String, Object>) responseMap.get("exceptionList");
		
		ExceptionList exNew = buildLocalExceptionList(ex);
		//ExceptionList ex = responseMap.get("exceptionList");
		assertFalse(exNew.getEx()!=null);
	}
	
	public ExceptionList buildLocalExceptionList(HashMap<String, Object> exList)
    {
		if(exList == null)
		{
			return null;
		}
    	HashMap<String, Object> getExList = (HashMap<String, Object>) exList.get("ex");
    	ExceptionList list = null;
    	if(getExList != null)
    	{
    		list = buildLocalExceptionList(getExList);
    	}
    	else
    	{
    		long perm = Long.parseLong(exList.get("perm").toString());
    		long ts = Long.parseLong(exList.get("exTime").toString());
    		String userID = null;
    		if(exList.get("userID") != null)
    		{
    			 userID = exList.get("userID").toString();	
    		}
    		
    		
    		ExceptionList exLis = new ExceptionList(userID, perm, ts, null);
    		return exLis;
    	}
    	String userID = (String) exList.get("userID");
    	long perm = Long.parseLong(exList.get("perm").toString());
    	long time = Long.parseLong(exList.get("exTime").toString());
    	ExceptionList returnList = new ExceptionList(userID, perm, time, list);
    	return returnList;
    }
	
	
	public void createDemoSession()
	{
		ExceptionList ex = new ExceptionList(userID, 0, 1212, null);
		HCAPResourceServer.lisMap.put((long) 1, ex);
	}
	
	
	private DTLSConnector buildDTLSConnectorForUpdate()
	{
		String trustStoreLocation = null;
		String trustStorePassword = null;;
		String keyStoreLocation = null;
		String keyStorePassword = null;;
		String propFileLocation = "/home/lakshya/Desktop/multiResServerCode/DTLScodeMultiResServer/PropertiesFiles/ResServer.properties";
		try
		{
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties prop = new Properties();
		    prop.load(in);
		    trustStoreLocation = prop.getProperty("trustStoreLocation");
		    trustStorePassword = prop.getProperty("trustStorePassword");
		    keyStoreLocation = prop.getProperty("keyStoreLocation");
		    keyStorePassword = prop.getProperty("keyStorePassword");
		   
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		try
		{
			//load key store
			KeyStore keyStore = KeyStore.getInstance("JKS");
			InputStream in = new FileInputStream(keyStoreLocation);
			keyStore.load(in, keyStorePassword.toCharArray());
			in.close();

			//load trust store
			KeyStore trustStore = KeyStore.getInstance("JKS");
			in = new FileInputStream(trustStoreLocation);
			trustStore.load(in, trustStorePassword.toCharArray());
			in.close();

			//You can load multiple certificates if needed
			Certificate[] trustedCertificates = new Certificate[1];
			trustedCertificates[0] = trustStore.getCertificate("root");
			
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));
			builder.setPskStore(new StaticPskStore("Client_identity", "secretPSK".getBytes()));
			builder.setIdentity((PrivateKey)keyStore.getKey("client", keyStorePassword.toCharArray()), keyStore.getCertificateChain("client"), false);
			builder.setTrustStore(trustedCertificates);
			builder.setClientAuthenticationRequired(true);
			
			HCAPResourceServer.updateConnector = new DTLSConnector(builder.build());	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return HCAPResourceServer.updateConnector;
	}
	
	
	
	
	
	
	
	
	
	public void startResourceServer()
	{
		String propFileLocation = "/home/lakshya/Desktop/multiResServerCode/DTLScodeMultiResServer/PropertiesFiles/ResServer.properties";
    	
    	int machNumber = 0;
    	int exNumber = 0;
    	String authServerAddress = null;
    	
    	CoapResource[] resT = null;
    	HashMap<Pair, Long> inPermMap = null;
    	
    	
    	//CODE FOR EXPERIMENTS STARTS
    	
    	//read experiment number and machine number from properties file
    	//int exNumber = 0;
    	//int machNumber = 0;
    	try
		{
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties prop = new Properties();
		    prop.load(in);
		    
		    exNumber = Integer.parseInt(prop.getProperty("experimentNumber"));
		    machNumber = Integer.parseInt(prop.getProperty("machineNumber"));
		    authServerAddress = prop.getProperty("authserveraddress");
 
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	
    	//Construct resources for test machine
		/*ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
    	resT =  resTest.construct();
    	inPermMap = new App().createMap();*/
    	

    	
    	if(exNumber == 0)
    	{
    		System.out.println("Demo resources initiated for resource server.");
    		//Construct resources for test machine
    		ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
        	resT =  resTest.construct();
    	}
    	else if(exNumber == 1)
    	{
    		//Construct resources for machine 1 -- EXPERIMENT 1
    		ConstructResourcesEx1 resources1 = new ConstructResourcesEx1();
        	resT =  resources1.construct();
        	inPermMap = new App().createMap();
    	}
    	else if(exNumber == 2)
    	{
    		//Construct resources for machine 2 -- EXPERIMENT 2
    		ConstructResourcesEx2 resources2 = new ConstructResourcesEx2();
    		resT = resources2.construct(machNumber);
    		inPermMap = new App().createMap(machNumber);
    	}
    	else if(exNumber == 3)
    	{
    		//just define some demo resources in order for resource server to not produce any errors
    		//Construct resources for test machine
    		ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
        	resT =  resTest.construct();
    	}
    	else if(exNumber == 4)
    	{
    		//Construct resources for machine 2 -- EXPERIMENT 2
    		ConstructResourcesEx2 resources2 = new ConstructResourcesEx2();
    		resT = resources2.construct(12);
    		inPermMap = new App().createMap(12);
    	}
    	else
    	{
    		System.out.println("Experiment not defined yet.");
    	}
    	
    	//CODE FOR EXPERIMENTS ENDS

    	//CoapResource resource = CoapDemoResource.createResource();
    	
    	
    	//there would be a recover resource to recover the lost capability
    
    	
    	
    	HCAPResourceServer server = new HCAPResourceServer(propFileLocation, inPermMap);
    	//initialize above before actual server runs.
    	
    	
        //METHOD TO START SERVER
    	/*
    	CoapResource[] resourcesFinal = new CoapResource[resT.length + 1];
    	for(int i = 0; i < resT.length + 1; i++)
    	{
    		if(i == resT.length)
    		{
    			CoapResource parentRes = CoapHcapParentResource.createResource();
    			resourcesFinal[i] = parentRes;
    		}
    		else
    		{
    			resourcesFinal[i] = resT[i];	
    		}
    	}
    	*/
    	
    	server.addResourcesToServer(resT);
    	
    	CoapResource parentRes = CoapHcapParentResource.createResource();
    	server.addResourcesToServer(parentRes);

    	
    	Object serverObj = server.startHCAPServer();
    	
    	if(exNumber == 4)
    	{
    		//ping authorization server
    		new App().pingAuthServer();
    	}
    	
    	
    	//******************************** CODE FOR EXPERIMENT 3 **********************************//
    	if(exNumber == 3)
    	{
    		//this is Experiment 3 Part 1
    		int exLisCount = 30000;
        	App app = new App();
        	app.runEx3(exLisCount);
        	app.printToFile("/home/grads/lakshya.tandon/Desktop/DTLSExperimentsCode/ExperimentResults/Experiment3Part1", exLisCount);
        	//printTime array to a file
        	
    		//new App().resetAuthServer(authServerAddress);
    	}
    	
    	//******************************** CODE FOR EXPERIMENT 3 **********************************//
    	
    	
    	/*
    	//METHOD TO STOP SERVER
    	Scanner in = new Scanner(System.in);
        int read = in.nextInt();
		while(read != 1)
		{
			read = in.nextInt();
		}
		if(read == 1)
		{
			//stop server
			server.stopHCAPServer(serverObj);
		}
		in.close();
		*/
    }
}
