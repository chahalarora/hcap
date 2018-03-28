package SecureResServer.SecureResServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoapCheckStatusResource {

	private CoapResource checkStatus;
	
	public CoapCheckStatusResource()
	{
		checkStatus = createResource();
	}
	
	public static CoapResource createResource()
	{
		CoapResource checkStatusResource  = new CoapResource("checkStatus")
		{
			@Override
			public void handleGET(CoapExchange exchange)
			{
				
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				String responseMessage = "Server is running. System Time: " + getTime();
				System.out.println(responseMessage);
				
				Response response = new Response(ResponseCode.CONTENT);
				response.setPayload(responseMessage);
				exchange.respond(response);
			}
			
			private String getTime() {
				Calendar cal = Calendar.getInstance();
		        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		        System.out.println( sdf.format(cal.getTime()) );
		        
		        return sdf.format(cal.getTime()).toString();
			}
			
		};
		return checkStatusResource;
	}
	
	
	public CoapResource getResource()
	{
		return checkStatus;
	}
}
