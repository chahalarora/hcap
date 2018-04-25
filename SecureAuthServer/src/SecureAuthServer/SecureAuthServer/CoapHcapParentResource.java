package SecureAuthServer.SecureAuthServer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class is the represents the parent resource of all the default resources part of HCAP protocol. 
 * This path is reserved for HCAP resources. Programmers using HCAP API should make sure not to use this 
 * path for their resources.
 * 
 * @author lakshya.tandon
 *
 */
public class CoapHcapParentResource 
{
	private CoapResource parent;
	
	public CoapHcapParentResource()
	{
		parent = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource parentResource  = new CoapResource("hcap")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
			
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				System.out.println("Got something in POST.");
				exchange.respond(ResponseCode.CONTENT, "Ping Successful");
			}
		};
		CoapResource getIssueNewResource = CoapIssueNewResource.createResource();
		CoapResource getIssueLostResource = CoapIssueLostResource.createResource();
		CoapResource getUpdateResource = CoapUpdateResource.createResource();
		CoapResource getFlushResource = CoapFlushResource.createResource();
		CoapResource getConfirmResource = CoapConfirmationResource.createResource();
		CoapResource shutdown = CoapShutdownResource.createResource();
		CoapResource checkStatus = CoapCheckStatusResource.createResource();
		
		
		//added reset resource for Ex3 Part1
		CoapResource getResetResource = CoapResetResource.createResource();
		CoapResource pingRes = CoapPingResource.createResource();
		parentResource.add(pingRes);
		parentResource.add(getResetResource);
		parentResource.add(getIssueNewResource);
		parentResource.add(getIssueLostResource);
		parentResource.add(getUpdateResource);
		parentResource.add(getFlushResource);
		parentResource.add(getConfirmResource);
		parentResource.add(shutdown);
		parentResource.add(checkStatus);
		
		return parentResource;
	}
	
	
	public CoapResource getResource()
	{
		return parent;
	}
}
