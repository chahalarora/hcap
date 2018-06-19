package SecureAuthServer.SecureAuthServer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import StateMachines.InitStateMachine;

public class CoapRemoveClientResource {
	private CoapResource removeClient;
	
	public CoapRemoveClientResource() {
		removeClient = createResource();
	}
	
	public static CoapResource createResource() 
	{
		CoapResource removeClientResource = new CoapResource("removeClient") 
		{
			@Override
			public void handleGET(CoapExchange exchange) {
				System.out.println("Got something in GET.");
				exchange.respond(ResponseCode.CONTENT, "This is authorization server, currently it doesn't support GET for this resource.");
			}
			@Override
			public void handlePUT(CoapExchange exchange) {
				HCAPProcessExchange process = new HCAPProcessExchange(exchange, HCAPAuthorizationServer.getIsCBOR());
				String ClientID = process.getClientID();
				
				System.out.println("Admin is performing configuration on Authorization Map");;
				/*This is the code that will handle adding clients on the Authorization Server*/
				byte[] inPayload = exchange.getRequestPayload();
				Map<String, Object> payloadMap = decodePayload(inPayload);
				
				//Structure of the hashmap
				//index 0 - clientID (String)
				//index 1 - request (i.e. remove or add)
				Map<String,Object> clientInfo = (HashMap<String,Object>)payloadMap.get("ticket");
				String clientID = clientInfo.get("clientID").toString();
				
				

				System.out.println("Removing Client: " + ClientID);
				App.server.removeClientStateMachine(clientID);
				
				HashMap<String, Object> outPayloadMap = new HashMap<String, Object>();
				
				outPayloadMap.put("reply","Client: " + clientID + " is remove!");
				byte[] outPayload = HCAPAuthorizationServer.encodeOutPayload(outPayloadMap);
				
				Response response = new Response(ResponseCode.CONTENT);
				response.setPayload(outPayload);
				response.getOptions().setSize2(outPayload.length);
				exchange.respond(response);
				
			}
			
			
			//Function for supporting adding Clients in authorization server if needed
			private Map<String, Object> decodePayload(byte[] inData)
			{
				if(HCAPAuthorizationServer.isCBOR)
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
		};
		return removeClientResource;
	}
	
	
	public CoapResource getResource() {
		return removeClient;
	}
}
