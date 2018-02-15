package SecureAuthServer.SecureAuthServer;


import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class represents the HCAP Ping resource. This is used when client wants to 
 * minimize overhead while sending consecutive request. It basically initializes 
 * DTLS and establishes a secure communication channel. Thus it can be said as the 
 * first point of contact with the resource server. 
 * 
 * @author lakshya.tandon
 *
 */
public class CoapPingResource 
{
	private CoapResource ping;
	private static HashMap<String, Object> payloadMap = new HashMap<String, Object>();
	private static byte[] payloadData;
	
	public CoapPingResource()
	{
		ping = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource pingResource  = new CoapResource("ping")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				System.out.println("Got something in POST for ping.");
				createMap();
				
				
				//convert map to cbordata
				if(HCAPAuthorizationServer.isCBOR)
		    	{
		    		cborConverter cCon = new cborConverter();
		    		//System.out.println("HCAP Authorize1: " + System.currentTimeMillis());
		    		//payloadMap = cCon.convertFromCBOR(inPayload);
		    		//payloadMap = cborConverter.convertFromCBOR(inPayload);
		    		//System.out.println("HCAP Authorize2: " + System.currentTimeMillis());
		    		payloadData = cCon.convertToCBOR(payloadMap);

		    	}
		    	else
		    	{
		    		jsonConverter jCon = new jsonConverter();
		    		payloadData =jCon.convertToJSON(payloadMap);
		    	}
				
				
				//convert cbor data to map
				if(HCAPAuthorizationServer.isCBOR)
		    	{
		    		cborConverter cCon = new cborConverter();
		    		//System.out.println("HCAP Authorize1: " + System.currentTimeMillis());
		    		//payloadMap = cCon.convertFromCBOR(inPayload);
		    		//payloadMap = cborConverter.convertFromCBOR(inPayload);
		    		//System.out.println("HCAP Authorize2: " + System.currentTimeMillis());
		    		payloadMap = cCon.convertFromCBOR(payloadData);

		    	}
		    	else
		    	{
		    		jsonConverter jCon = new jsonConverter();
		    		//payloadMap = jCon.convertFromJSON(inPayload);
		    		payloadMap = jCon.convertFromJSON(payloadData);
		    	}
				System.out.println("Initialization done");
				
				
				exchange.respond(ResponseCode.CONTENT, "Ping Successful");
			}
			
			public void createMap()
			{
				HashMap<String, Object> demoMap = new HashMap<String, Object>();
				demoMap.put("test1", "1");
				demoMap.put("test2", "2");
				demoMap.put("test3", "3");
				demoMap.put("test4", "4");
				
				payloadMap.put("demo", demoMap);
				
			}
		};
		return pingResource;
	}
	
	
	public CoapResource getResource()
	{
		return ping;
	}
}
