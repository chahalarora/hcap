package SecureAuthServer.SecureAuthServer;

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

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;

/**
 * This class contains all the necessary operations to configure and 
 * start the authorization server.
 * 
 * @author lakshya.tandon
 *
 */
public class HCAPAuthorizationServer
{
	private static String trustStoreLocation;
	private static String trustStorePassword;
	private static String keyStoreLocation;
	private static String keyStorePassword;
	private static int port;
	public static boolean isCBOR = true;
	public static String serverSharedKey;
	public static String AuthFileLoc;
	
	//public static boolean isPaused = false;
	public static HashMap<Long, Boolean> updateLockMap = new HashMap<Long, Boolean>(); 
	
	
	//map to store permissions per state for session id
	public static HashMap<Long, HashMap<String, ArrayList<Object>>> fragamentMap = new HashMap<Long, HashMap<String, ArrayList<Object>>> (); 
	
	public static long countingSessID = 0;
	
	//map which maps sessionIDs to object of states and object of state transitions(Both of which are in array List)
	public static HashMap<Long, ArrayList<Object>> sessionMap = new HashMap<Long, ArrayList<Object>>(); 
		
	//map which maps sessionIDs to userObject which has user attributes
	public static HashMap<Long, ManageUsers> userMap = new HashMap<Long, ManageUsers>();
	
	//map for referring to state machines for a client
	private static HashMap<String, ArrayList<Object>> SAMap;
	
	//map mapping resource servers to their shared secrets
	public static HashMap<String, String> sharedSecretsMap = new HashMap<String, String>();
	
	//map mapping states to the resourceServers which they represent
	public static HashMap<String, String> stateRSMap = new HashMap<String, String>();
	
	//for experiments
	public static int experimentNumber;
	public static int machineNumber;
	
	/**
	 * HCAPAuthorzation Server constructor. Expects Authorization Server's properties file location 
	 * as parameter.
	 * 
	 * @param propFileLocation
	 */
	public HCAPAuthorizationServer(String propFileLocation)
	{
		//first thing is to read the properties file and read all values 
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
		    port = Integer.parseInt(prop.getProperty("port"));
		    //capProp = Integer.parseInt(prop.getProperty("capProp"));
		    isCBOR = Boolean.parseBoolean(prop.getProperty("isCBOR"));
		    serverSharedKey = prop.getProperty("sharedSecret");
		    AuthFileLoc = prop.getProperty("AuthFile");
		    
		    SAMap = new HashMap<String, ArrayList<Object>>();
		    
		    experimentNumber = Integer.parseInt(prop.getProperty("experimentNumber"));
		    machineNumber = Integer.parseInt(prop.getProperty("machineNumber"));
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}
	
	
	/**
	 * This method is used to start HCAP Server.
	 * 
	 * @return HCAP Server object.
	 */
	public Object startHCAPServer()
	{		
		CoapServer server = new CoapServer();
		DTLSConnector connector = null;

		CoapResource getParentResource = CoapHcapParentResource.createResource();

		try 
		{
			// Pre-shared secrets
			InMemoryPskStore pskStore = new InMemoryPskStore();
			pskStore.setKey("password", "sesame".getBytes());
			
			// load the trust store
			KeyStore trustStore = KeyStore.getInstance("JKS");
			InputStream inTrust = new FileInputStream(trustStoreLocation);
			trustStore.load(inTrust, trustStorePassword.toCharArray());
			inTrust.close();
			// load the key store
			KeyStore keyStore = KeyStore.getInstance("JKS");
			InputStream in = new FileInputStream(keyStoreLocation);
			keyStore.load(in, keyStorePassword.toCharArray());
			in.close();
					
			//You can load multiple certificates if needed
			Certificate[] trustedCertificates = new Certificate[1];
			trustedCertificates[0] = trustStore.getCertificate("root");
					

			DtlsConnectorConfig.Builder config = new DtlsConnectorConfig.Builder(new InetSocketAddress(port));
			config.setSupportedCipherSuites(new CipherSuite[]{CipherSuite.TLS_PSK_WITH_AES_128_CCM_8, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8});
			config.setPskStore(pskStore);
			config.setIdentity((PrivateKey)keyStore.getKey("server", keyStorePassword.toCharArray()), keyStore.getCertificateChain("server"), true);
			config.setTrustStore(trustedCertificates);
			config.setClientAuthenticationRequired(true);
					
			connector = new DTLSConnector(config.build());					
			//InetSocketAddress adr = connector.getAddress();
					
			//server.add(getIssueNewResource, getIssueLostResource, getUpdateResource, getFlushResource, getParentResource, getResetResource);
			//server.add(getIssueNewResource, getIssueLostResource, getUpdateResource, getFlushResource);
			server.add(getParentResource);
			server.addEndpoint(new CoapEndpoint(connector, NetworkConfig.getStandard()));
			server.start();
							
		}
		catch (Exception e)
		{
			System.err.println("Could not load the keystore");
			e.printStackTrace();
		}

		System.out.println("Secure CoAP server powered by Scandium (Sc) is listening on port " + port);
		return server;
	}
	
