package SecureResServer.SecureResServer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class HCAPRevokeResource 
{
	private CoapResource revoke;
	
	public HCAPRevokeResource()
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
				if(HCAPResourceServer.isCBOR)
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
				
				HCAPResourceServer.lisMap.remove(sessionID);
				HCAPResourceServer.lisMapLock.remove(sessionID);
				HCAPResourceServer.sessionTimeMap.remove(sessionID);
				HCAPResourceServer.batonLengthMap.remove(sessionID);
				
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
