package SecureAdmin.SecureAdmin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Properties;

import java.lang.Process;
import java.lang.ProcessBuilder;

import java.io.ObjectOutputStream;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

import SecureAdmin.SecureAdmin.SecurityAutomaton;

public class AdminBuilder {
	private String trustStorePassword;
	private String keyStorePassword;
	
	private String keyStoreLocation;
	private String trustStoreLocation;
	
	public boolean isCBOR = false;
	
	public String authServerAddress;
	public String resServerAddress;
	
	private HashMap<String, Object> responsePayloadMap;

	private DTLSConnector dtlsConnectorAuth;

	private URI uriAuth;
	private URI uriRes;
	
	private CoapClient clienttoAuth;
	private CoapClient clienttoRes;
	
	/**
	 * This is the Client Builder constructor, it reads the properties file and initializes the variables
	 * used in the class. It also read the X.509 certificate and establishes a connector to be used for 
	 * DTLS.
	 * 
	 * @param propFileLocation
	 */
	public AdminBuilder(String propFileLocation) {
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
		    authServerAddress = prop.getProperty("authserveraddress");
		    resServerAddress = prop.getProperty("resserveraddress");
		    isCBOR = Boolean.parseBoolean(prop.getProperty("isCBOR"));
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
			builder.setIdentity((PrivateKey)keyStore.getKey("admin", keyStorePassword.toCharArray()), keyStore.getCertificateChain("admin"), false);
			builder.setTrustStore(trustedCertificates);
			builder.setClientAuthenticationRequired(true);
			
			dtlsConnectorAuth = new DTLSConnector(builder.build());

			
			//build clients
			uriAuth = new URI("coaps://" + authServerAddress);
			clienttoAuth = new CoapClient(uriAuth);
			clienttoAuth.setEndpoint(new CoapEndpoint(dtlsConnectorAuth, NetworkConfig.getStandard()));
			clienttoAuth.setTimeout(10000);
			
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	
	}
	/**
	 * This method is used to send a demo request(ping) to the Resource Server.
	 * 
	 * @return
	 */
	public boolean pingResServer()
	{
		try
		{
			Request req = new Request(Code.POST);
			req.setURI(uriRes + "/hcap/ping");
			
			CoapResponse response = clienttoRes.advanced(req);
			
			String responseStr = response.getResponseText();
			
			if(responseStr.equals("Ping Successful"))
			{
				System.out.println("Resource Server ping successful.");
				return true;
			}
			else
			{
				System.out.println("Resource Server ping unsuccessful.");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	/**
	 * This method is used to send a demo request(ping) to the Authorization Server.
	 * 
	 * @return
	 */
	public boolean pingAuthServer()
	{	
		try
		{
			Request req = new Request(Code.POST);
			req.setURI(uriAuth + "/hcap/ping");
			
			long startTime1 = System.currentTimeMillis();
			CoapResponse response = clienttoAuth.advanced(req);
			long endTime1 = System.currentTimeMillis();
			
			System.out.println(endTime1 - startTime1);
			
			String responseStr = response.getResponseText();
			
			if(responseStr.equals("Ping Successful"))
			{
				System.out.println("Authorization Server ping successful.");
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;

	}
	/**
	 * This method is used to decode the payload.
	 * 
	 * @param inPayload
	 * @return
	 */
	private HashMap<String, Object> decodePayload(byte[] inPayload)
	{
		if(inPayload != null)
		{
			HashMap<String, Object> outMap;
			if(isCBOR)
			{
				cborConverter convert = new cborConverter();
				outMap = convert.convertFromCBOR(inPayload);
			}
			else
			{
				jsonConverter convert = new jsonConverter();
				outMap = convert.convertFromJSON(inPayload);
			}
			return outMap;	
		}
		return null;
	}
	/**
	 * This method is used to encode the payload.
	 * 
	 * @param inMap
	 * @return
	 */
	private byte[] encodePayload(HashMap<String, Object> inMap)
	{
		byte[] payloadData;
		if(isCBOR)
		{
			cborConverter convert = new cborConverter();
			payloadData = convert.convertToCBOR(inMap);
		}
		else
		{
			jsonConverter convert = new jsonConverter();
			payloadData = convert.convertToJSON(inMap);
		}
		return payloadData;
	}
	/**
	 * This methed is used to create the payload which is to be put in a request. The payload
	 * comprises of the ticket and the actual payload which the client wants to send.
	 * 
	 * @param inTicket
	 * @param inPayload
	 * @return
	 */
	public HashMap<String, Object> createRequestPayload(HashMap<String, Object> inTicket, byte[] inPayload)
	{
		HashMap<String, Object> payloadMap = new HashMap<String, Object> ();
		
		payloadMap.put("ticket", inTicket);
		payloadMap.put("payload", inPayload);
		
		return payloadMap;
	}
	/**
	 * This method lets the admin add clients to the authorization server
	 * 
	 * 
	 */
	public boolean addClientToAuth(String client, SecurityAutomaton automaton, int propagation) {
		
		HashMap<String,Object> clientInfo = new HashMap<String,Object>();
		clientInfo.put("clientID", client);
		clientInfo.put("sA", automaton);
		clientInfo.put("prop", propagation);
		
		
		byte[] updatePayload = encodePayload(createRequestPayload(clientInfo, null));
			
		Request req = new Request(Code.PUT);
		req.setURI(uriAuth +"/hcap/admin/addClient");
		req.getOptions().setSize2(updatePayload.length);
		req.setPayload(updatePayload);
		
		
		CoapResponse response = clienttoAuth.advanced(req);
		byte[] responsePayload = response.getPayload();
		
		responsePayloadMap = decodePayload(responsePayload);
		if(responsePayload != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method lets the admin add clients to the authorization server
	 * 
	 * 
	 */
	public boolean addClientToAuth(String client) {
		
		HashMap<String,Object> clientInfo = new HashMap<String,Object>();
		clientInfo.put("clientID", client);
		byte[] updatePayload = encodePayload(createRequestPayload(clientInfo, null));
			
		Request req = new Request(Code.PUT);
		req.setURI(uriAuth +"/hcap/admin/addClient");
		req.getOptions().setSize2(updatePayload.length);
		req.setPayload(updatePayload);
		
		//Creates a certificate per client (Note: This only run in linux environment)
		/*
		try {
			Process p = new ProcessBuilder("D:\\Computer Science Stuff\\HCAP-Research\\hcap-master\\Keystores\\create-keystores.sh","client").start();
			p.waitFor();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Creating keystore failed...");
		}
		*/
		CoapResponse response = clienttoAuth.advanced(req);
		byte[] responsePayload = response.getPayload();
		
		responsePayloadMap = decodePayload(responsePayload);
		if(responsePayload != null) {
			return true;
		}
		return false;
	}
	
	public boolean removeClientToAuth(String client) {
		HashMap<String,Object> clientInfo = new HashMap<String,Object>();
		clientInfo.put("clientID", client);
		byte[] updatePayload = encodePayload(createRequestPayload(clientInfo, null));
			
		Request req = new Request(Code.PUT);
		req.setURI(uriAuth +"/hcap/admin/removeClient");
		req.getOptions().setSize2(updatePayload.length);
		req.setPayload(updatePayload);
		
		CoapResponse response = clienttoAuth.advanced(req);
		byte[] responsePayload = response.getPayload();
		
		responsePayloadMap = decodePayload(responsePayload);
		if(responsePayload != null) {
			return true;
		}
		return false;
		
	}
	
}
