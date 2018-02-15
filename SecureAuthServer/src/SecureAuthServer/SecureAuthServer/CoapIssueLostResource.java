package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class represents the resource used by the client to recover a lost certificate.
 * 
 * @author lakshya.tandon
 *
 */
public class CoapIssueLostResource 
{
	
	public static CoapResource createResource()
	{
		CoapResource issueLostResource  = new CoapResource("issueLost")
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
					responseMap.put("messageText", "isPaused");
					byte[] responsePayload = encodePayload(responseMap);
					
					exchange.respond(ResponseCode.CONTENT, responsePayload);
					return;
				}
				*/
				
				//here the authorization server should re issued the a capability for the session, the capability should have the ID
				//of the resource server which possesses the baton. If no resource server possesses the baton then, it should issue
				//capability for the resource server corresponding to the client's state.
				
				System.out.println("ReIssue Request");
				
				HCAPProcessExchange process = new HCAPProcessExchange(exchange, HCAPAuthorizationServer.getIsCBOR());
				String ClientID = process.getClientID();
				HashMap<String, Object> ticket = process.getTicket();
				
				HashMap<String, ArrayList<Object>> saMap = HCAPAuthorizationServer.getSAMap(); 
				ArrayList<Object> lis = saMap.get(ClientID);
				//SecurityAutomaton automaton = (SecurityAutomaton) lis.get(0);
				int capProp = (Integer) lis.get(1);
				
				HCAPRequestHandler handleReq = new HCAPRequestHandler(ticket, ClientID, capProp);
				HashMap<String, Object> capability = null;
				if(process.getSessID() != 0)
				{
					long sessID = process.getSessID();
					capability = handleReq.generateCapability(sessID);
				}
				else
				{
					Response nullRes = null;
					exchange.respond(nullRes);
					return;
				}
				
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
		return issueLostResource;
	}
}
