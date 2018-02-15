package SecureResServer.SecureResServer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoapDemoResource 
{
private CoapResource demoResource;
	
	public CoapDemoResource()
	{
		demoResource = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource inDemoResource  = new CoapResource("demoResource")
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
				byte[] requestPayload = exchange.getRequestPayload();
				
				Response response = new Response(ResponseCode.CONTENT);
				response.setPayload(requestPayload);
				response.getOptions().setSize2(requestPayload.length);
				exchange.respond(response);
			}
		};
		return inDemoResource;
	}
	
	
	public CoapResource getResource()
	{
		return demoResource;
	}
}
