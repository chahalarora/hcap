package SecureAuthServer.SecureAuthServer;

import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoapConfirmationResource 
{
	private CoapResource confirmationResource;
	
	public CoapConfirmationResource()
	{
		confirmationResource = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource confirmResource  = new CoapResource("confirm")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				byte[] payload = exchange.getRequestPayload();
				HashMap<String, Object> payloadMap = new HashMap<String, Object>(); 
				
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
				
				long sessionID = Long.parseLong(payloadMap.get("sessionID").toString());
				long tSer = Long.parseLong(payloadMap.get("tSerial").toString());
				
				ManageUsers user = HCAPAuthorizationServer.userMap.get(sessionID);
				boolean batonResult = user.getBaton();
				
				long userTime = user.getEntryTime();
				
				HashMap<String, Object> replyMap = new HashMap<String, Object>();
				
				//testing
				System.out.println("Baton Result: " + batonResult);
				System.out.println("user Time: " + userTime);
				System.out.println("tSer: " + tSer);
				
				if((!batonResult) && (userTime == tSer))
				{
					replyMap.put("confirmation", true);
					user.setBaton(true);
				}
				else
				{
					replyMap.put("confirmation", false);
				}
				replyMap.put("timeForSession", userTime);
				
				byte[] replyPayload = null;
				if(HCAPAuthorizationServer.isCBOR)
				{
					cborConverter cCon = new cborConverter();
					replyPayload = cCon.convertToCBOR(replyMap);
				}
				else
				{
					jsonConverter jCon = new jsonConverter();
					replyPayload = jCon.convertToJSON(replyMap);
				}
				
				exchange.respond(ResponseCode.CONTENT, replyPayload);
				
			}
		};
		return confirmResource;
	}
	
	
	public CoapResource getResource()
	{
		return confirmationResource;
	}
}