	/**
	 * This method is used to stop the HCAP Server, expects HCAP Server object as a parameter.
	 * 
	 * @param inServerObj
	 */
	public void stopHCAPServer(Object inServerObj)
	{
		CoapServer server = (CoapServer) inServerObj;
		server.stop();
		System.out.println("Server Stopped");
	}
	
	/**
	 * This method is used to add a state machine(Security Automaton) for a client to the HCAP server.
	 * 
	 * @param clientID
	 * @param automaton
	 * @param fragmentLength
	 */
	public void addClientStateMachine(String clientID, SecurityAutomaton automaton, int fragmentLength)
	{
		ArrayList<Object> lis = new ArrayList<Object>();
		lis.add(automaton);
		lis.add(fragmentLength);
		
		SAMap.put(clientID, lis);
	}
	
	/**
	 * This method is used to remove the state machine for a particular client from the HCAP Server.
	 * 
	 * @param clientID
	 */
	public void removeClientStateMachine(String clientID)
	{
		SAMap.remove(clientID);
	}
	
	/**
	 * This method is used to add a resource server id and a corresponding shared secret to the HCAP
	 * server.
	 * 
	 * @param serverAddress
	 * @param inSecret
	 */
	public void addResourceServer(String serverAddress, String inSecret)
	{
		sharedSecretsMap.put(serverAddress, inSecret);
	}
	
	/**
	 * This method is used to remove a resource server and its corresponding shared secret from the HCAP
	 * server
	 * 
	 * @param serverAddress
	 */
	public void removeResourceServer(String serverAddress)
	{
		sharedSecretsMap.remove(serverAddress);
	}
	
	public void addToStateRSMap(String state, String resourceServer)
	{
		stateRSMap.put(state, resourceServer);
	}
	
	public void removeFromStateRSMap(String state)
	{
		stateRSMap.remove(state);
	}
	
	/**
	 * This method is used to encode the payload.
	 * 
	 * @param inMap
	 * @return encoded payload as byte array.
	 */
	public static byte[] encodeOutPayload(HashMap<String, Object> inMap)
	{
		byte[] outData = null;
		if(isCBOR)
		{
			//encode as CBOR data
			cborConverter convert = new cborConverter();
			outData = convert.convertToCBOR(inMap);
		}
		else
		{
			//encode as JSON data
			jsonConverter convert = new jsonConverter();
			outData = convert.convertToJSON(inMap);
		}
		return outData;
	}
	
	/**
	 * This method is used to determine what data interchange format is specified by the
	 * administrator in the Authorization Server properties file.
	 * 
	 * @return true id CBOR is used, false otherwise.
	 */
	public static boolean getIsCBOR()
	{
		return isCBOR;
	}
	
	/**
	 * This method is used to get the map mapping a client to state machine(Security Automaton).
	 * 
	 * @return
	 */
	public static HashMap<String, ArrayList<Object>> getSAMap()
	{
		return SAMap;
	}
}
