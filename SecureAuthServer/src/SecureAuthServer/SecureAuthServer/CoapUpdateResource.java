package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class represents the resource used to update authorization server state 
 * using the state update request sent by the client.
 * 
 * @author lakshya.tandon
 *
 */
public class CoapUpdateResource 
{
	
	public static CoapResource createResource()
	{
		CoapResource updateResource  = new CoapResource("update")
		{
			@Override
			public void handleGET(CoapExchange exchange) 
			{
				System.out.println("Got something in GET.");
				exchange.respond(ResponseCode.CONTENT, "This is authorization server, currently it doesn't support GET.");
			}
			
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				/*
				if(HCAPAuthorizationServer.isPaused)
				{
					//Response response = new Response(ResponseCode.CONTENT);
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					responseMap.put("messageCode", 3);
					responseMap.put("messageText", "REQUEST CANCELLED");
					byte[] responsePayload = encodePayload(responseMap);
					
					exchange.respond(ResponseCode.CONTENT, responsePayload);
					return;
				}
				*/
				
				HCAPProcessExchange process = new HCAPProcessExchange(exchange, HCAPAuthorizationServer.getIsCBOR());
				String ClientID = process.getClientID();
				HashMap<String, Object> ticket = process.getTicket();
				
				if(ticket.containsKey("hash"))
    			{
    				verifyHash vhash = new verifyHash();
    				if(!vhash.checkHash(ticket, false, ClientID))
            		{
            			Response res = null;
    					exchange.respond(res);
    					return;
            		}
    			}
				
				HashMap<String, ArrayList<Object>> saMap = HCAPAuthorizationServer.getSAMap(); 
				ArrayList<Object> lis = saMap.get(ClientID);
				int capProp = (Integer) lis.get(1);
				
				HCAPRequestHandler handleReq = new HCAPRequestHandler(ticket, ClientID, capProp);
				
				HashMap<String, Object> capability = handleReq.handleUpdateRequest(ticket);
				HashMap<String, Object> outPayloadMap = new HashMap<String, Object>();
				outPayloadMap.put("ticket", capability);
				
				byte[] outPayload = HCAPAuthorizationServer.encodeOutPayload(outPayloadMap);
				
				Response response = new Response(ResponseCode.CONTENT);
				response.setPayload(outPayload);
				response.getOptions().setSize2(outPayload.length);
				exchange.respond(response);
			}
			
			private byte[] encodePayload(HashMap<String, Object> inMap)
			{
				byte[] returnValue = null;
				if(HCAPAuthorizationServer.isCBOR)
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
			
			private HashMap<String, Object> decodePayload(byte[] inData)
			{
				HashMap<String, Object> returnMap = null;
				if(HCAPAuthorizationServer.isCBOR)
				{
					cborConverter convert = new cborConverter();
					returnMap = convert.convertFromCBOR(inData);
				}
				else
				{
					jsonConverter convert = new jsonConverter();
					returnMap = convert.convertFromJSON(inData);
				}
				return returnMap;
			}
		};
		return updateResource;
	}

}
