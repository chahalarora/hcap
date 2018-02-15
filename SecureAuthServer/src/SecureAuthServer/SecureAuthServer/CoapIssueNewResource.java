package SecureAuthServer.SecureAuthServer;


import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class represents the resource used by the client to obtain a new capability.
 * 
 * @author lakshya.tandon
 */

public class CoapIssueNewResource
{
	private CoapResource issueNew;
	
	public CoapIssueNewResource()
	{
		issueNew = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource issueNewResource  = new CoapResource("issueNew")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				/*
				if(HCAPAuthorizationServer.isPaused)
				{
					exchange.respond(ResponseCode.CONTENT, "isPaused".getBytes());
					return;
				}
				*/
				
				System.out.println("Capability Request");
				
				HCAPProcessExchange process = new HCAPProcessExchange(exchange, HCAPAuthorizationServer.getIsCBOR());
				String ClientID = process.getClientID();
				
				System.out.println("Client ID is: " + ClientID);
				
				//long sessID = process.getSessID();
				
				HashMap<String, Object> ticket = process.getTicket();
				//HashMap<String, ArrayList<Object>> saMap = HCAPAuthorizationServer.getSAMap();
				//ArrayList<Object> lis = saMap.get(ClientID);
				
				HashMap<String, ArrayList<Object>> saMap = HCAPAuthorizationServer.getSAMap(); 
				ArrayList<Object> lis = saMap.get(ClientID);
				
				//get the state machine for the Client
				SecurityAutomaton automaton = (SecurityAutomaton) lis.get(0);
				int capProp = (Integer) lis.get(1);
				String initialState = automaton.getInitialState();
				
				
				HCAPRequestHandler handleReq = new HCAPRequestHandler(ticket, ClientID, capProp);				
				//now call the code to issue a new capability
				HashMap<String, Object> capability = handleReq.generateFirstCapability(automaton, initialState);
				HashMap<String, Object> outPayloadMap = new HashMap<String, Object>();
				outPayloadMap.put("ticket", capability);
				
				byte[] outPayload = HCAPAuthorizationServer.encodeOutPayload(outPayloadMap);
				
				Response response = new Response(ResponseCode.CONTENT);
				response.setPayload(outPayload);
				response.getOptions().setSize2(outPayload.length);
				exchange.respond(response);
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				System.out.println("Got something in POST.");
				exchange.respond(ResponseCode.CONTENT, "This is authorization server, currently it doesn't support POST for this resource.");
			}
		};
		return issueNewResource;
	}
	
	
	public CoapResource getResource()
	{
		return issueNew;
	}
}
