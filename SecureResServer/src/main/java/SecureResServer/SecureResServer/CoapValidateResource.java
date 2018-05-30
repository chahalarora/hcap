package SecureResServer.SecureResServer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import ExceptionList.ExceptionList;
/**
 * This class is used to validate a capability, the capability is sent by another resource server
 * and the other resource server request this server to validate the capability on its behalf.
 * After validation succeeds, this resource server returns true otherwise it returns false.
 * 
 * @author lakshya.tandon
 *
 */
public class CoapValidateResource 
{
	private CoapResource validateResource;
	public CoapValidateResource()
	{
		validateResource = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource inDemoResource  = new CoapResource("validateResource")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				System.out.println("Got something in GET.");
				exchange.respond(ResponseCode.CONTENT, "This is resource server, currently it doesn't support GET for the resource requested.");
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				//System.out.println("Got something in validation.");
				byte[] inPayload = exchange.getRequestPayload();
				Map<String, Object> payloadMap = decodePayload(inPayload);
				
				Map<String, Object> capability = (HashMap<String, Object>) payloadMap.get("ticket");
				String userID = payloadMap.get("userID").toString();
				long sessionID = Long.parseLong(capability.get("sessID").toString());
				
				ValidationAlgorithm vAl = new ValidationAlgorithm();
				boolean result = vAl.validate(capability, userID);
				
				Map<String, Object> replyMap = new HashMap<String, Object>();
				
				if(result)
				{
					//replyMap.put("validationSucceeded", true);
					ExceptionList exLis = HCAPResourceServer.lisMap.get(sessionID);
					replyMap.put("exceptionList", exLis);
					
					//remove exception list for the session from itself, after sending it to the other resource server.
					HCAPResourceServer.lisMap.remove(sessionID);
				}
				else
				{
					replyMap.put("exceptionList", null);
				}
				
				byte[] responsePayload = encodePayload(replyMap);
				
				exchange.respond(ResponseCode.CONTENT, responsePayload);
			}
			
			
			
			private Map<String, Object> decodePayload(byte[] inData)
			{
				if(HCAPResourceServer.isCBOR)
				{
					cborConverter cCon = new cborConverter();
					return cCon.convertFromCBOR(inData);
				}
				else
				{
					jsonConverter jCon = new jsonConverter();
					return jCon.convertFromJSON(inData);
				}
			}
			
			private byte[] encodePayload(Map<String, Object> inMap)
			{
				if(HCAPResourceServer.isCBOR)
				{
					cborConverter cCon = new cborConverter();
					return cCon.convertToCBOR(inMap);
				}
				else
				{
					jsonConverter jCon = new jsonConverter();
					return jCon.convertToJSON(inMap);
				}
			}
			
		};
		return inDemoResource;
	}
	
	public CoapResource getValidateResource()
	{
		return validateResource;
	}
}


