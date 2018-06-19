package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import StateMachines.InitStateMachine;

public class CoapAdminResource {
	private CoapResource admin;
	
	public CoapAdminResource() {
		admin = createResource();
	}
	
	public static CoapResource createResource() {
		CoapResource adminResource = new CoapResource("admin") 
		{
			@Override
			public void handleGET(CoapExchange exchange) {
				System.out.println("Got something in GET.");
				exchange.respond(ResponseCode.CONTENT, "This is authorization server, currently it doesn't support GET for this resource.");
			}
			@Override
			public void handlePOST(CoapExchange exchange) {
				System.out.println("Got something in POST.");
				exchange.respond(ResponseCode.CONTENT, "Ping Successful");
			}
			
		};
		
		CoapResource getClientAddResource = CoapAddClientResource.createResource();
		CoapResource getClientRemoveResource = CoapRemoveClientResource.createResource();
		
		adminResource.add(getClientAddResource);
		adminResource.add(getClientRemoveResource);
		
		return adminResource;
	}
	
	
	public CoapResource getResource() {
		return admin;
	}
}
