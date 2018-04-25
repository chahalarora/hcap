package SecureResServer.SecureResServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.MessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;

import ExceptionList.ExceptionList;

public class HCAPResourceServer 
{
	
	static 
	{
		//ScandiumLogger.initialize();
		//ScandiumLogger.setLevel(Level.FINER);
	}
	
	private CoapServer server;
	private static String trustStoreLocation;
	private static String trustStorePassword;
	private static String keyStoreLocation;
	private static String keyStorePassword;
	private static int port;
	//private static int capProp;
	public static boolean isCBOR = true;
	public static String AuthFileLoc;
	
	private HashMap<Pair, Long> permReferenceMap;
	private MessageDeliverer extendedDeliverer = null;
	
	public static boolean isPaused = false;
	//public static String serverSharedKey = "myKey1";
	public static String serverSharedKey;
	
	public static HashMap<Long, ExceptionList> lisMap = new HashMap<Long, ExceptionList>();
	public static HashMap<Long, Boolean> lisMapLock  = new HashMap<Long, Boolean>();
	
	public static int counter = 0;
	//public static Long rsTime = new Long(0);
	public static String rsID = "127.0.0.1:8080";
	//public static String rsID = "test";
	
	//Variables for Garbage Collection
	public static long requestCount = 0;
	public static long softGCCounter;
	public static long hardGCCounter = 0;
	
	//public static long updateRequestCounter;
	public static long softGCThreshold;
	public static long hardGCThreshold;
	public static long lastGCTime = System.currentTimeMillis();
	
	public static String authServerAddress;
	
	//Locks
	public static Lock lockGC = new ReentrantLock();
	public static Lock lockCounter = new ReentrantLock();
	public static long lCounter = 0;
	
	
	//for experiments
	public static int experimentNumber;
	public static int machineNumber;
	
	//for sending exception list to Authorization server
	public static CoapClient resToAuth;
	public static DTLSConnector updateConnector;
	
	public static HashMap<String, CoapClient> clientsForRS = new HashMap<String, CoapClient>();
	public static HashMap<Long, Long> sessionTimeMap = new HashMap<Long, Long>();
	
	
	public static boolean batonCompression = false;
	public static HashMap<Long, Integer> batonLengthMap = new HashMap<Long, Integer>();  
	
	//used for experiment 5
	public static int confirmationRequestCount = 0;
	public static int requestCounter = 0;
	public static int numberOfSessions = 100;
	
	
	
	public HCAPResourceServer(String propFileLocation, HashMap<Pair, Long> inPermReferenceMap, HCAPMessageDeliverer deliverer)
	{
		server = new CoapServer();
		//rsTime = System.currentTimeMillis();
		extendedDeliverer = deliverer;
		//resources = inResources;
		permReferenceMap = inPermReferenceMap;
		
		//read the properties file and read all values 
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
		    rsID = prop.getProperty("rsID").toString();
		    softGCCounter = Long.parseLong(prop.getProperty("updateRequestCounter"));
		    hardGCCounter = Long.parseLong(prop.getProperty("hardGCCounter"));
		    softGCThreshold = Long.parseLong(prop.getProperty("softGCThreshold"));
		    hardGCThreshold = Long.parseLong(prop.getProperty("hardGCThreshold"));
		    isCBOR = Boolean.parseBoolean(prop.getProperty("isCBOR"));
		    experimentNumber = Integer.parseInt(prop.getProperty("experimentNumber"));
		    machineNumber = Integer.parseInt(prop.getProperty("machineNumber"));
		    serverSharedKey = prop.getProperty("sharedSecret");
		    AuthFileLoc = prop.getProperty("AuthFile");
		    
		    //add the resource to authorization server address, The resource for flush at authorization server is flush
		    authServerAddress = "coaps://" + prop.getProperty("authserveraddress"); 
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public HCAPResourceServer(String propFileLocation, HashMap<Pair, Long> inPermReferenceMap)
	{
		server = new CoapServer();
		//rsTime = System.currentTimeMillis();
		//resources = inResources;
		permReferenceMap = inPermReferenceMap;
		
		//read the properties file and read all values 
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
		    softGCCounter = Long.parseLong(prop.getProperty("updateRequestCounter"));
		    hardGCThreshold = Long.parseLong(prop.getProperty("hardGCThreshold"));
		    isCBOR = Boolean.parseBoolean(prop.getProperty("isCBOR"));		    
		    serverSharedKey = prop.getProperty("sharedSecret");
		    AuthFileLoc = prop.getProperty("AuthFile");
		    
		    //add the resource to authserver address, The resource for flush at authorization server is flush
		    authServerAddress = "coaps://" + prop.getProperty("authserveraddress"); 
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	} 
	
	public Object startHCAPServer()
	{		
		//setting up the resource server client to send request
		resToAuth = new CoapClient(HCAPResourceServer.authServerAddress); 				//address of Authorization Server
		buildDTLSConnectorForUpdate();
		resToAuth.setEndpoint(new CoapEndpoint(HCAPResourceServer.updateConnector, NetworkConfig.getStandard()));
		//timeout set to 100 seconds
		resToAuth.setTimeout(1000000);

		DTLSConnector connector = null;
		try 
		{
			//Pre-shared secrets
			InMemoryPskStore pskStore = new InMemoryPskStore();
			pskStore.setKey("password", "sesame".getBytes());
					
			//load the trust store
			KeyStore trustStore = KeyStore.getInstance("JKS");
			InputStream inTrust = new FileInputStream(trustStoreLocation);
			trustStore.load(inTrust, trustStorePassword.toCharArray());
			inTrust.close();

			//load the key store
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
			config.setIdentity((PrivateKey)keyStore.getKey("server", keyStorePassword.toCharArray()), keyStore.getCertificateChain("server"), false);
			config.setTrustStore(trustedCertificates);
			config.setClientAuthenticationRequired(true);
					
			connector = new DTLSConnector(config.build());
			//InetSocketAddress adr = connector.getAddress();
					
					
			Resource root = server.getRoot();
			HCAPMessageDeliverer messageDeliverer;
			if(extendedDeliverer != null)
			{
				messageDeliverer = new HCAPMessageDeliverer(root, extendedDeliverer, permReferenceMap);
			}
			else
			{
				messageDeliverer = new HCAPMessageDeliverer(root, permReferenceMap);
			}
			
			//server.add(resources);
			server.setMessageDeliverer(messageDeliverer);
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
	
	public void addResourcesToServer(Resource... resources)
	{
		server.add(resources);
	}
	
	public HashMap<Pair, Long> getPermissionMap()
	{
		return permReferenceMap;
	}
	
	public void stopHCAPServer(Object inServerObj)
	{
		CoapServer server = (CoapServer) inServerObj;
		server.stop();
		System.out.println("Server stopped");
	}
	
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
	
	public static boolean getIsCBOR()
	{
		return isCBOR;
	}
	
	private DTLSConnector buildDTLSConnectorForUpdate()
	{
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
			
			updateConnector = new DTLSConnector(builder.build());	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return updateConnector;
	}
	
	public static DTLSConnector buildConnectorForRS()
	{
		DTLSConnector rsConnector = null;
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
			
			rsConnector = new DTLSConnector(builder.build());	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return rsConnector;
	}
}
