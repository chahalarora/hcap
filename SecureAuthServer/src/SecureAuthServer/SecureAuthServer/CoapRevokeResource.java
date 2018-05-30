package SecureAuthServer.SecureAuthServer;


import java.util.HashMap;
import java.util.Map;

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
public class CoapRevokeResource 
{
	private CoapResource revoke;
	
	public CoapRevokeResource()
	{
		revoke = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource revokeResource  = new CoapResource("revoke")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				byte[] payload = exchange.getRequestPayload();
				Map<String, Object> payloadMap = new HashMap<String, Object>();  
				if(HCAPAuthorizationServer.isCBOR)
				{
					cborConverter cCon = new cborConverter();
					payloadMap = cCon.convertFromCBOR(payload);
				}
				else
				{
					jsonConverter jCon = new jsonConverter();
					payloadMap = jCon.convertFromJSON(payload);
				}
				
				Long sessionID = Long.parseLong(payloadMap.get("sessID").toString());
				
				HCAPAuthorizationServer.userMap.remove(sessionID);
				HCAPAuthorizationServer.sessionMap.remove(sessionID);
				
				//new App_Ex().runEx3();
				
				exchange.respond(ResponseCode.CONTENT, "revocation done".getBytes());
			}
		};
		return revokeResource;
	}
	
	
	public CoapResource getResource()
	{
		return revoke;
	}
}
