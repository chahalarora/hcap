package SecureAuthServer.SecureAuthServer;


import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class represents the resource used to reset the reset the
 * authorization server.
 * 
 * @author lakshya.tandon
 *
 */
public class CoapResetResource 
{
	private CoapResource reset;
	
	public CoapResetResource()
	{
		reset = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource issueNewResource  = new CoapResource("reset")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				HCAPAuthorizationServer.userMap.clear();
				HCAPAuthorizationServer.sessionMap.clear();
				
				new App().runEx3();
				
				exchange.respond(ResponseCode.CONTENT, "resetDone".getBytes());
			}
		};
		return issueNewResource;
	}
	
	
	public CoapResource getResource()
	{
		return reset;
	}
}
