package SecureAuthServer.SecureAuthServer;

import java.security.Principal;
import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoapShutdownResource {
	private CoapResource shutdown;
	
	
	public CoapShutdownResource()
	{
		shutdown = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource shutdownResource  = new CoapResource("shutdown")
	
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				Request req = exchange.advanced().getRequest();
				Principal pr = req.getSenderIdentity();
				String accessorName = pr.getName();
				
				acHcapRes res = new acHcapRes(HCAPAuthorizationServer.AuthFileLoc);
				if(res.checkAuthorization(accessorName))
				{
					App.server.stopHCAPServer(App.serverObj);
					System.out.println("HCAP Resource Server stopped.");	
				}
			}
			
		};
		return shutdownResource;
	}
	
	
	public CoapResource getResource()
	{
		return shutdown;
	}

}
