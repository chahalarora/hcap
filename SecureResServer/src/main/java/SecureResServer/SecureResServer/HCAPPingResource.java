package SecureResServer.SecureResServer;


import java.util.HashMap;
import java.util.Map;

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
public class HCAPPingResource 
{
	private CoapResource ping;
	private static Map<String, Object> payloadMap = new HashMap<String, Object>();
	private static byte[] payloadData;
	
	
	public HCAPPingResource()
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
				
				if(HCAPResourceServer.isPaused)
				{
					System.out.println("Server is paused");
					//CoapExchange ex = new CoapExchange(exchange, (CoapResource) root);
					
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					responseMap.put("messageCode", 2);
					responseMap.put("messageText", "isPaused");
					
					byte[] responsePayload = encodePayload(responseMap);
					
					exchange.respond(ResponseCode.CONTENT, responsePayload);
					//req.cancel();
					return;
				}
				
				
				System.out.println("Got something in POST for ping.");
				createMap();
				
				
				//convert map to cbordata
				if(HCAPResourceServer.isCBOR)
		    	{
		    		cborConverter cCon = new cborConverter();
		    		payloadData = cCon.convertToCBOR(payloadMap);

		    	}
		    	else
		    	{
		    		jsonConverter jCon = new jsonConverter();
		    		payloadData =jCon.convertToJSON(payloadMap);
		    	}
				
				if(HCAPResourceServer.isCBOR)
		    	{
		    		cborConverter cCon = new cborConverter();
		    		payloadMap = cCon.convertFromCBOR(payloadData);

		    	}
		    	else
		    	{
		    		jsonConverter jCon = new jsonConverter();
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
			
			
			private byte[] encodePayload(HashMap<String, Object> inMap)
			{
				byte[] returnValue = null;
				if(HCAPResourceServer.isCBOR)
				{
					cborConverter convert = new cborConverter();
					returnValue = convert.convertToCBOR(inMap);
				}
				else
				{
					jsonConverter convert = new jsonConverter();
					returnValue = convert.convertToJSON(inMap);
				}
				return returnValue;
			}
		};
		return pingResource;
	}
	
	
	
	
	public CoapResource getResource()
	{
		return ping;
	}
}
